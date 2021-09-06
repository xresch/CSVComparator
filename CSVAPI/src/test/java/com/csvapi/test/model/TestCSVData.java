package com.csvapi.test.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.csvapi.model.CSVData;
import com.csvapi.test.global.AssertUtilities;
import com.csvapi.test.global.ConstantsForTests;

public class TestCSVData {

	@Before
	@After
	public void printSeperator(){
		System.out.println("======= test Separator ========");
	}
	
	@Test
	public void testReadCSVData() {
		CSVData data = new CSVData("Test");
		data.readCSVData(new File("./testdata/fruits_older.csv"), ",", false);
		
		Assert.assertEquals("Last line is readed", "Banana", data.getRow(4).get(0) );

	}

	@Test
	public void testReadCSVDataWrongFormat() {
		CSVData data =  new CSVData("Test");
		data.readCSVData(new File("./testdata/fruits_younger_wrongFormat.csv"), ",", false);
		
		Assert.assertEquals("Dash is added as placeholder", "-", data.getRow(1).get(1) );
		Assert.assertEquals("Last line is readed", "Banana", data.getRow(4).get(0) );
	}
	
	@Test
	public void testWriteCSVData() {
		
		String resultFilepath = "./result/testWriteCSVData.csv";
		
		try {
			CSVData  data = ConstantsForTests.getTestCSVData();
			
			data.writeCSVData(resultFilepath, ";", false);
			
			
		} catch (IOException e1) {
			Assert.fail("IO Exception was thrown."+ e1.getMessage());
		}
		
		File file = new File(resultFilepath);
		
		Assert.assertTrue("File is created", file.isFile());
		
		AssertUtilities.assertFileLineByLine(resultFilepath, 
				new String[]{
				"Name;Points;Ranking",
				"Giorgi;12345;1",
				"Reto;10000;2",
				"Marc;899;4",
				"Alessandra;9999;3"
		});
		
		new File(resultFilepath).delete();

	}
	
	@Test
	public void testWriteCSVDataWithQuotes() {
		
		String resultFilepath = "./result/testWriteCSVDataWithQuotes.csv";
		
		try {
			CSVData  data = ConstantsForTests.getTestCSVData();
			
			data.writeCSVData(resultFilepath, ";", true);
			
			
		} catch (IOException e1) {
			Assert.fail("IO Exception was thrown."+ e1.getMessage());
		}
		
		File file = new File(resultFilepath);
		
		Assert.assertTrue("File is created", file.isFile());
		
		AssertUtilities.assertFileLineByLine(resultFilepath, 
				new String[]{
				"\"Name\";\"Points\";\"Ranking\"",
				"\"Giorgi\";\"12345\";\"1\"",
				"\"Reto\";\"10000\";\"2\"",
				"\"Marc\";\"899\";\"4\"",
				"\"Alessandra\";\"9999\";\"3\""
		});
		
		new File(resultFilepath).delete();

	}
	
	@Test
	public void testReadCSVDataQuotation() throws IOException {
		
		CSVData data =  new CSVData("Test");
		String resultFilepath = "./result/testReadCSVDataQuotation.txt";

		data.readCSVData(new File("./testdata/extreme_quote_test.csv"), ",", true);
		data.writeCSVData(resultFilepath, "#", false);
		
		AssertUtilities.assertFileLineByLine(resultFilepath, 
				new String[]{
				"Fruit#Count#Description",
				"Orange#20#first quoted",
				"Apple#10#middle quoted",
				"Apple#10#last quoted",
				"Apple#12#all quoted",
				",PearTest#7#delimiter in quotes start",
				"Pear,Test#7#delimiter in quotes middle",
				"PearTest,#7#delimiter in quotes end",
				",Pear,Test,#7#delimiter in quotes combo",
				"\\\"Banana#5#escaped quote start",
				"Ban\\\"ana#5#escaped quote middle",
				"Banana\\\"#5#escaped quote end",
				"\\\"Ban\\\"ana\\\"#5#escaped quote combo",
				"\\\\Banana#6#escaped escape start",
				"Ban\\\\ana#6#escaped escape middle",
				"Banana\\\\#6#escaped escape end",
				"\\\\Ban\\\\ana\\\\#6#escaped escape combo",
				",,,,,,,,,#,,,,,,#,,,,,,combination, many delimiter test",
				"Ban,ana#8,5#combination, comma and text",
				"Ban,\\\"ana\\\\#8,5\\\\#combination, escaped quote and backslash ",
				"Ban,\\\"ana\\\\#\\9\\#combination, comma followed by escaped quote",
				"Ban,\\\"\\\"ana\\\"\\\"\\\"\\\\#\\9\\#combination, many escaped quotes",
				"Ban\\\"a\\\\na,\\\"9\\\",combination many not escaped quotes \\\"\\\"\\\"\\\"\\#-#-"
		});
		
		new File(resultFilepath).delete();
	
	}

}
