============== Autshumato Translation Memory and Glossary Plugin for OmegaT ==========

Version: 1.0.0
Compatible with OmegaT version: 4.3.2 and later
License: GNU GPL 3 (or later)
The licence is available in the OmegaT-plugins-Autshumato-GPLv3Licence.txt document.
Project Page: http://sourceforge.net/projects/autshumatoite/

The Autshumato Translation Memory and Glossary plugin for OmegaT provides a connection to the Autshumato TMG available at https://tmg.nwu.ac.za/ for downloading and uploading TMs and Glossaries for the South African languages.


============== About Autshumato ==========================================

The Autshumato project was initiated by the South African Department of Arts and Culture, and developments are done by the Centre for Text Technology (CTexT®) at the North-West University (Potchefstroom Campus).

The general aim of this project is the development of open source machine-aided translation tools and resources for South African languages. The term "open source" implies that every application developed in this project is freely available to the general public. This definition also extends to the source code of every application.

The objective of establishing this project as an open source project adheres to the South African National Government's policy and strategy for open source implementation. This policy specifies that all new software developed for government should be based on open standards. Furthermore, government also encourages and supports the use of open content and open standards within South Africa.

Website: http://autshumato.sourceforge.net/


============== INSTALL ===================================================

To install the plugin, extract all the contents of "OmegaT-plugins-AutshumatoTMG.zip" file into the /plugins/ directory of your OmegaT installation.
After successful installation the /plugins/ directory should contain (at least) the following files:

/OmegaT/plugins/
- OmegaT-plugins-AutshumatoTMG.jar
- OmegaT-plugins-AutshumatoTMG-GPLv3License.txt
- OmegaT-plugins-AutshumatoTMG-Readme.txt


============== USE =======================================================

Run OmegaT.
After OmegaT has started up, expand the Autshumato TMG panel, which should be available at the bottom of OmegaT alongside Dictionary|Machine Translation|Comments|Segment Properties. Expand the panel and navigate the TMG site to upload or download translation memories and glossaries to the current project. Please see https://tmg.nwu.ac.za/ for more information on using the TMG service.

If the Autshumato TMG panel is not available from the main user interface, the installation was not successful. Please ensure that the plugin was extracted to the correct location and that it is not contained within a subdirectory. 


============== TO BUILD ==================================================

To build the plugin, you require the complete OmegaT and Omegat-plugins-AutshumatoTMG sources which are available from: http://sourceforge.net/projects/omegat/ and http://sourceforge.net/projects/autshumatoite/ respectively.
Download and extract the sources. The project was created using the NetBeans IDE 12.1 and it is recommended but not required to use the same environment.
The build uses the Apache Maven build infrastructure, and all required libraries are defined as part of the Maven pom.xml file.


============== BUGS, FEATURE REQUESTS & HELP =============================

To report bugs, request new features, or to obtain additional help, visit the project page at: http://sourceforge.net/projects/autshumatoite/

Bug Reporting: https://sourceforge.net/p/autshumatoite/bug-reports
Feature Requests: https://sourceforge.net/p/autshumatoite/discussion
Help: https://sourceforge.net/projects/autshumatoite/support

For additional Autshumato plugins and resource or to become involved with the Autshumato project visit the website: http://autshumato.sourceforge.net/


============== LIBRARIES & LICENCES ======================================

OmegaT 5.7.1 (GPLv3)
lib-mnemonics (GPLv2)
openjfx 13.0.1 (GPL v2 with the Classpath exception)
vldocking 3.0.5 (LGPL) 

==========================================================================