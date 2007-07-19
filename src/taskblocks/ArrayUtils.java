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

package taskblocks;


import java.lang.reflect.Array;

/**
 * Set of utilities used to manipulate arrays.
 * 
 * @author j.neubauer
 * @since 11.1.2007
 */
public class ArrayUtils {
	
	/**
	 * Creates new array of the same type as given array and adds specified member
	 * to the end of new array.
	 *  
	 * @param array
	 * @param member
	 * @return new array with member appened
	 */
	public static Object[] addToArray(Object[] array, Object member) {
		Object[] newArray = (Object[])Array.newInstance(array.getClass().getComponentType(), array.length + 1);
		System.arraycopy(array, 0, newArray, 0, array.length);
		newArray[array.length] = member;
		return newArray;
	}

	/**
	 * Finds member in array and if finds it, creates the same array without this member.
	 * 
	 * @param array
	 * @param member
	 * @return new array without member or the old array if member wasn't found
	 */
	public static Object[] removeFromArray(Object[] array, Object member) {
		int i;
		for(i = 0; i<array.length;i++) {
			if(array[i] == member) {
				break;
			}
		}
		// if we found the listener, construct new arrray without it.
		if(i < array.length) {
			Object[] newArray = (Object[])Array.newInstance(array.getClass().getComponentType(), array.length - 1);
			System.arraycopy(array, 0, newArray, 0, i);
			System.arraycopy(array, i+1, newArray, i, array.length-i-1);
			return newArray;
		}
		return array;
	}

	public static Object[] removeFromArray(Object[] array, int i) {
		if(i < 0 || i >= array.length) {
			throw new IndexOutOfBoundsException();
		}
		Object[] newArray = (Object[])Array.newInstance(array.getClass().getComponentType(), array.length - 1);
		System.arraycopy(array, 0, newArray, 0, i);
		System.arraycopy(array, i+1, newArray, i, array.length-i-1);
		return newArray;
	}
}
