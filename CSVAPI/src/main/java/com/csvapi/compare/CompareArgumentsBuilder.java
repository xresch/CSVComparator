package com.csvapi.compare;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.csvapi.arguments.ArgumentsException;

public class CompareArgumentsBuilder {
	
	private LinkedHashMap<String,String> argumentsToSet;
	private ArrayList<CompareDefinition> compareDefinitions;

	//####################################################################################
	// CONSTRUCTORS
	//####################################################################################
	public CompareArgumentsBuilder() {
		argumentsToSet = new LinkedHashMap<String,String>();
		compareDefinitions = new ArrayList<CompareDefinition>();
	}
	
	//####################################################################################
	// BUILD METHOD
	//####################################################################################
	public CompareArguments build(){
		CompareArguments arguments = new CompareArguments();
		arguments.addAllArgument(argumentsToSet);
		arguments.addCompareDefinitions(compareDefinitions);
		
		return arguments;
	}
	
	/*******************************************************************
	 * Returns an array list with all arguments that were added to this 
	 * builder.
	 * 
	 * @return ArrayList<String>
	 *******************************************************************/
	public ArrayList<String> toArrayListAll(){
		ArrayList<String> arrayList = new ArrayList<String>();
		for(Entry<String, String> entry : argumentsToSet.entrySet()){
			arrayList.add(entry.getKey()+"="+entry.getValue());
		}
		
		for(CompareDefinition definition : compareDefinitions){
			arrayList.add(CompareArguments.COLUMN_COMPARE_DEF+"="+definition.getArgumentValue());
		}
		
		return arrayList;
	}
	
	/*******************************************************************
	 * Returns an array list containing the arguments that were added to this 
	 * builder without compare definitions
	 * 
	 * @return ArrayList<String>
	 *******************************************************************/
	public ArrayList<String> toArrayListNoCompareDefinitions(){
		ArrayList<String> arrayList = new ArrayList<String>();
		for(Entry<String, String> entry : argumentsToSet.entrySet()){
			arrayList.add(entry.getKey()+"="+entry.getValue());
		}
		
		return arrayList;
	}
	
	/*******************************************************************
	 * Returns an array list containing all compare definitions added to
	 * the builder.
	 * 
	 * @return ArrayList<String>
	 *******************************************************************/
	public ArrayList<String> toArrayListCompareDefinitions(){
		ArrayList<String> arrayList = new ArrayList<String>();
		
		for(CompareDefinition definition : compareDefinitions){
			arrayList.add(CompareArguments.COLUMN_COMPARE_DEF+"="+definition.getArgumentValue());
		}
		
		return arrayList;
	}
	

	//####################################################################################
	// GETTERS & SETTERS
	//####################################################################################
	public CompareArgumentsBuilder setOlderFile(String olderFile) {
		argumentsToSet.put(CompareArguments.OLDER_FILE, olderFile);
		return this;
	}
	
	public CompareArgumentsBuilder setOlderLabel(String olderLabel) {
		argumentsToSet.put(CompareArguments.OLDER_LABEL, olderLabel);
		return this;
	}
	
	public CompareArgumentsBuilder setOlderDelimiter(String olderDelimiter) {
		argumentsToSet.put(CompareArguments.OLDER_DELIMITER, olderDelimiter);
		return this;
	}
	
	public CompareArgumentsBuilder setOlderQuotes(boolean olderQuotes) {
		argumentsToSet.put(CompareArguments.OLDER_QUOTES, Boolean.toString(olderQuotes));
		return this;
	}
	
	public CompareArgumentsBuilder setYoungerFile(String youngerFile) {
		argumentsToSet.put(CompareArguments.YOUNGER_FILE, youngerFile);
		return this;
	}
	
	public CompareArgumentsBuilder setYoungerLabel(String youngerLabel) {
		argumentsToSet.put(CompareArguments.YOUNGER_LABEL, youngerLabel);
		return this;
	}
	
	public CompareArgumentsBuilder setYoungerDelimiter(String youngerDelimiter) {
		argumentsToSet.put(CompareArguments.YOUNGER_DELIMITER, youngerDelimiter);
		return this;
	}
	
	public CompareArgumentsBuilder setYoungerQuotes(boolean youngerQuotes) {
		argumentsToSet.put(CompareArguments.YOUNGER_QUOTES, Boolean.toString(youngerQuotes));
		return this;
	}
	
