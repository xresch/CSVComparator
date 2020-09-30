package com.csvcomparator.gui.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCTabItem;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.csvapi.test.global.AssertUtilities;
import com.csvcomparator.gui.CSVComparatorGUI;

public class TestCSVComparatorGUI {

	private static SWTBot bot = null;
	
	private static SWTBotMenu fileMenu;
	private static SWTBotMenu saveConfigMenu;
	private static SWTBotMenu saveScriptMenu;
	private static SWTBotMenu loadConfigMenu;
	private static SWTBotMenu exitMenu;
	
	private static SWTBotText olderFilepathField;
	private static SWTBotText olderLabelField;
	private static SWTBotCombo olderDelimiterCombo;
	private static SWTBotCombo olderIdentifierCombo;
	private static SWTBotCheckBox olderQuotesCheckbox;
	
	private static SWTBotText youngerFilepathField;
	private static SWTBotText youngerLabelField;
	private static SWTBotCombo youngerDelimiterCombo;
	private static SWTBotCombo youngerIdentifierCombo;
	private static SWTBotCheckBox youngerQuotesCheckbox;
	
	private static String generalSettingsLabel;
	private static SWTBotText resultFilepathField;
	private static SWTBotCombo resultDelimiterCombo;
	private static SWTBotCheckBox sortResultCheckbox;
	private static SWTBotCheckBox makeUniqueCheckbox;
	private static SWTBotCheckBox topLevelCheckbox;
	private static SWTBotCombo topLevelOlderCombo;
	private static SWTBotCombo topLevelYoungerCombo;
	private static SWTBotText topLevelField;

	private static SWTBotButton defaultCompareButton;
	private static SWTBotCheckBox compareAsStringsCheckbox;
	private static SWTBotCheckBox compareDiffCheckbox;
	private static SWTBotCheckBox compareDiffPercentCheckbox;

	private static SWTBotCTabItem defaultComparisonTab;
	private static SWTBotCTabItem manualComparisonTab;
	private static SWTBotButton manualCompareButton;
	
	private static CSVComparatorGUI shell;
	
	
	@BeforeClass
	public static void beforeClass(){
		Display display = new Display();
		shell = new CSVComparatorGUI(display);
		
		shell.open();
		shell.setMinimized(false);
		shell.setActive();
		
		bot = new SWTBot(shell);
	
		wrapSWTControls();
	}
	
