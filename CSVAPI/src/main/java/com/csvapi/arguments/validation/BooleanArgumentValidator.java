package com.csvapi.arguments.validation;

import com.csvapi.arguments.ArgumentDefinition;

/**************************************************************************************
 * The BooleanArgumentValidator will validate if the value of the ArgumentDefinition
 * is a string representation of "true" or "false".
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public class BooleanArgumentValidator extends AbstractArgumentValidator {

	
	public BooleanArgumentValidator(ArgumentDefinition argDefinition) {
		super(argDefinition);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean validate(String value) {
		
		if(value.toLowerCase().matches("true|false")){
			return true;
		}else{
			this.setInvalidMessage("The value of the argument "+argDefinition.getKey()+" is not a boolean value.(value='"+value+"')");
			return false;
		}
		
	}

}
