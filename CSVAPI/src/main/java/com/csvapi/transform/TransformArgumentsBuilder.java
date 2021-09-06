package com.csvapi.transform;

import java.util.LinkedHashMap;

import com.csvapi.compare.CompareArguments;

/*************************************************************************
 * This class is used to build an instance of TransformArguments.
 *  
 * @author Reto Scheiwiller, 2016
 * 
 *************************************************************************/
public class TransformArgumentsBuilder {

	private LinkedHashMap<String,String> argumentsToSet;

	//####################################################################################
	// CONSTRUCTORS
	//####################################################################################
	public TransformArgumentsBuilder() {
		argumentsToSet = new LinkedHashMap<String,String>();
	}

	//####################################################################################
	// BUILD METHOD
	//####################################################################################
	public TransformArguments build(){
		TransformArguments arguments = new TransformArguments();
		arguments.addAllArgument(argumentsToSet);
		return arguments;
	}

	//####################################################################################
	// GETTERS & SETTERS
	//####################################################################################
	public TransformArgumentsBuilder setInputPath(String inputPath) {
		argumentsToSet.put(TransformArguments.INPUT_PATH, inputPath);
		return this;
	}

	public TransformArgumentsBuilder setInputFormat(String inputFormat) {
		argumentsToSet.put(TransformArguments.INPUT_FORMAT, inputFormat);
		return this;
	}

	public TransformArgumentsBuilder setInputFileFilter(String inputFileFilter) {
		argumentsToSet.put(TransformArguments.INPUT_FILEFILTER,
				inputFileFilter);
		return this;
	}

	public TransformArgumentsBuilder setResultFile(String resultFile) {
		argumentsToSet.put(TransformArguments.RESULT_FILE, resultFile);
		return this;
	}

	public TransformArgumentsBuilder setResultDelimiter(String resultDelimiter) {
		argumentsToSet.put(TransformArguments.RESULT_DELIMITER,
				resultDelimiter);
		return this;
	}

	public TransformArgumentsBuilder setResultSort(boolean resultSort) {
		argumentsToSet.put(TransformArguments.RESULT_SORT, Boolean.toString(resultSort));
		return this;
	}

	public TransformArgumentsBuilder setResultStdout(boolean resultStdout) {
		argumentsToSet.put(TransformArguments.RESULT_STDOUT, Boolean.toString(resultStdout));
		return this;
	}

	public TransformArgumentsBuilder setResultPrefix(String resultPrefix) {
		argumentsToSet.put(TransformArguments.RESULT_PREFIX, resultPrefix);
		return this;
	}
	
	public TransformArgumentsBuilder setConfigFile(String filePath) {
		argumentsToSet.put(CompareArguments.CONFIG_FILE, filePath);
		return this;
	}
	
	public TransformArgumentsBuilder setLogLevelFile(String logLevel) {
		argumentsToSet.put(CompareArguments.CONFIG_LOGLEVEL_FILE, logLevel);
		return this;
	}
	
	public TransformArgumentsBuilder setLogLevelConsole(String logLevel) {
		argumentsToSet.put(CompareArguments.CONFIG_LOGLEVEL_CONSOLE, logLevel);
		return this;
	}
}
