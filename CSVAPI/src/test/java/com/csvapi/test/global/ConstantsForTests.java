package com.csvapi.test.global;

import com.csvapi.model.CSVData;
import com.csvapi.model.Column;

public class ConstantsForTests {
	
	public static CSVData getTestCSVData(){
		
		CSVData data = new CSVData("DataToWrite");
		
		Column firstColumn = new Column("Name");
		firstColumn.add("Giorgi");
		firstColumn.add("Reto");
		firstColumn.add("Marc");
		firstColumn.add("Alessandra");
		
		Column secondColumn = new Column("Points");
		secondColumn.add("12345");
		secondColumn.add("10000");
		secondColumn.add("899");
		secondColumn.add("9999");
		
		Column thirdColumn = new Column("Ranking");
		thirdColumn.add("1");
		thirdColumn.add("2");
		thirdColumn.add("4");
		thirdColumn.add("3");
		
		data.addColumn(firstColumn);
		data.addColumn(secondColumn);
		data.addColumn(thirdColumn);
		
		return data;
	}
	
	public static Column getTestColumnWithUniqueNames(){
		Column names = new Column("Name");
		names.add("Giorgi");
		names.add("Reto");
		names.add("Marc");
		names.add("Alessandra");
		names.add("Zara");
		names.add("Olivia");
		
		return names;
	}

	
	public static Column getTestColumnWithDuplicateNames(){
		Column names = new Column("Name");
		names.add("Giorgi");
		names.add("Reto");
		names.add("Marc");
		names.add("Alessandra");
		names.add("Giorgi");
		names.add("Alessandra");
		
		return names;
	}

}
