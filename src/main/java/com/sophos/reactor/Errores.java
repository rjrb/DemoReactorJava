package com.sophos.reactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Errores {

	public static void main(String[] args) {
		onErrorContinue();
		onErrorResume();
		onErrorReturn();
		onErrorStop();

		try {
			Thread.sleep(3000);
		} catch (Exception e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
	}

	private static void onErrorContinue() {
		// Si hay un error, reporte y continúe con el flujo (doOnError no hace nada)
		Mono.fromCompletionStage(Errores::getListaEnteros)
			.flatMapMany(lista -> Flux.just(lista.toArray(Integer[]::new)))
			.map(i -> {
				if(i == 0) {
					throw new ArithmeticException("División por cero");
				}
				return 100 / i;
			})
			.onErrorContinue((throwable, object) -> System.out.println("Error " + throwable.getLocalizedMessage() + " para " + object))
			.doOnError(error -> System.out.println("Haciendo algo con el error: " + error))
			.subscribe(System.out::println)
		;
	}

	private static void onErrorResume() {
		// Si hay un error, haga algo distinto (doOnError no hace nada, salvo que se retorne el error)
		Mono.fromCompletionStage(Errores::getListaEnteros)
			.flatMapMany(lista -> Flux.just(lista.toArray(Integer[]::new)))
			.map(i -> {
				if(i == 0) {
					throw new ArithmeticException("División por cero");
				}
				return 100 / i;
			})
			.onErrorResume(throwable -> Flux.just(-1, -2, -3))
//			.onErrorResume(throwable -> Flux.empty())
//			.onErrorResume(throwable -> Mono.error(new ArithmeticException("Mi otro error por división por cero")))
			.doOnError(error -> System.out.println("Haciendo algo con el error: " + error))
			.subscribe(System.out::println)
		;
	}

	private static void onErrorReturn() {
		// Si hay un error, retorne un valor de fallback (doOnError no hace nada)
		Mono.fromCompletionStage(Errores::getListaEnteros)
			.flatMapMany(lista -> Flux.just(lista.toArray(Integer[]::new)))
			.map(i -> {
				if(i == 0) {
					throw new ArithmeticException("División por cero");
				}
				return 100 / i;
			})
			.onErrorReturn(-50)
			.doOnError(error -> System.out.println("Haciendo algo con el error: " + error))
			.subscribe(System.out::println)
		;
	}

	private static void onErrorStop() {
		// Si hay un error, aborte (doOnError captura el error)
		Mono.fromCompletionStage(Errores::getListaEnteros)
			.flatMapMany(lista -> Flux.just(lista.toArray(Integer[]::new)))
			.map(i -> {
				if(i == 0) {
					throw new ArithmeticException("División por cero");
				}
				return 100 / i;
			})
			.onErrorStop()
			.doOnError(error -> System.out.println("Haciendo algo con el error: " + error))
			.subscribe(System.out::println)
		;
	}

	private static CompletionStage<List<Integer>> getListaEnteros() {
		return CompletableFuture.supplyAsync(() -> {
				try {
					Thread.sleep(500);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					ie.printStackTrace();
				}
				List<Integer> lista = IntStream.rangeClosed(0, 10).boxed().collect(Collectors.toList());
				Collections.shuffle(lista);
				return lista;
			})
			.minimalCompletionStage()
		;
	}

}
