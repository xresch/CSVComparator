package com.csvapi.arguments.validation;

import com.csvapi.arguments.ArgumentDefinition;

/**************************************************************************************
 * The StringLengthArgumentValidator will validate if the value of the ArgumentDefinition
 * has a certain lenght in a minimum and maximum range.
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public class StringLengthArgumentValidator extends AbstractArgumentValidator {

	private int minLength;
	private int maxLength;

	public StringLengthArgumentValidator(ArgumentDefinition argDefinition, int minLength, int maxLength) {
		super(argDefinition);
		this.minLength = minLength;
		this.maxLength = maxLength;
	}
	
	@Override
	public boolean validate(String value) {
		
		if(   (value.length() >= minLength || minLength == -1) 
		   && (value.length() >= maxLength || maxLength == -1) ){
			return true;
		}else{
			if(minLength == -1){
				this.setInvalidMessage("The value of the argument "+argDefinition.getKey()+
						" should be at maximum "+maxLength+" characters long.(value='"+value+"')");
			}else if(maxLength == -1){
				this.setInvalidMessage("The value of the argument "+argDefinition.getKey()+
						" should be at least "+minLength+" characters long.(value='"+value+"')");
			}else {
				this.setInvalidMessage("The value of the argument "+argDefinition.getKey()+
						" should be between "+minLength+" and "+maxLength+" characters long.(value='"+value+"')");
			}
			
			return false;
		}
		
	}

}
