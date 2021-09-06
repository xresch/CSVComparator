package com.csvapi.test.global;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.csvapi.test.compare.TestCSVAPICompare;
import com.csvapi.test.compare.TestColumnMethods;
import com.csvapi.test.compare.TestCompareMethods;
import com.csvapi.test.model.TestCSVData;
import com.csvapi.test.transform.TestCSVAPITransform;

@RunWith(Suite.class)
@SuiteClasses({ TestColumnMethods.class, 
				TestCompareMethods.class,
	            TestCSVData.class,
	            TestCSVAPICompare.class,
	            TestCSVAPITransform.class
	          })

public class TestSuiteAll {

}
