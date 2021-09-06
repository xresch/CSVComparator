package com.csvapi.compare;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.csvapi.arguments.ArgumentsException;


/*************************************************************************
 * This class is used to hold a compare definition used for manual
 * comparison.
 * 
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/
public class CompareDefinition {
	
	private static final Logger logger = LogManager.getLogger(CompareDefinition.class.getName());
	
	public static final String PRINT_OLDER = "printOlder";
	public static final String PRINT_YOUNGER = "printYounger";
	public static final String PRINT_MERGED = "printMerged";
	public static final String PRINT_SEPARATOR = "printSeparator";

	public static final String COMPARE_DIFF = "compareDifference";
	public static final String COMPARE_DIFF_PERCENTAGE = "compareDifference%";
	public static final String COMPARE_AS_STRINGS = "compareAsStrings";

	public static final String DIVIDE_YOUNGER = "divideYoungerBy";
	public static final String MULTIPLY_YOUNGER = "multiplyYoungerBy";
	public static final String DIVIDE_OLDER = "divideOlderBy";
	public static final String MULTIPLY_OLDER = "multiplyOlderBy";

	private String olderColumnName = "";
	private String youngerColumnName = "";
	private String argument = "";
	
	// Stores methods with arguments
	private ArrayList<Entry<String,String>> methodsArray = new ArrayList<Entry<String,String>>();

	
	//####################################################################################
	// CLASS METHODS
	//####################################################################################
	
	/*************************************************************************
	 * Adds a method to the compare definition.
	 * 
	 * @param method method
	 * @param arguments arguments
	 * 
	 *************************************************************************/
	public CompareDefinition addMethod(String method, String arguments){
		methodsArray.add(new AbstractMap.SimpleEntry<String,String>(method,arguments));
		return this;
	}
	
	/*************************************************************************
	 * Load a compare definition with the format provided on the command line
	 * argument.
	 * 
	 * @param argumentValue the value of "-column.compareDef=&lt;value&gt;"
	 * 
	 *************************************************************************/
	public void parseDefinition(String argumentValue) throws ArgumentsException{
		
		logger.trace("Load compare definition: " + argumentValue);
		
		//------------------------------------------------------
		// Resolve Older/Younger Column
		//------------------------------------------------------
		String[] splittedDefinition = argumentValue.split(",");
		
		if(splittedDefinition.length >= 2){
			olderColumnName = splittedDefinition[0];
			youngerColumnName = splittedDefinition[1];
		}else{
			ArgumentsException exception = new ArgumentsException("Compare Definition could not be loaded: "+argumentValue);
			exception.setArgument(argumentValue);
			throw exception;
		}
		
		//------------------------------------------------------
		// Resolve Methods
		//------------------------------------------------------
		for(int i=2; i < splittedDefinition.length; i++){
			String method = splittedDefinition[i];
			
			if(method.trim().matches(".+\\(.*\\)$")){
				
				logger.trace("Add method to compare definition: " + method);
				String methodName =  		method.substring(0, method.indexOf("("));
				String methodArguments = 	method.substring(method.indexOf("(")+1, method.lastIndexOf("")-1);
				
				this.addMethod(methodName, methodArguments);
			}else{
				ArgumentsException exception = new ArgumentsException("Wrong method format in compare definition: "+argumentValue);
				exception.setArgument(argumentValue);
				throw exception;
			}
			
		}
		
		this.setArgument(argumentValue);
		
	}
	
	//####################################################################################
	// GETTERS & SETTERS
	//####################################################################################
	
	public String getArgumentValue() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(olderColumnName);
		buffer.append(",");
		buffer.append(youngerColumnName);
		
		for(Entry<String,String> methodEntry : methodsArray){
			buffer.append(",");
			buffer.append(methodEntry.getKey());
			buffer.append("(");
			buffer.append(methodEntry.getValue());
			buffer.append(")");
		}
		return buffer.toString();
	}
	
	public String getArgument() {
		return argument;
	}
	public void setArgument(String argument) {
		this.argument = argument;
	}
	
	public ArrayList<Entry<String,String>> getMethods(){
		return methodsArray;
	}
	public String getOlderColumnName() {
		return olderColumnName;
	}

	public void setOlderColumnName(String olderColumnName) {
		this.olderColumnName = olderColumnName;
	}

	public String getYoungerColumnName() {
		return youngerColumnName;
	}

	public void setYoungerColumnName(String youngerColumnName) {
		this.youngerColumnName = youngerColumnName;
	}
	
	
}
