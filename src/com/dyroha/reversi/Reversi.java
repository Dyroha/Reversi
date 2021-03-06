package com.dyroha.reversi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Game window for the Reversi application
 * 
 * @version 21/04/2021
 * @author Dylan Hamilton
 */
public class Reversi {

	private JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
	private JFrame frame;
	private GameSession session;
	private JLabel messageBar;
	private JTextField blackName;
	private JTextField whiteName;
	private JLabel blackScore;
	private JLabel whiteScore;
	private JLabel blackPieces;
	private JLabel whitePieces;
	private JButton playButton;
	private JPanel gameBag;
	private JPanel sidePanel;
	private int size = 8;

	public static void main(String[] args) {
		try {
			new Reversi();
		} catch (Exception e) {
			System.err.println("Something went wrong, application cannot be started");
			e.printStackTrace();
		}
	}

	/**
	 * Creates a GameWindow and components within then packs and show the frame
	 */
	public Reversi() throws IOException {
		frame = new JFrame();
		session = new GameSession(this);

		frame.setLayout(new BorderLayout());

		frame.setJMenuBar(createMenuBar());
		sidePanel = createSidePanel();
		frame.add(sidePanel, BorderLayout.EAST);

		messageBar = new JLabel("Press Play to start");
		frame.add(messageBar, BorderLayout.SOUTH);

		frame.pack();
		setFrameTitle();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLocation(100, 50);
		frame.setVisible(true);
	}

	private JMenuBar createMenuBar() {
		// create bar
		JMenuBar menuBar = new JMenuBar();

		// file
		JMenu file = new JMenu("File");
		// save session
		JMenuItem saveFile = new JMenuItem("Save Session");
		saveFile.addActionListener(e -> {
			int i = fc.showSaveDialog(frame);
			if (i == JFileChooser.APPROVE_OPTION) {
				try {
					ReversiIO.saveSession(fc.getSelectedFile(), session);
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(frame, "An Error has occured, could not save file", "Save File Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		// load session
		JMenuItem loadFile = new JMenuItem("Load Session");
		loadFile.addActionListener(e -> {
			if (session.getBoard() != null) {
				// ask for confirmation
				int i = JOptionPane.showConfirmDialog(frame,
						"Are you sure you want to load a session, " + "this will delete the current session",
						"Load Session Alert", JOptionPane.YES_NO_OPTION);
				if (i == JOptionPane.NO_OPTION)
					return;
			}
			//choose file
			fc.showOpenDialog(frame);
			//create session from file
			try {
				session = ReversiIO.loadSession(fc.getSelectedFile());
				session.setGui(this);
				size = session.getGameSize();
				play(false);
				this.setFrameTitle();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(frame, "An Error has occured, could not load file", "Load File Error", JOptionPane.ERROR_MESSAGE);
			}
		});

		file.add(saveFile);
		file.add(loadFile);

		// Game menu
		JMenu game = new JMenu("Game");
		// New session
		JMenuItem newSession = new JMenuItem("New Session");
		newSession.addActionListener(e -> {
			try {
				newSession();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(frame, "An Error has occured, could not create a new session", "New Session Error", JOptionPane.ERROR_MESSAGE);
			}
		});

		// resize
		JMenuItem resize = new JMenuItem("Change game size");
		resize.addActionListener(e -> changeSizePrompt());

		game.add(newSession);
		game.add(resize);

		// add menus to menu bar
		menuBar.add(file);
		menuBar.add(game);

		return menuBar;
	}

	private JPanel createSidePanel() {
		// side
		JLabel black = new JLabel("Black", SwingConstants.CENTER);
		black.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

		JLabel white = new JLabel("White", SwingConstants.CENTER);
		white.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

		// names
		blackName = new JTextField();
		blackName.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				blackName.selectAll();
			}
		});
		blackName.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		blackName.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		blackName.setHorizontalAlignment(SwingConstants.CENTER);
		blackName.setText("Player 1 name");

		whiteName = new JTextField();
		whiteName.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				whiteName.selectAll();
			}
		});
		whiteName.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		whiteName.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		whiteName.setHorizontalAlignment(SwingConstants.CENTER);
		whiteName.setText("Player 2 name");

