package com.csvcomparator.cli.main;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.csvapi.arguments.ArgumentsException;
import com.csvapi.compare.CSVAPICompare;
import com.csvapi.compare.CompareArguments;
import com.csvapi.utils.Log4jUtils;

/*************************************************************************
 * In the Main class the arguments are read and depending on the input
 * it is decided if a comparison could be done. Also the result is written 
 * to the resultFile provided by the arguments or to the default file.
 * <p>
 * For a List of all possible arguments see {@link CompareArguments}.
 * 
 * @author Reto Scheiwiller
 * 
 * @param args - List of Arguments. 
 * 
 *************************************************************************/

public class CSVComparatorCLI {

	public static final Logger logger = LogManager.getLogger(CSVComparatorCLI.class.getName());
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Log4jUtils.setNewRequestIDForThread(false);
		//-------------------------------------
		// Read and check CommandLine Arguments
		// terminate in case of issues
		//-------------------------------------
		CompareArguments arguments = new CompareArguments();

		if(args.length <= 0){
			arguments.printUsage();
			return;
		}
		
		try {
			arguments.readArguments(args);
		} catch (ArgumentsException e) {
			logger.error("Could not read argument: "+e.getArgument());
			return;
		}
	
		try {
			CSVAPICompare.compareCSVWriteResult(arguments);
		} catch (ArgumentsException e) {
			
			System.out.println(e.getMessage());
			arguments.printUsage();
		}
	}


}
