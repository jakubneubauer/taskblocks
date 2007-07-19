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

import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

public class TaskBlocks {

	static final boolean RUNNING_ON_MAC = System.getProperty("os.name")
			.toLowerCase().startsWith("mac os x");

	static final boolean RUNNING_ON_WINDOWS = System.getProperty("os.name")
			.toLowerCase().startsWith("windows");

	public TaskBlocks(final String[] args) {

		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.growbox.intrudes",
				"false");
		System.setProperty("com.apple.mrj.application.live-resize", "true");

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (args.length > 0) {
					for (int i = 0; i < args.length; i++) {
						new ProjectFrame().openFile(new File(args[i]));
					}
				} else {
					new ProjectFrame();
				}
			}
		});
	}

	public static void main(String args[]) {
		new TaskBlocks(args);
	}

	public static ImageIcon getImage(String name) {
		URL url = ProjectFrame.class.getResource("/taskblocks/img/" + name);
		if (url == null) {
			return null;
		}
		return new ImageIcon(url);
	}

}
