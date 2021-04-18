package dhamilton.reversi;

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

public class GameWindow extends JFrame {

	private JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
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

	public GameWindow() throws IOException {
		session = new GameSession(this);

		this.setLayout(new BorderLayout());

		this.setJMenuBar(createMenuBar());
		sidePanel = createSidePanel();
		this.add(sidePanel, BorderLayout.EAST);

		messageBar = new JLabel("Press Play to start");
		this.add(messageBar, BorderLayout.SOUTH);

		this.pack();
		this.setTitle("Reversi: 8x8");
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setSize(800, 600);
		this.setLocation(100, 50);
		this.setVisible(true);
	}

	private JMenuBar createMenuBar() {
		// create bar
		JMenuBar menuBar = new JMenuBar();

		// file
		JMenu file = new JMenu("File");
		// save session
		JMenuItem saveFile = new JMenuItem("Save Session");
		saveFile.addActionListener(e -> {
			int i = fc.showSaveDialog(this);
			if (i == JFileChooser.APPROVE_OPTION) {
				try {
					ReversiIO.saveSession(fc.getSelectedFile(), session);
				} catch (IOException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		});
		// load session
		JMenuItem loadFile = new JMenuItem("Load Session");
		loadFile.addActionListener(e -> {
			if (session.getBoard() != null) {
				// ask for confirmation
				int i = JOptionPane.showConfirmDialog(this,
						"Are you sure you want to load a session, " + "this will delete the current session",
						"Load Session Alert", JOptionPane.YES_NO_OPTION);
				if (i == JOptionPane.NO_OPTION)
					return;
			}
			fc.showOpenDialog(this);
			try {
				session = ReversiIO.loadSession(fc.getSelectedFile());
				session.setGui(this);
				size = session.getGameSize();
				play(false);
				this.setTitle("Reversi: " + size + "x" + size);
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
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
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
			public void mouseClicked(MouseEvent e) {
				blackName.selectAll();
			}
		});
		blackName.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		blackName.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		blackName.setHorizontalAlignment(JTextField.CENTER);
		blackName.setText("Player 1 name");

		whiteName = new JTextField();
		whiteName.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				whiteName.selectAll();
			}
		});
		whiteName.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		whiteName.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		whiteName.setHorizontalAlignment(JTextField.CENTER);
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
		// TODO create play button listener
		playButton.addActionListener(e -> play(true));
		playButton.setPreferredSize(new Dimension(100, 30));

		// create sidePanel
		JPanel sidePanel = new JPanel(new GridBagLayout());
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
		if (newGame == true)
			session.startGame(blackName.getText(), whiteName.getText(), size);
		session.setUpGame();
		disableNameInputs();
		playButton.setVisible(false);
		playButton.setText("Next Game");

		if (gameBag != null)
			this.remove(gameBag);

		gameBag = new JPanel();
		gameBag.setLayout(new GridBagLayout());
		gameBag.add(session.getBoard());

		this.add(gameBag, BorderLayout.CENTER);
		SwingUtilities.updateComponentTreeUI(this);
	}

	private void disableNameInputs() {
		blackName.setEditable(false);
		whiteName.setEditable(false);
		blackName.setBackground(Color.LIGHT_GRAY);
		whiteName.setBackground(Color.LIGHT_GRAY);
		blackName.setBorder(BorderFactory.createEmptyBorder());
		whiteName.setBorder(BorderFactory.createEmptyBorder());
	}

	public void setWhiteScore(int score) {
		whiteScore.setText(Integer.toString(score));
	}

	public void setBlackScore(int score) {
		blackScore.setText(Integer.toString(score));
	}

	public void setBlackPieces(int noPieces) {
		blackPieces.setText(Integer.toString(noPieces));
	}

	public void setWhitePieces(int noPieces) {
		whitePieces.setText(Integer.toString(noPieces));
	}

	public void setMessageBar(String message) {
		messageBar.setText(message);
	}

	public void setBlackName(String name) {
		this.blackName.setText(name);
	}

	public void setWhiteName(String name) {
		this.whiteName.setText(name);
	}

	private void changeSizePrompt() {
		if (session.getBoard() != null) {
			// check if they are sure
			int input = JOptionPane.showConfirmDialog(this,
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
			this.setTitle("Reversi: " + size + "x" + size);
			if (session.getBoard() != null)
				play(true);
			// send error otherwise
		} else {
			JOptionPane.showMessageDialog(this,
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
			int i = JOptionPane.showConfirmDialog(this,
					"Are you sure you want to start a new session, " + "this will delete the current session",
					"New Session Alert", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (i == JOptionPane.NO_OPTION)
				return;
		}
		size = 8;
		this.setTitle("Reversi: " + size + "x" + size);
		session = new GameSession(this);
		this.remove(gameBag);
		this.remove(sidePanel);
		sidePanel = createSidePanel();
		this.add(sidePanel, BorderLayout.EAST);
		SwingUtilities.updateComponentTreeUI(this);
	}

	public void alertNoTurn(String player) {
		JOptionPane.showMessageDialog(this, player + " has no valid moves");
	}

	public void endOfGameEvent(String endGameMessage) {
		JOptionPane.showMessageDialog(this, endGameMessage);
		setMessageBar("Press Next Game to start a new game");
		playButton.setVisible(true);

	}
}
