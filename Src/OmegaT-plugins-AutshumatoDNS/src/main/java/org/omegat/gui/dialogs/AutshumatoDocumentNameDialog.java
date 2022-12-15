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

import com.ctext.omegat.loader.ADNSStringHandler;
import org.omegat.util.FileNameUtils;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import org.apache.commons.io.FilenameUtils;
import org.omegat.core.Core;
import org.omegat.util.FieldType;

/**
 * AutshumatoDocumentNameDialog.
 * This is where the field values for the document name is specified.
 * It is populated with the fields specified on the AutshumatoDNSConfigurationDialog
 * as well as the default values entered. Any of the values can now be changed.
 * 
 * @author Wildrich Fourie
 */
public final class AutshumatoDocumentNameDialog extends javax.swing.JDialog
{
    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;
    private int returnStatus = RET_CANCEL;
    private int counter_counter = 0;
    private int dialog_call_counter = 0; // Might not be needed? Check on cleanup.
    
    private char seperator;
    private ArrayList<JTextField> valueFields;
    private String extension;
    
    // Holds the counters
    private static final String COUNTER_STRING = "COUNTER";
    private static final HashMap<String, Integer> COUNTERS = new HashMap<String, Integer>();
    
    /**
     * Creates a new AutshumatoDocumentNameDialog. 
     * This is where the field values for the document name is specified.
     * It is populated with the fields specified on the AutshumatoDNSConfigurationDialog
     * as well as the default values entered. Any of the values can now be changed.
     * @param parent The parent frame of this dialog.
     * @param modal Display the dialog modal.
     */
    public AutshumatoDocumentNameDialog(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
        
        initKeys();
    }
    
    /**
     * Reset the counter value for the version number of the files
     * @param value 
     */
    public void resetCounter(int value)
    {
        this.counter_counter = value;
    }
    
