package edu.usc.infolab.base;

import java.util.LinkedList;
import java.util.List;

public class StringUtils {
	
	public static  List<String> SimpleSplit(String source, char gap)
	{
		List<String> result = new LinkedList<String>();
		if (source == null) return result;
		char[] sourceChars = source.toCharArray();
		int startIndex = 0, index = -1;
		while (index++ != sourceChars.length)
		{
			if (index == sourceChars.length || sourceChars[index] == gap)
			{
				char[] section = new char[index - startIndex];
				System.arraycopy(sourceChars, startIndex,section, 0, index - startIndex);
				result.add(String.valueOf(section));
				startIndex = index + 1;
			}
		}
		return result;
	}
	
	public static boolean IsNullOrEmpty(String str)
	{
		return (str==null || str.isEmpty());
	}
}
