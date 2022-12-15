/*
 *  This file is part of the Autshumato DNS plugin for OmegaT
 *  The plugin provides the users with an easy way of renaming documents
 *  according to a defined file naming convention.
 *
 *  Copyright (C) 2020 Centre for Text Technology (CTexT®)
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

package org.omegat.plugin.autshumatoDNS;

import com.ctext.omegat.loader.DNSLoader;
import org.omegat.util.Log;

/**
 * This is just the very basic interface to load the plugin in OmegaT
 * @author Roald Eiselen
 */
public class DNSPlugin {
    
    private static DNSLoader loader;
    
    /**
     * Register plugin into OmegaT.
     */
    public static void loadPlugins() {
        Log.log("DNSPlugin: Loading Plugin");
        
        loader = new DNSLoader();
    }
    
    /**
     * Unload any used resource
     */
    public static void unloadPlugins()
    {
        
    }
    
}
