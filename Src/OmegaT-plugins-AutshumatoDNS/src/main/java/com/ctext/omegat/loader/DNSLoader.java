/**************************************************************************
 *  This file is part of the Autshumato Document Naming System plugin for OmegaT
 *  The plugin provides support for renaming project documents to conform
 *  to the document naming system conventions as required by the 
 *  Department of Sports, Arts, And Culture
 *
 *  Copyright (C) 2022 Centre for Text Technology (CTexT®)
 *  Home page: https://humanities.nwu.ac.za/ctext
 *  Project page: https://autshumato.sourceforge.net/
   
   A derived work of OmegaT. The complete source code is available in the /lib/sources/ directory.
   All modifications made are clearly indicated by the text "Autshumato ITE:".
   Additionally the exact modifications are indicated in the /lib/source/omegat.patch
   file. All the modifications were done between 2013/11/20 and 2013/11/30 by Wildrich Fourie
   * and in 2022 by Roald Eiselen
   
   OmegaT:
   Home page: http://www.omegat.org/            
   Support center: http://groups.yahoo.com/group/OmegaT/
  
   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 3 of the License, or 
   (at your option) any later version.
   
   This program is distributed in the hope that it will be useful, 
   but WITHOUT ANY WARRANTY; without even the implied warranty of 
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   
   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **************************************************************************/

package com.ctext.omegat.loader;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.omegat.core.Core;
import org.omegat.core.CoreEvents;
import org.omegat.core.events.IApplicationEventListener;
import org.omegat.core.events.IProjectEventListener;
import org.omegat.core.events.IProjectEventListener.PROJECT_CHANGE_TYPE;
import org.omegat.gui.dialogs.AutshumatoDNSConfigurationDialog;
import org.omegat.gui.dialogs.AutshumatoDocumentNameDialog;
import org.omegat.gui.dialogs.DocumentNamingField;
import org.omegat.gui.main.ProjectUICommands;
import org.omegat.util.FileUtil;
import org.omegat.util.Log;


/**
 * This is the loader class that initializes and adds the extra user interface feature of this plugin.
 * @author Wildrich Fourie
 */
public class DNSLoader
{       
    private AutshumatoDNSConfigurationDialog configDialog;

    /**
     * Initialize a new instance of the DNSLoader class adding a menu options
     * to the Options menu and reading the necessary settings
     */
    public DNSLoader()
    {
        addMenuOption();
        Settings.readSettings();
    }


