package com.csvcomparator.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import com.csvapi.arguments.ArgumentsException;
import com.csvapi.compare.CompareArguments;
import com.csvapi.compare.CompareArgumentsBuilder;
import com.csvapi.utils.Log4jUtils;
import com.csvcomparator.gui.validation.ValidatorEngine;

/*************************************************************************
 * CSVComparatorGUI is the main shell which contains most of the other UI 
 * elements.
 *  
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
public class CSVComparatorGUI extends Shell {

	private static CSVComparatorGUI INSTANCE;
	private static FileGroup youngerFileGroup;
	private static FileGroup olderFileGroup;
	private static GeneralSettingsGroup generalSettingsGroup;
	private static DefaultComparisonComposite defaultComparisonComposite;
	private static ManualComparisonComposite manualComparisonComposite;
	
	private static ValidatorEngine validatorEngine = new ValidatorEngine();
	private CTabFolder tabFolder;
	
	public static final Logger logger = LogManager.getLogger(CSVComparatorGUI.class.getName());
	
	//####################################################################################
	// CONSTRUCTORS
	//####################################################################################
	public CSVComparatorGUI(Display display) {
		
		super(display, SWT.SHELL_TRIM);
		INSTANCE = this;
		
		//------------------------------------------------
		// Shell Settings
		//------------------------------------------------
		this.setBackgroundImage(SWTResourceManager.getImage(CSVComparatorGUI.class, "/com/csvcomparator/gui/images/CSVComparatorGUI_Background.jpg"));
		this.setImage(SWTResourceManager.getImage(CSVComparatorGUI.class, "/com/csvcomparator/gui/images/CSVComparator_icon2_64x64px.png"));
		this.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.setBackgroundMode(SWT.INHERIT_FORCE);
		
		this.setLayout(new GridLayout(1, false));
		
		this.addListener(SWT.CLOSE, new ExitListener());
		
		//------------------------------------------------
		// Create UI
		//------------------------------------------------
		createMenuBar();
	
		createHeaderImagePart();
		
		createFilesGroupPart();
		
		createGeneralSettingsPart();
		
		createTabFolderPart();
		
		createDefaultComparisonPart();
		
		createManualComparisonPart();
		
		//------------------------------------------------
		// Set Title and pack
		//------------------------------------------------
		this.setText("CSVComparator GUI");
		this.setMinimumSize(500, 800);
		this.pack();
		this.setSize(1100, 800);
	}

	//####################################################################################
	// CLASS METHODS
	//####################################################################################
	/*************************************************************************
	 * Creates the Menubar 
	 *************************************************************************/ 
	private void createMenuBar() {
		Menu menuBar = new Menu(this, SWT.BAR);
		setMenuBar(menuBar);
		
		MenuItem fileMenu = new MenuItem(menuBar, SWT.CASCADE);
		fileMenu.setText("File");
		
		Menu cascadeMenu = new Menu(fileMenu);
		fileMenu.setMenu(cascadeMenu);
		
		MenuItem saveAsConfigFileItem = new MenuItem(cascadeMenu, SWT.NONE);
		saveAsConfigFileItem.setText("Save as .config-File...");
		saveAsConfigFileItem.addSelectionListener(new SaveAsConfigFileListener());
		
		MenuItem saveAsBashScriptItem = new MenuItem(cascadeMenu, SWT.NONE);
		saveAsBashScriptItem.setText("Save as Bash-Script...");
		saveAsBashScriptItem.addSelectionListener(new SaveAsBashScriptListener());
		
		MenuItem loadConfigFileItem = new MenuItem(cascadeMenu, SWT.NONE);
		loadConfigFileItem.setText("Load .config-File...");
		loadConfigFileItem.addSelectionListener(new LoadConfigFileListener());
		
		MenuItem separator = new MenuItem(cascadeMenu, SWT.SEPARATOR);
		separator.setText("separator");
		
		MenuItem exitItem = new MenuItem(cascadeMenu, SWT.NONE);
		exitItem.setText("Exit");
		exitItem.addSelectionListener(new ExitListener());
	}

	/*************************************************************************
	 * Creates the header with the image.
	 *************************************************************************/ 
	private void createHeaderImagePart() {
		Label headerLabel = new Label(this, SWT.NONE);
		headerLabel.setBackgroundImage(SWTResourceManager.getImage(CSVComparatorGUI.class, "/com/csvcomparator/gui/images/CSVComparator_Logo_Header_100px.jpg"));
		GridData headerLabelGridData = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		headerLabelGridData.widthHint = 800;
		headerLabelGridData.heightHint = 100;
		headerLabel.setLayoutData(headerLabelGridData);
	}

	/*************************************************************************
	 * Creates the FileGroup with older and younger file group. 
	 *************************************************************************/ 
	private void createFilesGroupPart() {
		//------------------------------------------------
		// Create Files Group
		//------------------------------------------------
		Group filesGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		filesGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		filesGroup.setText("Files");
		FillLayout fl_grpFiles = new FillLayout(SWT.HORIZONTAL);
		fl_grpFiles.spacing = 5;
		fl_grpFiles.marginHeight = 5;
		fl_grpFiles.marginWidth = 5;
		filesGroup.setLayout(fl_grpFiles);
		
		//------------------------------------------------
		// Create older FileGroup
		//------------------------------------------------
		olderFileGroup = new FileGroup(filesGroup, SWT.NONE);
		olderFileGroup.setText("Older File");
		olderFileGroup.setLabel("old");
		
		//------------------------------------------------
		// Create younger FileGroup
		//------------------------------------------------
		youngerFileGroup = new FileGroup(filesGroup, SWT.NONE);
		youngerFileGroup.setText("Younger File");
		youngerFileGroup.setLabel("young");
	}

	/*************************************************************************
	 * Creates the General settings part. 
	 *************************************************************************/ 
	private void createGeneralSettingsPart() {
		generalSettingsGroup = new GeneralSettingsGroup(this, SWT.NONE);
		generalSettingsGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	}
	
	/*************************************************************************
	 * Creates TabFolder part. 
	 *************************************************************************/ 
	private void createTabFolderPart() {
		tabFolder = new CTabFolder(this, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tabFolder.setSimple(false);
	}

	/*************************************************************************
	 * Creates the default comparison part.
	 *************************************************************************/ 
	private void createDefaultComparisonPart() {
		CTabItem defaultComparisonItem = new CTabItem(tabFolder, SWT.NONE);
		defaultComparisonItem.setText("Default Comparison");
		
		defaultComparisonComposite = new DefaultComparisonComposite(tabFolder, SWT.NONE);
		defaultComparisonItem.setControl(defaultComparisonComposite);
		
		defaultComparisonItem.setData("org.eclipse.swtbot.widget.key", "defaultComparisonTab");
	}

	/*************************************************************************
	 * Creates the Manual comparison part. 
	 *************************************************************************/ 
	private void createManualComparisonPart() {
		CTabItem manualComparisonTabItem = new CTabItem(tabFolder, SWT.NONE);
		manualComparisonTabItem.setText("Manual Comparison");
		
		manualComparisonComposite = new ManualComparisonComposite(tabFolder, SWT.NONE);
		manualComparisonTabItem.setControl(manualComparisonComposite);
		
		manualComparisonTabItem.setData("org.eclipse.swtbot.widget.key", "manualComparisonTab");
		
		//set selection to defaultComparison
		tabFolder.setSelection(0);
	}
	
	public static void saveAsBashScript(String filePath){
		
		Log4jUtils.setNewRequestIDForThread(true);
		logger.info("START - Save as bash script to: "+filePath); long startTime = System.nanoTime();
		
		if(filePath != null){
			
			BufferedReader reader = null;
			try {
				//------------------------------------------------
				// Load Script Template
				//------------------------------------------------

				InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("templates/bash_template_noArgs.sh");
				
				reader = new BufferedReader(new InputStreamReader(inputStream));
				
				StringBuffer stringBuffer = new StringBuffer();
				String currentLine = "";
				while( (currentLine = reader.readLine() ) != null){
					stringBuffer.append(currentLine+"\n");
				}
				
				String scriptTemplate = stringBuffer.toString();
				//------------------------------------------------
				// Load Replacements 
				//------------------------------------------------
				StringBuffer generalSettingsReplacement = new StringBuffer();
				for (String argument : generalSettingsGroup.getArguments()){
					generalSettingsReplacement.append("\t\t\t\t\t\""+argument+"\"\\\n");
				}
				
				StringBuffer defaultComparisonReplacement = new StringBuffer();
				for (String argument : defaultComparisonComposite.getArguments()){
					defaultComparisonReplacement.append("\t\t\t\t\t\""+argument+"\"\\\n");
				}
				
				StringBuffer columnDefinitionReplacement = new StringBuffer();
				for (String argument : manualComparisonComposite.getArguments()){
					columnDefinitionReplacement.append("\t\t\t\t\t\""+argument+"\"\\\n");
				}
				
				//------------------------------------------------
				// Replace placeholders
				//------------------------------------------------
				scriptTemplate = scriptTemplate.replace("<<olderFile>>", olderFileGroup.getFilePath());
				scriptTemplate = scriptTemplate.replace("<<olderLabel>>", olderFileGroup.getLabel());
				scriptTemplate = scriptTemplate.replace("<<olderDelimiter>>", olderFileGroup.getDelimiter());
				scriptTemplate = scriptTemplate.replace("<<olderQuotes>>", Boolean.toString(olderFileGroup.getHandleQuotes()));
				
				scriptTemplate = scriptTemplate.replace("<<youngerFile>>", youngerFileGroup.getFilePath());
				scriptTemplate = scriptTemplate.replace("<<youngerLabel>>", youngerFileGroup.getLabel());
				scriptTemplate = scriptTemplate.replace("<<youngerDelimiter>>", youngerFileGroup.getDelimiter());
				scriptTemplate = scriptTemplate.replace("<<youngerQuotes>>", Boolean.toString(youngerFileGroup.getHandleQuotes()));
				
				scriptTemplate = scriptTemplate.replace("<<identifierColumn>>", olderFileGroup.getIdentifierColumn() + "," + youngerFileGroup.getIdentifierColumn());
				
				scriptTemplate = scriptTemplate.replace("<<generalSettings>>", generalSettingsReplacement);
				scriptTemplate = scriptTemplate.replace("<<defaultComparison>>", defaultComparisonReplacement);
				scriptTemplate = scriptTemplate.replace("<<columnDefinitions>>", columnDefinitionReplacement);
				
				Charset charset = StandardCharsets.UTF_8;
				Files.write(Paths.get(filePath), scriptTemplate.getBytes(charset));
				
				
			} catch (IOException e) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), 
						"IO Error", 
						"The following error occured while saving the script:\n"+ e.getMessage());
			}
			finally{
				if(reader!=null){
					try {
						reader.close();
					} catch (IOException e) {
						MessageDialog.openError(Display.getCurrent().getActiveShell(), 
								"IO Error", 
								"The following error occured while saving the script:\n"+ e.getMessage());
					}
				}
				logger.info("END - Save as bash script (Duration="+( (System.nanoTime()-startTime) / 1000000)+"ms)");
			}
		}
		
		MessageDialog.openInformation(Display.getCurrent().getActiveShell(), 
				"Script created", 
				"The script was created.\n"
			  + "Please set the location of java and the CSVComparatorCLI.jar in the file.");
		
	}
	
	public static void saveAsConfigFile(String filePath){
		if(filePath != null){
			
			Log4jUtils.setNewRequestIDForThread(true);
			logger.info("START - Save as config file to: "+filePath); long startTime = System.nanoTime();
			
			try {
				
				CompareArgumentsBuilder builder = getArgumentsBuilder();
				
				StringBuffer sb = new StringBuffer();
				for(String line : builder.toArrayListAll()){
					sb.append(line+"\n");
				}
				
				Files.write(Paths.get(filePath), sb.toString().getBytes(StandardCharsets.UTF_8));
				
			} catch (IOException e) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), 
						"Error", 
						"Error while saving the file:\n"+ e.getMessage());
				e.printStackTrace();
			} finally {
				logger.info("END - Save as config file (Duration="+( (System.nanoTime()-startTime) / 1000000)+"ms)");
			}
		}
	}
	
	
	public static void loadConfigFile(String filePath){
		if(filePath != null){
			
			Log4jUtils.setNewRequestIDForThread(true);
			logger.info("START - Load Config File: "+filePath ); long startTime = System.nanoTime();

			//------------------------------------------------
			// Load Config File
			//------------------------------------------------
			CompareArguments arguments = new CompareArguments();
			
			try {
				arguments.readArgumentsFromFile(filePath);
			} catch (ArgumentsException e) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), 
						"Configuration Error", 
						"The config file could not be loaded and failed on the following line:\n"+ e.getArgument());
				
				return;
			}
			
			//-----------------------------
			// Older File Group
			if(arguments.getValue(CompareArguments.OLDER_FILE) != null)
				olderFileGroup.setSelectedFile(arguments.getValue(CompareArguments.OLDER_FILE));
			
			if(arguments.getValue(CompareArguments.OLDER_LABEL) != null)
				olderFileGroup.setLabel(arguments.getValue(CompareArguments.OLDER_LABEL));
			
			if(arguments.getValue(CompareArguments.OLDER_DELIMITER) != null)
				olderFileGroup.setDelimiter(arguments.getValue(CompareArguments.OLDER_DELIMITER));
			
			if(arguments.getValue(CompareArguments.OLDER_QUOTES) != null)
				olderFileGroup.setHandleQuotes(arguments.getValue(CompareArguments.OLDER_QUOTES));
			
			//-----------------------------
			// Younger File Group
			if(arguments.getValue(CompareArguments.YOUNGER_FILE) != null)
				youngerFileGroup.setSelectedFile(arguments.getValue(CompareArguments.YOUNGER_FILE));
			
			if(arguments.getValue(CompareArguments.YOUNGER_LABEL) != null)
				youngerFileGroup.setLabel(arguments.getValue(CompareArguments.YOUNGER_LABEL));
			
			if(arguments.getValue(CompareArguments.YOUNGER_DELIMITER) != null)
				youngerFileGroup.setDelimiter(arguments.getValue(CompareArguments.YOUNGER_DELIMITER));
			
			if(arguments.getValue(CompareArguments.YOUNGER_QUOTES) != null)
				youngerFileGroup.setHandleQuotes(arguments.getValue(CompareArguments.YOUNGER_QUOTES));
			
			//-----------------------------
			// Set identifier for older&younger
			if(arguments.getValue(CompareArguments.COLUMN_IDENTIFIER) != null){
				String[] splitted = arguments.getValue(CompareArguments.COLUMN_IDENTIFIER).split(",");
				if(splitted.length == 2){
					olderFileGroup.setIdentifierColumn(splitted[0]);
					youngerFileGroup.setIdentifierColumn(splitted[1]);
				}else{
					logger.warn("The argument '"+CompareArguments.COLUMN_IDENTIFIER+"' was not loaded because the number of defined columns is not equals 2.");
				}
			}
			
			//-----------------------------
			// General Settings
			generalSettingsGroup.loadConfig(arguments);
			
			//-----------------------------
			// Default Comparison
			defaultComparisonComposite.loadConfig(arguments);
			
			//-----------------------------
			// Manual Comparison
			manualComparisonComposite.loadConfig(arguments);
			
			logger.info("END - Load Config File (Duration="+( (System.nanoTime()-startTime) / 1000000)+"ms)");
		}
	}

	//####################################################################################
	// OVERRIDDEN METHODS
	//####################################################################################
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	//####################################################################################
	// GETTERS & SETTERS
	//####################################################################################
	
	/*************************************************************************
	 * Returns an CompareArgumentsBuilder that contains all the settings
	 * currently set in the application.
	 * 
	 *************************************************************************/ 
	public static CompareArgumentsBuilder getArgumentsBuilder(){
		
		CompareArgumentsBuilder builder = new CompareArgumentsBuilder();
		
		builder.setLogLevelConsole("INFO")
			   .setLogLevelFile("INFO")
				.setOlderFile(olderFileGroup.getFilePath())
				.setOlderLabel(olderFileGroup.getLabel())
				.setOlderDelimiter(olderFileGroup.getDelimiter())
				.setOlderQuotes(olderFileGroup.getHandleQuotes())
				.setYoungerFile(youngerFileGroup.getFilePath())
				.setYoungerLabel(youngerFileGroup.getLabel())
				.setYoungerDelimiter(youngerFileGroup.getDelimiter())
				.setYoungerQuotes(youngerFileGroup.getHandleQuotes())
                .setColumnIdentifier(olderFileGroup.getIdentifierColumn(), youngerFileGroup.getIdentifierColumn());     
		
		generalSettingsGroup.addArgumentsToBuilder(builder);
		
		defaultComparisonComposite.addArgumentsToBuilder(builder);
		manualComparisonComposite.addArgumentsToBuilder(builder);
		
		return builder;
	}
	
	/*************************************************************************
	 * Returns the YoungerFileGroup of the instance.
	 * 
	 * @return FileGroup
	 *************************************************************************/ 
	public static FileGroup getYoungerFileGroup() {
		return youngerFileGroup;
	}

	/*************************************************************************
	 * Returns the OlderFileGroup of the instance.
	 * 
	 * @return FileGroup
	 *************************************************************************/ 
	public static FileGroup getOlderFileGroup() {
		return olderFileGroup;
	}
	
	/*************************************************************************
	 * Returns the validator engine.
	 * 
	 * @return ValidatorEngine
	 *************************************************************************/ 
	public static ValidatorEngine getValidatorEngine() {
		return validatorEngine;
	}
	
	//####################################################################################
	// INNER CLASSES
	//####################################################################################
	/*************************************************************************
	 * This listener is used to save the current settings in the application 
	 * as a config file.
	 *************************************************************************/ 
	private class SaveAsConfigFileListener implements SelectionListener{
		@Override
		public void widgetSelected(SelectionEvent arg0) {
			
			if(!validatorEngine.validateAll()){
				MessageDialog.openError(Display.getCurrent().getActiveShell(), 
						"Error", 
						"Please correct the following issues before saving:\n"+ validatorEngine.getInvalidMessages());
				return;
			}
			
			FileDialog fileChooser = new FileDialog(getShell(), SWT.SAVE);
			
			String filePath = fileChooser.open();
			
			saveAsConfigFile(filePath);

		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) { }

	}
	/*************************************************************************
	 * This listener is used to save the current settings in the application 
	 * as a bash script.
	 *************************************************************************/ 
	private class ExitListener implements Listener, SelectionListener{

		@Override
		public void handleEvent(Event arg0) {
			this.onExit();
		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			this.onExit();
		}
		
		public void onExit(){
			SWTResourceManager.dispose();
			INSTANCE.close();
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {}

		
	}
	/*************************************************************************
	 * This listener is used to save the current settings in the application 
	 * as a bash script.
	 *************************************************************************/ 
	private class SaveAsBashScriptListener implements SelectionListener{

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) { /* do nothing */ }

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			
			if(!validatorEngine.validateAll()){
				MessageDialog.openError(Display.getCurrent().getActiveShell(), 
						"Error", 
						"Please correct the following issues before saving:\n"+ validatorEngine.getInvalidMessages());
				return;
			}
			
			FileDialog fileChooser = new FileDialog(getShell(), SWT.SAVE);
			
			String filePath = fileChooser.open();
			
			saveAsBashScript(filePath);
		}
	}
	
	/*************************************************************************
	 * This listener will load the settings form a config file into the 
	 * application. 
	 *************************************************************************/ 
	private class LoadConfigFileListener implements SelectionListener{

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) { /* do nothing */ }

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			
			FileDialog fileChooser = new FileDialog(getShell(), SWT.OPEN);
			
			String filePath = fileChooser.open();
			
			loadConfigFile(filePath);
			
		}
	}
	
}
