package com.dyroha.reversi;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;

/**
 * A square board space for a game board
 * 
 * @version 21/04/2021
 * @author Dylan Hamilton
 */
public class BoardSpace extends JComponent implements MouseListener {

	private Image currentImage;
	private final int yPos;
	private final int xPos;
	private ArrayList<ActionListener> listeners;

	/**
	 * creates a board space
	 * @param y the y position (row) the space is in
	 * @param x the x position (collumn) the space is in
	 * @param blankSpace an image for the default space
	 */
	public BoardSpace(int y, int x, BufferedImage blankSpace) {
		super();
		listeners = new ArrayList<>();
		enableInputMethods(true);
		addMouseListener(this);
		currentImage = blankSpace;
		this.yPos = y;
		this.xPos = x;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(getWidth(),getHeight());
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	public void addActionListener(ActionListener a) {
		listeners.add(a);
	}

	public void notifyListeners(MouseEvent e) {
		ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "", e.getWhen(), e.getModifiersEx());
        synchronized(listeners)
        {
            for (int i = 0; i < listeners.size(); i++)
            {
                ActionListener tmp = listeners.get(i);
                tmp.actionPerformed(evt);
            }
        }
	}

	public void mousePressed(MouseEvent e) {
		notifyListeners(e);
	}

	public void mouseReleased(MouseEvent e) {
		//
	}

	public void mouseClicked(MouseEvent e) {
		//
	}

	public void mouseEntered(MouseEvent e) {
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}

	public void mouseExited(MouseEvent e) {
		this.setBorder(BorderFactory.createEmptyBorder());
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
