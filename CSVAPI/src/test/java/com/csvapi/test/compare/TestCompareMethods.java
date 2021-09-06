package com.csvapi.test.compare;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.csvapi.compare.CompareArguments;
import com.csvapi.compare.CompareMethods;
import com.csvapi.model.CSVData;

public class TestCompareMethods {

	@Test
	public void testCompare2CSVFiles() {
		CSVData olderCSVData = new CSVData("old");
		olderCSVData.readCSVData(new File("./testdata/fruits_older.csv"), ",", false);
		
		CSVData youngerCSVData = new CSVData("young");
		youngerCSVData.readCSVData(new File("./testdata/fruits_younger.csv"), ",",false);
		
		CSVData result = CompareMethods.compare2CSVFiles(new CompareArguments(), olderCSVData, youngerCSVData);
		
		Assert.assertEquals("output is correct(header)", "Fruit;Count(old);Count(young);Count Diff%", result.getHeaders(";", false) );
		Assert.assertEquals("output is correct(row - 01)", "Apple [1];10;8;-0.19999999999999996", result.getRow(0,";", false) );
		Assert.assertEquals("output is correct(row - 02)", "Apple [2];12;-;-", result.getRow(1,";", false) );
		Assert.assertEquals("output is correct(row - 03)", "Banana [1];5;5;0.0", result.getRow(2,";", false) );
		Assert.assertEquals("output is correct(row - 04)", "Banana [2];-;8008;-", result.getRow(3,";", false) );
		Assert.assertEquals("output is correct(row - 05)", "Lemon;-;16;-", result.getRow(4,";", false) );
		Assert.assertEquals("output is correct(row - 06)", "Orange;20;-;-", result.getRow(5,";", false) );
		Assert.assertEquals("output is correct(row - 07)", "Pear;7;17;1.4285714285714284", result.getRow(6,";", false) );
		Assert.assertEquals("output has correct size", 7, result.getSize());
	}

	@Test
	public void testCalcDiffAsDouble() {
		
		try {
			Assert.assertEquals(-1.2, CompareMethods.calcDiffAsDouble("3.7", "2.5"), 0.0000001);
		} catch (Exception e) {
			Assert.fail("Exception in testCalcDiffAsDouble()");
			e.printStackTrace();
		}
	}

	@Test
	public void testCalcDiffPercentageAsDouble() {
		try {
			Assert.assertEquals(-0.745, CompareMethods.calcDiffPercentageAsDouble("100", "25.5"), 0.0000001);
			Assert.assertEquals(2.0, CompareMethods.calcDiffPercentageAsDouble("100", "300"), 0.0000001);
		} catch (Exception e) {
			Assert.fail("Exception in testCalcDiffAsDouble()");
			e.printStackTrace();
		}
	}

	@Test
	public void testCompareAsString() {
		try {
			Assert.assertEquals("string compare equals", "EQ",  CompareMethods.compareAsString("equalStrings", "equalStrings"));
			Assert.assertEquals("string compare equals", "NOTEQ",  CompareMethods.compareAsString("String", "differentString"));
		} catch (Exception e) {
			Assert.fail("Exception in testCalcDiffAsDouble()");
			e.printStackTrace();
		}
	}

}
