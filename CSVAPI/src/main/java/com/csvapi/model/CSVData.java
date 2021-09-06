package com.csvapi.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.csvapi.utils.CSVAPIUtils;

/*************************************************************************
 * This class is used to hold all the CSVData which is readed from a File.
 * It uses the class {@link Column} to store the values, also the class 
 * {@link Row} to give a possibility to get the data as rows.
 *  
 * @author Reto Scheiwiller, 2014
 * 
 *************************************************************************/

public class CSVData {
	
	public static final Logger logger = LogManager.getLogger(CSVData.class.getName());
	
	
	private ArrayList<Column> columnsArray;
	private String label;
	
	//####################################################################################
	// CONSTRUCTORS
	//####################################################################################
	public CSVData(String label){
		columnsArray = new ArrayList<Column>();
		this.label = label;
	}
	
	//####################################################################################
	// CLASS METHODS
	//####################################################################################
	
	/*************************************************************************
	 * Prints the CSVData to standard output.
	 * 
	 * @param delimiter delimiter used in the csv output.
	 *   
	 *************************************************************************/
	public void printCSVtoSTDOUT(String delimiter, boolean withQuotes){
		// Print the Headers
		System.out.println(this.getHeaders(delimiter, withQuotes));
		
		// Print the Rows
		int size = columnsArray.get(0).size();
		for(int i=0; i < size; i++){
			System.out.println(this.getRow(i, delimiter, withQuotes));
		}
		
	}
	