	public static void wrapSWTControls(){
		
		fileMenu = bot.menu("File");
		saveConfigMenu = bot.menu("Save as .config-File...");
		saveScriptMenu = bot.menu("Save as Bash-Script...");
		loadConfigMenu = bot.menu("Load .config-File...");
		exitMenu = bot.menu("Exit");
		
		String olderGroupTitle = "Older File";
		olderFilepathField = bot.textWithId(olderGroupTitle+".filepathField");
		olderLabelField = bot.textWithId(olderGroupTitle+".labelField");
		olderIdentifierCombo = bot.comboBoxWithId(olderGroupTitle+".identifierCombo");
		olderDelimiterCombo = bot.comboBoxWithId(olderGroupTitle+".delimiterCombo");
		olderQuotesCheckbox = bot.checkBoxWithId(olderGroupTitle+".handleQuotesCheckbox");
		
		String youngerGroupTitle = "Younger File";
		youngerFilepathField = bot.textWithId(youngerGroupTitle+".filepathField");
		youngerLabelField = bot.textWithId(youngerGroupTitle+".labelField");
		youngerIdentifierCombo = bot.comboBoxWithId(youngerGroupTitle+".identifierCombo");
		youngerDelimiterCombo = bot.comboBoxWithId(youngerGroupTitle+".delimiterCombo");
		youngerQuotesCheckbox = bot.checkBoxWithId(youngerGroupTitle+".handleQuotesCheckbox");
		
		generalSettingsLabel = "General Settings";
		resultFilepathField = bot.textWithId(generalSettingsLabel+".resultFilepathField");
		resultDelimiterCombo = bot.comboBoxWithId(generalSettingsLabel+".delimiterCombo");
		sortResultCheckbox = bot.checkBoxWithId(generalSettingsLabel+".sortResultCheckbox");
		makeUniqueCheckbox = bot.checkBoxWithId(generalSettingsLabel+".makeUniqueCheckbox");
		topLevelCheckbox = bot.checkBoxWithId(generalSettingsLabel+".topLevelCheckbox");
		topLevelOlderCombo = bot.comboBoxWithId(generalSettingsLabel+".olderColumnCombo");
		topLevelYoungerCombo = bot.comboBoxWithId(generalSettingsLabel+".youngerColumnCombo");
		topLevelField = bot.textWithId(generalSettingsLabel+".topLevelField");
		
		String label = "Default Comparison";
		defaultCompareButton = bot.buttonWithId(label+".compareButton");
		compareAsStringsCheckbox = bot.checkBoxWithId(label+".compareAsStringsCheckbox");
		compareDiffCheckbox = bot.checkBoxWithId(label+".compareDiffCheckbox");
		compareDiffPercentCheckbox = bot.checkBoxWithId(label+".compareDiffPercentCheckbox");
		
		defaultComparisonTab = bot.cTabItemWithId("defaultComparisonTab");
		manualComparisonTab = bot.cTabItemWithId("manualComparisonTab");
		
		manualComparisonTab.activate();
		manualCompareButton = bot.buttonWithId("manualCompareButton");
	}
	
	@Test
	public void testMenusExist(){
		
		Assert.assertNotNull(fileMenu);
		Assert.assertNotNull(saveConfigMenu);
		Assert.assertNotNull(saveScriptMenu);
		Assert.assertNotNull(loadConfigMenu);
		Assert.assertNotNull(exitMenu);
		
	}
	
	
	@Test
	public void testOlderGroup(){
		
		this.testFileGroup( olderFilepathField,
							olderLabelField,
							olderDelimiterCombo,
							olderIdentifierCombo,
							olderQuotesCheckbox);
	}
	
	@Test
	public void testYoungerGroup(){
		
		this.testFileGroup( youngerFilepathField,
							youngerLabelField,
							youngerDelimiterCombo,
							youngerIdentifierCombo,
							youngerQuotesCheckbox);
	}
	
