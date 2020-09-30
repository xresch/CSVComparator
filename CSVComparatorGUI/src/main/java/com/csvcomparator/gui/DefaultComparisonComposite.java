package com.csvcomparator.gui;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.csvapi.compare.CompareArguments;
import com.csvapi.compare.CompareArgumentsBuilder;
import com.csvapi.utils.Log4jUtils;
import com.csvcomparator.cli.main.CSVComparatorCLI;
import com.csvcomparator.gui.validation.ValidatorEngine;

/*************************************************************************
 * This class represents the content of the tab "Default Comparison".
 * 
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
public class DefaultComparisonComposite extends Composite {

	private Button compareButton;
	private Button compareAsStringsCheckbox;
	private Button compareDiffPercentCheckbox;
	private Button compareDiffCheckbox;
	private ControlDecoration controlDecoration;
	private ControlDecoration diffPercentDeco;
	private ControlDecoration compareAsStringDeco;
	
	public static final Logger logger = LogManager.getLogger(DefaultComparisonComposite.class.getName());

	//####################################################################################
	// CONSTRUCTORS
	//####################################################################################
	public DefaultComparisonComposite(Composite parent, int style) {
		super(parent, style);

		//------------------------------------------------
		// Set GridLayout
		//------------------------------------------------
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginBottom = 5;
		gridLayout.marginRight = 5;
		gridLayout.marginTop = 5;
		gridLayout.marginLeft = 5;
		this.setLayout(gridLayout);
		
		//------------------------------------------------
		// Create UI Parts
		//------------------------------------------------
		createDescriptionLabel();
		
		createCheckboxes();
		
		createCompareButton();
		
		setSWTTestIDs();
		
	}

	//####################################################################################
	// CLASS METHODS
	//####################################################################################
	/*************************************************************************
	 * Creates the label with the description.
	 *************************************************************************/ 
	private void createDescriptionLabel() {
		Label descriptionLabel = new Label(this, SWT.WRAP);
		GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_label.heightHint = 50;
		descriptionLabel.setLayoutData(gd_label);
		descriptionLabel.setText("In the default comparison mode the columns which have the same header from both files will be taken and compared."
							   + " Columns which are not found in both files are ignored.");
	}

	/*************************************************************************
	 * Creates the checkboxes.
	 *************************************************************************/ 
	private void createCheckboxes() {
		//------------------------------------------------
		// Checkbox: Compare Difference
		//------------------------------------------------
		compareDiffCheckbox = new Button(this, SWT.CHECK);
		compareDiffCheckbox.setText("compare Difference");
		
		controlDecoration = new ControlDecoration(compareDiffCheckbox, SWT.LEFT | SWT.TOP);
		controlDecoration.setImage(SWTResourceManager.getImage(DefaultComparisonComposite.class, "/org/eclipse/jface/fieldassist/images/info_ovr.gif"));
		controlDecoration.setDescriptionText( "The values will be compared as numbers.\n"
											+ "The absolute difference will be calculated.");
		
		//------------------------------------------------
		// Checkbox: Compare Difference %
		//------------------------------------------------
		compareDiffPercentCheckbox = new Button(this, SWT.CHECK);
		compareDiffPercentCheckbox.setText("compare Difference %");
		compareDiffPercentCheckbox.setSelection(true);
		
		diffPercentDeco = new ControlDecoration(compareDiffPercentCheckbox, SWT.LEFT | SWT.TOP);
		diffPercentDeco.setImage(SWTResourceManager.getImage(DefaultComparisonComposite.class, "/org/eclipse/jface/fieldassist/images/info_ovr.gif"));
		diffPercentDeco.setDescriptionText("The values will be compared as numbers.\n "
											 + "The difference in percentage will be calculated.");
		
		//------------------------------------------------
		// Checkbox: Compare as String
		//------------------------------------------------
		compareAsStringsCheckbox = new Button(this, SWT.CHECK);
		compareAsStringsCheckbox.setText("compare as String");
		
		compareAsStringDeco = new ControlDecoration(compareAsStringsCheckbox, SWT.LEFT | SWT.TOP);
		compareAsStringDeco.setImage(SWTResourceManager.getImage(DefaultComparisonComposite.class, "/org/eclipse/jface/fieldassist/images/info_ovr.gif"));
		compareAsStringDeco.setDescriptionText("The values will be compared as strings.\n"
												+ "If the strings are equal the result is “EQ”,\n"
												+ "otherwise the result is “NOTEQ”.");
	}

	/*************************************************************************
	 * Creates the compare button
	 *************************************************************************/ 
	private void createCompareButton() {
		compareButton = new Button(this, SWT.PUSH);
		compareButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, true, 1, 1));
		compareButton.setText("Compare");
		compareButton.addSelectionListener(new DefaultCompareListener());
	}
	
	/*************************************************************************
	 * Loads the configuration from the provided arguments.
	 * 
	 * @param arguments the arguments which should be loaded.
	 *************************************************************************/ 
	public void loadConfig(CompareArguments arguments) {
		this.setCompareDiff(arguments.getValue(CompareArguments.RESULT_COMPAREDIFF));
		this.setCompareDiffPercent(arguments.getValue(CompareArguments.RESULT_COMPARE_DIFF_PERCENTAGE));
		this.setCompareAsStrings(arguments.getValue(CompareArguments.RESULT_COMPARESTRING));
	}

	/*************************************************************************
	 * This method will set all ids for SWTBot testing.
	 * Only call after all UI components are instantiated.
	 * 
	 *************************************************************************/ 
	private void setSWTTestIDs() {
		
		String label = "Default Comparison";
		
		//set keys for SWTBot testing
		compareButton.setData("org.eclipse.swtbot.widget.key", label+".compareButton");
		compareAsStringsCheckbox.setData("org.eclipse.swtbot.widget.key", label+".compareAsStringsCheckbox");
		compareDiffCheckbox.setData("org.eclipse.swtbot.widget.key", label+".compareDiffCheckbox");
		compareDiffPercentCheckbox.setData("org.eclipse.swtbot.widget.key", label+".compareDiffPercentCheckbox");
	
	}
	
	//####################################################################################
	// GETTERS & SETTERS
	//####################################################################################
	public boolean getCompareDiff(){
		return compareDiffCheckbox.getSelection();
	}
	
	public boolean getCompareDiffPercent(){
		return compareDiffPercentCheckbox.getSelection();
	}
	
	public boolean getCompareAsString(){
		return compareAsStringsCheckbox.getSelection();
	}

	public CompareArgumentsBuilder addArgumentsToBuilder(CompareArgumentsBuilder builder){
		
		builder.setResultCompareDiff(getCompareDiff())
				.setResultCompareDiffPercentage(getCompareDiffPercent())
				.setResultCompareString(getCompareAsString());
		
		return builder;
	}
	
	public ArrayList<String> getArguments(){
		
		ArrayList<String> arguments = new ArrayList<String>();
		
		arguments.add(CompareArguments.RESULT_COMPAREDIFF	+"="+ getCompareDiff());
		arguments.add(CompareArguments.RESULT_COMPARE_DIFF_PERCENTAGE	+"="+ getCompareDiffPercent());
		arguments.add(CompareArguments.RESULT_COMPARESTRING	+"="+ getCompareAsString());
		
		return arguments;
	}
	
	public void setCompareDiff(String compareDiff){
		if(compareDiff != null)
			if(compareDiff.toLowerCase().equals("true")){
				this.setCompareDiff(true);
			}else{
				this.setCompareDiff(false);
			}
	}
	
	public void setCompareDiff(Boolean sortResult){
		compareDiffCheckbox.setSelection(sortResult);
	}
	
	public void setCompareDiffPercent(String compareDiffPercent){
		if(compareDiffPercent != null)
			if(compareDiffPercent.toLowerCase().equals("true")){
				this.setCompareDiffPercent(true);
			}else{
				this.setCompareDiffPercent(false);
			}
	}
	
	public void setCompareDiffPercent(Boolean sortResult){
		compareDiffPercentCheckbox.setSelection(sortResult);
	}
	
	public void setCompareAsStrings(String compareAsStrings){
		if(compareAsStrings != null)
			if(compareAsStrings.toLowerCase().equals("true")){
				this.setCompareAsStrings(true);
			}else{
				this.setCompareAsStrings(false);
			}
	}
	
	public void setCompareAsStrings(Boolean sortResult){
		compareAsStringsCheckbox.setSelection(sortResult);
	}

	//####################################################################################
	// INNER CLASSES
	//####################################################################################
	/*************************************************************************
	 * This listener will trigger the default comparison.
	 *************************************************************************/ 
	class DefaultCompareListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent arg0) {
			
			
			ValidatorEngine validatorEngine = CSVComparatorGUI.getValidatorEngine();
			
			//------------------------------------------------
			// Do Precheck
			//------------------------------------------------
			if(!validatorEngine.validateMatchingTag("PRECHECK")){
				MessageDialog.openError(Display.getCurrent().getActiveShell(), 
						"Error", 
						"ERROR: \n\t" 
						+ validatorEngine.getInvalidMessages() 
						+ "\n\n Please check if the result file is not in use and the filepath is valid."
						);
			}
	
			//------------------------------------------------
			// Do Validate and Compare
			//------------------------------------------------
			if(validatorEngine.validateMatchingTag("ALWAYS")){
				ArrayList<String> arguments = CSVComparatorGUI.getArgumentsBuilder().toArrayListNoCompareDefinitions();
				
				Log4jUtils.setNewRequestIDForThread(true);
				logger.info("START - Default Comparison"); long startTime = System.nanoTime();
				
					CSVComparatorCLI.main(arguments.toArray(new String [arguments.size()]));
				
				logger.info("END - Default Comparison (Duration="+( (System.nanoTime()-startTime) / 1000000)+"ms)");
				
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), 
						"Done", "Compare finished!");
			}else{
				MessageDialog.openError(Display.getCurrent().getActiveShell(), 
										"Error", 
										"Please correct the following issues:\n"+ validatorEngine.getInvalidMessages());
			}
		}
	}
	
}
