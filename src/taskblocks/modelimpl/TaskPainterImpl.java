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
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import taskblocks.Colors;
import taskblocks.graph.TaskGraphPainter;

public class TaskPainterImpl implements TaskGraphPainter {

	Color _taskCol = Colors.TASK_COLOR;
	Color _taskSelCol = _taskCol.brighter();
	Color _taskBorderCol = _taskCol.darker();
	Color _taskBorderSelCol = Colors.SELECTOIN_COLOR;
	
	public void paintTask(Object task, Graphics2D g2, Rectangle bounds, boolean selected) {
		String taskName = ((TaskImpl)task).getName();
		
		Color col = ((TaskImpl)task).getColor();
		
		if(selected) {
			col = col.brighter();
		}
		g2.setColor(col);
		g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 5, 5);
		
		if(selected) {
			g2.setColor(_taskBorderSelCol);
		} else {
			g2.setColor(_taskBorderCol);
		}
		g2.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 5, 5);
		
		Rectangle oldClips = g2.getClipBounds();
		g2.setColor(Color.black);
		g2.clipRect(bounds.x, bounds.y, bounds.width-4, bounds.height-3);
		
		FontMetrics fm = g2.getFontMetrics();
		
		g2.drawString(taskName, bounds.x + 5, bounds.y+ (fm.getHeight() + bounds.height)/2 - fm.getDescent());
		g2.setClip(oldClips.x, oldClips.y, oldClips.width, oldClips.height);
		//g2.drawRect(bounds.x+1, bounds.y+1, bounds.width, bounds.height);
	}

	public void paintRowHeader(Object man, Graphics2D g2, Rectangle bounds, boolean selected) {
		if(selected) {
			g2.setColor(Color.LIGHT_GRAY);
			g2.fillRoundRect(bounds.x+3, bounds.y+6, bounds.width-7, bounds.height-6, 5, 5);
			g2.setColor(Color.GRAY);
			g2.drawRoundRect(bounds.x+3, bounds.y+6, bounds.width-7, bounds.height-6, 5, 5);
		}
		g2.setColor(Color.black);
		g2.drawString(((ManImpl)man).getName(), bounds.x + 8, bounds.y + (bounds.height + g2.getFontMetrics().getHeight())/2);
	}

}
