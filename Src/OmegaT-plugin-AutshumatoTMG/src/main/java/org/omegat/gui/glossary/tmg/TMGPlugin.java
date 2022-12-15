/*
 *  This file is part of the Autshumato TMG plugin for OmegaT
 *  The plugin provides the interface to connect to the Autshumato 
 *  Translation Memory and Glossary system.
 *
 *  Copyright (C) 2017 Centre for Text Technology (CTexT®)
 *  Home page: http://www.nwu.co.za/ctext
 *  Project page: http://autshumatoite.sourceforge.net
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

package org.omegat.gui.glossary.tmg;

import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import org.omegat.core.Core;
import org.omegat.core.CoreEvents;
import org.omegat.core.events.IApplicationEventListener;
import org.omegat.util.Log;

/**
 * Entry point for the OmegaT-plugins-AutshumatoTMG.
 * 
 * Based on code for TaaS plugin - part of OmegaT
 * <code>org.omegat.gui.glossary.taas.TaaSPlugin</code> by
 * @author Alex Buloichik (alex73mail@gmail.com)
 * 
 * <p>Modified by Wildrich Fourie and Roald Eiselen
 */
public class TMGPlugin 
{
    private static BrowserPane bp;
    
    public TMGPlugin()
    {
        System.out.println("TMGPlugin constructor");
    }
    
    private static void initPlugin()
    {
        Log.log("TMGPlugin: Adding Panel");
        JMenu toolsMenu = Core.getMainWindow().getMainMenu().getGlossaryMenu();
        toolsMenu.addSeparator();
        toolsMenu.add(new AbstractAction("Autshumato TMG") {
            @Override
            public void actionPerformed(ActionEvent event) {
                TMGPlugin.bp = new BrowserPane(Core.getMainWindow(), 
                                "tmg", "Autshumato TMG");
            }
        });
    }
    
    /**
     * Register plugin into OmegaT.
     */
    public static void loadPlugins() {
        Log.log("TMGPlugin: Loading Plugin");
        
        CoreEvents.registerApplicationEventListener(new IApplicationEventListener() 
        {
            @Override
            public void onApplicationStartup() {
                Log.log("TMGPlugin: onApplicationStartup");
                initPlugin();
            }

            @Override
            public void onApplicationShutdown() {}
        });
    }
    
    /**
     * Called by OmegaT when plugins are unloaded
     */
    public static void unloadPlugins()
    {
        if (bp != null) {
            bp.close();
        }
    }
}
