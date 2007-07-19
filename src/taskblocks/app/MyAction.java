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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

public abstract class MyAction extends AbstractAction {
	
	public MyAction(String name) {
		super(name);
		this.putValue(Action.LONG_DESCRIPTION, name);
		this.putValue(Action.SHORT_DESCRIPTION, name);
	}
	
	public MyAction(String name, Icon icon) {
		super(name, icon);
		this.putValue(Action.SHORT_DESCRIPTION, name);
		this.putValue(Action.LONG_DESCRIPTION, name);
	}
	
	public MyAction(String name, Icon icon, String longDescription) {
		super(name, icon);
		this.putValue(Action.SHORT_DESCRIPTION, name);
		this.putValue(Action.LONG_DESCRIPTION, longDescription);
	}

}
