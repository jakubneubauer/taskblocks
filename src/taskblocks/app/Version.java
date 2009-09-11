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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Version {

	// Application version. On runtime read from file which is generated when building the application.
	public static final String VERSION;
	
	static {
		String ver ="unknown";
		InputStream versionResourceStream = null;
		try {
			versionResourceStream = ClassLoader.getSystemResourceAsStream("taskblocks/version");
			if(versionResourceStream != null) {
				BufferedReader r = new BufferedReader(new InputStreamReader(versionResourceStream));
				ver=r.readLine();
			}
		} catch (Exception e) {
			// DO NOTHING
		} finally {
			if(versionResourceStream != null) {
				try {
					versionResourceStream.close();
				} catch (IOException e) {
					// DO NOTHING
				}
			}
		}
		VERSION=ver;
	}
}
