package com.csvapi.compare;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.csvapi.model.CSVData;
import com.csvapi.model.Column;
import com.csvapi.model.HierarchicalCSVData;
import com.csvapi.model.Row;
import com.csvapi.utils.CSVAPIUtils;

/*************************************************************************
 * This class provides the methods which are used to compare two CSV-Files.
 * 
 * @author Reto Scheiwiller, 2014
 *************************************************************************/

public class CompareMethods {
	
	public static final Logger logger = LogManager.getLogger(CompareMethods.class.getName());
	
	/***********************************************************************************************
	 * This method will initialize the columns which are used as identifiers in the comparison.
	 * Then it will decide based on the given command line arguments how the files will be compared.
	 * 
	 * @param olderCSVData the object with the CSVData which should be considered as older in the 
	 *                       comparison
	 * @param youngerCSVData the object with the CSVData which should be considered as younger in 
	 *                       the comparison
	 ***********************************************************************************************/
	public static CSVData compare2CSVFiles(CompareArguments arguments, CSVData olderCSVData, CSVData youngerCSVData){
				
		//###################################################################################################
		// Initialize Variables
		//###################################################################################################
		String identifierColumnValue = arguments.getValue(CompareArguments.COLUMN_IDENTIFIER);
		String topLevel = arguments.getValue(CompareArguments.COLUMN_TOPLEVEL);
		String makeIdentifierUnique = arguments.getValue(CompareArguments.COLUMN_IDENTIFIER_MAKEUNIQUE);
		
		//###################################################################################################
		// Initialize identifier columns
		//###################################################################################################
		String identiferColumnNameOlder = null;
		String identiferColumnNameYounger = null;
		
		Column identifierColumnOlder;
		Column identifierColumnYounger;
		if(identifierColumnValue == null){
			identifierColumnOlder = olderCSVData.getColumn(0);
			identifierColumnYounger = youngerCSVData.getColumn(0);
		}else{
			int index = identifierColumnValue.indexOf(",");
			identiferColumnNameOlder = identifierColumnValue.substring(0, index);
			identiferColumnNameYounger = identifierColumnValue.substring(index+1);
			
			identifierColumnOlder = olderCSVData.getColumn(identiferColumnNameOlder);
			identifierColumnYounger = youngerCSVData.getColumn(identiferColumnNameYounger);
			
			logger.debug("CompareMethods.compare2CSVFiles(): " +
					"identiferColumnNameOlder: "+identiferColumnNameOlder+
					", identiferColumnNameYounger: "+identiferColumnNameYounger);
		}
								
		//###################################################################################################
		// Decide how to compare Compare
		//###################################################################################################
		
		CSVData compareResults;
		
		//compare only if the identifier Column could be found in both files
		if(identifierColumnOlder != null && identifierColumnYounger != null){
			
			if(topLevel == null){
				
				if(makeIdentifierUnique.toLowerCase().equals("true")){
					CSVAPIUtils.makeValuesUnique(identifierColumnOlder, identifierColumnYounger);
				}
				
				if(!arguments.hasColumnDefinitions()){
					compareResults = CompareMethods.doDefaultComparision(arguments, olderCSVData, youngerCSVData, identifierColumnOlder, identifierColumnYounger);
				}else{
					compareResults = CompareMethods.doComparisionByManualDefinition(arguments, olderCSVData, youngerCSVData, identifierColumnOlder, identifierColumnYounger);
				}
			}else{
				compareResults =CompareMethods.doComparisionWithTopLevel(arguments, olderCSVData, youngerCSVData, identifierColumnOlder, identifierColumnYounger);
			}
				return compareResults;
		}else{
			if(identifierColumnOlder == null)
				logger.error("CompareMethods.compare2CSVFiles(): The older identifierColumn could not be found: "+identiferColumnNameOlder);

			
			if(identifierColumnYounger == null)
				logger.error("CompareMethods.compare2CSVFiles(): The younger identifierColumn could not be found: "+identiferColumnNameYounger);
			
		}
			
		return null;

	}
	
