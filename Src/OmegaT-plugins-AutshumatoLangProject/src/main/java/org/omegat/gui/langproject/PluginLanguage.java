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

/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
               2007 Didier Briel, Zoltan Bartko
               2010-2011 Didier Briel
               2012 Guido Leenders
               Home page: http://www.omegat.org/
               Support center: http://groups.yahoo.com/group/OmegaT/

 This file is part of OmegaT.

 OmegaT is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 OmegaT is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **************************************************************************/

package org.omegat.gui.langproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.omegat.util.Language;
import org.omegat.util.Log;
import org.omegat.util.StaticUtils;

/**
 * This class is here, because the Locale has hard-coded '_' inside, and we must
 * adhere to ISO standard LL-CC.
 * <p>
 * This class tries to follow <a
 * href="http://www.lisa.org/standards/tmx/tmx.html#xml:lang">TMX Specification
 * on languages</a>, which is based on <a
 * href="http://www.ietf.org/rfc/rfc3066.txt">RFC 3066</a>, i.e.
 * <ul>
 * <li>Language is composed from 1-8 alpha (A-Za-z) chars, then "-", then 1-8
 * alpha/digit chars (A-Za-z0-9).
 * <li>Case insensitive
 * <li>Case is not altered by this class, even though there exist conventions
 * for capitalization ([ISO 3166] recommends that country codes are capitalized
 * (MN Mongolia), and [ISO 639] recommends that language codes are written in
 * lower case (mn Mongolian)).
 * </ul>
 * 
 * @author Maxym Mykhalchuk
 * @author Didier Briel
 * @author Zoltan Bartko bartkozoltan@bartkozoltan.com
 * @author Guido Leenders
 */

/**
 * Class inheriting from Language to support and display languages not
 * supported by OmegaT (Sepedi) and provide diacritic character lists for each
 * language for the CharInsert plugin
 * @author Roald Eiselen, Wildrich Fourie
 */
public class PluginLanguage extends Language {    
    private static final List<PluginLanguage> USER_DEFINED_LANGUAGES = 
            new ArrayList<PluginLanguage>();    
    private static final String FILE_LANGUAGE_PREFERENCES = "omegat.language.prefs";
    
    // Autshumato ITE:
    private final String displayName;
    // Autshumato ITE:
    private final String[] characters;
    
    // Autshumato ITE:
    /**
     * PluginLanguage constructor
     * Creates a new instance of Language, based on a string of a form "XX_YY"
     * or "XX-YY", where XX is a language code composed from 1-8 alpha (A-Za-z)
     * chars, and YY is a country ISO code composed from 1-8 alpha/digit
     * (A-Za-z0-9) chars.<br>
     * The form xx-xxxx-xx is also accepted, where "xxxx" is a 4 alpha characters script as defined in
     * <a href="http://unicode.org/iso15924/iso15924-codes.html">ISO 15924</a>. E.g., sr-Latn-RS, 
     * which represents Serbian ('sr') written using Latin script ('Latn') as used in Serbia ('RS').
     * This form is described in <a href="http://www.rfc-editor.org/rfc/bcp/bcp47.tx">BCP47</a>.
     * @param str The iso code for the language
     * @param languageName The full name associated with the language
     */
    public PluginLanguage(String str, String languageName) {
        super(str);
        this.displayName = languageName;
        this.characters = new String[0];
    }
    
    // Autshumato ITE:
    /**
     * PluginLanguage constructor
     * Creates a new instance of Language, based on a string of a form "XX_YY"
     * or "XX-YY", where XX is a language code composed from 1-8 alpha (A-Za-z)
     * chars, and YY is a country ISO code composed from 1-8 alpha/digit
     * (A-Za-z0-9) chars.<br>
     * The form xx-xxxx-xx is also accepted, where "xxxx" is a 4 alpha characters script as defined in
     * <a href="http://unicode.org/iso15924/iso15924-codes.html">ISO 15924</a>. E.g., sr-Latn-RS, 
     * which represents Serbian ('sr') written using Latin script ('Latn') as used in Serbia ('RS').
     * This form is described in <a href="http://www.rfc-editor.org/rfc/bcp/bcp47.tx">BCP47</a>.
     * @param str The iso code for the language
     * @param languageName The full name associated with the language
     * @param characters The set of diacritic characters available for the language
     */
    public PluginLanguage(String str, String languageName, String[] characters) {
        super(str);
        this.displayName = languageName;
        this.characters = characters.clone();
    }

