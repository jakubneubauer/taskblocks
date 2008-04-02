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

package taskblocks.graph;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import taskblocks.Pair;
import taskblocks.Utils;

public class GraphMouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

	/** 
	 * drag mode
	 * 0 - nothing,
	 * 1 - dragging whole selected tasks,
	 * 2 - dragging task left boundary,
	 * 3 - dragging task right boundary
	 * 4 - dragging new connection
	 * 5 - dragging the whole canvas
	 */
	int _dragMode;
	
	/** Used when dragging. Task on which the mouse was pressed*/
	Task _pressedTask;
	
	/** Last mouse-pressed position */
	int _pressX;
	
	/** Last mouse-pressed position */
	int _pressY;
	
	/** Used when dragging - on this position will be shown the moved task shadow*/
	long _cursorTime;
	
	/** Used when dragging */
	TaskRow _cursorTaskRow;
	
	/** Used when dragging  - task to which the new connection is to be created */
	Task _destTask;
	
	/** Used when dragging - last mouse x */
	private int _dragX;
	
	/** Used when dragging - last mouse y */
	private int _dragY;
	
	/** Remembered firstDay when mouse was pressed and drag mode is 5 (dragging whole canvas) */
	private long _pressFirstDay;
	
	/** The day on which the mouse was last pressed */
	private long _pressDay;
	
	/** Graph component for which this handler works */
	private TaskGraphComponent _graph;
	
	/** The set of currently selected tasks */
	Set<Object> _selection = new HashSet<Object>();
	
	GraphMouseHandler(TaskGraphComponent graph) {
		_graph = graph;
	}
	
	public void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();
		Object o = _graph.findObjectOnPo(p.x, p.y);
		Task t = null;
		
		
		GraphObject go = null;
		if(o instanceof GraphObject) {
			go = (GraphObject)o;
		}
		if(o instanceof Task) {
			t = (Task)o;
		} else if(o instanceof Pair) {
			Pair pressedObj = (Pair)o;
			t = (Task)pressedObj.fst;
			go = (Task)pressedObj.fst;
		}

		boolean selectionChanged = false;
		
		if(e.getButton() == MouseEvent.BUTTON1
				&& (
						(e.getModifiers() & MouseEvent.CTRL_MASK) != 0
						|| (e.getModifiers() & MouseEvent.META_MASK) != 0
						|| (e.getModifiers() & MouseEvent.SHIFT_MASK) != 0
					)
		) {
			
			// left mouse but. pressed with ctr or meta or shift => multi-selection

			if(go != null) {
				go._selected = !go._selected;
				selectionChanged = true;
				if(go._selected) {
					_selection.add(go);
					_dragMode = 1; // moving whole task(s)
				} else {
					_selection.remove(go);
				}
			}
		} else {
			
			if(go == null) {
				// outside task -> clear selection
				if(_selection.size() > 0) {
					clearSelection();
					selectionChanged = true;
				}
				
			} else {
				
				// if selection contains exactly t, do nothing
				if(_selection.size() != 1 || !_selection.contains(go)) {
					clearSelection();
					_selection.add(go);
					go._selected = true;
					selectionChanged = true;
				}
			}
		}
		
		if(selectionChanged) {
			_graph.repaint();
		}
		
		
		if(_graph._grActListener != null) {
			_graph._grActListener.mouseClicked(t == null ? null : t._userObject, e);
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		Point p = e.getPoint();
		_dragX = p.x;
		_dragY = p.y;
		switch(_dragMode) {
			case 1: // dragging whole pressed task
				if(_pressedTask != null) {
					
					// find the destination position.
					// 1. find row
					TaskRow myRow = _graph.findNearestRow(p.y);
					if(myRow != null) {
						// 2. find time
						int x = p.x - (_pressX - _pressedTask._bounds.x);
						_cursorTime = _graph.xToTime(x);
						_cursorTaskRow = myRow;
						_graph.repaint();
					} else {
						// nothing found, let the cursor be the last one.
					}
				}
				break;
			case 2: // dragging left task boundary
				if(_pressedTask != null) {
					long newStartDay = _graph.xToTime(p.x);
					long oldTaskStart = _pressedTask.getStartTime();
					if(oldTaskStart != newStartDay) {

						long t1 = Math.min(oldTaskStart, newStartDay);
						long t2 = Math.max(oldTaskStart, newStartDay);
						long workingDaysDiff = Utils.countWorkDuration(t1, t2);
						if(workingDaysDiff > 0) {
							_pressedTask.setStartTime(newStartDay);
							long newDuration = _pressedTask.getDuration();
							if(oldTaskStart > newStartDay) {
								newDuration += workingDaysDiff;
							} else {
								newDuration -= workingDaysDiff;
							}
							_pressedTask.setDuration(Math.max(1, newDuration));
							_graph.repaint();
						}
					}
				}
				break;
			case 3: // dragging right task boundary
				if(_pressedTask != null) {
					long newEndTime = _graph.xToTime(p.x);
					long oldEndTime = _pressedTask.getFinishTime();
					if(newEndTime != oldEndTime) {
						long t1 = Math.min(newEndTime, oldEndTime);
						long t2 = Math.max(newEndTime, oldEndTime);
						long workingDaysDiff = Utils.countWorkDuration(t1, t2);
						if(workingDaysDiff > 0) {
							long newDuration = _pressedTask.getDuration();
							if(newEndTime > oldEndTime) {
								newDuration += workingDaysDiff;
							} else {
								newDuration -= workingDaysDiff;
							}
							_pressedTask.setDuration(Math.max(1, newDuration));
							_graph.repaint();
						}
					}
				}
				break;
			case 4: // new connection
				if(_pressedTask != null) {
					Object o = _graph.findObjectOnPo(p.x, p.y);
					if(o instanceof Task) {
						_destTask = (Task)o;
					} else if(o instanceof Pair) {
						_destTask = (Task)((Pair)o).fst;
					} else {
						_destTask = null;
					}
				}
				_graph.repaint();
				break;
			case 5: // Scrolling the Frame
				long tmpFirstDay = _graph._firstDay;
				_graph._firstDay = _pressFirstDay;
				long mouseDay = _graph.xToTime(p.x);
				_graph._firstDay = tmpFirstDay;
				
				long newFirstDay = _pressFirstDay - (mouseDay - _pressDay);
				if(newFirstDay != _graph._firstDay) {
					_graph._firstDay = newFirstDay;
					_graph.repaint();
					_graph._builder.setPaintDirty();
				}
				break;
		}
		_graph._builder.updateModel();
	}
	
	public void mouseEntered(MouseEvent arg0) {
	}
	
	public void mouseExited(MouseEvent arg0) {
		_graph.setCursor(Cursor.getDefaultCursor());
	}
	
	public void mouseMoved(MouseEvent e) {
		Point p = e.getPoint();
		_dragX = p.x;
		_dragY = p.y;
		
		Object o = _graph.findObjectOnPo(p.x, p.y);
		_graph.changeCursor(o);
		if(o instanceof Task || o instanceof Pair) {
			Task t;
			if(o instanceof Task) {
				t = (Task)o;
			} else {
				t = (Task)((Pair)o).fst;
			}
			String taskName = _graph._model.getTaskName(t._userObject);
			DateFormat df = new SimpleDateFormat("d.M.");
			String start = df.format(new Date(t.getStartTime() * Utils.MILLISECONDS_PER_DAY));
			String end = df.format(new Date(t.getFinishTime() * Utils.MILLISECONDS_PER_DAY));
			String duration = String.valueOf(t.getDuration());
			String comment = t.getComment();
			_graph.setToolTipText("<html><p style=\"padding:2 5 2 5;\"><b>" + taskName + "</b><br>Start: " + start + "&nbsp;&nbsp;&nbsp;End: " + end + "<br>Duration: " + duration + " days<br>Comment: "+comment);
		} else {
			_graph.setToolTipText(null);
		}
	}
	
	public void mousePressed(MouseEvent e) {
		Point p = e.getPoint();
		Object o = _graph.findObjectOnPo(p.x, p.y);
		_graph.requestFocus();
		
		_pressX = p.x;
		_pressY = p.y;

		if(e.getButton() == MouseEvent.BUTTON1 && o instanceof Pair) {
			// presed on task left/right boundary
			Pair pressedObj = (Pair)o;
			Task t = (Task)pressedObj.fst;
			Integer direction = (Integer)pressedObj.snd;
			_pressedTask = t;
			if(TaskGraphComponent.LEFT == direction) {
				_dragMode = 2;
			} else if(TaskGraphComponent.RIGHT == direction) {
				_dragMode = 3;
			}
			return;
		}
		
		GraphObject go = null;
		if(o instanceof GraphObject) {
			go  = (GraphObject)o;
		}
		
		if(go instanceof Task) {
			_pressedTask = (Task)go;
		} else {
			_pressedTask = null;
		}
		
		if(e.getButton() != MouseEvent.BUTTON1) {
			_pressedTask = null;
		}
		
		if(_pressedTask != null && (e.getModifiers() & MouseEvent.SHIFT_MASK) != 0) {
			
			// mouse pressed with shift => start defining new connection
			_dragMode = 4; // draggin new connection
		} else {
			
			if(go == null || e.getButton() == MouseEvent.BUTTON3) {
				// outside task/connection or with right button -> drag the canvas
				
				if(p.x > _graph._graphLeft) {
					_pressFirstDay = _graph._firstDay;
					_pressDay = _graph.xToTime(p.x);
					_dragMode = 5;
				}
				return;
				
			} else {
				
				// if selection contains exactly t, do nothing
				_graph.repaint();
				_dragMode = 1; // moving whole tasks
			}
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		
		if(_cursorTaskRow != null && _cursorTime >= 0 && _pressedTask != null) {
			_pressedTask.setStartTime(_cursorTime);
			if(_cursorTaskRow != _pressedTask.getRow()) {
				// change man of the task
				_graph._builder.changeTaskRow(_pressedTask, _cursorTaskRow);
			}
			_graph._builder.recountStartingTimes();
		}
		
		if(_dragMode == 2 || _dragMode == 3) {
			if(_pressedTask != null) {
				_graph._builder.recountStartingTimes();
				_graph.repaint();
			}
		}
		
		// if creating new connection
		if(_dragMode == 4) {
			if(_pressedTask != null && _destTask != null && _pressedTask != _destTask) {
				try {
					_graph._builder.createConnection(_pressedTask, _destTask);
				} catch(Exception e1) {
					JOptionPane.showMessageDialog(_graph, "<html><b>Can't set dependency</b><br><br>" + e1.getMessage());
				}
				_graph._builder.recountStartingTimes();
				_graph.repaint();
			}
		}
		
		_graph._builder.updateModel();
		
		_graph.changeCursor(e.getPoint());
		_dragMode = 0;
		_cursorTaskRow = null;
		_cursorTime = -1;
		_destTask = null;
		_pressedTask = null;
		_graph.repaint();
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		int rot = e.getWheelRotation();
		if(rot < 0) {
			for(int i = 0; i < -rot; i++) {
				_graph.scaleUp();
			}
		} else if(rot > 0) {
			for(int i = 0; i < rot; i++) {
				_graph.scaleDown();
			}
		}
	}
	int getLastMouseX() {
		return _dragX;
	}
	int getLastMouseY() {
		return _dragY;
	}

	void clearSelection() {
		for(Object o: _selection) {
			if(o instanceof GraphObject) {
				((GraphObject)o)._selected = false;
			}
		}
		_selection.clear();
	}

	public void keyPressed(KeyEvent e) {
		/*if(e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			deleteSelection();
		} else */if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
			_graph._verticalScroll.setValue(_graph._verticalScroll.getValue() + _graph._graphHeight/2);
		} else if(e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
			_graph._verticalScroll.setValue(_graph._verticalScroll.getValue() - _graph._graphHeight/2);
		} else if(e.getKeyCode() == KeyEvent.VK_UP) {
			_graph._verticalScroll.setValue(_graph._verticalScroll.getValue() - 10);
		} else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			_graph._verticalScroll.setValue(_graph._verticalScroll.getValue() + 10);
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	void deleteSelection() {
		boolean changed = false;
		for(Object o: _selection) {
			if(o instanceof Connection) {
				_graph._builder.removeConnection((Connection)o);
				changed = true;
			}
		}
		for(Object o: _selection) {
			if(o instanceof Task) {
				_graph._builder.removeTask((Task)o);
				changed = true;
			}
		}
		for(Object o: _selection) {
			if(o instanceof TaskRow) {
				_graph._builder.removeRow((TaskRow)o);
				changed = true;
			}
		}
		if(changed) {
			_graph.repaint();
		}
	}
}
