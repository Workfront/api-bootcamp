# api-bootcamp
[![Apache v2 License][license-image]][license-url]

A base project for use in Developer Bootcamps to speed setup of a java environment 
and provide simple documentation for challenges meant to acquaint a developer with
the Workfront REST API.

##Installation

Simply clone this repository and setup an IDE for a basic Java application with source
files in the cloned directory.

##File Structure

The files located in this project are organized as follows:  
* libs
  - org/json: Contains code from [json.org](http://www.json.org/java/index.html) for parsing http responses into JsonObjects
* src/com/workfront
  - api: Contains the StreamClient example implementation from [developers.attask.com](https://developers.attask.com/api-docs/code-samples/)
  - StreamClientSample.java: The basic usage sample from [developers.attask.com](https://developers.attask.com/api-docs/code-samples/)
  - ScratchPad.java: An empty class intended to be used freely during the course for implementing examples
  - Task classes: A set of classes describing a task that should be implemented using the StreamClient against a Workfront instance.

## License

Copyright (c) 2015 Workfront

Licensed under the Apache License, Version 2.0.
See the top-level file `LICENSE` and
(http://www.apache.org/licenses/LICENSE-2.0).


[license-image]: http://img.shields.io/badge/license-APv2-blue.svg?style=flat
[license-url]: LICENSE
