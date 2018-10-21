# Curriculum Processor

The software retrieves relevant information of a certain GitHub user and displays it in the terminal.  
The program takes the following inputs:

- OAuth token (from GITHUB\_OAUTH\_TOKEN environment variable);
- User name, as a command line argument.

The program outputs all public repositories of the user, and for each repository list collaborators ordered by the number of commits per collaborator in descending order.   
It uses GitHub REST API v3.

## Instructions
Built with: Java 9  
Dependency system: Gradle 4.6

Dependencies:  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; eclipse egit github  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;,google gson   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;,findbugs jsr305  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;,mockito  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;,junit 5  

To build and run the application:  
   1) clone the GitHub repository [mberionni/playground](https://github.com/mberionni/playground) in a local directory  
   2) set the environment variable GITHUB\_OAUTH\_TOKEN with the desired token, for example in the .profile:  
       export GITHUB_OAUTH_TOKEN="yyyyyy"
   3) build the project (and run the tests):  
    
	gradle build
	
   4) run the application passing the github $username

	gradle run -PappArgs="['$username']"
(with gradle 4.9 passing of arguments to the main method has been made easier.)

To only run unit tests:

	gradle clean test

Tests results can be found in $buildDir/reports/...

## Notes

The "number of commits per collaborator" is intended as commits in the repository.  
To be able to view collaborators of a repository, the user needs to have push access.

The tests CurriculumProcessorTest.testUserNameProcess and CurriculumProcessorTest.getUserRepos have been disabled as they need my token in order to run succesfully.