package com.csvapi.transform;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.csvapi.arguments.AbstractArgumentsHandler;
import com.csvapi.arguments.ArgumentDefinition;
import com.csvapi.arguments.ArgumentsException;
import com.csvapi.arguments.validation.BooleanArgumentValidator;
import com.csvapi.arguments.validation.FileCanReadArgumentValidator;
import com.csvapi.arguments.validation.FileCanWriteArgumentValidator;
import com.csvapi.arguments.validation.NotNullArgumentValidator;
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
 * 																						<br>
 * -input.path={filepath}																<br>	
 * -input.format={xml}																	<br>
 * -input.filefilter={filter}															<br>
 * 																						<br>
 * -result.file={filepath}																<br>
 * -result.delimiter={delimiter}														<br>
 * 																						<br>
 * -result.sort={true|false}															<br>
 * -result.stdout={true|false|onerror}													<br>
 * -result.prefix={prefix}																<br>
 *
 * @author Reto Scheiwiller, 2014
 * 
 *********************************************************************************************/

public class TransformArguments extends AbstractArgumentsHandler {
	
	public static final String INPUT_PATH = "-input.path";
	public static final String INPUT_FORMAT = "-input.format";
	public static final String INPUT_FILEFILTER = "-input.filefilter";
	public static final String RESULT_FILE = "-result.file";
	public static final String RESULT_DELIMITER = "-result.delimiter";
	public static final String RESULT_SORT = "-result.sort";
	public static final String RESULT_STDOUT = "-result.stdout";
	public static final String RESULT_PREFIX = "-result.prefix";

	public static final Logger logger = LogManager.getLogger(TransformArguments.class.getName());

	//####################################################################################
	// CONSTRUCTORS
	//####################################################################################
	public TransformArguments() {
		
		initializeSupportedArguments();
		
		loadDefaultSettings();
	
	}

	//####################################################################################
	// CLASS METHODS
	//####################################################################################
	
