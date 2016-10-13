/**
 * 
 */
package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * Provides miscellaneous methods that are useful for various applications. 
 * 
 * @author Simon Heiden
 */
final public class Misc {
	
	//suppress default constructor (class should not be instantiated)
	private Misc() {
		throw new AssertionError();
	}
	
	/**
	 * searches for a method with the given name in the given class.
	 * @param target
	 * class in which to search for the method
	 * @param name
	 * identifier of the method to be searched for 
	 * @return
	 * the method or null if no match was found
	 */
	public static Method getMethod(
			final Class<?> target, final String name) {
		final Method[] mts = target.getDeclaredMethods();

		for (final Method m : mts) {
			if (m.getName().compareTo(name) == 0) {
				return m;
			}
		}
		return null;
	}
	
	/**
	 * Replaces all white spaces (including tabs, new lines, etc.) in a given 
	 * String with a replacement String.
	 * @param aString
	 * a String in which to replace white spaces
	 * @param replaceString
	 * the String to replace the white spaces with
	 * @return
	 * the result String
	 */
	public static String replaceWhitespacesInString(
			final String aString, final String replaceString) {
		return replaceNewLinesInString(aString, replaceString)
				.replace(" ", replaceString)
				.replace("\t", replaceString);
	}
	
	/**
	 * Replaces all new lines, carriage returns and form feeds in a given 
	 * String with a replacement String.
	 * @param aString
	 * a String in which to replace white spaces
	 * @param replaceString
	 * the String to replace the white spaces with
	 * @return
	 * the result String
	 */
	public static String replaceNewLinesInString(
			final String aString, final String replaceString) {
		return aString
				.replace("\n", replaceString)
				.replace("\r", replaceString)
				.replace("\f", replaceString);
	}
	
	/**
	 * Returns a String representation of the given array
	 * with ',' as separation element and enclosed in rectangular brackets.
	 * @param array
	 * an array
	 * @return
	 * a String representation of the given array
	 * @param <T>
	 * the type of the array
	 */
	public static <T> String arrayToString(final T[] array) {
		return arrayToString(array, ",", "[", "]");
	}
	
	/**
	 * Returns a String representation of the given array.
	 * @param array
	 * an array
	 * @param sepElement
	 * a separation element that separates the different elements of
	 * the array in the returned String representation
	 * @param start
	 * a String that marks the begin of the array
	 * @param end
	 * a String that marks the end of the array
	 * @return
	 * a String representation of the given array
	 * @param <T>
	 * the type of the array
	 */
	public static <T> String arrayToString(
			final T[] array, final String sepElement, 
			final String start, final String end) {
		final StringBuilder builder = new StringBuilder();
		builder.append(start);
		boolean isFirst = true;
		for (final T element : array) {
			if (isFirst) {
				isFirst = false;
			} else {
				builder.append(sepElement);
			}
			builder.append(element);
		}
		builder.append(end);
		
		return builder.toString();
	}
	
	/**
	 * Joins two arrays of type {@code T} and returns the concatenated arrays.
	 * @param a
	 * the first array
	 * @param b
	 * the second array
	 * @return
	 * the concatenation of the two given arrays
	 * @param <T>
	 * the type of the arrays
	 */
	public static <T> T[] joinArrays(final T[] a, final T[] b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}
		final Class<?> type = a.getClass().getComponentType();
		@SuppressWarnings("unchecked")
		final T[] joinedArray = createGenericArray((Class<T>)type, a.length + b.length);
		System.arraycopy(a, 0, joinedArray, 0, a.length);
		System.arraycopy(b, 0, joinedArray, a.length, b.length);
		return joinedArray;
	}
	
	/**
	 * Adds the given item to the end of the given 
	 * array of type {@code T}.
	 * @param a
	 * the first array
	 * @param items
	 * item to append to the array
	 * @return
	 * the array with the given item appended
	 * @param <T>
	 * the type of the arrays
	 */
	@SafeVarargs
	public static <T> T[] addToArrayAndReturnResult(final T[] a, final T... items) {
		return joinArrays(a, items);
	}
	
	/**
	 * Generates an array of a generic type that is only known at runtime.
	 * @param clazz
	 * the type of the items in the array
	 * @param arrayLength
	 * the length of the array
	 * @return
	 * the created array
	 * @param <T>
	 * the type of the items in the array
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] createGenericArray(final Class<T> clazz, final int arrayLength) {
        return (T[]) Array.newInstance(clazz, arrayLength);
    }
	
	/**
	 * Blocks further execution until the given thread is dead.
	 * @param thread
	 * the thread to wait on
	 */
	public static void waitOnThread(final Thread thread) {
		while (thread.isAlive()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				//do nothing
			}
		}
	}
	
	/**
	 * Converts a wrapper object array to its corresponding 
	 * simple type array.
	 * @param oBytes
	 * the wrapper object array
	 * @return
	 * the corresponding simple type array
	 */
	public static byte[] toPrimitives(final Byte[] oBytes)
	{
	    byte[] bytes = new byte[oBytes.length];

	    for(int i = 0; i < oBytes.length; i++) {
	        bytes[i] = oBytes[i];
	    }

	    return bytes;
	}
	
	/**
	 * Converts a wrapper object array to its corresponding 
	 * simple type array.
	 * @param oIntegers
	 * the wrapper object array
	 * @return
	 * the corresponding simple type array
	 */
	public static int[] toPrimitives(Integer[] oIntegers)
	{
	    int[] integers = new int[oIntegers.length];

	    for(int i = 0; i < oIntegers.length; i++) {
	        integers[i] = oIntegers[i];
	    }

	    return integers;
	}

	
	
}
