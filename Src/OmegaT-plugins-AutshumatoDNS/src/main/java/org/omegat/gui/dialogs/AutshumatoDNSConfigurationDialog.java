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

import com.ctext.omegat.loader.Settings;
import com.ctext.omegat.loader.ADNSStringHandler;
import org.omegat.util.FileNameUtils;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import java.text.ParseException;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import org.omegat.util.FieldType;

/**
 * Configuration dialog for the Autshumato Document Naming System(s).
 * 
 * @author Wildrich Fourie
 */
public class AutshumatoDNSConfigurationDialog extends javax.swing.JDialog
{
    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;
    private int returnStatus = RET_CANCEL;
    
    // Stores all of the specified fields
    private char separator;
    private ArrayList<DocumentNamingField> fields;
    private boolean renameTarget;
    
    // Default values
    private static final int COLUMN_NUMBER_WIDTH = 25;
    
    // Column Numbers
    private static final int COL_NUM = 0;       // Number
    private static final int COL_FIELD = 1;     // Field Name
    private static final int COL_DEFAULT = 2;   // Default Value
    private static final int COL_TYPE = 3;      // Type of the field (Date, Title, etc.)
    
    
    /** 
     * Creates new form AutshumatoMTConfigurationDialog 
     * @param parent Parent frame
     */
    public AutshumatoDNSConfigurationDialog(Frame parent)
    {
        super(parent, true);

        initKeys();

        initComponents();
        initTable();
        this.pack();
        
        // Additional settings
        DefaultFormatterFactory dff;
        try 
        {
            dff = new DefaultFormatterFactory(new MaskFormatter("*"));
            tfSeparator.setFormatterFactory(dff);
        } catch (ParseException ex) {}
        tfSeparator.setHorizontalAlignment(JTextField.CENTER);
        
        
        NumberColoumnRenderer TR = new NumberColoumnRenderer();
        jTable.setDefaultRenderer(jTable.getColumnClass(COL_NUM), TR);
        jTable.setDefaultRenderer(jTable.getColumnClass(COL_FIELD), TR);
        jTable.setDefaultRenderer(jTable.getColumnClass(COL_DEFAULT), TR);
        jTable.getColumnModel().getColumn(0).setPreferredWidth(COLUMN_NUMBER_WIDTH);
        jTable.getColumnModel().getColumn(0).setMaxWidth(COLUMN_NUMBER_WIDTH);
        
        
        // Load the comboboxes
        String[] items = FieldType.getValuesStrings();
        jTable.getColumnModel().getColumn(COL_TYPE).setCellEditor(new ComboBoxEditor(items));
        
        // Handlers
        tfSeparator.addFocusListener(new FocusListener() 
        {
            @Override
            public void focusGained(FocusEvent e) 
            {
                tfSeparator.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) 
            {
                validateSeparator();
            }
        });
        
        
        // Doesn't restore the default value if illegal character entered.
        // - Might use the fields entity to restore the original value
        // Handler to validate each cell after it has been edited.
        jTable.getDefaultEditor(String.class).addCellEditorListener(new CellEditorListener() 
        {
            @Override
            public void editingStopped(ChangeEvent e) 
            {
                // Only check for the Default values
                if(jTable.getSelectedColumn() == COL_DEFAULT)
                {
                    Object cell = jTable.getValueAt(jTable.getSelectedRow(), COL_DEFAULT);
                    if(cell != null
                       &&
                       !cell.toString().trim().isEmpty())
                    {
                        String enteredValue = cell.toString().trim();
                        
                        // Attempt to restore previous value
                        // It will require a new type to hold the info recently entered and not yet saved.
                        //String originalValue = fields.get(jTable.getSelectedRow()).getDefaultValue();
                        //System.out.println("EnteredValue: " + enteredValue);
                        //System.out.println("PreviousValue: " + originalValue);
                        
                        if(FileNameUtils.containsIllegalCharacters(enteredValue))
                        {
                            JOptionPane.showMessageDialog(rootPane, 
                                    ADNSStringHandler.getString("ADNS_CONFIG_ERROR_DEFAULT_DM"),
                                    ADNSStringHandler.getString("ADNS_CONFIG_ERROR_DEFAULT_DT"), 
                                    JOptionPane.WARNING_MESSAGE);
                        
                            jTable.setValueAt("", jTable.getSelectedRow(), COL_DEFAULT);
                        }
                        else if(enteredValue.contains(tfSeparator.getText().trim()))
                        {
                            JOptionPane.showMessageDialog(rootPane, 
                                    ADNSStringHandler.getString("ADNS_CONFIG_ERROR_DEFISSEP_DM"),
                                    ADNSStringHandler.getString("ADNS_CONFIG_ERROR_DEFAULT_DT"), 
                                    JOptionPane.WARNING_MESSAGE);
                        
                            jTable.setValueAt("", jTable.getSelectedRow(), COL_DEFAULT);
                        }
                    }
                }
                else if(jTable.getSelectedColumn() == COL_TYPE)
                {
                    Object cell = jTable.getValueAt(jTable.getSelectedRow(), COL_TYPE);
                    if(cell != null)
                    {
                        String selectedValue = cell.toString();
                        FieldType selectedType = FieldType.convertStringToType(selectedValue);
                        
                        if(selectedType == FieldType.STRING || selectedType == FieldType.COUNTER)
                        {
                            // [LATER] Enable the cell for editing
                            
                        }
                        else if(selectedType == FieldType.DATE || 
                                selectedType == FieldType.LANGUAGE || 
                                selectedType == FieldType.TITLE)
                        {
                            // For now just ignore default values
                            // [LATER] Disable the field for editing
                        }
                    }
                }
            }

            @Override
            public void editingCanceled(ChangeEvent e) {}
        });

        loadSettings();
    }
    
    
    // Attach key handlers for the Enter and Escape keys
    /**
     * Taken from OmegaT (org.omegat.gui.dialogs.AboutDialog.java)
     * Original Authors:
     * @author Maxym Mykhalchuk
     * @author Henry Pijffers (henry.pijffers@saxnot.com)
     * 
     * Modified by Wildrich Fourie
     */
    private void initKeys()
    {
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
        
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        Action enterAction = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                shiftTableRows();
                if(validateSeparator() && readTableData())
                {
                    saveSettings();
                    doClose();
                }
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enter, "ENTER");
        getRootPane().getActionMap().put("ENTER", enterAction);
    }

    
    // Loads the settings for this dialog, if no settings were found init the defaults.
    private void loadSettings()
    {
        Settings.readSettings();
        this.separator = Settings.getSeparator();
        this.fields = Settings.getFields();
        this.renameTarget = Settings.isRenameTarget();
        
        tfSeparator.setText(String.valueOf(this.separator));
        cbRenameTrgDoc.setSelected(this.renameTarget);
        
        for(int i=0; i < this.fields.size(); i++)
        {
            DocumentNamingField dnf = this.fields.get(i);
            jTable.setValueAt(dnf.getFieldName(), i, COL_FIELD);
            jTable.setValueAt(dnf.getDefaultValue(), i, COL_DEFAULT);
            jTable.setValueAt(dnf.getFieldTypeString(), i, COL_TYPE);
        }
    }

    
    // Save the settings entered into the dialog
    private void saveSettings()
    {
        // When reaching here the readTableData() has already been executed and
        // the files have been populated with the DocumentNamingFields.
        this.separator = tfSeparator.getText().trim().toCharArray()[0];
        this.renameTarget = cbRenameTrgDoc.isSelected();  
        Settings.writeSettings(separator, fields, renameTarget);
    }
    
    
    // Checks that tfSeparator contains a character and that it is a valid one
    // If the character is invalid then revert to the default value
    private boolean validateSeparator()
    {
        String sep = tfSeparator.getText().trim();
        if(!sep.isEmpty())
        {
            boolean valid = false;
            
            if(!FileNameUtils.containsAlphaNumericCharacters(sep) && !FileNameUtils.containsIllegalCharacters(sep))
                valid = true;
            
            // If the character is valid then return
            if(valid)
                return true;
            else
                // Warning Message that the separator is invalid
                JOptionPane.showMessageDialog(rootPane, 
                    ADNSStringHandler.getString("ADNS_CONFIG_ERROR_SEPARATOR_DM"), 
                    ADNSStringHandler.getString("ADNS_CONFIG_ERROR_SEPARATOR_DT"), 
                    JOptionPane.WARNING_MESSAGE);
        }
        else
        {
            // Warning Message that the field is empty.
            JOptionPane.showMessageDialog(rootPane, 
                    ADNSStringHandler.getString("ADNS_CONFIG_ERROR_SEPARATOR_EMPTY_DM"), 
                    ADNSStringHandler.getString("ADNS_CONFIG_ERROR_SEPARATOR_EMPTY_DT"), 
                    JOptionPane.WARNING_MESSAGE);
        }
        
        // Revert to default character
        tfSeparator.setText(String.valueOf(this.separator));
        
        return false;
    }
    
    
    // Reads all the data from the table and saves it in the fields DocumentNamingField entity.
    private boolean readTableData()
    {
        int rowCount = jTable.getModel().getRowCount();
        ArrayList<DocumentNamingField> tempFields = new ArrayList<>();
        for(int i=0; i < rowCount; i++)
        {
            // Stop when there are no more data
            if(jTable.getValueAt(i, COL_FIELD) == null)
                break;
            
            Object comp1 = jTable.getValueAt(i, COL_NUM);
            int intVal = Integer.parseInt(comp1.toString());
        
            Object comp2 = jTable.getValueAt(i, COL_FIELD);
            String fieldVal = "";
            if(comp2 != null)
                fieldVal = comp2.toString();
        
            Object comp3 = jTable.getValueAt(i, COL_DEFAULT);
            String defaultVal = "";
            if(comp3 != null)
                defaultVal = comp3.toString();
            
            String fieldType = FieldType.convertTypeToString(FieldType.STRING);
            Object comp4 = jTable.getValueAt(i, COL_TYPE);
            if(comp4 != null)
                fieldType = jTable.getValueAt(i, COL_TYPE).toString();
        
            // Test for null or empty values and names.
            if(!fieldVal.isEmpty())
            {
                DocumentNamingField dnf = new DocumentNamingField(fieldVal, defaultVal, intVal, fieldType);
                tempFields.add(dnf);
            }
        }
        
        if(tempFields.size() > 0)
        {
            fields = tempFields;
            return true;
        }
        else
            JOptionPane.showMessageDialog(rootPane, 
                    ADNSStringHandler.getString("ADNS_CONFIG_ERROR_FIELD_DM"), 
                    ADNSStringHandler.getString("ADNS_CONFIG_ERROR_FIELD_DT"), 
                    JOptionPane.WARNING_MESSAGE);
        
        return false;
    }
    
    
    // Detetcts any empty rows and shifts them up
    private void shiftTableRows()
    {
        int rowCount = jTable.getModel().getRowCount();
        for(int i=0; i < rowCount; i++)
        {
            // Only get the Field names as they are the most important at this stage.
            Object comp1 = jTable.getValueAt(i, COL_FIELD);
            if(comp1 == null || comp1.toString().equals(""))
            {
                // Shift all one up
                for(int j=i+1; j < rowCount; j++)
                {
                    jTable.setValueAt(jTable.getValueAt(j, COL_FIELD), j-1, COL_FIELD);
                    jTable.setValueAt(jTable.getValueAt(j, COL_DEFAULT), j-1, COL_DEFAULT);
                    jTable.setValueAt(jTable.getValueAt(j, COL_TYPE), j-1, COL_TYPE);
                    jTable.setValueAt("", j, COL_FIELD);
                    jTable.setValueAt("", j, COL_DEFAULT);
                    jTable.setValueAt("", j, COL_TYPE);
                }
            }
        }
    }

    /**
     * 
     * @return 
     */
    /*public int getReturnStatus() 
    {
        return returnStatus;
    }*/

    private void doClose() 
    {
        setVisible(false);
        dispose();
    }
    
    private void initTable()
    {
        jTable.setModel(new DefaultTableModelImpl(
            new Object [][] {
                {1, null, null, ""},
                {2, null, null, null},
                {3, null, null, null},
                {4, null, null, null},
                {5, null, null, null},
                {6, null, null, null},
                {7, "", null, null},
                {8, "", null, null},
                {9, null, null, null},
                {10, null, null, null}
            },
            new String [] 
            {
                ADNSStringHandler.getString("ADNS_CONFIG_TABLE_NO"), 
                ADNSStringHandler.getString("ADNS_CONFIG_TABLE_FIELDNAME"), 
                ADNSStringHandler.getString("ADNS_CONFIG_TABLE_DEFAULTVALUE"), 
                ADNSStringHandler.getString("ADNS_CONFIG_TABLE_TYPE")
            }));
        jScrollPane1.setViewportView(jTable);
        jTable.getColumnModel().getColumn(0).setResizable(false);
        jTable.getColumnModel().getColumn(0).setHeaderValue(ADNSStringHandler.getString("ADNS_CONFIG_TABLE_NO"));
        jTable.getColumnModel().getColumn(1).setHeaderValue(ADNSStringHandler.getString("ADNS_CONFIG_TABLE_FIELDNAME"));
        jTable.getColumnModel().getColumn(2).setHeaderValue(ADNSStringHandler.getString("ADNS_CONFIG_TABLE_DEFAULTVALUE"));
        jTable.getColumnModel().getColumn(3).setHeaderValue(ADNSStringHandler.getString("ADNS_CONFIG_TABLE_TYPE"));
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        cbRenameTrgDoc = new javax.swing.JCheckBox();
        tfSeparator = new javax.swing.JFormattedTextField();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        taInfo = new javax.swing.JTextArea();
        jSplitPane1 = new javax.swing.JSplitPane();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/ctext/omegat/loader/ADNSStrings"); // NOI18N
        setTitle(bundle.getString("ADNS_CONFIG_TITLE")); // NOI18N
        setMinimumSize(new java.awt.Dimension(500, 400));
        setPreferredSize(new java.awt.Dimension(500, 400));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jTable.setModel(new DefaultTableModelImpl1(
            new Object [][] {
                { 1, null, null, ""},
                { 2, null, null, null},
                { 3, null, null, null},
                { 4, null, null, null},
                { 5, null, null, null},
                { 6, null, null, null},
                { 7, "", null, null},
                { 8, "", null, null},
                { 9, null, null, null},
                { 10, null, null, null}
            },
            new String [] {
                "No", "Field Name", "Default Value", "Type"
            }));
        jScrollPane1.setViewportView(jTable);
        if (jTable.getColumnModel().getColumnCount() > 0) {
            jTable.getColumnModel().getColumn(0).setResizable(false);
            jTable.getColumnModel().getColumn(0).setHeaderValue("No");
            jTable.getColumnModel().getColumn(1).setHeaderValue("Field Name");
            jTable.getColumnModel().getColumn(2).setHeaderValue("Default Value");
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 429;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 5);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, bundle.getString("ADNS_CONFIG_LABEL_SEPARATOR")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 0, 3);
        getContentPane().add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(cbRenameTrgDoc, bundle.getString("ADNS_CONFIG_LABEL_TARGET")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 10, 0, 0);
        getContentPane().add(cbRenameTrgDoc, gridBagConstraints);

        tfSeparator.setText(".");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        getContentPane().add(tfSeparator, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ADNS_CONFIG_LABEL_INFO"))); // NOI18N
        jPanel1.setLayout(new java.awt.BorderLayout());

        taInfo.setColumns(20);
        taInfo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        taInfo.setLineWrap(true);
        taInfo.setRows(5);
        taInfo.setText(bundle.getString("ADNS_CONFIG_INFO")); // NOI18N
        taInfo.setWrapStyleWord(true);
        taInfo.setEnabled(false);
        jScrollPane2.setViewportView(taInfo);

        jPanel1.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 15;
        gridBagConstraints.ipady = 61;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 5);
        getContentPane().add(jPanel1, gridBagConstraints);

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerSize(0);
        jSplitPane1.setResizeWeight(0.5);

        org.openide.awt.Mnemonics.setLocalizedText(cancelButton, bundle.getString("ADNS_CANCEL")); // NOI18N
        cancelButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            cancelButtonActionPerformed(evt);
        });
        jSplitPane1.setRightComponent(cancelButton);

        org.openide.awt.Mnemonics.setLocalizedText(okButton, bundle.getString("ADNS_OK")); // NOI18N
        okButton.setMaximumSize(new java.awt.Dimension(73, 23));
        okButton.setMinimumSize(new java.awt.Dimension(73, 23));
        okButton.setPreferredSize(new java.awt.Dimension(73, 23));
        okButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            okButtonActionPerformed(evt);
        });
        jSplitPane1.setLeftComponent(okButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 5);
        getContentPane().add(jSplitPane1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_okButtonActionPerformed
    {//GEN-HEADEREND:event_okButtonActionPerformed

        shiftTableRows();
        if(validateSeparator() && readTableData())
        {
            saveSettings();
            doClose();
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
    {//GEN-HEADEREND:event_cancelButtonActionPerformed
        doClose();
    }//GEN-LAST:event_cancelButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox cbRenameTrgDoc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable;
    private javax.swing.JButton okButton;
    private javax.swing.JTextArea taInfo;
    private javax.swing.JFormattedTextField tfSeparator;
    // End of variables declaration//GEN-END:variables

    

    // Custom table renderer for the numbers column
    private static class NumberColoumnRenderer extends DefaultTableCellRenderer
    {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
        {
            JLabel c = (JLabel)super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);
            
            c.setHorizontalAlignment(SwingConstants.CENTER);
            return c;
        }
    }
    
    // Enables the use of comboboxes on the table.
    private static class ComboBoxEditor extends DefaultCellEditor
    {
        //private JComboBox jComboBox;
        public ComboBoxEditor(String[] items)
        {
            super(new JComboBox(items));
        }
    }

    private static class DefaultTableModelImpl extends DefaultTableModel {

        public DefaultTableModelImpl(Object[][] data, Object[] columnNames) {
            super(data, columnNames);
        }
        Class[] types = new Class []
        {
            java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
        };
        boolean[] canEdit = new boolean []
        {
            false, true, true, true
        };

        @Override
        public Class getColumnClass(int columnIndex)
        {
            return types [columnIndex];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            return canEdit [columnIndex];
        }
    }

    private static class DefaultTableModelImpl1 extends DefaultTableModel {

        public DefaultTableModelImpl1(Object[][] data, Object[] columnNames) {
            super(data, columnNames);
        }
        Class[] types = new Class [] {
            java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
        };
        boolean[] canEdit = new boolean [] {
            false, true, true, true
        };

        @Override
        public Class getColumnClass(int columnIndex) {
            return types [columnIndex];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    }
}