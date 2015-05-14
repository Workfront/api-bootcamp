package com.workfront;

import com.workfront.api.*;
import org.json.*;

import java.text.*;
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

public class OverdueTasks {

    static final String WORKFRONT_URL_V4 = "https://leapco.attask-ondemand.com/attask/api/v4.0";
    static final String API_KEY = "r44o0uldiz5ub9u4af6ieymkxfruuepi";

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

            // Example usine 'username/password'
            // client = new StreamClient(WORKFRONT_URL_V4);
            // client.login(USERNAME, PASSWORD);

            // Login using API Key
            client = new StreamClient(WORKFRONT_URL_V4, API_KEY);

            // Get a list of all projects in the specified program
            Map<String, Object> search = new HashMap<String, Object>();
            search.put("programID", DEV_PROGRAM_ID);
            JSONArray projectList = client.search("project", search);

            // for each of the projects in the program
            for (int i = 0; i < projectList.length(); i++) {

                JSONObject project = projectList.getJSONObject(i);

                // Get a list of all tasks for the specified project
                // Filter search for tasks where the current date is past the planned completion date (overdue)
                String[] taskFields = {"ID", "assignedToID", "plannedCompletionDate"};
                search.clear();
                search.put("projectID", project.get("ID").toString());
                search.put("plannedCompletionDate", "$$TODAY");
                search.put("plannedCompletionDate_Mod", "lte");
                JSONArray taskList = client.search("task", search, taskFields);

                // Iterate through each task and add update as needed to manager.
                for (int j = 0; j < taskList.length(); j++) {

                    JSONObject task = taskList.getJSONObject(j);

                    // Find the number of days between the planned completion date and NOW
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(df.parse(task.get("plannedCompletionDate").toString()));
                    int days = daysBetween(calendar.getTime(), Calendar.getInstance().getTime());

                    // Prevent updates from posting to a update feed more that once a week. To accomplish this I will
                    // check to see when a tasks plannedCompletion date was and then only update the thread every 7 days
                    // from that date.
                    if (days % 7 == 0) {

                        // Create overdue note to post on update stream
                        Map<String, Object> message = new HashMap<String, Object>();
                        message.put("noteText", OVERDUE_MESSAGE);
                        message.put("taskID", task.get("ID"));
                        message.put("topNoteObjCode", "TASK");
                        message.put("topObjID", task.get("ID"));

                        // Get the assigned to users manager so they can be tagged to the note
                        if (task.has("assignedToID") && !task.get("assignedToID").equals(null)) {
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

                        // Create the new note (even if not tagging manager)
                        JSONObject newUpdate = client.post("NOTE", new HashMap<String, Object>(), message, null);
                        System.out.println(newUpdate.toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.logout();
            }
        }
    }

    public static int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }
}
