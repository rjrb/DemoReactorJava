package com.sophos.reactor;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Random;
import java.util.stream.Stream;

public class Paralelismo {

	public static void main(String[] args) {
		subscribeOn();
		publishOn();
		parallel();
		connectable();
		flatMap();
	}

	private static void subscribeOn() {
		Scheduler scheduler = Schedulers.newBoundedElastic(10, 5, "miHilo", 5);
		Flux.just("Hola", "mundo", "asíncrono")
			.log()
			.doOnNext(valor -> System.out.println("Hilo inicial: " + Thread.currentThread().getName() + " - " + valor))
			.subscribeOn(scheduler)
			.map(String::length)
			.doOnNext(valor -> System.out.println("Hilo luego de subscribeOn: " + Thread.currentThread().getName() + " - " + valor))
			.subscribe()
		;
	}

	private static void publishOn() {
		Scheduler scheduler = Schedulers.newBoundedElastic(10, 5, "miHilo", 5);
		Flux.just("Hola", "mundo", "asíncrono")
			.doOnNext(valor -> System.out.println("Hilo inicial: " + Thread.currentThread().getName() + " - " + valor))
			.doOnEach(signal -> System.out.println("Señal: " + signal))
			.publishOn(scheduler)
			.map(String::length)
			.doOnNext(valor -> System.out.println("Hilo luego de publishOn: " + Thread.currentThread().getName() + " - " + valor))
			.doFinally(signalType -> System.out.println("Tipo de señal de terminación: " + signalType))
			.subscribe()
		;
	}

	private static void parallel() {
		Mono.fromCallable(System::currentTimeMillis)
			.repeat()
			.sample(Duration.ofMillis(100))
			.take(Duration.ofSeconds(3))
			.parallel(8)
			.runOn(Schedulers.parallel())
			.doOnNext(d -> System.out.println("Estoy en el hilo " + Thread.currentThread() + " ::: " + d))
			.subscribe()
		;
	}

	private static void connectable() {
		ConnectableFlux<Integer> connectableFlux = Flux.<Integer>create(fluxSink -> {
				Stream.generate(Paralelismo::random).limit(10).forEach(fluxSink::next);
				fluxSink.complete();
			})
			.take(Duration.ofMillis(100))
			.publish()
		;

		connectableFlux.subscribe(System.out::println);
		connectableFlux.subscribe(i -> System.out.println(i + " ::: " + i * 2));

		connectableFlux.connect();
	}

	private static void flatMap() {
		Scheduler scheduler = Schedulers.newParallel("miParalelo", 5, true);

		Flux.range(1, 10)
			.flatMap(i -> Mono.defer(() -> {
					System.out.printf("Ejecutando %s en el hilo %s%n", i, Thread.currentThread().getName());
					try {
						Thread.sleep(random());
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
					System.out.printf("Terminó de ejecutar %s%n", i);
					return Mono.just(i);
				}).subscribeOn(scheduler)
				, 5
			)
			.subscribe()
		;

		try {
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static final Random RANDOM = new Random();
	private static int random() {
		return RANDOM.nextInt(200);
	}

}
