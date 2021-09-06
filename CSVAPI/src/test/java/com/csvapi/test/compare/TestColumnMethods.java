package com.csvapi.test.compare;

import org.junit.Assert;
import org.junit.Test;

import com.csvapi.model.Column;
import com.csvapi.test.global.ConstantsForTests;
import com.csvapi.utils.CSVAPIUtils;

public class TestColumnMethods {

	@Test
	public void testMergeRemoveDuplicatesAndSort() {
		
		Column names1 = ConstantsForTests.getTestColumnWithUniqueNames();
		Column names2 = ConstantsForTests.getTestColumnWithDuplicateNames();
		
		Column result = CSVAPIUtils.mergeRemoveDuplicatesAndSort(names1, names2);
		
		Assert.assertEquals("6 unique names in result", 6, result.size());
		Assert.assertEquals("Result is sorted(check first)", "Alessandra", result.get(0));
		Assert.assertEquals("Result is sorted(check last)", "Zara", result.get(result.size()-1));
	}

	@Test
	public void testMergeRemoveDuplicatesAndMaintainOrder() {
		Column names1 = ConstantsForTests.getTestColumnWithUniqueNames();
		Column names2 = ConstantsForTests.getTestColumnWithDuplicateNames();
		
		Column result = CSVAPIUtils.mergeRemoveDuplicatesAndMaintainOrder(names1, names2);
		
		Assert.assertEquals("6 unique names in result", 6, result.size());
		Assert.assertEquals("Result is not sorted(check first)", "Giorgi", result.get(0));
		Assert.assertEquals("Result is not sorted(check last)", "Olivia", result.get(result.size()-1));
	}

	@Test
	public void testMergeValuesOf2ResultColumns() {
		
		Column firstColumn = new Column("Values(bla)");
		firstColumn.add("yes");
		firstColumn.add("-");
		firstColumn.add("yes");
		
		Column secondColumn = new Column("Points(blub)");
		secondColumn.add("-");
		secondColumn.add("maybe");
		secondColumn.add("no");
		
		Column result = CSVAPIUtils.mergeValuesOf2ResultColumns(firstColumn, secondColumn);
		
		Assert.assertEquals("is merged result 'yes'", "yes", result.get(0));
		Assert.assertEquals("is merged result 'maybe'", "maybe", result.get(1));
		Assert.assertEquals("is merged result 'yes/no'", "yes/no", result.get(2));
		
	}

	@Test
	public void testMakeValuesUniqueColumn() {
		Column names = ConstantsForTests.getTestColumnWithDuplicateNames();
		
		CSVAPIUtils.makeValuesUnique(names);

		Assert.assertEquals("6 unique names in result", 6, names.size());
		Assert.assertEquals("Numbers are added(check first)", "Giorgi [1]", names.get(0));
		Assert.assertEquals("Numbers are added(check last)", "Alessandra [2]", names.get(names.size()-1));
		
		int count;
		for(Object name : names){
			count = 0; 
			for(Object value : names){
				if(name.equals(value)){
					count++;
				}
			}
			if( count > 1){
				Assert.fail("Duplicated values found");
			}
		}
	}

	@Test
	public void testMakeValuesUniqueColumnColumn() {
		Column names1 = ConstantsForTests.getTestColumnWithUniqueNames();
		Column names2 = ConstantsForTests.getTestColumnWithDuplicateNames();
		
		CSVAPIUtils.makeValuesUnique(names1, names2);
		
		Assert.assertEquals("6 unique names in result", 6, names1.size());
		Assert.assertEquals("Numbers are added(check first)", "Giorgi [1]", names1.get(0));
		
		Assert.assertEquals("Numbers are added(check first)", "Giorgi [1]", names2.get(0));
		Assert.assertEquals("Numbers are added(check last)", "Alessandra [2]", names2.get(names2.size()-1));
		
		int count;
		
		for(Object name : names1){
			count = 0; 
			for(Object value : names1){
				if(name.equals(value)){
					count++;
				}
			}
			if( count > 1){
				Assert.fail("Duplicated values found(first column)");
			}
		}
		

		for(Object name : names2){
			count = 0; 
			for(Object value : names2){
				if(name.equals(value)){
					count++;
				}
			}
			if( count > 1){
				Assert.fail("Duplicated values found(second column)");
			}
		}
	}
	
	@Test
	public void testMultiplyColumnBy(){
		Column values = new Column("Values");
		values.add("1");
		values.add("2");
		values.add("3");
		
		try {
			CSVAPIUtils.multiplyColumnBy(values, "2.5");
			
			Assert.assertEquals("number is multiplied", 2.5, values.get(0));
			Assert.assertEquals("number is multiplied", 5.0, values.get(1));
			Assert.assertEquals("number is multiplied", 7.5, values.get(2));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	@Test
	public void testDivideColumnBy(){
		Column values = new Column("Values");
		values.add("1");
		values.add("2");
		values.add("3");
		
		try {
			CSVAPIUtils.divideColumnBy(values, "2");
			
			Assert.assertEquals("number is divided", 0.5, values.get(0));
			Assert.assertEquals("number is divided", 1.0, values.get(1));
			Assert.assertEquals("number is divided", 1.5, values.get(2));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

}
