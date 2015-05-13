package com.workfront;

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
