package com.csvapi.arguments.validation;

import com.csvapi.arguments.ArgumentDefinition;

/*************************************************************************
 * Validator interface used for arguments validation.
 *   
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
public interface IArgumentValidator {
	
	public boolean validate(String value);
	public String getInvalidMessage();
	public void setInvalidMessage(String message);
	public void setArgument(ArgumentDefinition argument);
	public ArgumentDefinition getArgument();
	
}
