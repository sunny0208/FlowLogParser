## FlowLogParser Application

### What is FlowLogParser?

    FlowLogParser is a simple Spring Boot application designed to help you parse network flow logs and determine tags based on destination ports and protocols.
    It provides a straightforward RESTful API that you can use to upload flow logs and find out which tags are assigned to specific port and protocol combinations.


### Project Layout

Here's a quick overview of how the project is organized:

src/main/java: This is where the main application code lives.

    com.flowlogparser.controller.FlowLogController: Handles incoming API requests.

    com.flowlogparser.service.FlowLogParserService: Contains the core logic for parsing flow logs and retrieving tags.

    com.flowlogparser.FlowLogParserApplication: The main class that kicks off the Spring Boot application.

src/main/resources: This is where you store resources like configuration files.

    lookup.csv: The lookup table used by the application to map port/protocol combinations to tags.

src/test/java: Contains the test code.

    FlowLogControllerTest: Tests for the controller.
    FlowLogParserServiceTest: Tests for the service layer.
src/test/resources: Stores test-related resources.

lookuptest.csv: A test version of the lookup table.
flowlogtest.txt: A sample flow log file for testing.


### What You Need to Get Started

 To run the application, make sure you have the following:

     Java 21  
    
     Maven
    
     Spring Boot




### Setting Up and Running the Application

Clone the Repository: First, get a copy of the project on your local machine.

    Run the commands in Bash
    git clone <your-repository-url>
    cd FlowLogParser


    Build the Application: Use Maven to compile and package the project.

    Run the command in Bash
    mvn clean install

    Run the Application: Start the Spring Boot application.

    Run the command in Bash
    mvn spring-boot:run

Once it’s up and running, the app will be available at http://localhost:8080.

## How to Use It

GET Method: Find a Tag for a Specific Port and Protocol

Want to find the tag associated with a specific port and protocol? Just send a GET request to this endpoint:

### Endpoint:

    GET http://localhost:8080/api/flowlog/tag

    Parameters:
    dstport: The destination port (e.g., 25).
    protocol: The protocol (e.g., udp).

Example Request:

    http://localhost:8080/api/flowlog/tag?dstport=25&protocol=udp

    The output for the GET method is :

    The application will return the tag associated with the port and protocol you specified.


POST Method: Upload and Parse a Flow Log
Need to parse a flow log? You can do that with a POST request.

#### Endpoint:

    POST http://localhost:8080/api/flowlog/parse

    Form Data:

    Key: file
    Type: File
    Value: The flow log file you want to upload (e.g., flowlog.txt).

#### Example Steps:

    Choose the POST method in your REST client.
    Use this URL: http://localhost:8080/api/flowlog/parse.
    In the form data:
    Set Key to file.
    Set Type to File.
    Upload your flow log file (flowlog.txt).

The output for the POST method is :

    If everything goes smoothly, you’ll see a message saying, “Flow log parsed successfully. Results saved to output.txt.”
    The results will be saved in the output.txt file.

#### Running Tests

    The application comes with a set of unit tests to make sure everything works as expected.

Run the Tests: Use Maven to run all the tests.

    bash
    mvn test

Test Files: The files lookuptest.csv and flowlogtest.txt in the src/test/resources directory are used during testing.

### Assumptions

#### File Locations: 

    I have made the assumption that the lookup.csv file, which contains the tagging rules, will always be located in the src/main/resources directory.
    This ensures that the file is bundled with the application and easily accessible when the program runs. Similarly, 
    the output of the parsing process will always be written to a file called output.txt in the project's root directory.

##### Input Format Consistency: 
    
    I have assumed that the input files will always follow the expected format. For lookup.csv, 
    this means three columns: dstport, protocol, and tag. For the flow log files, I have expected two columns: dstport and protocol.
    If the files deviate from these formats, the program is designed to catch and report those errors to maintain the integrity of the data processing.

#### Handling Unmatched Entries:
    If a dstport and protocol combination in the flow log doesn’t have a corresponding tag in lookup.csv,
    the program will label it as "Untagged" by default. This assumption ensures that the program handles all entries gracefully, 
    even if they’re not explicitly covered in the lookup table.

#### Testing Environment:

    The test files (lookuptest.csv and flowlogtest.txt) are stored in the src/test/resources directory. 
    I have assumed that these files accurately represent the types of data the application will encounter in real-world use. 
    The tests are designed to cover typical scenarios ensuring the program behaves correctly under a variety of conditions.

#### Error Handling: 
    The application is set up to handle common issues like missing or malformed data.
    For instance, if the lookup.csv file isn’t found or if a line in the flow log is incorrectly formatted, 
    the program will throw a clear and helpful error message. This approach ensures that users are aware of issues and can address them promptly.

### Analysis

#### Data Integrity:
    By enforcing strict formats for input files, I ensure that the flow log parsing process is accurate.
    The application is designed to reject malformed data, which helps maintain the quality and reliability of the output.

#### Hardcoding Choices:

    I chose to hardcode the file names (lookup.csv and output.txt) for simplicity. While this makes the application easier to use,
    It does limit flexibility—if you need to use different files, you’ll have to modify the code. We felt this trade-off was worth it for the sake of keeping things straightforward.


#### Scalability: 

    The application is built to handle a reasonable amount of data, assuming that the size of lookup.csv and the flow log files will remain manageable in memory. 
    If the data grows significantly, I might need to consider more scalable solutions, like using a database for the lookup table or processing flow logs in a more efficient way.


#### Testing:
    The tests I included are designed to ensure the program can handle the methods that I have used in the Service and the controller  
    from typical use cases to more unusual situations. However, these tests assume that the environment 
    where they’re run is similar to the environment where the program will be used, which should help ensure consistent behavior across different setups.

#### Important Notes:

    The lookup file (lookup.csv) and output file (output.txt) are hardcoded. 
    Make sure the lookup.csv file is present in the src/main/resources directory before you run the application.
    For testing, the lookuptest.csv and flowlogtest.txt files are stored in the src/test/resources directory.
