package com.csvcomparator.gui.validation;

import org.eclipse.swt.widgets.Control;

/*************************************************************************
 * Validator to check if a string is not null or an empty string.
 * 
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
public abstract class NotNullStringValidator extends AbstractStringValidator {

	public NotNullStringValidator(Control controlToDecorate,
			int decorationPosition) {
		super(controlToDecorate, decorationPosition);

	}
	
	@Override
	public String getInvalidMessage() {
		return "This value is mandatory.";
	}
	
	/*************************************************************************
	 * Checks if the string returned by getStringToValidate is not null and 
	 * is not an empty string.
	 * 
	 * @return boolean
	 *************************************************************************/ 	
	@Override
	protected boolean runValidation() {
		
		String text = getStringToValidate();
		
		if(!text.equals("") && text != null){
			return true;
		}
		
		return false;
		
	}

}
