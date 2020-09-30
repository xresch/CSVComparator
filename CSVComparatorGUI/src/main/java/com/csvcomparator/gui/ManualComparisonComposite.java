package com.csvcomparator.gui;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
 * This class represents the content of the tab "Manual Comparison".
 * 
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
public class ManualComparisonComposite extends Composite {

	private CompareDefinitionTable columnDefinitionTable;

	public static final Logger logger = LogManager.getLogger(ManualComparisonComposite.class.getName());
	
	//####################################################################################
	// CONSTRUCTORS
	//####################################################################################
	public ManualComparisonComposite(Composite arg0, int arg1) {
		super(arg0, arg1);
		setLayout(new GridLayout(1, false));
		
		//solves scrolling issue caused by background propagation
		this.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		//------------------------------------------------
		// Description
		//------------------------------------------------
		Label descriptionlabel = new Label(this, SWT.NONE);
		descriptionlabel.setText("Instead of comparing all columns which have the same header, you can define by yourself how the files should be compared.");
		
		//------------------------------------------------
		// Column Definition Table
		//------------------------------------------------
		columnDefinitionTable = new CompareDefinitionTable(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		columnDefinitionTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		columnDefinitionTable.setExpandHorizontal(true);
		columnDefinitionTable.setExpandVertical(true);
		
		//------------------------------------------------
		// ButtonComposite
		//------------------------------------------------
		Composite buttonComposite = new Composite(this, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(2, false));
		buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		//------------------------------------------------
		// Add Button
		//------------------------------------------------
		Button addButton = new Button(buttonComposite, SWT.NONE);
		addButton.setText("Add");
		addButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				columnDefinitionTable.addColumnDefinition();
				
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}
		});
		
		//------------------------------------------------
		// CompareButton
		//------------------------------------------------
		Button compareButton = new Button(buttonComposite, SWT.NONE);
		compareButton.setText("Compare");
		compareButton.addSelectionListener(new ManualCompareListener());
		compareButton.setData("org.eclipse.swtbot.widget.key", "manualCompareButton");
		//compareButton

	}
	
	//####################################################################################
	// CLASS METHODS
	//####################################################################################
	/*************************************************************************
	 * Loads the configuration from the given compare arguments.
	 * 
	 * @param arguments the compare arguments to load
	 *************************************************************************/ 
	public void loadConfig(CompareArguments arguments){
		columnDefinitionTable.loadConfig(arguments);
	}

	//####################################################################################
	// GETTERS & SETTERS
	//####################################################################################
	
	public CompareArgumentsBuilder addArgumentsToBuilder(CompareArgumentsBuilder builder){
		
		columnDefinitionTable.addArgumentsToBuilder(builder);
		
		return builder;
	}
	
	public ArrayList<String> getArguments(){
		return columnDefinitionTable.getArguments();
	}
	
	//####################################################################################
	// INNER CLASSES
	//####################################################################################
	/*************************************************************************
	 * This listener triggers the manual comparison.
	 *************************************************************************/ 
	class ManualCompareListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent arg0) {
			
			ValidatorEngine validatorEngine = CSVComparatorGUI.getValidatorEngine();
			
			if(validatorEngine.validateMatchingTag("ALWAYS|MANUALCOMPARE")){
				ArrayList<String> arguments = CSVComparatorGUI.getArgumentsBuilder().toArrayListAll();
				
				Log4jUtils.setNewRequestIDForThread(true);
				logger.info("START - Manual Comparison"); long startTime = System.nanoTime();
				
					CSVComparatorCLI.main(arguments.toArray(new String [arguments.size()]));
				
				logger.info("END - Manual Comparison (Duration="+( (System.nanoTime()-startTime) / 1000000)+"ms)");
				
				
				MessageDialog.openInformation(getShell(), 
						"Done", "Compare finished!");
			}else{
				MessageDialog.openError(Display.getCurrent().getActiveShell(), 
										"Error", 
										"Please correct the following issues:\n"+ validatorEngine.getInvalidMessages());
			}
			
		}
	}
}
