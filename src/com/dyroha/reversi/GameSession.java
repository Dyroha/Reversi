package com.dyroha.reversi;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * A game session for Reversi
 * 
 * @version 20/04/2021
 * @author Dylan Hamilton
 */
public class GameSession {
	private GameWindow gui;
	private ReversiGame game;
	private int blackScore;
	private int whiteScore;
	private String blackName;
	private String whiteName;
	private GameBoard gameBoard;
	private BufferedImage blankSpace;
	private BufferedImage blackPiece;
	private BufferedImage whitePiece;
	
	/**
	 * Creates a new GameSession
	 * @param gameWindow the gui it is connected to
	 * @throws IOException thrown if board space images cannot be found
	 */
	public GameSession(GameWindow gameWindow) throws IOException {
		gui = gameWindow;
		blackScore = 0;
		whiteScore = 0;
		getBoardImages();
	}
	
	/**
	 * Creates a GameSession from exsiting data
	 * @param blackName player name for black
	 * @param whiteName player name for white
	 * @param blackScore current score for black
	 * @param whiteScore current score for white
	 * @param game current ReversiGame game
	 * @throws IOException thrown if board space images cannot be found
	 */
	public GameSession(String blackName, String whiteName, int blackScore, int whiteScore, ReversiGame game) throws IOException {
		this.blackName = blackName;
		this.whiteName = whiteName;
		this.blackScore = blackScore;
		this.whiteScore = whiteScore;
		getBoardImages();
		this.game = game;
		gameBoard = new GameBoard(game.getSize());
		generateSpaces(game.getSize());
	}

	private void getBoardImages() throws IOException {
			blankSpace = ImageIO.read(getClass().getResource("/resources/blank_space.png"));
			blackPiece = ImageIO.read(getClass().getResource("/resources/black_piece.png"));
			whitePiece = ImageIO.read(getClass().getResource("/resources/white_piece.png"));
		}
	
	/**
	 * creates a new reversi game
	 * @param bName player name for black
	 * @param wName player name for white
	 * @param size size of the game
	 */
	public void startGame(String bName, String wName, int size) {
		if(game == null) {
			blackName = bName;
			whiteName = wName;
		} else if (game.isGameOver()){
			blackScore += game.getBlackCount();
			whiteScore += game.getWhiteCount();
			gui.setBlackScore(blackScore);
			gui.setWhiteScore(whiteScore);
		}
		game = new ReversiGame(size);
	}
	
	/**
	 * generates the board for the gui
	 */
	public void setUpGame() {
		gameBoard = new GameBoard(game.getSize());
		generateSpaces(game.getSize());
		updateGUI(true);
		refreshBoard();
	}

	private void generateSpaces(int size) {
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				int j = y;
				int i = x;
				BoardSpace space = new BoardSpace(j, i, blankSpace);
				space.addActionListener(e -> {
					if (game.placePiece(j, i)) {
						refreshBoard();
						if (game.isGameOver()) {
							updateGUI(true);
							gui.endOfGameEvent(game.findWinner());
							return;
						} else if (!game.testForValidTurn(game.getCurrentPlayerChar())) {
							noValidMoves(game.getCurrentPlayerStr());
							game.nextTurn();
						}
						updateGUI(true);
					} else {
						updateGUI(false);
					}
				});
				gameBoard.add(space);
			}
		}
	}

	private void updateGUI(boolean isValidTurn) {
		gui.setBlackPieces(game.getBlackCount());
		gui.setWhitePieces(game.getWhiteCount());
		if (isValidTurn)
			gui.setMessageBar(game.getCurrentPlayerStr() + "'s turn");
		else
			gui.setMessageBar("Invalid move");
		
	}
	
	private void noValidMoves(String player) {
		gui.alertNoTurn(player);
	}

	private void refreshBoard() {
		for (Component c : gameBoard.getComponents()) {
			if(c instanceof BoardSpace) {
				BoardSpace space = (BoardSpace) c;
				char piece = game.getBoardPiece(space.getYPos(), space.getXPos());
				if (piece == '-') continue;
				if (piece == 'b')
					space.setCurrentImage(blackPiece);
				else if (piece == 'w') 
					space.setCurrentImage(whitePiece);
			}
		}
	}

	/**
	 * get's the game board gui element
	 * @return the board
	 */
	public JPanel getBoard() {
		return gameBoard;
	}

	/**
	 * sets the gui for the session
	 * @param gui the GameWindow gui for the session
	 */
	public void setGui(GameWindow gui) {
		this.gui = gui;
		gui.setBlackScore(blackScore);
		gui.setWhiteScore(whiteScore);
		gui.setBlackName(blackName);
		gui.setWhiteName(whiteName);
	}
	
	/**
	 * gets the size for the current game of reversi
	 * @return the size of the current game
	 */
	public int getGameSize() {
		return game.getSize();
	}

	/**
	 * returns the session as to be saved in a file in the format
	 * "{blackName};{blackScore};{whiteName};{whiteScore};{game}"
	 * @return string representing the current session
	 */
	public String getFileFormattedString() {
		return blackName + ";" + blackScore + ";" + whiteName + ";" + whiteScore + ";" + game;
	}
}
