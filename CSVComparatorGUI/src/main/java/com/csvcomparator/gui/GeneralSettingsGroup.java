package com.csvcomparator.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.csvapi.compare.CompareArguments;
import com.csvapi.compare.CompareArgumentsBuilder;
import com.csvcomparator.gui.utils.SWTGUIUtils;
import com.csvcomparator.gui.validation.FileCanWriteValidator;
import com.csvcomparator.gui.validation.NotNullStringValidator;
import com.csvcomparator.gui.validation.RegexCompilableValidator;

/*************************************************************************
 * This class represents the part "General Settings" in the UI.
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
public class GeneralSettingsGroup extends Composite {
	
	private Text resultFilepathField;
	private LinkedHashMap<String, String> delimiterMap = new LinkedHashMap<String, String>();
	
	private Combo delimiterCombo;
	private File selectedFile;
	private Button sortResultCheckbox;
	private Button makeUniqueCheckbox;
	private Button resultQuotesCheckbox;
	
	private ControlDecoration controlDecoration;
	private ControlDecoration makeUniqueDeco;
	private ControlDecoration resultFileDeco;
	private ControlDecoration delimiterDeco;
	
	private Button topLevelCheckbox;
	private Composite topLevelComposite;
	private ColumnChooserCombo olderColumnCombo;
	private ColumnChooserCombo youngerColumnCombo;
	private Text topLevelField;
	private Group generalSettingsGroup;
	private ControlDecoration topLevelDeco;
	private ControlDecoration topLevelFieldDeco;
	private Composite checkboxComposite;

	//####################################################################################
	// CONSTRUCTORS
	//####################################################################################
	public GeneralSettingsGroup(Composite parent, int swtStyle) {
		super(parent, swtStyle);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		//------------------------------------------------
		// Set delimiters
		//------------------------------------------------
		delimiterMap.put("Komma ','", ",");
		delimiterMap.put("Semicolon ';'", ";");
		delimiterMap.put("Hash '#'", "#");
		delimiterMap.put("Tab '\\t'", "\t");
		delimiterMap.put("Blank ' '", " ");
		
		//------------------------------------------------
		// Create Group
		//------------------------------------------------
		generalSettingsGroup = new Group(this, SWT.NONE);
		generalSettingsGroup.setText("General Settings");
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginBottom = 5;
		gridLayout.marginRight = 5;
		gridLayout.marginTop = 5;
		gridLayout.marginLeft = 5;
		generalSettingsGroup.setLayout(gridLayout);
		
		
		//------------------------------------------------
		// Add Control Parts
		//------------------------------------------------
		createResultFilePart();
		
		createDelimiterPart();
		
		createTopLevelPart();
		
		createCheckboxComposite();
		
		setSWTTestIDs();

	}

	


	//####################################################################################
	// CLASS METHODS
	//####################################################################################
	

	/*************************************************************************
	 * Creates the sortResultCheckbox
	 *************************************************************************/ 
	private void createCheckboxComposite() {
		
		checkboxComposite = new Composite(generalSettingsGroup, SWT.NONE);
		GridLayout gl_composite = new GridLayout(3, false);
		gl_composite.horizontalSpacing = 12;
		gl_composite.marginWidth = 0;
		checkboxComposite.setLayout(gl_composite);
		checkboxComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 3, 1));
		
		//------------------------------------------------
		// sortResult Checkbox
		//------------------------------------------------
		sortResultCheckbox = new Button(checkboxComposite, SWT.CHECK);
		sortResultCheckbox.setText(" sort Result");
		sortResultCheckbox.setSelection(true);
		
		controlDecoration = new ControlDecoration(sortResultCheckbox, SWT.LEFT | SWT.TOP);
		controlDecoration.setImage(SWTResourceManager.getImage(GeneralSettingsGroup.class, "/org/eclipse/jface/fieldassist/images/info_ovr.gif"));
		controlDecoration.setDescriptionText("If true, the rows will be sorted from A-Z by the\n"
											+ "values in the specified identifier column. ");

		//------------------------------------------------
		// makeUnique Checkbox
		//------------------------------------------------
		
		makeUniqueCheckbox = new Button(checkboxComposite, SWT.CHECK);
		makeUniqueCheckbox.setText("make Identifiers unique");
		makeUniqueCheckbox.setSelection(true);
		
		makeUniqueDeco = new ControlDecoration(makeUniqueCheckbox, SWT.LEFT | SWT.TOP);
		makeUniqueDeco.setImage(SWTResourceManager.getImage(GeneralSettingsGroup.class, "/org/eclipse/jface/fieldassist/images/info_ovr.gif"));
		makeUniqueDeco.setDescriptionText("If in one file two or more rows have the same identifying value,\n"
											 + "a number in braces like “[1]” is added to the values to make them unique.");
		
		//------------------------------------------------
		// resultQuotes Checkbox
		//------------------------------------------------
		resultQuotesCheckbox = new Button(checkboxComposite, SWT.CHECK);
		resultQuotesCheckbox.setText("add Quotes");
		resultQuotesCheckbox.setSelection(false);
		
		SWTGUIUtils.addInfoDeco(resultQuotesCheckbox, 
				"Adds quotes to the result.", 
				SWT.LEFT | SWT.TOP);
	}

	/*************************************************************************
	 * Creates the Result file Part
	 *************************************************************************/ 
	private void createResultFilePart() {
		//------------------------------------------------
		// Result File Label
		//------------------------------------------------
		Label resultFileLabel = new Label(generalSettingsGroup, SWT.NONE);
		resultFileLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		resultFileLabel.setText("Result File:");
		
		resultFileDeco = new ControlDecoration(resultFileLabel, SWT.LEFT | SWT.TOP);
		resultFileDeco.setImage(SWTResourceManager.getImage(GeneralSettingsGroup.class, "/org/eclipse/jface/fieldassist/images/info_ovr.gif"));
		resultFileDeco.setDescriptionText("Choose a file to store the compare results.");
		
		//------------------------------------------------
		// "Choose..." Button
		//------------------------------------------------
		Button fileChooserButton = new Button(generalSettingsGroup, SWT.NONE);
		fileChooserButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		fileChooserButton.setText("Choose...");
		
		fileChooserButton.addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						FileDialog fileDialog = new FileDialog(getShell());
						String filePath = fileDialog.open();
						
						if(filePath != null){
							setSelectedFile(filePath);
						}
					}
				});
		
		//------------------------------------------------
		// FilePath Field
		//------------------------------------------------
		resultFilepathField = new Text(generalSettingsGroup, SWT.BORDER);
		resultFilepathField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		NotNullStringValidator filepathValidator = new NotNullStringValidator(resultFileLabel, SWT.BOTTOM | SWT.LEFT) {
			
			@Override
			public String getStringToValidate() {
				return resultFilepathField.getText();
			}
		};
		
		resultFilepathField.addModifyListener(filepathValidator);
		
		filepathValidator.setTag("ALWAYS");
		CSVComparatorGUI.getValidatorEngine().addValidator(filepathValidator);
		
		//------------------------------------------------
		// Create file writeable Validator
		//------------------------------------------------

		FileCanWriteValidator filepathWritableValidator = new FileCanWriteValidator(null, SWT.BOTTOM | SWT.LEFT) {
			
			@Override
			public String getStringToValidate() {
				return resultFilepathField.getText();
			}
		};
		
		filepathWritableValidator.setTag("PRECHECK");
		CSVComparatorGUI.getValidatorEngine().addValidator(filepathWritableValidator);
	}
	
	/*************************************************************************
	 * Creates the delimiter part.
	 *************************************************************************/ 
	private void createDelimiterPart() {
		//------------------------------------------------
		// Delimiter Label & Combo
		//------------------------------------------------
		Label delimiterLabel = new Label(generalSettingsGroup, SWT.NONE);
		delimiterLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		delimiterLabel.setText("Result Delimiter:");
		
		delimiterDeco = new ControlDecoration(delimiterLabel, SWT.LEFT | SWT.TOP);
		delimiterDeco.setImage(SWTResourceManager.getImage(GeneralSettingsGroup.class, "/org/eclipse/jface/fieldassist/images/info_ovr.gif"));
		delimiterDeco.setDescriptionText("Choose the delimiter used in the result file.");
		
		delimiterCombo = new Combo(generalSettingsGroup, SWT.NONE);
		delimiterCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		delimiterCombo.setItems(delimiterMap.keySet().toArray(new String[delimiterMap.size()]));
		delimiterCombo.select(1);
		
		RegexCompilableValidator delimiterValidator = 
				new RegexCompilableValidator(delimiterLabel, SWT.BOTTOM | SWT.LEFT) {
					@Override
					public String getStringToValidate() {
						return delimiterCombo.getText();
					}
				};
				
		delimiterCombo.addModifyListener(delimiterValidator);
		delimiterValidator.setTag("ALWAYS");
		CSVComparatorGUI.getValidatorEngine().addValidator(delimiterValidator);
	}


	/*************************************************************************
	 * Creates the top level part.
	 *************************************************************************/ 
	private void createTopLevelPart() {
		//------------------------------------------------
		// Top Level
		//------------------------------------------------
		topLevelCheckbox = new Button(generalSettingsGroup, SWT.CHECK);
		topLevelCheckbox.setText("Top Level:");
		
		topLevelDeco = new ControlDecoration(topLevelCheckbox, SWT.LEFT | SWT.TOP);
		topLevelDeco.setImage(SWTResourceManager.getImage(GeneralSettingsGroup.class, "/org/eclipse/jface/fieldassist/images/info_ovr.gif"));
		topLevelDeco.setDescriptionText(  "A top level can be used if the csv data is structured in blocks,"
										+ "\nwith a top level and sub corresponding sub levels, example: "
										+ "\n"
										+ "\n Level Description\t\tCount"
										+ "\n ----- ------------\t\t-----"
										+ "\n Top   Warehouse A\t10"
										+ "\n Sub   Apple\t\t2"
										+ "\n Sub   Durian\t\t8"
										+ "\n Top   Warehouse B\t12"
										+ "\n Sub   Apple\t\t9"
										+ "\n Sub   Durian\t\t1"
										+ "\n Sub   Passionfruit\t\t2" );
		
		topLevelCheckbox.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if(topLevelCheckbox.getSelection()){
					
					olderColumnCombo.getCombo().setEnabled(true);		
					youngerColumnCombo.getCombo().setEnabled(true);		
					topLevelField.setEnabled(true);
					sortResultCheckbox.setSelection(false);
					sortResultCheckbox.setEnabled(false);
					
				}else{
					
					olderColumnCombo.getCombo().setEnabled(false);		
					youngerColumnCombo.getCombo().setEnabled(false);		
					topLevelField.setEnabled(false);	
					sortResultCheckbox.setEnabled(true);
					
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}
		});
		
		topLevelComposite = new Composite(generalSettingsGroup, SWT.NONE);
		topLevelComposite.setLayout(new GridLayout(3, false));
		GridData gd_topLevelComposite = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_topLevelComposite.heightHint = 30;
		topLevelComposite.setLayoutData(gd_topLevelComposite);
		
		//---------------------------------
		// older Column Combo
		olderColumnCombo = new ColumnChooserCombo(topLevelComposite, SWT.NONE | SWT.READ_ONLY, CSVComparatorGUI.getOlderFileGroup());
		olderColumnCombo.setDescriptionText("Choose the column of the older file\nused to determine if it is a topLevel.");
		olderColumnCombo.getCombo().setEnabled(false);	
		
		NotNullStringValidator olderValidator = new NotNullStringValidator(olderColumnCombo, SWT.BOTTOM | SWT.LEFT) {
			
			@Override
			public boolean checkPreconditions(){
				//only check if toplevel is enabled
				return topLevelCheckbox.getSelection();
			}
			
			@Override
			public String getStringToValidate() {
				return olderColumnCombo.getText();
			}
		};
		
		olderColumnCombo.setValidator(olderValidator);
		olderColumnCombo.setTag("ALWAYS");
		
		
		//---------------------------------
		// younger Column Combo
		youngerColumnCombo = new ColumnChooserCombo(topLevelComposite, SWT.NONE | SWT.READ_ONLY, CSVComparatorGUI.getYoungerFileGroup());
		youngerColumnCombo.setDescriptionText("Choose the column of the younger file\nused to determine if it is a topLevel.");
		youngerColumnCombo.getCombo().setEnabled(false);	
		
		NotNullStringValidator youngerValidator = new NotNullStringValidator(youngerColumnCombo, SWT.BOTTOM | SWT.LEFT) {
			
			@Override
			public boolean checkPreconditions(){
				//only check if toplevel is enabled
				return topLevelCheckbox.getSelection();
			}
			
			@Override
			public String getStringToValidate() {
				return youngerColumnCombo.getText();
			}
		};
		
		youngerColumnCombo.setValidator(youngerValidator);
		youngerColumnCombo.setTag("ALWAYS");
		
		//---------------------------------
		// FilePath Field
		
		topLevelField = new Text(topLevelComposite, SWT.BORDER);
		GridData gd_topLevelField = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_topLevelField.horizontalIndent = 5;
		topLevelField.setLayoutData(gd_topLevelField);
		topLevelField.setEnabled(false);
		
		topLevelFieldDeco = new ControlDecoration(topLevelField, SWT.LEFT | SWT.TOP);
		topLevelFieldDeco.setImage(SWTResourceManager.getImage(GeneralSettingsGroup.class, "/org/eclipse/jface/fieldassist/images/info_ovr.gif"));
		topLevelFieldDeco.setDescriptionText("The value which identifies a row as a top level.");
		
		NotNullStringValidator topLevelValidator = new NotNullStringValidator(topLevelField, SWT.BOTTOM | SWT.LEFT) {
			
			@Override
			public boolean checkPreconditions(){
				//only check if toplevel is enabled
				return topLevelCheckbox.getSelection();
			}
			
			@Override
			public String getStringToValidate() {
				return topLevelField.getText();
			}
		};
		new Label(topLevelComposite, SWT.NONE);
		
		topLevelField.addModifyListener(topLevelValidator);
		
		topLevelValidator.setTag("ALWAYS");
		CSVComparatorGUI.getValidatorEngine().addValidator(topLevelValidator);
	}
	
	/*************************************************************************
	 * This method will set all ids for SWTBot testing.
	 * Only call after all UI components are instantiated.
	 * 
	 *************************************************************************/ 
	private void setSWTTestIDs() {
		
		String label = "General Settings";
		
		//set keys for SWTBot testing
		resultFilepathField.setData("org.eclipse.swtbot.widget.key", label+".resultFilepathField");
		delimiterCombo.setData("org.eclipse.swtbot.widget.key", label+".delimiterCombo");
		sortResultCheckbox.setData("org.eclipse.swtbot.widget.key", label+".sortResultCheckbox");
		makeUniqueCheckbox.setData("org.eclipse.swtbot.widget.key", label+".makeUniqueCheckbox");
		topLevelCheckbox.setData("org.eclipse.swtbot.widget.key", label+".topLevelCheckbox");
		olderColumnCombo.getCombo().setData("org.eclipse.swtbot.widget.key", label+".olderColumnCombo");
		youngerColumnCombo.getCombo().setData("org.eclipse.swtbot.widget.key", label+".youngerColumnCombo");
		topLevelField.setData("org.eclipse.swtbot.widget.key", label+".topLevelField");
	}
	
	/*************************************************************************
	 * Loads the configuration defined in the provided arguments.
	 * 
	 * @param arguments the arguments containing the config to load
	 *************************************************************************/ 
	public void loadConfig(CompareArguments arguments){
		
		this.setSelectedFile(	arguments.getValue(CompareArguments.RESULT_FILE));
		this.setDelimiter(		arguments.getValue(CompareArguments.RESULT_DELIMITER));
		this.setSortResult(		arguments.getValue(CompareArguments.RESULT_SORT));
		this.setMakeUnique(		arguments.getValue(CompareArguments.COLUMN_IDENTIFIER_MAKEUNIQUE));
		this.setTopLevel(		arguments.getValue(CompareArguments.COLUMN_TOPLEVEL));
		this.setResultQuotes(	Boolean.parseBoolean(arguments.getValue(CompareArguments.RESULT_QUOTES)));
		
	}

	//####################################################################################
	// GETTERS & SETTERS
	//####################################################################################
	private boolean setSelectedFile(String filePath){
		
		if(filePath != null && !filePath.equals("")){
			
			resultFilepathField.setText(filePath);
			
			File file = new File(filePath);
			
			selectedFile = file;
				
			return true;
			
		}
		return false;
	}
	
	public File getResultFile(){
		return selectedFile;
	}
	
	public String getResultFilePath(){
		return resultFilepathField.getText();
	}
	
	public boolean getSortResult(){
		return sortResultCheckbox.getSelection();
	}
	
	public boolean getMakeUnique(){
		return makeUniqueCheckbox.getSelection();
	}
	
	public boolean getResultQuotes(){
		return resultQuotesCheckbox.getSelection();
	}
	
	public void setDelimiter(String delimiter){
		if(delimiter != null){
			if(delimiterMap.containsValue(delimiter)){
				String[] values = delimiterMap.values().toArray(new String[0]);
				for(int i=0; i < values.length; i++){
					if(values[i].equals(delimiter)){
						String[] keys = delimiterMap.keySet().toArray(new String[0]);
						delimiterCombo.setText(keys[i]);
						break;
					}
				}
			}else{
				delimiterCombo.setText(delimiter);
			}
		}
	}


	public String getDelimiter(){
		
		// Check if the value represents a key in the
		// delimitersMap and return the correct value
		String comboValue = delimiterCombo.getText();
		
		String[] delimiterKeys = delimiterMap.keySet().toArray(new String[delimiterMap.size()]);
		for(int i=0; i < delimiterKeys.length; i++){
			if(comboValue.equals(delimiterKeys[i])){
				return delimiterMap.get(comboValue);
			}
		}
		
		return delimiterCombo.getText();
	}
	
	public void setSortResult(String sortResult){
		if(sortResult != null)
			if(sortResult.toLowerCase().equals("true")){
				this.setSortResult(true);
			}else{
				this.setSortResult(false);
			}
	}
	
	public void setSortResult(Boolean sortResult){
		sortResultCheckbox.setSelection(sortResult);
	}
	
	public void setMakeUnique(String makeUnique){
		if(makeUnique != null)
			if(makeUnique.toLowerCase().equals("true")){
				this.setMakeUnique(true);
			}else{
				this.setMakeUnique(false);
			}
	}
	
	public void setMakeUnique(Boolean sortResult){
		makeUniqueCheckbox.setSelection(sortResult);
	}
	
	public void setResultQuotes(Boolean sortResult){
		resultQuotesCheckbox.setSelection(sortResult);
	}
	
	public void setTopLevel(String topLevel){
		if(topLevel != null){
			String[] splitted = topLevel.split(",");
			if( splitted.length == 3){
				this.setTopLevel(splitted[0], splitted[1], splitted[2]);
			}else{
				
			}
		}
	}
	
	public void setTopLevel(String olderColumn, String youngerColumn, String value){
		this.topLevelCheckbox.setSelection(true);
		this.olderColumnCombo.setText(olderColumn);
		this.youngerColumnCombo.setText(youngerColumn);
		this.topLevelField.setText(value);
		
		this.topLevelCheckbox.notifyListeners(SWT.Selection, new Event());
	}
	
	public CompareArgumentsBuilder addArgumentsToBuilder(CompareArgumentsBuilder builder){
		
		builder.setResultFile(resultFilepathField.getText())
				.setResultDelimiter(this.getDelimiter())
				.setResultSort(this.getSortResult())
				.setResultQuotes(this.getResultQuotes())
				.setColumnIdentifierMakeUnique(this.getMakeUnique());
		
		if(topLevelCheckbox.getSelection()){
			builder.setColumnTopLevel(olderColumnCombo.getText(),
					youngerColumnCombo.getText(),
					topLevelField.getText());
		}
		
		return builder;
	}
	
	public ArrayList<String> getArguments(){
		
		ArrayList<String> arguments = new ArrayList<String>();
		
		arguments.add(CompareArguments.RESULT_FILE			+"="+ resultFilepathField.getText());
		arguments.add(CompareArguments.RESULT_DELIMITER		+"="+ this.getDelimiter());
		arguments.add(CompareArguments.RESULT_SORT			+"="+ this.getSortResult());
		arguments.add(CompareArguments.RESULT_QUOTES		+"="+ this.getResultQuotes());
		arguments.add(CompareArguments.COLUMN_IDENTIFIER_MAKEUNIQUE +"="+ this.getMakeUnique());
		
		if(topLevelCheckbox.getSelection()){
			StringBuffer buffer = new StringBuffer();
			buffer.append(olderColumnCombo.getText());
			buffer.append(",");
			buffer.append(youngerColumnCombo.getText());
			buffer.append(",");
			buffer.append(topLevelField.getText());
			arguments.add(CompareArguments.COLUMN_TOPLEVEL +"="+ buffer.toString());
		}
		
		return arguments;
	}

}
