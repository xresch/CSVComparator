package com.csvapi.arguments;

import java.util.ArrayList;

import com.csvapi.arguments.validation.IArgumentValidator;

/**************************************************************************************
 * The ArgumentDefinition represents an argument with a key value pair.
 * It contains the default value, syntax and a description of the argument.
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public class ArgumentDefinition {
	
	private String key = "";
	private String syntax = "";
	private String defaultValue = "";
	private String description = "";
	
	private ArrayList<IArgumentValidator> validatorArray = new ArrayList<IArgumentValidator>();
	private ArrayList<String> invalidMessages;

	//####################################################################################
	// CONSTRUCTORS
	//####################################################################################
	public ArgumentDefinition(String key, String syntax, String defaultValue, String description){
		
		this.key = key;
		this.syntax = syntax;
		this.defaultValue = defaultValue;
		this.description = description;
	}
	
	//####################################################################################
	// CLASS METHODS
	//####################################################################################
	
	/*************************************************************************
	 * Executes all validators added to the argument.
	 * 
	 * @return true if all validators returned true, false otherwise
	 *************************************************************************/ 
	public boolean validateValue(String value){
		
		boolean isValid = true;
		invalidMessages = new ArrayList<String>();
		
		for(IArgumentValidator validator : validatorArray){
			
			if(!validator.validate(value)){
				invalidMessages.add(validator.getInvalidMessage());
				
				isValid=false;
			}
		}
		
		return isValid;
	}
	
	//####################################################################################
	// GETTERS & SETTERS
	//####################################################################################
	/*************************************************************************
	 * Returns all the InvalidMessages from the last validation execution. 
	 *************************************************************************/ 
	public ArrayList<String> getInvalidMessages() {
		return invalidMessages;
	}
	
	
	public boolean addValidator(IArgumentValidator e) {
		return validatorArray.add(e);
	}

	public boolean removeValidator(IArgumentValidator o) {
		return validatorArray.remove(o);
	}
	
	
	public String getKey() {
		return key;
	}

	public String getSyntax() {
		return syntax;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getDescription() {
		return description;
	}
	
	
}
