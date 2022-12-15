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

package org.omegat.util;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods to check file names. Mainly for use with the Autshumato DNS.
 * @author Wildrich Fourie
 */
public class FileNameUtils
{
    private static final Pattern ALPHA_NUMERIC = Pattern.compile("[\\p{L}\\p{N}]");
    /**
     * Checks the string for any characters than may not be included in a file name.
     * @param input The string to check.
     * @return Boolean indicating whether the string has any illegal characters.
     */
    public static boolean containsIllegalCharacters(String input)
    {
        File f = new File(input);
        try {
            f.getCanonicalPath();
            return false;
        }
        catch (IOException e) {
            return true;
        }
        // This is a very limiting way to verify, since it is dependent
        // on the defined character set, while the set of invalid chars may
        // include other chars, and this is not system specific
        /*return 
           (input.contains("/") 
           || input.contains("\\") 
           || input.contains(":") 
           || input.contains("*") 
           || input.contains("?") 
           || input.contains("\"")
           || input.contains("<") 
           || input.contains(">") 
           || input.contains("|"));*/
    }
    
    /**
     * Checks if the specified string contains alphanumeric characters.
     * @param input The string to check
     * @return Boolean indicating whether any alphanumeric characters have been found.
     */
    public static boolean containsAlphaNumericCharacters(String input)
    {
        Matcher matcher = ALPHA_NUMERIC.matcher(input);
        return matcher.matches();
    }
}
