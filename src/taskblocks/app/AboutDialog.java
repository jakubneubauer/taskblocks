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

package taskblocks.app;

import java.awt.Component;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class AboutDialog {
	public static void showAbout(Component parent) {
		
		Box b = Box.createVerticalBox();
		Box b2 = Box.createHorizontalBox();
		JLabel l3 = new JLabel();
		JLabel l1 = new JLabel("<html><h2>Task Blocks</h2>Version " + Version.VERSION);
		JLabel l2 = new JLabel(
				"<html><br>"
				+ "<center>Copyright \u00A9 2007 Jakub Neubauer<br>"
				+"&lt;http://taskblocks.googlecode.com&gt;"
				+"</center><br>"
				);
		l3.setIcon(TaskBlocks.getImage("taskblocks.png"));
		l1.setIconTextGap(30);
		l1.setHorizontalTextPosition(SwingConstants.LEFT);
		l1.setFont(l1.getFont().deriveFont(Font.PLAIN));
		l2.setFont(l2.getFont().deriveFont(Font.PLAIN));
		b2.add(l1);
		b2.add(l3);
		b.add(b2);
		b.add(l2);
		l3.setBorder(new EmptyBorder(0,0,0,20));
		
		l1.setAlignmentX(0f);
		l2.setAlignmentX(0f);
		l3.setAlignmentX(0f);
		b2.setAlignmentX(0f);
		
		JOptionPane op = new JOptionPane(b);
		JDialog d = op.createDialog(parent, "About");
		d.setVisible(true);
	}
	
	public static void main(String[] args) {
		AboutDialog.showAbout(null);
	}
}
