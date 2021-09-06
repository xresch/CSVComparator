package com.csvtransformer.cli.main;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.csvapi.arguments.ArgumentsException;
import com.csvapi.transform.CSVAPITransform;
import com.csvapi.transform.TransformArguments;

public class CSVTransformerCLI {
	
	public static final Logger logger = LogManager.getLogger(CSVTransformerCLI.class.getName());
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.setProperty("isThreadContextMapInheritable", "true");
		org.apache.logging.log4j.ThreadContext.put("requestid", UUID.randomUUID().toString());
		
		//-------------------------------------
		// Read and check CommandLine Arguments
		// terminate in case of issues
		//-------------------------------------
		TransformArguments arguments = new TransformArguments();

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
			CSVAPITransform.transfromAndWriteResult(arguments);
		} catch (ArgumentsException e) {
			System.out.println(e.getArgument());
			arguments.printUsage();
		}
	}
}
