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

public class DocumentMover {

	/**
	 * The purpose of this class is to automate the entry of documents from another system into workfront.  When
	 * a file is dropped into a certain directory, we will upload the document to the appropriate object and
	 * update the object with a message saying that we have moved the document into the object automatically.  We
	 * will use a basic folder structure to get documents to the correct object. Once a document is uploaded, it will
	 * be moved to another directory.
	 *
	 * So to recap, the requirements are:
	 *
	 * 1. Setup a root "input" directory that contains 3 children: project, task and issue
	 * 2. When a directory is found under any of these children that looks like an ID (32 characters [0-9a-f])
	 *    attempt to upload any files in the directory to the corresponding object
	 * 3. If the object cannot be found, move the document to a corresponding folder structure under a "failures" directory
	 * 4. If a file is uploaded correctly to the object, move it to a "complete" directory with the same folder structure
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		//write your solution here
	}
}
