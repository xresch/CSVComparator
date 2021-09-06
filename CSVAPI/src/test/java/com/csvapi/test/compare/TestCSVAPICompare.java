package com.csvapi.test.compare;

import java.io.File;

import org.junit.Test;

import com.csvapi.arguments.ArgumentsException;
import com.csvapi.compare.CSVAPICompare;
import com.csvapi.compare.CompareArguments;
import com.csvapi.compare.CompareArgumentsBuilder;
import com.csvapi.compare.CompareDefinition;
import com.csvapi.test.global.AssertUtilities;

public class TestCSVAPICompare {
	
	/**********************************************************
	 * Test comparison of one file with quotes against one file
	 * without quotes.
	 * @throws ArgumentsException 
	 **********************************************************/
	@Test
	public void testCompareQuotedFile() throws ArgumentsException {

		String olderFile = "./testdata/fruits_older_quotes.csv";
		String youngerFile = "./testdata/fruits_younger.csv";
		String resultFilePath = "./result/testCompareQuotedFile.csv";
		
		CompareArguments arguments = new CompareArgumentsBuilder()
								.setOlderFile(olderFile)
								.setOlderLabel("olderLabel")
								.setOlderDelimiter(",")
								.setOlderQuotes(true)
								.setYoungerFile(youngerFile)
								.setYoungerLabel("youngerLabel")
								.setYoungerDelimiter(",")
								.setYoungerQuotes(false)
								.setResultCompareDiff(false)
								.setResultCompareDiffPercentage(true)
								.setResultCompareString(true)
								.setResultDelimiter(";")
								.setResultFile(resultFilePath)
								.setResultSort(false)
								.setResultStdout(false)
								.setColumnIdentifier("Fruit,Fruit")
								.setColumnIdentifierMakeUnique(true)
								.setLogLevelConsole("DEBUG")
								.setLogLevelFile("INFO")
								.build();
		
		CSVAPICompare.compareCSVWriteResult(arguments);
	
		AssertUtilities.assertFileLineByLine(resultFilePath, new String[]{
				"Fruit;Count(olderLabel);Count(youngerLabel);Count Diff%;Count EQ?",
				"Orange;20;-;-;-",
				"Lemon;-;16;-;-",
				"Apple [1];10;8;-0.19999999999999996;NOTEQ",
				"Apple [2];12;-;-;-",
				"Pear;7;17;1.4285714285714284;NOTEQ",
				"Banana [1];5;5;0.0;EQ",
				"Banana [2];-;8008;-;-"
		});
		
		new File(resultFilePath).delete();
		
	}
	
	/**********************************************************
	 * Test comparison with Top Level.
	 **********************************************************/
	@Test
	public void testTopLevelComparison() throws ArgumentsException {

		String olderFile = "./testdata/fruits_toplevel_older.csv";
		String youngerFile = "./testdata/fruits_toplevel_younger.csv";
		String resultFilePath = "./result/testTopLevelComparison.csv";
		
		CompareArguments arguments = new CompareArgumentsBuilder()
								.setOlderFile(olderFile)
								.setOlderLabel("older")
								.setOlderDelimiter(";")
								.setYoungerFile(youngerFile)
								.setYoungerLabel("younger")
								.setYoungerDelimiter(";")
								.setResultCompareDiffPercentage(true)
								.setResultDelimiter(";")
								.setResultFile(resultFilePath)
								.setResultSort(false)
								.setColumnIdentifier("Description","Description")
								.setColumnIdentifierMakeUnique(true)
								.setColumnTopLevel("Level", "Level", "Top")
								.addColumnCompareDefinition("Level,Level,printMerged()")
								.addColumnCompareDefinition("Description,Description,printMerged()")
								.addColumnCompareDefinition("Count,Count,printOlder(),printYounger(),compareDifference%()")
								.setLogLevelConsole("DEBUG")
								.build();
				
		CSVAPICompare.compareCSVWriteResult(arguments);
	
		AssertUtilities.assertFileLineByLine(resultFilePath, new String[]{
				"Level;Description;Count(older);Count(younger);Count Diff%",
				"Top;Warehouse A;54;92;0.7037037037037037",
				"Sub;Orange;20;5;-0.75",
				"Sub;Apple [1];10;77;6.7",
				"Sub;Apple [2];12;-;-",
				"Sub;Pear;7;2;-0.7142857142857143",
				"Sub;Banana;5;8;0.6000000000000001",
				"Top;Warehouse B;62;62;0.0",
				"Sub;Orange;15;17;0.1333333333333333",
				"Sub;Apple;14;12;-0.1428571428571429",
				"Sub;PassionFruit;16;18;0.125",
				"Sub;Pear;7;9;0.2857142857142858",
				"Sub;Banana;10;6;-0.4",
				"Top;Warehouse C;16157;13646;-0.155412514699511",
				"Sub;Plum;0;-;-",
				"Sub;Melon;-;0;-",
				"Sub;Durian;2;2;0.0",
				"Sub;Cranberry;10000;12345;0.23449999999999993",
				"Sub;Strawberry;600;300;-0.5",
				"Sub;Blackberry;5555;999;-0.8201620162016201",
				"Top;Warehouse D;-;238;-",
				"Sub;PassionFruit;-;16;-",
				"Sub;Mango;-;2;-",
				"Sub;Grapes;-;220;-"
		});
		
		new File(resultFilePath).delete();
		
	}
}
