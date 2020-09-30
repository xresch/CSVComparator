package com.csvcomparator.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/*************************************************************************
 * The DelimiterChooserCombo provides a easy way to choose the delimiter
 * for a CSV file. Also it provides a method to automatically find the
 * delimiter of a file.
 * 
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
public class DelimiterChooserCombo extends Combo {

	private LinkedHashMap<String, String> delimiterMap = new LinkedHashMap<String, String>();

	//####################################################################################
	// CONSTRUCTORS
	//####################################################################################
	public DelimiterChooserCombo(Composite parent, int style) {
		super(parent, style);

		//------------------------------------------------
		// Set delimiters
		//------------------------------------------------
		delimiterMap.put("Komma ','", ",");
		delimiterMap.put("Semicolon ';'", ";");
		delimiterMap.put("Hash '#'", "#");
		delimiterMap.put("Tab ( regex: '\\t+' )", "\t+");
		delimiterMap.put("Blank ( regex ' +' )", " +");
		delimiterMap.put("Blank to non-blank transition ( regex: '[ \\t]+' )", "[ \t]+");

		//------------------------------------------------
		// Delimiter
		//------------------------------------------------
		
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		this.setItems(delimiterMap.keySet().toArray(new String[delimiterMap.size()]));
	}
	
	//####################################################################################
	// OVERRIDDEN METHODS
	//####################################################################################
	@Override
	public void checkSubclass(){
		//prevents subclassing error
	}
	
	//####################################################################################
	// CLASS METHODS
	//####################################################################################
	/*************************************************************************
	 * Tries to resolve the delimiter of the given file by checking the first
	 * 10 lines of the file.
	 * 
	 * @param selectedFile a file with csv formatted content
	 *************************************************************************/ 
	public void resolveDelimiter(File selectedFile){
		
		if(selectedFile != null ){
			
			BufferedReader br = null;
			try {
				
				//------------------------------------------------
				// Check each delimiter in the delimiter map.
				//------------------------------------------------
				String[] delimiters = delimiterMap.values().toArray(new String[delimiterMap.size()]);
				for(int i = 0; i < delimiters.length ; i++ ){
					
					br = new BufferedReader(new FileReader(selectedFile));
					
					String delimiter = delimiters[i];
					boolean hasMoreLines = true;
					boolean isDelimiter = true;
					int previousDelimiterCount = 0;
					
					//------------------------------------------------
					//Check the first 10 lines for the delimiter
					//------------------------------------------------
					int counter = 1;
					while(hasMoreLines && counter <= 10 && isDelimiter){
						
						String currentLine = br.readLine();
						//------------------------------------------------
						// proceed if not end of file
						//------------------------------------------------
						if (currentLine != null){
							
							int currentDelimiterCount = currentLine.split(delimiter).length - 1;
							if(currentDelimiterCount > 0){
								
								//------------------------------------------------
								// if first line, only save the count without 
								// comparing
								//------------------------------------------------
								if (counter == 1){
									previousDelimiterCount = currentDelimiterCount;
								}else{
									if(currentDelimiterCount == previousDelimiterCount){
										previousDelimiterCount = currentDelimiterCount;
										isDelimiter = true;
									} else{
										isDelimiter = false;
									}
								}
							}else{
								isDelimiter = false;
							}
						}else{
							hasMoreLines = false;
						}
						
						counter++;
					}
					if(isDelimiter){
						this.select(i);
						break;
					}
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
	
	//####################################################################################
	// GETTERS & SETTERS
	//####################################################################################
	public String getDelimiter(){
		
		// Check if the value represents a key in the
		// delimitersMap and return the correct value
		String comboValue = this.getText();
		
		String[] delimiterKeys = delimiterMap.keySet().toArray(new String[delimiterMap.size()]);
		for(int i=0; i < delimiterKeys.length; i++){
			if(comboValue.equals(delimiterKeys[i])){
				return delimiterMap.get(comboValue);
			}
		}
		
		return this.getText();
	}
	
	public void setDelimiter(String delimiter){
		if(delimiterMap.containsValue(delimiter)){
			String[] values = delimiterMap.values().toArray(new String[0]);
			for(int i=0; i < values.length; i++){
				if(values[i].equals(delimiter)){
					String[] keys = delimiterMap.keySet().toArray(new String[0]);
					this.setText(keys[i]);
					break;
				}
			}
		}else{
			this.setText(delimiter);
		}
	}

	public LinkedHashMap<String, String> getDelimiterMap() {
		return delimiterMap;
	}
	
	
}
