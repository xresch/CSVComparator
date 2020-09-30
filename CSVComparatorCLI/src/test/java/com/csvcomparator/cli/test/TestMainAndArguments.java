package com.csvcomparator.cli.test;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.csvapi.test.global.AssertUtilities;
import com.csvcomparator.cli.main.CSVComparatorCLI;

public class TestMainAndArguments {

	/**********************************************************
	 * test main default settings
	 * --------------------------
	 * - output should be sorted
	 * - identifiers should be made unique
	 * - delimiter is',' in both files and in result
	 * - default labels are applied in header (younger / older)
	 **********************************************************/
	@Test
	public void testMainDefaultArguments() {

		String olderFile = "./testdata/fruits_older.csv";
		String youngerFile = "./testdata/fruits_younger.csv";
		String resultFilePath = "./result/testMainDefaultArguments.csv";
		
		CSVComparatorCLI.main( new String[] {	"-result.file=" + resultFilePath,
								 	"-older.file=" + olderFile,
								 	"-younger.file=" + youngerFile} );
	
		AssertUtilities.assertFileLineByLine(resultFilePath, new String[]{
				"Fruit,Count(older),Count(younger),Count Diff%",
				"Apple [1],10,8,-0.19999999999999996"
		});
		
		new File(resultFilePath).delete();
		
	}
	
	/**********************************************************
	 * Test behavior in case wrong arguments are passed to the
	 * application.
	 * 
	 **********************************************************/
	@Test
	public void testMainUnsupportedArguments() {

		String olderFile = "./testdata/fruits_older.csv";
		String youngerFile = "./testdata/fruits_younger.csv";
		String resultFile = "./result/testMainUnsupportedArguments.csv";
		
		CSVComparatorCLI.main( new String[] {	"-result.file=" + resultFile,
								 	"-older.file=" + olderFile,
								 	"-younger.file=" + youngerFile,
								 	"-unsupportedArgument=true",
								 	"-aWrongInput=true",
								 	"-strangeCommandline=false",
								 	} );
		
		File file = new File(resultFile);
		
		Assert.assertTrue("Result file should not be created", !file.isFile());
		
		//TODO: Check system.out

		file.delete();
		
	}
	
	/**********************************************************
	 * Test if sort can be disabled
	 **********************************************************/
	@Test
	public void testMainDoNotSort() {

		String olderFile = "./testdata/fruits_older.csv";
		String youngerFile = "./testdata/fruits_younger.csv";
		String resultFilePath = "./result/testMainDoNotSort.csv";
		
		CSVComparatorCLI.main( new String[] {	"-result.file=" + resultFilePath,
								 	"-older.file=" + olderFile,
								 	"-younger.file=" + youngerFile,
								 	"-result.sort=false"} );
		
		AssertUtilities.assertFileLineByLine(resultFilePath, new String[]{
				"Fruit,Count(older),Count(younger),Count Diff%",
				"Orange,20,-,-",
				"Lemon,-,16,-",
				"Apple [1],10,8,-0.19999999999999996"
		});
		
		new File(resultFilePath).delete();
	}
	/**********************************************************
	 * Test if delimiter of all files(older, younger, result) 
	 * can be changed.
	 **********************************************************/
	@Test
	public void testMainSemicolonDelimiter() {

		String olderFile = "./testdata/fruits_older_semicolon.csv";
		String youngerFile = "./testdata/fruits_younger_semicolon.csv";
		String resultFilePath = "./result/testMainSemicolonDelimiter.csv";

		CSVComparatorCLI.main( new String[] {	"-result.file=" + resultFilePath,
								 	"-older.file=" + olderFile,
								 	"-younger.file=" + youngerFile,
								 	"-younger.delimiter=;",
								 	"-older.delimiter=;",
								 	"-result.delimiter=;"
								 	} );
		
		AssertUtilities.assertFileLineByLine(resultFilePath, new String[]{
				"Fruit;Count(older);Count(younger);Count Diff%",
				"Apple [1];10;8;-0.19999999999999996",
				"Apple [2];12;-;-"
		});

		new File(resultFilePath).delete();
	}
	
	/**********************************************************
	 * Test if identifierColumn can be changed.
	 * Identifier column will stay at the same location in the
	 * result, at the end of the table 
	 **********************************************************/
	@Test
	public void testMainIdentifierColumn() {

		String olderFile = "./testdata/fruits_identifierColumn_older.csv";
		String youngerFile = "./testdata/fruits_identifierColumn_younger.csv";
		String resultFilePath = "./result/testMainIdentifierColumn.csv";

		CSVComparatorCLI.main( new String[] {	"-result.file=" + resultFilePath,
								 	"-older.file=" + olderFile,
								 	"-younger.file=" + youngerFile,
								 	"-column.identifier=Fruit,SweetFood"
								 	} );
	
		AssertUtilities.assertFileLineByLine(resultFilePath, new String[]{
				"Count(older),Count(younger),Count Diff%,SweetFood",
				"10,8,-0.19999999999999996,Apple",
		});
		
		new File(resultFilePath).delete();
		
	}
	
