/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2016 Chihiro Hio, Aaron Madlon-Kay
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

package org.omegat.gui.glossary.tmg;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.omegat.core.Core;
import org.omegat.core.CoreEvents;
import org.omegat.core.data.IProject;
import org.omegat.core.data.ProjectProperties;
import org.omegat.core.events.IApplicationEventListener;
import org.omegat.core.events.IProjectEventListener;
import org.omegat.externalfinder.item.ExternalFinderConfiguration;
import org.omegat.externalfinder.item.ExternalFinderItem;
import org.omegat.externalfinder.item.ExternalFinderItem.SCOPE;
import org.omegat.externalfinder.item.ExternalFinderItemMenuGenerator;
import org.omegat.externalfinder.item.ExternalFinderItemPopupMenuConstructor;
import org.omegat.externalfinder.item.ExternalFinderXMLLoader;
import org.omegat.externalfinder.item.ExternalFinderXMLWriter;
import org.omegat.externalfinder.item.IExternalFinderItemLoader;
import org.omegat.externalfinder.item.IExternalFinderItemMenuGenerator;
import org.omegat.util.Log;
import org.omegat.util.StaticUtils;

/**
 * Entry point for ExternalFinder functionality.
 * <p>
 * ExternalFinder was originally a plugin developed by Chihiro Hio, and
 * generously donated for inclusion in OmegaT itself.
 * <p>
 * See {@link #getProjectFile(IProject)} and {@link ExternalFinderItem} for
 * details about how this implementation's behavior differs from the original
 * plugin.
 * <p>
 * See the plugin page or <code>package.html</code> for information about the
 * XML format.
 * 
 * @see <a href=
 *      "https://github.com/hiohiohio/omegat-plugin-externalfinder">omegat-plugin-externalfinder
 *      on GitHub</a>
 */
public class ExternalFinder {

    private static final String FINDER_FILE = "finder.xml";
    
    private static final Logger LOGGER = Logger.getLogger(ExternalFinder.class.getName());

    /**
     * OmegaT will call this method when loading plugins.
     */
    public static void loadPlugins() {
        // register listeners
        CoreEvents.registerApplicationEventListener(generateIApplicationEventListener());
        CoreEvents.registerProjectChangeListener(generateIProjectEventListener());
    }