	/***********************************************************************************************
	 * 
	 *  !!!!!!!!!!!!!! UNDER CONSTRUCTION !!!!!!!!!!!!!!!!!!!!<p>
	 * 
	 * This method is used when the arguments "-hierarchy" and "-hierarchyColumn" is provided.
	 * It will split the provided CSVData in parts, with the topLevel as a "delimiter". 
	 * This method considers also hierarchical relations between the rows in the data, and creates 
	 * subsets of CSVData in the parts.
	 * The parts are compared if in both files a topLevel with the same identifier is found.
	 * The result of all part comparisons is merged in one result file.
	 * 
	 * @param olderCSVData the object with the CSVData which should be considered as older in the 
	 *        comparison
	 * @param youngerCSVData the object with the CSVData which should be considered as younger in 
	 *        the comparison
	 * @param identifierColumnOlder the column of the older CSVData which is used to identify 
	 *        the record   
	 * @param identifierColumnYounger the column of the older CSVData which is used to identify 
	 *        the record                                                  
	 ***********************************************************************************************/
	private static CSVData doComparisionWithHierarchy(CompareArguments arguments, CSVData olderCSVData, CSVData youngerCSVData, Column identifierColumnOlder, Column identifierColumnYounger){
		
		//###################################################################################################
		// Initialize Variables
		//###################################################################################################
		
		//Initialize result variable
		CSVData compareResults = null;;
		
		//Initialize topLevel
		String hierarchyColumns = arguments.getValue("-hierarchyColumn");
		String[] splittedTopLevel = hierarchyColumns.split(",");
		String olderHierarchyName = splittedTopLevel[0];
		String youngerHierarchyName = splittedTopLevel[1];
		
		//Initialize hierarchy
		String hierarchy = arguments.getValue("-hierarchy");
		ArrayList<String> hierarchyArray = new ArrayList<String>();
		hierarchyArray.addAll(Arrays.asList(hierarchy.split(",")));
		
		String topLevelContent = hierarchyArray.get(0);
			
		//###################################################################################################
		// DEBUG
		//###################################################################################################
		logger.debug(	"olderTopLevelName: "+olderHierarchyName+
						", youngerTopLevelName: "+youngerHierarchyName+
						", topLevelContent: "+topLevelContent);
		
		for (int i=0 ; i < hierarchyArray.size(); i++){
			logger.debug("Hierarchy: Level-"+i+" is "+hierarchyArray.get(i));
		}

		
		//###################################################################################################
		// Initialize older TopLevels
		//###################################################################################################
		Column olderTopLevelColumn = olderCSVData.getColumn(olderHierarchyName);
		
		Map<String, CSVData> olderTopLevelParts = new TreeMap<String, CSVData>();
		
		HierarchicalCSVData olderAllLevels = new HierarchicalCSVData("olderHCVS");
		
		String olderLabel = arguments.getValue(CompareArguments.OLDER_LABEL);
		
		Row olderHeaders = olderCSVData.getHeadersAsRow();
		
		for(int i = 0; i < olderTopLevelColumn.size(); i++){
			if(olderTopLevelColumn.get(i).toString().contains(topLevelContent)){
				
				//save identifier for the Map
				String identifier = identifierColumnOlder.get(i).toString();
				
				//add the first row with contains the TopLevel
				HierarchicalCSVData topLevelMap = new HierarchicalCSVData(identifier, olderAllLevels);
				topLevelMap.initializeColumnsByHeaders(olderHeaders);
				topLevelMap.addRow(youngerCSVData.getRow(i));
				
				// Save current Values
				HierarchicalCSVData currentCSVData = topLevelMap; 
				int currentLevel = hierarchyArray.indexOf(olderTopLevelColumn.get(i).toString());
				
				//jump to next row and add all rows till the next topLevel
				i++;
				int nextLevel = hierarchyArray.indexOf(olderTopLevelColumn.get(i).toString());
				
				while(   i < olderTopLevelColumn.size()
					  && !olderTopLevelColumn.get(i).toString().contains(topLevelContent) ){
					
					
					if(currentLevel == nextLevel){
						Row row = currentCSVData.getRow(i);						
						topLevelMap.addRow(row);
					}else if(currentLevel > nextLevel){
						//add new hierarchy
					}else{
						//go one hierarchy back up
					}
					i++;
				}
				
				//revert to one row before, so we are back on the topLevel on the next loop
				i--;
				
				olderAllLevels.addCSVDataChild(topLevelMap);
			}
		}
		
		
		logger.debug("topLevels found in older: " + olderTopLevelParts.size());
		
		//###################################################################################################
		// Initialize younger TopLevels
		//###################################################################################################
		Column youngerTopLevelColumn = youngerCSVData.getColumn(youngerHierarchyName);
		
		Map<String, CSVData> youngerTopLevelParts = new  TreeMap<String, CSVData>();
		String youngerLabel = arguments.getValue(CompareArguments.YOUNGER_LABEL);
		
		Row youngerHeaders = youngerCSVData.getHeadersAsRow();
		
		for(int i = 0; i < youngerTopLevelColumn.size(); i++){
			if(youngerTopLevelColumn.get(i).toString().contains(topLevelContent)){
				
				//save identifier for the Map
				String identifier = identifierColumnYounger.get(i).toString();
								
				//add the first row with contains the TopLevel
				CSVData topLevelPart = new CSVData(youngerLabel);
				topLevelPart.initializeColumnsByHeaders(youngerHeaders);
				topLevelPart.addRow(youngerCSVData.getRow(i));
				
				//jump to next row and add all rows till the next topLevel
				i++;
				while(   i < youngerTopLevelColumn.size() 
					  && !youngerTopLevelColumn.get(i).toString().contains(topLevelContent) ){
					
					topLevelPart.addRow(youngerCSVData.getRow(i));
					i++;
				}
				//revert to one row before
				i--;
				
				youngerTopLevelParts.put(identifier, topLevelPart);
			}
		}
		
		
		logger.debug("topLevels found in younger: " + youngerTopLevelParts.size());
		
		//###################################################################################################
		// Compare TopLevelParts
		//###################################################################################################
		
		boolean isFirstComparision = true;
		for(String identifier : youngerTopLevelParts.keySet()){
			if(olderTopLevelParts.containsKey(identifier)){
				
				CSVData olderCSVDataPart = olderTopLevelParts.get(identifier);
				CSVData youngerCSVDataPart = youngerTopLevelParts.get(identifier);
				
				// Removing topLevel-Argument from ArgumentList
				// to not end in an endless loop
				arguments.getLoadedArguments().remove(CompareArguments.COLUMN_TOPLEVEL);
				
				CSVData partResult = CompareMethods.compare2CSVFiles(arguments, olderCSVDataPart, youngerCSVDataPart);
				
				if(isFirstComparision){
					isFirstComparision = false;
					compareResults = partResult;
				}else{
					compareResults.addRows(partResult.getAllRows());
				}
			}
				
		}
			
		return compareResults;
	}

