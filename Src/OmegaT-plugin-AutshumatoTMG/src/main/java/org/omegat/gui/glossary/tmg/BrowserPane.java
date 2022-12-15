package org.omegat.gui.glossary.tmg;

import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.docking.RelativeDockablePosition;
import org.omegat.core.Core;
import org.omegat.gui.main.IMainWindow;
import org.omegat.gui.main.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Based on code for omegat-browser plugin
 * <code>https://github.com/yoursdearboy/omegat-browser</code> by
 * @author Kirill Voronin (yoursdearboy@gmail.com)
 * 
 * <p>Modified by Wildrich Fourie and Roald Eiselen
 * 
 */
// FIXME: Don't cast IMainWindow to MainWindow to get DockingDesktop (don't use it)
@SuppressWarnings("serial")
class BrowserPane extends JPanel {
    private static Map<String,BrowserPane> panes = new HashMap<String, BrowserPane>();
    private IMainWindow mainWindow;
    private DockablePanel pane;
    private Browser browser;
    
    private static final String TMG_URL = "https://tmg.nwu.ac.za/";

    /**
     * Constructor
     * @param mainWindow the main window where the browser should be placed
     * @param key The key value for the BrowserPane
     * @param title The title to be displayed in the header of the pane
     */
    BrowserPane(final IMainWindow mainWindow, String key, String title) {
        this(mainWindow, key, title, TMG_URL);
    }

    /**
     * Constructor
     * @param mainWindow the main window where the browser should be placed
     * @param key The key value for the BrowserPane
     * @param title The title to be displayed in the header of the pane
     * @param domain The domain for the TMG
     */
    BrowserPane(final IMainWindow mainWindow, String key, String title, String domain) {
        super(new BorderLayout());

        this.mainWindow = mainWindow;
        this.pane = new DockablePanel(key, title, this, true);

        final DockingDesktop desktop = getDockingDesktop((MainWindow) mainWindow);
        desktop.addHiddenDockable(pane, RelativeDockablePosition.BOTTOM_LEFT);

        this.browser = new Browser(domain);
        super.add(browser, BorderLayout.CENTER);
        super.setVisible(true);
    }

    // FIXME: Close panes instead of hiding
    // Dockable can't be closed because of event listener that cancels it (see MainWinodwUI#initDocking)
    // Also, probably it can't be done using vldocking public API
    // But, it seems that DockingDesktop#remove at least allows to hide pane
    public void close() {
        DockingDesktop desktop = getDockingDesktop((MainWindow) mainWindow);
        desktop.unregisterDockable(pane);
        desktop.remove((Dockable) pane); // this don't allow it to get in layout config
    }

    // Since we can't close pane, let's reuse it
    public static BrowserPane get(String key, String title, String domain) {
        BrowserPane pane = panes.get(key);
        if (pane == null) {
            pane = new BrowserPane(Core.getMainWindow(), key, title, domain);
            panes.put(key, pane);
        } else {
            DockingDesktop desktop = getDockingDesktop((MainWindow) Core.getMainWindow());
            desktop.addHiddenDockable(pane.pane, RelativeDockablePosition.BOTTOM_LEFT);
        }
        return pane;
    }

    private static DockingDesktop getDockingDesktop(MainWindow mainWindow) {
        DockingDesktop desktop = null;
        for (Component component : mainWindow.getContentPane().getComponents()) {
            if (component instanceof DockingDesktop) {
                desktop = (DockingDesktop) component;
                break;
            }
        }
        if (desktop == null) throw new RuntimeException("Can't find DockingDesktop to register Dockable panel");
        return desktop;
    }
}
