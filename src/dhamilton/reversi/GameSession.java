package dhamilton.reversi;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GameSession {
	private GameWindow gui;
	private ReversiGame game;
	private int blackScore, whiteScore;
	private String blackName, whiteName;
	private GameBoard gameBoard;
	private BufferedImage blankSpace;
	private BufferedImage blackPiece;
	private BufferedImage whitePiece;
	
	public GameSession(GameWindow gameWindow) throws IOException {
		gui = gameWindow;
		blackScore = 0;
		whiteScore = 0;
		getBoardImages();
	}
	
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
	
	public void setUpGame() {
		gameBoard = new GameBoard(game.getSize());
		generateSpaces(game.getSize());
		updateGUI(true);
		refreshBoard();
	}
	
	private void getBoardImages() throws IOException {
		blankSpace = ImageIO.read(getClass().getResource("/resources/blank_space.png"));
		blackPiece = ImageIO.read(getClass().getResource("/resources/black_piece.png"));
		whitePiece = ImageIO.read(getClass().getResource("/resources/white_piece.png"));
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
		game.countPieces();
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
				char piece = game.getBoardPiece(new int[] { space.getYPos(), space.getXPos() });
				if (piece == '-') continue;
				if (piece == 'b')
					space.setCurrentImage(blackPiece);
				else if (piece == 'w') 
					space.setCurrentImage(whitePiece);
			}
		}
	}

	public JPanel getBoard() {
		return gameBoard;
	}

	public void setGui(GameWindow gui) {
		this.gui = gui;
		gui.setBlackScore(blackScore);
		gui.setWhiteScore(whiteScore);
		gui.setBlackName(blackName);
		gui.setWhiteName(whiteName);
	}

	public void setGame(ReversiGame game) {
		this.game = game;
	}
	
	public int getGameSize() {
		return game.getSize();
	}

	public String getFileFormattedString() {
		return blackName + ";" + blackScore + ";" + whiteName + ";" + whiteScore + ";" + game;
	}
}
