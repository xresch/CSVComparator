package com.csvapi.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.csvapi.arguments.AbstractArgumentsHandler;
import com.csvapi.arguments.ArgumentDefinition;
import com.csvapi.arguments.ArgumentsException;
import com.csvapi.arguments.validation.BooleanArgumentValidator;
import com.csvapi.arguments.validation.FileCanReadArgumentValidator;
import com.csvapi.arguments.validation.FileCanWriteArgumentValidator;
import com.csvapi.arguments.validation.RegexArgumentValidator;
import com.csvapi.arguments.validation.StringLengthArgumentValidator;

/*********************************************************************************************
 * This class is used to read the arguments provided to the application,
 * storing them and provided them globally.
 * The arguments are used in the whole application for different purposes.
 * Also the argument "-column.compareDef" is used to choose some methods, which
 * should be invoked.<p>
 * Please find the documentation for the arguments and methods in the file
 * "CSVComparator_Documentation.docx".
 * <p>
 * Available arguments 																	<br>
 * ===============================================										<br>
 * -config.file={filepath}																<br>
 * -config.loglevel.console={true|false}												<br>
 * -config.loglevel.file={true|false}													<br>
 * 
 * -older.label={label}																	<br>
 * -older.delimiter={delimiter}															<br>
 * -older.file={filepath}																<br>
 * -older.quotes={true|false}															<br>
 * 	
 * -younger.label={label}																<br>
 * -younger.delimiter={delimiter}														<br>
 * -younger.file={filepath}																<br>
 * -younger.quotes={true|false}															<br>
 * 																						<br>
 * -result.file={filepath}																<br>
 * -result.delimiter={delimiter}														<br>
 * 																						<br>
 * -result.comparediff={true|false}														<br>
 * -result.comparediff%={true|false}													<br>
 * -result.comparestring={true|false}													<br>
 * 																						<br>
 * -result.sort={true|false}															<br>
 * -result.stdout={true|false|onerror}													<br>
 * 																						<br>
 * -column.identifier={olderColumnHeader},{youngerColumnHeader}							<br>
 * -column.identifier.makeunique={true|false}											<br>
 * -column.toplevel={olderColumnHeader},{youngerColumnHeader},{Value}					<br>
 * -column.compareDef={olderColumnHeader},{youngerColumnHeader},{method},{method},...	<br>
 * 																						<br>
 * ##### TODO -hierarchy={Value},{Value},{Value}...										<br>
 * ##### TODO -hierarchyColumn={olderColumnHeader},{youngerColumnHeader}				<br>
 * 																						<br>

 * 																						<br>
 * Available Methods for -column.compareDef												<br>
 * ===============================================										<br>
 * -printOlder()																		<br>
 * -printYounger()																		<br>
 * -printMerged()																		<br>
 * -printSeparator()																	<br>
 * 																						<br>
 * -compareDifference%()																<br>
 * -compareDifference()																	<br>
 * -compareAsStrings()																	<br>
 * 																						<br>
 * -divideYoungerBy()																	<br>
 * -multiplyYoungerBy()																	<br>
 * -divideOlderBy()																		<br>
 * -multiplyOlderBy()																	<br>
 *
 * @author Reto Scheiwiller, 2014
 * 
 *********************************************************************************************/

public class CompareArguments extends AbstractArgumentsHandler {
	
	
	public static final String OLDER_FILE = "-older.file";
	public static final String OLDER_LABEL = "-older.label";
	public static final String OLDER_DELIMITER = "-older.delimiter";
	public static final String OLDER_QUOTES = "-older.quotes";
	
	public static final String YOUNGER_FILE = "-younger.file";
	public static final String YOUNGER_LABEL = "-younger.label";
	public static final String YOUNGER_DELIMITER = "-younger.delimiter";
	public static final String YOUNGER_QUOTES = "-younger.quotes";
			
	public static final String RESULT_FILE = "-result.file";
	public static final String RESULT_DELIMITER = "-result.delimiter";
	public static final String RESULT_COMPAREDIFF = "-result.comparediff";
	public static final String RESULT_COMPARE_DIFF_PERCENTAGE = "-result.comparediff%";
	public static final String RESULT_COMPARESTRING = "-result.comparestring";
	public static final String RESULT_SORT = "-result.sort";
	public static final String RESULT_STDOUT = "-result.stdout";
	public static final String RESULT_QUOTES = "-result.quotes";

