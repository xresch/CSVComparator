package com.csvapi.arguments.validation;

import com.csvapi.arguments.ArgumentDefinition;

/**************************************************************************************
 * The RegexArgumentValidator will validate if the value of the ArgumentDefinition
 * is matching the given regular expression.
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public class RegexArgumentValidator extends AbstractArgumentValidator {

	private String pattern="";
	
	public RegexArgumentValidator(ArgumentDefinition argDef, String pattern){
		super(argDef);
		this.pattern = pattern;
	}
	
	@Override
	public boolean validate(String value) {
		
		if(value.matches(pattern)){
			return true;
		}else{
			StringBuffer sb = new StringBuffer();
			sb.append("The value of the argument ");
			sb.append(this.getArgument().getKey());
			sb.append(" did not match the pattern '");
			sb.append(pattern);
			sb.append("'.(value='");
			sb.append(value);
			sb.append("')");
			
			this.setInvalidMessage(sb.toString());
			
			return false;
		}
	}


}
