package com.csvapi.arguments.validation;

import java.io.File;
import java.io.IOException;

import com.csvapi.arguments.ArgumentDefinition;


/**************************************************************************************
 * The FileCanWriteArgumentValidator will validate if the value of the ArgumentDefinition
 * is a filepath and the application can write to this path.
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public class FileCanWriteArgumentValidator extends AbstractArgumentValidator {

	public FileCanWriteArgumentValidator(ArgumentDefinition argDefinition) {
		super(argDefinition);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean validate(String value) {
		
		String filepath = value;
		
		if(filepath != null){
			
			File f = new File(filepath);
			if(f.exists()){
				if(!f.canWrite()){
					this.setInvalidMessage("File cannot be written: '"+value+"'");
					return false;
				}
			}else{
				try {
					f.mkdirs();
					f.createNewFile();
					f.delete();
				} catch (IOException e) {
					return false;
				}
			}
		}else{
			this.setInvalidMessage("Result filepath is null.");
			return false;
		}
		
		return true;
		
	}

}