	public void testFileGroup(SWTBotText filepathField,
							  SWTBotText labelField,
							  SWTBotCombo delimiterCombo,
							  SWTBotCombo identifierCombo,
							  SWTBotCheckBox quotesCheckbox){
		
		//##############################################################
		// Check if the fields are present
		//##############################################################
		Assert.assertNotNull(filepathField);
		Assert.assertNotNull(labelField);
        Assert.assertNotNull(delimiterCombo);
        Assert.assertNotNull(identifierCombo);
        Assert.assertNotNull(quotesCheckbox);
        
		//##############################################################
		// Change File to blank multi delimiter
		//##############################################################
		filepathField.setFocus();
		filepathField.setText("./testdata/blank_multi_delimiter.csv");
		
		Assert.assertEquals("Filepath was changed", 
                            "./testdata/blank_multi_delimiter.csv", 
                            filepathField.getText());
		
		//change focus will trigger that fields are updated
		delimiterCombo.setFocus();

		// Check if Delimiter was resolved
		Assert.assertEquals("blank multi delimiter was resolved", 
                "Blank ( regex ' +' )", 
                delimiterCombo.getText());
		
		// Check if IdentifierColumn was resolved
		Assert.assertEquals("identifier Column was resolved", 
				"Fruit", 
				identifierCombo.getText());
		
		//##############################################################
		// Change File to nonblank to blank delimiter
		//##############################################################
		filepathField.setFocus();
		
		filepathField.setText("./testdata/nonblank_blank_transition_delimiter.csv");
		Assert.assertEquals("Filepath was changed", 
							"./testdata/nonblank_blank_transition_delimiter.csv", 
                            filepathField.getText());
		
		//change focus will trigger that fields are updated
		delimiterCombo.setFocus();

		// Check if Delimiter was resolved
		Assert.assertEquals("nonblank to blank delimiter was resolved", 
                "Blank to non-blank transition ( regex: '[ \\t]+' )",
                delimiterCombo.getText());
		
		//##############################################################
		// Change File to hash delimiter
		//##############################################################
		filepathField.setFocus();
		filepathField.setText("./testdata/hash_single_delimiter.csv");
		
		Assert.assertEquals("Filepath was changed", 
				"./testdata/hash_single_delimiter.csv", 
				filepathField.getText());
		
		//change focus will trigger that fields are updated
		delimiterCombo.setFocus();
		
		// Check if Delimiter was resolved
		Assert.assertEquals("hash delimiter was resolved", 
				"Hash '#'",
				delimiterCombo.getText());
		
		//##############################################################
		// Change File to semicolon delimiter
		//##############################################################
		filepathField.setFocus();
		filepathField.setText("./testdata/semicolon_single_delimiter.csv");
		
		Assert.assertEquals("Filepath was changed", 
				"./testdata/semicolon_single_delimiter.csv", 
				filepathField.getText());
		
		//change focus will trigger that fields are updated
		delimiterCombo.setFocus();
		
		// Check if Delimiter was resolved
		Assert.assertEquals("Semicolon delimiter was resolved", 
				"Semicolon ';'",
				delimiterCombo.getText());
		
		//##############################################################
		// Select Quotes Checkbox
		//##############################################################
		quotesCheckbox.setFocus();
		quotesCheckbox.select();
		
		Assert.assertTrue("Quotes Checkbox is selected", quotesCheckbox.isChecked());
	}
	
	@Test
	public void testGeneralSettingsGroup(){
		
		//##############################################################
		// Check if the controls are present
		//##############################################################
		Assert.assertNotNull(resultFilepathField);
		Assert.assertNotNull(resultDelimiterCombo);
		Assert.assertNotNull(sortResultCheckbox);
		Assert.assertNotNull(makeUniqueCheckbox);
		Assert.assertNotNull(topLevelCheckbox);
		Assert.assertNotNull(topLevelOlderCombo);
		Assert.assertNotNull(topLevelYoungerCombo);
		Assert.assertNotNull(topLevelField);
		
		//##############################################################
		// change result file path
		//##############################################################
		resultFilepathField.setFocus();
        resultFilepathField.setText("./testdata/test_change_resultfile_path.csv");
		
		Assert.assertEquals("Result Filepath was changed", 
							"./testdata/test_change_resultfile_path.csv",
							resultFilepathField.getText());
		
		//change focus will trigger that fields are updated
		resultDelimiterCombo.setFocus();
		resultDelimiterCombo.setText("###");
		// Check if Delimiter was resolved
		Assert.assertEquals("Delimiter was changed", 
							"###",
							resultDelimiterCombo.getText());

		//##############################################################
		// Check Top Level part
		//##############################################################
		
		//--------------------------------------
		// set files to have columns to choose
		youngerFilepathField.setFocus(); //set focus to be sure to change it afterwards
		
		youngerFilepathField.setText("./testdata/blank_multi_delimiter.csv");
		
		olderFilepathField.setFocus();
		olderFilepathField.setText("./testdata/blank_multi_delimiter.csv");
		
		//--------------------------------------
		// check Checkbox
		topLevelCheckbox.setFocus();
		topLevelCheckbox.select();
		
		Assert.assertTrue("older column combo for top levels is enabled", topLevelOlderCombo.isEnabled());
		Assert.assertTrue("younger column combo for top levels is enabled", topLevelYoungerCombo.isEnabled());
		Assert.assertFalse("Checkbox 'sort' is disabled when using top levels", sortResultCheckbox.isEnabled());
		
		//--------------------------------------
		// check Combo has Selections
		String[] olderItems = topLevelOlderCombo.items();
		String[] youngerItems = topLevelYoungerCombo.items();

		Assert.assertEquals("older top level column items are loaded", "Count", olderItems[2]);
		Assert.assertEquals("younger top level column items are loaded", "Count", youngerItems[2]);
		
	}
	
