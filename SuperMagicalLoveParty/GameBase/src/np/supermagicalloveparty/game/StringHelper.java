package np.supermagicalloveparty.game;

public class StringHelper
{
	/**
	 * returns the first index that matches the string. If it is not found, returns -1. CASE SENSITIVE.
	 * @param string String to look for
	 * @param array Array to look for the string in
	 * @return index where the string is found, or -1 if it is not found.
	 */
	public static int getIndexInArray(String string, String[] array)
	{
		for(int i = 0; i < array.length; i++)
		{
			if(string.equals(array[i]))
			{
				return i;
			}
		}
		return -1;
	}
	

	/**
	 * combines an array of words into a sentence.
	 * @param array List of words to combine. Must not be empty.
	 * @param start start index (inclusive)
	 * @param end end index (exclusive)
	 * @return A single string consisting of each word separated by a space
	 */
	public static String combineStrings(String[] array, int start, int end)
	{
		String sentence = "";
		for(int i = start; i < end-1; i++)
		{
			sentence += array[i] + " ";
		}
		sentence += array[end-1];
		return sentence;
	}
	
	/**
	 * combines an array of words into a sentence.
	 * @param array List of words to combine. Must not be empty.
	 * @return A single string consisting of each word separated by a space
	 */
	public static String combineStrings(String[] array)
	{
		return combineStrings(array, 0, array.length);
	}
	
	/**
	 * combines an array of words into a sentence.
	 * @param array List of words to combine. Must not be empty.
	 * @param start start index (inclusive)
	 * @return A single string consisting of each word separated by a space
	 */
	public static String combineStrings(String[] array, int start)
	{
		return combineStrings(array, start, array.length);
	}
}