	public static final String COLUMN_COMPARE_DEF = "-column.compareDef";
	public static final String COLUMN_TOPLEVEL = "-column.toplevel";
	public static final String COLUMN_IDENTIFIER_MAKEUNIQUE = "-column.identifier.makeunique";
	public static final String COLUMN_IDENTIFIER = "-column.identifier";

	private ArrayList<CompareDefinition> compareDefinitions;
	
	//List of the arguments which are supported with a usage description
	private static HashMap<String,String[]> methodMap;
	
	//####################################################################################
	// CONSTRUCTORS
	//####################################################################################
	public CompareArguments() {
		
		
		compareDefinitions = new ArrayList<CompareDefinition>();
		
		methodMap = new HashMap<String,String[]>();

		initializeSupportedArguments();
		
		//initialize with default settings
		loadDefaultSettings();
		
		//*********************************************
		// Methods for Default Comparison
		//*********************************************
		methodMap.put(RESULT_COMPAREDIFF, new String[]{"calcDiffAsDouble","Diff"});
		methodMap.put(RESULT_COMPARE_DIFF_PERCENTAGE, new String[]{"calcDiffPercentageAsDouble","Diff%"});
		methodMap.put(RESULT_COMPARESTRING, new String[]{"compareAsString","EQ?"});
		
		//*********************************************
		// Methods for Manual Comparison
		//*********************************************
		methodMap.put("compareDifference", new String[]{"calcDiffAsDouble","Diff"});
		methodMap.put("compareDifference%", new String[]{"calcDiffPercentageAsDouble","Diff%"});
		methodMap.put("compareAsStrings", new String[]{"compareAsString","EQ?"});
		
	}

	//####################################################################################
	// CLASS METHODS
	//####################################################################################
	
