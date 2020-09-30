package com.csvcomparator.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.PatternSyntaxException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.csvapi.utils.CSVAPIUtils;
import com.csvcomparator.gui.utils.SWTGUIUtils;
import com.csvcomparator.gui.validation.FilepathCanReadValidator;
import com.csvcomparator.gui.validation.NotNullStringValidator;
import com.csvcomparator.gui.validation.RegexCompilableValidator;

/*************************************************************************
 * This class is used in the UI for creating the older and younger file
 * part. 
 * 
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
/**
 * @author rscheiwi
 *
 */
public class FileGroup extends Composite {
	
	private Text filepathField;
	private Text labelField;
	private Group fileGroup;
	private DelimiterChooserCombo delimiterCombo;
	private Combo identifierCombo;
	private Button handleQuotesCheckbox;
	private File selectedFile;
	private ControlDecoration controlDecoration;
	private ControlDecoration delimiterDeco;
	private ControlDecoration identifierDeco;
	private ControlDecoration fileChooserDeco;
	
	private ArrayList<ChangeListener> headersChangeListenerArray = new ArrayList<ChangeListener>();

	//####################################################################################
	// CONSTRUCTORS
	//####################################################################################
	public FileGroup(Composite parent, int swtStyle) {
		super(parent, swtStyle);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		

		//------------------------------------------------
		// Create File Group
		//------------------------------------------------
		
		fileGroup = new Group(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginTop = 5;
		gridLayout.marginRight = 5;
		gridLayout.marginLeft = 5;
		gridLayout.marginBottom = 5;
		fileGroup.setLayout(gridLayout);
		
		//------------------------------------------------
		// Create UI Parts
		//------------------------------------------------
		createFilepathPart();
		
		createLabelPart();
		
		createDelimiterPart();
		
		createIdentifierColumnPart();
		
		createHandleQuotesPart();
	}

	private void createFilepathPart() {
		//------------------------------------------------
		// "Choose..." Button
		//------------------------------------------------
		Button fileChooserButton = new Button(fileGroup, SWT.NONE);
		fileChooserButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		fileChooserButton.setText("Choose...");
		
		fileChooserDeco = new ControlDecoration(fileChooserButton, SWT.LEFT | SWT.TOP);
		fileChooserDeco.setImage(SWTResourceManager.getImage(FileGroup.class, "/org/eclipse/jface/fieldassist/images/info_ovr.gif"));
		fileChooserDeco.setDescriptionText("Choose a file containing comma saparated values to compare it. "
											 + "\nThe first line of the file is considered as a header.");
		
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
		// Filepath Field
		//------------------------------------------------
		filepathField = new Text(fileGroup, SWT.BORDER);
		filepathField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		FilepathCanReadValidator canReadValidator = 
				new FilepathCanReadValidator(fileChooserButton,SWT.BOTTOM | SWT.LEFT) {
			
					@Override
					public String getStringToValidate() {
						return filepathField.getText();
					}
				};
				
		canReadValidator.setTag("ALWAYS");
		CSVComparatorGUI.getValidatorEngine().addValidator(canReadValidator);
		filepathField.addModifyListener(canReadValidator);
				
		filepathField.addFocusListener(
							new FocusAdapter() {
								@Override
								public void focusLost(FocusEvent arg0) {
									setSelectedFile(filepathField.getText());
								}
							});
	}

	private void createLabelPart() {
		//------------------------------------------------
		// Label 
		//------------------------------------------------
		Label labelLabel = new Label(fileGroup, SWT.NONE);
		labelLabel.setText("Label:");
		
		controlDecoration = new ControlDecoration(labelLabel, SWT.LEFT | SWT.TOP);
		controlDecoration.setImage(SWTResourceManager.getImage(FileGroup.class, "/org/eclipse/jface/fieldassist/images/info_ovr.gif"));
		controlDecoration.setDescriptionText("The Label will be appended to the column headers in the result file.");
		
		labelField = new Text(fileGroup, SWT.BORDER);
		labelField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		NotNullStringValidator labelValidator = 
				new NotNullStringValidator(labelLabel, SWT.BOTTOM | SWT.LEFT) {
			
					@Override
					public String getInvalidMessage() {
						return "If you don't specify a label, columns will be hard to differentiate in the result file.";
					}
				
					@Override
					public String getStringToValidate() {
						return labelField.getText();
						
					}
				};
		
		labelValidator.setDecoratorImage("/org/eclipse/jface/fieldassist/images/warn_ovr.gif");
		
		labelField.addKeyListener(labelValidator);
	}

	private void createDelimiterPart() {
		
		Label delimiterLabel = new Label(fileGroup, SWT.NONE);
		delimiterLabel.setText("Delimiter(regex):");
		
		delimiterDeco = new ControlDecoration(delimiterLabel, SWT.LEFT | SWT.TOP);
		delimiterDeco.setImage(SWTResourceManager.getImage(FileGroup.class, "/org/eclipse/jface/fieldassist/images/info_ovr.gif"));
		delimiterDeco.setDescriptionText("The delimiter used in the chosen csv-file. \n Regex has to be used.");
		
		delimiterCombo = new DelimiterChooserCombo(fileGroup, SWT.NONE);
		RegexCompilableValidator delimiterValidator = 
				new RegexCompilableValidator(delimiterLabel, SWT.BOTTOM | SWT.LEFT) {
					@Override
					public String getStringToValidate() {
						return delimiterCombo.getText();
					}
				};
	
		delimiterValidator.setTag("ALWAYS");
		CSVComparatorGUI.getValidatorEngine().addValidator(delimiterValidator);
		
		delimiterCombo.addModifyListener(delimiterValidator);
		
		delimiterCombo.addFocusListener(
						new FocusAdapter() {
							@Override
							public void focusLost(FocusEvent arg0) {
								resolveQuotes();
								resolveHeaders();
							}
						});
		
		delimiterCombo.addSelectionListener(
						new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent arg0) {
								resolveQuotes();
								resolveHeaders();
							}
						});
	}

	private void createIdentifierColumnPart() {
		//------------------------------------------------
		// Identifier Column
		//------------------------------------------------
		Label identifierLabel = new Label(fileGroup, SWT.NONE);
		identifierLabel.setText("Identifier Column:");
		
		identifierDeco = new ControlDecoration(identifierLabel, SWT.LEFT | SWT.TOP);
		identifierDeco.setImage(SWTResourceManager.getImage(FileGroup.class, "/org/eclipse/jface/fieldassist/images/info_ovr.gif"));
		identifierDeco.setDescriptionText("The identifier column is the column containing the value which is considered as the identifier of a row. \n"
											 + "If in both files a row has the same identifier the comparator will compare this rows to each other.");
		
		identifierCombo = new Combo(fileGroup, SWT.NONE | SWT.READ_ONLY);
		identifierCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		NotNullStringValidator identifierValidator = new NotNullStringValidator(identifierLabel, SWT.BOTTOM | SWT.LEFT) {
			
			@Override
			public String getStringToValidate() {
				return identifierCombo.getText();
			}
		};
		
		identifierValidator.setTag("ALWAYS");
		CSVComparatorGUI.getValidatorEngine().addValidator(identifierValidator);
		identifierCombo.addModifyListener(identifierValidator);
	}
	
	private void createHandleQuotesPart() {
		//------------------------------------------------
		// Handle Quotes
		//------------------------------------------------
		Label handleQuotesLabel = new Label(fileGroup, SWT.NONE);
		handleQuotesLabel.setText("");

		handleQuotesCheckbox = new Button(fileGroup, SWT.CHECK);
		handleQuotesCheckbox.setText(" handle Quotes");
		handleQuotesCheckbox.setSelection(false);
		
		SWTGUIUtils.addInfoDeco(handleQuotesCheckbox, 
				"Toggles handling of quoted values, including \\\\ and \\\" inside quotation. (does not work with \" outside of quotation.)", 
				SWT.LEFT | SWT.TOP);
		
	}
	
	private void resolveHeaders(){
		
		if( selectedFile != null ){
			BufferedReader bf = null;
			
			try {
			
				LinkedHashMap<String, String> delimiterMap = delimiterCombo.getDelimiterMap();
				String previousComboValue = identifierCombo.getText();
				
				String delimiter = delimiterCombo.getText();
				
				if(delimiterMap.containsKey(delimiter)){
					delimiter = delimiterMap.get(delimiter);
				}
				bf = new BufferedReader(new FileReader(selectedFile));
				String headerLine = bf.readLine();
				
				if( headerLine != null && !headerLine.equals("") ){
					if (!delimiter.equals("")){
						
						String[] headers;
						if(!this.getHandleQuotes()){
							headers = headerLine.split(delimiter);
						}else{
							headers = CSVAPIUtils.splitHandleQuotesRegex(headerLine, delimiter);
						}
						identifierCombo.setItems(headers);
					} else{
						// Set the whole header line when the delimiter
						// is not set
						identifierCombo.setItems(new String[]{headerLine});
					}
				}
				
				//----------------------------------------------
				// Set selection
				//----------------------------------------------
				
				//default Selection
				identifierCombo.select(0);
				
				// Change default to the previous selection
				// if it is also in the new resolved headers
				String[] headers = identifierCombo.getItems();
				for(int i=0; i < headers.length; i++){
					if(previousComboValue.equals(headers[i])){
						identifierCombo.select(i);
					}
				}
				
			}catch (FileNotFoundException e) {
				MessageDialog.openError(getShell(), "File Not Found ", "The specified file was not found.");
				e.printStackTrace();
			} catch (IOException e) {
				MessageDialog.openError(getShell(), "IO Exception ", "An exception occured when trying to read a file.");
				e.printStackTrace();
			} catch (PatternSyntaxException e) {
				//Do nothing, field will be decorated by validator
			}finally{
				
				this.fireChange();
				
				if(bf != null)
					try {
						bf.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
	}
	
	/*************************************************************************
	 * Tries to resolve if quotes are used in the input file by checking the 
	 * first 10 lines of the file.
	 * 
	 * @param selectedFile a file with csv formatted content
	 *************************************************************************/ 
	public void resolveQuotes(){
		
		if(selectedFile != null ){
			
			BufferedReader br = null;
			try {
					
				br = new BufferedReader(new FileReader(selectedFile));
				
				String delimiter = delimiterCombo.getDelimiter();
				boolean hasMoreLines = true;
				int quotesCount = 0;
				
				//------------------------------------------------
				//Check the first 10 lines for the delimiter
				//------------------------------------------------
				
				//split by quotes at before and after delimiter
				String splitPattern = delimiter+"\"";

				int counter = 0;
				while(hasMoreLines && counter < 10){
					
					String currentLine = br.readLine();
					//------------------------------------------------
					// proceed if not end of file
					//------------------------------------------------
					if (currentLine != null){
						
						quotesCount += currentLine.split(splitPattern).length -1;
						
						if(currentLine.matches("^\".*")) 
							quotesCount++;
					
					}else{
						hasMoreLines = false;
					}
					
					counter++;
				}
				
				//checks if there is at least on each line a quoted value
				if( counter!=0 && ( quotesCount/(counter) ) >= 1){
					this.setHandleQuotes(true);
				}else{
					this.setHandleQuotes(false);
				}
			
			} catch (FileNotFoundException e) {
				MessageDialog.openError(getShell(), "File Not Found ", "The specified file was not found.");
				e.printStackTrace();
				
			} catch (IOException e) {
				MessageDialog.openError(getShell(), "IO Exception ", "An exception occured when trying to read a file.");
				e.printStackTrace();
				
			} finally{
				if(br != null){
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	

	
	public void setText(String text){
		fileGroup.setText(text);
		
		//set keys for SWTBot testing
		filepathField.setData("org.eclipse.swtbot.widget.key", text+".filepathField");
		labelField.setData("org.eclipse.swtbot.widget.key", text+".labelField");
		delimiterCombo.setData("org.eclipse.swtbot.widget.key", text+".delimiterCombo");
		identifierCombo.setData("org.eclipse.swtbot.widget.key", text+".identifierCombo");
		handleQuotesCheckbox.setData("org.eclipse.swtbot.widget.key", text+".handleQuotesCheckbox");
	}

	public File getSelectedFile(){
		return selectedFile;
	}
	
	public boolean setSelectedFile(String filePath){
		
		if(filePath != null && !filePath.equals("")){
			File file = new File(filePath);
			filepathField.setText(filePath);
			
			if(!file.canRead() || !file.exists()){
				return false;
			}else{
				selectedFile = file;
				delimiterCombo.resolveDelimiter(selectedFile);
				resolveQuotes();
				resolveHeaders();
				return true;
			}
		}
		return false;
	}
	
	public String getFilePath(){
		return filepathField.getText();
	}

	public String getLabel(){
		return labelField.getText();
	}
	
	public void setLabel(String label){
		labelField.setText(label);
	}

	public String getDelimiter(){
		return delimiterCombo.getDelimiter();
	}
	
	public void setDelimiter(String delimiter){
		delimiterCombo.setDelimiter(delimiter);
	}
	
	public String getIdentifierColumn(){
		return identifierCombo.getText();
	}
	
	public void setIdentifierColumn(String identifier){
		identifierCombo.setText(identifier);
	}

	public boolean getHandleQuotes(){
		return handleQuotesCheckbox.getSelection();
	}
	
	public void setHandleQuotes(boolean handleQuotes){
		handleQuotesCheckbox.setSelection(handleQuotes);
	}
	
	public void setHandleQuotes(String handleQuotes){
		boolean bool = Boolean.parseBoolean(handleQuotes);
		handleQuotesCheckbox.setSelection(bool);
	}
	
	
	public String[] getHeaders(){
		return identifierCombo.getItems();
	}

	
	public void addHeadersChangeListener(ChangeListener listener){
		headersChangeListenerArray.add(listener);
	}
	
	public void removeHeadersChangeListener(ChangeListener listener){
		headersChangeListenerArray.remove(listener);
	}
	
	private void fireChange(){
		
		for(ChangeListener listener : headersChangeListenerArray){
			listener.stateChanged(new ChangeEvent(this));
		}
	}
}


