package taskblocks.app;

import java.awt.Dimension;

import javax.swing.JSeparator;

public class FixedSeparator extends JSeparator {

	public FixedSeparator(int direction) {
		super(direction);
	}
	
	public Dimension getMaximumSize() {
		Dimension s = super.getMaximumSize();
		if(getOrientation() == HORIZONTAL) {
			s.height = getPreferredSize().height;
		} else {
			s.width = getPreferredSize().width;
			s.height = 40;
		}
		return s;
	}
}