	/***********************************************************
	 * Initialize the supported arguments. 
	 * 
	 ***********************************************************/
	private void initializeSupportedArguments() {

		ArgumentDefinition olderFile = new ArgumentDefinition(OLDER_FILE, OLDER_FILE+"={filepath}",
				null,
				"The path to the older csv-file, which is used in the comparison.");
		new FileCanReadArgumentValidator(olderFile);
		this.addSupportedArgument(olderFile.getKey(), olderFile);
		
		//##################################################################################################
		
		ArgumentDefinition olderLabel = new ArgumentDefinition(OLDER_LABEL, OLDER_LABEL+"={label}",
				"older",
				"The label which is appended to the older columns in the results.");
		new StringLengthArgumentValidator(olderLabel, 1, -1);
		this.addSupportedArgument(olderLabel.getKey(), olderLabel);
		
		//##################################################################################################

		ArgumentDefinition olderDelimiter = new ArgumentDefinition(OLDER_DELIMITER, 
				OLDER_DELIMITER+"={delimiter}",
				",",
				"The delimiter which is used in the older file to delimit the data.");
		new StringLengthArgumentValidator(olderDelimiter, 1, -1);
		this.addSupportedArgument(olderDelimiter.getKey(), olderDelimiter);
		

		//##################################################################################################

		ArgumentDefinition olderQuotes = new ArgumentDefinition(OLDER_QUOTES, 
				OLDER_QUOTES+"={true|false}",
				"false",
				"Toggles handling of quoted values, including \\\\ and \\\" inside quotation. (does not work with \\\" outside of quotation.)");
		new BooleanArgumentValidator(olderQuotes);
		this.addSupportedArgument(olderQuotes.getKey(), olderQuotes);
		
		//##################################################################################################

		ArgumentDefinition youngerFile = new ArgumentDefinition(YOUNGER_FILE, 
				YOUNGER_FILE+"={filepath}",
				null,
				"The path to the younger csv-file, which is used in the comparison.");
		new FileCanReadArgumentValidator(youngerFile);
		this.addSupportedArgument(youngerFile.getKey(), youngerFile);
		
		//##################################################################################################

		ArgumentDefinition youngerLabel = new ArgumentDefinition(YOUNGER_LABEL, 
				YOUNGER_LABEL+"={label}",
				"younger",
				"The label which is appended to the younger columns in the results.");
		new StringLengthArgumentValidator(youngerLabel, 1, -1);
		this.addSupportedArgument(youngerLabel.getKey(), youngerLabel);
		
		//##################################################################################################

		ArgumentDefinition youngerDelimiter = new ArgumentDefinition(YOUNGER_DELIMITER, 
				YOUNGER_DELIMITER+"={delimiter}", 
				",", 
				"The delimiter which is used in the younger file to delimit the data.");
		new StringLengthArgumentValidator(youngerDelimiter, 1, -1);
		this.addSupportedArgument(youngerDelimiter.getKey(), youngerDelimiter);
		
		
		//##################################################################################################

		ArgumentDefinition youngerQuotes = new ArgumentDefinition(YOUNGER_QUOTES, 
				YOUNGER_QUOTES+"={true|false}",
				"false",
				"Toggles handling of quoted values, including \\\\ and \\\" inside quotation. (does not work with \\\" outside of quotation.)");
		new BooleanArgumentValidator(youngerQuotes);
		this.addSupportedArgument(youngerQuotes.getKey(), youngerQuotes);
		
		
		//##################################################################################################

		ArgumentDefinition resultFile = new ArgumentDefinition(RESULT_FILE, 
				RESULT_FILE+"={filepath}",
				"./CSVCompare_Result.csv",
				"The path to the result file, were the compared data should be written.");
		new FileCanWriteArgumentValidator(resultFile);
		this.addSupportedArgument(resultFile.getKey(), resultFile);
		
		//##################################################################################################

		ArgumentDefinition resultDelimiter = new ArgumentDefinition(RESULT_DELIMITER, 
				RESULT_DELIMITER+"={delimiter}",
				",",
				"The delimiter which is used in the result to delimit the data.");
		new StringLengthArgumentValidator(resultDelimiter, 1, -1);
		this.addSupportedArgument(resultDelimiter.getKey(), resultDelimiter);
		
		//##################################################################################################

		ArgumentDefinition resultCompareDiff = new ArgumentDefinition(RESULT_COMPAREDIFF, 
				RESULT_COMPAREDIFF+"={true|false}",
				"false",
				"Takes no effect if “-column.compareDef” is used. If true, for all columns the difference will be calculated. ");
		new BooleanArgumentValidator(resultCompareDiff);
		this.addSupportedArgument(resultCompareDiff.getKey(), resultCompareDiff);
		
		//##################################################################################################

		ArgumentDefinition resultCompareDiffPerc = new ArgumentDefinition(RESULT_COMPARE_DIFF_PERCENTAGE, 
				RESULT_COMPARE_DIFF_PERCENTAGE+"-result.comparediff%={true|false}",
				"true",
				"Takes no effect if “-column.compareDef” is used. If true, for all columns the difference in percentage will be calculated. ");
		new BooleanArgumentValidator(resultCompareDiffPerc);
		this.addSupportedArgument(resultCompareDiffPerc.getKey(), resultCompareDiffPerc);
		
		//##################################################################################################

		ArgumentDefinition resultCompareString = new ArgumentDefinition(RESULT_COMPARESTRING, 
				RESULT_COMPARESTRING+"={true|false}",
				"false",
				"Takes no effect if “-column.compareDef” is used. If true, for all columns the values will be compared as strings. If the strings are equal the result is “EQ”, otherwise the result is “NOTEQ”. ");
		new BooleanArgumentValidator(resultCompareString);
		this.addSupportedArgument(resultCompareString.getKey(), resultCompareString);
		
		//##################################################################################################

		ArgumentDefinition resultSort = new ArgumentDefinition(RESULT_SORT, 
				RESULT_SORT+"={true|false}",
				"true",
				"If true, the result will be sorted by the natural order of the identifierColumn. If false, the rows from both files are merged by alternately taking one row of each file. (see argument “-column.identifier” for more information).");
		new RegexArgumentValidator(resultSort, "true|false|onerror");
		this.addSupportedArgument(resultSort.getKey(), resultSort);
		
		//##################################################################################################

		ArgumentDefinition resultSTDOut = new ArgumentDefinition(RESULT_STDOUT, 
				RESULT_STDOUT+"={true|false|onerror}",
				"false",
				"If true, the result will be written to the standard output instead to a file. 'onerror' will print the result to standard output in case an error occured during writting the file. If you plan to pipe the output to another tool, set'-config.loglevel.console=OFF' so nothing else will be written to the standard output.");
		new BooleanArgumentValidator(resultSTDOut);
		this.addSupportedArgument(resultSTDOut.getKey(), resultSTDOut);
		
		//##################################################################################################

		ArgumentDefinition resultQuotes = new ArgumentDefinition(RESULT_QUOTES, 
				RESULT_QUOTES+"={true|false}",
				"false",
				"Toggles writing result with quotes.");
		new BooleanArgumentValidator(resultQuotes);
		this.addSupportedArgument(resultQuotes.getKey(), resultQuotes);
		
		//##################################################################################################

		ArgumentDefinition columnIdentifier = new ArgumentDefinition(COLUMN_IDENTIFIER, 
				COLUMN_IDENTIFIER+"={olderColumnHeader},{youngerColumnHeader}",
				"First column in both files.",
				"Set the identifier column for the older and the younger File. If this argument is not used, the first column in both files is considered as the identifier column.");
		new RegexArgumentValidator(columnIdentifier, "[^,]+,[^,]+"){
			@SuppressWarnings("unused") //used in other place
			public String getInvalidMessages() {
				return "The value for the argument "+COLUMN_IDENTIFIER+" does not match the pattern '{olderColumnHeader},{youngerColumnHeader}'";
			}
		};
		this.addSupportedArgument(columnIdentifier.getKey(), columnIdentifier);
		
		//##################################################################################################

		ArgumentDefinition identifierMakeUnique = new ArgumentDefinition(COLUMN_IDENTIFIER_MAKEUNIQUE, 
				COLUMN_IDENTIFIER_MAKEUNIQUE+"={true|false}",
				"true",
				"If true, the identifiers in both files are made unique, if one idenitifier is used multiple times.If this is turned off, only the first row with the identifier is used, all other rows are ignored. ");
		new BooleanArgumentValidator(identifierMakeUnique);
		this.addSupportedArgument(identifierMakeUnique.getKey(), identifierMakeUnique);
		
		//##################################################################################################

		ArgumentDefinition columnTopLevel = new ArgumentDefinition(COLUMN_TOPLEVEL, 
				COLUMN_TOPLEVEL+"={olderColumnHeader},{youngerColumnHeader},{Value}",
				null,
				"Set the topLevel column for the older and the younger File. The value is the string which is used to determine if a row is a topLevel.");
		
		new RegexArgumentValidator(columnTopLevel, "[^,]+,[^,]+,[^,]+"){
			@SuppressWarnings("unused") //used in other place
			public String getInvalidMessages() {
				return "The value for the argument "+COLUMN_TOPLEVEL+" does not match the pattern '{olderColumnHeader},{youngerColumnHeader},{Value}'";
			}
		};
		
		this.addSupportedArgument(columnTopLevel.getKey(), columnTopLevel);
		
		//##################################################################################################

		ArgumentDefinition columnCompareDef = new ArgumentDefinition(COLUMN_COMPARE_DEF, 
				COLUMN_COMPARE_DEF+"={olderColumnHeader}, {youngerColumnHeader},{method()},…",
				null,
				"Set a column definition for the manual comparison. You can specify as many methods as you want delimited by comma. For a list of methods you can use, refer to the section “Methods for Column Definitions”. This argument can be used zero to many times. ");
		new RegexArgumentValidator(columnCompareDef, "[^,]+,[^,]+,([^,]+\\(.*\\),)+"){
			@SuppressWarnings("unused") //used in other place
			public String getInvalidMessages() {
				return "The value for the argument "+COLUMN_COMPARE_DEF+" does not match the pattern '{olderColumnHeader},{youngerColumnHeader},{method()},...'";
			}
		};
		this.addSupportedArgument(columnCompareDef.getKey(), columnCompareDef);
	}


