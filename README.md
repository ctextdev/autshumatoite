# Autshumato ITE

The Autshumato Integrated Translation Environment (ITE) is a series of plugins that customises the popular OmegaT translation memory application. It provides a single environment with translation memory, machine translation (MT) and a glossary to assist during the translation process. Although Autshumato ITE is specifically developed for the eleven official South African languages, OmegaT is language independent and can be used for translation between any two languages.

**Version**: 5.0.0    

**Platforms**: Windows, Linux, Mac 

**License**: GNU GPL 3 (or later)

# Prerequisites

- Java Runtime Environment (JRE) version 1.8 or higher

# Minimum system requirements

- 1GHz processor
- 1GB RAM
- 500MB available disk space

# Plugins included

- **Autshumato Diacritic Character Insertion**

	The Autshumato diacritic character insertion plugin for OmegaT provides functionality to easily insert diacritic characters associated with the various South African languages.

- **Autshumato Document Naming System**

	The Autshumato Document Naming System (DNS) plugin for OmegaT provides functionality for defining and renaming project documents according to a specific structure.

- **Autshumato ZA Language Project**

	The Autshumato ZA Language project plugin for OmegaT provides an additional dialog to create new OmegaT projects with only the South African languages available from the project properties menu.

- **Autshumato Machine Translation**

	The Autshumato Machine Translation (MT) plugin for OmegaT provides the connection to the Autshumato Machine Translation systems.

- **Autshumato Translation Memory and Glossary**

	The Autshumato Translation Memory and Glossary (TMG) plugin for OmegaT provides a connection to the Autshumato TMG available at https://tmg.nwu.ac.za/ for downloading and uploading TMs and Glossaries for the South African languages.

# License and Warranty

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3 of the License.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY, without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program.  If not, the license can be viewed here: http://www.gnu.org/licenses/.

# How to Install

**Windows**

Option 1: Install OmegaT together with all the Autshumato plugins

1. Download the latest "OmegaT with Autshumato plugins (Offline).exe" installer from SourceForge or GitHub.

2. Run the installer.
3. The installer will automatically install the required prerequisites and Autshumato plugins.
4. The OmegaT program and the Autshumato plugins is now installed and ready to be used.

Option 2: If you already have OmegaT installed and only want to install all the Autshumato plugins

1. Download the latest "OmegaT-plugins-Autshumato.zip" file from SourceForge or GitHub.
2. Extract the contents of "OmegaT-plugins-Autshumato.zip" file into the /plugins/ directory of your OmegaT installation.
3. After successful installation the /plugins/ directory should contain (at least) the following files:

	/OmegaT/plugins/    
	OmegaT-plugins-AutshumatoCharInsert-GPLv3License.txt    
	OmegaT-plugins-AutshumatoCharInsert-Readme.txt    
	OmegaT-plugins-AutshumatoCharInsert.jar    
	OmegaT-plugins-AutshumatoDNS-GPLv3License.txt    
	OmegaT-plugins-AutshumatoDNS-Readme.txt    
	OmegaT-plugins-AutshumatoDNS.jar    
	OmegaT-plugins-AutshumatoLangProject-GPLv3License.txt    
	OmegaT-plugins-AutshumatoLangProject-Readme.txt    
	OmegaT-plugins-AutshumatoLangProject.jar    
	OmegaT-plugins-AutshumatoMT-GPLv3License.txt    
	OmegaT-plugins-AutshumatoMT-Readme.txt    
	OmegaT-plugins-AutshumatoMT.jar    
	OmegaT-plugins-AutshumatoTMG-GPLv3License.txt    
	OmegaT-plugins-AutshumatoTMG-Readme.txt    
	OmegaT-plugins-AutshumatoTMG.jar    
	omegat.language.prefs

4. The Autshumato plugins is now installed and ready to be used.

**Linux**

