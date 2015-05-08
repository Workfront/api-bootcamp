package com.workfront;

public class OverdueTasks {

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
    public static void main(String[] args) {
	    // write your solution here
    }
}