	/***********************************************************************************************
	 * This method is used when the argument "-column.toplevel" is provided.
	 * It will split the provided CSVData in parts, with the topLevel as a "delimiter".
	 * The parts are compared if in both files a topLevel with the same identifier is found.
	 * The result of all part comparisons is merged in one result file.
	 * 
	 * @param olderCSVData the object with the CSVData which should be considered as older in the 
	 *        comparison
	 * @param youngerCSVData the object with the CSVData which should be considered as younger in 
	 *        the comparison
	 * @param identifierColumnOlder the column of the older CSVData which is used to identify 
	 *        the record   
	 * @param identifierColumnYounger the column of the older CSVData which is used to identify 
	 *        the record
	 ***********************************************************************************************/
	private static CSVData doComparisionWithTopLevel(CompareArguments arguments, CSVData olderCSVData, CSVData youngerCSVData, Column identifierColumnOlder, Column identifierColumnYounger){
		
		//###################################################################################################
		// Initialize Variables
		//###################################################################################################
		
		//Initialize result variable
		CSVData compareResults = null;;
		
		String topLevel = arguments.getValue(CompareArguments.COLUMN_TOPLEVEL);
		String[] splittedTopLevel = topLevel.split(",");
		String olderTopLevelName = splittedTopLevel[0];
		String youngerTopLevelName = splittedTopLevel[1];
		String topLevelContent = splittedTopLevel[2];
		
		// [DEBUG]
		logger.debug("olderTopLevelName: "+olderTopLevelName+
					", youngerTopLevelName: "+youngerTopLevelName+
					", topLevelContent: "+topLevelContent);
		
		//###################################################################################################
		// Initialize older TopLevels
		//###################################################################################################
		Column olderTopLevelColumn = olderCSVData.getColumn(olderTopLevelName);
		
		Map<String, CSVData> olderTopLevelParts = new  TreeMap<String, CSVData>();
		String olderLabel = arguments.getValue(CompareArguments.OLDER_LABEL);
		
		Row olderHeaders = olderCSVData.getHeadersAsRow();
		
		for(int i = 0; i < olderTopLevelColumn.size(); i++){
			if(olderTopLevelColumn.get(i).toString().contains(topLevelContent)){
				
				//save identifier for the Map
				String identifier = identifierColumnOlder.get(i).toString();
				
				//add the first row with contains the TopLevel
				CSVData topLevelPart = new CSVData(olderLabel);
				topLevelPart.initializeColumnsByHeaders(olderHeaders);
				topLevelPart.addRow(olderCSVData.getRow(i));
				
				//jump to next row and add all rows till the next topLevel
				i++;
				while(   i < olderTopLevelColumn.size() 
						&& !olderTopLevelColumn.get(i).toString().contains(topLevelContent) ){
					
					topLevelPart.addRow(olderCSVData.getRow(i));
					i++;
				}
				//revert to one row before
				i--;
				
				olderTopLevelParts.put(identifier, topLevelPart);
			}
		}
		
		logger.debug("top levels found in older: " + olderTopLevelParts.size());
		
		//###################################################################################################
		// Initialize younger TopLevels
		//###################################################################################################
		Column youngerTopLevelColumn = youngerCSVData.getColumn(youngerTopLevelName);
		
		Map<String, CSVData> youngerTopLevelParts = new  TreeMap<String, CSVData>();
		String youngerLabel = arguments.getValue(CompareArguments.YOUNGER_LABEL);
		
		Row youngerHeaders = youngerCSVData.getHeadersAsRow();
		
		for(int i = 0; i < youngerTopLevelColumn.size(); i++){
			if(youngerTopLevelColumn.get(i).toString().contains(topLevelContent)){
				
				//save identifier for the Map
				String identifier = identifierColumnYounger.get(i).toString();
				
				//add the first row with contains the TopLevel
				CSVData topLevelPart = new CSVData(youngerLabel);
				topLevelPart.initializeColumnsByHeaders(youngerHeaders);
				topLevelPart.addRow(youngerCSVData.getRow(i));
				
				//jump to next row and add all rows till the next topLevel
				i++;
				while(   i < youngerTopLevelColumn.size() 
						&& !youngerTopLevelColumn.get(i).toString().contains(topLevelContent) ){
					
					topLevelPart.addRow(youngerCSVData.getRow(i));
					i++;
				}
				//revert to one row before
				i--;
				
				youngerTopLevelParts.put(identifier, topLevelPart);
			}
		}
		
		
		logger.debug("topLevels found in younger: " + youngerTopLevelParts.size());
		
		//###################################################################################################
		// Compare TopLevelParts
		//###################################################################################################
		
		// remove topLevel-Argument from ArgumentList and compare TopLevelParts
		arguments.getLoadedArguments().remove(CompareArguments.COLUMN_TOPLEVEL);
		
		// On the first Comparison, use the result to initialize the variable "comparedResult"
		boolean isFirstComparison = true;
		
		// Save not Comparable TopLevels and append them on the end of the result
		ArrayList<CSVData>  youngerPartsNotComparable = new ArrayList<CSVData>();
		ArrayList<CSVData>  olderPartsNotComparable = new ArrayList<CSVData>();
		
		// -----------------------------------------------------
		// Iterate over youngerParts and compare if possible
		for(String identifier : youngerTopLevelParts.keySet()){
			
			CSVData olderCSVDataPart = olderTopLevelParts.get(identifier);
			CSVData youngerCSVDataPart = youngerTopLevelParts.get(identifier);
			
			if(olderTopLevelParts.containsKey(identifier)){
					
				CSVData partResult = CompareMethods.compare2CSVFiles(arguments, olderCSVDataPart, youngerCSVDataPart);
				
				if(isFirstComparison){
					isFirstComparison = false;
					compareResults = partResult;
				}else{
					compareResults.addRows(partResult.getAllRows());
				}
			}else{
				youngerPartsNotComparable.add(youngerCSVDataPart);
			}
		}
		
		// -----------------------------------------------------
		// Iterate over olderParts and find not comparable Parts
		for(String identifier : olderTopLevelParts.keySet()){
			if(!youngerTopLevelParts.containsKey(identifier)){
				olderPartsNotComparable.add(olderTopLevelParts.get(identifier));
			}
		}
		
		logger.info("younger top levels not comparable: " + youngerPartsNotComparable.size());
		logger.info("older top levels not comparable: " + olderPartsNotComparable.size());
		
		//###################################################################################################
		// Pseudo Compare for not comparable parts
		// this will create a result which fits in the 
		// other results.
		//###################################################################################################
		
		//initialize pseudo data
		CSVData olderPseudoCSVData = new CSVData("pseudo");
		olderPseudoCSVData.initializeColumnsByHeaders(youngerCSVData.getHeadersAsRow());
		CSVData youngerPseudoCSVData = new CSVData("pseudo");
		youngerPseudoCSVData.initializeColumnsByHeaders(youngerCSVData.getHeadersAsRow());
		
		// -----------------------------------------------------
		// younger pseudo compare
		for(CSVData youngerPart : youngerPartsNotComparable){
			CSVData partResult = CompareMethods.compare2CSVFiles(arguments, olderPseudoCSVData, youngerPart);
			
			if(isFirstComparison){
				isFirstComparison = false;
				compareResults = partResult;
			}else{
				compareResults.addRows(partResult.getAllRows());
			}
		}
		
		// -----------------------------------------------------
		// older pseudo compare
		for(CSVData olderPart : olderPartsNotComparable){
			CSVData partResult = CompareMethods.compare2CSVFiles(arguments, olderPart, youngerPseudoCSVData);
			
			if(isFirstComparison){
				isFirstComparison = false;
				compareResults = partResult;
			}else{
				compareResults.addRows(partResult.getAllRows());
			}
		}
				
		
		return compareResults;
	}
		

