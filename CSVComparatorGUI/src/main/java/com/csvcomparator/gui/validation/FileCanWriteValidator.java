package com.csvcomparator.gui.validation;

import java.io.File;

import org.eclipse.swt.widgets.Control;

/*************************************************************************
 * This validator checks if it is possible to write to the path
 * returned by the method getStringToValidate.
 *  
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
public abstract class FileCanWriteValidator extends AbstractStringValidator {

	
	public FileCanWriteValidator(Control controlToDecorate,
			int decorationPosition) {
		super(controlToDecorate, decorationPosition);

	}
	
	@Override
	public String getInvalidMessage() {
		return "Cannot write to filepath: "+ getStringToValidate();
	}
	
	/*************************************************************************
	 * Checks if writing to the filepath returned by getStringToValidate is 
	 * possible.
	 *************************************************************************/ 
	@Override
	protected boolean runValidation() {
		
		String filepath = getStringToValidate();
		
		if(filepath != null){
			
			File f = new File(getStringToValidate());
			if(f.exists()){
				if(!f.canWrite()){
					return false;
				}
			}
		}else{
			return false;
		}
		
		return true;
	}

}
