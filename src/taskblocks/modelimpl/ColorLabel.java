/*
 * Copyright (C) Jakub Neubauer, 2007
 *
 * This file is part of TaskBlocks
 *
 * TaskBlocks is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * TaskBlocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package taskblocks.modelimpl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import taskblocks.utils.Colors;

public class ColorLabel {
	
	public static ColorLabel[] COLOR_LABELS = new ColorLabel[] {
		new ColorLabel("None", Colors.TASK_COLOR, 0),
		new ColorLabel("Red", new Color(255,120,100), 1),
		new ColorLabel("Orange", new Color(255,200,80), 2),
		new ColorLabel("Yellow", new Color(255,255,100), 3),
		new ColorLabel("Green", new Color(140,255,110), 4),
		new ColorLabel("Gray", new Color(200,200,200), 5),
	};
	
	final public Color _color;
	final public String _name;
	final public Icon  _icon;
	final public int _index;
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
