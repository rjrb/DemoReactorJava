package com.sophos.reactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class Union {

	/** Flujos para pruebas */
	private static final Flux<Integer> pares = Flux.just(2, 4, 6, 8, 10).delayElements(Duration.ofMillis(10));
	private static final Flux<Integer> impares = Flux.just(1, 3, 5, 7, 9).delayElements(Duration.ofMillis(20));


	public static void main(String[] args) {
		zip();
		concat();
		merge();
		mergeSequential();
		mergeOrdered();
		switchIfEmpty();
		switchMap();

		try {
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void zip() {
		// Zip
		Flux.just("Una frase muy larga para enumerar y contar la cantidad de palabras")
			.flatMap(frase -> Flux.fromArray(frase.split(" ")))
			.map(String::toLowerCase)
			.zipWith(
				Flux.range(1, Integer.MAX_VALUE),
				(palabra, posicion) -> posicion + " - " + palabra
			)
			.doOnNext(System.out::println)
			.count()
			.subscribe(cantidad -> System.out.println("Se contaron " + cantidad + " palabras en el Flux"))
		;
		System.out.println();
	}

	private static void concat() {
		pares.concatWith(impares)
			.subscribe(valor -> System.out.println("Concat - " + valor))
		;
		System.out.println();
	}

	private static void merge() {
		Flux.merge(pares, impares)
			.subscribe(valor -> System.out.println("Merge - " + valor))
		;
		System.out.println();
	}

	private static void mergeSequential() {
		Flux.mergeSequential(pares, impares)
			.subscribe(valor -> System.out.println("MergeSequential - " + valor));
		System.out.println();
	}

	private static void mergeOrdered() {
		Flux.mergeOrdered(pares, impares)
			.subscribe(valor -> System.out.println("MergeOrdered - " + valor));
		System.out.println();
	}

	private static void switchIfEmpty() {
		Flux.just(1, 3, 5, 7, 9)
			.filter(i -> i % 2 == 0)
			.map(String::valueOf)
			.switchIfEmpty(Mono.just("Vacío"))
			.subscribe(System.out::println)
		;
		System.out.println();
	}

	private static void switchMap() {
		Flux.just(1, 2, 3, 4, 5)
			.switchMap(valor ->
				valor % 2 == 0
					? Mono.just(0)
					: Flux.just(1, 2)
			)
			.subscribe(System.out::println)
		;
		System.out.println();
	}

}
