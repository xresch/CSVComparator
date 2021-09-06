package com.csvapi.arguments.validation;

import com.csvapi.arguments.ArgumentDefinition;

/**************************************************************************************
 * The AbstractArgumentValidator provides some default implementation of the 
 * methods defined by the IArgumentValidator interface.
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public abstract class AbstractArgumentValidator implements IArgumentValidator {

	protected ArgumentDefinition argDefinition;
	private String invalidMessage="";
	
	public AbstractArgumentValidator(ArgumentDefinition argument){
		this.argDefinition = argument;
		argument.addValidator(this);
	}
	
	@Override
	public void setArgument(ArgumentDefinition argument) {
		this.argDefinition = argument;
	}

	@Override
	public ArgumentDefinition getArgument() {
		return argDefinition;
	}
	
	@Override
	public String getInvalidMessage(){
		return invalidMessage;
	}
	
	@Override
	public void setInvalidMessage(String message){
		this.invalidMessage = message;
	}


}
