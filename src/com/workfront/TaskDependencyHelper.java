package com.workfront;

public class TaskDependencyHelper {

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
	 *      "Technical Approach", "UX Design", "QA Test Map"
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		//write your solution here
	}
}
