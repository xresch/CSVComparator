package com.csvapi.test.transform;

import java.io.File;

import org.junit.Test;

import com.csvapi.arguments.ArgumentsException;
import com.csvapi.test.global.AssertUtilities;
import com.csvapi.transform.CSVAPITransform;
import com.csvapi.transform.TransformArguments;
import com.csvapi.transform.TransformArgumentsBuilder;

public class TestCSVAPITransform {

	@Test
	public void testTransformXMLSingleFile() throws ArgumentsException {
		
		TransformArguments arguments = new TransformArguments();
		String resultFilepath = "./result/testTransformXMLSingleFile_result.csv";
		
		arguments.readArguments(
				new String[]{	
						TransformArguments.INPUT_PATH+"=./testdata/generaltest.xml", 
						TransformArguments.RESULT_FILE+"="+resultFilepath,
						TransformArguments.RESULT_DELIMITER+"=;",
						//TransformArguments.RESULT_STDOUT+"=true",
						//TransformArguments.CONFIG_LOGLEVEL_CONSOLE+"=DEBUG",
						TransformArguments.RESULT_PREFIX+"=testTransformXMLSingleFile"
				});
		
		CSVAPITransform.transfromAndWriteResult(arguments);
		
		AssertUtilities.assertFileLineByLine(resultFilepath, 
					new String[]{
				"Key;Value",
				"testTransformXMLSingleFile.generaltest.xml.root.cdatatest.contains;attr=bla",
				"testTransformXMLSingleFile.generaltest.xml.root.cdatatest.contains.cdata;<bla>       &blie&      \"blub\"",
				"testTransformXMLSingleFile.generaltest.xml.root.cdatatest.records.cdata;so many things",
				"testTransformXMLSingleFile.generaltest.xml.root.cdatatest.records.cdata;",
				"testTransformXMLSingleFile.generaltest.xml.root.log[0].event.level;DEBUG",
				"testTransformXMLSingleFile.generaltest.xml.root.log[0].event.msg;Key: -younger.label, Value:younger",
				"testTransformXMLSingleFile.generaltest.xml.root.log[0].location.class;com.csvcomparator.cli.main.CSVComparatorCLI",
				"testTransformXMLSingleFile.generaltest.xml.root.log[0].location.method;main",
				"testTransformXMLSingleFile.generaltest.xml.root.log[0].location.requestid;f30bb37c-5fdd-44d2-aec8-3d883f4a9ad4",
				"testTransformXMLSingleFile.generaltest.xml.root.log[0].time[0];attribute=attributeOnly",
				"testTransformXMLSingleFile.generaltest.xml.root.log[0].time[1];attribute=bla / count=234 / haha=hihi / text=2015-03-09 07:23:26.446",
				"testTransformXMLSingleFile.generaltest.xml.root.log[1].event.level;DEBUG",
				"testTransformXMLSingleFile.generaltest.xml.root.log[1].event.msg;    multiline    multiline    multiline    multiline    multiline   ",
				"testTransformXMLSingleFile.generaltest.xml.root.log[1].location.class;com.csvcomparator.cli.main.CSVComparatorCLI",
				"testTransformXMLSingleFile.generaltest.xml.root.log[1].location.method;main",
				"testTransformXMLSingleFile.generaltest.xml.root.log[1].location.requestid;f30bb37c-5fdd-44d2-aec8-3d883f4a9ad4",
				"testTransformXMLSingleFile.generaltest.xml.root.log[1].time;2015-03-09 07:23:26.446",
				"testTransformXMLSingleFile.generaltest.xml.root.log[2].location.class;com.csvcomparator.cli.main.CSVComparatorCLI",
				"testTransformXMLSingleFile.generaltest.xml.root.log[2].location.method;main",
				"testTransformXMLSingleFile.generaltest.xml.root.log[2].location.requestid[0];f30bb37c-5fdd-44d2-aec8-3d883f4a9ad4",
				"testTransformXMLSingleFile.generaltest.xml.root.log[2].location.requestid[1];f30bb37c-5fdd-44d2-aec8-3d883f4a9ad4",
				"testTransformXMLSingleFile.generaltest.xml.root.log[2].time[0];2015-03-09 07:23:26.446",
				"testTransformXMLSingleFile.generaltest.xml.root.log[2].time[1];2015-03-09 07:23:26.446",
				"testTransformXMLSingleFile.generaltest.xml.root.log[2].time[2];2015-03-09 07:23:26.446"
		});
		
		new File(resultFilepath).delete();
	}
	
