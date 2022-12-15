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
   and in 2022 by Roald Eiselen
   
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

package org.omegat.util;

import com.ctext.omegat.loader.ADNSStringHandler;

/**
 * The types of fields that can be specified for the Document Naming.
 * <li>STRING - Normal user entered data
 * <li>DATE - Automatically attaches the current date
 * <li>COUNTER - Integer that automatically increases with each document
 * <li>TITLE - The current document title that is inserted automatically.
 * <li>LANGUAGE - Automatically insert the Source or Target language in this field,
 * depending of if the source or target document is renamed.
 * 
 * @author Wildrich Fourie
 */
public enum FieldType
{
    /**
     * String field which can be edited by the user
     */
    STRING(ADNSStringHandler.getString("ADNS_FIELDTYPE_STRING")), 
    /**
     * Date field which should conform to parseable date
     */
    DATE(ADNSStringHandler.getString("ADNS_FIELDTYPE_DATE")), 
    /**
     * Integer field for version number of the document
     */
    COUNTER(ADNSStringHandler.getString("ADNS_FIELDTYPE_COUNTER")),
    /**
     * Language String representation, the 2 letter ISO code
     */
    LANGUAGE(ADNSStringHandler.getString("ADNS_FIELDTYPE_LANGUAGE")), 
    /**
     * Title field
     */
    TITLE(ADNSStringHandler.getString("ADNS_FIELDTYPE_TITLE")),
    /**
     * Default field when no other value is set
     */
    NONE("None");
    
    
    /**
     * Converts a String representation to the FieldType enum value.
     * @param input
     * @return FieldType for string representation. Returns FieldType.None if 
     * no FieldTypes matches the input string
     */
    public static FieldType convertStringToType(String input)
    {
        for (FieldType t : FieldType.values()) {
            if (t.name().equals(input)
                ||
                t.fieldTypeString.equals(input)) {
                return t;
            }
        }
        return FieldType.NONE;
    }
    
    /** 
     * Converts a type to a string. 
     * @param fieldType The FieldType value to convert
     * @return String value of the Type
     */
    public static String convertTypeToString(FieldType fieldType)
    {
        return fieldType.fieldTypeString;
    }
    
    /**
     * Get an array of the string representations of all the valid FieldType
     * enum values
     * @return Array of FieldType string representations
     */
    public static String[] getValuesStrings() {
        FieldType[] ft = FieldType.values();
        String[] stringValues = new String[ft.length];
        for (int i = 0; i < FieldType.values().length; i++) {
            stringValues[i] = ft[i].fieldTypeString;
        }
        
        return stringValues;
    }
    
    private final String fieldTypeString;
    private FieldType(String fieldTypeName) {
        this.fieldTypeString = fieldTypeName;
    }
}
