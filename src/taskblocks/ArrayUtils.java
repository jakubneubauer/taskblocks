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
