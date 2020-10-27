package com.sophos.reactor;

public class Errores {

	public static void main(String[] args) {
	}

	private static void consumirError(Throwable t) {
		System.out.println(t.getLocalizedMessage());
	}

}