	/***********************************************************
	 * Initialize the supported arguments. 
	 * 
	 ***********************************************************/
	private void initializeSupportedArguments() {

		ArgumentDefinition inputPath = new ArgumentDefinition(INPUT_PATH, 
				INPUT_PATH+"={filepath}",
				null,
				"The path to the input file or folder which should be transformed. "
			  + "If a folder is used, all files which match the regex set by -input.filefilter will be transformed.");
		
		new NotNullArgumentValidator(inputPath);
		new FileCanReadArgumentValidator(inputPath);
		this.addSupportedArgument(inputPath.getKey(), inputPath);
		
		
		//####################################################################################
		ArgumentDefinition inputFormat = new ArgumentDefinition(INPUT_FORMAT, 
				INPUT_FORMAT+"={xml}",
				"xml",
				"The format the input currently has an should be transformed into comma separated values.");
		
		new RegexArgumentValidator(inputFormat, "xml");
		this.addSupportedArgument(inputFormat.getKey(), inputFormat);
		
		
		//####################################################################################
		ArgumentDefinition inputFileFilter = new ArgumentDefinition(INPUT_FILEFILTER, 
				INPUT_FILEFILTER+"={regextoMatch}",
				".*",
				"Only files matching the specified regular expression will be transformed. "
			   +"The filter will be used only if a folder is specified with "+INPUT_PATH+".");
		
		//TODO RegexCompilableValidator
		this.addSupportedArgument(inputFileFilter.getKey(), inputFileFilter);
		
		
		//####################################################################################
		ArgumentDefinition resultFile = new ArgumentDefinition(RESULT_FILE, 
				RESULT_FILE+"={filepath}",
				"./transform_result.csv", 
				"The path to the result file, were the compared data should be written.");
		
		new FileCanWriteArgumentValidator(resultFile);
		this.addSupportedArgument(resultFile.getKey(), resultFile);
		
		
		//####################################################################################
		ArgumentDefinition resultDelimiter = new ArgumentDefinition(RESULT_DELIMITER, 
				RESULT_DELIMITER+"={delimiter}",
				",",
				"The delimiter which is used in the result to delimit the data.");
		new StringLengthArgumentValidator(resultDelimiter, 1, -1);
		this.addSupportedArgument(resultDelimiter.getKey(), resultDelimiter);
		
		
		//####################################################################################
		ArgumentDefinition resultSort = new ArgumentDefinition(RESULT_SORT, 
				RESULT_SORT+"={true|false}",
				"true",
				"If true, the result will be sorted by the natural order of the identifierColumn. If false, the rows from both files are merged by alternately taking one row of each file. (see argument “-column.identifier” for more information).");
		new BooleanArgumentValidator(resultSort);
		this.addSupportedArgument(resultSort.getKey(), resultSort);
		
		
		//####################################################################################
		ArgumentDefinition resultSTDOut = new ArgumentDefinition(RESULT_STDOUT, 
				RESULT_STDOUT+"={true|false|onerror}",
				"false",
				"If true, the result will be written to the standard output instead to a file. 'onerror' will print the result to standard output in case an error occured during writting the file. If you plan to pipe the output to another tool, set'-config.loglevel.console=OFF' so nothing else will be written to the standard output.");
		new RegexArgumentValidator(resultSTDOut, "true|false|onerror");
		this.addSupportedArgument(resultSTDOut.getKey(), resultSTDOut);
		
		
		//####################################################################################
		ArgumentDefinition resultPrefix = new ArgumentDefinition(RESULT_PREFIX, 
				RESULT_PREFIX+"={prefix}",
				"",
				"This value will be used as a prefix in the column 'Key' in the result file for all results.");
		this.addSupportedArgument(resultPrefix.getKey(), resultPrefix);
		
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
		
		loadedArguments.put(INPUT_FORMAT, 			supportedArgumentsMap.get(INPUT_FORMAT).getDefaultValue());
		loadedArguments.put(INPUT_FILEFILTER, 		supportedArgumentsMap.get(INPUT_FILEFILTER).getDefaultValue());
		
		loadedArguments.put(RESULT_FILE,			supportedArgumentsMap.get(RESULT_FILE).getDefaultValue());
		loadedArguments.put(RESULT_DELIMITER, 		supportedArgumentsMap.get(RESULT_DELIMITER).getDefaultValue());
		loadedArguments.put(RESULT_PREFIX, 			supportedArgumentsMap.get(RESULT_PREFIX).getDefaultValue());
		loadedArguments.put(RESULT_SORT, 			supportedArgumentsMap.get(RESULT_SORT).getDefaultValue());
		loadedArguments.put(RESULT_STDOUT, 			supportedArgumentsMap.get(RESULT_STDOUT).getDefaultValue());
		
		loadedArguments.put(CONFIG_LOGLEVEL_CONSOLE, supportedArgumentsMap.get(CONFIG_LOGLEVEL_CONSOLE).getDefaultValue());
		loadedArguments.put(CONFIG_LOGLEVEL_FILE, 	supportedArgumentsMap.get(CONFIG_LOGLEVEL_FILE).getDefaultValue());
		
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
				
				loadedArguments.put(argKey, argValue);
				logger.debug("Argument Loaded >> Key="+argKey+", Value="+argValue);	
			}else{
				ArgumentsException exception = new ArgumentsException("Argument could not be loaded: "+argument);
				exception.setArgument(argument);
				throw exception;
			}
			
		}
	}
	
	/***********************************************************
	 * Returns true if all arguments were correct, false otherwise.
	 ***********************************************************/
	@Override
	public boolean validateArguments(){
		
		invalidMessages = new ArrayList<String>();
		
		if(loadedArguments.containsKey(INPUT_PATH)){
			return super.validateArguments();
		}else{
			invalidMessages.add("Please specify the argument '"+INPUT_PATH+"'");
			return false;
		}
		
	}
}