	@Test
	public void testDefaultComparison(){
		//##############################################################
		// Check if the fields are present
		//##############################################################
		Assert.assertNotNull(defaultCompareButton);
		Assert.assertNotNull(compareAsStringsCheckbox);
        Assert.assertNotNull(compareDiffCheckbox);
        Assert.assertNotNull(compareDiffPercentCheckbox);
        
	}
	
	@Test
	public void testConfigLoading(){
		
		CSVComparatorGUI.loadConfigFile("./testdata/config_test_configLoading.txt");
		
		//##############################################################
		// Check older values loaded;
		//		-older.file=.\testdata\older_topLevel.csv
		//		-older.label=old
		//		-older.delimiter=;
		//		-older.quotes=true
		//		-column.identifier=Count,Count
		//##############################################################
		Assert.assertEquals("Older Filepath is loaded", 
				"./testdata/older_topLevel.csv",
				olderFilepathField.getText());
		
		Assert.assertEquals("Older Label is loaded", 
				"old",
				olderLabelField.getText());
		
		Assert.assertEquals("Older Delimiter is loaded", 
				"Hash '#'",
				olderDelimiterCombo.getText());
		
		Assert.assertTrue("Older Quotes is loaded", 
				olderQuotesCheckbox.isChecked());
		
		Assert.assertEquals("Older identifier column is loaded", 
				"Count",
				olderIdentifierCombo.getText());
		
		//##############################################################
		// Check younger values loaded;
		//		-younger.file=.\testdata\younger_topLevel.csv
		//		-younger.label=young
		//		-younger.delimiter=;
		//		-younger.quotes=true
		//		-column.identifier=Count,Count
		//##############################################################
		Assert.assertEquals("Younger Filepath is loaded", 
				"./testdata/younger_topLevel.csv",
				youngerFilepathField.getText());
		
		Assert.assertEquals("Younger Label is loaded", 
				"young",
				youngerLabelField.getText());
		
		Assert.assertEquals("Younger Delimiter is loaded", 
				"Komma ','",
				youngerDelimiterCombo.getText());
		
		Assert.assertTrue("Younger Quotes is loaded", 
				youngerQuotesCheckbox.isChecked());
		
		Assert.assertEquals("Younger identifier column is loaded", 
				"Description",
				youngerIdentifierCombo.getText());
		
		//##############################################################
		// Check result settings loaded
		//		-result.file=.\testdata\_result.csv
		//		-result.delimiter=,
		//		-result.sort=false
		//		-result.comparediff=true
		//		-result.comparediff%=false
		//		-result.comparestring=true
		//##############################################################
		Assert.assertEquals("Result Filepath is loaded", 
							"./testdata/_result.csv",
							resultFilepathField.getText());
		
		Assert.assertEquals("Result Delimiter is loaded", 
							"Komma ','",
							resultDelimiterCombo.getText());
		
		Assert.assertTrue("Result sort is loaded", 
							!sortResultCheckbox.isChecked());
		
		Assert.assertTrue("Result compare difference is loaded", 
							compareDiffCheckbox.isChecked());
		
		Assert.assertTrue("Result compare difference  percentage is loaded", 
							!compareDiffPercentCheckbox.isChecked());
		
		Assert.assertTrue("Result compare as strings is loaded", 
							compareAsStringsCheckbox.isChecked());
	
		//##############################################################
		// Check column settings loaded
		//		-column.identifier.makeunique=false
		//		-column.toplevel=Level,Level,Top
		//##############################################################
		
		Assert.assertTrue("Identifier column make unique is loaded", 
				!makeUniqueCheckbox.isChecked());
		
		Assert.assertEquals("TopLevel older is loaded", 
				"Level",
				topLevelOlderCombo.getText());
		
		Assert.assertEquals("TopLevel younger is loaded", 
				"Level",
				topLevelYoungerCombo.getText());
		
		//##############################################################
		// Check compare definitions loaded
		//##############################################################
		ArrayList<String> argumentsArray = CSVComparatorGUI.getArgumentsBuilder().toArrayListAll();
		
		Assert.assertTrue("Compare definition 01",
				argumentsArray.contains("-column.compareDef=Description,Description,printOlder(),printYounger(),compareDifference(),compareDifference%(),compareAsStrings(),printSeparator()"));
		Assert.assertTrue("Compare definition 02",
				argumentsArray.contains("-column.compareDef=Count,Count,divideOlderBy(2),printYounger(),printOlder(),compareDifference(),compareAsStrings()"));
		Assert.assertTrue("Compare definition 03",
				argumentsArray.contains("-column.compareDef=Level,Level,divideYoungerBy(3),printOlder(),compareDifference%(),printSeparator()"));
		Assert.assertTrue("Compare definition 04",
				argumentsArray.contains("-column.compareDef=Level,Count,multiplyOlderBy(4),printYounger()"));
		Assert.assertTrue("Compare definition 05",
				argumentsArray.contains("-column.compareDef=Level,Description,multiplyYoungerBy(5),printMerged()"));
		Assert.assertTrue("Compare definition 06",
				argumentsArray.contains("-column.compareDef=Description,Count"));
		
	}
	
