package com.workfront;


import com.workfront.api.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.lang.Object;

import org.json.JSONObject;



public class DocumentMover {

		//All of these will depend on the environment
	public static final String WORKFRONT_URL_V4 = "https://leapco.attask-ondemand.com/attask/api/v4.0";
	public static final String API_KEY = "r44o0uldiz5ub9u4af6ieymkxfruuepi";
	public static final String DIRECTORY = "PATH_TO_MAIN_DIRECTORY";
	public static final String DIRECTORY_COMPLETE = "PATH_TO_COMPLETE_DIRECTORY";
	public static final String DIRECTORY_FAILED = "PATH_TO_FAILED_DIRECTORY";
	

	/**
	 * The purpose of this class is to automate the entry of documents from another system into workfront.  When
	 * a file is dropped into a certain directory, we will upload the document to the appropriate object and
	 * update the object with a message saying that we have moved the document into the object automatically.  We
	 * will use a basic folder structure to get documents to the correct object. Once a document is uploaded, it will
	 * be moved to another directory.
	 *
	 * So to recap, the requirements are:
	 *
	 * 1. Setup a root "input" directory that contains 3 children: project, task and issue
	 * 2. When a directory is found under any of these children that looks like an ID (32 characters [0-9a-f])
	 *    attempt to upload any files in the directory to the corresponding object
	 * 3. If the object cannot be found, move the document to a corresponding folder structure under a "failures" directory
	 * 4. If a file is uploaded correctly to the object, move it to a "complete" directory with the same folder structure
	 *
	 * @param args
	 */
	public static void main(String[] args) {
			try {
				//Create client Instance
				StreamClient client = new StreamClient(WORKFRONT_URL_V4, API_KEY);
				//Searches Issue directory for files and list files
				File path = new File(DIRECTORY+"/Issue");
				File[] files = path.listFiles();
				
						//Cycles through each file			
				        for(File file:files)
				         {
				        	//Remove file extension to get ID of issue to search
				        	String fileName = file.getName();
				        	String ID = fileName.substring(0, fileName.lastIndexOf('.'));
				        	try {
				        		//Check if Issue exists if it does continue
				        		client.get("Issue", ID);
				        		//Gets Handle for upload
					        	JSONObject results = client.upload(file);
					        	//Builds Map for POST
					        	Map<String, Object> message = new HashMap<String, Object>();
					        	message.put("objID" , ID);
					        	message.put( "docObjCode" , "OPTASK");
					        	message.put("handle", results.get("handle"));
					        	message.put("name", file);
					        	//Final post to upload document to Workfront
					        	client.post("document", message);
					        	//Move file to Complete directory
					        	file.renameTo(new File(DIRECTORY_COMPLETE+fileName));
				        	}catch(Exception f){
				        		file.renameTo(new File(DIRECTORY_FAILED+fileName));
				        	}

				         }

			    //Searches Task directory for files and list files
				path = new File(DIRECTORY+"/Task");
				files = path.listFiles();
					
						//Cycles through each File			
				        for(File file:files)
				         {
				        	//Remove file extension to get ID of Task to search
				        	String fileName = file.getName();
				        	String ID = fileName.substring(0, fileName.lastIndexOf('.'));
				        	try {
				        		//Check if Task exists if it does continue
				        		client.get("Task", ID);
				        		//Gets Handle for upload
					        	JSONObject results = client.upload(file);
					        	//Builds Map for POST
					        	Map<String, Object> message = new HashMap<String, Object>();
					        	message.put("objID" , ID);
					        	message.put( "docObjCode" , "TASK");
					        	message.put("handle", results.get("handle"));
					        	message.put("name", file);
					        	//Final post to upload document to Workfront
					        	client.post("document", message);
					        	//Move file to Complete directory
					        	file.renameTo(new File(DIRECTORY_COMPLETE+fileName));
				        	}catch(Exception f){
				        		//If Task does not exsist move to failed directory
				        		file.renameTo(new File(DIRECTORY_FAILED+fileName));
				        	}
				         }			
					        
						      //Searches Project directory for files and list files
						path = new File(DIRECTORY+"/Project");
						files = path.listFiles();
							
								//Cycles through each file			
						        for(File file:files)
						         {
						        	//Remove file extension to get ID of issue to search
						        	String fileName = file.getName();
						        	String ID = fileName.substring(0, fileName.lastIndexOf('.'));
						        	try {
						        		//Check if Issue exists if it does continue
						        		client.get("Proj", ID);
						        		//Gets Handle for upload
							        	JSONObject results = client.upload(file);
							        	//Builds Map for POST
							        	Map<String, Object> message = new HashMap<String, Object>();
							        	message.put("objID" , ID);
							        	message.put( "docObjCode" , "PROJ");
							        	message.put("handle", results.get("handle"));
							        	message.put("name", file);
							        	//Final post to upload document to Workfront
							        	client.post("document", message);
							        	//Move file to Complete directory
								        file.renameTo(new File(DIRECTORY_COMPLETE+fileName));
						        	}catch(Exception f){
						        		file.renameTo(new File(DIRECTORY_FAILED+fileName));
						        		}
						         }							        
				        
				}catch (Exception e) {
					e.printStackTrace();
				}
	}
}