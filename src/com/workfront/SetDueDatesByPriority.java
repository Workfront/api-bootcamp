package com.workfront;

import com.workfront.api.*;
import org.json.*;

import java.text.*;
import java.util.*;

public class SetDueDatesByPriority {


	static final String WORKFRONT_URL_V4 = "https://leapco.attask-ondemand.com/attask/api/v4.0";
	static final String API_KEY = "r44o0uldiz5ub9u4af6ieymkxfruuepi";

	static final String OVERDUE_ISSUE_PROJECT_ID = "55523b250002174cb2bc37ffba08798a";
	static final String SET_PRIORITY_MESSAGE = "Please set a priority on this issue so that the date can be set properly. Thanks.";

	static final int NONE = 0;
	static final int LOW = 1;
	static final int NORMAL = 2;
	static final int HIGH = 3;
	static final int URGENT = 4;

	/**
	 * The purpose of this class is to detect issues in a project that are incomplete and ensure that it has the
	 * proper due date.  The due date is determined by how long it has been on the queue and what priority it was assigned.
	 * If no priority has been assigned, add an update to the issue asking for a priority to be set.  Ensure that this
	 * update is done no more than once per week.
	 *
	 * So to recap, the requirements are:
	 *
	 * 1. Pick a project on which to update due dates and find all of the issues that are not complete.
	 * 2. Update the due date by priority as follows:
	 *		"None": Add an update asking for priority to be set
	 *		"Low": 2 weeks after the creation date
	 *		"Normal": 1 week after the creation date
	 *		"High":	3 days after the creation date
	 *		"Urgent": 1 day after the creation date
	 *
	 * @param args
	 */
	public static void main(String[] args) throws StreamClientException {

		StreamClient client = null;
		Map<String, Object> message = new HashMap<String, Object>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSSZ");

		try {
			// Login using API Key
			client = new StreamClient(WORKFRONT_URL_V4, API_KEY);

			// Get the project which is being used for this task
			JSONObject project = client.get("PROJ", OVERDUE_ISSUE_PROJECT_ID);

			// Search for all issues found in this project
			String[] issueFields = {"ID", "priority", "entryDate", "dueDate"};
			Map<String, Object> search = new HashMap<String, Object>();
			search.put("projectID", project.get("ID").toString());
			// TODO: Add into the search to only get issues that are NOT marked as complete
			JSONArray issueList = client.search("OPTASK", search, issueFields);

			for (int i=0;i<issueList.length();i++) {

				JSONObject issue = issueList.getJSONObject(i);

				if (issue.has("priority")) {

					int priority = Integer.parseInt(issue.get("priority").toString());
					Calendar entryDate = Calendar.getInstance();
					Calendar dueDate = entryDate;
					entryDate.setTime(df.parse(issue.get("entryDate").toString()));

					// TODO: Need to only perform update on a task no more than once a week

					switch(priority) {
						case NONE:
							// "None": Add an update asking for priority to be set
							message.clear();
							message.put("noteText", SET_PRIORITY_MESSAGE);
							message.put("opTaskID", issue.get("ID"));
							message.put("topNoteObjCode", "OPTASK");
							message.put("topObjID", issue.get("ID"));
							JSONObject newUpdate = client.post("NOTE", new HashMap<String, Object>(), message, null);
							System.out.println("Update:  " + newUpdate.toString());
							break;

						case LOW:
							// "Low": 2 weeks after the creation date
							dueDate = entryDate;
							dueDate.add(Calendar.DATE, 14);
							message.clear();
							message.put("plannedCompletionDate", df.format(dueDate.getTime()));
							client.put("OPTASK", issue.get("ID").toString(), message);
							break;

						case NORMAL:
							// "Normal": 1 week after the creation date
							dueDate = entryDate;
							dueDate.add(Calendar.DATE, 7);
							message.clear();
							message.put("plannedCompletionDate", df.format(dueDate.getTime()));
							client.put("OPTASK", issue.get("ID").toString(), message);
							break;

						case HIGH:
							// "High":	3 days after the creation date
							dueDate = entryDate;
							dueDate.add(Calendar.DATE, 3);
							message.clear();
							message.put("plannedCompletionDate", df.format(dueDate.getTime()));
							client.put("OPTASK", issue.get("ID").toString(), message);
							break;

						case URGENT:
							// "Urgent": 1 day after the creation date
							dueDate = entryDate;
							dueDate.add(Calendar.DATE, 1);
							message.clear();
							message.put("plannedCompletionDate", df.format(dueDate.getTime()));
							client.put("OPTASK", issue.get("ID").toString(), message);
							break;
						default:
							break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.logout();
		}
	}
}