    /**
     * Reset the dialogue call counter
     */
    public void resetCallCounter()
    {
        this.dialog_call_counter = 0;
        COUNTERS.clear();
    }

    
    /**
     * Loads all the fields and lays out the dialog.
     * @param fields The fields specified as part of the document naming convention
     * @param separator The separator character between field elements
     * @param documentName The current document name
     * @param sourceDocuments Indicates if the documents are source documents
     */
    public void initFieldsAndDefaults(ArrayList<DocumentNamingField> fields, 
            char separator, String documentName, boolean sourceDocuments)
    {
        this.seperator = separator;
        
        panelFields.removeAll();
        
        GridLayout gridLayout = new GridLayout(fields.size() + 2, 3);
        gridLayout.setHgap(5);
        gridLayout.setVgap(5);
        panelFields.setLayout(gridLayout);
        
        // Labels
        JLabel titleNum = new JLabel(ADNSStringHandler.getString("ADNS_RENAME_FIELDS_NUM"));
        JLabel titleName = new JLabel(ADNSStringHandler.getString("ADNS_RENAME_FIELDS_NAME"));
        JLabel titleVal = new JLabel(ADNSStringHandler.getString("ADNS_RENAME_FIELDS_VALUE"));
        panelFields.add(titleNum);
        panelFields.add(titleName);
        panelFields.add(titleVal);
        
        
        //detectNameAdheresToConvention(fields, documentName, separator);

        valueFields = new ArrayList<>();
        for(DocumentNamingField dnf : fields)
        {
            if(dnf.getFieldName().equals(""))
                continue;
            
            JLabel numberLabel = new JLabel("" + dnf.getOrder() + ":");
            JLabel fieldLabel = new JLabel(dnf.getFieldName());
            
            String value;
            if(null == dnf.getFieldType())
            {
                value = dnf.getDefaultValue();
            }
            else switch (dnf.getFieldType()) {
                case COUNTER:
                    if(COUNTERS.containsKey(COUNTER_STRING + counter_counter))
                    {
                        COUNTERS.put(COUNTER_STRING + counter_counter, 
                                COUNTERS.get(COUNTER_STRING + 
                                        counter_counter) + 1);
                    }
                    else
                    {
                        int counterDefaultValue = 0;
                        if(!dnf.getDefaultValue().equals(""))
                            counterDefaultValue = Integer.parseInt(dnf.getDefaultValue());
                        COUNTERS.put(COUNTER_STRING + counter_counter, counterDefaultValue);
                    }
                    value = COUNTERS.get(COUNTER_STRING + counter_counter).toString();
                    this.counter_counter++;
                    break;
                case DATE:
                    //[LATER] Add a way to specify the date format
                    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    value = LocalDate.now().toString();//sdf.format(LocalDate.now());
                    break;
                case LANGUAGE:
                    if(sourceDocuments)
                        value = Core.getProject().getProjectProperties().getSourceLanguage().toString();
                    else
                        value = Core.getProject().getProjectProperties().getTargetLanguage().toString();
                    break;
                case TITLE:
                    value = FilenameUtils.removeExtension(documentName);
                    break;
                case STRING:
                    value = dnf.getDefaultValue();
                    break;
                default:
                    value = dnf.getDefaultValue();
                    break;
            }
            
            final JTextField valueField = new JTextField(value);
            valueField.setName(value);
            valueField.addFocusListener(new FocusListener() 
            {
                @Override
                public void focusGained(FocusEvent e)
                {
                    valueField.selectAll();
                }

                @Override
                public void focusLost(FocusEvent e)
                {
                    // Validate for illegal characters and the seperator
                    if(valueField.getText().trim().contains(String.valueOf(seperator)))
                    {
                        JOptionPane.showMessageDialog(rootPane, 
                                    ADNSStringHandler.getString("ADNS_RENAME_ERROR_SEP_DM"),
                                    ADNSStringHandler.getString("ADNS_RENAME_ERROR_VALUE_DT"), 
                                    JOptionPane.WARNING_MESSAGE);
                        valueField.setText(valueField.getName());
                    }
                    else if(FileNameUtils.containsIllegalCharacters(valueField.getText().trim()))
                    {
                        JOptionPane.showMessageDialog(rootPane, 
                                    ADNSStringHandler.getString("ADNS_RENAME_ERROR_VALUE_DM"),
                                    ADNSStringHandler.getString("ADNS_RENAME_ERROR_VALUE_DT"), 
                                    JOptionPane.WARNING_MESSAGE);
                        valueField.setText(valueField.getName());
                    }
                    
                    updateNewDocumentNameLabel(getNewDocumentName());
                }
            });
            valueFields.add(valueField);
            
            panelFields.add(numberLabel);
            panelFields.add(fieldLabel);
            panelFields.add(valueField);
        }
        
        valueCurDocName.setText(documentName);
        valueSeparator.setText(String.valueOf(separator));
        // Get the extension
        extension = FilenameUtils.getExtension(documentName);
        updateNewDocumentNameLabel(getNewDocumentName());
        
        pack();
        
        this.dialog_call_counter++;
        System.out.println("Dialog Call Counter: " + this.dialog_call_counter);
        
        detectValidFields(fields, separator, 
                FilenameUtils.removeExtension(documentName));
    }
    
    
    // Initialises the Enter and Escape keyboard keys.
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
                doClose(RET_CANCEL);
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
                doClose(RET_OK);
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enter, "ENTER");
        getRootPane().getActionMap().put("ENTER", enterAction);
    }
    
    
    // First partial attempt to detect if the document already adheres to the specified naming convention.
    // TODO: Finish name document adherence
    private void detectNameAdheresToConvention(ArrayList<DocumentNamingField> fields, String documentName, char sep) 
    {
        if(documentName.contains(String.valueOf(sep)))
        {
            System.out.println("Name contains seperator");
            String[] nameFields = documentName.split(String.valueOf(sep));
            System.out.println("Number of fields in name: " + nameFields.length);
            System.out.println("Number of field configured: " + fields.size());
        }
        else
        {
            
        }
    }
    
    
    /** 
     * Generates the new document name. 
     * @return The new document name
     */
    public String getNewDocumentName()
    {
        StringBuilder nameString = new StringBuilder();
        for(JTextField tf : valueFields) {
            nameString.append(tf.getText().trim());
            nameString.append(this.seperator);
        }        
        nameString.append(extension);
        
        return nameString.toString();
    }
    
    
    // Updates the valueNewDocName label.
    private void updateNewDocumentNameLabel(String newDocumentName)
    {
        valueNewDocName.setText(newDocumentName);
    }    
    
    // Check if the file name has the same amount of fields
    // and the same separator as specified in the Config dialog.
    // When this is all found to be true, insert the values into the valueFields.
    private void detectValidFields(ArrayList<DocumentNamingField> fields, 
                                    char separator, String documentName)
    {        
        // Test for reg ex special chars and insert escape chars
        String separatorString = String.valueOf(separator);
        if(separatorString.equals(".") || 
                separatorString.equals("+") ||
                separatorString.equals("?") ||
                separatorString.equals("^") ||
                separatorString.equals("*"))
            separatorString = "\\" + separatorString;
        
        if(documentName.contains(String.valueOf(separator)))
        {
            String[] nameFields = documentName.split(separatorString);
            if(nameFields.length == fields.size())
            {
                for(int i=0; i < nameFields.length; i++)
                {
                    if (fields.get(i).getFieldType() != FieldType.LANGUAGE) {
                        valueFields.get(i).setText(nameFields[i]);
                    }
                }
            }
        }
    }
    
    
    /**
     * Get the status of whether the renaming completed successfully
     * @return 0 if cancelled, 1 if OK
     */
    public int getReturnStatus() 
    {
        return returnStatus;
    }
    
    private void doClose(int retStatus) 
    {
        returnStatus = retStatus;
        setVisible(false);
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        butOk = new javax.swing.JButton();
        panelInformation = new javax.swing.JPanel();
        lblCurDocName = new javax.swing.JLabel();
        lblSep = new javax.swing.JLabel();
        lblNewDocName = new javax.swing.JLabel();
        valueCurDocName = new javax.swing.JLabel();
        valueSeparator = new javax.swing.JLabel();
        valueNewDocName = new javax.swing.JLabel();
        panelFields = new javax.swing.JPanel();
        butCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/ctext/omegat/loader/ADNSStrings"); // NOI18N
        setTitle(bundle.getString("ADNS_RENAME_TITLE")); // NOI18N

        butOk.setText(bundle.getString("ADNS_OK")); // NOI18N
        butOk.addActionListener((java.awt.event.ActionEvent evt) -> {
            butOkActionPerformed(evt);
        });

        panelInformation.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ADNS_RENAME_INFORMATION"))); // NOI18N

        lblCurDocName.setText(bundle.getString("ADNS_RENAME_CURNAME")); // NOI18N

        lblSep.setText(bundle.getString("ADNS_CONFIG_LABEL_SEPARATOR")); // NOI18N

        lblNewDocName.setText(bundle.getString("ADNS_RENAME_NEWNAME")); // NOI18N

        valueCurDocName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        valueCurDocName.setText("Current Document Name");

        valueSeparator.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        valueSeparator.setText(".");

        valueNewDocName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        valueNewDocName.setText("New Document Name");

        javax.swing.GroupLayout panelInformationLayout = new javax.swing.GroupLayout(panelInformation);
        panelInformation.setLayout(panelInformationLayout);
        panelInformationLayout.setHorizontalGroup(
            panelInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInformationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInformationLayout.createSequentialGroup()
                        .addComponent(lblCurDocName)
                        .addGap(18, 18, 18)
                        .addComponent(valueCurDocName, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE))
                    .addGroup(panelInformationLayout.createSequentialGroup()
                        .addGroup(panelInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSep)
                            .addComponent(lblNewDocName))
                        .addGap(34, 34, 34)
                        .addGroup(panelInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(valueNewDocName, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                            .addComponent(valueSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE))))
                .addContainerGap())
        );
        panelInformationLayout.setVerticalGroup(
            panelInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInformationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCurDocName)
                    .addComponent(valueCurDocName))
                .addGap(18, 18, 18)
                .addGroup(panelInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSep)
                    .addComponent(valueSeparator))
                .addGap(18, 18, 18)
                .addGroup(panelInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNewDocName)
                    .addComponent(valueNewDocName))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        panelFields.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ADNS_RENAME_FIELDS"))); // NOI18N

        javax.swing.GroupLayout panelFieldsLayout = new javax.swing.GroupLayout(panelFields);
        panelFields.setLayout(panelFieldsLayout);
        panelFieldsLayout.setHorizontalGroup(
            panelFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
        );
        panelFieldsLayout.setVerticalGroup(
            panelFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 140, Short.MAX_VALUE)
        );

        butCancel.setText(bundle.getString("ADNS_CANCEL")); // NOI18N
        butCancel.addActionListener((java.awt.event.ActionEvent evt) -> {
            butCancelActionPerformed(evt);
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelFields, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelInformation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(butOk)
                        .addGap(5, 5, 5)
                        .addComponent(butCancel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelInformation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelFields, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butOk)
                    .addComponent(butCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void butOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butOkActionPerformed

    doClose(RET_OK);
    
}//GEN-LAST:event_butOkActionPerformed

private void butCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCancelActionPerformed
    
    doClose(RET_CANCEL);
    
}//GEN-LAST:event_butCancelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butCancel;
    private javax.swing.JButton butOk;
    private javax.swing.JLabel lblCurDocName;
    private javax.swing.JLabel lblNewDocName;
    private javax.swing.JLabel lblSep;
    private javax.swing.JPanel panelFields;
    private javax.swing.JPanel panelInformation;
    private javax.swing.JLabel valueCurDocName;
    private javax.swing.JLabel valueNewDocName;
    private javax.swing.JLabel valueSeparator;
    // End of variables declaration//GEN-END:variables

    
}