	/***********************************************************************************************
	 * This method is used when no "-column.compareDef" arguments are provided.
	 * It will compare all columns which has the same header in both files.
	 * 
	 * @param olderCSVData the object with the CSVData which should be considered as older in the 
	 *        comparison
	 * @param youngerCSVData the object with the CSVData which should be considered as younger in 
	 *        the comparison
	 * @param identifierColumnOlder the column of the older CSVData which is used to identify 
	 *        the record   
	 * @param identifierColumnYounger the column of the older CSVData which is used to identify 
	 *        the record
	 ***********************************************************************************************/
	private static CSVData doDefaultComparision(CompareArguments arguments, CSVData olderCSVData, CSVData youngerCSVData, Column identifierColumnOlder, Column identifierColumnYounger){
		
		//###################################################################################################
		// Initialize Variables
		//###################################################################################################
		
		//Initialize result variable
		CSVData compareResults = new CSVData("result");
		
		//Load Arguments
		String compareDifference = arguments.getValue(CompareArguments.RESULT_COMPAREDIFF).toLowerCase();
		String compareDiffPercentage = arguments.getValue(CompareArguments.RESULT_COMPARE_DIFF_PERCENTAGE).toLowerCase();
		String compareString = arguments.getValue(CompareArguments.RESULT_COMPARESTRING).toLowerCase();
		String doSortString = arguments.getValue(CompareArguments.RESULT_SORT).toLowerCase();
		boolean doSort = doSortString.matches("true") ? true : false ;
		
		//merge identifier Columns
		Column mergedIdentifierColumns = CSVAPIUtils.merge2ColumnsNoDuplicates(identifierColumnOlder, identifierColumnYounger, doSort);
		mergedIdentifierColumns.setHeader(identifierColumnYounger.getHeader());
		
		//###################################################################################################
		// Iterate over all Columns of the youngerData, try to find a 
		// matching column in the olderData and compare if there is 
		// a column with the same name.
		//###################################################################################################
		
		for(int i = 0; i < youngerCSVData.getColumns().size(); i++){
			
			// getColumns for Comparison
			Column valueColumnYounger = youngerCSVData.getColumn(i);
			Column valueColumnOlder = olderCSVData.getColumn(valueColumnYounger.getHeader());
			
			//if the current column is the identifier print it instead of comparing it
			if(valueColumnYounger.equals(identifierColumnYounger)){
				compareResults.addColumn(mergedIdentifierColumns);
			}
			else{	
				if(valueColumnOlder != null){
					
					logger.debug( "current columns to compare - "
							+ "olderColumn:"+valueColumnOlder.getHeader()
							+ ", youngerColumn:"+valueColumnYounger.getHeader());
					
					// create result Columns
					Column resultColumnOlder = CompareMethods.initializeResultColumn(mergedIdentifierColumns, identifierColumnOlder, valueColumnOlder);
					resultColumnOlder.setHeader(valueColumnOlder.getHeader()+"("+olderCSVData.getLabel()+")");
					
					Column resultColumnYounger = CompareMethods.initializeResultColumn(mergedIdentifierColumns, identifierColumnYounger, valueColumnYounger);
					resultColumnYounger.setHeader(valueColumnYounger.getHeader()+"("+youngerCSVData.getLabel()+")");	
					
					compareResults.addColumn(resultColumnOlder);
					compareResults.addColumn(resultColumnYounger);
					
					//Print Difference
					if(compareDifference.equals("true")){
						
						logger.debug("execute: " + CompareArguments.RESULT_COMPAREDIFF);
						
						Method diffCalculationMethod = CompareMethods.getMethod(CompareArguments.RESULT_COMPAREDIFF);
						if(diffCalculationMethod != null ){
							Column comparedColumn = CompareMethods.compare2Columns(resultColumnOlder, resultColumnYounger, diffCalculationMethod);
							
							if(comparedColumn != null){
								comparedColumn.setHeader(valueColumnOlder.getHeader()+" "+arguments.getHeaderForArgument(CompareArguments.RESULT_COMPAREDIFF));
								compareResults.addColumn(comparedColumn);
							}
						}
					}
					
					//Print Difference Percentage
					if(compareDiffPercentage.equals("true")){
						
						logger.debug("execute: "+ CompareArguments.RESULT_COMPARE_DIFF_PERCENTAGE);
						
						Method diffCalculationMethod = CompareMethods.getMethod(CompareArguments.RESULT_COMPARE_DIFF_PERCENTAGE);
						if(diffCalculationMethod != null ){
							Column comparedColumn = CompareMethods.compare2Columns(resultColumnOlder, resultColumnYounger, diffCalculationMethod);
							
							if(comparedColumn != null){
								comparedColumn.setHeader(valueColumnOlder.getHeader()+" "+arguments.getHeaderForArgument(CompareArguments.RESULT_COMPARE_DIFF_PERCENTAGE));
								compareResults.addColumn(comparedColumn);
							}
						}
					}
					
					//Print stringCompare
					if(compareString.equals("true")){
						
						logger.debug("execute: "+CompareArguments.RESULT_COMPARESTRING);

						Method compareStringMethod = CompareMethods.getMethod(CompareArguments.RESULT_COMPARESTRING);
						if(compareStringMethod != null ){
							Column comparedColumn = CompareMethods.compare2Columns(resultColumnOlder, resultColumnYounger, compareStringMethod);
							
							if(comparedColumn != null){
								comparedColumn.setHeader(valueColumnOlder.getHeader()+" "+arguments.getHeaderForArgument(CompareArguments.RESULT_COMPARESTRING));
								compareResults.addColumn(comparedColumn);
							}
						}
					}
				}else{
					logger.info("column header not found in older file: "+valueColumnYounger.getHeader());
				}
			}
		}
		
		return compareResults;
	}
	
