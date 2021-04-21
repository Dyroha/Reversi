package com.dyroha.reversi;

/**
 * Reversi application main class
 * 
 * @version 10/04/2021
 * @author Dylan Hamilton
 */
public class Reversi {
	public static void main(String[] args) {
		try {
			new GameWindow();
		} catch (Exception e) {
			System.err.println("Something went wrong, application cannot be started");
			e.printStackTrace();
		}
	}
}
