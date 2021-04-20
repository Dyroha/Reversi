package dhamilton.reversi;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JButton;

/**
 * A square board space for a game board
 * 
 * @version 20/04/2021
 * @author Dylan Hamilton
 */
public class BoardSpace extends JButton {

	private Image currentImage;
	private final int yPos;
	private final int xPos;

	/**
	 * creates a board space
	 * @param y the y position (row) the space is in
	 * @param x the x position (collumn) the space is in
	 * @param blankSpace an image for the default space
	 */
	public BoardSpace(int y, int x, BufferedImage blankSpace) {
		currentImage = blankSpace;
		this.yPos = y;
		this.xPos = x;
	}

	/**
	 * sets the current image to one provided i.e. change the shown piece to reflect the reversi board
	 * @param piece image of the piece
	 */
	public void setCurrentImage(BufferedImage piece) {
		currentImage = piece;
		repaint();
	}

	/**
	 * paints the componant with the current image
	 */
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D pic = (Graphics2D) g.create();
		pic.drawImage(currentImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, this);

	}

	/**
	 * returns the y position (row) of the reflected piece
	 * @return y position
	 */
	public int getYPos() {
		return yPos;
	}

	/**
	 * returns the x position (collumn) of the reflected piece
	 * @return x position
	 */
	public int getXPos() {
		return xPos;
	}
}
