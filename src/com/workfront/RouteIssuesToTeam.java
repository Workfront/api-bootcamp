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

public class RouteIssuesToTeam {

	/**
	 * The purpose of this class is to route incoming issues to the proper team and convert them into tasks
	 * on their backlog.  We will mark the incoming issues as resolved by our new tasks on the backlog and
	 * leave an update on the update stream for the issue that it will be completed by our new task.
	 * These issues are categorized by product ownership since the groups are separated
	 * across multiple products.  This task will require additional setup in workfront.
	 *
	 * So to recap, the requirements are:
	 *
	 * In workfront
	 * 1. Create a custom field named "Product Area" that has the following values and add it to a form:
	 *              "User Management", "Calendars", "Document Management", "All The Rest"
	 * 2. Either alter an existing request queue or create a new request queue to take this new value as an option.
	 *
	 * In this class
	 * 3. Find all issues that come in through this queue and have a Product Area set and are not resolved by another
	 *    object and convert them to a task on the "Development Product Backlog" project.
	 * 4. If the value is "User Management" or "Calendars" or "Document Management", ensure it shows up on the
	 *    Jedi Council team's backlog.
	 *        If the value is "All The Rest," ensure it shows up on the Gryffindore backlog.
	 * 5. Update the issue with a message that the work will be done in a task and reference the task ID.
	 * 6. Set the issue to be resolved by the new Task.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		//write your solution here
	}
}
