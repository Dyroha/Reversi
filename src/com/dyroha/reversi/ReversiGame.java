package com.dyroha.reversi;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A basic game of reversi
 * 
 * @version 20/04/2021
 * @author Dylan Hamilton
 */
public class ReversiGame {
	private char[][] board;
	private int turnNumber;
	private final char[] PIECE_COLOURS = { 'b', 'w' };
	private ArrayList<int[]> locations;
	private char currentPlayerTurn;
	private int size;
	private int blackCount;
	private int whiteCount;

	/**
	 * Creates a new ReversiGame
	 * @param size the size of the game board
	 */
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
		countPieces();
	}

	/**
	 * Creates a ReversiGame from a ReversiGame string
	 * @param turnNumber the current turn number
	 * @param size the size of the game board
	 * @param boardStr a string representing the board as done by the toString method
	 */
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
		countPieces();
	}

	/**
	 * prints out a basic representation of the board
	 */
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

	/**
	 * gets a board space's state
	 * @param y the y position (row) of the piece
	 * @param x the x position (collumn) of the piece
	 * @return the char representing the state of the space
	 */
	public char getBoardPiece(int y, int x) {
		return board[y][x];
	}

	/**
	 * checks if the game is over
	 * @return true if the game is over, otherwise false
	 */
	public boolean isGameOver() {
		return !testForValidTurn('b') && !testForValidTurn('w');
	}

	/**
	 * generates a string with the leading player's name and score in the format
	 * "{Black/White} wins with {said playerScore} points"
	 * @return winning player string
	 */
	public String findWinner() {
		if (blackCount > whiteCount)
			return "Black wins with " + blackCount + " points";
		else if (whiteCount > blackCount)
			return "White wins with " + whiteCount + " points";
		else
			return "Game was a draw";
	}

	/**
	 * gets black player's pieces on the board
	 * @return black's piece count
	 */
	public int getBlackCount() {
		return blackCount;
	}

	/**
	 * gets white player's pieces on the board
	 * @return white's piece count
	 */
	public int getWhiteCount() {
		return whiteCount;
	}

	private void countPieces() {
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

	/**
	 * checks if said player has any valid moves on the board
	 * @param player the player ('b' or 'w')
	 * @return true if the player can go, otherwise false
	 */
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

	/**
	 * places a piece on the board and flips appropriate tiles if it is a valid move
	 * @param y y position (row) of the space
	 * @param x x position (collumn) of the space
	 * @return true if it was successful, false otherwise (invalid move)
	 */
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

	/**
	 * skips the current player's turn
	 */
	public void nextTurn() {
		turnNumber++;
		currentPlayerTurn = PIECE_COLOURS[turnNumber % 2];
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

	private void flipFromList() {
		locations.forEach(l -> {
			if (board[l[0]][l[1]] == 'b')
				board[l[0]][l[1]] = 'w';
			else if (board[l[0]][l[1]] == 'w')
				board[l[0]][l[1]] = 'b';
		});
		locations.clear();
	}

	/**
	 * gets the current player turn
	 * @return 'b' if it's black's turn, 'w' otherwise
	 */
	public char getCurrentPlayerChar() {
		return currentPlayerTurn;
	}

	/**
	 * gets the current player turn
	 * @return "Black" if it's black's turn, "White" otherwise
	 */
	public String getCurrentPlayerStr() {
		return currentPlayerTurn == 'b' ? "Black" : "White";
	}

	/**
	 * gets the size of the board
	 * @return height/width of the board
	 */
	public int getSize() {
		return size;
	}

	/**
	 * creates a string representing the ReversiGame formatted as
	 * {turnNumber};{size};{board as a string}
	 * @return the board as a string
	 */
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
