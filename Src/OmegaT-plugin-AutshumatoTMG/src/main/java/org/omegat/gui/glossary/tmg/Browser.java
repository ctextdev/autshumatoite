package org.omegat.gui.glossary.tmg;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;
import org.omegat.core.Core;
import org.omegat.core.data.IProject;
import org.omegat.core.data.NotLoadedProject;
import org.omegat.util.Log;


/**
 * Based on code for omegat-browser plugin
 * <code>https://github.com/yoursdearboy/omegat-browser</code> by
 * @author Kirill Voronin (yoursdearboy@gmail.com)
 * 
 * <p>Modified by Wildrich Fourie and Roald Eiselen
 * 
 */
@SuppressWarnings("serial")
class Browser extends JFXPanel
{
    private WebView webView;

    Browser() {
        this(null);
    }

    Browser(final String domain) 
    {
        super.setLayout(new BorderLayout());
        Platform.runLater(() -> {
            this.webView = new WebView();
            this.processURLChange();
            this.setScene(new Scene(webView));
            this.loadURL(domain);
        });
    }

    public void loadURL(final String url) 
    {
        Platform.runLater(() -> {
            String tmp = toURL(url);
            if (tmp == null) {
                tmp = toURL("https://" + url);
            }
            this.webView.getEngine().load(tmp);
        });
    }

    private static String toURL(String str) 
    {
        try {
            return new URL(str).toExternalForm();
        } catch (MalformedURLException ignored) {
            return null;
        }
    }

    /**
     * Adds a listener to the webView Engine for URL changes
     * URLs that reference the SVN repo are downloads and should trigger
     * a SaveAsDialog for the user to save the tm/gloss to either a custom
     * location, or a location in the project directory
     */
    private void processURLChange() 
    {
        this.webView.getEngine().locationProperty().addListener(
                        (ObservableValue<? extends String> observable, 
                                final String oldValue, 
                                String newValue) -> {
            try {
                URI newUri = new URI(newValue);
                String newDomain = newUri.getHost();

                Log.log("newUri: "+ newUri);
                Log.log("newDomain: "+ newDomain);

                if (newUri.toString().contains("/svn/")) {

                    FileChooser chooser = new FileChooser();
                    chooser.setTitle("Save file");
                    
                    // If a project has been loaded, save the tm or gloss to
                    // project tm/gloss folder, otherwise, save to default location
                    File initial_dir = new File(System
                                                .getProperty("user.home"));
                    IProject project = Core.getProject();
                    if (project == null
                        ||
                        project instanceof NotLoadedProject) {
                       initial_dir = new File(System.getProperty("user.home")); 
                    }
                    else if (newUri.toString().contains("/glos/"))
                    {
                        String glossary_path = project.getProjectProperties()
                                         .getGlossaryRoot();
                        Log.log("Gloss path: "+ glossary_path);
                        initial_dir = new File(glossary_path);
                    }
                    else if (newUri.toString().contains("/tm/"))
                    {
                        String tm_path = project.getProjectProperties()
                                         .getTMRoot();
                        Log.log("Tm path: "+ tm_path);
                        initial_dir = new File(tm_path);
                    }

                    String full_path = newUri.toString();
                    String filename = full_path
                            .substring(full_path
                                    .lastIndexOf('/') + 1);
                    String decoded_fn = URLDecoder
                            .decode(filename, "UTF-8");
                    Log.log("Full path: "+ full_path);
                    Log.log("Filename: "+ filename);
                    Log.log("Decoded Filename: "+ decoded_fn);

                    chooser.setInitialDirectory(initial_dir);
                    chooser.setInitialFileName(decoded_fn);

                    File saveFile = chooser
                            .showSaveDialog(
                                webView.getScene().getWindow());

                    if (saveFile != null)
                    {
                        URL url;
                        String current_url = newUri.toString();

                        try{
                            url = new URL(current_url);
                            
                            FileUtils.copyURLToFile(url, saveFile);
                            // TODO: Show a dialogue if file sucessfully downloaded
                            
                            //TODO: Navigate back to previous page
                        } catch (IOException ex) {
                            Logger.getLogger(Browser.class
                                    .getName())
                                    .log(Level.SEVERE, null, ex);
                        }
                    }
                }
                else {
                    //webView.getEngine().load(newValue);
                }
            } catch (URISyntaxException ignored) {
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Browser.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        });
    }
}
