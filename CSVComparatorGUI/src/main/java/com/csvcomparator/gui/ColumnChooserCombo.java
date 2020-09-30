package com.csvcomparator.gui;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;

import com.csvcomparator.gui.validation.AbstractValidator;
import com.csvcomparator.gui.validation.NotNullStringValidator;

/*************************************************************************
 * ColumnChooserCombo is a combobox which loads the headers from a 
 * FileGroup as its values to provide the possibility to choose the 
 * headers of the CSV file.
 * 
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
public class ColumnChooserCombo extends Composite {

	private HeadersListener headersListener = new HeadersListener();
	private FileGroup source;
	private AbstractValidator validator;
	private ControlDecoration comboDeco;
	private Combo innerCombo;
	
	//####################################################################################
	// CONSTRUCTORS
	//####################################################################################
	public ColumnChooserCombo(Composite parent, int style, FileGroup source) {
		
		super(parent, style);	
		this.source = source;
		
		GridData gd_this = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_this.widthHint = 100;
		gd_this.horizontalIndent = 5;
		this.setLayoutData(gd_this);
		
		this.setLayout(new FillLayout());
		
		//---------------------------------------
		// Create Combo
		//---------------------------------------
		innerCombo = new Combo(this, SWT.NONE | SWT.READ_ONLY);
		comboDeco = new ControlDecoration(innerCombo, SWT.LEFT | SWT.TOP);
		comboDeco.setImage(SWTResourceManager.getImage(ColumnChooserCombo.class, "/org/eclipse/jface/fieldassist/images/info_ovr.gif"));
		comboDeco.setDescriptionText("Choose a column.");
		
		//---------------------------------------
		// Get Headers from FileGroup
		//---------------------------------------
		innerCombo.setItems(source.getHeaders());
		
		//---------------------------------------
		// Add Listener for Header Change
		//---------------------------------------
		source.addHeadersChangeListener(headersListener);
		
		//---------------------------------------
		// Add Validator
		//---------------------------------------
		validator = new NotNullStringValidator(this, SWT.BOTTOM | SWT.LEFT) {
			
			@Override
			public String getStringToValidate() {
				return innerCombo.getText();
			}
		};
		innerCombo.addModifyListener(validator);
		
		CSVComparatorGUI.getValidatorEngine().addValidator(validator);
	}
	
	//####################################################################################
	// METHODS
	//####################################################################################	
	
	/*************************************************************************
	 * Disposes the component and removes all listeners and validators.
	 *************************************************************************/ 
	@Override
	public void dispose(){
		source.removeHeadersChangeListener(headersListener);
		CSVComparatorGUI.getValidatorEngine().removeValidator(validator);
		super.dispose();
	}
	
	//####################################################################################
	// GETTERS & SETTERS
	//####################################################################################	

	public void setDescriptionText(String text){
		comboDeco.setDescriptionText(text);
	}
	
	public void toggleDecoratorVisible(boolean isVisible){
		
		if(isVisible){
			comboDeco.show() ; 
		}else{ 
			comboDeco.hide();
		}
	}
	
	public void setTag(String tag){
		validator.setTag(tag);
	}
	
	public String getText(){
		return innerCombo.getText();
	}
	
	public void setText(String text){
		innerCombo.setText(text);
	}
	
	public Combo getCombo() {
		return innerCombo;
	}

	public void setValidator(AbstractValidator validator){
		
		//remove current
		innerCombo.removeModifyListener(this.validator);
		CSVComparatorGUI.getValidatorEngine().removeValidator(this.validator);
		
		//set new
		this.validator = validator;
		innerCombo.addModifyListener(validator);
		CSVComparatorGUI.getValidatorEngine().addValidator(validator);	
	}
	
	//####################################################################################
	// INNER CLASSES
	//####################################################################################
	private final class HeadersListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			
			String currentSelection = innerCombo.getText();
			innerCombo.setItems(source.getHeaders());
			innerCombo.setText(currentSelection);
			
		}
	}
}