	@Test
	public void testTransformArgumentsBuilder() throws ArgumentsException {
		
		String resultFilepath = "./result/testTransformXMLSingleFile_result.csv";
		
		TransformArguments arguments = new TransformArgumentsBuilder()
								.setInputPath("./testdata/generaltest.xml")
								.setResultFile(resultFilepath)
								.setResultDelimiter(";")
								.setResultPrefix("testTransformXMLSingleFile")
								.setInputFileFilter(".xml")
								.setInputFormat("xml")
								.setLogLevelConsole("INFO")
								.setLogLevelFile("DEBUG")
								.setResultSort(true)
								.build();
		
		if( !arguments.validateArguments() ){
			throw new ArgumentsException(arguments.getInvalidMessages());
		}else{
		
			CSVAPITransform.transfromAndWriteResult(arguments);
			
			AssertUtilities.assertFileLineByLine(resultFilepath, 
						new String[]{
					"Key;Value",
					"testTransformXMLSingleFile.generaltest.xml.root.cdatatest.contains;attr=bla",
					"testTransformXMLSingleFile.generaltest.xml.root.cdatatest.contains.cdata;<bla>       &blie&      \"blub\"",
					"testTransformXMLSingleFile.generaltest.xml.root.cdatatest.records.cdata;so many things",
					"testTransformXMLSingleFile.generaltest.xml.root.cdatatest.records.cdata;",
					"testTransformXMLSingleFile.generaltest.xml.root.log[0].event.level;DEBUG",
					"testTransformXMLSingleFile.generaltest.xml.root.log[0].event.msg;Key: -younger.label, Value:younger",
					"testTransformXMLSingleFile.generaltest.xml.root.log[0].location.class;com.csvcomparator.cli.main.CSVComparatorCLI",
					"testTransformXMLSingleFile.generaltest.xml.root.log[0].location.method;main",
					"testTransformXMLSingleFile.generaltest.xml.root.log[0].location.requestid;f30bb37c-5fdd-44d2-aec8-3d883f4a9ad4",
					"testTransformXMLSingleFile.generaltest.xml.root.log[0].time[0];attribute=attributeOnly",
					"testTransformXMLSingleFile.generaltest.xml.root.log[0].time[1];attribute=bla / count=234 / haha=hihi / text=2015-03-09 07:23:26.446",
					"testTransformXMLSingleFile.generaltest.xml.root.log[1].event.level;DEBUG",
					"testTransformXMLSingleFile.generaltest.xml.root.log[1].event.msg;    multiline    multiline    multiline    multiline    multiline   ",
					"testTransformXMLSingleFile.generaltest.xml.root.log[1].location.class;com.csvcomparator.cli.main.CSVComparatorCLI",
					"testTransformXMLSingleFile.generaltest.xml.root.log[1].location.method;main",
					"testTransformXMLSingleFile.generaltest.xml.root.log[1].location.requestid;f30bb37c-5fdd-44d2-aec8-3d883f4a9ad4",
					"testTransformXMLSingleFile.generaltest.xml.root.log[1].time;2015-03-09 07:23:26.446",
					"testTransformXMLSingleFile.generaltest.xml.root.log[2].location.class;com.csvcomparator.cli.main.CSVComparatorCLI",
					"testTransformXMLSingleFile.generaltest.xml.root.log[2].location.method;main",
					"testTransformXMLSingleFile.generaltest.xml.root.log[2].location.requestid[0];f30bb37c-5fdd-44d2-aec8-3d883f4a9ad4",
					"testTransformXMLSingleFile.generaltest.xml.root.log[2].location.requestid[1];f30bb37c-5fdd-44d2-aec8-3d883f4a9ad4",
					"testTransformXMLSingleFile.generaltest.xml.root.log[2].time[0];2015-03-09 07:23:26.446",
					"testTransformXMLSingleFile.generaltest.xml.root.log[2].time[1];2015-03-09 07:23:26.446",
					"testTransformXMLSingleFile.generaltest.xml.root.log[2].time[2];2015-03-09 07:23:26.446"
			});
			
			new File(resultFilepath).delete();
		}
	}
	
