package com.csvapi.transform;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.csvapi.arguments.ArgumentsException;
import com.csvapi.compare.CSVAPICompare;
import com.csvapi.model.CSVData;

/*********************************************************************************************
* This class is the API provided for csv transformation.
* 
* @author R.Scheiwiller, 2015
**********************************************************************************************/
public class CSVAPITransform {

	public static final Logger logger = LogManager.getLogger(CSVAPICompare.class.getName());

	/*********************************************************************************************
	* Transforms the file defined in the arguments and writes the result.
	* 
	* @param arguments the arguments used for comparison
	* 
	**********************************************************************************************/
	public static void transfromAndWriteResult(TransformArguments arguments) throws ArgumentsException {
		
		String resultFilePath = arguments.getValue(TransformArguments.RESULT_FILE);
		String resultDelimiter = arguments.getValue(TransformArguments.RESULT_DELIMITER);
		String resultSTDOUT = arguments.getValue(TransformArguments.RESULT_STDOUT);

		CSVData result = transformAndReturnResult(arguments);

		//######################################
		// Write Result
		//######################################
		try {
			if(resultSTDOUT.toLowerCase().equals("true")){
				logger.info("Write result to standard output");
				result.printCSVtoSTDOUT(resultDelimiter, false);
			}else{
				
				result.writeCSVData(resultFilePath, resultDelimiter, false);
					
			}
		} catch (IOException e) {
			logger.error("Could not write result: ", e);
			e.printStackTrace();
		}
		
	}
	
	/*********************************************************************************************
	* Transforms the file defined in the arguments to csv and returns the result.
	* 
	* @param arguments the arguments used for comparison
	* 
	**********************************************************************************************/
	public static CSVData transformAndReturnResult(TransformArguments arguments) throws ArgumentsException {
		
		logger.info("Start Transform"); long startTime = System.nanoTime();

		if(!arguments.validateArguments()){
			throw new ArgumentsException(arguments.getInvalidMessages());
		}
		
		CSVData result = TransformMethods.transform(arguments);
		
		String sortResult = arguments.getValue(TransformArguments.RESULT_SORT);
		
		if(sortResult.toLowerCase().equals("true")){
			
		}
		
		logger.info("End Transform <duration>"+( (System.nanoTime()-startTime) / 1000000)+"ms</duration>");

		return result;
	}

}
