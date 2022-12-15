/*
 *  This file is part of the Autshumato Machine Translation plugin for OmegaT
 *  The plugin provides the interface to connect to the Autshumato 
 *  Machine Translation (MT) systems for South African languages.
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

package org.omegat.plugin.machinetranslators;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

import org.omegat.core.machinetranslators.BaseTranslate;
import org.omegat.util.Language;
import org.omegat.util.Log;
import org.omegat.util.PatternConsts;
import org.omegat.util.WikiGet;

/**
* Connection to the Autshumato Machine Translation systems.
* <p>Based on OmegaT <code>org.omegat.core.machinetranslators.GoogleTranslate.java</code> by
* Alex Buloichik (alex73mail@gmail.com) and Didier Briel.
* 
* <p>Modified by Wildrich Fourie, Cindy McKellar & Roald Eiselen
*/
public class AutshumatoTranslate extends BaseTranslate
{
   // Components 
   private static final String HOST_URL = "http://mt.nwu.ac.za/services/translate/ite";
   private static final String MARK_BEG = "{\"_status\":200,\"_message\":\"";
   private static final String MARK_END = "\"}";
   private static final Pattern RE_UNICODE = Pattern.compile("\\\\u([0-9A-Fa-f]{4})");
   private static final Pattern RE_HTML = Pattern.compile("&#([0-9]+);");

   /** 
    * Creates a new instance of the AutshumatoTranslate. 
    */
   public AutshumatoTranslate() {
       Log.log("Initialised Autshumato-MT");
   }
   
   /**
    * Get the name used in preferences 
    * @return Preference string name
    */
   @Override
   protected String getPreferenceName() {
       return "allow_autshumato_translate";
   }

   /**
    * Get the standard display name for the plugin
    * @return String name for display
    */
   @Override
   public String getName() {
       return "Autshumato Translate";
   }

   /**
    * Run a string through the translation service given the respective source
    * and target language
    * @param sLang Source language
    * @param tLang Target language
    * @param text Text to translate
    * @return Returns a translated version of the text
    * @throws Exception 
    */
   @Override
   protected String translate(Language sLang, Language tLang, String text) throws Exception
   {
       // If the text is longer than 5000 characters only send 5000 characters to the translator
       String trText = text.length() > 5000 ? text.substring(0, 4997) + "..." : text;
       
       // CLEANUP
       // Remove the tags
       Pattern tagPattern = PatternConsts.OMEGAT_TAG;
       Matcher tagMatcher = tagPattern.matcher(trText);
       if(tagMatcher.find()) {
           trText = tagMatcher.replaceAll("");
       }

       // Insert the text in the Parameters map
       Map<String, String> p = new TreeMap<>();
       p.put("source",sLang.getLanguageCode());
       p.put("target",tLang.getLanguageCode());
       p.put("text", trText);
       
       // Additional headers
       Map<String, String> h = new TreeMap<>();
       p.put("Accept-Charset", "UTF-8");

       // Query the host for the translation
       String v = WikiGet.get(HOST_URL, p, h);
       
       Log.log("Autshumato Translate");
       Log.log("Source: " + sLang.getLanguageCode());
       Log.log("Target: " + tLang.getLanguageCode());
       Log.log("Text: " + trText);
       Log.log("Return: " + v);
       
       while (true)  {
           Matcher m = RE_UNICODE.matcher(v);
           if (!m.find()) {
               break;
           }
           String g = m.group();
           char c = (char) Integer.parseInt(m.group(1), 16);
           v = v.replace(g, Character.toString(c));
       }
       
       // Replace special HTML chars
       v = v.replace("&quot;", "&#34;");
       v = v.replace("&nbsp;", "&#160;");
       v = v.replace("&amp;", "&#38;");
       
       while (true) {
           Matcher m = RE_HTML.matcher(v);
           if (!m.find()) {
               break;
           }
           String g = m.group();
           char c = (char) Integer.parseInt(m.group(1));
           v = v.replace(g, Character.toString(c));
       }

       // Only get the translation from the message received
       int beg = v.indexOf(MARK_BEG) + MARK_BEG.length();
       int end = v.indexOf(MARK_END, beg);
       String tr = v.substring(beg, end);

       tr = StringUtils.stripEnd(tr, "\n");

       // Clean additional spaces
       // Spaces after
       Matcher tag = PatternConsts.OMEGAT_TAG_SPACE.matcher(tr);
       while (tag.find()) {
           String searchTag = tag.group();
           if (!text.contains(searchTag)) { 
               // The tag didn't appear with a
               // trailing space in the source text
               String replacement = searchTag.substring(0, searchTag.length() - 1);
               tr = tr.replace(searchTag, replacement);
           }
       }

       // Spaces before
       tag = PatternConsts.SPACE_OMEGAT_TAG.matcher(tr);
       while (tag.find()) {
           String searchTag = tag.group();
           if (!text.contains(searchTag)) { // The tag didn't appear with a
               // leading space in the source text
               String replacement = searchTag.substring(1, searchTag.length());
               tr = tr.replace(searchTag, replacement);
           }
       }
       return tr;
   }   
}
