/**************************************************************************
 *  This file is part of the Autshumato Language Project plugin for OmegaT
 *  The plugin provides support for creating projects in one of the 
 *  South African official languages. 
 *  This is done for two purposes, firstly, to properly support Sesotho sa Leboa
 *  (Sepedi/Northern Sotho) which only has a three letter ISO abbreviation (nso)
 *  and is not supported by OmegaT.
 *  Secondly, for ease of use by SA language translators
 * 
 *  Copyright (C) 2022 Centre for Text Technology (CTexT®)
 *  Home page: https://humanities.nwu.ac.za/ctext
 *  Project page: https://autshumato.sourceforge.net/
 * 
 * A derived work of OmegaT. The complete source code is available in the /lib/sources/ directory.
 * All modifications made are clearly indicated by the text "Autshumato ITE:".
 * Additionally the exact modifications are indicated in the /lib/source/omegat.patch
 * file. All the modifications were done between 2013/11/20 and 2013/11/30 by Wildrich Fourie
 * * and in 2022 by Roald Eiselen
 * 
 * OmegaT:
 * Home page: http://www.omegat.org/            
 * Support center: http://groups.yahoo.com/group/OmegaT/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **************************************************************************/

package org.omegat.gui.langproject;

import javafx.application.Platform;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.omegat.core.Core;
import org.omegat.core.CoreEvents;
import org.omegat.core.events.IApplicationEventListener;
import org.omegat.gui.local.ProjectUICommands;
import org.omegat.util.Log;

/**
 * Entry point for the OmegaT-plugins-AutshumatoZALangProject
 * Part of the code in this plugin originates from the standalone version of
 * the Autshumato ITE.
 * @author Roald Eiselen
 */
public class ZALangProjectPlugin {
    
    private ZALangProjectPlugin()
    {
        System.out.println("ZALangProjectPlugin constructor");
    }
    
    /**
     * Register plugin into OmegaT.
     */
    public static void loadPlugins() {
        Log.log("ZALangCharInsert: Loading Plugin");
        
        CoreEvents.registerApplicationEventListener(new IApplicationEventListener() 
        {
            @Override
            public void onApplicationStartup() {
                Log.log("ZALangCharInsert: onApplicationStartup");
                // Get the Project menu and insert a new MenuItem
                JMenu projectMenu = Core.getMainWindow()
                                     .getMainMenu().getProjectMenu();
                JMenuItem newProjectMenuItem = new JMenuItem();
                newProjectMenuItem.setText("New ZA Language Project...");                
                projectMenu.insert(newProjectMenuItem, 1);
                
                newProjectMenuItem.addActionListener(e -> {
                    newProjectMenuItem.setSelected(false);
                    // It doesn't seem possible to disable the menuItem
                    // automatically so it's impossible to prevent a click 
                    // if a project is currently loaded.
                    // Show a message box to inform user that the project 
                    // should be closed prior to creating a new project.
                    if (Core.getProject().isProjectLoaded()) {
                        JOptionPane
                            .showMessageDialog(Core.getMainWindow()
                                                .getApplicationFrame(), 
                                    "Cannot create a new project while another"
                                            + " project is still open.\n"
                                            + " Please close current project.", 
                                    "New ZA Language Project", 0);
                        return;
                    }
                    
                    // Run Open project GUI
                    ProjectUICommands.projectCreate();
                });
                
                Platform.setImplicitExit(false);
            }

            @Override
            public void onApplicationShutdown() {}
        });
    }
    
    /**
     * Interface to unload any plugin resources
     */
    public static void unloadPlugins()
    {
        
    }
}
