package com.csvapi.test.global;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Assert;

public class AssertUtilities {

	/***********************************************************************************
	 * Asserts a file line by line.
	 * 
	 * @param filepath the path of the File
	 * @param expectedOutputLines the lines expected in the file
	 ***********************************************************************************/
	public static void assertFileLineByLine(String filepath, String[] expectedOutputLines){
		
		File file = new File(filepath);
		
		Assert.assertTrue("File is created", file.isFile());
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			for(String line : expectedOutputLines){
				Assert.assertEquals("Output Line matches expected", line, reader.readLine() );
			}
			
			reader.close();
		} catch (FileNotFoundException e) 	{ e.printStackTrace(); Assert.fail("File not Found");} 
		  catch (IOException e) 			{ e.printStackTrace(); Assert.fail("IO Exception");}
		
	}
	
	public static void assertCompareFilesLineByLine(String firstFilepath, String secondFilepath) throws IOException{
		
		
		File secondFile = new File(secondFilepath);
		
		BufferedReader reader = new BufferedReader(new FileReader(secondFile));
		
		String currentLine = "";
		
		ArrayList<String> lines = new ArrayList<>();
		while( (currentLine = reader.readLine()) != null){
			lines.add(currentLine);
		}
		
		AssertUtilities.assertFileLineByLine(firstFilepath, lines.toArray(new String[]{}));
		
		reader.close();
		
	}
}