	/*************************************************************************
	 * Add columns to the CSVData by using a row containing headers.
	 * 
	 * @param headerRow the row containing the headers
	 *   
	 *************************************************************************/
	public void initializeColumnsByHeaders(Row headerRow){
		
		columnsArray = new ArrayList<Column>();
		
		for(Object header : headerRow){
			columnsArray.add(new Column(header.toString()));
		}
	}
	
	
	/*************************************************************************
	 * Static method for reading a csv file into an instance of CSVData.
	 * Quotation will not be handled.
	 *  
	 * @param csvFile a file containing csv data
	 * @param delimiter the string which is used in the csv file to delimit
	 *        the data
	 * 
	 *************************************************************************/
	public void readCSVData(File csvFile, String delimiter, boolean handleQuotes){
		
		logger.info("START - Read file: "+csvFile.getPath() ); long startTime = System.nanoTime();
		
		BufferedReader bf = null;
		try {
			bf = new BufferedReader(new FileReader(csvFile));
			
			//-----------------------------------
			// Read the Headers
			//-----------------------------------
			String headerRow = bf.readLine();
			String headers[];
			
			if(!handleQuotes){
				headers = headerRow.split(delimiter);
			}else{
				headers = CSVAPIUtils.splitHandleQuotesRegex(headerRow, delimiter);
			}
			
			ArrayList<Column> columnsArray = new ArrayList<Column>();
			
			for(int i=0; i<headers.length; i++){
				columnsArray.add(new Column(headers[i]));
			}
			
			//-----------------------------------
			// Read the Data
			//-----------------------------------
			boolean hasMoreLines = true;
			while(hasMoreLines){
			 
				String row = bf.readLine();
				
				// read till file end, skipping blank lines
				if(row != null && !row.trim().isEmpty()){
					
					//-----------------------------------
					// Delimit the values by the separator
					//-----------------------------------
					String values[];
					if(!handleQuotes){
						//-----------------------------------
						// Ignore Quotes
						values = row.split(delimiter);
					}else{
						//-----------------------------------
						// Handle Quotes
						values = CSVAPIUtils.splitHandleQuotesRegex(row, delimiter);
					}
					
					if(values.length != headers.length){
						logger.warn(" Row has not the same number of values than header line: "+row);
					}
					
					for(int i=0; i<headers.length; i++){
						
						if(i < values.length){
							columnsArray.get(i).add(values[i]);
						}else{
							columnsArray.get(i).add("-");
						}	
						
					}
				}else{
					if(row == null){
						hasMoreLines = false;
					}
				}
			}
			
			//-----------------------------------
			// Add columns to csvFile
			//-----------------------------------
			this.addColumns(columnsArray);
			
			
		} catch (FileNotFoundException e) {
			logger.error("File not found: "+csvFile.getAbsolutePath());
			
		} catch (IOException e) {
			logger.error("Error reading File: "+csvFile.getAbsolutePath());
			e.printStackTrace();
			
		} finally{
			if(bf != null){
				try {
					bf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			logger.info("END - Read File (Duration="+( (System.nanoTime()-startTime) / 1000000)+"ms)");
		}
		
		
	}

	/*************************************************************************
	 * Static method for writing an instance of CSVData to a file.
	 *  
	 * @param dataToWrite the CSVData to be written
	 * @param filePath the path of the result file
	 * @param delimiter the string which is used in the resulting csv file 
	 *        to delimit the data
	 * 
	 *************************************************************************/
	public void writeCSVData(String filePath, String delimiter, boolean withQuotes) throws IOException {
		
		logger.info("START - Write Result to path: "+filePath ); long startTime = System.nanoTime();
		
		File file = new File(filePath);
		
		File parentDir = new File(file.getParent());
		parentDir.mkdirs();
		file.delete();
		file.createNewFile();
		
		BufferedWriter bw = null;
		try{
			if(file.canWrite()){
				
				bw= new BufferedWriter(new FileWriter(file));
				
				// Write the Headers
				bw.write(this.getHeaders(delimiter, withQuotes)+"\n");
				
				// Write the Rows
				int size = this.getColumn(0).size();
				for(int i=0; i < size; i++){
					bw.write(this.getRow(i, delimiter, withQuotes)+"\n");
				}
				
			}else{
				logger.error("Writing is blocked on the file: " + file.getAbsolutePath());
			}
		}finally{
			if(bw != null){
				bw.flush();
				bw.close();
				logger.info("END - Write result (Duration="+( (System.nanoTime()-startTime) / 1000000)+"ms)");
			}
		}
	}
	
	
	/*************************************************************************
	 * Sorts the data by the column index.
	 *  
	 * @param columnIndex the index the CSVData should be sorted by.
	 * 
	 *************************************************************************/
	public void sortCSVData(int columnIndex){
		
		ArrayList<Row> rowList = this.getAllRows();
		Row[] rows = rowList.toArray(new Row[0]);
		
		Arrays.sort(rows, new RowComparator(columnIndex));
		
		this.clearContent(); 
		this.addRows(rows);
	}
	
	
	/*************************************************************************
	 * Removes all values from all columns, the columns with the headers will
	 * still exist after a call to this method.
	 * 
	 *************************************************************************/
	public void clearContent() {
		for(Column c : columnsArray){
			
			c.clear();
		}
	}
	
	//####################################################################################
	// GETTERS & SETTERS
	//####################################################################################

	public int getSize(){
		return columnsArray.get(0).size();
	}
	
	public String getHeaders(String delimiter, boolean withQuotes){
		
		if(delimiter.equals("\\t")){
			delimiter="	";
		}
		
		StringBuilder sb = new StringBuilder();
		for(int i=0; i < columnsArray.size();i++){
			
			if(withQuotes) sb.append("\"");
			
			sb.append(columnsArray.get(i).getHeader());
			
			if(withQuotes) sb.append("\"");
			
			if(i < columnsArray.size()-1){
				sb.append(delimiter);
			}
			
		}
		return sb.toString();
	}
	
	public Row getHeadersAsRow(){
		Row row = new Row();
		for(int i=0; i < columnsArray.size();i++){
			row.add(columnsArray.get(i).getHeader());
		}
		return row;
	}

	public void addStringsAsRow(String... values){
		
		for(int i=0; i < columnsArray.size();i++){
			
			//if there are not enough values add
			//a dash to the leftover columns
			if( i <= values.length){
				columnsArray.get(i).add(values[i]);
			}else{
				columnsArray.get(i).add("-");
			}
		}
	}
	
	public void addRow(Row row){
		for(int i=0; i < columnsArray.size();i++){
			
			//if the row has not enough values add
			//a dash to the leftover columns
			if( i <= row.size()){
				columnsArray.get(i).add(row.get(i));
			}else{
				columnsArray.get(i).add("-");
			}
		}
	}
	
	public void addRows(ArrayList<Row> rowsArray){
		this.addRows(rowsArray.toArray(new Row[0]));
	}
	
	public void addRows(Row[] rowsArray){
	
		for(Row row : rowsArray){
			for(int i=0; i < columnsArray.size();i++){
				
				//if the row has not enough values add
				//a dash to the leftover columns
				if( i < row.size()){
					columnsArray.get(i).add(row.get(i));
				}else{
					columnsArray.get(i).add("-");
				}
			}
		}
	}
	
	public String getRow(int index, String delimiter, boolean withQuotes){
		
		if(delimiter.equals("\\t")){
			delimiter="	";
		}
		
		
		StringBuilder sb = new StringBuilder();
		for(int i=0; i < columnsArray.size();i++){
			
			if(withQuotes) sb.append("\"");
			
			Column c = columnsArray.get(i);
			sb.append(c.get(index));
			
			if(withQuotes) sb.append("\"");
			
			if(i < columnsArray.size()-1){
				sb.append(delimiter);
			}
		}
		return sb.toString();
	}
	
	public Row getRow(int index){
		
		Row row = new Row();
		
		for(int i=0; i < columnsArray.size();i++){
			Column c = columnsArray.get(i);
			if(index < c.size()){
				row.add(c.get(index));
			}else{
				row.add("NULL");
			}	
		}
		return row;
	}
	
	public ArrayList<Row> getAllRows(){
		ArrayList<Row> rowsArray = new ArrayList<Row>();
		
		int size = columnsArray.get(0).size();
		for(int i=0; i < size; i++){
			rowsArray.add(this.getRow(i));
		}
		
		return rowsArray;
	}
	
	public Column getColumn(String header) {
		for(Column c : columnsArray){
			if(c.getHeader().trim().equals(header)){
				return c;
			}
		}
		return null;
	}
	
	public Column getColumn(int index) {
		if(index < columnsArray.size()){
			return columnsArray.get(index);
		}
		
		return null;
	}
	
	public ArrayList<Column> getColumns() {
		return columnsArray;
	}

	public void setColumns(ArrayList<Column> columns) {
		this.columnsArray = columns;
	}
	
	public void addColumn(Column c){
		this.columnsArray.add(c);
	}
	
	public void addColumns(ArrayList<Column> columnsArray){
		for(Column c : columnsArray){
			this.columnsArray.add(c);
		}
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String name) {
		this.label = name;
	}
	
	
	//####################################################################################
	// INNER CLASSES
	//####################################################################################
	
	private class RowComparator implements Comparator<Row>{
		
		int columnIndex;
		
		public RowComparator(int columnIndex){
			this.columnIndex = columnIndex;
		}
		
		@Override
		public int compare(Row r1, Row r2) {
			
			String firstString = (String)r1.get(columnIndex);
			String secondString = (String)r2.get(columnIndex);
			
			return firstString.compareTo(secondString);
		}
		
		
		
	}
	
	
}
