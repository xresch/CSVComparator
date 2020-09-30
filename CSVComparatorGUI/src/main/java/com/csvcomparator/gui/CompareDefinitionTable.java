package com.csvcomparator.gui;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.csvapi.compare.CompareArguments;
import com.csvapi.compare.CompareArgumentsBuilder;
import com.csvapi.compare.CompareDefinition;
import com.csvcomparator.gui.utils.SWTGUIUtils;

/*************************************************************************
 * CompareDefinitionTable is a scrolled composite which displays 
 * compare definitions like a table. 
 * 
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
public class CompareDefinitionTable extends ScrolledComposite {
	
	private ArrayList<CompareDefinitionGUI> definitionArray = new ArrayList<CompareDefinitionGUI>();
	
	Composite innerComposite;

	//####################################################################################
	// CONSTRUCTORS
	//####################################################################################
	public CompareDefinitionTable(Composite arg0, int arg1) {
		super(arg0, arg1);
		
		innerComposite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(9, false);
		gridLayout.marginBottom = 5;
		gridLayout.marginRight = 5;
		gridLayout.marginTop = 5;
		gridLayout.marginLeft = 5;
		innerComposite.setLayout(gridLayout);
	
		this.addHeader();

		this.setContent(innerComposite);
		this.setMinSize(innerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
	}
	
	/*************************************************************************
	 * 
	 *************************************************************************/ 
	private void addHeader(){
		
		Label blankLabel = new Label(innerComposite, SWT.NONE);
		GridData gd_blankLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_blankLabel.widthHint = 25;
		blankLabel.setLayoutData(gd_blankLabel);
		blankLabel.setText(" ");
		
		Label lblOlderColumn = new Label(innerComposite, SWT.NONE);
		GridData gd_lblOlderColumn = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblOlderColumn.widthHint = 100;
		gd_lblOlderColumn.horizontalIndent = 5;
		lblOlderColumn.setLayoutData(gd_lblOlderColumn);
		lblOlderColumn.setText("Older Column");
		SWTGUIUtils.addInfoDeco(lblOlderColumn, "Choose the column of the older file used for this comparison.", SWT.LEFT | SWT.TOP);
		
		Label lblYoungercolumn = new Label(innerComposite, SWT.NONE);
		GridData gd_lblYoungercolumn = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblYoungercolumn.widthHint = 100;
		gd_lblYoungercolumn.horizontalIndent = 5;
		lblYoungercolumn.setLayoutData(gd_lblYoungercolumn);
		lblYoungercolumn.setText("YoungerColumn");
		SWTGUIUtils.addInfoDeco(lblYoungercolumn, "Choose the column of the younger file used for this comparison.", SWT.LEFT | SWT.TOP);
		
		Label calculationLabel = new Label(innerComposite, SWT.NONE);
		GridData gd_lblCalculation = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblCalculation.widthHint = 190;
		gd_lblCalculation.horizontalIndent = 5;
		calculationLabel.setLayoutData(gd_lblCalculation);
		calculationLabel.setText("Calculate");
		SWTGUIUtils.addInfoDeco(calculationLabel, 
				"Before comparing you can perform calculations on the values in a column."
				+ "\ne.g. When you have to compare files with data made in timeframes of different length (1 and 2 hour).", 
				SWT.LEFT | SWT.TOP);
		
		Label printLabel = new Label(innerComposite, SWT.NONE);
		GridData gd_printLabel = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_printLabel.widthHint = 120;
		gd_printLabel.horizontalIndent = 5;
		printLabel.setLayoutData(gd_printLabel);
		printLabel.setText("Print");
		SWTGUIUtils.addInfoDeco(printLabel, "Select how the chosen older and younger column should be printed.", SWT.LEFT | SWT.TOP);
		
		Label lblCompare = new Label(innerComposite, SWT.NONE);
		GridData gd_lbCompare = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		gd_lbCompare.horizontalIndent = 5;
		lblCompare.setLayoutData(gd_lbCompare);
		lblCompare.setText("Compare");
		SWTGUIUtils.addInfoDeco(lblCompare, "Choose how the data should be compared.", SWT.LEFT | SWT.TOP);
		
		this.setMinSize(innerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}
	
	/*************************************************************************
	 * Adds a compare definition to the table and loads the values from the
	 * provided cli compare definition.
	 * 
	 * @param definitionCLI definition which values should be loaded
	 *************************************************************************/ 
	public CompareDefinitionGUI addColumnDefinition(CompareDefinition definitionCLI){
		CompareDefinitionGUI definitionGUI = this.addColumnDefinition();
		definitionGUI.loadDefinition(definitionCLI);
		return definitionGUI;
	}

	/*************************************************************************
	 * Adds a compare definition without any values.
	 *************************************************************************/ 
	public CompareDefinitionGUI addColumnDefinition(){
		CompareDefinitionGUI definition = new CompareDefinitionGUI(innerComposite);
		definitionArray.add(definition);
		
		definition.addDeleteListener(
				new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						
						if(e.getSource() instanceof CompareDefinitionGUI){
							definitionArray.remove(e.getSource());
							updateScrolling();
						}
					}
				});
		
		this.updateScrolling();
		
		return definition;
	}
	
	public CompareArgumentsBuilder addArgumentsToBuilder(CompareArgumentsBuilder builder){
		
		for(CompareDefinitionGUI definition : definitionArray){
			builder.addColumnCompareDefinition(definition.createColumnDefinition());
		}
		
		return builder;
	}
	
	/*************************************************************************
	 * Returns an array list of -column.compareDef arguments.
	 *************************************************************************/ 
	public ArrayList<String> getArguments(){
		
		ArrayList<String> arguments = new ArrayList<String>();
		
		for(CompareDefinitionGUI definition : definitionArray){
			arguments.add(definition.getColumnDefinitionArgument());
		}
		
		return arguments;
	}
	
	/*************************************************************************
	 * Updates the scrolling composite.
	 *************************************************************************/ 
	public void updateScrolling(){
		innerComposite.pack();
		this.setMinSize(innerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	/*************************************************************************
	 * Loads all -column.compareDef arguments from a CompareArguments class.
	 * 
	 * @param arguments an instance of CompareArguments which contains the
	 * -column.compareDef arguments to load.
	 * 
	 *************************************************************************/ 
	public void loadConfig(CompareArguments arguments) {
		
		//------------------------------------------------
		// Remove existing definitions
		// ---------------------------
		// A copy of the definitionArray is created to
		// prevent ConcurrentModificationException.
		//------------------------------------------------
		CompareDefinitionGUI[] arrayCopy = definitionArray.toArray(new CompareDefinitionGUI[0]);
		for(CompareDefinitionGUI definition : arrayCopy){
			definition.delete();
		}
		
		//------------------------------------------------
		// Load definitions from CompareArguments
		//------------------------------------------------
		for(CompareDefinition definition : arguments.getCompareDefinitions()){
			this.addColumnDefinition(definition);
		}
		
	}

}