	/***********************************************************************************************
	 * This method is used when "-column.compareDef" arguments are provided.
	 * It will compare all columns which has the same header in both files.
	 * 
	 * @param olderCSVData the object with the CSVData which should be considered as older in the 
	 *        comparison
	 * @param youngerCSVData the object with the CSVData which should be considered as younger in 
	 *        the comparison
	 * @param identifierColumnOlder the column of the older CSVData which is used to identify 
	 *        the record   
	 * @param identifierColumnYounger the column of the older CSVData which is used to identify 
	 *        the record
	 ***********************************************************************************************/
	private static CSVData doComparisionByManualDefinition(CompareArguments arguments, CSVData olderCSVData, CSVData youngerCSVData, Column identifierColumnOlder, Column identifierColumnYounger){
		
		//###################################################################################################
		// Initialize Variables
		//###################################################################################################
		
		//Initialize result variable
		CSVData compareResults = new CSVData("result");
		
		//used to check if identifierColumn is printed
		boolean isIdentifierPrinted = false;
		
		// Load sort argument
		String doSortString = arguments.getValue(CompareArguments.RESULT_SORT).toLowerCase();
		boolean doSort = doSortString.matches("true") ? true : false ;
		
		//Load Arguments
		ArrayList<CompareDefinition> compareDefinitions = arguments.getCompareDefinitions();

		//merge identifier Columns
		Column mergedIdentifierColumns = CSVAPIUtils.merge2ColumnsNoDuplicates(identifierColumnOlder, identifierColumnYounger, doSort);
		mergedIdentifierColumns.setHeader(identifierColumnOlder.getHeader());
		
		//###################################################################################################
		// Iterate over all compare definitions, and do the actions
		// which are defined in it
		//###################################################################################################
		for(CompareDefinition definition : compareDefinitions){
			
			//###################################################################################################
			//Evaluate Definition
			//###################################################################################################
		
			// getColumns for Comparison
			Column valueColumnOlder = olderCSVData.getColumn(definition.getOlderColumnName());
			Column valueColumnYounger = youngerCSVData.getColumn(definition.getYoungerColumnName());
			
			logger.debug("current columns for compare definition - "
						+ "olderColumn:"+valueColumnOlder.getHeader()
						+", youngerColumn:"+valueColumnYounger.getHeader());
			

			//###################################################################################################
			// create result Columns
			//###################################################################################################
			Column resultColumnOlder = CompareMethods.initializeResultColumn(mergedIdentifierColumns, identifierColumnOlder, valueColumnOlder);
			resultColumnOlder.setHeader(valueColumnOlder.getHeader()+"("+olderCSVData.getLabel()+")");
			
			Column resultColumnYounger = CompareMethods.initializeResultColumn(mergedIdentifierColumns, identifierColumnYounger, valueColumnYounger);
			resultColumnYounger.setHeader(valueColumnYounger.getHeader()+"("+youngerCSVData.getLabel()+")");	
			
			//###################################################################################################
			// Iterate over all methods in the definition
			//###################################################################################################
			
			for(Entry<String,String> methodEntry : definition.getMethods()){
				
				if(valueColumnYounger != null && valueColumnOlder != null ){
					
					String methodName =  		methodEntry.getKey();
					String methodArguments = 	methodEntry.getValue();
					
					logger.debug("manual compare - "
								+ " executeMethod: " + methodName
								+ " with Arguments: " + methodArguments);
					
					//###################################################################################################
					//Resolving Block for comparing methods
					//###################################################################################################
					if(   methodName.equals("compareDifference") 
					   || methodName.equals("compareDifference%")
					   || methodName.equals("compareAsStrings")){
						
						Method compareMethod = CompareMethods.getMethod(methodName);
						if(compareMethod != null ){
							Column comparedColumn = CompareMethods.compare2Columns(resultColumnOlder, resultColumnYounger, compareMethod);
							
							if(comparedColumn != null){
								
								//add result Columns
								
								comparedColumn.setHeader(valueColumnOlder.getHeader()+" "+arguments.getHeaderForArgument(methodName));
								compareResults.addColumn(comparedColumn);
							}
						}
					}
					
					//###################################################################################################
					//Resolving Block for calculation methods
					//###################################################################################################
					if(   methodName.equals("multiplyYoungerBy") 
					   || methodName.equals("multiplyOlderBy")
					   || methodName.equals("divideYoungerBy")
					   || methodName.equals("divideOlderBy")){
						
							try {
							
								if(methodName.equals("divideYoungerBy"))
									CSVAPIUtils.divideColumnBy(resultColumnYounger, methodArguments);
								
								if(methodName.equals("multiplyYoungerBy"))
									CSVAPIUtils.multiplyColumnBy(resultColumnYounger, methodArguments);
								
								if(methodName.equals("divideOlderBy"))
									CSVAPIUtils.divideColumnBy(resultColumnOlder, methodArguments);
								
								if(methodName.equals("multiplyOlderBy"))
									CSVAPIUtils.multiplyColumnBy(resultColumnOlder, methodArguments);
								
							} catch (Exception e) {
								logger.error("Error occured during manual compare: "+e.getMessage());
							}
					}
				
					//###################################################################################################
					//Resolving Blocks for printing methods
					//###################################################################################################
					
					if(methodName.equals("printOlder")){
						compareResults.addColumn(resultColumnOlder);
					}
					
					if(methodName.equals("printYounger")){
						compareResults.addColumn(resultColumnYounger);
					}
					
					if(methodName.equals("printMerged")){
						
						Column merged = CSVAPIUtils.mergeValuesOf2ResultColumns(resultColumnOlder, resultColumnYounger);
						
						if(valueColumnYounger.equals(identifierColumnYounger)){
							isIdentifierPrinted=true;
						}
						
						compareResults.addColumn(merged);
					}
					if(methodName.equals("printSeparator")){
						Column separatorColumn = new Column(" ");
						for(int j=0; j < mergedIdentifierColumns.size(); j++){
							separatorColumn.add(" ");
						}
						compareResults.addColumn(separatorColumn);
					}
					
					
				}else{
					logger.error("Could not find both columns - " +
								"youngerColumn: "+valueColumnYounger.getHeader()+
								", olderColumn: "+valueColumnOlder.getHeader());
				}
			}
		}
		
		//###################################################################################################
		// if the identifier column was not printed till know
		// add it as first column
		//###################################################################################################
		if(!isIdentifierPrinted){
			ArrayList<Column> columnsArray = compareResults.getColumns();
			compareResults = new CSVData("Results");
			compareResults.addColumn(mergedIdentifierColumns);
			compareResults.addColumns(columnsArray);
		}
		
		return compareResults;
	}
		
