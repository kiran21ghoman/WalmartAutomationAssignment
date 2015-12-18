# WalmartAutomationAssignment

OS used:  Windows 8,
Tool Used:  Selenium Webdriver,
Testing Framework:  TestNG,
IDE:  Eclipse IDE,
Language Used:  Java,
Browser used:  Chrome.

Before performing the test cases, cart for logged in user should be empty. If it is not please remove all items.

To Run: Go to "test-output" folder-> right-click on testng.xml file ->Run As- TestNG suite.

To maintain a flow between all methods, I am using “dependsOnMethods” annotation. It means every test case depends on previous case except the first test case. Before running 
the test case’s class, before class method will get executed and it will load the base URL.

Username and password can be changed manually(can get it dynamically) in "User.java" Class which placed under com/walmart/main directory.

Data to search is available in "DataProviderSource.java" class under com/walmart/main directory.

To add an item in cart, we first need to perform search functionality. And we can change the search value in "testng.xml" file placed in folder "test-output". 

To add an item in cart, I used Cart.java class placed under com/walmart/main directory of project.  We can save item id in our class and can use it to match the saved item during view cart test.

I created Util.java class to save utility functions for reusability purposes e.g. to find an element with wait time and
