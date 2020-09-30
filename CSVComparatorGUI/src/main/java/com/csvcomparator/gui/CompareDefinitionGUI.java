package com.csvcomparator.gui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.csvapi.compare.CompareArguments;
import com.csvapi.compare.CompareDefinition;
import com.csvcomparator.gui.validation.IValidator;
import com.csvcomparator.gui.validation.IsNumberStringValidator;

/*************************************************************************
 * This class is used to hold the UI elements for a compare definition.
 * It provides methods to create a -column.compareDef argument and load
 * them into the definition.
 * 
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
public class CompareDefinitionGUI{
	

	private static final String CALC_MULTIPLY_YOUNGER 	= "multiply Younger by";
	private static final String CALC_MULTIPLY_OLDER 	= "multiply Older by";
	private static final String CALC_DIVIDE_YOUNGER 	= "divide Younger by";
	private static final String CALC_DIVIDE_OLDER 		= "divide Older by";
	private static final String CALC_NOTHING 			= "nothing";
	
	private static final String PRINT_OLDER_THAN_YOUNGER 	= "Older than Younger";
	private static final String PRINT_YOUNGER_THAN_OLDER 	= "Younger than Older";
	private static final String PRINT_OLDER_ONLY 			= "Older only";
	private static final String PRINT_YOUNGER_ONLY 			= "Younger only";
	private static final String PRINT_MERGED 				= "Merge Older/Younger";
	private static final String PRINT_NOTHING 				= "nothing";
	
	private ColumnChooserCombo 		olderColumnCombo;
	private ColumnChooserCombo 		youngerColumnCombo;
	
	private Label 		deleteButton;
	private Combo 		printCombo;
	private Composite 	calculationComposite;
	private Combo 		calculationCombo;
	private Button 		compareDiffCheckbox;
	private Button 		compareDiffPercentageCheckbox;
	private Button 		compareAsStringsCheckbox;
	private Button 		printEmptyColumnCheckbox;
	private Text 		calculationNumberField;
	
	private Composite parent;
	
	private LinkedHashMap<String, String> printMap = new LinkedHashMap<String, String>();
	private LinkedHashMap<String, String> calculationMap = new LinkedHashMap<String, String>();
	private ArrayList<ChangeListener> deleteListenerArray = new ArrayList<ChangeListener>();

	private final CompareDefinitionGUI INSTANCE;
	
	private ArrayList<IValidator> validatorArray = new ArrayList<IValidator>();
	
	
	//####################################################################################
	// CONSTRUCTORS
	//####################################################################################
	
	public CompareDefinitionGUI(Composite parent){
		
		INSTANCE = this;
		this.parent = parent;
		
		initializeMaps();

		createDeleteButton(parent);
		
		createOlderCombo(parent);
		
		createYoungerCombo(parent);	

		createCalculationComposite(parent);
		
		createPrintCombo(parent);
		
		createCheckboxes(parent);
		
	}

	//####################################################################################
	// CLASS METHODS
	//####################################################################################
	
	/*************************************************************************
	 * Initializes the printMap and calculationMap.
	 *************************************************************************/ 
	private void initializeMaps() {
		
		//------------------------------------------------
		// initialize PrintMap
		//------------------------------------------------
		printMap.put(PRINT_OLDER_THAN_YOUNGER, "printOlder(),printYounger()");
		printMap.put(PRINT_YOUNGER_THAN_OLDER, "printYounger(),printOlder()");
		printMap.put(PRINT_OLDER_ONLY, "printOlder()");
		printMap.put(PRINT_YOUNGER_ONLY, "printYounger()");
		printMap.put(PRINT_MERGED, "printMerged()");
		printMap.put(PRINT_NOTHING, "");
		
		//------------------------------------------------
		// initialize CalculationMap
		//------------------------------------------------
		calculationMap.put(CALC_NOTHING, "");
		calculationMap.put(CALC_DIVIDE_OLDER, CompareDefinition.DIVIDE_OLDER);
		calculationMap.put(CALC_DIVIDE_YOUNGER, CompareDefinition.DIVIDE_YOUNGER);
		calculationMap.put(CALC_MULTIPLY_OLDER, CompareDefinition.MULTIPLY_OLDER);
		calculationMap.put(CALC_MULTIPLY_YOUNGER, CompareDefinition.MULTIPLY_YOUNGER);
	}

	/*************************************************************************
	 * Creates the blue delete button. 
	 * @param parent the parent composite
	 *************************************************************************/ 
	private void createDeleteButton(Composite parent) {
		//------------------------------------------------
		// Delete Button
		//------------------------------------------------
		deleteButton = new Label(parent, SWT.NONE);
		GridData gd_deleteButton = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_deleteButton.widthHint = 25;
		deleteButton.setLayoutData(gd_deleteButton);
		
		deleteButton.setImage(SWTResourceManager.getImage(CompareDefinitionGUI.class, "/com/csvcomparator/gui/images/icon_delete.png"));
		deleteButton.addMouseListener(new DeleteListener());
	}

	/*************************************************************************
	 * Creates the column chooser combo for the older file. 
	 * @param parent the parent composite
	 *************************************************************************/ 
	private void createOlderCombo(Composite parent) {
		//------------------------------------------------
		// older Column Combo
		//------------------------------------------------
		olderColumnCombo = new ColumnChooserCombo(parent, SWT.NONE | SWT.READ_ONLY, CSVComparatorGUI.getOlderFileGroup());
		//olderColumnCombo.setDescriptionText("Choose the column of the older file used for this comparison.");
		olderColumnCombo.toggleDecoratorVisible(false);
		olderColumnCombo.setTag("MANUALCOMPARE");
	}

	/*************************************************************************
	 * Creates the column chooser combo for the younger file. 
	 * @param parent the parent composite
	 *************************************************************************/ 
	private void createYoungerCombo(Composite parent) {
		//------------------------------------------------
		// younger Column Combo
		//------------------------------------------------
		youngerColumnCombo = new ColumnChooserCombo(parent, SWT.NONE | SWT.READ_ONLY, CSVComparatorGUI.getYoungerFileGroup());
		//youngerColumnCombo.setDescriptionText("Choose the column of the younger file used for this comparison.");
		youngerColumnCombo.toggleDecoratorVisible(false);
		youngerColumnCombo.setTag("MANUALCOMPARE");
	}

	/*************************************************************************
	 * Creates the calculation composite with a combo box and a number field.
	 * @param parent the parent composite
	 *************************************************************************/ 
	private void createCalculationComposite(Composite parent) {
		//------------------------------------------------
		// Calculation Composite
		//------------------------------------------------
		calculationComposite = new Composite(parent, SWT.BORDER);
		GridLayout gl_calculationComposite = new GridLayout(2, false);
		gl_calculationComposite.marginLeft = 5;
		calculationComposite.setLayout(gl_calculationComposite);
		GridData gd_calculationComposite = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_calculationComposite.widthHint = 190;
		calculationComposite.setLayoutData(gd_calculationComposite);

		//------------------------------------------------
		// Calculation Combo
		//------------------------------------------------
		calculationCombo = new Combo(calculationComposite, SWT.NONE | SWT.READ_ONLY);
		calculationCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		calculationCombo.setItems(calculationMap.keySet().toArray(new String[calculationMap.size()]));
		calculationCombo.select(0);
		
//		ControlDecoration calculationComboDeco = new ControlDecoration(calculationCombo, SWT.LEFT | SWT.TOP);
//		calculationComboDeco.setImage(SWTResourceManager.getImage(FileGroup.class, "/org/eclipse/jface/fieldassist/images/info_ovr.gif"));
//		calculationComboDeco.setDescriptionText("Before comparing you can perform calculations on the values in a column."
//												+ "\nE.g. When you have to compare files with data made in timeframes which are not the same size.");
		
		//------------------------------------------------
		// Calculation Number Field
		//------------------------------------------------
		calculationNumberField = new Text(calculationComposite, SWT.BORDER);
		GridData gd_calculationNumberField = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_calculationNumberField.widthHint = 30;
		calculationNumberField.setLayoutData(gd_calculationNumberField);
		
		IsNumberStringValidator calculationNumberValidator = 
					new IsNumberStringValidator(calculationCombo, SWT.BOTTOM | SWT.LEFT) {
						
						@Override
						public boolean checkPreconditions(){
							
							//only validate if calculation method was selected
							if(!calculationCombo.getText().equals(CALC_NOTHING)){
								return true;
							}else{
								return false;
							}
						}
						
						@Override
						public String getStringToValidate() {
							
							return calculationNumberField.getText();
						}
					};
		
		calculationNumberField.addModifyListener(calculationNumberValidator);
		
		calculationNumberValidator.setTag("MANUALCOMPARE");
		CSVComparatorGUI.getValidatorEngine().addValidator(calculationNumberValidator);
		validatorArray.add(calculationNumberValidator);
	}

	/*************************************************************************
	 * Creates the print Combo.
	 * @param parent the parent composite
	 *************************************************************************/ 
	private void createPrintCombo(Composite parent) {
		//------------------------------------------------
		// printCombo
		//------------------------------------------------
		printCombo = new Combo(parent, SWT.NONE | SWT.READ_ONLY);
		GridData gd_printCombo = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_printCombo.widthHint = 120;
		gd_printCombo.horizontalIndent = 5;
		printCombo.setLayoutData(gd_printCombo);
		printCombo.setItems(printMap.keySet().toArray(new String[printMap.size()]));
		printCombo.select(0);
		
//		ControlDecoration printComboDeco = new ControlDecoration(printCombo, SWT.LEFT | SWT.TOP);
//		printComboDeco.setImage(SWTResourceManager.getImage(FileGroup.class, "/org/eclipse/jface/fieldassist/images/info_ovr.gif"));
//		printComboDeco.setDescriptionText("Select how the chosen older and younger column should be printed.");
	}

	/*************************************************************************
	 * Creates the checkboxes.
	 * @param parent the parent composite
	 *************************************************************************/ 
	private void createCheckboxes(Composite parent) {
		//------------------------------------------------
		// Compare Difference Checkbox
		//------------------------------------------------
		compareDiffCheckbox = new Button(parent, SWT.CHECK);
		compareDiffCheckbox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		compareDiffCheckbox.setText("Difference");

		//------------------------------------------------
		// Compare Difference % Checkbox
		//------------------------------------------------
		compareDiffPercentageCheckbox = new Button(parent, SWT.CHECK);
		compareDiffPercentageCheckbox.setText("Difference %");
		
		//------------------------------------------------
		// Compare as String Checkbox
		//------------------------------------------------
		compareAsStringsCheckbox = new Button(parent, SWT.CHECK);
		compareAsStringsCheckbox.setText("as Strings");

		//------------------------------------------------
		// Print Empty Column Checkbox
		//------------------------------------------------
		printEmptyColumnCheckbox = new Button(parent, SWT.CHECK);
		printEmptyColumnCheckbox.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		printEmptyColumnCheckbox.setText("Print empty column");
	}
	
	/*************************************************************************
	 * Deletes this compare definition by disposing all components and 
	 * removing all listeners & validators.
	 *************************************************************************/ 
	public void delete(){
		
		deleteButton.dispose();                  
		olderColumnCombo.dispose();             
		youngerColumnCombo.dispose();           
		printCombo.dispose();                   
		calculationComposite.dispose();         
		calculationCombo.dispose();             
		compareDiffCheckbox.dispose();          
		compareDiffPercentageCheckbox.dispose();
		compareAsStringsCheckbox.dispose();      
		printEmptyColumnCheckbox.dispose();     
		calculationNumberField.dispose();  
		
		for(IValidator validator : validatorArray){
			CSVComparatorGUI.getValidatorEngine().removeValidator(validator);
		}
		
		for(ChangeListener listener : deleteListenerArray){
			listener.stateChanged(new ChangeEvent(INSTANCE));
		}
		
		parent.layout();
		parent.pack();
	}
	
	/*************************************************************************
	 * Returns a CompareDefinition for this instance.
	 *  
	 *************************************************************************/ 
	public CompareDefinition createColumnDefinition(){
		
		CompareDefinition definition = new CompareDefinition();
		definition.setOlderColumnName(olderColumnCombo.getText());
		definition.setYoungerColumnName(youngerColumnCombo.getText());
		
		//------------------------------------------------
		// Calculation
		//------------------------------------------------
		String calculationKey = calculationCombo.getText();
		if(!calculationKey.equals(PRINT_NOTHING)){
			definition.addMethod(calculationMap.get(calculationKey), calculationNumberField.getText());
		}
		
		//------------------------------------------------
		// Print
		//------------------------------------------------
		String printKey = printCombo.getText();
		switch (printKey){
		
			case PRINT_OLDER_THAN_YOUNGER:
				definition.addMethod(CompareDefinition.PRINT_OLDER, "");
				definition.addMethod(CompareDefinition.PRINT_YOUNGER, "");
				break;
			
			case PRINT_MERGED:
				definition.addMethod(CompareDefinition.PRINT_MERGED, "");
				break;
			
			case PRINT_OLDER_ONLY:
				definition.addMethod(CompareDefinition.PRINT_OLDER, "");
				break;
			
				
			case PRINT_YOUNGER_ONLY:
				definition.addMethod(CompareDefinition.PRINT_YOUNGER, "");
				break;
			
			case PRINT_YOUNGER_THAN_OLDER:
				definition.addMethod(CompareDefinition.PRINT_YOUNGER, "");
				definition.addMethod(CompareDefinition.PRINT_OLDER, "");
				break;
			
			default: 
				break;
				
		}
		
		//------------------------------------------------
		// Compare Difference
		//------------------------------------------------
		if(compareDiffCheckbox.getSelection()){
			definition.addMethod(CompareDefinition.COMPARE_DIFF, "");
		}
		
		//------------------------------------------------
		// Compare Difference %
		//------------------------------------------------
		if(compareDiffPercentageCheckbox.getSelection()){
			definition.addMethod(CompareDefinition.COMPARE_DIFF_PERCENTAGE, "");
		}
		
		//------------------------------------------------
		// Compare As STrings
		//------------------------------------------------
		if(compareAsStringsCheckbox.getSelection()){
			definition.addMethod(CompareDefinition.COMPARE_AS_STRINGS, "");
		}
		
		//------------------------------------------------
		// Print Empty Column
		//------------------------------------------------
		if(printEmptyColumnCheckbox.getSelection()){
			definition.addMethod(CompareDefinition.PRINT_SEPARATOR, "");
		}
		
		return definition;
	}
	
	/*************************************************************************
	 * Uses the values defined in the compare definition and returns the
	 * value for the argument.
	 *  
	 *************************************************************************/ 
	public String getColumnDefinitionValue(){
		
		StringBuffer sb = new StringBuffer();
		//------------------------------------------------
		// Older and Younger column
		//------------------------------------------------
		sb.append(olderColumnCombo.getText());
		sb.append(",");
		sb.append(youngerColumnCombo.getText());
		
		//------------------------------------------------
		// Calculation
		//------------------------------------------------
		String calculationKey = calculationCombo.getText();
		if(!calculationKey.equals(PRINT_NOTHING)){
			String calculationValue = calculationMap.get(calculationKey);
			String calcWithNumber = calculationValue + "(" + calculationNumberField.getText() + ")";
			sb.append(",");
			sb.append(calcWithNumber);
		}
		
		//------------------------------------------------
		// Print
		//------------------------------------------------
		String printKey = printCombo.getText();
		if(!printKey.equals(PRINT_NOTHING)){
			sb.append(",");
			sb.append(printMap.get(printKey));
		}
		
		//------------------------------------------------
		// Compare Difference
		//------------------------------------------------
		Boolean compareDiff = compareDiffCheckbox.getSelection();
		if(compareDiff){
			sb.append(",");
			sb.append("compareDifference()");
		}
		
		//------------------------------------------------
		// Compare Difference %
		//------------------------------------------------
		Boolean compareDiffPercentage = compareDiffPercentageCheckbox.getSelection();
		if(compareDiffPercentage){
			sb.append(",");
			sb.append("compareDifference%()");
		}
		
		//------------------------------------------------
		// Compare As STrings
		//------------------------------------------------
		Boolean compareAsStrings = compareAsStringsCheckbox.getSelection();
		if(compareAsStrings){
			sb.append(",");
			sb.append("compareAsStrings()");
		}
		
		//------------------------------------------------
		// Print Empty Column
		//------------------------------------------------
		Boolean printEmptyColumn = printEmptyColumnCheckbox.getSelection();
		if(printEmptyColumn){
			sb.append(",");
			sb.append("printSeparator()");
		}
		
		return sb.toString();
	}
	
	/*************************************************************************
	 * Uses the values defined in the compare definition and creates a 
	 * -column.compareDef argument.
	 *************************************************************************/ 
	public String getColumnDefinitionArgument(){
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(CompareArguments.COLUMN_COMPARE_DEF);
		sb.append("=");
		sb.append(this.getColumnDefinitionValue());
		
		return sb.toString();

	}

	/*************************************************************************
	 *  Add a delete listener.
	 *************************************************************************/ 
	public void addDeleteListener(ChangeListener listener){
		deleteListenerArray.add(listener);
	}
	
	/*************************************************************************
	 * remove Delete Listener
	 *************************************************************************/ 
	public void removeDeleteListener(ChangeListener listener){
		deleteListenerArray.remove(listener);
	}

	/*************************************************************************
	 * Loads the values from a compare definition.
	 *************************************************************************/ 
	public void loadDefinition(CompareDefinition definitionCLI) {
		
		//------------------------------------------------
		// Set Older/Younger Column
		//------------------------------------------------
		this.olderColumnCombo.setText(definitionCLI.getOlderColumnName());
		this.youngerColumnCombo.setText(definitionCLI.getYoungerColumnName());
		
		//------------------------------------------------
		// Resolve Methods
		//------------------------------------------------
		int index = 0;
		int printOlderIndex = -1;
		int printYoungerIndex = -1;
		int printMergedIndex = -1;
		
		for(Entry<String,String> methodEntry : definitionCLI.getMethods()){
			
			switch (methodEntry.getKey()) {
			
				case CompareDefinition.PRINT_OLDER:
					printOlderIndex = index;
					break;
					
				case CompareDefinition.PRINT_YOUNGER:
					printYoungerIndex = index;
					break;
					
				case CompareDefinition.PRINT_MERGED:
					printCombo.setText(PRINT_MERGED);
					printMergedIndex = index;
					break;
					
				case CompareDefinition.PRINT_SEPARATOR:
					printEmptyColumnCheckbox.setSelection(true);
					break;
					
				case CompareDefinition.COMPARE_DIFF:
					compareDiffCheckbox.setSelection(true);
					break;
					
				case CompareDefinition.COMPARE_DIFF_PERCENTAGE:
					compareDiffPercentageCheckbox.setSelection(true);
					break;
					
				case CompareDefinition.COMPARE_AS_STRINGS:
					compareAsStringsCheckbox.setSelection(true);
					break;
					
				case CompareDefinition.DIVIDE_OLDER:
					calculationCombo.setText(CALC_DIVIDE_OLDER);
					calculationNumberField.setText(methodEntry.getValue());
					break;
					
				case CompareDefinition.DIVIDE_YOUNGER:
					calculationCombo.setText(CALC_DIVIDE_YOUNGER);
					calculationNumberField.setText(methodEntry.getValue());
					break;
					
				case CompareDefinition.MULTIPLY_OLDER:
					calculationCombo.setText(CALC_MULTIPLY_OLDER);
					calculationNumberField.setText(methodEntry.getValue());
					break;
					
				case CompareDefinition.MULTIPLY_YOUNGER:
					calculationCombo.setText(CALC_MULTIPLY_YOUNGER);
					calculationNumberField.setText(methodEntry.getValue());
					break;
					
				default:
					break;
			}
			
			index++;
		}
		
		//------------------------------------------------
		// Resolve Printing
		//------------------------------------------------
		if(printOlderIndex != -1 && printYoungerIndex != -1){
			if( printOlderIndex < printYoungerIndex){
				printCombo.setText(PRINT_OLDER_THAN_YOUNGER);
			}else{
				printCombo.setText(PRINT_YOUNGER_THAN_OLDER);
			}
		}else{
			
			if(printOlderIndex != -1){
				printCombo.setText(PRINT_OLDER_ONLY);
			}
			if(printYoungerIndex != -1){
				printCombo.setText(PRINT_YOUNGER_ONLY);
			}
			
			if(   printOlderIndex == -1 
			   && printYoungerIndex == -1 
			   && printMergedIndex == -1){
				printCombo.setText(PRINT_NOTHING);
			}
		}
		
	}

	//####################################################################################
	// INNER CLASSES 
	//####################################################################################

	/*************************************************************************
	 * Delete Listener which will delete this compare definition.
	 *************************************************************************/ 
	private class DeleteListener implements MouseListener {
	
		@Override
		public void mouseUp(MouseEvent arg0) {
			delete();
		}
		
		@Override
		public void mouseDoubleClick(MouseEvent arg0) {}
	
		@Override
		public void mouseDown(MouseEvent arg0) {}
	
	}

}
