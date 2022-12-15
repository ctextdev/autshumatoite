package org.omegat.gui.glossary.tmg;

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockingConstants;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * Based on code for omegat-browser plugin
 * <code>https://github.com/yoursdearboy/omegat-browser</code> by
 * @author Kirill Voronin (yoursdearboy@gmail.com)
 * 
 * <p>Modified by Wildrich Fourie and Roald Eiselen
 * 
 */
/**
 * Absolute analogue (except scroll!) of org.omegat.gui.main.DockableScrollPane
 */
@SuppressWarnings("serial")
public class DockablePanel extends JPanel implements Dockable {
    private final DockKey dockKey;

    /**
     * Set the tooltip text to display on hover for dockKey
     * @param text Tooltip text
     */
    @Override
    public void setToolTipText(String text) {
        this.dockKey.setTooltip(text);
    }

    /**
     * Set the name of the dockable panel
     * @param name Name text
     */
    @Override
    public void setName(String name) {
        this.dockKey.setName(name);
    }

    /**
     * Return the key value of the dockable panel
     * @return DockKey
     */
    @Override
    public DockKey getDockKey() {
        return this.dockKey;
    }

    /**
     * Return an instance of this dockable panel
     * @return this instances
     */
    @Override
    public Component getComponent() {
        return this;
    }
    
    DockablePanel(String key, String name, Component view, boolean detouchable) {
        super(new BorderLayout());

        super.add(view);

        if(view instanceof JTextComponent && UIManager.getBoolean("OmegaTDockablePanel.isProportionalMargins")) {
            JTextComponent panelBorder = (JTextComponent)view;
            int viewportBorder = panelBorder.getFont().getSize() / 2;
            panelBorder.setBorder(new EmptyBorder(viewportBorder, viewportBorder, viewportBorder, viewportBorder));
        }

        Border panelBorder1 = UIManager.getBorder("OmegaTDockablePanel.border");
        if(panelBorder1 != null) {
            super.setBorder(panelBorder1);
        }

        this.dockKey = new DockKey(key, name, null, null, DockingConstants.HIDE_BOTTOM);
        this.dockKey.setFloatEnabled(detouchable);
        this.dockKey.setCloseEnabled(false);
    }
}
