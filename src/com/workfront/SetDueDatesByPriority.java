package com.workfront;

public class SetDueDatesByPriority {

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
	public static void main(String[] args) {
		//write your solution here
	}
}
