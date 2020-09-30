package com.csvcomparator.gui.validation;

import org.eclipse.swt.widgets.Control;

/*************************************************************************
 * The AbstractStringValidator extends the AbstractValidator and
 * adds the abstract method getStringToValidate.
 *  
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
public abstract class AbstractStringValidator extends AbstractValidator{

	public AbstractStringValidator(Control controlToDecorate,
			int decorationPosition) {
		super(controlToDecorate, decorationPosition);
	}
	
	public abstract String getStringToValidate();
	
}
