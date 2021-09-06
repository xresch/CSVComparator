package com.csvapi.model;

import java.util.ArrayList;

/*************************************************************************
 * This class is used to hold the data of one column of the CSVData. 
 * 
 * @author Reto Scheiwiller, 2014
 * 
 *************************************************************************/

public class Column extends ArrayList<Object>{
	private static final long serialVersionUID = 154252L;
	private String header;
	
	//####################################################################################
	// CONSTRUCTORS
	//####################################################################################
	public Column(String header){
		this.header = header;
	}
	
	public Column(String header, ArrayList<Object> values){
		this.header = header;
		this.addAll(values);
	}
	
	//####################################################################################
	// GETTERS & SETTERS
	//####################################################################################
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}
}