		// scores
		blackScore = new JLabel("0", SwingConstants.CENTER);
		blackScore.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));

		whiteScore = new JLabel("0", SwingConstants.CENTER);
		whiteScore.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));

		// pieces count
		blackPieces = new JLabel("0", SwingConstants.CENTER);

		whitePieces = new JLabel("0", SwingConstants.CENTER);

		// Play button
		playButton = new JButton("Play");
		
		playButton.addActionListener(e -> play(true));
		playButton.setPreferredSize(new Dimension(100, 30));

		// create sidePanel
		sidePanel = new JPanel(new GridBagLayout());
		sidePanel.setPreferredSize(new Dimension(250, 600));
		sidePanel.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints c = new GridBagConstraints();

		// add names to sidePanel
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 0.1;

		c.gridx = 0;
		sidePanel.add(black, c);

		c.gridx = 1;
		sidePanel.add(white, c);

		// add names to sidePanel
		c.gridy = 1;

		c.gridx = 0;
		sidePanel.add(blackName, c);

		c.gridx = 1;
		sidePanel.add(whiteName, c);

		// add scores
		c.gridy = 2;
		c.weighty = 0.2;

		c.gridx = 0;
		sidePanel.add(blackScore, c);

		c.gridx = 1;
		sidePanel.add(whiteScore, c);

		// add points
		c.gridy = 3;
		c.weighty = 0.2;

		c.gridx = 0;
		sidePanel.add(blackPieces, c);

		c.gridx = 1;
		sidePanel.add(whitePieces, c);

		// add play button
		c.gridy = 4;
		c.weighty = 0.2;
		c.gridx = 0;
		c.gridwidth = 2;
		sidePanel.add(playButton, c);

		return sidePanel;

	}

	private void play(boolean newGame) {
		if (newGame)
			session.startGame(blackName.getText(), whiteName.getText(), size);
		session.setUpGame();
		disableNameInputs();
		playButton.setVisible(false);
		playButton.setText("Next Game");

		if (gameBag != null)
			frame.remove(gameBag);

		gameBag = new JPanel();
		gameBag.setLayout(new GridBagLayout());
		gameBag.add(session.getBoard());

		frame.add(gameBag, BorderLayout.CENTER);
		SwingUtilities.updateComponentTreeUI(frame);
	}

	private void disableNameInputs() {
		blackName.setEditable(false);
		whiteName.setEditable(false);
		blackName.setBackground(Color.LIGHT_GRAY);
		whiteName.setBackground(Color.LIGHT_GRAY);
		blackName.setBorder(BorderFactory.createEmptyBorder());
		whiteName.setBorder(BorderFactory.createEmptyBorder());
	}

	
	private void changeSizePrompt() {
		if (session.getBoard() != null) {
			// check if they are sure
			int input = JOptionPane.showConfirmDialog(frame,
					"Changing the game size will reset the current game, are you sure you want to do this?",
					"Change size alert", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			// no option
			if (input == 1)
				return;
		}
		changeSize();
	}

	private void changeSize() {
		// get input and check it
		String inputSize = JOptionPane
				.showInputDialog("What size board do you want?\n(the larger it is the slower the game will run)");
		//check if null
		if(inputSize == null) {
			return;
		}
		// check if valid size
		if (isValidInput(inputSize)) {
			size = Integer.parseInt(inputSize);
			this.setFrameTitle();
			if (session.getBoard() != null)
				play(true);
			// send error otherwise
		} else {
			JOptionPane.showMessageDialog(frame,
					"Invalid game size, input must be an even positive intager greater than 2", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private boolean isValidInput(String inputSize) {
		Pattern p = Pattern.compile("[1-9]*[0-9]*[02468]");
		Matcher m = p.matcher(inputSize);
		return m.matches() && Integer.parseInt(inputSize) > 2;
	}

	private void newSession() throws IOException {
		if (gameBag == null)
			return;
		
		if (session.getBoard() != null) {
			// ask for confirmation
			int i = JOptionPane.showConfirmDialog(frame,
					"Are you sure you want to start a new session, " + "this will delete the current session",
					"New Session Alert", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (i == JOptionPane.NO_OPTION)
				return;
		}
		size = 8;
		setFrameTitle();
		session = new GameSession(this);
		frame.remove(gameBag);
		frame.remove(sidePanel);
		sidePanel = createSidePanel();
		frame.add(sidePanel, BorderLayout.EAST);
		messageBar.setText("Press Play to start");
		SwingUtilities.updateComponentTreeUI(frame);
	}

	private void setFrameTitle() {
		frame.setTitle("Reversi: " + size + "x" + size);
	}

	/**
	 * sets the white side's score
	 * 
	 * @param score the score for which to be set
	 */
	public void setWhiteScore(int score) {
		whiteScore.setText(Integer.toString(score));
	}

	/**
	 * sets the black side's score
	 * 
	 * @param score the score for which to be set
	 */
	public void setBlackScore(int score) {
		blackScore.setText(Integer.toString(score));
	}

	/**
	 * sets the black side's current number of pieces on the board
	 * 
	 * @param noPieces the number of pieces for which to be set
	 */
	public void setBlackPieces(int noPieces) {
		blackPieces.setText(Integer.toString(noPieces));
	}

	/**
	 * sets the white side's current number of pieces on the board
	 * 
	 * @param noPieces the number of pieces for which to be set
	 */
	public void setWhitePieces(int noPieces) {
		whitePieces.setText(Integer.toString(noPieces));
	}

	/**
	 * sets the message for the message bar
	 * 
	 * @param message the message for which to be set
	 */
	public void setMessageBar(String message) {
		messageBar.setText(message);
	}

	/**
	 * sets the black side's player name
	 * 
	 * @param name the name of the black side's player
	 */
	public void setBlackName(String name) {
		this.blackName.setText(name);
	}

	/**
	 * sets the white side's player name
	 * 
	 * @param name the name of the white side's player
	 */
	public void setWhiteName(String name) {
		this.whiteName.setText(name);
	}

	/**
	 * creates dialog box saying a player cannot make a move
	 * 
	 * @param player the player's name
	 */
	public void alertNoTurn(String player) {
		JOptionPane.showMessageDialog(frame, player + " has no valid moves");
	}

	/**
	 * creates dialog box saying the endGameMessage, then allows the user to start a new game
	 * @param endGameMessage the message to be displayed
	 */
	public void endOfGameEvent(String endGameMessage) {
		JOptionPane.showMessageDialog(frame, endGameMessage);
		setMessageBar("Press Next Game to start a new game");
		playButton.setVisible(true);

	}
}