	/**********************************************************
	 * Test if columnDefinition print methods are working 
	 * correctly.
	 **********************************************************/
	@Test
	public void testMainColumnDefPrintMethods() {

		String olderFile = "./testdata/fruits_identifierColumn_older.csv";
		String youngerFile = "./testdata/fruits_identifierColumn_younger.csv";
		String resultFilePath = "./result/testMainColumnDefPrintMethods.csv";
		
		CSVComparatorCLI.main( new String[] {	"-result.file=" + resultFilePath,
								 	"-older.file=" + olderFile,
								 	"-younger.file=" + youngerFile,
								 	"-column.identifier=Fruit,SweetFood",
								 	"-column.compareDef=Count,Count,printOlder(),printYounger(),printSeparator(),printMerged()"
								 	} );
		
		AssertUtilities.assertFileLineByLine(resultFilePath, new String[]{
				"Fruit,Count(older),Count(younger), ,Count",
				"Apple,10,8, ,10/8",
				"Banana,5,5, ,5"
		});
		
		new File(resultFilePath).delete();
	}
	
	/**********************************************************
	 * Test if columnDefinition compare methods are working 
	 * correctly.
	 **********************************************************/
	@Test
	public void testMainColumnDefCompareMethods() {

		String olderFile = "./testdata/fruits_identifierColumn_older.csv";
		String youngerFile = "./testdata/fruits_identifierColumn_younger.csv";
		String resultFilePath = "./result/testMainColumnDefCompareMethods.csv";
		
		CSVComparatorCLI.main( new String[] {	"-result.file=" + resultFilePath,
								 	"-older.file=" + olderFile,
								 	"-younger.file=" + youngerFile,
								 	"-column.identifier=Fruit,SweetFood",
								 	"-column.compareDef=Count,Count,compareDifference%(),compareDifference(),compareAsStrings()"
								 	} );
		
		
		AssertUtilities.assertFileLineByLine(resultFilePath, new String[]{
				"Fruit,Count Diff%,Count Diff,Count EQ?",
				"Apple,-0.19999999999999996,-2.0,NOTEQ",
				"Banana,0.0,0.0,EQ"
		});
		
		new File(resultFilePath).delete();
	}
	
	/**********************************************************
	 * Test if columnDefinition divide methods are working 
	 * correctly.
	 **********************************************************/
	@Test
	public void testMainColumnDefDivideMethods() {

		String olderFile = "./testdata/fruits_identifierColumn_older.csv";
		String youngerFile = "./testdata/fruits_identifierColumn_younger.csv";
		String resultFilePath = "./result/testMainColumnDefDivideMethods.csv";
		
		CSVComparatorCLI.main( new String[] {	"-result.file=" + resultFilePath,
								 	"-older.file=" + olderFile,
								 	"-younger.file=" + youngerFile,
								 	"-column.identifier=Fruit,SweetFood",
								 	"-column.compareDef=Count,Count,divideYoungerBy(2),divideOlderBy(2),printYounger(),printOlder()"
								 	} );
	
		AssertUtilities.assertFileLineByLine(resultFilePath, new String[]{
				"Fruit,Count(younger),Count(older)",
				"Apple,4.0,5.0"
		});
		
		new File(resultFilePath).delete();
		
	}
	
	/**********************************************************
	 * Test if columnDefinition multiply methods are working 
	 * correctly.
	 **********************************************************/
	@Test
	public void testMainColumnDefMultiplyMethods() {

		String olderFile = "./testdata/fruits_identifierColumn_older.csv";
		String youngerFile = "./testdata/fruits_identifierColumn_younger.csv";
		String resultFilePath = "./result/testMainColumnDefMultiplyMethods.csv";
		
		CSVComparatorCLI.main( new String[] {	"-result.file=" + resultFilePath,
								 	"-older.file=" + olderFile,
								 	"-younger.file=" + youngerFile,
								 	"-column.identifier=Fruit,SweetFood",
								 	"-column.compareDef=Count,Count,multiplyYoungerBy(2),multiplyOlderBy(2),printYounger(),printOlder()"
								 	} );
		
		AssertUtilities.assertFileLineByLine(resultFilePath, new String[]{
				"Fruit,Count(younger),Count(older)",
				"Apple,16.0,20.0"
		});
		
		new File(resultFilePath).delete();

	}
	
	/**********************************************************
	 * Test comparison of one file with quotes against one file
	 * without quotes.
	 **********************************************************/
	@Test
	public void testMainCompareQuotedFile() {

		String olderFile = "./testdata/fruits_older_quotes.csv";
		String youngerFile = "./testdata/fruits_younger.csv";
		String resultFilePath = "./result/testMainCompareQuotedFile.csv";
		
		CSVComparatorCLI.main( new String[] {	"-result.file=" + resultFilePath,
								 	"-older.file=" + olderFile,
								 	"-older.quotes=true",
								 	"-younger.file=" + youngerFile} );
	
		AssertUtilities.assertFileLineByLine(resultFilePath, new String[]{
				"Fruit,Count(older),Count(younger),Count Diff%",
				"Apple [1],10,8,-0.19999999999999996",
				"Apple [2],12,-,-",
				"Banana [1],5,5,0.0",
				"Banana [2],-,8008,-",
				"Lemon,-,16,-",
				"Orange,20,-,-",
				"Pear,7,17,1.4285714285714284"
		});
		
		new File(resultFilePath).delete();
		
	}
	
}
