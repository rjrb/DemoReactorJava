package com.sophos.reactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Random;

public class Sink {

	private static final String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent imperdiet vulputate quam ac malesuada. Quisque mollis dolor sapien. Nunc vel massa vitae ex volutpat malesuada nec eu tellus. Sed interdum nisl vel nunc maximus, vitae pharetra nunc ornare. Nunc porttitor purus tincidunt, interdum lorem non, pretium nunc. Proin malesuada urna ipsum, eu vehicula sem eleifend non. Donec a vehicula ex, non accumsan ligula. Nulla facilisi. Suspendisse scelerisque libero mi, at venenatis nibh commodo vitae. Duis sodales sapien tincidunt metus mattis, in tristique metus fermentum. Quisque quis varius erat. Pellentesque dignissim cursus purus, sed ullamcorper augue dapibus eget. Nullam eu rutrum";

	public static void main(String[] args) {
		// Crea el emisor
		Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

		// Toma el publicador y se suscribe por primera vez
		sink.asFlux()
			.map(String::toUpperCase)
			.distinct()
			.subscribe(System.out::println)
		;

		// Toma el publicador y se suscribe por segunda vez
		sink.asFlux()
			.flatMap(palabra -> Flux.fromArray(palabra.split(""))
				.map(String::toLowerCase)
				.collectList()
			)
			.subscribe(System.out::println)
		;

		Random random = new Random();
		String[] palabras = loremIpsum.split(" ");
		for(int i = 0 ; i < 10 ; i++) {
			// Empuja eventos aleatoriamente al emisor
			int next = random.nextInt(palabras.length);
			sink.tryEmitNext(palabras[next]).orThrow();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
		}

		// Completa el emisor
		sink.tryEmitComplete().orThrow();
	}

}