	/************************************************************
	 * Calculate the difference between two values as double.
	 * 
	 ************************************************************/
	public static double calcDiffAsDouble(Object olderValue, Object youngerValue) throws Exception{
		
		String valString1 = olderValue.toString();
		String valString2 = youngerValue.toString();
		Double d1;
		Double d2;
		Double compared;
		
		try{
			d1 = Double.parseDouble(valString1);
			d2 = Double.parseDouble(valString2);
			
			compared = d2 - d1; 
		}catch (NumberFormatException e) {
			throw new Exception("Could not parse as double: "+valString1+","+valString2);
		}
	
		return compared;
	}
	
	/************************************************************
	 * Calculate the difference in percentage between two values 
	 * as double.
	 * 
	 * @param  olderValue the older value
	 * @param youngerValue the younger value
	 * 
	 ************************************************************/
	public static double calcDiffPercentageAsDouble(Object olderValue, Object youngerValue) throws Exception{
		
		String valStringOlder = olderValue.toString();
		String valStringYounger = youngerValue.toString();
		Double doubleOlder;
		Double doubleYounger;
		Double compared;
		
		try{
			doubleOlder = Double.parseDouble(valStringOlder);
			doubleYounger = Double.parseDouble(valStringYounger);
			
			compared = doubleYounger/doubleOlder-1; 
		}catch (NumberFormatException e) {
			throw new Exception("Could not parse as double: "+valStringOlder+","+valStringYounger);
		}
	
		return compared;
	}
	
