package com.csvcomparator.gui.validation;

import java.io.File;

import org.eclipse.swt.widgets.Control;

/*************************************************************************
 * Validator to check if a String is a path to a readable file.
 * 
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
public abstract class FilepathCanReadValidator extends AbstractStringValidator {

	public FilepathCanReadValidator(Control controlToDecorate,
			int decorationPosition) {
		super(controlToDecorate, decorationPosition);

	}
	
	@Override
	public String getInvalidMessage() {
		return "File cannot not be read: "+ getStringToValidate();
	}

	/*************************************************************************
	 * Checks if the string returned by getStringToValidate is a path to a
	 * readable file.
	 * 
	 * @return boolean
	 *************************************************************************/ 	
	@Override
	protected boolean runValidation() {
		
		File f = new File(getStringToValidate());
		if(f.canRead()){
			return true;
		}
		
		return false;
	}

}
