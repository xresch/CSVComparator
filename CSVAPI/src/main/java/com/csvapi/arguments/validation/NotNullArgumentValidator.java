package com.csvapi.arguments.validation;

import com.csvapi.arguments.ArgumentDefinition;

/**************************************************************************************
 * The BooleanArgumentValidator will validate if the value of the ArgumentDefinition
 * is a string representation of "true" or "false".
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public class NotNullArgumentValidator extends AbstractArgumentValidator {

	
	public NotNullArgumentValidator(ArgumentDefinition argDefinition) {
		super(argDefinition);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean validate(String value) {
		
		if(value != null){
			return true;
		}else{
			this.setInvalidMessage("The value of the argument "+argDefinition.getKey()+" cannot be null, please specify the argument.");
			return false;
		}
		
	}

}