    /**
     * Returns a name for the language that is appropriate for display to the
     * user.
     * @return Display name of PluginLanguage object
     */
    @Override
    public String getDisplayName() {
        // Autshumato ITE:
        if(displayName != null)
            return displayName;
        
        return super.getDisplayName();
    }    
        
    /**
     * 
     * @return whether the diacritic characters are available for instance
     */
    public boolean hasCharacters()
    {
        if(characters != null)
            if(characters.length > 0)
                return true;
        return false;
    }
    
   // Autshumato ITE:
    /**
     * Returns an array of strings containing the separate diacritic characters
     * of this language. If no characters have been defined <code>null</code> is
     * returned.
     * @return set of diacritic characters associated with the instance
     */
    public String[] getCharacters()
    {
        if(this.hasCharacters())
            return characters.clone();
        return new String[0]; 
    }
    
    static {
        PluginLanguage.loadLanguages();
    }
    
    /**
     * Get an unmodifiable list including all of the languages. If Languages
     * have been defined in the omgegat.language.pref file, only those languages
     * will be returned. Otherwise, the full list of languages defined by
     * omegat will be returned
     * @return A list of Language objects
     */
    public static List<Language> getLanguages() {
        if (USER_DEFINED_LANGUAGES.isEmpty()) {
            return Language.getLanguages();
        }
        return Collections.unmodifiableList(USER_DEFINED_LANGUAGES);
    }
    
    /**
     * Get an unmodifiable list of languages that have diacritics. Since only
     * user defined languages can have diacritics, if none were defined,
     * an empty list will be returned.
     * @return A list of PluginLanguage objects that have 1+ diacritic characters
     * defined.
     */
    public static List<PluginLanguage> getLanguagesWithDiacritics() {
        if (USER_DEFINED_LANGUAGES.isEmpty()) {
            return new ArrayList<PluginLanguage>(0);
        }
        ArrayList<PluginLanguage> diacriticLanguages 
                = new ArrayList<PluginLanguage>();
        for(PluginLanguage lang : USER_DEFINED_LANGUAGES){
            if (lang.hasCharacters()) {
                diacriticLanguages.add(lang);
            }
        }
        return Collections.unmodifiableList(diacriticLanguages);
    }
    
    /**
     * Static method to load the languages and the respective diacritic chars
     * associated with the language from the preferences file in the plugins folder
     */
    private static void loadLanguages()
    {
        try 
        {
            Path p = Paths.get(StaticUtils.installDir(), "plugins", 
                    FILE_LANGUAGE_PREFERENCES);
            File languagePreferencesFile = p.toFile();
            if(!languagePreferencesFile.exists())
            {   // if the file does not exists, the functionality has essentially
                // been removed                
                return;
            }
            
            try (FileInputStream fis = 
                    new FileInputStream(languagePreferencesFile)) {
            try (InputStreamReader isr = new InputStreamReader(fis, "UTF-8")) {
            try (BufferedReader in = new BufferedReader(isr)) {
                // BOM (byte order mark) bugfix
                in.mark(1);
                int ch = in.read();
                if (ch != 0xFEFF)
                    in.reset();

                for (String s = in.readLine(); s != null; s = in.readLine()) {
                    // skip lines that start with '#'
                    if (s.startsWith("#"))
                        continue;
                    // divide lines on tabs
                    String tokens[] = s.split("\t");
                    // check token list to see if it has a valid string
                    if (tokens.length < 3 || tokens[0].length() == 0)
                        continue;

                    // create the custom language
                    String code = tokens[1] + "_" + tokens[2];
                    String languageName = tokens[0];
                    if (tokens.length >= 4) {
                        String[] chars = tokens[3].split(",");
                        USER_DEFINED_LANGUAGES.add(new PluginLanguage(code, languageName, chars));
                    } else {
                        USER_DEFINED_LANGUAGES.add(new PluginLanguage(code, languageName));
                    }
                }
            }
            }
            }
        } catch (FileNotFoundException ex) {
            // there is no config file yet
        } catch (IOException iox) {
            Log.logWarningRB("PM_ERROR_READING_FILE");
            Log.log(iox);
        }
    }
}
