[![Android Weekly](http://img.shields.io/badge/Android%20Weekly-%23139-lightgrey.svg?style=flat)](http://androidweekly.net/issues/issue-139)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-RushOrm-blue.svg?style=flat)](http://android-arsenal.com/details/1/1499)
[![Join the chat at https://gitter.im/Stuart-campbell/RushOrm](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/Stuart-campbell/RushOrm?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


# RushOrm 
### Object-relational mapping for Android

RushOrm replaces the need for SQL by mapping java classes to SQL tables.
<hr>
###What is the aim?
The aim is to abstract away all SQLite interaction under a very simple interface to make object storage incredibly quick to implement.
<hr>
###What projects is it right for?
<ul>
    <li>Projects with complex data structures</li>
    <li>Projects that want to implement database storage in the minimum amount of time</li>
    <li>Projects that talk to a rest api</li>
</ul>
<hr>
###Why was RushOrm written?
<ul>
    <li>Complex relationships - RushObjects support Lists of other RushObjects</li>
    <li>SQL free migration</li>
    <li>Easily extendable</li>
    <li>No dependencies</li>
    <li>Support asynchronous call</li>
    <li>Be fast through compound inserts and selects</li>
    <li>Support importing and exporting data to and from JSON</li>
    <li>Unique ids to support merging databases</li>
    <li>Supports conflict resolution when importing data</li>
</ul>
While there are a number of other ORMs, the areas many seem to fall short is the support of 'one to many' relationships, migration and extensions. While claiming all the same basic feature of most other ORMs RushOrm supports 'List' properties without having to add the parent object to it's children. It also handles migrating the class structure without any SQL scripts being required by the developer. Finally it is designed with the understanding that not every situation can be anticipated so instead it can be easily customized.
<hr>

For getting started see http://www.rushorm.com/

<hr>
### Licence Apache License, Version 2.0
Copyright (C) 2015 Stuart Campbell
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

