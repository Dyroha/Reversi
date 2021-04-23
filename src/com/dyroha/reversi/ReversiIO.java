package com.dyroha.reversi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * File Save and load for a GameSession
 * 
 * @version 23/04/2021
 * @author Dylan Hamilton
 */
public class ReversiIO {
	// private constructor to hide the public super one
	private ReversiIO() {}

	/**
	 * Save a GameSession to a given file
	 * @param gameFile the file for which to save the session to
	 * @param session the session to save to file
	 * @throws IOException if the file cannot be found
	 */
	public static void saveSession(File gameFile, GameSession session) throws IOException {
		try (FileWriter fw = new FileWriter(gameFile)) {
			fw.write(session.getFileFormattedString());
		}
	}

	/**
	 * Load a GameSession from a file
	 * @param gameFile the file to load the game from
	 * @return the loaded GameSession object
	 * @throws IOException if the file cannot be found or read propperly, or if the GameSession couln't load the pieces
	 */
	public static GameSession loadSession(File gameFile) throws IOException {
		try (Scanner sc = new Scanner(gameFile).useDelimiter(";")) {
			String blackName = sc.next();
			int blackScore = sc.nextInt();
			String whiteName = sc.next();
			int whiteScore = sc.nextInt();

			int turnNumber = sc.nextInt();
			int size = sc.nextInt();
			String boardString = sc.next();
			return new GameSession(blackName, whiteName, blackScore, whiteScore,
					new ReversiGame(turnNumber, size, boardString));
		}
	}
}
