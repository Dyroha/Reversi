package com.dyroha.reversi;

import java.util.ArrayList;
import java.util.Arrays;

public class ReversiGame {
	private char[][] board;
	private int turnNumber;
	private char[] PIECE_COLOURS = { 'b', 'w' };
	private ArrayList<int[]> locations;
	private char currentPlayerTurn;
	private int size;
	private int blackCount, whiteCount;

	public ReversiGame(int size) {
		this.size = size;
		board = new char[size][size];
		turnNumber = 0;
		locations = new ArrayList<>();
		currentPlayerTurn = 'b';

		for (char[] row : board) {
			Arrays.fill(row, '-');
		}

		int middle = size / 2;
		board[middle - 1][middle - 1] = 'w';
		board[middle - 1][middle] = 'b';
		board[middle][middle - 1] = 'b';
		board[middle][middle] = 'w';
	}

	public ReversiGame(int turnNumber, int size, String boardStr) throws NumberFormatException {
		this.size = size;
		this.locations = new ArrayList<>();
		this.turnNumber = turnNumber;
		this.currentPlayerTurn = PIECE_COLOURS[turnNumber % 2];
		this.board = new char[size][size];

		String[] pieces = boardStr.split(",");
		int count = 0;
		for (String piece : pieces) {
			char type = piece.substring(0, 1).toCharArray()[0];
			int number = Integer.parseInt(piece.substring(1));
			for (int i = 0; i < number; i++) {
				board[count / size][count % size] = type;
				count++;
			}
		}
	}

	public void printBoard() {
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				char disk = board[y][x];
				System.out.print(disk + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	public char getBoardPiece(int y, int x) {
		return board[y][x];
	}

	public boolean isGameOver() {
		return !testForValidTurn('b') && !testForValidTurn('w');
	}

	public String findWinner() {
		countPieces();
		if (blackCount > whiteCount)
			return "Black wins with " + blackCount + " points";
		else if (whiteCount > blackCount)
			return "White wins with " + whiteCount + " points";
		else
			return "Game was a draw";
	}

	public int getBlackCount() {
		return blackCount;
	}

	public int getWhiteCount() {
		return whiteCount;
	}

	public void countPieces() {
		int black = 0;
		int white = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (board[j][i] == 'b')
					black++;
				else if (board[j][i] == 'w')
					white++;
			}
		}
		blackCount = black;
		whiteCount = white;
	}

	public boolean testForValidTurn(char player) {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				validPlaceCheck(j, i, player);
				if (!locations.isEmpty()) {
					locations.clear();
					return true;
				}
			}
		}
		return false;
	}

	public void nextTurn() {
		turnNumber++;
		currentPlayerTurn = PIECE_COLOURS[turnNumber % 2];
	}

	public boolean placePiece(int y, int x) {
		try {
			validPlaceCheck(y, x, currentPlayerTurn);
			if (!locations.isEmpty()) {
				flipFromList();
				board[y][x] = currentPlayerTurn;
				nextTurn();
				countPieces();
				return true;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return false;

	}

	private void flipFromList() {
		locations.forEach(l -> {
			if (board[l[0]][l[1]] == 'b')
				board[l[0]][l[1]] = 'w';
			else if (board[l[0]][l[1]] == 'w')
				board[l[0]][l[1]] = 'b';
		});
		locations.clear();
	}

	private void validPlaceCheck(int y, int x, char colour) {
		if (board[y][x] != '-')
			return;
		for (Direction dir : Direction.values()) {
			checkDirection(y, x, dir, colour);
		}
	}

	private boolean checkDirection(int y, int x, Direction direction, char colour) {
		int[] start = { y, x };
		int[] end = { y, x };
		end = movePosition(end, direction);

		if (!(end[0] >= 0 && end[0] < size && end[1] >= 0 && end[1] < size))
			return false;
		char nextPiece = board[end[0]][end[1]];

		// check if next piece is invalid
		if (nextPiece == '-' || nextPiece == colour)
			return false;

		// count till reaches own colour or triggers exception
		while (true) {
			end = movePosition(end, direction);
			// is within bounds
			if (!(end[0] >= 0 && end[0] < size && end[1] >= 0 && end[1] < size))
				return false;
			// doesn't end in null
			if (board[end[0]][end[1]] == '-')
				return false;

			if (board[end[0]][end[1]] == colour) {
				break;
			}
		}

		// do flips
		start = movePosition(start, direction);
		while (start != end) {
			if (board[start[0]][start[1]] == colour)
				return true;
			locations.add(new int[] { start[0], start[1] });
			start = movePosition(start, direction);
		}
		return true;
	}

	private int[] movePosition(int[] pos, Direction direction) {
		switch (direction) {
		case UPLEFT:
			pos[0]--;
			pos[1]--;
			break;
		case UP:
			pos[0]--;
			break;
		case UPRIGHT:
			pos[0]--;
			pos[1]++;
			break;
		case LEFT:
			pos[1]--;
			break;
		case RIGHT:
			pos[1]++;
			break;
		case DOWNLEFT:
			pos[0]++;
			pos[1]--;
			break;
		case DOWN:
			pos[0]++;
			break;
		case DOWNRIGHT:
			pos[0]++;
			pos[1]++;
			break;
		}
		return pos;
	}

	public char getCurrentPlayerChar() {
		return currentPlayerTurn;
	}

	public String getCurrentPlayerStr() {
		return currentPlayerTurn == 'b' ? "Black" : "White";
	}

	public int getSize() {
		return size;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		char type = board[0][0];
		for (char[] diskRow : board) {
			for (char disk : diskRow) {
				if (disk == type)
					count++;
				else {
					sb.append(type);
					sb.append(count);
					sb.append(',');
					count = 1;
					type = disk;
				}
			}
		}
		sb.append(type);
		sb.append(count);

		return turnNumber + ";" + size + ";" + sb;
	}

}
