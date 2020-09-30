package com.csvcomparator.gui.validation;

import org.eclipse.swt.widgets.Control;

/*************************************************************************
 * The AbstractNumberValidator extends the AbstractValidator and
 * adds the abstract method getNumberToValidate(). 
 *  
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
public abstract class AbstractNumberValidator extends AbstractValidator{

	public AbstractNumberValidator(Control controlToDecorate,
			int decorationPosition) {
		super(controlToDecorate, decorationPosition);
	}
	
	public abstract Number getNumberToValidate();
	
}