1. Download the "OmegaT_4.3.3_Linux.tar.bz2" or "OmegaT_4.3.3_Linux_64.tar.bz2" tar from https://omegat.org/.
2. Place the archive in any suitable folder and unpack it.
3. Change folder to the folder containing OmegaT.jar and the linux-install.sh script, and execute the script with ./linux-install.sh
4. Download the latest "OmegaT-plugins-Autshumato.zip" file from SourceForge or GitHub.
5. Extract the contents of "OmegaT-plugins-Autshumato.zip" file into the /plugins/ directory of your OmegaT installation.
6. After successful installation the /plugins/ directory should contain (at least) the following files:

	/OmegaT/plugins/    
	OmegaT-plugins-AutshumatoCharInsert-GPLv3License.txt    
	OmegaT-plugins-AutshumatoCharInsert-Readme.txt    
	OmegaT-plugins-AutshumatoCharInsert.jar    
	OmegaT-plugins-AutshumatoDNS-GPLv3License.txt    
	OmegaT-plugins-AutshumatoDNS-Readme.txt    
	OmegaT-plugins-AutshumatoDNS.jar    
	OmegaT-plugins-AutshumatoLangProject-GPLv3License.txt    
	OmegaT-plugins-AutshumatoLangProject-Readme.txt    
	OmegaT-plugins-AutshumatoLangProject.jar    
	OmegaT-plugins-AutshumatoMT-GPLv3License.txt    
	OmegaT-plugins-AutshumatoMT-Readme.txt    
	OmegaT-plugins-AutshumatoMT.jar    
	OmegaT-plugins-AutshumatoTMG-GPLv3License.txt    
	OmegaT-plugins-AutshumatoTMG-Readme.txt    
	OmegaT-plugins-AutshumatoTMG.jar    
	omegat.language.prefs

7. The OmegaT program and the Autshumato plugins is now installed and ready to be used.

**Mac**

1. Download the "OmegaT_4.3.3_Mac_Notarized.zip" or "OmegaT_4.3.3_Mac.zip" zip from https://omegat.org/.
2. Unpack the OmegaT .zip archive to obtain a folder that contains a documentation file and the OmegaT application. Move the folder to an appropriate location such as the Applications folder.
3. Download the latest "OmegaT-plugins-Autshumato.zip" file from SourceForge or GitHub.
4. Extract the contents of "OmegaT-plugins-Autshumato.zip" file into the ~/Library/Preferences/OmegaT/plugins folder.
5. After successful installation the /plugins/ directory should contain (at least) the following files:

	/OmegaT/plugins/    
	OmegaT-plugins-AutshumatoCharInsert-GPLv3License.txt    
	OmegaT-plugins-AutshumatoCharInsert-Readme.txt    
	OmegaT-plugins-AutshumatoCharInsert.jar    
	OmegaT-plugins-AutshumatoDNS-GPLv3License.txt    
	OmegaT-plugins-AutshumatoDNS-Readme.txt    
	OmegaT-plugins-AutshumatoDNS.jar    
	OmegaT-plugins-AutshumatoLangProject-GPLv3License.txt    
	OmegaT-plugins-AutshumatoLangProject-Readme.txt    
	OmegaT-plugins-AutshumatoLangProject.jar    
	OmegaT-plugins-AutshumatoMT-GPLv3License.txt    
	OmegaT-plugins-AutshumatoMT-Readme.txt    
	OmegaT-plugins-AutshumatoMT.jar    
	OmegaT-plugins-AutshumatoTMG-GPLv3License.txt    
	OmegaT-plugins-AutshumatoTMG-Readme.txt    
	OmegaT-plugins-AutshumatoTMG.jar    
	omegat.language.prefs

6. The OmegaT program and the Autshumato plugins is now installed and ready to be used.

# How to run

**Windows**

- Use the shortcut created by the installer.

**Linux**

- Open the terminal and run the following commands:
	- cd \<the folder containing the OmegaT.jar>
	- /jre/bin/java -jar OmegaT.jar

**Mac**

- Run the OmegaT.app application.

# Useful links 

More information about the Autshumato project can be found at: https://autshumato.sourceforge.net/

More information about the developers can be found at: https://humanities.nwu.ac.za/ctext

More information about OmegaT can be found at: https://www.omegat.org/

