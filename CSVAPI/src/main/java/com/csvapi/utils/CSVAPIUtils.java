package com.csvapi.utils;

import java.util.Comparator;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.csvapi.model.Column;

/*************************************************************************
 * This class provides static methods to do modifications on one ore more 
 * columns.
 * 
 * @author Reto Scheiwiller, 2014
 * 
 *************************************************************************/

public class CSVAPIUtils {
	
	public static final Logger logger = LogManager.getLogger(CSVAPIUtils.class.getName());
	
	
	/***************************************************************
	 * Splits a line by a delimiter handling quotes.
	 * Delimiters inside quotes will be ignored.
	 * The values will have their surrounding quotes removed.
	 * 
	 * This method will not work correctly if there are any
	 * escaped quotes '\"' outside of quotation.
	 * 
	 * @param line the string to split
	 * @param delimiter the delimiter
	 * 
	 * @return String[] containing the splitted values
	 ***************************************************************/
	public static String[] splitHandleQuotesRegex(String line, String delimiter){
		//-----------------------------------
		// Handle Quotes
		// -------------
		// The regex searches for zero or an even number of not escaped quotes after the delimiter.
		// This will find all delimiters that are not in quotes. 
		// 
		// Quote Group: Search for zero or multiple doulbe Quotes
		//   (\\\\{2}|[^\\\\]\\\\\"|([^\\\\\"]|\\\\[^\"]))*?\" >> Allow either (\\, \") or (\, " but not \") after the delimiter till the next quote
		//   (\\\\{2}|[^\\\\]\\\\\"|[^\"])*?\") >> Allow (\\, \") in quotes.
		// After quote Group:
		//   [^\"]*$ >> Allow all signs except a quote
		// 
		//-----------------------------------
		String[] resultValues = line.split(delimiter+"(?=((\\\\{2}|[^\\\\]\\\\\"|([^\\\\\"]|\\\\[^\"]))*?\"(\\\\{2}|[^\\\\]\\\\\"|[^\"])*?\")*?[^\"]*$)");
		
		//remove quotes from values
		for(int i= 0; i < resultValues.length; i++){
			resultValues[i] =  resultValues[i].replaceAll("^\"|\"$", "");
		}
		
		return resultValues;
		
	}
	/***************************************************************
	 * Merge Columns and remove duplicated Values, decide if
	 * the result should be sorted.
	 * 
	 * @param olderColumn the Column of the older CSVData
	 * @param youngerColumn the Column of the younger CSVData
	 * 
	 * @return Column - merged column
	 ***************************************************************/
	public static Column merge2ColumnsNoDuplicates(Column olderColumn, Column youngerColumn, boolean doSort){
		
		if(doSort){
			return mergeRemoveDuplicatesAndSort(olderColumn, youngerColumn);
		}else{
			return mergeRemoveDuplicatesAndMaintainOrder(olderColumn, youngerColumn);
		}
	}
	
