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
