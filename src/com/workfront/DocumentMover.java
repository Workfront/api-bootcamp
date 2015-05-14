package com.workfront;


import com.workfront.api.*;

import java.io.File;
import java.util.*;
import java.lang.Object;

import org.json.JSONObject;

/*
 * Copyright (c) 2015 Workfront, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

public class DocumentMover {

	//All of these will depend on the environment
	public static final String WORKFRONT_URL_V4 = "https://leapco.attask-ondemand.com/attask/api/v4.0";
	public static final String API_KEY = "r44o0uldiz5ub9u4af6ieymkxfruuepi";
	public static final String DIRECTORY = "PATH_TO_MAIN_DIRECTORY";
	public static final String DIRECTORY_COMPLETE = "PATH_TO_COMPLETE_DIRECTORY";
	public static final String DIRECTORY_FAILED = "PATH_TO_FAILED_DIRECTORY";
	public static final Map<String, String> DIRECTORY_TO_OBJCODE;

	static {
		Map<String, String> directoryToObjCode = new HashMap<String, String>();
		directoryToObjCode.put("Issue", "OPTASK");
		directoryToObjCode.put("Task", "TASK");
		directoryToObjCode.put("Project", "PROJ");

		DIRECTORY_TO_OBJCODE = Collections.unmodifiableMap(directoryToObjCode);
	}

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
	 * attempt to upload any files in the directory to the corresponding object
	 * 3. If the object cannot be found, move the document to a corresponding folder structure under a "failures" directory
	 * 4. If a file is uploaded correctly to the object, update the object with a message saying the document was automatically
	 *    added and move it to a "complete" directory with the same folder structure
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//Create client Instance
			StreamClient client = new StreamClient(WORKFRONT_URL_V4, API_KEY);

			//Iterate over directories and move files
			for (Map.Entry<String, String> entry : DIRECTORY_TO_OBJCODE.entrySet()) {

				//Searches Object directory for files and list files, eg: /root/Issue
				File objectDirectory = new File(DIRECTORY + "/" + entry.getKey());

				if (objectDirectory.exists()) {
					//Cycles through each file
					for (File subdirectory : objectDirectory.listFiles()) {
						String objID = subdirectory.getName();

						//Check that this directory looks like a GUID, eg: /root/Issue/55540a5e0009472b9485da4324bec42b
						if (subdirectory.isDirectory() && objID.matches("^[0-9A-Fa-f]{32}$")) {

							//go through the files in this directory and attempt to add them to the object
							for (File file : subdirectory.listFiles()) {
								try {
									//Check if object exists if it does continue... this is cheaper than
									//uploading a file only to find out we can't attach it to anything
									client.get(entry.getKey(), objID);

									//Gets Handle for upload
									JSONObject results = client.upload(file);

									//Builds Map for POST
									Map<String, Object> message = new HashMap<String, Object>();
									message.put("objID", objID);
									message.put("docObjCode", entry.getValue());
									message.put("handle", results.get("handle"));
									message.put("name", file.getName());

									//Final post to upload document to Workfront
									client.post("document", message);

									//Post a note about the new document
									message.clear();
									message.put("noteText", "Automatically uploaded file: " + file.getName());
									message.put("objID", objID);
									message.put("noteObjCode", entry.getValue());

									client.post("NOTE", message);

									//Move file to Complete directory
									File completeDir = new File(DIRECTORY_COMPLETE + "/" + entry.getKey() + "/" + subdirectory.getName());
									completeDir.mkdirs();
									file.renameTo(new File(completeDir, file.getName()));
								} catch (StreamClientException f) {
									File errorDir = new File(DIRECTORY_FAILED + "/" + entry.getKey() + "/" + subdirectory.getName());
									errorDir.mkdirs();
									file.renameTo(new File(errorDir, file.getName()));
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}