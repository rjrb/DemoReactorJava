package com.sophos.reactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

public class Pruebas {

	public static void main(String[] args) {
		StepVerifier.create(Flux.just(1, 2, 3, 4, 5))
			.expectNext(1)
			.expectNext(2)
			.expectNext(3)
			.expectNext(4)
			.expectNext(5)
			.expectComplete()
			.verify()
		;

		StepVerifier.create(
				Flux.combineLatest(
					Flux.just(1, 3, 5),
					Flux.just(2, 4, 6),
					(a, b) -> a * b
				)
			)
			.expectNext(10)
			.expectNext(20)
			.expectNext(30)
			.expectComplete()
			.verify()
		;

		StepVerifier.create(
				Mono.just("Hola soy un mensaje")
					.concatWith(Mono.error(new IllegalArgumentException("Soy un error")))
					.delaySubscription(Duration.ofSeconds(1))
			)
			.thenAwait()
			.expectNext("Hola soy un mensaje")
			.expectError(IllegalArgumentException.class)
			.verify()
		;
	}
}
