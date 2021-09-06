package com.csvapi.compare;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.csvapi.arguments.ArgumentsException;
import com.csvapi.model.CSVData;

/*********************************************************************************************
* This class is the API provided for csv comparison.
* 
* @author R.Scheiwiller, 2015
**********************************************************************************************/
public class CSVAPICompare {

	public static final Logger logger = LogManager.getLogger(CSVAPICompare.class.getName());
	
	/*********************************************************************************************
	* Compares the .csv-Files defined with the arguments and writes the result.
	* 
	* @param arguments the arguments used for comparison
	* 
	**********************************************************************************************/
	public static void compareCSVWriteResult(CompareArguments arguments) throws ArgumentsException {
		
		//-------------------------------------
		// Save Arguments into Strings
		//-------------------------------------
		String olderLabel 		= arguments.getValue(CompareArguments.OLDER_LABEL);
		String olderFile 		= arguments.getValue(CompareArguments.OLDER_FILE);
		String olderDelimiter 	= arguments.getValue(CompareArguments.OLDER_DELIMITER);
		boolean olderQuotes 	= arguments.getValue(CompareArguments.OLDER_QUOTES).toLowerCase().equals("true") ? true : false ;
		
		String youngerLabel 	= arguments.getValue(CompareArguments.YOUNGER_LABEL);
		String youngerFile 		= arguments.getValue(CompareArguments.YOUNGER_FILE);
		String youngerDelimiter = arguments.getValue(CompareArguments.YOUNGER_DELIMITER);
		boolean youngerQuotes 	= arguments.getValue(CompareArguments.YOUNGER_QUOTES).toLowerCase().equals("true") ? true : false ;
		
		//-------------------------------------
		// Compare if both file exists and are readable
		//-------------------------------------
		if(new File(olderFile).canRead() && new File(youngerFile).canRead()){
				
			//-------------------------------------
			// Read the CSV-Files
			//-------------------------------------
			CSVData olderCSVData = new CSVData(olderLabel);
			olderCSVData.readCSVData(new File(olderFile), olderDelimiter, olderQuotes);

			CSVData youngerCSVData = new CSVData(youngerLabel);
			youngerCSVData.readCSVData(new File(youngerFile), youngerDelimiter, youngerQuotes);
			
			compareCSVWriteResult(arguments, olderCSVData, youngerCSVData);
			
		}else{
			if(!new File(olderFile).canRead()){
				logger.error("Could not read file: "+olderFile);
			}
			if(!new File(youngerFile).canRead()){
				logger.error("Could not read file: "+youngerFile);
			}
		}
		
	}

	/*********************************************************************************************
	* Compares the two .csv-Files and writes the result.
	* 
	* @param arguments the arguments used for comparison
	* @param olderCSVData the CSVData containing the older values
	* @param youngerCSVData the CSVData containing the younger values
	* 
	**********************************************************************************************/
	public static void compareCSVWriteResult( CompareArguments arguments,
									CSVData olderCSVData, 
									CSVData youngerCSVData) throws ArgumentsException {
		
		String resultFile 		= arguments.getValue(CompareArguments.RESULT_FILE);
		String resultDelimiter 	= arguments.getValue(CompareArguments.RESULT_DELIMITER);
		String resultSTDOUT 	= arguments.getValue(CompareArguments.RESULT_STDOUT);
		boolean resultQuotes 	= Boolean.parseBoolean(arguments.getValue(CompareArguments.RESULT_QUOTES));
		
		CSVData comparedCSVData = compareCSVReturnResult(arguments, olderCSVData,
				youngerCSVData);

		// -------------------------------------
		// Write the compared Result
		// -------------------------------------
		if (comparedCSVData != null) {

			if (resultSTDOUT.toLowerCase().equals("false")) {

				// -------------------------------------
				// Save to file
				// -------------------------------------
				try {
					comparedCSVData.writeCSVData(resultFile, resultDelimiter, resultQuotes);

				} catch (IOException e) {
					logger.error("A error occured while writting to the result: "
							+ e.getMessage());
					if (resultSTDOUT.toLowerCase().matches("onerror")) {
						comparedCSVData.printCSVtoSTDOUT(resultDelimiter, resultQuotes);
					}
				}
				
			} else {
				// -------------------------------------
				// Write to standard output
				// -------------------------------------
				comparedCSVData.printCSVtoSTDOUT(resultDelimiter, resultQuotes);
			}

		} else {
			logger.warn("The result is null!!!");
		}
	}

	/*********************************************************************************************
	* Compares the two .csv-Files and returns the result.
	* 
	* @param arguments the arguments used for comparison
	* @param olderCSVData the CSVData containing the older values
	* @param youngerCSVData the CSVData containing the younger values
	*  
	**********************************************************************************************/
	public static CSVData compareCSVReturnResult(CompareArguments arguments,
			CSVData olderCSVData, CSVData youngerCSVData) throws ArgumentsException {
		checkArguments(arguments);
		
		logger.info("START - Compare CSV Files"); long startTime = System.nanoTime();
		
		CSVData comparedCSVData = CompareMethods.compare2CSVFiles(arguments, olderCSVData,
				youngerCSVData);
		
		logger.info("END - Compare CSV Files (Duration="+( (System.nanoTime()-startTime) / 1000000)+"ms)");
		
		return comparedCSVData;
	}

	/*********************************************************************************************
	* Checks if the arguments are valid.
	* 
	* @param arguments the arguments that should be checked
	* 
	**********************************************************************************************/
	private static void checkArguments(CompareArguments arguments) throws ArgumentsException {
		
		if(!arguments.validateArguments()){
			StringBuffer allMessages = new StringBuffer();
			
			for(String message : arguments.getInvalidMessages()){ 
				logger.error(message.toString());
				allMessages.append(message);
				allMessages.append("\n");
			}
			throw new ArgumentsException(allMessages.toString());
		}
		
		//-------------------------------------
		// DEBUG
		//-------------------------------------
		logger.debug("######################## print readed arguments ########################");
		
		LinkedHashMap<String,String> argumentList = arguments.getLoadedArguments();
		Set<String> keySet = argumentList.keySet();
		for(String key : keySet){
			logger.debug("Key: "+key+", Value:"+argumentList.get(key));
		}
		
		logger.debug("######################## print readed compare definitions ########################");
		ArrayList<CompareDefinition> compareDefinitions = arguments.getCompareDefinitions();
		for(CompareDefinition definition : compareDefinitions){
			logger.debug("Column Definition: "+definition.getArgument());
		}
		
	}
}
