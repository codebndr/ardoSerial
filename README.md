This repository is part of the [codebender.cc](http://www.codebender.cc) maker and artist web platform.

ardoSerial is a java applet designed to act as an intermediate between the codebender arduino IDE and the Arduinos you plug to your computers.

The features provided by the Java Applet are:
* the functionality for automatically detecting the devices connected to the computer to make it as simple as possible for users 
* the functionality of a serial monitor to send and receive messages to and from the Arduino devices
* the functionality needed to upload compiled sketches to the Arduino devices over every Operating System (originally Linux, Mac and Windows)

To facilitate communication with the Arduino devices we use [jssc](http://code.google.com/p/java-simple-serial-connector/) library for communicating with serial ports.

Open Issues we are working on:
* Automatically detecting the COM ports on windows ;)
