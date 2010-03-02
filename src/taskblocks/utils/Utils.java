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

package taskblocks.utils;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Utils {

	public static final long MILLISECONDS_PER_DAY = 1000 * 60 * 60 * 24;

	/** The first saturday since the Epoch (time 0). */
	public static final long FIRST_SATURDAY = 1;

	/**
	/**
	 * This method adds 'duration' working days to 'startTime'. Saturday is also the regular
	 * end of the work. (This is different from beginning of work - there saturday should be recounted
	 * to monday
	 * 
	 * @param startTime
	 * @param effort
	 * @param workload - workload of a worker, between 0 and 1.
	 * @return
	 */
	public static long countFinishTime(long startTime, long effort, double workload) {
		
		long duration = (long)((double)effort / workload);
		
		long startDayInWeek = getDayInWeek(startTime);
		
		long durationWeeks = (duration) / 5;
		long durationRest = (duration) % 5;
		
		long daysAdd = durationWeeks*2; // every week of work means 2 days of weekend
		
		if(startDayInWeek + durationRest > 5) {
			daysAdd += 2;
		}
		
		if(duration > 0 && startDayInWeek == 0 && duration % 5 == 0) {
			daysAdd-=2;
		}
		return startTime + duration + daysAdd;
	}
	
	/** Returns day in week for given time. Time is the count of days, not milliseconds
	 * 
	 * @param time Time in number of days.
	 * @return
	 */
	public static int getDayInWeek(long time) {
		return (int)((time + FIRST_SATURDAY + 2) % 7);
	}
	
	/**
	 * Repairs starting time of task - saturday and sunday are changed to next monday.
	 * @param startTime
	 * @return
	 */
	public static long repairStartTime(long startTime) {
		long startDayInWeek = Utils.getDayInWeek(startTime);
		if(startDayInWeek == 5) {
			return startTime+2;
		} else if(startDayInWeek == 6) {
			return startTime+1;
		}
		return startTime;
	}
	
	/** Counts duration between given times, counting only working days */
	public static long countWorkDuration(long start, long end) {
		long result;
		long weeks = (end-start) / 7;
		result = weeks * 5;
		long rest = (end-start) % 7;
		int startInWeek = getDayInWeek(start);
		for(int i = 1; i <= rest; i++) {
			long stepInWeek = (startInWeek + i) % 7;
			if(stepInWeek != 0 && stepInWeek != 6) {
				result ++;
			}
		}
		return result;
	}

	public static Element[] getChilds(Element e, String name) {
		List<Element>childs = new ArrayList<Element>();
		NodeList nl = e.getChildNodes();
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if(n.getNodeType() == Node.ELEMENT_NODE && name.equals(n.getNodeName())) {
				childs.add((Element)n);
			}
		}
		return childs.toArray(new Element[childs.size()]);
	}
	
	/**
	 * Returns text enclosed in the first child element of the parent element
	 * 
	 * @param parent
	 * @param childName
	 * @return
	 */
	public static String getFirstElemText(Element parent, String childName) {
		Element[] childs = getChilds(parent, childName);
		if(childs.length == 0) {
			return null;
		}
		return childs[0].getTextContent();
	}

	public static String getElemTexts(Element parent, String childName) {
		StringBuilder result = new StringBuilder();
		Element[] childs = getChilds(parent, childName);
		if(childs.length == 0) {
			return null;
		}
		int i = 0;
		for(Element child: childs) {
			if(i > 0) {
				result.append(",");
			}
			result.append(child.getTextContent());
			i++;
		}
		return result.toString();
	}

}
