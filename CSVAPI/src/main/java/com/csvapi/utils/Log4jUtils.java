package com.csvapi.utils;

import java.util.UUID;

import org.apache.logging.log4j.ThreadContext;

public class Log4jUtils {
	
	
	public static void setNewRequestIDForThread(boolean overrideIfExists){
		
		System.setProperty("isThreadContextMapInheritable", "true");
		
		if(overrideIfExists){
			ThreadContext.put("requestid", UUID.randomUUID().toString());
		}else{
			
			if(ThreadContext.get("requestid") == null){
				ThreadContext.put("requestid", UUID.randomUUID().toString());
			}
			
		}
	}
}
