package com.sophos.reactor;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

public class Union {

	public static void main(String[] args) {
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

		// Flujos para pruebas
		Flux<Integer> pares = Flux.just(2, 4, 6, 8, 10).delayElements(Duration.ofMillis(10));
		Flux<Integer> impares = Flux.just(1, 3, 5, 7, 9).delayElements(Duration.ofMillis(20));

		// Concat
//		pares.concatWith(impares).subscribe(valor -> System.out.println("Concat - " + valor));
		System.out.println();

		// Merge
//		Flux.merge(pares, impares).subscribe(valor -> System.out.println("Merge - " + valor));
		System.out.println();

		// Merge secuencial
//		Flux.mergeSequential(pares, impares).subscribe(valor -> System.out.println("MergeSequential - " + valor));
		System.out.println();

		// Merge ordenado
		Flux.mergeOrdered(pares, impares).subscribe(valor -> System.out.println("MergeOrdered - " + valor));
		System.out.println();


		try {
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
