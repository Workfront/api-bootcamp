package com.workfront;

import com.workfront.api.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class RouteIssuesToTeam {

	//All of these will depend on the environment
	public static final String PRODUCT_AREA = "DE:Product Area";
	public static final String SOFTWARE_TICKET_CATEGORY_ID = "1586d59dcb97400ae0533365290ac8b8";
	public static final String WORKSTATION_REQUESTS_ID = "1586d59dc388400ae0533365290ac8b8";
	public static final String DEVELOPMENT_PRODUCT_BACKLOG_ID = "1586d59dc376400ae0533365290ac8b8";
	public static final String JEDI_COUNCIL_ID = "1586d59dc61c400ae0533365290ac8b8";
	public static final String GRYFFINDORE_ID = "1586d59dc652400ae0533365290ac8b8";

	/**
	 * The purpose of this class is to route incoming issues to the proper team and convert them into tasks
	 * on their backlog.  These issues are categorized by product ownership since the groups are separated
	 * across multiple products.  This task will require additional setup in workfront.
	 *
	 * So to recap, the requirements are:
	 *
	 * In workfront
	 * 1. Create a custom field named "Product Area" that has the following values and add it to a form:
	 * 		"User Management", "Calendars", "Document Management", "All The Rest"
	 * 2. Either alter an existing request queue or create a new request queue to take this new value as an option.
	 *
	 * In this class
	 * 3. Find all issues that come in through this queue and have a Product Area set and convert them to a task
	 *    on the "Development Product Backlog" project.
	 * 4. If the value is "User Management" or "Calendars" or "Document Management", ensure it shows up on the
	 *    Jedi Council team's backlog.
	 * 	  If the value is "All The Rest," ensure it shows up on the Gryffindore backlog.
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			StreamClient client = new StreamClient("https://leapco.attask-ondemand.com/attask/api/v4.0");
			client.login("marci@leapco.attask", "Pa55word");

			//find all issues with our category - we want to narrow the scope to the project issues land on
			//  as well as issues that have our form set and our field populated ignoring everything else.
			//  This should perform well because we have used 2 primary keys (IDs) even as our data grows.
			//TODO: fix this
			Map<String, Object> search = new HashMap<>();
			search.put("projectID", WORKSTATION_REQUESTS_ID);
			search.put("categoryID", SOFTWARE_TICKET_CATEGORY_ID);
			search.put("statusEquatesWith", "CPL");
			search.put("statusEquatesWith_Mod", "ne");

			JSONArray issues = client.search("OPTASK", search,
					new HashSet<>(Arrays.asList("parameterValues", "description")));

			for (int i = 0; i < issues.length(); i++) {
				JSONObject issue = issues.getJSONObject(i);

				//Ignore issues without the value set that we care about
				if (issue.getJSONObject("parameterValues") != null
						&& issue.getJSONObject("parameterValues").has(PRODUCT_AREA)) {

					//Figure out which team this belongs to
					String teamID = JEDI_COUNCIL_ID;
					if (!"All The Rest".equals(issue.getJSONObject("parameterValues").get(PRODUCT_AREA))) {
						teamID = GRYFFINDORE_ID;
					}

					//Since there is no endpoint to convert an issue to a task, we'll just create a task on the
					//project in question and copy over the most important data
					Map<String, Object> message = new HashMap<>();
					message.put("projectID", DEVELOPMENT_PRODUCT_BACKLOG_ID);
					message.put("name", issue.get("name"));
					message.put("description", issue.get("description"));
					message.put("teamID", teamID);

					JSONObject newTask = client.post("TASK", message);

					//Mark the issue complete and reference new task in update stream
					//TODO: debug this bad boy
//					message.clear();
//					message.put("notText", "This has been converted to task: " + newTask.get("ID"));
//					message.put("optaskID", issue.get("ID"));
//					client.post("NOTE", message);
//
//					message.clear();
//					message.put("status", "CPL");
//					client.put("OPTASK", issue.getString("ID"), message);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		//write your solution here
		//TODO: Joe
	}
}