	/***************************************************************
	 * Merge Columns and remove duplicated Values, and sort 
	 * the values.
	 * 
	 * @param olderColumn the Column of the older CSVData
	 * @param youngerColumn the Column of the younger CSVData
	 * 
	 * @return Column merged and sorted column without duplicates
	 *  
	 ***************************************************************/
	public static Column mergeRemoveDuplicatesAndSort(Column olderColumn, Column youngerColumn){
		
		Column result = new Column("");
		
		Column merged = new Column(youngerColumn.getHeader()); 
		merged.addAll(olderColumn);
		merged.addAll(youngerColumn);
		
		//######################################
		// Custom Comparator
		//######################################
		Comparator<Object> comp = new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				String s1 = o1.toString();
				String s2 = o2.toString();
				return s1.compareTo(s2);
			}
		};
		
		//######################################
		// Remove Duplicates with Set
		//######################################
		TreeSet<Object> h = new TreeSet<Object>(comp);
		h.addAll(merged);
		
		result.addAll(h); 
		
		return result;
	}
	

	/***************************************************************
	 * Merge Columns and remove duplicated Values, while maintaining 
	 * the order of the values.
	 * 
	 * @param olderColumn the Column of the older CSVData
	 * @param youngerColumn the Column of the younger CSVData
	 * 
	 * @return Column merged Columns without duplicates, maintained
	 *         order
	 ***************************************************************/
	public static Column mergeRemoveDuplicatesAndMaintainOrder(Column olderColumn, Column youngerColumn){
		
		Column merged = new Column(olderColumn.getHeader());
		
		//######################################
		// Get size 
		//######################################
		int olderSize = olderColumn.size();
		int youngerSize = youngerColumn.size();
		
		int largerSize = 0;
		if(olderSize >= youngerSize){
			largerSize = olderSize;
		}else{
			largerSize = youngerSize;
		}
		
		//######################################
		// iterate over the Columns
		//######################################
		for(int i = 0; i < largerSize; i++){
			
			if(i < olderSize){
				String olderValue = olderColumn.get(i).toString(); 
				if(!merged.contains(olderValue)){
					merged.add(olderValue);
				}
			}
			
			if(i < youngerSize){
				String youngerValue = youngerColumn.get(i).toString(); 
				if(!merged.contains(youngerValue)){
					merged.add(youngerValue);
				}
			}
			
		}
		return merged;
	}
	

	/***************************************************************
	 * Merge the Values of 2 Columns. <br>
	 * If the value in one column is a dash "-*, the value of the
	 * other column is used. <br>
	 * If the value in both column is a dash, "-", a dash will be
	 * used in the merged column. <br>
	 * If both values are not a dash "-" they will be concatenated 
	 * with a slash "/" in between.
	 * 
	 * @param olderResultColumn the Column of the older CSVData
	 * @param youngerResultColumn the Column of the younger CSVData
	 * 
	 * @return Column - merged Column
	 ***************************************************************/
	public static Column mergeValuesOf2ResultColumns(Column olderResultColumn, Column youngerResultColumn){

		//######################################
		// initialize Result Column
		//######################################
		String header = youngerResultColumn.getHeader();
		String headerNoLabel = header.substring(0, header.lastIndexOf("("));
		
		Column resultColumn = new Column(headerNoLabel);
		
		//######################################
		// merge Columns
		//######################################
		for(int i=0; i < youngerResultColumn.size(); i++){
			String resultValue;
			
			String olderValue = olderResultColumn.get(i).toString();
			String youngerValue = youngerResultColumn.get(i).toString();

			if(olderValue.equals(youngerValue)){
				resultValue = olderValue;
			}else{
				if(olderValue.equals("-")){
					resultValue = youngerValue;
				}else if(youngerValue.equals("-")){
					resultValue = olderValue;
				}else{
					resultValue = olderValue+"/"+youngerValue;
				}
			}
		
			resultColumn.add(resultValue);
		}
		
		return resultColumn;
		
	}
	
	/***************************************************************
	 * Make values unique in one column, the result will be written
	 * directly to the column passed to this method. 
	 * For duplicated values a number like "[1]" will be appended.
	 * 
	 * @param column a column with or without duplicated values
	 ***************************************************************/
	public static void makeValuesUnique(Column column) {
			
		for(Object o : column){
			
			if(!(column.indexOf(o) == column.lastIndexOf(o))){
				
				int counter = 1;
				for(int i= 0; i < column.size(); i++){
					if(column.get(i).equals(o)){
						String value = column.get(i).toString();
						String numberedValue = value+" ["+counter+"]";
						
						column.set(i, numberedValue);
						counter++;
					}
				}
				
			}
		}
		
	}
	
	/***************************************************************
	 * Make values unique for considering two columns
	 * This will add a number in both columns, when one column 
	 * contains the same identifier twice. This will 
	 * prevent comparison failures when a identifier is used once
	 * in one file and multiple times in the other file. 
	 * The result is applied to the columns passed to the method.
	 * 
	 * @param olderColumn the Column of the older CSVData
	 * @param youngerColumn the Column of the younger CSVData
	 * 
	 ***************************************************************/
	public static void makeValuesUnique(Column olderColumn, Column youngerColumn) {
		
		//######################################################################################
		//iterate over the older Values
		//######################################################################################
		for(Object o : olderColumn){
			
			if(!(olderColumn.indexOf(o) == olderColumn.lastIndexOf(o))){
				
				//In older Column
				int counter = 1;
				for(int i= 0; i < olderColumn.size(); i++){
					if(olderColumn.get(i).equals(o)){
						String value = olderColumn.get(i).toString();
						String numberedValue = value+" ["+counter+"]";
						
						olderColumn.set(i, numberedValue);
						counter++;
					}
				}
		
				//In younger Column
				counter = 1;
				for(int i= 0; i < youngerColumn.size(); i++){
					if(youngerColumn.get(i).equals(o)){
						String value = youngerColumn.get(i).toString();
						String numberedValue = value+" ["+counter+"]";
						
						youngerColumn.set(i, numberedValue);
						counter++;
					}
				}
				
			}
		}
		
		//######################################################################################
		//iterate over the younger Values
		//######################################################################################
		for(Object o : youngerColumn){
			
			if(!(youngerColumn.indexOf(o) == youngerColumn.lastIndexOf(o))){
				
				//In younger Column
				int counter = 1;
				for(int i = 0; i < youngerColumn.size(); i++){
					if(youngerColumn.get(i).equals(o)){
						String value = youngerColumn.get(i).toString();
						String numberedValue = value+" ["+counter+"]";
						
						youngerColumn.set(i, numberedValue);
						counter++;
					}
				}
				
				//In older Column
				counter = 1;
				for(int i = 0; i < olderColumn.size(); i++){
					if(olderColumn.get(i).equals(o)){
						String value = olderColumn.get(i).toString();
						String numberedValue = value+" ["+counter+"]";
						
						olderColumn.set(i, numberedValue);
						counter++;
					}
				}
				
			}
		}
	}

	/***************************************************************
	 * Multiply the column values by the given number, the result 
	 * will be applied directly to the column passed to this method. 
	 * 
	 * @param columnToMultiply a column with numerical 
	 *        data which should be multiplied
	 * @param multiplikatorString a string representing a
	 *        double value
	 ***************************************************************/
	public static void multiplyColumnBy(Column columnToMultiply, String multiplikatorString) throws Exception{
				
		int size = columnToMultiply.size();
		for(int i=0 ; i < size ; i++){
			String value = columnToMultiply.get(i).toString();
			
			if(!value.equals("-")){
				try{
					Double d = Double.parseDouble(value);
					Double multiplikator = Double.parseDouble(multiplikatorString);
					
					columnToMultiply.set(i, d*multiplikator);
				}catch (NumberFormatException e) {
					throw new Exception("[ERROR] CompareMethods.divideColumnBy(): Could not parse as Double - ColumnValue: "+value+", Multiplikator:"+multiplikatorString);
				}
			}
		}
	}

	/***************************************************************
	 * Divide the column values by the given number, the result 
	 * will be applied directly to the column passed to this method. 
	 * 
	 * @param columnToDivide a column with numerical 
	 *        data which should be divided
	 * @param divisorString a string representing a
	 *        double value
	 ***************************************************************/
	public static void divideColumnBy(Column columnToDivide, String divisorString) throws Exception{
				
		int size = columnToDivide.size();
		for(int i=0 ; i < size ; i++){
			String value = columnToDivide.get(i).toString();
			
			if(!value.equals("-")){
				try{
					Double d = Double.parseDouble(value);
					Double divisor = Double.parseDouble(divisorString);
					
					columnToDivide.set(i, d/divisor);
				}catch (NumberFormatException e) {
					throw new Exception("[ERROR] CompareMethods.divideColumnBy(): Could not parse as Double - Divident: "+value+", Divisor:"+divisorString);
				}
			}
		}
	}

	
	
}
