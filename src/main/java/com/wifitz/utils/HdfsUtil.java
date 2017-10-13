package com.wifitz.utils;

import java.io.InputStream;
import java.io.OutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;


public class HdfsUtil {
	
	private static Configuration configuration = new Configuration();
	private static Logger logger = Logger.getLogger(HdfsUtil.class);
	
   
	
	public static void updateToHDFS(String filePath, String resultStr) {
		System.out.println("************************"+filePath);
		OutputStream out = null;

		try 
		{
			FileSystem fs = FileSystem.get(configuration);
			Path path = new Path(filePath);
			out  = fs.create(path, true);	
			out.write(resultStr.getBytes());
		} 
		catch (Exception e) 
		{
			//put to log
			logger.error("write data " + resultStr + " to file " + filePath + " exception.", e);
		}
		finally
		{
			if(null != out)
			{
				try
				{
					out.close();
				}
				catch(Exception e)
				{
					//put to log
					logger.error("close file path " + filePath + " exception.", e);
				}
			}
		}
	}
	public static void appendToHDFS(String filePath, String resultStr) {
		System.out.println("path:"+filePath+"    string:"+resultStr);
		OutputStream out = null;

		try {
			FileSystem fs = FileSystem.get(configuration);
			Path path = new Path(filePath);

			if (fs.exists(path)) {
				out = fs.append(path);
			} else {
				out = fs.create(path);
			}

			out.write(resultStr.getBytes());
			out.write("\n".getBytes());
		} catch (Exception e) {
			// put to log

		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (Exception e) {
					// put to log

				}
			}
		}
	}
	
	public static String getFileContent(String filePath)
	{
		InputStream in = null;
		String fileDataStr = "";
		
		try
		{
			FileSystem fs = FileSystem.get(configuration);
			Path path = new Path(filePath);
			
			if(!fs.exists(path))
			{
				return fileDataStr;
			}
			
			in = fs.open(path);
			fileDataStr = StreamUtil.convertInputStream2String(in);
		} 
		catch (Exception e) 
		{
			//put to log
			logger.error("read file " + filePath + " exception.", e);
		}
		finally
		{
			if(null != in)
			{
				try
				{
					in.close();
				}
				catch(Exception e)
				{
					//put to log
					logger.error("close file path " + filePath + " exception.", e);
				}
				
			}
		}
		
		return fileDataStr;
	}
}
