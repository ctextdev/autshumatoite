/*
 *  This file is part of the Autshumato Diacritic Character plugin for OmegaT
 *  The plugin provides the interface to insert relevant diacritic characters
 *  applicable to the South African languages.
 *
 *  Copyright (C) 2022 Centre for Text Technology (CTexT®)
 *  Home page: https://humanities.nwu.ac.za/ctext
 *  Project page: https://autshumato.sourceforge.net/
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.omegat.gui.charinsert;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import javafx.application.Platform;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.omegat.core.Core;
import org.omegat.core.CoreEvents;
import org.omegat.core.events.IApplicationEventListener;
import org.omegat.util.Log;

/**
 * Entry point for the OmegaT-plugins-AutshumatoCharInsert.
 * Part of the code in this plugin originates from the standalone version of
 * the Autshumato ITE.
 * <p> Developed by Roald Eiselen and Wildrich Fourie
 */
public class ZALangCharInsert 
{    
    private ZALangCharInsert()
    {
        System.out.println("ZALangCharInsert constructor");
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
                // Get the Tools menu, since the Edit menu is not accessible
                // And it is not possible to add primary menu items as was
                // previously implemented in the Autshumato ITE
                JMenu toolsMenu = Core.getMainWindow().getMainMenu().getToolsMenu();
                JMenu insertMenu = new JMenu();
                insertMenu.setText("Insert diacritics");
                insertMenu.addActionListener(e -> {
                    insertMenu.setSelected(false);
                });
                
                // Iterate through the languages specified in the preferences
                // file, and add relevant language and diacritic menu items
                Iterator<PluginLanguage> iter = PluginLanguage
                                                .getLanguagesWithDiacritics()
                                                .iterator();
                boolean hasCharMenus = false;
                while (iter.hasNext()) {
                    PluginLanguage lang = iter.next();                
                    if (lang.hasCharacters()) {
                        JMenu langMenuItem = new JMenu();
                        langMenuItem.setText(lang.getDisplayName());
                        for (String langChar : lang.getCharacters()) {
                            JMenuItem charMenuItem = new JMenuItem();
                            charMenuItem.setText(langChar);
                            charMenuItem.setToolTipText("Press shift to get uppercase diacritic character");
                            charMenuItem.addActionListener(e -> {
                                // If the shift key is pressed while clicking
                                // on a menu item, insert the uppercase version
                                // of the character
                                if ((e.getModifiers() & ActionEvent.SHIFT_MASK)
                                        > 0) {
                                    Core.getEditor().insertText(langChar.toUpperCase());
                                }
                                else {
                                    Core.getEditor().insertText(langChar);                                    
                                }
                            });
                            langMenuItem.add(charMenuItem);
                        }
                        insertMenu.add(langMenuItem);
                        hasCharMenus = true;
                    }
                }
                // Only add the menu item to the Tools menu if there is one or 
                // more languages with diacritic characters
                if (hasCharMenus) {
                    toolsMenu.add(insertMenu, 0);
                }
                
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
