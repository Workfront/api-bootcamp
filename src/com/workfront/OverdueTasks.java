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
    static final String API_KEY = "p4wromjqf77bmotug7t8l8zai9srk7jm";

    static final String DEV_PROGRAM_ID = "5550f476000176606f6de06eb1f09365";
    static final String OVERDUE_MESSAGE = "Hello, just letting you know that this task is now overdue.";

    /**
     * The purpose of this class is to find tasks within a certain program that are overdue and
     * notify the proper manager via the update stream.  It is intended to run nightly so knowing that it
     * did not complete by the date specified on the task should be sufficient.  It would not be a good
     * idea to bother the manager every night, however.  Weekly should be sufficient.
     *
     * So to recap, the requirements are:
     * 1. Find all tasks under a program that are overdue
     * 2. If the task has not been updated with a notice to the manager in the past 7 days, post an update warning the manager
     *
     * @param args
     */
    public static void main(String[] args) throws StreamClientException {

        StreamClient client = null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSSZ");

        try {

            // Login using API Key
            client = new StreamClient(WORKFRONT_URL_V4, API_KEY);

            // Get a list of all tasks for the specified program
            // Filter search for tasks where the current date is past the planned completion date or commit date (overdue)
            String[] taskFields = {"assignedTo:managerID"};
            Map<String, Object> search = new HashMap<String, Object>();
            search.clear();
            search.put("OR:A:projectProgramID", DEV_PROGRAM_ID);
            search.put("OR:A:plannedCompletionDate", "$$TODAY");
            search.put("OR:A:plannedCompletionDate_Mod", "lte");
            search.put("OR:B:projectProgramID", DEV_PROGRAM_ID);
            search.put("OR:B:commitDate", "$$TODAY");
            search.put("OR:B:commitDate_Mod", "lte");
            JSONArray taskList = client.search("task", search, taskFields);

            // Iterate through each task and add update as needed to manager.
            for (int j = 0; j < taskList.length(); j++) {

                JSONObject task = taskList.getJSONObject(j);

                //Check to see if we have entered a note already for this task in the past week
                search.clear();
                search.put("taskID", task.get("ID"));
                search.put("ownerID", "$$USER.ID");
                search.put("entryDate", "$$TODAY-7d");
                search.put("entryDate_Mod", "gte");

                JSONArray warnings = client.search("NOTE", search);

                //warn if we haven't recently
                if (warnings.length() == 0) {

                    // Create overdue note to post on update stream
                    Map<String, Object> message = new HashMap<String, Object>();
                    message.put("noteText", OVERDUE_MESSAGE);
                    message.put("taskID", task.get("ID"));
                    message.put("topNoteObjCode", "TASK");
                    message.put("topObjID", task.get("ID"));

                    // Get the assigned to users manager so they can be tagged to the note
                    if (task.has("assignedTo") && !task.get("assignedTo").equals(null)) {
                        JSONObject assignedTo = (JSONObject) task.get("assignedTo");

                        // Tag user if they have a defined manager
                        if (assignedTo.has("managerID") && !assignedTo.get("managerID").equals(null)) {
                            JSONObject noteTag = new JSONObject();
                            noteTag.put("objID", assignedTo.getString("managerID"));
                            noteTag.put("objObjCode", "USER");
                            message.put("tags", new JSONArray(Arrays.asList(noteTag)));
                        }
                    }

                    // Create the new note (even if not tagging manager)
                    JSONObject newUpdate = client.post("NOTE", new HashMap<String, Object>(), message, null);
                    System.out.println(newUpdate.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
