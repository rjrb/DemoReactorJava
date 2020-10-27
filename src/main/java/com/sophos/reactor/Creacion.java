package com.sophos.reactor;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Creacion {

	public static void main(String[] args) {
		// Un Mono
		Mono.just("Hola mundo")
			.subscribe(System.out::println);
		System.out.println();

		// Un FLux
		Flux.just(1, 2, 3, 4, 5)
			.subscribe(System.out::println);
		System.out.println();

		// Flux con sus tres eventos
		Flux.just(10, 20, 30, 40, 50)
			.subscribe(
				valor -> System.out.println("OnNext: "  + valor),
				error -> System.err.println("OnError: " +  error),
				() -> System.out.println("OnComplete")
			)
		;
		System.out.println();

		// Flux con operaciones intermedias
		Disposable disposable = Flux.just("Hola", "desde", "el", "mundo", "reactivo", "!")
			.filter(palabra -> palabra.length() > 2)
			.map(String::toUpperCase)
			.skip(1)
			.subscribe(
				valor -> System.out.println("OnNext: "  + valor),
				error -> System.err.println("OnError: " +  error),
				() -> System.out.println("OnComplete")
			)
		;
		System.out.println("Is disposed? " + disposable.isDisposed());
		System.out.println();

		// Flux con errores
		Flux.just("Hola", null, "el", "mundo", "reactivo", "!")
			.filter(palabra -> palabra.length() > 2)
			.map(String::toUpperCase)
			.take(3)
			.subscribe(
				valor -> System.out.println("OnNext: "  + valor),
				error -> System.out.println("OnError: " +  error),
				() -> System.out.println("OnComplete")
			)
		;
		System.out.println();

		// Crear un Flux desde otro Flux
		Flux<Integer> flux1 = Flux.just(1,2,3)
			.map(i -> i * 10)
		;
		Flux<String> flux2 = Flux.from(flux1)
			.filter(i -> i > 10)
			.map(String::valueOf)
		;
		flux2.subscribe(System.out::println);
		System.out.println();

		// Crear un Flux desde un Stream
		Stream<Integer> stream = Stream.of(1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5 ,5)
			.distinct()
			.filter(i -> i % 2 != 0)
			.map(i -> i * 2)
		;
		Flux.fromStream(stream)
			.map(i -> Math.pow(i, 2))
			.doOnNext(System.out::println)
			.subscribe()
		;
		System.out.println();

		// Crear un Flux desde un Iterable
		Flux.fromIterable(List.of("varias", "palabras", "en", "una", "lista"))
			.log()
			.flatMap(palabra -> Flux.fromArray(palabra.split("")))
			.collect(
				Collectors.groupingBy(
					Function.identity(),
					Collectors.counting()
				)
			)
			.subscribe(System.out::println)
		;
		System.out.println();

		// Crear un Flux a partir de un rango numérico
		Flux.range(1, 5)
			.subscribe(System.out::println)
		;
		System.out.println();

		// Tomar el último elemento emitido por un Flux
		Flux.range(1, 1000)
			.flatMap(i -> Flux.just(i * 10))
			.last()
			.subscribe(System.out::println)
		;
		System.out.println();

		// Reducir un Flux a un Mono
		Flux.just(1, 2, 3, 4, 5)
			.reduce(1, (acumulado, valor) -> acumulado * valor)
			.subscribe(System.out::println)
		;
		System.out.println();

		// Reducir un Flux pero ir emitiendo los valores intermedios (una productoria)
		Flux.just(1, 2, 3, 4, 5)
			.scan((acumulado, valor) -> acumulado * valor)
			.subscribe(System.out::println)
		;
		System.out.println();

		// Crear un Flux infinito
		Disposable interval = Flux.interval(Duration.ofMillis(500))
			.doOnNext(System.out::println)
			.subscribe()
		;
		try {
			Thread.sleep(5000);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		interval.dispose();
	}

}