	/***********************************************************
	 * Loads the default Settings of the Arguments, removes 
	 * -column.identifier and all -column.compareDef definitions. This 
	 * method can be used when running multiple tests to reset
	 * the arguments.
	 * 
	 ***********************************************************/
	public void loadDefaultSettings() {

		loadedArguments = new LinkedHashMap<String,String>(); 
		compareDefinitions = new ArrayList<CompareDefinition>();
		
		loadedArguments.put(RESULT_FILE, 					supportedArgumentsMap.get(RESULT_FILE).getDefaultValue());
		loadedArguments.put(RESULT_DELIMITER,				supportedArgumentsMap.get(RESULT_DELIMITER).getDefaultValue());
		
		loadedArguments.put(OLDER_LABEL, 					supportedArgumentsMap.get(OLDER_LABEL).getDefaultValue());
		loadedArguments.put(OLDER_DELIMITER, 				supportedArgumentsMap.get(OLDER_DELIMITER).getDefaultValue());
		loadedArguments.put(OLDER_QUOTES, 					supportedArgumentsMap.get(OLDER_QUOTES).getDefaultValue());
		
		loadedArguments.put(YOUNGER_LABEL, 					supportedArgumentsMap.get(YOUNGER_LABEL).getDefaultValue());
		loadedArguments.put(YOUNGER_DELIMITER, 				supportedArgumentsMap.get(YOUNGER_DELIMITER).getDefaultValue());
		loadedArguments.put(YOUNGER_QUOTES, 				supportedArgumentsMap.get(YOUNGER_QUOTES).getDefaultValue());
		
		loadedArguments.put(RESULT_COMPAREDIFF, 			supportedArgumentsMap.get(RESULT_COMPAREDIFF).getDefaultValue());
		loadedArguments.put(RESULT_COMPARESTRING, 			supportedArgumentsMap.get(RESULT_COMPARESTRING).getDefaultValue());
		loadedArguments.put(RESULT_COMPARE_DIFF_PERCENTAGE, supportedArgumentsMap.get(RESULT_COMPARE_DIFF_PERCENTAGE).getDefaultValue());
		loadedArguments.put(RESULT_SORT, 					supportedArgumentsMap.get(RESULT_SORT).getDefaultValue());
		loadedArguments.put(RESULT_STDOUT, 					supportedArgumentsMap.get(RESULT_STDOUT).getDefaultValue());
		
		loadedArguments.put(COLUMN_IDENTIFIER_MAKEUNIQUE, 	supportedArgumentsMap.get(COLUMN_IDENTIFIER_MAKEUNIQUE).getDefaultValue());
		
		loadedArguments.put(CONFIG_LOGLEVEL_CONSOLE, 		supportedArgumentsMap.get(CONFIG_LOGLEVEL_CONSOLE).getDefaultValue());
		loadedArguments.put(CONFIG_LOGLEVEL_FILE, 			supportedArgumentsMap.get(CONFIG_LOGLEVEL_FILE).getDefaultValue());
		
	}

