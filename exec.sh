#!/bin/sh

JAVA_HOME=~/bin/java8/ mvn clean package
~/bin/java8/bin/java -cp target/csa-1.0-SNAPSHOT-jar-with-dependencies.jar jgreg.internship.nii.WF.Pipeline