	/************************************************************
	 * Compare two Strings.<br>
	 * 
	 * @param  olderValue the older value
	 * @param youngerValue the younger value
	 * @return if(equals) --> "EQ"<br>
	 *         if(!equals) --> "NOTEQ"
	 ************************************************************/
	public static String compareAsString(Object olderValue, Object youngerValue) throws Exception{
		
		String valString1 = olderValue.toString();
		String valString2 = youngerValue.toString();
		String result = "NOTEQ";
	
		if(valString1.equals(valString2)){
			result="EQ";
		}
		
		return result;
	}
	
	/************************************************************
	 * Initialize a Result Column, this will put the Values at the
	 * correct position, so the values fit to the merged identifier   
	 * column.
	 * 
	 * @param mergedIdentifierColumns the column containing
	 *        the identifiers of both CSVData
	 * @param unmergedIdentifierColumn the identifier Column of
	 *        one of the CSVData
	 * @param valueColumn the column containing the values from
	 *        the same CSVData as the unmergedIdentifierColumn.
	 ************************************************************/	
	private static Column initializeResultColumn(Column mergedIdentifierColumns, Column unmergedIdentifierColumn, Column valueColumn){
		Column resultColumn = new Column("result");
		
		for(Object rowIdentifier : mergedIdentifierColumns){
			Object resultValue;
	
			//initialize resultColumns
			if(unmergedIdentifierColumn.contains(rowIdentifier)){
				resultValue = valueColumn.get(unmergedIdentifierColumn.indexOf(rowIdentifier));
			}else{
				resultValue = "-";
			}
		
			resultColumn.add(resultValue);
		}
		
		return resultColumn;
	}
	