	/***********************************************************
	 * Resolves the command line arguments and stores them in
	 * the internal argument list.
	 * 
	 * @param argArray the arguments to resolve with the format
	 * "-{key}={value}"
	 * @throws ArgumentsException 
	 * 
	 ***********************************************************/
	@Override
	protected void resolveArguments(String[] argArray) throws ArgumentsException {
		
		this.loadLogLevels(argArray);
		
		//-------------------------------------------
		// Read the Arguments
		for(String argument : argArray){
			
			String argKey = "";
			String argValue = "";
			
			String[] splitted = argument.split("=");
			if(splitted.length == 2){
				argKey = splitted[0];
				argValue = splitted[1];
				
				if(!argKey.equals(COLUMN_COMPARE_DEF)){
					loadedArguments.put(argKey, argValue);
				}else{
					CompareDefinition definition = new CompareDefinition();
					definition.parseDefinition(argValue);
					compareDefinitions.add(definition);
				}
			}else{
				ArgumentsException exception = new ArgumentsException("Argument could not be loaded: "+argument);
				exception.setArgument(argument);
				throw exception;
			}
			
		}
	}
	

	/***********************************************************
	 * Print a list of readed column definitions to standard
	 * output.
	 * Will be executed if debug is enabled.
	 * 
	 ***********************************************************/
	public void printCompareDefinitions(){
		for(CompareDefinition definition : compareDefinitions){
			System.out.println("Compare definition: "+definition.getArgument());
		}
	}
	
	/***********************************************************
	 * Check if column definitions are defined.
	 * 
	 * @return true if at least one column definition is 
	 *         present
	 *         
	 ***********************************************************/
	public boolean hasColumnDefinitions(){
		return !compareDefinitions.isEmpty();
	}
	
	//####################################################################################
	// GETTERS & SETTERS
	//####################################################################################
	
	public ArrayList<CompareDefinition> getCompareDefinitions() {
		return compareDefinitions;
	}
	
	public void addCompareDefinitions(ArrayList<CompareDefinition> definitions) {
		compareDefinitions.addAll(definitions);
	}
	
	public static String getMethodForArgument(String argument){
		if(methodMap.containsKey(argument)){
			return methodMap.get(argument)[0];
		}
		return null;
	}

	public String getHeaderForArgument(String argument){
		if(methodMap.containsKey(argument)){
			return methodMap.get(argument)[1];
		}
		return null;
	}
	
}