    private static IProjectEventListener generateIProjectEventListener() {
        return new IProjectEventListener() {
            private final List<Component> menuItems = new ArrayList<Component>();

            @Override
            public void onProjectChanged(final IProjectEventListener.PROJECT_CHANGE_TYPE eventType) {
                switch (eventType) {
                    case LOAD:
                        onLoad();
                        onLoadCommit();
                        break;
                    case CLOSE:
                        onClose();
                        break;
                    default:
                    // ignore
                }
            }

            private void onLoad() {
                // clear old items
                menuItems.clear();

                // add finder items to menuItems
                final IExternalFinderItemMenuGenerator generator
                        = new ExternalFinderItemMenuGenerator(ExternalFinderItem.TARGET.BOTH, false);
                final List<JMenuItem> newMenuItems = generator.generate();
                menuItems.addAll(newMenuItems);

                // add menuItems to menu
                final JMenu toolsMenu = Core.getMainWindow().getMainMenu().getToolsMenu();
                menuItems.forEach(component -> {
                    toolsMenu.add(component);
                });
            }

            private void onClose() {
                // remove menu items
                final JMenu menu = Core.getMainWindow().getMainMenu().getToolsMenu();
                menuItems.forEach(menu::remove);
                menuItems.clear();

                PROJECT_CONFIG = null;
            }
            
            private void writeHeader(File output_file, String name,
                                     String copyright,String licence, 
                                     String domain, String srclang,
                                     String trglang)
            {
                String text = "";
                try {
                    try (BufferedReader brTest = 
                            Files.newBufferedReader(output_file.toPath(), 
                                                    StandardCharsets.UTF_8)) {
                        text = brTest.readLine();
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ExternalFinder.class.getName()).log(Level.SEVERE, null, ex);
                }catch (IOException ex) {
                    Logger.getLogger(ExternalFinder.class.getName()).log(Level.SEVERE, null, ex);
                }
                                    
                String header = "#<HEADER\r\n" +
                                            "#name = "+name+"\r\n" +
                                            "#version = 1\r\n" +
                                            "#copyright = "+copyright+"\r\n" +
                                            "#licence = "+licence+"\r\n" +
                                            "#domain = "+domain+"\r\n" +
                                            "#srclang = "+srclang+"\r\n" +
                                            "#trglang = "+trglang+"\r\n" +
                                            "#>\r\n" +
                                            "#</HEADER>\r\n" + text;
                
                try {
                    try(RandomAccessFile f = new RandomAccessFile(
                                    new File(output_file.toString(), "UTF-8"), 
                                        "rw")) {                        
                        f.seek(0); // to the beginning
                        f.write(header.getBytes("UTF-8"));
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ExternalFinder.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
            
            private void onLoadCommit()
            {
                int backup_freq = 7;
        
                Date currentDate = new Date();
        
                String string_date = "February 2, 2017";
                DateFormat format = new SimpleDateFormat("MMMM d, yyyy", 
                                    Locale.ENGLISH);
                Date olderDate = null;
                try {
                    olderDate = format.parse(string_date);
                } catch (ParseException ex) {
                    Logger.getLogger(ExternalFinder.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
                
                TreeMap<String, String> tree = new TreeMap<>();
                
                tree.put("AFR-ZA", "Afrikaans");
                tree.put("ENG-GB", "English");
                tree.put("NBL-ZA", "isiNdebele");
                tree.put("ZUL-ZA", "IsiZulu");
                tree.put("XHO-ZA", "IsiXhosa");
                tree.put("SOT-ZA", "Sesotho");
                tree.put("SSW-ZA", "Siswati");
                tree.put("TSN-ZA", "Setswana");
                tree.put("NSO-ZA", "Sepedi");
                tree.put("VEN-ZA", "Tshivenda");
                tree.put("TSO-ZA", "Xitsonga");
                
                tree.put("AF-ZA", "Afrikaans");
                tree.put("EN-GB", "English");
                tree.put("NR", "isiNdebele");
                tree.put("ZU", "IsiZulu");
                tree.put("XH", "IsiXhosa");
                tree.put("ST", "Sesotho");
                tree.put("SS", "Siswati");
                tree.put("TN", "Setswana");
                tree.put("ND", "Sepedi");
                tree.put("VE", "Tshivenda");
                tree.put("TS", "Xitsonga");
                
                int diffInDays = (int)( (currentDate.getTime() 
                                    - olderDate.getTime()) / (1000 * 60 * 60 * 24) );
                
                String auto_tm_root = Core.getProject().getProjectProperties().getProjectRoot() + "omegat/";
                String auto_gloss_root = Core.getProject().getProjectProperties().getGlossaryRoot();
                
                String target_lang = Core.getProject().getProjectProperties().getTargetLanguage().toString();
                String source_lang = Core.getProject().getProjectProperties().getSourceLanguage().toString();
                
                String full_target_lang = tree.get(target_lang);
                String full_source_lang = tree.get(source_lang);
                
                File f_tm = new File(auto_tm_root+"project_save.tmx");
                File f_gloss = new File(auto_gloss_root+"glossary.txt");
                
                
                if(f_tm.exists() || f_gloss.exists())
                { 
                    if (diffInDays > backup_freq)
                    {
                        int dialogButton = JOptionPane.YES_NO_OPTION;
                        int dialogResult = JOptionPane.showConfirmDialog (null, "Would You Like to Save your Glossary and Translation Memory?","Warning",dialogButton);
                        if(dialogResult == JOptionPane.YES_OPTION)
                        {
                            JTextField aff_Field = new JTextField(50);
                            JTextField dom_Field = new JTextField(50);
                            JTextField lang_Field = new JTextField(50);
                            JTextField cr_Field = new JTextField(50);
                            JTextField tm_new_fn_Field = new JTextField(50);
                            JTextField gloss_new_fn_Field = new JTextField(50);
                            
                            JLabel aff_label = new JLabel("Affiliation:");
                            JLabel dom_label = new JLabel("Domain:");
                            JLabel lang_label = new JLabel("Languages:");
                            JLabel cr_label = new JLabel("Copyright:");
                            JLabel lic_label = new JLabel("Licence:");
                            JLabel new_fn_label = new JLabel("Resource name:");
                            
                            String[] comboBoxArray = {"Creative Commons Attribution 3.0 ZA","Creative Commons Attribution-ShareAlike 3.0 ZA","Creative Commons Attribution-NoDerivs 3.0 ZA","Creative Commons Attribution-NonCommercial 3.0 ZA","Creative Commons Attribution-NonCommercial-ShareAlike 3.0 ZA","Creative Commons Attribution-NonCommercial-NoDerivs 3.0 ZA"};
                            JComboBox<String> lic_list = new JComboBox<>(comboBoxArray);
                            
                            lang_Field.setEditable(false);

                            JPanel myPanel = new JPanel(new GridBagLayout());
                            GridBagConstraints c = new GridBagConstraints();
                            c.anchor = GridBagConstraints.WEST;
                            c.gridx = 0;
                            c.gridy = 0;
                            c.insets = new Insets(2,2,2,2);
                            
                            myPanel.add(aff_label,c);
                            c.gridy++;
                            myPanel.add(aff_Field,c);
                            c.gridy+=2;
                            
                            myPanel.add(dom_label,c);
                            c.gridy++;
                            myPanel.add(dom_Field,c);
                            c.gridy+=2;
                            
                            myPanel.add(lang_label,c);
                            c.gridy++;
                            myPanel.add(lang_Field,c);
                            c.gridy+=2;
                            
                            myPanel.add(cr_label,c);
                            c.gridy++;
                            myPanel.add(cr_Field,c);
                            c.gridy+=2;
                            
                            myPanel.add(lic_label,c);
                            c.gridy++;
                            myPanel.add(lic_list,c);
                            c.gridy+=2;
                            
                            myPanel.add(new_fn_label,c);
                            c.gridy++;
                            
                            myPanel.add(tm_new_fn_Field,c);
                            myPanel.add(gloss_new_fn_Field,c);
                            c.gridy+=2;
                            
                            Pattern diacritic_langs = Pattern.compile("Afrikaans|Setswana|Sepedi|Tshivenda");
                            
                            aff_Field.addFocusListener(new FocusListener() {
                                @Override
                                public void focusGained(FocusEvent e) {                                
                            }

                                @Override
                                public void focusLost(FocusEvent e) {
                                    tm_new_fn_Field.setText(
                                            lang_Field.getText()
                                            +", "+aff_Field.getText()
                                            +", "+dom_Field.getText()
                                            +" Version 1.tmx");
                                    // Afrikaans, Sepedi Mathematics Glossary from DAC Version 1 (18).utf8
                                    if (diacritic_langs
                                        .matcher(lang_Field.getText()).find())
                                    {
                                        gloss_new_fn_Field
                                        .setText(lang_Field.getText()
                                                +" "+dom_Field.getText()
                                                +" Glossary from "
                                                +aff_Field.getText()
                                                +" Version 1.utf8");
                                    }

                                    else
                                    {
                                        gloss_new_fn_Field
                                        .setText(lang_Field.getText()
                                            +" "+dom_Field.getText()
                                            +" Glossary from "
                                            +aff_Field.getText()
                                            +" Version 1.tab");
                                    }                                
                                }
                            });
                            
                            dom_Field.addFocusListener(new FocusListener() {
                            @Override
                            public void focusGained(FocusEvent e) {     
                            }

                            @Override
                            public void focusLost(FocusEvent e) {
                                tm_new_fn_Field
                                    .setText(lang_Field.getText()
                                        +", "
                                        +aff_Field.getText()
                                        +", "
                                        +dom_Field.getText()
                                        +" Version 1.tmx");
                                if (diacritic_langs
                                        .matcher(lang_Field.getText()).find())
                                {
                                    gloss_new_fn_Field
                                        .setText(lang_Field.getText()
                                            +" "
                                            +dom_Field.getText()
                                            +" Glossary from "
                                            +aff_Field.getText()
                                            +" Version 1.utf8");
                                }                                
                                else
                                {
                                    gloss_new_fn_Field
                                        .setText(lang_Field.getText()
                                            +" "
                                            +dom_Field.getText()
                                            +" Glossary from "
                                            +aff_Field.getText()
                                            +" Version 1.tab");
                                }
                            }
                            });

                            lang_Field.setText(full_source_lang
                                                +", "+full_target_lang);
                            
                            if(f_tm.exists())
                            {
                                tm_new_fn_Field.setVisible(true);
                                gloss_new_fn_Field.setVisible(false);
                                
                                int tm_result = JOptionPane
                                        .showConfirmDialog(null, myPanel, 
                                                "Upload Translation Memory", 
                                                JOptionPane.OK_CANCEL_OPTION);
                                
                                if (tm_result == JOptionPane.OK_OPTION)
                                {
                                    String new_tm_name = tm_new_fn_Field.getText();

                                    File f_out_tm = new File(auto_tm_root
                                                            +new_tm_name+".tmp");
                                    try {
                                        Files.copy(f_tm.toPath(), 
                                            f_out_tm.toPath(), 
                                            StandardCopyOption.REPLACE_EXISTING);
                                    } catch (IOException ex) {
                                        Logger
                                            .getLogger(ExternalFinder
                                                        .class.getName())
                                                        .log(Level.SEVERE, 
                                                                null, ex);
                                    }
                                    
                                    writeHeader(f_out_tm, tm_new_fn_Field.getText(), 
                                            cr_Field.getText(), 
                                            lic_list.getSelectedItem().toString(), 
                                            dom_Field.getText(), 
                                            full_source_lang, full_target_lang); 
                                }
                            }
                            
                            if(f_gloss.exists())
                            {        
                                tm_new_fn_Field.setVisible(false);
                                gloss_new_fn_Field.setVisible(true);
                                
                                int gloss_result = JOptionPane.showConfirmDialog(null, myPanel, "Upload Glossary", JOptionPane.OK_CANCEL_OPTION);
                                
                                if (gloss_result == JOptionPane.OK_OPTION)
                                    {
                                    String new_gloss_name = gloss_new_fn_Field.getText();

                                    File f_out_gloss = new File(auto_gloss_root+new_gloss_name+".tmp");
                                    try {
                                        Files.copy(f_gloss.toPath(), f_out_gloss.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    } catch (IOException ex) {
                                        Logger.getLogger(ExternalFinder.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    
                                    writeHeader(f_out_gloss, gloss_new_fn_Field.getText(), cr_Field.getText(), lic_list.getSelectedItem().toString(), dom_Field.getText(), full_source_lang, full_target_lang);
                                    
                                }          
                            }
                        }
                    }
                Log.log("Date difference: "+ diffInDays);
                }
            }
        };
    }

    private static IApplicationEventListener generateIApplicationEventListener() {
        return new IApplicationEventListener() {

            @Override
            public void onApplicationStartup() {
                Core.getEditor().registerPopupMenuConstructors(getGlobalConfig().getPriority(),
                        new ExternalFinderItemPopupMenuConstructor());
            }

            @Override
            public void onApplicationShutdown() {
            }
        };
    }

    /**
     * Code called by OmegaT when unloading plugins
     */
    public static void unloadPlugins() {
    }

    private static ExternalFinderConfiguration GLOBAL_CONFIG = initGlobalConfig();

    /**
     * Get the global configuration. This is stored in the user's OmegaT
     * configuration directory. If the file does not exist, an empty
     * configuration will be created and returned.
     * 
     * @return The configuration (will never be null)
     */
    public static ExternalFinderConfiguration getGlobalConfig() {
        return GLOBAL_CONFIG;
    }

    /**
     * Set the global configuration. Any existing configuration file will be
     * overwritten with the new one. Pass null to delete the config file.
     * @param newConfig The new configuration to set and overwrite
     */
    public static void setGlobalConfig(ExternalFinderConfiguration newConfig) {
        ExternalFinderConfiguration oldConfig = GLOBAL_CONFIG;
        GLOBAL_CONFIG = newConfig;
        if (!Objects.equals(newConfig, oldConfig)) {
            writeConfig(newConfig, getGlobalConfigFile());
        }
    }

    private static ExternalFinderConfiguration initGlobalConfig() {
        ExternalFinderConfiguration efc = null;
        try {
                File globalFile = getGlobalConfigFile();
                IExternalFinderItemLoader userItemLoader = 
                        new ExternalFinderXMLLoader(globalFile, SCOPE.GLOBAL);
                efc = userItemLoader.load();
        } catch (FileNotFoundException e) {
            // Ignore
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        if (efc == null) {
            efc = ExternalFinderConfiguration.empty();
        }
        return efc;
    }
    
    private static File getGlobalConfigFile() {
        String configDir = StaticUtils.getConfigDir();
        return new File(configDir, FINDER_FILE);
    }

    private static ExternalFinderConfiguration PROJECT_CONFIG;

    /**
     * Get the project-specific configuration.
     * 
     * @return The configuration, or null if no project is loaded or the project
     *         has no config file
     */
    public static ExternalFinderConfiguration getProjectConfig() {
        IProject currentProject = Core.getProject();
        if (!currentProject.isProjectLoaded()) {
            return null;
        }
        if (PROJECT_CONFIG == null) {
            // load project's xml file
            File projectFile = getProjectFile(currentProject);
            IExternalFinderItemLoader projectItemLoader = new ExternalFinderXMLLoader(projectFile, SCOPE.PROJECT);
            try {
                PROJECT_CONFIG = projectItemLoader.load();
            } catch (FileNotFoundException e) {
                // Ignore
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return PROJECT_CONFIG;
    }

    /**
     * Set the project-specific configuration. Has no effect if no project is
     * loaded. Any existing configuration file will be overwritten with the new
     * one. Pass null to delete the config file.
     * @param newConfig New project configuration to set and overwrite 
     */
    public static void setProjectConfig(ExternalFinderConfiguration newConfig) {
        IProject currentProject = Core.getProject();
        if (!currentProject.isProjectLoaded()) {
            return;
        }
        ExternalFinderConfiguration oldConfig = PROJECT_CONFIG;
        PROJECT_CONFIG = newConfig;
        if (!Objects.equals(newConfig, oldConfig)) {
            File projectFile = getProjectFile(currentProject);
            writeConfig(newConfig, projectFile);
        }
    }

    private static void writeConfig(ExternalFinderConfiguration config, File toFile) {
        if (config == null) {
            boolean deleted = toFile.delete();
            if (!deleted) {
                LOGGER.log(Level.SEVERE, "Unable to delete ExternalFinder config file: {0}", toFile);
            }
        } else {
            try {
                File tmpFile = File.createTempFile("omt", "externalfinder");
                ExternalFinderXMLWriter writer = new ExternalFinderXMLWriter(tmpFile);
                writer.write(config);
                Files.move(tmpFile.toPath(), toFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    /**
     * Get the project-specific config file. In the original plugin this was
     * stored in the project root ({@link ProjectProperties#getProjectRoot()});
     * now it's in the <code>omegat</code> directory for consistency with other
     * project-specific config files.
     */
    private static File getProjectFile(IProject project) {
        ProjectProperties projectProperties = project.getProjectProperties();
        File projectRoot = projectProperties.getProjectInternalDir();
        return new File(projectRoot, FINDER_FILE);
    }

    /**
     * Get the list of Configuration ExternalFinderItems from the combination of
     * the project and global configuration items. Duplicate items from the 
     * global config is replaced by project configurations
     * @return Unmodifiable list of External finder items all config items 
     */
    public static List<ExternalFinderItem> getItems() {
        // replace duplicated items based on name
        List<ExternalFinderItem> result = new ArrayList<>(getGlobalConfig().getItems());
        ExternalFinderConfiguration projectConfig = getProjectConfig();
        if (projectConfig != null) {
            projectConfig.getItems().forEach(item -> addOrReplaceByName(result, item));
        }
        return Collections.unmodifiableList(result);
    }

    static void addOrReplaceByName(List<ExternalFinderItem> items, ExternalFinderItem item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().equals(item.getName())) {
                items.set(i, item);
                return;
            }
        }
        items.add(item);
    }
}
