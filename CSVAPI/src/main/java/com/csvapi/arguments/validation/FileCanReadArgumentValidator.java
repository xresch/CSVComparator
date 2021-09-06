package com.csvapi.arguments.validation;

import java.io.File;

import com.csvapi.arguments.ArgumentDefinition;


/**************************************************************************************
 * The FileCanReadArgumentValidator will validate if the value of the ArgumentDefinition
 * is a filepath and the file is readable.
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public class FileCanReadArgumentValidator extends AbstractArgumentValidator {

	
	public FileCanReadArgumentValidator(ArgumentDefinition argDefinition) {
		super(argDefinition);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean validate(String value) {
		
		File f = new File(value);
		if(f.canRead()){
			return true;
		}else{
			this.setInvalidMessage("File cannot be read: '"+value+"'");
			return false;
		}
		
	}

}
