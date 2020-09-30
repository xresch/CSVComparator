package com.csvcomparator.gui.validation;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.swt.widgets.Control;

/*************************************************************************
 * Validator to check if a string is a compilable regular expression.
 * 
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
public abstract class RegexCompilableValidator extends AbstractStringValidator {

	public RegexCompilableValidator(Control controlToDecorate,
			int decorationPosition) {
		super(controlToDecorate, decorationPosition);

	}
	
	@Override
	public String getInvalidMessage() {
		return "Regex could not be compiled: "+getStringToValidate();
	}
	

	/*************************************************************************
	 * Checks if the string returned by getStringToValidate is a compilable 
	 * regular expression.
	 * 
	 * @return boolean
	 *************************************************************************/ 	
	@Override
	protected boolean runValidation() {
		
		String regex = (getStringToValidate());
		
		try {
            Pattern.compile(regex);
        } catch (PatternSyntaxException exception) {
            return false;
        }
		
		return true;
		
	}

}
