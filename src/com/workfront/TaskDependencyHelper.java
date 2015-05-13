package com.workfront;

import com.workfront.api.*;
import org.json.*;

import java.util.*;

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

public class TaskDependencyHelper {

	//All of these will depend on the environment
	public static final String WORKFRONT_URL_V4 = "https://leapco.attask-ondemand.com/attask/api/v4.0";
	public static final String API_KEY = "r44o0uldiz5ub9u4af6ieymkxfruuepi";
	public static final String DEVELOPMENT_PRODUCT_BACKLOG_ID = "1586d59dc376400ae0533365290ac8b8";
	public static final String STORY_CATEGORY_ID = "1586d59dcb9a400ae0533365290ac8b8";
	public static final String TECH_APPROACH_NAME = "Technical Approach";
	public static final String UX_DESIGN_NAME = "UX Design";
	public static final String QA_TEST_MAP_NAME = "QA Test Map";

	/**
	 * The purpose of this class is to ensure that certain types of tasks that are entered follow the proper
	 * process to facilitate consistent reporting.  When a task is entered under the Development Product Backlog
	 * project and is and has the form "Story" associated with it, we want to ensure that the story has a certain
	 * set of subtasks.
	 *
	 * So to recap, the requirements are:
	 *
	 * 1. When a task on the Development Product Backlog has the custom form "Story" selected and is assigned
	 *    to an agile team, ensure the following subtasks exist and are assigned to the same team as the story:
	 *    	"Technical Approach", "UX Design", "QA Test Map"
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			StreamClient client = new StreamClient(WORKFRONT_URL_V4, API_KEY);

			//One consideration is to change the updateType on the project to manual while inserting subtasks
			//and then returning to the value it was when the script was run.  This would prevent the timeline
			//from running on every insert and then it would be run when the updateType is returned to its
			//existing setting.  This would greatly speed up the script if it tends to insert a lot of tasks
			//frequently.

			//Find our stories to check.  Some important points to consider:
			// * We don't care about completed work.  This keeps us from getting really slow over time as more work is done.
			// * We are using a primary key to reduce the set significantly with projectID
			// * We are only looking at stories with the custom form we need and a team already assigned
			Map<String, Object> search = new HashMap<String, Object>();
			search.put("projectID", DEVELOPMENT_PRODUCT_BACKLOG_ID);
			search.put("statusEquatesWith", "CPL");
			search.put("statusEquatesWith_Mod", "ne");
			search.put("categoryID", STORY_CATEGORY_ID);
			search.put("teamID_Mod", "notnull");

			JSONArray stories = client.search("TASK", search, new HashSet<String>(Arrays.asList("teamID")));

			Map<String, JSONObject> storiesByID = new HashMap<String, JSONObject>();
			for (int i = 0; i < stories.length(); i++) {
				JSONObject story = stories.getJSONObject(i);

				storiesByID.put(story.getString("ID"), story);
			}

			//Now that we have the set of stories, search for all of their children in one api call.
			//If we knew this set would grow to a large amount, we might iterate over a page 2000 at a time.
			search.clear();
			search.put("projectID", DEVELOPMENT_PRODUCT_BACKLOG_ID);
			search.put("parentID", storiesByID.keySet().toArray(new String[storiesByID.keySet().size()]));
			search.put("$$LIMIT", 2000);

			JSONArray children = client.search("TASK", search, new HashSet<String>(Arrays.asList("parentID")));

			//We are putting these all in memory because we plan on the set being reasonable.  Should we
			//know that the set will tend to be very large, we might get children on pages of parent tasks
			//each time instead.  These are all considerations to be made during planning and development
			//based on typical workflow.
			Map<String, List<JSONObject>> childrenByParentID = new HashMap<String, List<JSONObject>>();
			for (int i = 0; i < children.length(); i++) {
				JSONObject child = children.getJSONObject(i);

				List<JSONObject> siblings = childrenByParentID.get(child.getString("parentID"));
				if (siblings == null) {
					siblings = new ArrayList<JSONObject>();
					childrenByParentID.put(child.getString("parentID"), siblings);
				}

				siblings.add(child);
			}

			//Now that I have the immediate children of each story, we can check them for missing tasks
			for (Map.Entry<String, JSONObject> entry : storiesByID.entrySet()) {

				//We move the decisions into the code and not through the API.  We could have done this
				//through searches, but it is much more efficient for the CPU to make these decisions.
				boolean	foundQA = false, foundTechApproach = false, foundUX = false;
				if (childrenByParentID.containsKey(entry.getKey())) {
					for (JSONObject child : childrenByParentID.get(entry.getKey())) {
						String childName = child.getString("name");
						if (TECH_APPROACH_NAME.equalsIgnoreCase(childName)) {
							foundTechApproach = true;
						} else if (UX_DESIGN_NAME.equalsIgnoreCase(childName)) {
							foundUX = true;
						} else if (QA_TEST_MAP_NAME.equalsIgnoreCase(childName)) {
							foundQA = true;
						}
					}
				}

				//Create our missing subtasks
				String teamID = entry.getValue().getString("teamID");

				if (!foundTechApproach) {
					createChild(client, entry.getKey(), TECH_APPROACH_NAME, teamID);
				}

				if (!foundUX) {
					createChild(client, entry.getKey(), UX_DESIGN_NAME, teamID);
				}

				if (!foundQA) {
					createChild(client, entry.getKey(), QA_TEST_MAP_NAME, teamID);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createChild(StreamClient client, String parentID, String name, String teamID)
		throws StreamClientException {

		Map<String, Object> message = new HashMap<String, Object>();
		message.put("projectID", DEVELOPMENT_PRODUCT_BACKLOG_ID);
		message.put("parentID", parentID);
		message.put("name", name);
		message.put("teamID", teamID);

		client.post("TASK", message);
	}
}
