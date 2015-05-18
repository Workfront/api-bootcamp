package com.workfront;

import com.workfront.api.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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

public class RouteIssuesToTeam {

	//All of these will depend on the environment
	public static final String WORKFRONT_URL_V4 = "https://leapco.attask-ondemand.com/attask/api/v4.0";
	public static final String API_KEY = "r44o0uldiz5ub9u4af6ieymkxfruuepi";
	public static final String PRODUCT_AREA_FIELD = "DE:Product Area";
	public static final String SOFTWARE_TICKET_CATEGORY_ID = "1586d59dcb97400ae0533365290ac8b8";
	public static final String WORKSTATION_REQUESTS_ID = "1586d59dc388400ae0533365290ac8b8";
	public static final String DEVELOPMENT_PRODUCT_BACKLOG_ID = "1586d59dc376400ae0533365290ac8b8";
	public static final String JEDI_COUNCIL_ID = "1586d59dc61c400ae0533365290ac8b8";
	public static final String GRYFFINDORE_ID = "1586d59dc652400ae0533365290ac8b8";

	/**
	 * The purpose of this class is to route incoming issues to the proper team and convert them into tasks
	 * on their backlog.  We will mark the incoming issues as resolved by our new tasks on the backlog and
	 * leave an update on the update stream for the issue that it will be completed by our new task.
	 * These issues are categorized by product ownership since the groups are separated
	 * across multiple products.  This task will require additional setup in workfront.
	 *
	 * So to recap, the requirements are:
	 *
	 * In workfront
	 * 1. Create a custom field named "Product Area" that has the following values and add it to a form:
	 *              "User Management", "Calendars", "Document Management", "All The Rest"
	 * 2. Either alter an existing request queue or create a new request queue to take this new value as an option.
	 *
	 * In this class
	 * 3. Find all issues that come in through this queue and have a Product Area set and are not resolved by another
	 *    object and convert them to a task on the "Development Product Backlog" project.
	 * 4. If the value is "User Management" or "Calendars" or "Document Management", ensure it shows up on the
	 *    Jedi Council team's backlog.
	 *        If the value is "All The Rest," ensure it shows up on the Gryffindore backlog.
	 * 5. Update the issue with a message that the work will be done in a task and reference the task ID.
	 * 6. Set the issue to be resolved by the new Task.
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			StreamClient client = new StreamClient(WORKFRONT_URL_V4, API_KEY);

			//find all issues with our category - we want to narrow the scope to the project these issues land on
			//as well as issues that are not already resolved by a task.
			//This should perform well because we have used 2 primary keys (IDs) even as our data grows.
			Map<String, Object> search = new HashMap<String, Object>();
			search.put("projectID", WORKSTATION_REQUESTS_ID);
			search.put("categoryID", SOFTWARE_TICKET_CATEGORY_ID);
			search.put("resolvingObjID_Mod", "isnull");

			//We will need to fetch the fields we want to copy in this search like the description
			JSONArray issues = client.search("OPTASK", search,
					new HashSet<String>(Arrays.asList("parameterValues", "description")));

			for (int i = 0; i < issues.length(); i++) {
				JSONObject issue = issues.getJSONObject(i);

				//Ignore issues without the value set that we care about
				if (issue.has("parameterValues") && issue.getJSONObject("parameterValues").has(PRODUCT_AREA_FIELD)) {

					//Figure out which team this belongs to so it shows up on the proper backlog
					String teamID = JEDI_COUNCIL_ID;
					if (!"All The Rest".equals(issue.getJSONObject("parameterValues").get(PRODUCT_AREA_FIELD))) {
						teamID = GRYFFINDORE_ID;
					}

					//Since there is no endpoint to convert an issue to a task, we'll just create a task on the
					//project in question and copy over the most important data
					Map<String, Object> message = new HashMap<String, Object>();
					message.put("projectID", DEVELOPMENT_PRODUCT_BACKLOG_ID);
					message.put("name", issue.get("name"));
					message.put("description", issue.get("description"));
					message.put("teamID", teamID);

					JSONObject newTask = client.post("TASK", message);

					//Reference new task in update stream
					message.clear();
					message.put("noteText", "This has been converted to task: " + newTask.get("ID"));
					message.put("objID", issue.get("ID"));
					message.put("noteObjCode", "OPTASK");
					client.post("NOTE", message);

					//Mark the issue as resolved by our new task
					message.clear();
					message.put("resolvingObjID", newTask.get("ID"));
					message.put("resolvingObjCode", "TASK");
					client.put("OPTASK", issue.get("ID").toString(), message);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
