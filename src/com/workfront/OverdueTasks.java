package com.workfront;

import com.workfront.api.*;
import org.json.*;

import java.text.*;
import java.util.*;

public class OverdueTasks {

    static final String WORKFRONT_URL_V4 = "https://leapco.attask-ondemand.com/attask/api/v4.0";
    static final String API_KEY = "r44o0uldiz5ub9u4af6ieymkxfruuepi";
    //    Keeping credentials so that I know the credentials to the on demand account
    //    static final String USERNAME = "marci@leapco.attask";
    //    static final String PASSWORD = "Pa55word";

    // TODO: I am not sure which program we want to use ... but should probably name one of them a little bit better
    static final String DEV_PROGRAM_ID = "5550f476000176606f6de06eb1f09365";
    static final String PLANNED_COMPLETION_DATE = "plannedCompletionDate";
    static final String OVERDUE_MESSAGE = "Hello, just letting you know that this task is now overdue.";


    /**
     * The purpose of this class is to find tasks within a certain program that are overdue and
     * notify the proper [manager] via the update stream.  It is intended to run nightly so knowing that it
     * did not complete by the date specified on the task should be sufficient.  It would not be a good
     * idea to bother the [manager] every night, however.  Weekly should be sufficient.
     *
     * So to recap, the requirements are:
     * 1. Find tasks under the [FIND ME] program that are overdue
     * 2. If the task has not been updated with a notice to the [manager] in the past 7 days, post an update warning the manager
     *
     * @param args
     */
    public static void main(String[] args) throws StreamClientException {

        StreamClient client = null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSSZ");

        try {

//            Keeping here for example of how to login with username/password
//            client = new StreamClient(WORKFRONT_URL_V4);
//            client.login(USERNAME, PASSWORD);

            // Login using API Key
            client = new StreamClient(WORKFRONT_URL_V4, API_KEY);

            // Get a list of all projects in the specified program
            Map<String, Object> search = new HashMap<String, Object>();
            search.put("programID", DEV_PROGRAM_ID);
            JSONArray projectList = client.search("project", search);

            // for each of the projects in the program
            for(int i=0;i<projectList.length();i++) {

                JSONObject project = projectList.getJSONObject(i);

                // Get a list of all tasks for the specified project
                String[] taskFields = {"ID", "assignedToID", "plannedCompletionDate"};
                search.clear();
                search.put("projectID", project.get("ID").toString());
                // TODO: Add the overdue date query in the search instead of checking if older below
                JSONArray taskList = client.search("task", search, taskFields);

                // Iterate through each task and check to see if the task is overdue or not
                for (int j = 0; j < taskList.length(); j++) {

                    JSONObject task = taskList.getJSONObject(j);

                    // Get planned completion date of the task
                    // Example Date Format - '2015-05-11T09:00:00:000-0600'
                    Calendar taskPlanCompletionDate = Calendar.getInstance();
                    taskPlanCompletionDate.setTime(df.parse(task.get(PLANNED_COMPLETION_DATE).toString()));

                    // Get the time now
                    Calendar now = Calendar.getInstance();

                    // If the task is past due, then send a message to the assigned users manager
                    if (now.after(taskPlanCompletionDate)) {

                        // TODO: Only notify the manager every week. Since this will run on the daily timer we can just get the number
                        // TODO: of days that the task is overdue and if it is a mod of 7 then we can add the update to the stream

                        // Create overdue note to post on update stream
                        Map<String, Object> message = new HashMap<String, Object>();
                        message.put("noteText", OVERDUE_MESSAGE);
                        message.put("taskID", task.get("ID"));
                        message.put("topNoteObjCode", "TASK");
                        message.put("topObjID", task.get("ID"));

                        // Get the assigned to users manager so they can be tagged to the note
                        if (task.has("assignedToID")) {
                            String[] userFields = {"ID", "managerID"};
                            JSONObject user = client.get("USER", task.get("assignedToID").toString(), userFields);

                            // Tag user if they have a defined manager
                            if (user.has("managerID")) {
                                List<JSONObject> tags = new ArrayList<JSONObject>();
                                JSONObject noteTag = new JSONObject();
                                noteTag.put("objID", user.get("managerID").toString());
                                noteTag.put("objObjCode", "USER");
                                tags.add(noteTag);
                                message.put("tags", new JSONArray(tags));
                            }
                        }

                        // Create the new note
                        JSONObject newUpdate = client.post("NOTE", new HashMap<String, Object>(), message, null);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                // TODO: Not sure if we have to logout or not ... but this seems like the righ place if we have to
                client.logout();
            }
        }
    }
}
