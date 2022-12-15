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

package org.omegat.gui.dialogs;

import org.omegat.util.FieldType;

/**
 * Special type to hold information about one Document Naming Field.
 * 
 * @author Widlrich Fourie
 */
public class DocumentNamingField 
{
    private String fieldName;
    private String defaultValue = "";
    private int order;
    private FieldType fieldType = FieldType.STRING;

    /**
     * Initialise a new instance of DocumentNamingField
     */
    public DocumentNamingField()  {}
    
    /**
     * Initialise a new instance of DocumentNamingField with parameters
     * @param fieldName Name of the field
     * @param defaultValue Default value of the field
     * @param order Integer indicating the order of the field
     * @param fieldType String value for the type of field
     */
    public DocumentNamingField(String fieldName, String defaultValue, int order, String fieldType) 
    {
        this.fieldName = fieldName;
        this.defaultValue = defaultValue;
        this.order = order;
        this.fieldType = FieldType.convertStringToType(fieldType);
    }

    /**
     * Get the default value of the field
     * @return Default value as String
     */
    public String getDefaultValue() 
    {
        return defaultValue;
    }

    /**
     * Get the field name as initialised
     * @return String field name
     */
    public String getFieldName() 
    {
        return fieldName;
    }

    /**
     * Get the order number of the field
     * @return Integer for order
     */
    public int getOrder() 
    {
        return order;
    }

    /**
     * Get the Type of field as a string
     * @return String field type
     */
    public String getFieldTypeString()
    {
        return FieldType.convertTypeToString(fieldType);
    }    

    /**
     * Get the Type of field as a string
     * @return String field type
     */
    public FieldType getFieldType()
    {
        return fieldType;
    }
    
    /**
     * Set the default value of the field
     * @param defaultValue Default field value
     */
    public void setDefaultValue(String defaultValue) 
    {
        this.defaultValue = defaultValue;
    }

    /**
     * Set the field name
     * @param fieldName Field name as a string
     */
    public void setFieldName(String fieldName) 
    {
        this.fieldName = fieldName;
    }

    /**
     * Set the ordering integer
     * @param order Order number
     */
    public void setOrder(int order) 
    {
        this.order = order;
    }

    /**
     * Set the field type
     * @param fieldType Field type string
     */
    public void setFieldType(String fieldType)
    {
        this.fieldType = FieldType.convertStringToType(fieldType);
    }
}