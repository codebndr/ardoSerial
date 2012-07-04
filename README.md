This repository is part of the [codebender.cc](http://www.codebender.cc) maker and artist web platform.

## And what's that?

codebender comes to fill the need for reliable and easy to use tools for makers. A need that from our own experience could not be totally fulfilled by any of the existing solutions. Things like installing libraries, updating the software or installing the IDE can be quite a painful process.

In addition to the above, the limited features provided (e.g. insufficient highlighting, indentation and autocompletion) got us starting building codebender, a completely web-based IDE, that requires no installation and offers a great code editor. It also stores your sketches on the cloud.

That way, you can still access your sketches safely even if your laptop is stolen or your hard drive fails! codebender also takes care of compilation, giving you extremely descriptive warnings on terrible code. On top of that, when you are done, you can upload your code to your Arduino straight from the browser without installing anything.

Currently codebender.cc is running its beta and we are trying to fix issues that may (will) come up so that we can launch and offer our services to everyone!
If you like what we do you can also support our campaign on [indiegogo](http://www.indiegogo.com/codebender) to also get early access to codebender! 

## What does ardoSerial do?

ardoSerial is a java applet designed to act as an intermediate between the codebender.cc Web Based Arduino IDE and the Arduino Boards you plug to your computer.

The features provided by the Java Applet are:
* the functionality for automatically detecting the devices connected to the computer to make it as simple as possible for users 
* the functionality of a serial monitor to send and receive messages to and from the Arduino devices
* the functionality needed to upload compiled sketches to the Arduino devices over every Operating System (originally Linux, Mac and Windows)

## Interested in more technical stuff?

To facilitate communication with the Arduino devices we use [jssc](http://code.google.com/p/java-simple-serial-connector/) library for communicating with serial ports.


## And what's the status?

Open Issues we are working on:
* Automatically detecting the COM ports on windows ;)
