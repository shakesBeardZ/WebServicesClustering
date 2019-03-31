# WebServicesClustering
Clustering web services for improved web service discovery using Java &amp; RStudio



Requirements:
Needs an installation of Netbeans IDE and RStudio.

Running the application:
	- Load the Java application files into NetBeans IDE.
	
	- Add the jar files
	
	- Run WSProject.java
	
	- Open RStudio
	
	-  Use the list generated as an input to x and run the commands given below:
	
		- library(flexclust)

		- x <- matrix( */ list generated from the java output*/ ) 

		- cl <- qtclust(x, radius=0.05) 

		- plot(x, col=predict(cl), xlab="", ylab=""); 

Built Using:
Java, RStudio.
