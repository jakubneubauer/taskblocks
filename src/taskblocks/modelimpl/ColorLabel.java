package taskblocks.modelimpl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import taskblocks.Colors;

public class ColorLabel {
	
	public static ColorLabel[] COLOR_LABELS = new ColorLabel[] {
		new ColorLabel("None", Colors.TASK_COLOR, 0),
		new ColorLabel("Red", new Color(255,120,100), 1),
		new ColorLabel("Orange", new Color(255,200,80), 2),
		new ColorLabel("Yellow", new Color(255,255,100), 3),
		new ColorLabel("Green", new Color(140,255,110), 4),
		new ColorLabel("Gray", new Color(200,200,200), 5),
	};
	
	public Color _color;
	public String _name;
	public Icon  _icon;
	public int _index;
	public ColorLabel(String name, Color color, int index) {
		_name = name;
		_color = color;
		_index = index;
		BufferedImage img = new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D)img.getGraphics();
		g2.setColor(_color);
		g2.fillRect(0,0,img.getWidth(), img.getHeight());
		_icon = new ImageIcon(img);
	}
	public String toString() {
		return _name;
	}
}
