# What is the MDCF Architect?
This is a plugin for [Osate 2](https://wiki.sei.cmu.edu/aadl/index.php/Osate_2) which translates from a selected subset of AADL into code runnable on the [MDCF](http://mdcf.santos.cis.ksu.edu/).  It's developed by Sam Procter at K-State's [SAnToS](http://santoslab.org/) Lab. More complete documentation is available at the [project website](http://santoslab.org/pub/mdcf-architect/).

# Installing
If you just want to use the translator, you just need to install it into OSATE2 using the [update site](http://santoslab.org/pub/mdcf-architect/updatesite/)

# Building with Maven
After cloning, the translator can be built by the command "mvn install"  Note that the build process requires [Maven](http://maven.apache.org/) (version 3.0+) and [Java](http://www.java.com/en/) (version 1.7+). If you also wish to build the documentation, you'll need [Pygments](http://pygments.org/) (version 1.2+).

# Building with Eclipse
In order to use eclipse you'll need to:

1. Install Java 7+.
2. Follow the steps in the "Getting Eclipse and XText Environment" section of [this page](https://wiki.sei.cmu.edu/aadl/index.php/Getting_Osate_2_sources#Getting_the_Eclipse_and_XText_environment).
	* Note: Don't worry about any of the other sections of this page if you're using Eclipse -- getting the sources of everything is handled automatically via the project set file. 
3. Import the project set file (AADL Translator.psf) by going to File, Import... then under "Team" select "Team Project Project"

# License
This project, and its source, are publicly available under the [EPL](http://www.eclipse.org/legal/epl-v10.html), unless otherwise stated in the file header.

# Acknowledgements
This project uses icons from [famfamfam](http://famfamfam.com/lab/icons/silk/)'s silk icon set.
