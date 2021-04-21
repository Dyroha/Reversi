package com.dyroha.reversi;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;

/**
 * A game board with a GridLayout that preferes to be square
 * 
 * @version 20/04/2021
 * @author Dylan Hamilton
 */
public class GameBoard extends JPanel {
	
	/**
	 * Creates a GameBoard with a square GridLayout
	 * @param size the number of collumns and rows for the grid
	 */
	public GameBoard(int size) {
		super(new GridLayout(size, size));
	}
	
	/**
	 * returns a square Dimension with the smallest of the height and width as both height and width values
	 * @return a square Dimension object
	 */
	@Override
	public Dimension getPreferredSize() {
		Dimension dimension;
		Container box = getParent();
		if (box != null) {
			dimension = box.getSize();
		} else {
			return new Dimension(10, 10);
		}
		int w = (int) dimension.getWidth();
		int h = (int) dimension.getHeight();
		int s = (w < h ? w : h);
		return new Dimension(s, s);
	}
}
