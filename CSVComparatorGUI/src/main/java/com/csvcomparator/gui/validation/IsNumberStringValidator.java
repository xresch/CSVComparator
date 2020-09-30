package com.csvcomparator.gui.validation;

import org.eclipse.swt.widgets.Control;

/*************************************************************************
 * Validator to check if a String is a parsable number. 
 * 
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
public abstract class IsNumberStringValidator extends AbstractStringValidator {

	public IsNumberStringValidator(Control controlToDecorate,
			int decorationPosition) {
		super(controlToDecorate, decorationPosition);

	}
	
	@Override
	public String getInvalidMessage() {
		return "Please enter a valid number.";
	}
	
	/*************************************************************************
	 * Checks if the string returned by getStringToValidate is a parsable
	 * number.
	 * 
	 * @return boolean
	 *************************************************************************/ 	
	@Override
	protected boolean runValidation() {
		
		String text = getStringToValidate();
		
		try{
			Double.parseDouble(text);
		}catch(NumberFormatException e){
			return false;
		}
		
		return true;
		
	}

}