	@Test
	public void testTransformXMLFolder() throws ArgumentsException {
		
		TransformArguments arguments = new TransformArguments();
		String resultFilepath = "./result/testTransformXMLFolder_result.csv";
		
		arguments.readArguments(
				new String[]{	
						TransformArguments.INPUT_PATH+"=./testdata/xmlfolder", 
						TransformArguments.RESULT_FILE+"="+resultFilepath,
						TransformArguments.RESULT_DELIMITER+"=;",
						//TransformArguments.RESULT_STDOUT+"=true",
						//TransformArguments.CONFIG_LOGLEVEL_CONSOLE+"=DEBUG",
						TransformArguments.RESULT_PREFIX+"=testTransformXMLFolder"
				});
		
		CSVAPITransform.transfromAndWriteResult(arguments);
		
		AssertUtilities.assertFileLineByLine(resultFilepath, 
					new String[]{
					"Key;Value",
					"testTransformXMLFolder.xmlfolder.subfolder1.file1.xml.root.description;folder test",
					"testTransformXMLFolder.xmlfolder.subfolder1.file1.xml.root.folder;id=sub1",
					"testTransformXMLFolder.xmlfolder.subfolder2.file2.xml.root.description;Test folder structure",
					"testTransformXMLFolder.xmlfolder.subfolder2.file2.xml.root.folder;id=sub2",
					"testTransformXMLFolder.xmlfolder.topfile.xml.root.description;In parent folder",
					"testTransformXMLFolder.xmlfolder.topfile.xml.root.folder;id=parent"
		});
		
		new File(resultFilepath).delete();
	}
	
	@Test
	public void testMandatoryInput() throws ArgumentsException {
		
		TransformArguments arguments = new TransformArguments();
		
		//default output path
		String resultFilepath = "./transform_result.csv";
		
		arguments.readArguments(
				new String[]{	
						TransformArguments.INPUT_PATH+"=./testdata/smalltest.xml", 
						//TransformArguments.RESULT_STDOUT+"=true",
						//TransformArguments.CONFIG_LOGLEVEL_CONSOLE+"=DEBUG",
				});
		
		CSVAPITransform.transfromAndWriteResult(arguments);
		
		AssertUtilities.assertFileLineByLine(resultFilepath, 
					new String[]{
					"Key,Value",
					"smalltest.xml.root.test.attrAndContent,attribute=bla / count=234 / haha=hihi / text=2015-03-09 07:23:26.446",
					"smalltest.xml.root.test.attrOnly,attribute=attributeOnly",
					"smalltest.xml.root.test.cdatatest.cdata,so many things",
					"smalltest.xml.root.test.cdatatest.cdata,",
					"smalltest.xml.root.test.msg,   multiline   multiline   multiline   multiline   multiline  ",
					"smalltest.xml.root.test.subelements.class,com.csvcomparator.cli.main.CSVComparatorCLI",
					"smalltest.xml.root.test.subelements.method,main",
					"smalltest.xml.root.test.subelements.requestid,f30bb37c-5fdd-44d2-aec8-3d883f4a9ad4"
		});
		
		new File(resultFilepath).delete();
	}
	
	@Test
	public void testConfigFile() throws ArgumentsException {
		
		TransformArguments arguments = new TransformArguments();
		
		arguments.readArguments(
				new String[]{	
					TransformArguments.CONFIG_FILE+"=./testdata/transform.config", 
				});
		
		//get result path from read arguments
		String resultFilepath = arguments.getValue(TransformArguments.RESULT_FILE);
		
		CSVAPITransform.transfromAndWriteResult(arguments);
		
		AssertUtilities.assertFileLineByLine(resultFilepath, 
					new String[]{
					"Key;Value",
					"MyPrefix.smalltest.xml.root.test.attrOnly;attribute=attributeOnly",
					"MyPrefix.smalltest.xml.root.test.attrAndContent;attribute=bla / count=234 / haha=hihi / text=2015-03-09 07:23:26.446",
					"MyPrefix.smalltest.xml.root.test.subelements.requestid;f30bb37c-5fdd-44d2-aec8-3d883f4a9ad4",
					"MyPrefix.smalltest.xml.root.test.subelements.class;com.csvcomparator.cli.main.CSVComparatorCLI",
					"MyPrefix.smalltest.xml.root.test.subelements.method;main",
					"MyPrefix.smalltest.xml.root.test.msg;   multiline   multiline   multiline   multiline   multiline  ",
					"MyPrefix.smalltest.xml.root.test.cdatatest.cdata;so many things",
					"MyPrefix.smalltest.xml.root.test.cdatatest.cdata;"
		});
		
		new File(resultFilepath).delete();
	}
	
}
