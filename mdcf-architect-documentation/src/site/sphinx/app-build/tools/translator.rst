.. include:: ../../util/substitution.rst
.. default-domain:: aadl
.. _aadl-app-translator:

#############################
The MDCF-Architect Translator
#############################

**********
Installing
**********

1. Download and install |OSATE2| by following the instructions on the `OSATE2 wiki <https://wiki.sei.cmu.edu/aadl/index.php/OSATE_2_download_page>`_. You can use either the stable or testing version, but most users will probably want the stable release.
2. Launch OSATE2, and then go to ``Help -> Install New Software...`` Click ``Add...``
3. Under ``Name:`` type *MDCF Architect* and under ``Location:`` type |Update Site URL|. Click ``Ok``.
4. Select the checkbox next to "Aadl-plugin" and click ``Next >``
5. On the "Install Details" page, click ``Next >`` a second time.
6. This plugin is licensed under the |EPL|. To proceed, select the "I accept the terms of the license agreement." Click ``Finish``.
7. The plugin is not digitally signed, so click ``Ok`` on the pop-up titled "Security Warning."
8. OSATE2 now has to restart to complete the installation.  Click ``Yes`` on the "Software Updates" pop-up.
9. Verify that the installation was successful by selecting ``About OSATE2`` (under OSATE2 on Mac OS X, under Help on Windows and Linux).  Click ``Installation Details`` and you should see "Aadl-plugin" at the top of the "Installed Software" tab.
10. [Optional] If you want HTML-formatting for the generated hazard analysis reports, you'll need to install `Pandoc <http://johnmacfarlane.net/pandoc/installing.html>`_, and set its path in your OSATE2 preferences.


*******
Running
*******

1. Configure the output directory by clicking ``Preferences`` (under "OSATE2" on Mac OS X, under "Window" in Windows and Linux) and then setting the "AppDev Directory" option. Click ``Ok``.
2. Open both the "map-globals" project and the app's project.
3. Double-click the file that contains your :construct:`system`, and then click the icon associated with your target format:
	1. |cog_go icon|: Java and XML for the MDCF
	2. |cog_error icon|: An app hazard analysis report
	3. |report_go icon|: Java for an MDCF device driver