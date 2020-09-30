package com.csvcomparator.gui.validation;

/*************************************************************************
 * Validator interface used for swt component validation.
 *   
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
public interface IValidator {
	public boolean validate();
	public String getInvalidMessage();
	public String getTag();
	public void setTag(String tag);
}
