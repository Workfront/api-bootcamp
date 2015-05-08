package com.workfront;

public class TaskDependencyHelper {

	/**
	 * The purpose of this class is to ensure that certain types of tasks that are entered follow the proper
	 * process to facilitate consistent reporting.  When a task is entered under a certain project and is
	 * marked in custom data as an "Agile Story," we want to ensure that the story has a certain set of subtasks.
	 *
	 * So to recap, the requirements are:
	 *
	 * In Workfront:
	 * 1. Add a custom field called "Agile Story" that is a checkbox to a custom form of your choosing that will
	 *    be applied to agile team stories.
	 *
	 * In this class:
	 * 2. When a task on the Development Product Backlog has the custom field "Agile Story" selected, ensure the following
	 *    subtasks exist:
	 *    	"Technical Approach": 4 hours default duration and assigned to the team
	 *      "UX Design": 2 days default duration and assigned to team
	 *      "QA Test Map": 2 days default duration and assigned to team
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		//write your solution here
	}
}
