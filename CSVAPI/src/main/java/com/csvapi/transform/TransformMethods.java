package com.csvapi.transform;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.csvapi.compare.CSVAPICompare;
import com.csvapi.model.CSVData;
import com.csvapi.model.Column;

/**************************************************************************************
 * This class contains the algorithms for transforming files to CSV format.
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public class TransformMethods {

	public static final Logger logger = LogManager.getLogger(CSVAPICompare.class.getName());

	/**************************************************************************************
	 * Transforms the input file or folder as defined by the arguments.
	 *
	 * @param arguments the arguments used for transformation
	 **************************************************************************************/
	public static CSVData transform(TransformArguments arguments){
		
		String inputFormat = arguments.getValue(TransformArguments.INPUT_FORMAT);
		String resultSort = arguments.getValue(TransformArguments.RESULT_SORT);
		
		CSVData result = null;
		
		if(inputFormat.toLowerCase().equals("xml")){
			result = transformXML(arguments);
			
		}else{
			logger.warn("Specified input format is not supported.");
		}
			
		if (resultSort.toLowerCase().equals("true")){
			logger.debug("Sort result");
			result.sortCSVData(0);
		}
		
		return result;
		
	}
	
	/**************************************************************************************
	 * Transforms the input file containing xml data to csv as defined by the arguments.
	 *
	 * @param arguments the arguments used for transformation
	 **************************************************************************************/	
	private static CSVData transformXML(TransformArguments arguments){

		logger.debug("Start Transform XML input");
		
		CSVData result = new CSVData("Transform Result");
		result.addColumn(new Column("Key"));
		result.addColumn(new Column("Value"));
		
		String prefix = arguments.getValue(TransformArguments.RESULT_PREFIX);
		String inputPath = arguments.getValue(TransformArguments.INPUT_PATH);
		
		File file = new File(inputPath);
		
		if(file.isDirectory()){
			transformXMLFromFolder(arguments, prefix, file, result);
		}else{
			transformXMLFile(arguments, prefix, file, result);
			
		}
		
		return result;
	}
	
	/**************************************************************************************
	 * Iterates over a folder and transforms each xml-file found to csv values, filtered
	 * by the file filter defined in the arguments.
	 * 
	 * This method is called recursively for each subfolder found.
	 * 
	 * @param arguments the arguments used for transformation
	 * @param prefix the prefix added to the key to represent the folder/file/node structure.
	 * @param folder the folder containing the files to transform
	 * @param result the CSVData instance were the results will be stored
	 * 
	 **************************************************************************************/	
	private static void transformXMLFromFolder(TransformArguments arguments, String prefix, File folder, CSVData result){
		
		logger.debug("Transform XML in Folder: "+folder);
		
		if(folder.isDirectory() && folder.canRead()){
			
			//######################################
			// Create Prefix
			//######################################
			String folderName = folder.getName();
			
			if(prefix.equals("")){
				prefix = folderName;
			}else{
				prefix = prefix + "." + folderName;
			}
			
			//######################################
			// Traverse folders
			//######################################
			for(File currentFolder : folder.listFiles()){
				if(currentFolder.isDirectory()){
					if(currentFolder.canRead()){
						TransformMethods.transformXMLFromFolder(arguments, prefix, currentFolder, result);
					}else{
						logger.error("Specified folder could not be read: "+currentFolder.getPath());
					}
				}
			}
			
			//######################################
			// Traverse files
			//######################################
			
			String fileFilterRegex = arguments.getValue(TransformArguments.INPUT_FILEFILTER);
			
			for(File currentFile : folder.listFiles()){
				if(currentFile.isFile() && currentFile.getName().matches(fileFilterRegex)){
					if(currentFile.canRead()){
						TransformMethods.transformXMLFile(arguments, prefix, currentFile, result);
					}else{
						logger.error("Specified file could not be read: "+currentFile.getPath());
					}
				}
			}
			
		}else{
			logger.error("[ERROR] Specified folder could not be read: "+folder);
		}
		
	}

	/**************************************************************************************
	 * Transforms an xml-file to csv values, filtered
	 * by the file filter defined in the arguments.
	 *
	 * @param arguments the arguments used for transformation
	 * @param prefix the prefix added to the key to represent the folder/file/node structure.
	 * @param file the file that should be transformed
	 * @param result the CSVData instance were the results will be stored
	 **************************************************************************************/		
	private static void transformXMLFile(TransformArguments arguments, String prefix, File file, CSVData result) {
		
		logger.debug("Transform XML file: "+file);
		
		//######################################
		// Create Prefix
		//######################################
		String fileName = file.getName();
		
		if(prefix.equals("")){
			prefix = fileName;
		}else{
			prefix = prefix + "." + fileName;
		}
		
		//######################################
		// Parse document
		//######################################
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder builder;
		
		try {
			builder = dbf.newDocumentBuilder();
			builder.setEntityResolver(new EntityResolver() {

	            @Override
	            public InputSource resolveEntity(String publicId, String systemId)
	                    throws SAXException, IOException {
	            	// Ignore .dtd-Files
	                return new InputSource(new StringReader(""));
	            }
	        });

			Document document = builder.parse(file);
			NodeList nodes = document.getChildNodes();
			
			transformNodesToCSV(prefix, nodes, result);
			
			builder.reset();
		} catch (ParserConfigurationException e) { e.printStackTrace();}
		catch (SAXException e) {e.printStackTrace();}
		catch ( IOException e) {e.printStackTrace();}
	
	}
	
	/**************************************************************************************
	 * Transforms the nodes of the nodelist to csv key value pairs.
	 * This method will be called recursively for each child node.
	 * 
	 * @param parentKey the prefix added to the key to represent the folder/file/node structure.
	 * @param nodes the node list to transform
	 * @param result the CSVData instance were the results will be stored
	 **************************************************************************************/	
	private static void transformNodesToCSV(String parentKey, NodeList nodes, CSVData result){
		

		for(int i = 0; i < nodes.getLength(); i++){
			Node currentNode = nodes.item(i);

			if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
				
				String currentName = currentNode.getNodeName();
					
				//###########################################
				// Get Element Text
				//###########################################
				String elementText = "";
				
				if(currentNode.hasChildNodes()){
					
					Node firstChild = currentNode.getFirstChild();
					
					if(firstChild.getNodeType() == Node.TEXT_NODE){
						elementText = firstChild.getNodeValue();
						elementText = elementText.replaceAll("\r\n|\r|\n|\t", " ");
					}
				}
					
				//###########################################
				// Create unique key 
				//###########################################
				String nextKey = parentKey + "." + currentName;
				
				// Create a list which only contain the element nodes
				// with the same name as the currentNode
				ArrayList<Node> filteredElementNodes = new ArrayList<Node>();
				for(int j = 0; j < nodes.getLength(); j++){
					Node node = nodes.item(j);
					if(   node.getNodeType() == Node.ELEMENT_NODE
					   && node.getNodeName().equals(currentName)){
						filteredElementNodes.add(nodes.item(j));
					}
				
				}
				
				//Place an index 
				for( Node element : filteredElementNodes){
					String checkName = element.getNodeName();
					if(currentName.equals(checkName) && !element.equals(currentNode)){
						int index = filteredElementNodes.indexOf(currentNode);
						nextKey = nextKey + "[" + index + "]";
						break;
					}
				}

				//###########################################
				// Create Value: check for Attributes
				//###########################################
				StringBuilder currentValue = new StringBuilder();
				
				if(currentNode.hasAttributes()){
					NamedNodeMap attributes = currentNode.getAttributes();
					
					for(int k = 0; k < attributes.getLength();k++){
						Node attribute = attributes.item(k);
						currentValue.append(attribute.getNodeName())
									.append("=")
									.append(attribute.getNodeValue());

						//add comma if it is not the last attribute
						if(k != attributes.getLength()-1 ) {
							currentValue.append(" / ");
						}
					}
					
					if(!elementText.toString().trim().isEmpty()){
						currentValue.append(" / text=")
									.append(elementText);
					}
				}else{
					currentValue.append(elementText);
				}
				
				//###########################################
				// Add to result CSVData
				//###########################################
				if(!currentValue.toString().trim().isEmpty() ){
					result.addStringsAsRow(nextKey,currentValue.toString());
					logger.debug("Transformed xml-Node to csv: "+nextKey+" >> "+currentValue.toString());
				}
				
				if(currentNode.hasChildNodes()){
					transformNodesToCSV(nextKey, currentNode.getChildNodes(), result);
				}
				
			}else if(currentNode.getNodeType() == Node.CDATA_SECTION_NODE){
				String key = parentKey+".cdata";
				String value = currentNode.getNodeValue().replaceAll("\n|\r\n|\t", " ");
				result.addStringsAsRow(	key, value);
				logger.debug("Transformed xml-Node: "+key+" >> "+value);
			}
		}
	}
}