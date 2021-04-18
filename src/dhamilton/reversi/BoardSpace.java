package dhamilton.reversi;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JButton;

public class BoardSpace extends JButton {

	private Image currentImage;
	private final int yPos, xPos;

	public BoardSpace(int y, int x, BufferedImage blankSpace) {
		currentImage = blankSpace;
		this.yPos = y;
		this.xPos = x;
	}

	public void setCurrentImage(BufferedImage piece) {
		currentImage = piece;
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D pic = (Graphics2D) g.create();
		pic.drawImage(currentImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, this);

	}

	public int getYPos() {
		return yPos;
	}

	public int getXPos() {
		return xPos;
	}
}
