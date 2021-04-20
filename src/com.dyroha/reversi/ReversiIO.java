package com.dyroha.reversi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ReversiIO {
	public static void saveSession(File gameFile, GameSession session) throws IOException {
		try (FileWriter fw = new FileWriter(gameFile)) {
			fw.write(session.getFileFormattedString());
		}
	}

	public static GameSession loadSession(File gameFile) throws IOException {
		try (Scanner sc = new Scanner(gameFile).useDelimiter(";")) {
			String blackName = sc.next();
			int blackScore = sc.nextInt();
			String whiteName = sc.next();
			int whiteScore = sc.nextInt();

			int turnNumber = sc.nextInt();
			int size = sc.nextInt();
			String boardString = sc.next();
			GameSession gameSession = new GameSession(blackName, whiteName, blackScore, whiteScore,
					new ReversiGame(turnNumber, size, boardString));
			return gameSession;
		}
	}
}
