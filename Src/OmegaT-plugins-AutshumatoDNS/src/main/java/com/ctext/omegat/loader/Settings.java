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


import java.util.ArrayList;
import org.omegat.gui.dialogs.DocumentNamingField;
import org.omegat.util.FieldType;
import org.omegat.util.Preferences;

/**
 * Class to write and get the settings for this plugin.
 * @author Wildrich Fourie
 */
public class Settings
{
    // Setting keys
    private static final String SETTING_AUTSHUMATO_DNS_SEPARATOR = "autshumato_dns_separator";
    private static final String SETTING_AUTSHUMATO_DNS_FIELDS = "autshumato_dns_fields";
    private static final String SETTING_AUTSHUMATO_DNS_DEFAULTS = "autshumato_dns_defaults";
    private static final String SETTING_AUTSHUMATO_DNS_TARGET = "autshumato_dns_target";
    private static final String SETTING_AUTSHUMATO_DNS_TYPE = "autshumato_dns_type";
    //public static final String SETTING_AUTSHUMATO_DNS_ENABLE = "autshumato_dns_enable";
    
    // Properties
    private static final char DEFAULT_SEPARATOR = '.';
    private static char separator = '.';
    private static ArrayList<DocumentNamingField> fields = new ArrayList<DocumentNamingField>();
    private static boolean renameTarget = false;
    
    /** Number of maximum fields allowed, counted from 0. */
    public static final int MAX_FIELDS = 9;
    
    
    /**
     * Writes the settings as specified in the Preferences.
     * @param separator document fields separator
     * @param fields list of DocumentNamingFields items
     * @param renameTarget boolean indicating whether the target should be 
     * renamed
     */
    public static void writeSettings(char separator, ArrayList<DocumentNamingField> fields, boolean renameTarget)
    {
        Settings.separator = separator;
        Settings.fields = fields;
        Settings.renameTarget = renameTarget;
        
        Preferences.setPreference(SETTING_AUTSHUMATO_DNS_SEPARATOR, String.valueOf(separator));
        Preferences.setPreference(SETTING_AUTSHUMATO_DNS_TARGET, renameTarget);
        for(int i=0; i < fields.size(); i++)
        {
            if(fields.get(i) == null)
                continue;
            
            Preferences.setPreference(SETTING_AUTSHUMATO_DNS_FIELDS + i, fields.get(i).getFieldName());
            Preferences.setPreference(SETTING_AUTSHUMATO_DNS_DEFAULTS + i, fields.get(i).getDefaultValue());
            Preferences.setPreference(SETTING_AUTSHUMATO_DNS_TYPE + i, fields.get(i).getFieldTypeString());
        }
        
        // Erase other (previously entered) fields that are now empty.
        int remaining = MAX_FIELDS - fields.size();
        for(int i=fields.size(); i < remaining; i++)
        {
            Preferences.setPreference(SETTING_AUTSHUMATO_DNS_FIELDS + i,"");
            Preferences.setPreference(SETTING_AUTSHUMATO_DNS_DEFAULTS + i, "");
        }
        
        Preferences.save();
    }
    
    
    /** Loads the settings, if no settings were found initialize the defaults. */
    public static void readSettings()
    {   
        Settings.fields = new ArrayList<DocumentNamingField>();
        
        if(Preferences.existsPreference(SETTING_AUTSHUMATO_DNS_SEPARATOR))
            Settings.separator = Preferences.getPreference(SETTING_AUTSHUMATO_DNS_SEPARATOR).trim().charAt(0);
        else
            Settings.separator = DEFAULT_SEPARATOR;
        
        if(Preferences.existsPreference(SETTING_AUTSHUMATO_DNS_TARGET))
            Settings.renameTarget = Preferences.isPreference(SETTING_AUTSHUMATO_DNS_TARGET);

        int count = 0;
        while(true)
        {
            if(Preferences.existsPreference(SETTING_AUTSHUMATO_DNS_FIELDS + count) && 
                    !Preferences.getPreference(SETTING_AUTSHUMATO_DNS_FIELDS + count).equals(""))
            {
                String fieldName = Preferences.getPreference(SETTING_AUTSHUMATO_DNS_FIELDS + count);
                String defaultValue = "";
                if(Preferences.existsPreference(SETTING_AUTSHUMATO_DNS_DEFAULTS + count))
                    defaultValue = Preferences.getPreference(SETTING_AUTSHUMATO_DNS_DEFAULTS + count);
                
                String fieldType = FieldType.convertTypeToString(FieldType.STRING);
                if(Preferences.existsPreference(SETTING_AUTSHUMATO_DNS_TYPE + count))
                    fieldType = Preferences.getPreference(SETTING_AUTSHUMATO_DNS_TYPE + count);
                
                DocumentNamingField dnf = new DocumentNamingField(fieldName, defaultValue, count, fieldType);
                Settings.fields.add(dnf);
                
                count++;
            }
            else
                break;
        }
    }
    
    
    /**
     * Check that some DNS fields have been specified
     * Needed to use both this and method below
     * When valid settings have been entered on the Config it is saved in the Preferences
     * so if there are valid settings in the preferences then some valid info has been entered some time
     * which would be the best way to get valid settings.
     * @return whether the preferences contains any DNS settings
     */
    public static boolean preferencesContainsDNSSettings()
    {
        // Read it directly from the Preferences
        boolean existSeparator = Preferences.existsPreference(SETTING_AUTSHUMATO_DNS_SEPARATOR);
        boolean existField = Preferences.existsPreference(SETTING_AUTSHUMATO_DNS_FIELDS + "0");
        
        return existSeparator && existField;
    }

    
    /**
     * Returns the fields obtained from the preferences.
     * @return DocumentNamingFields defined in the preferences
     */
    public static ArrayList<DocumentNamingField> getFields()
    {
        return fields;
    }

    /**
     * Returns the separator character obtained from the preferences.
     * If a value could not be retrieved then use the default character '.'
     * @return the separator character defined by the user.
     */
    public static char getSeparator()
    {
        return separator;
    }

    /** 
     * Indicates whether the target documents should also be renamed. 
     * @return whether the target should be renamed
     */
    public static boolean isRenameTarget()
    {
        return renameTarget;
    }
}
