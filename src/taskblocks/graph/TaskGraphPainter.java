package taskblocks.graph;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public interface TaskGraphPainter {

	public void paintRowHeader(Object man, Graphics2D g2, Rectangle bounds, boolean selected);
	
	public void paintTask(Object task, Graphics2D g2, Rectangle bounds, boolean selected);
}
