package bigdb.tf.crawler;

import java.util.Collection;
import java.util.Iterator;

public class StringUtils
{

	/**
	 * Convert a {@link Collection} to a delimited {@code String} (e.g. CSV).
	 * <p>
	 * Useful for {@code toString()} implementations.
	 * 
	 * @param coll
	 *            the {@code Collection} to convert
	 * @param delim
	 *            the delimiter to use (typically a ",")
	 * @param prefix
	 *            the {@code String} to start each element with
	 * @param suffix
	 *            the {@code String} to end each element with
	 * @return the delimited {@code String}
	 */
	public static String collectionToDelimitedString(Collection<?> coll, String delim, String prefix, String suffix)
	{
		if (coll == null || coll.size() == 0)
		{
			return "";
		}
		StringBuilder sb = new StringBuilder();
		Iterator<?> it = coll.iterator();
		while (it.hasNext())
		{
			sb.append(prefix).append(it.next()).append(suffix);
			if (it.hasNext())
			{
				sb.append(delim);
			}
		}
		return sb.toString();
	}

	/**
	 * Convert a {@code Collection} into a delimited {@code String} (e.g. CSV).
	 * <p>
	 * Useful for {@code toString()} implementations.
	 * 
	 * @param coll
	 *            the {@code Collection} to convert
	 * @param delim
	 *            the delimiter to use (typically a ",")
	 * @return the delimited {@code String}
	 */
	public static String collectionToDelimitedString(Collection<?> coll, String delim)
	{
		return collectionToDelimitedString(coll, delim, "", "");
	}

	/**
	 * Convert a {@code Collection} into a delimited {@code String} (e.g., CSV).
	 * <p>
	 * Useful for {@code toString()} implementations.
	 * 
	 * @param coll
	 *            the {@code Collection} to convert
	 * @return the delimited {@code String}
	 */
	public static String collectionToCommaDelimitedString(Collection<?> coll)
	{
		return collectionToDelimitedString(coll, ",");
	}

	/**
	 * Convert a {@code String} array into a delimited {@code String} (e.g. CSV).
	 * <p>
	 * Useful for {@code toString()} implementations.
	 * 
	 * @param arr
	 *            the array to display
	 * @param delim
	 *            the delimiter to use (typically a ",")
	 * @return the delimited {@code String}
	 */
	public static String arrayToDelimitedString(Object[] arr, String delim)
	{
		if (arr == null || arr.length == 0)
		{
			return "";
		}
		if (arr.length == 1)
		{
			return arr[0] == null ? "" : arr[0].toString();
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++)
		{
			if (i > 0)
			{
				sb.append(delim);
			}
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	/**
	 * Convert a {@code String} array into a comma delimited {@code String}
	 * (i.e., CSV).
	 * <p>
	 * Useful for {@code toString()} implementations.
	 * 
	 * @param arr
	 *            the array to display
	 * @return the delimited {@code String}
	 */
	public static String arrayToCommaDelimitedString(Object[] arr)
	{
		return arrayToDelimitedString(arr, ",");
	}

}
