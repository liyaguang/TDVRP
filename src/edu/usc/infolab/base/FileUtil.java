package edu.usc.infolab.base;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil
{
	
	/**
	 * try open a file which is readable.
	 * return the file instance when it is ok;
	 * throw Exception when it is not existed or is not readale.
	 * 
	 * @author JiahaiWu
	 * @param filePath  
	 * @throws ComponentException 
	 */
	public  static File readableFile(String filePath) throws IOException
	{
		File  file = new File(filePath);
		if(file.isFile() && file.canRead())
			return file;
		else
			throw new IOException("can not open the readable file with path :".concat(filePath));
	}
	
	
	public static File writeableFile(String filePath) throws IOException
	{
		return writeableFile(filePath,false);
	}

	/**
	 * try open a file which is writeable.
	 * if the file parent directories not exist, auto make the directroies.
	 * return the file instance when it is ok;
	 * throw Exception when the file open failed.
	 * 
	 * @param filePath
	 * @param overWrite If this setting is true,the file which is existed shall
	 *        be deleted first.
	 * @return
	 * @throws ComponentException
	 */
	public static File writeableFile(String filePath,boolean overWrite) throws IOException
	{
		File  file = new File(filePath);
		try
		{
			if (!file.isDirectory())
			{
				if(file.exists() && overWrite)
				{
						file.delete();
						Thread.sleep(50);
						file.createNewFile();
				}
				else
				{
					if(!file.getParentFile().exists())
					file.getParentFile().mkdirs();
					file.createNewFile();
				}
			}
			else
				throw new IOException("not a file with path: ".concat(filePath));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new IOException("open the writeable file Exception.",e.getCause());
		}
		if(file.canWrite())
			return file;
		else
			throw new IOException("can not open the writeable file with path :".concat(filePath));
	}
	
	/**
	 * try open a folder which matches the folderPath.
	 * if the file parent directories not exist, auto make the directroies.
	 * return the file instance when it is ok;
	 * throw Exception when the file open failed.
	 * @author JiahaiWu
	 * @param folderPath
	 * @return
	 * @throws ComponentException
	 */
	public static File newFolder(String folderPath) throws IOException
	{
		String filePath = folderPath;
		if(!filePath.endsWith(File.separator) && !filePath.endsWith("\\"))
			filePath +=File.separator;
		File  file = new File(filePath);
		try
		{
			if(!file.exists())
			{
				if(!file.exists())
				file.mkdirs();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new IOException("open the folfer Exception.",e.getCause());
		}
		return file;
	}

	/**
	 * get the bufferReader for the filepath.
	 * 
	 * @author JiahaiWu
	 * @param filePath
	 * @param charset
	 * @return
	 * @throws ComponentException
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public static BufferedReader fileBufferReader(String filePath, String charset) throws IOException, UnsupportedEncodingException,
			FileNotFoundException
	{
		File readFile = readableFile(filePath);
		return new BufferedReader(new InputStreamReader(new FileInputStream(readFile), charset));
	}

	/**
	 * open a bufferwriter with the filePath.
	 * Note: If this setting is true,the file which is existed shall
	 * be deleted first.
	 * 
	 * @author JiahaiWu
	 * @param filePath
	 * @param charset
	 * @param append
	 * @return
	 * @throws ComponentException
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public static BufferedWriter fileBufferedWriter(String filePath, String charset, boolean append) throws IOException,
			UnsupportedEncodingException, FileNotFoundException
	{
		File outFile = writeableFile(filePath, true);
		if(charset==null)
			return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile, append)));
		else
			return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile, append), charset));
	}

	/**
	 * try get the last suffix name of the supposedly file path.
	 * if the suffix can not be found, it will return the empty String.
	 * 
	 * @author JiahaiWu
	 * @param path
	 * @return
	 */
	public static String getFilePathSuffix(String path)
	{
		String suffix = "";
		if (path != null)
		{
			int s = path.lastIndexOf('.');
			if (s > -1)
				suffix = path.substring(s + 1);
		}
		else
			throw new NullPointerException("the path can not be NULL.");
		return suffix;
	}

	/**
	 * try get the last file name of the supposedly file path.
	 * if the file name can not be found, it will throw ComponentException.
	 * 
	 * @author JiahaiWu
	 * @param path
	 * @return
	 * @throws ComponentException
	 */
	public static String getFileName(String path) throws IOException
	{
		String name = path;
		if (name != null)
		{
			int s = name.lastIndexOf(File.separatorChar);
			if (s > -1)
			{
				name = name.substring(s + 1);
				return name;
			}
			else
			{
				return name;
			}
		}
		else
			throw new NullPointerException("the path can not be NULL.");
	}

	
	public static String getFileNameAndNoSuffix(String path)
	{
		String name = path;
		if (name != null)
		{
			int s = name.lastIndexOf(File.separatorChar);
			if (s > -1)
			{
				name = name.substring(s + 1);
			}
			s = name.lastIndexOf('.');
			if (s > -1)
				name = name.substring(0,s);
			return name;
		}
		else
			throw new NullPointerException("the path can not be NULL.");
	}
	
	public static String getMinFileNameAndNoSuffix(String path)
	{
		String name = path;
		if (name != null)
		{
			int s = name.lastIndexOf(File.separatorChar);
			if (s > -1)
			{
				name = name.substring(s + 1);
			}
			s = name.indexOf('.');
			if (s > -1)
				name = name.substring(0,s);
			return name;
		}
		else
			throw new NullPointerException("the path can not be NULL.");
	}
	
	/**
	 * read the file as text to a String Object and return the String.
	 * if the file is too large, it will throw ComponentException.
	 * 
	 * @param readFilePath
	 * @param CharsetStr
	 * @return
	 * @throws ComponentException
	 * @throws IOException
	 */
	public static String readFileAsText(String readFilePath, String CharsetStr) throws IOException
	{
		File readFile = FileUtil.readableFile(readFilePath);
		return readFileAsText(readFile, CharsetStr);
	}

	/**
	 * read the file as text to a String Object and return the String.
	 * if the file is too large, it will throw ComponentException.
	 * 
	 * @author JiahaiWu
	 * @param readFile
	 * @param CharsetStr
	 * @return
	 * @throws ComponentException
	 * @throws IOException
	 */
	public static String readFileAsText(File readFile, String CharsetStr) throws IOException
	{
		if (readFile.length() > MaxMemCacheBlock)
		{
			throw new IOException("the File is too large (large than 100MB), it should be not read once-off in this way.");
		}
		else
		{
			String cacheStr = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(readFile), CharsetStr));
			String Line = reader.readLine();
			if(Line!=null)
			{
				cacheStr = Line;
				while ((Line = reader.readLine()) != null)
				{
					cacheStr = cacheStr.concat("\n\r").concat(Line);
				}
			}
			reader.close();
			return cacheStr;
		}
	}

	/**
	 * read the file as text to a String List and return the List<String> Object.
	 * if the file is too large, it will throw ComponentException.
	 * @Title: readAllLines 
	 * @Description: 
	 * @param readFile
	 * @param CharsetStr
	 * @return
	 * @throws IOException
	 * List<String>
	 * @throws
	 */
	public static List<String> readAllLines(File readFile, String CharsetStr) throws IOException
	{
		if (readFile.length() > MaxMemCacheBlock)
		{
			throw new IOException("the File is too large (large than 100MB), it should be not read once-off in this way.");
		}
		else
		{
			List<String> cacheLines=new ArrayList<String>();
			FileInputStream fis=new FileInputStream(readFile);
			InputStreamReader is=new InputStreamReader(fis, CharsetStr);
			BufferedReader reader = new BufferedReader(is);
			String Line = null;
			while ((Line = reader.readLine()) != null)
			{
				cacheLines.add(Line);
			}
			reader.close();
			is.close();
			fis.close();
			return cacheLines;
		}
	}
	
	
	/**
	 * write string to the filepath as a text file.
	 * 
	 * @author JiahaiWu
	 * @param str
	 * @param filePath
	 * @param append
	 * @param charset
	 * @return
	 * @throws ComponentException
	 */
	public static boolean writeToFile(String str, String filePath, boolean append, String charset) throws IOException
	{
		try
		{
			BufferedWriter outer = fileBufferedWriter(filePath, charset, append);
			outer.write(str);
			//outer.newLine();
			outer.flush();
			outer.close();
		}
		catch (IOException e)
		{
			return false;
		}
		return true;
	}

	/**
	 * write string list to the filepath as a text file.
	 * 
	 * @author JiahaiWu
	 * @param strList
	 * @param filePath
	 * @param append
	 * @param charset
	 * @return
	 * @throws ComponentException
	 */
	public static boolean writeToFile(List<String> strList, String filePath, boolean append, String charset) throws IOException
	{
		try
		{
			BufferedWriter outer = fileBufferedWriter(filePath, charset, append);
			for (String str : strList)
			{
				outer.write(str);
				outer.newLine();
			}
			outer.flush();
			outer.close();
		}
		catch (IOException e)
		{
			return false;
		}
		return true;
	}
	
	/**
	 * get InpuStream Info and write into the filepath as a file.
	 * @author JiahaiWu
	 * @param is
	 * @param filePath
	 * @return
	 * @throws IOException
	 * @throws ComponentException 
	 */
	public static boolean writeToFile(InputStream is, String filePath) throws IOException
	{
		boolean result = false;
		File readFile = new File(filePath);
		FileOutputStream fos =new FileOutputStream(readFile);
		byte[] data = new byte[1024];
		int size = 0;
		size = is.read(data);
		while (size > 0)
		{
			fos.write(data, 0, size);
			size = is.read(data);
		}
		fos.flush();
		fos.close();
		result = true;
		return result;
	}
	
	/**
	 * 将指定的文件移至目标文件夹下
	 * @author JiahaiWu
	 * @param sourceFile
	 * @param tagertFolder
	 * @return
	 * @throws ComponentException 
	 */
	public static boolean moveFile(File sourceFile,File tagertFolder) throws IOException
	{
		if(!tagertFolder.exists())
			tagertFolder.mkdirs();
		if(tagertFolder.isDirectory())
		{
			File dest = new File(tagertFolder,sourceFile.getName());
			return sourceFile.renameTo(dest);
		}
		else
			throw new IOException("the target should be a Folder!");
	}
	
	/**
	 *  将指定的文件移至目标文件夹下
	 * @param source
	 * @param tagert
	 * @return
	 * @throws ComponentException
	 */
	public static boolean moveFile(String source,String tagert) throws IOException
	{
		File sourceFile=new File(source);
		File tagertFolder = newFolder(tagert);
		if(sourceFile.exists())
			return moveFile(sourceFile,tagertFolder);
		else
			throw new IOException("the source filePath should be existed file!");
	}
	
	/**
	 * 删除指定的文件，如果是文件夹则子文件一并删除。
	 * @note 使用该方法前，请确认当前文件全部不再需要！
	 * @author JiahaiWu
	 * @param file
	 */
	public static boolean deleteFile(File file)
	{
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
		}
		if(file.isDirectory())
		{
			File[] subfiles = file.listFiles();
			for(File subfile:subfiles)
				deleteFile(subfile);
		}
		return file.delete();
	}

	/**
	 * 文件内容拷贝
	 * @param source
	 * @param dest
	 * @return
	 * @throws IOException 
	 */
	public static boolean copyFile(File sourceFile,File destFile) throws IOException
	{
		BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(destFile));
            // 缓冲数组
            byte[] b = new byte[1024 * 4];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
        return true;
	}
	
	public static boolean copyDirectory(String sourceDir,String destDir) throws IOException
	{
		 // 新建目标目录
        (new File(destDir)).mkdirs();
        // 获取源文件夹当前下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 源文件
                File sourceFile = file[i];
                // 目标文件
                File targetFile = new File(new File(destDir).getAbsolutePath() + File.separator + file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            if (file[i].isDirectory()) {
                // 准备复制的源文件夹
                String dir1 = sourceDir + "/" + file[i].getName();
                // 准备复制的目标文件夹
                String dir2 = destDir + "/" + file[i].getName();
                copyDirectory(dir1, dir2);
            }
        }
        return true;
	}
	public final static long MaxMemCacheBlock=104857600L;
}
