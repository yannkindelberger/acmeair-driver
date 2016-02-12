# JMeter Workload Setup Instructions


These instructions will assist with setting up the Apache JMeter workload for the Acme Air sample application. 
## Download Apache JMeter 

* Download Link:  [http://jmeter.apache.org](http://jmeter.apache.org)
* Click on "Download Releases" and download the binary release.
* Uncompress the file to a directory of your choice. 
* For the rest of these instructions we will assume this to be the JMETER_DIR 

```text
tar -xzf apache-jmeter-2.13.tgz
```
 

##  Build and Package the Acme Air Driver Code
Go in to the acmeair-driver directory and use the gradle wrapper (included in the repository) to compile and package the jar file. 

```text
cd acmeair-driver 
./gradlew build
```

for Windows:
```text
gradlew.bat build
```

copy the jar to  jmeter's ext directory
```text
cp acmeair-jmeter/build/libs/acmeair-jmeter-*-SNAPSHOT.jar  %JMETER_DIR%/lib/ext/
```

Also needed is the json-simple library to jmeter's ext directory.  
* Download Link: http://code.google.com/p/json-simple/
* Download the json-simple-1.1.1.jar and copy the jar to %JMETER_DIR%/lib/ext/


## Configure JMeter  

```text
cd acmeair-jmeter/scripts/
```
Edit the host.csv file to be the appropriate name or address for the WebSphere Liberty server.
The workload can be started using either the command line or JMeter's graphical interface.  

To run the workload using the command line, some additional steps are needed to configure what will be collected in the output. Edit the %JMETER_DIR%/bin/jmeter.properties file setting the summariser properties to get basic output reporting.

```text
#---------------------------------------------------------------------------
# Summariser - Generate Summary Results - configuration (mainly applies to non-GUI mode)
#---------------------------------------------------------------------------
#
log_level.jmeter.reporters.Summariser=INFO
summariser.name=summary
summariser.interval=30
summariser.log=true
```
Setting these properties will allow summary statistics to be printed on the console screen, as well as in the specified log file. 

In addition to the data collected to the log file. JMeter can also collect the sampler result data of individual requests. What data is collected in this JTL file is fully configurable within the jmeter.properties file, and a basic set of data collection is enabled in JMeter by default. Adding too many metrics to be collected can add additional overhead to the workload process.  

The default format of the JTL file output is csv. if desired, this can be switched to an XML format by changing the output_format property  
```text
jmeter.save.saveservice.output_format=xml
```

In addition to the standard metrics that are available within JMeter, the AcmeAir.jmx test plan also has some additional metrics available.  These custom metrics can be printed in the JTL output file by adding the sample_variables property to the %JMETER_DIR%/bin/user.properties file. 
```text
sample_variables=FLIGHTTOCOUNT,FLIGHTRETCOUNT,ONEWAY
```

## Run the Workload using the JMeter GUI 

Running the jmeter command in the bin directory will bring up the JMeter GUI window (Figure 1). You can then open up the AcmeAir.jmx script file in to the JMeter GUI. The parameters of the run are configurable from the JMeter GUI window.  

**Figure 1.** 
![JMeter GUI, main screen](Documentation/images/AcmeAir-jmx_main.png)


The following is a list of some of the run configurations that can be adjusted:
* **Number of Threads**: This is the number of virtual users to issue request on the Acme Air sample application.  For a brand new environment, it is recommended to start with a single user, and make sure there are no errors with the requests issued. Once satisfied that all requests are successful, the number of threads can be increased to the desired load. 
* **Loop Count:**: The default script has the **Forever** and **Scheduler** check boxes selected, so the workload will run for a given time interval.  If preferred, the workload can be ran for a set number of loop instead by setting a value for this parameter.  
* **Duration (seconds)**: The default for the AcmeAir.jmx script is to run for 10 minutes. 

### Enabling JMeter listeners 
There are four listeners that are already defined in the Acme Air test plan. These listeners are disabled by default as they are not needed when running JMeter in a command line mode, and add extra overhead.  When running the workload from the JMeter GUI, you will want to enable one or more of these listeners in order to see the results of the workload. Figure 2 shows the listeners included in the test plan. 

**Figure 2.** 
![AcmeAir test plan with listeners](Documentation/images/AcmeAir-jmx_listeners.png)

The most common listeners that you will most likely use are the *Summary Report* and *Aggregate Report*.
To enable a listener, right click on the desired element, and select **Enable**.

**Figure 3.** 
![listners enabled](Documentation/images/AcmeAir-jmx_listeners_enabled.png)

The *View Results Tree* listener is especially useful when the workload is reporting errors during the test run.  It will show each request element that was executed with an icon next to it to indicate if it was successful or not.  you can then click on each of the request elements and see the details for the given request.  You can also view the raw request that was sent along with the response data returned from the server. 

After setting the desired parameters for the test plan, and saving your changes, you can start the workload by clicking on the green triangle on the tool bar (Ctrl-R ).  While the test is running you can monitor the test's progress, and view the final results by clicking on one of the listeners that you have enabled. 

The listeners provide an option for writing the results to an output file.  In between doing multiple workload runs it is recommended that you clear the result data *Run* -> *Clear All* (Ctrl-E).  

## Run the Workload from the command line
Alternatively, the workload can also be ran from the command line if desired. 

The most common syntax for running the workload from the command line would be:
```text
%JMETER_DIR%/bin/jmeter -DusePureIDs=true -n -t AcmeAir-v5.jmx -j AcmeAir1.log -l AcmeAir1.jtl (JHOST=myServer.mycompany.com) (-JPORT=9080) (-JTHREAD=25) (-JUSER=999) (-JDURATION=300) (-JRAMP=120) (JURL=acme-app)
```
* **-DusePureIDs=true** This is a Java systems property & it is needed (current Acmeair Java & Node.js versions require this)
* **-n** This specifies JMeter is to run in non-gui mode 
* **-t** The name of the JMeter test plan. 
* **-j** The name of the output log file. 
* **-l** The name of the output file to collect JMeter sampler results. 
* **-JHOST** Optional : Default is to use hostname defined in hosts.csv. Alternatively, hostname can be passed with this option.
* **-JPORT** Optional : Default is 80. The port number of the application.  Java usually uses 9080 & Node with 80.
* **-JTHREAD** Optional : Default is 10. The jMeter thread number.  Adjust the number depending on your app & environment.
* **-JUSER** Optional : Default is 199.  The number of Acmeair users which can be changed when database is populated.  Use **Total number of users - 1** (e.g. for total number of users is 200, this number should be set to 199 meaning users 0 to 199 = 200 will be used to test)
* **-JDURATION** Optional : Default is 600 (10 minutes).  The test duration.
* **-JRAMP** Optional : Default is 30 (30 seconds).  The thread number will gradually increase within this time frame.
* **-JURL** Optional : Default is "/".  Usually, Node.js uses default "/".  Java uses "/acmeair-webapp", however, it is configurable within server.xml.  Verify with server.xml for the exact path.  