	/************************************************************
	 * Compare two columns by the given Method.
	 * 
	 * @param olderColumn the column of the older CSVData
	 * @param youngerColumn the column of the younger CSVData
	 ************************************************************/
	private static Column compare2Columns(Column olderColumn, Column youngerColumn, Method compareMethod){
		
		Column comparedColumn = new Column("compared");
		
		int size = olderColumn.size();
		for(int i=0 ; i < size ; i++){
			String olderValue = olderColumn.get(i).toString();
			String youngerValue = youngerColumn.get(i).toString();
			
			Object resultValue="-";
			if(olderValue.trim().equals("-") || youngerValue.trim().equals("-")){
				resultValue = "-";
			}else{
			
				try {
					resultValue = compareMethod.invoke(null, olderValue, youngerValue);
				} catch (Exception e) {
					logger.warn("Error on invoke Method '"+compareMethod.getName()+"': "
							+ e.getMessage());

					resultValue = "ERR";
				}
			}
			
			comparedColumn.add(resultValue);
		}
		
		return comparedColumn;
	}
	
	/************************************************************
	 * Get the Method to use for the Comparison by the argument.
	 * 
	 * @param argument the command line argument which
	 *         represents a method in Arguments.methodMap.
	 *         
	 ************************************************************/
	private static Method getMethod(String argument){
		String methodName = CompareArguments.getMethodForArgument(argument);
		try{
			return CompareMethods.class.getMethod(methodName, Object.class, Object.class);
		} catch (SecurityException e) {
			logger.error("Could not reflect method '"+methodName+"': "
					+ e.getMessage());
		} catch (NoSuchMethodException e) {
			logger.error("Could not reflect method '"+methodName+"': "
					+ e.getMessage());
		}
		return null;
	} 
}
