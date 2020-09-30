package com.csvcomparator.gui.validation;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wb.swt.SWTResourceManager;

import com.csvcomparator.gui.DefaultComparisonComposite;

/*************************************************************************
 * The AbstractValidator implements parts of the IValidator interface.
 * Also it implements methods of ModifyListener, FocusListener and 
 * KeyListener. It is possible to give the validator a Control which
 * should be decorated in case the validation result is false.
 *  
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
public abstract class AbstractValidator implements IValidator, ModifyListener, FocusListener, KeyListener{

	private ControlDecoration controlDecoration;
	private String tag;
	private boolean lastResult = true;

	//####################################################################################
	// CONSTRUCTORS
	//####################################################################################
	/*************************************************************************
	 * Creates the AbstractValidator.
	 * 
	 * @param controlToDecorate a Control which should be decorated in case
	 * the validation result is false
	 * @param decorationPosition the position of the Decorator
	 *************************************************************************/ 
	public AbstractValidator(Control controlToDecorate, int decorationPosition){
		
		if(controlToDecorate != null){
			controlDecoration = new ControlDecoration(controlToDecorate, decorationPosition);
			controlDecoration.hide();
			controlDecoration.setImage(SWTResourceManager.getImage(DefaultComparisonComposite.class, "/org/eclipse/jface/fieldassist/images/error_ovr.gif"));
		}
	}
	
	//####################################################################################
	// ABSTRACT METHODS
	//####################################################################################
	/*************************************************************************
	 * Run validation will be implemented by the subclass and will contain
	 * the logic to run a validation.
	 *  
	 *************************************************************************/ 
	protected abstract boolean runValidation();

	//####################################################################################
	// CLASS METHODS
	//####################################################################################
	/*************************************************************************
	 * Executes the validation if the checkPrecondition() returns true.
	 * 
	 * @return true if validation was fine or the precondition for validation
	 * is not met, false otherwise
	 *************************************************************************/ 
	public final boolean validate(){
		
		if(checkPreconditions()){
			if(runValidation()){
				if(controlDecoration != null){
					controlDecoration.hide();
				}
				
				lastResult = true;
				return true;
				
			}else{
				if(controlDecoration != null){
					controlDecoration.setDescriptionText(getInvalidMessage());
					controlDecoration.show();
				}
				
				lastResult = false;
				return false;
			}
		}else{
			lastResult = false;
			return true;
		}

	}
	
	/*************************************************************************
	 * If the precondition for validation is met and this method returns true,
	 * the validation will be performed.
	 * Override this method if you want to check for preconditions.
	 * 
	 * @return true if validation should be performed.
	 *************************************************************************/ 
	public boolean checkPreconditions(){
		return true;
	}

	//####################################################################################
	// IMPLEMENTED LISTENER METHODS
	// ----------------------------
	// This implementations allow to add a validator to the standard listeners of 
	// the swt components.
	//####################################################################################
	@Override public void modifyText(ModifyEvent arg0)		{ validate(); }
	@Override public void focusLost(FocusEvent arg0)		{ validate(); }
	@Override public void focusGained(FocusEvent arg0)	 	{ /*do nothing*/ }
	@Override public void keyReleased(KeyEvent arg0) 		{ validate(); }
	@Override public void keyPressed(KeyEvent arg0) 		{ /*do nothing*/ }
	
	
	//####################################################################################
	// GETTERS & SETTERS
	//####################################################################################
	/*************************************************************************
	 * Sets the decorator image which should be displayed in case the 
	 * validation fails.
	 * 
	 * @param imagePath path to the decorator image
	 *************************************************************************/ 
	public void setDecoratorImage(String imagePath){
		controlDecoration.setImage(SWTResourceManager.getImage(DefaultComparisonComposite.class, imagePath));
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public boolean getLastResult() {
		return lastResult;
	}
}