	public CompareArgumentsBuilder setResultFile(String resultFile) {
		argumentsToSet.put(CompareArguments.RESULT_FILE, resultFile);
		return this;
	}
	
	public CompareArgumentsBuilder setResultDelimiter(String resultDelimiter) {
		argumentsToSet.put(CompareArguments.RESULT_DELIMITER, resultDelimiter);
		return this;
	}
	
	public CompareArgumentsBuilder setResultCompareDiff(boolean resultCompareDiff) {
		argumentsToSet.put(CompareArguments.RESULT_COMPAREDIFF, Boolean.toString(resultCompareDiff));
		return this;
	}
	
	public CompareArgumentsBuilder setResultCompareDiffPercentage(boolean resultCompareDiffPercentage) {
		argumentsToSet.put(CompareArguments.RESULT_COMPARE_DIFF_PERCENTAGE, Boolean.toString(resultCompareDiffPercentage));
		return this;
	}
	
	public CompareArgumentsBuilder setResultCompareString(boolean resultCompareString) {
		argumentsToSet.put(CompareArguments.RESULT_COMPARESTRING, Boolean.toString(resultCompareString));
		return this;
	}
	
	public CompareArgumentsBuilder setResultSort(boolean resultSort) {
		argumentsToSet.put(CompareArguments.RESULT_SORT, Boolean.toString(resultSort));
		return this;
	}
	
	/**
	 * @param resultStdout either "true", "false" or "onerror" (Default: "true")
	 */
	public CompareArgumentsBuilder setResultStdout(boolean resultStdout) {
		argumentsToSet.put(CompareArguments.RESULT_STDOUT, Boolean.toString(resultStdout));
		return this;
	}
	
	public CompareArgumentsBuilder setResultQuotes(boolean resultQuotes) {
		argumentsToSet.put(CompareArguments.RESULT_QUOTES, Boolean.toString(resultQuotes));
		return this;
	}
	
	public CompareArgumentsBuilder addColumnCompareDefinition(String columnCompareDefinition) throws ArgumentsException {
		CompareDefinition definition = new CompareDefinition();
		definition.parseDefinition(columnCompareDefinition);
		compareDefinitions.add(definition);
		return this;
	}
	
	public CompareArgumentsBuilder addColumnCompareDefinition(CompareDefinition columnCompareDefinition) {
		compareDefinitions.add(columnCompareDefinition);
		return this;
	}
	
	public CompareArgumentsBuilder setColumnTopLevel(String columnTopLevel) {
		argumentsToSet.put(CompareArguments.COLUMN_TOPLEVEL, columnTopLevel);
		return this;
	}
	
	
	public CompareArgumentsBuilder setColumnTopLevel(String olderColumn, String youngerColumn, String match) {
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(olderColumn);
		buffer.append(",");
		buffer.append(youngerColumn);
		buffer.append(",");
		buffer.append(match);
		
		argumentsToSet.put(CompareArguments.COLUMN_TOPLEVEL, buffer.toString());
		return this;
	}
	
	public CompareArgumentsBuilder setColumnIdentifierMakeUnique(boolean columnIdentifierMakeUnique) {
		argumentsToSet.put(CompareArguments.COLUMN_IDENTIFIER_MAKEUNIQUE, Boolean.toString(columnIdentifierMakeUnique));
		return this;
	}
	
	public CompareArgumentsBuilder setColumnIdentifier(String columnIdentifier) {
		argumentsToSet.put(CompareArguments.COLUMN_IDENTIFIER, columnIdentifier);
		return this;
	}
	
	public CompareArgumentsBuilder setColumnIdentifier(String olderColumn, String youngerColumn) {
		argumentsToSet.put(CompareArguments.COLUMN_IDENTIFIER, olderColumn + "," + youngerColumn);
		return this;
	}

	public CompareArgumentsBuilder setConfigFile(String filePath) {
		argumentsToSet.put(CompareArguments.CONFIG_FILE, filePath);
		return this;
	}
	
	public CompareArgumentsBuilder setLogLevelFile(String logLevel) {
		argumentsToSet.put(CompareArguments.CONFIG_LOGLEVEL_FILE, logLevel);
		return this;
	}
	
	public CompareArgumentsBuilder setLogLevelConsole(String logLevel) {
		argumentsToSet.put(CompareArguments.CONFIG_LOGLEVEL_CONSOLE, logLevel);
		return this;
	}
}
