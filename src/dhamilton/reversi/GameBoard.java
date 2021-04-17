package dhamilton.reversi;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;

public class GameBoard extends JPanel {
	
	public GameBoard(int size) {
		super(new GridLayout(size, size));
	}
	
	@Override
	public Dimension getPreferredSize() {
		Dimension dimension = super.getPreferredSize();
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
