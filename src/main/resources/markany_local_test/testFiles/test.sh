#!/bin/bash
sudo javac -classpath /home/dooray/ServiceLinker_File/02_Module/02_ServiceLinker/scsl.jar TestEnc.java
sudo java -classpath /home/dooray/ServiceLinker_File/02_Module/02_ServiceLinker/scsl.jar: TestEnc
sudo javac -classpath /home/dooray/ServiceLinker_File/02_Module/02_ServiceLinker/scsl.jar TestDec.java
sudo java -classpath /home/dooray/ServiceLinker_File/02_Module/02_ServiceLinker/scsl.jar: TestDec