	@Test
	public void testConfigSaving() throws IOException {
		
		String loadFilePath = "./testdata/config_test_configLoading.txt";
		String resultFilePath = "./result/testConfigSaving.txt";
		
		CSVComparatorGUI.loadConfigFile(loadFilePath);
		CSVComparatorGUI.saveAsConfigFile(resultFilePath);
		
		AssertUtilities.assertCompareFilesLineByLine(loadFilePath, resultFilePath);
		
		new File(resultFilePath).delete();
	}
	
	@Test
	public void testBashSaving() throws IOException {
		
		String loadFilePath = "./testdata/config_test_configLoading.txt";
		String resultFilePath = "./result/testBashSaving.sh";
		String compareToFilePath = "./testdata/bash_test_saving_noArgs.sh";
		
		CSVComparatorGUI.loadConfigFile(loadFilePath);
		CSVComparatorGUI.saveAsBashScript(resultFilePath);
		
		AssertUtilities.assertCompareFilesLineByLine(resultFilePath, compareToFilePath);
		
		new File(resultFilePath).delete();
	}
	
	@Test
	public void testHandleQuotesDefault() throws IOException{
		
		String loadFilePath = "./testdata/config_test_quotes.txt";
		String resultFilePath = "./result/testHandleQuotesDefault.csv";
		
		CSVComparatorGUI.loadConfigFile(loadFilePath);
		
		defaultComparisonTab.activate();
		defaultCompareButton.click();
		
		AssertUtilities.assertFileLineByLine(resultFilePath, new String[]{
				"Fruit;Count(old);Count(young);Count Diff%",
				"Apple [1];10;8;-0.19999999999999996",
				"Apple [2];12;-;-",
				"Banana [1];5;5;0.0",
				"Banana [2];-;8008;-",
				"Lemon;-;16;-",
				"Orange;20;-;-",
				"Pear;7;17;1.4285714285714284"
				}
		);
		
		new File(resultFilePath).delete();
	}
	
