package com.csvapi.model;

import java.util.ArrayList;

/*************************************************************************
 * This class is an extention of CSVData and provides the possibility to
 * build a hierarchical structure of CSVData. 
 * <p>
 *        !!!!!!!!! UNDER CONSTRUCTION !!!!!!!!!!
 *      
 * @author Reto Scheiwiller, 2014
 * 
 *************************************************************************/

public class HierarchicalCSVData extends CSVData{
	private static final long serialVersionUID = 154252L;
	
	private int level = -1;
	private HierarchicalCSVData parent;
	private ArrayList<HierarchicalCSVData> csvDataChildrenArray;
	
	//####################################################################################
	// CONSTRUCTORS
	//####################################################################################
	public HierarchicalCSVData(String name){
		super(name);
	}
	
	public HierarchicalCSVData(String name, HierarchicalCSVData parent){
		super(name);
		this.parent = parent;
	}
		
	//####################################################################################
	// GETTERS & SETTERS
	//####################################################################################
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}

	public ArrayList<HierarchicalCSVData> getSubCSVData() {
		return csvDataChildrenArray;
	}

	public void addCSVDataChild(HierarchicalCSVData hirachicalCSVData) {
		this.csvDataChildrenArray.add(hirachicalCSVData);
	}
	
}
