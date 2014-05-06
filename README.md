# What is the AADL Translator?
This is a plugin for [Osate 2](https://wiki.sei.cmu.edu/aadl/index.php/Osate_2) which translates from a selected subset of AADL into code runnable on the [MDCF](http://mdcf.santos.cis.ksu.edu/).  It's developed by Sam Procter at K-State's [SAnToS](http://santoslab.org/) Lab.

# Installing
If you just want to use the translator, you just need to install it into osate 2 using the [update site](http://people.cis.ksu.edu/~samprocter/aadl-translator/)

# Building
This project is also publicly available under the [EPL](http://www.eclipse.org/legal/epl-v10.html).

## Building with Maven
After cloning, the translator can be built by the command "mvn install"  Note that the build process requires [Maven](http://maven.apache.org/) (version 3.0+) and [Java](http://www.java.com/en/) (version 1.7+)

## Building with Eclipse
In order to use eclipse you'll need to first download and prepare eclipse (steps adapted from the [OSATE 2 wiki](https://wiki.sei.cmu.edu/aadl/index.php/Getting_Osate_2_sources)):

1. Start by downloading the [Eclipse Modeling Tools](http://www.eclipse.org/downloads/packages/eclipse-modeling-tools/keplersr2) (version "Kepler" (4.3)).
2. Install [XText](http://download.eclipse.org/modeling/tmf/xtext/updates/composite/releases/) (version 2.4.2+), [Google Guice](http://guice-plugin.googlecode.com/svn/trunk/eclipse-update-site/), the [graphical editing framework zest visualization toolkit](http://download.eclipse.org/tools/gef/updates/releases/) and [slf4j](http://download.eclipse.org/releases/kepler) into eclipse
3. Import the project set file (AADL Translator.psf) by going to File, Import... then under "Team" select "Team Project Project"

# Acknowledgements
This project uses icons from [famfamfam](http://famfamfam.com/lab/icons/silk/)'s silk icon set.