	@Test
	public void testCompareTopLevelManualResultsQuotes() throws IOException{
		
		String loadFilePath = "./testdata/config_test_toplevel.txt";
		String resultFilePath = "./result/testCompareTopLevelManualResultsQuotes.csv";
		
		CSVComparatorGUI.loadConfigFile(loadFilePath);
		
		manualComparisonTab.activate();
		manualCompareButton.click();
		
		AssertUtilities.assertFileLineByLine(resultFilePath, new String[]{
				"\"Level\",\"Description\",\"Count(old)\",\"Count(young)\",\"Count Diff\",\"Count Diff%\",\"Count EQ?\",\" \"",
				"\"Top\",\"Warehouse A\",\"54\",\"92\",\"38.0\",\"0.7037037037037037\",\"NOTEQ\",\" \"",
				"\"Sub\",\"Orange\",\"20\",\"5\",\"-15.0\",\"-0.75\",\"NOTEQ\",\" \"",
				"\"Sub\",\"Apple [1]\",\"10\",\"77\",\"67.0\",\"6.7\",\"NOTEQ\",\" \"",
				"\"Sub\",\"Apple [2]\",\"12\",\"-\",\"-\",\"-\",\"-\",\" \"",
				"\"Sub\",\"Pear\",\"7\",\"2\",\"-5.0\",\"-0.7142857142857143\",\"NOTEQ\",\" \"",
				"\"Sub\",\"Banana\",\"5\",\"8\",\"3.0\",\"0.6000000000000001\",\"NOTEQ\",\" \"",
				"\"Top\",\"Warehouse B\",\"62\",\"62\",\"0.0\",\"0.0\",\"EQ\",\" \"",
				"\"Sub\",\"Orange\",\"15\",\"17\",\"2.0\",\"0.1333333333333333\",\"NOTEQ\",\" \"",
				"\"Sub\",\"Apple\",\"14\",\"12\",\"-2.0\",\"-0.1428571428571429\",\"NOTEQ\",\" \"",
				"\"Sub\",\"PassionFruit\",\"16\",\"18\",\"2.0\",\"0.125\",\"NOTEQ\",\" \"",
				"\"Sub\",\"Pear\",\"7\",\"9\",\"2.0\",\"0.2857142857142858\",\"NOTEQ\",\" \"",
				"\"Sub\",\"Banana\",\"10\",\"6\",\"-4.0\",\"-0.4\",\"NOTEQ\",\" \"",
				"\"Top\",\"Warehouse C\",\"16157\",\"13646\",\"-2511.0\",\"-0.155412514699511\",\"NOTEQ\",\" \"",
				"\"Sub\",\"Plum\",\"0\",\"-\",\"-\",\"-\",\"-\",\" \"",
				"\"Sub\",\"Melon\",\"-\",\"0\",\"-\",\"-\",\"-\",\" \"",
				"\"Sub\",\"Durian\",\"2\",\"2\",\"0.0\",\"0.0\",\"EQ\",\" \"",
				"\"Sub\",\"Cranberry\",\"10000\",\"12345\",\"2345.0\",\"0.23449999999999993\",\"NOTEQ\",\" \"",
				"\"Sub\",\"Strawberry\",\"600\",\"300\",\"-300.0\",\"-0.5\",\"NOTEQ\",\" \"",
				"\"Sub\",\"Blackberry\",\"5555\",\"999\",\"-4556.0\",\"-0.8201620162016201\",\"NOTEQ\",\" \"",
				"\"Top\",\"Warehouse D\",\"-\",\"238\",\"-\",\"-\",\"-\",\" \"",
				"\"Sub\",\"PassionFruit\",\"-\",\"16\",\"-\",\"-\",\"-\",\" \"",
				"\"Sub\",\"Mango\",\"-\",\"2\",\"-\",\"-\",\"-\",\" \"",
				"\"Sub\",\"Grapes\",\"-\",\"220\",\"-\",\"-\",\"-\",\" \""
				}
		);
		
		new File(resultFilePath).delete();
	}
		
		

}

