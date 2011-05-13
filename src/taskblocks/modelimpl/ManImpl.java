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

public class ManImpl implements Cloneable {
	
	private String _name;
	private double _workload = 1.0; // by default, the worker will work full time on its jobs
	
	/** Used only when saving */
	public String _id;
	
	
	public ManImpl clone() {
		ManImpl clone;
		try {
			clone = (ManImpl) super.clone();
		} catch (CloneNotSupportedException e) {
			// NEVER GET HERE
			throw new RuntimeException(e);
		}
		return clone;
	}
	
	public void updateFrom(ManImpl m) {
		_name = m._name;
		_workload = m._workload;
	}
	
	public String getName() {
		return _name;
	}
	
	public String getLabel() {
		if(_workload == 1.0) {
			return _name;
		} else {
			return _name + " (" + ((int)(_workload * 100.0)) + "%)";
		}
	}
	
	public void setName(String name) {
		_name = name;
	}
	
	public String toString() {
		return _name;
	}
	
	public double getWorkload() {
		return _workload;
	}
	
	public void setWorkload(double workload) {
		_workload = workload;
	}
}
