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