    // Adds the 'Configure Autshumato MT' to the Options Menu.
    private void addMenuOption()
    {
        // This works for the DNS, test if it works for the MT
        CoreEvents.registerApplicationEventListener(new IApplicationEventListener() 
        {
            @Override
            public void onApplicationStartup()
            {
                initNewMenuItem();
            }

            @Override
            public void onApplicationShutdown() {}
        });
        
        // When the project is changed
        CoreEvents.registerProjectChangeListener(new IProjectEventListener() 
        {
            @Override
            public void onProjectChanged(PROJECT_CHANGE_TYPE eventType)
            {
                if(eventType != null) {
                    switch (eventType) {
                        case CLOSE:
                            dnsAction.setEnabled(false);
                            break;
                        case CREATE:
                        case LOAD:
                            if(Settings.preferencesContainsDNSSettings())
                                dnsAction.setEnabled(true);
                            break;
                    // Call the rename of Target documents from here [Later]
                        case COMPILE:
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    //private JMenu autshumatoMenu;
    private AutshumatoDocumentNameDialog documentNameDialog;
    private JMenuItem dnsAction;
    private void initNewMenuItem()
    {
        JMenu menu = Core.getMainWindow().getMainMenu().getOptionsMenu();       
        JMenuItem dnsConfigMenuItem = new JMenuItem(ADNSStringHandler.getString("ADNS_MENU_CONFIG"));
        dnsConfigMenuItem.addActionListener(new CustomHandler());
        
        dnsAction = new JMenuItem(ADNSStringHandler.getString("ADNS_MENU_RENAME"));
        dnsAction.setEnabled(false);
        dnsAction.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Could only reach here if the Preferences contains valid DNS settings.
                renameCounter = 0;
                
                File srcRootDir = new File(Core.getProject().getProjectProperties().getSourceRoot());
                renameDocuments(srcRootDir, true);
                
                if(Settings.isRenameTarget())
                {
                    File trgRootDir = new File(Core.getProject().getProjectProperties().getTargetRoot());
                    renameDocuments(trgRootDir, false);
                }
                // Project reload after all the files have been renamed,
                // only reload if 1 or more were renamed!
                if(renameCounter > 0)
                    ProjectUICommands.projectReload();
            }
        });
         
        menu.addSeparator();
        menu.add(dnsConfigMenuItem);
        menu.add(dnsAction);
    }

    private int renameCounter = 0;
    private void renameDocuments(File rootDir, boolean sourceDocuments)
    {
        // Retrieve the settings
        ArrayList<DocumentNamingField> fields = Settings.getFields();
        char sep = Settings.getSeparator();
        
        if(documentNameDialog != null)
            documentNameDialog.resetCallCounter();
        
        // Get the documents and open the rename dialog for each.
        List<File> fileList = getDocumentList(rootDir);
        List<String> renameFailList = new ArrayList<String>();
        for(int i=0; i < fileList.size(); i++)
        {
            File fileName = fileList.get(i);
            
            if(documentNameDialog == null)
                documentNameDialog = new AutshumatoDocumentNameDialog(Core.getMainWindow().getApplicationFrame(), true);
            documentNameDialog.resetCounter(0);
            documentNameDialog.initFieldsAndDefaults(fields, sep, fileName.getName(), sourceDocuments);
            documentNameDialog.setVisible(true);

            // Rename each document on which OK was returned on the DocumentName Dialog.
            if(documentNameDialog.getReturnStatus() == AutshumatoDocumentNameDialog.RET_OK)
            {
                renameCounter++;
                String newName = documentNameDialog.getNewDocumentName();

                // Handle a duplicate name
                if(containsDuplicateFile(rootDir, newName))
                {
                    String message = ADNSStringHandler.getString("ADNS_RENAME_ERROR_EXISTS_DM");
                    message = message.replace("<filename>", newName);
                    message = message.replace("<path>", rootDir.getAbsolutePath());
                    int ret = JOptionPane.showConfirmDialog(Core.getMainWindow().getApplicationFrame(),
                            message, 
                            ADNSStringHandler.getString("ADNS_RENAME_ERROR_EXISTS_DT"), 
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    
                    if(ret == JOptionPane.YES_OPTION)
                    {
                        // Decrease 'i' to handle this file again.
                        i--;
                        continue;
                    }
                    else
                        continue;
                }

                // Rename the document and if it fails add it to the FailList.
                if(!renameDocument(rootDir, fileName.getName(), newName))
                    renameFailList.add(fileName.getName());
            }
        }
        
        if(renameFailList.size() > 0)
        {
            StringBuilder renameFailNames = new StringBuilder();
            for(String s : renameFailList) {
                renameFailNames.append(s);
                renameFailNames.append(System.lineSeparator());
            }

            String message = ADNSStringHandler.getString("ADNS_RENAME_ERROR_DONE_DM");
            message = message.replace("<number>", "" + renameFailList.size());
            message = message.replace("<names>", renameFailNames);
            JOptionPane.showMessageDialog(Core.getMainWindow().getApplicationFrame(), 
                    message,
                    ADNSStringHandler.getString("ADNS_RENAME_ERROR_DONE_DT"),
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    
    /**
     * Retrieves the paths of all files in the specified directory.
     * If true is specified the Target files are retrieved,
     * else the Source files are obtained.
     */
    private List<File> getDocumentList(File rootDir)
    {
        List<File> fileList;
        try {
            fileList = FileUtil.buildFileList(rootDir, true);
            return fileList;
        } catch (IOException ex) {
            Logger.getLogger(DNSLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>(0);
    }
    
    
    /** 
     * Check if the specified directory already contains a file with 
     * the same name as the specified string. 
     */
    private boolean containsDuplicateFile(File directory, String fileName)
    {
        String[] currentFiles = directory.list();
        for(String curFile : currentFiles)
            if(curFile.equalsIgnoreCase(fileName))
                return true;
        return false;
    }
    
    
    /** Renames the specified document with the parameters received. */
    private boolean renameDocument(File directory, String srcFileName, String newFileName)
    {
        try
        {
            File src = new File(directory, srcFileName);
            File trg = new File(directory, newFileName);
            boolean success = src.renameTo(trg);
            return success;
        }
        catch (Exception ex)
        {
            Log.log(ADNSStringHandler.getString("ADNS_ERROR_RENAME") + srcFileName);
            Log.log(ex);
            return false;
        }
    }

    
    // Custom Handler for the 'Configure Autshumato DNS' menu item.
    private class CustomHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            configDialog = new AutshumatoDNSConfigurationDialog(Core.getMainWindow().getApplicationFrame());   
            configDialog.setVisible(true);
        }
    }
}
