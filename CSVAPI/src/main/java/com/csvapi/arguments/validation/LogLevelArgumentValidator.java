package com.csvapi.arguments.validation;

import com.csvapi.arguments.ArgumentDefinition;

/**************************************************************************************
 * The LogLevelArgumentValidator will validate if the value of the ArgumentDefinition
 * is a valid log4j2 log level.
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public class LogLevelArgumentValidator extends AbstractArgumentValidator {

	
	public LogLevelArgumentValidator(ArgumentDefinition argDefinition) {
		super(argDefinition);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean validate(String value) {
		
		if(value.toUpperCase().matches("ALL|TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF"))
			return true;
		else
			this.setInvalidMessage("The value of the argument "+argDefinition.getKey()+" is not a valid log4j2 log level.(value='"+value+"')");
			return false;
		
	}

}
