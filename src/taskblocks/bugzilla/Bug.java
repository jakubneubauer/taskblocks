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

package taskblocks.bugzilla;

public class Bug {
	private String _product;
	private String _component;
	private String _summary;
	private String _version;
	private String _hardware;
	private String _platform;
	private String _os;
	private String _priority;
	private String _severity;
	private String _status;
	private String _assignedTo;
	private long _estimation;
	private String _statusWhiteboard;
	private String[] _blocks; 
	private String[] _keywords;
}
