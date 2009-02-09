/*
 * frmMain.java
 *
 * Created on November 21, 2008, 9:56 AM
 */

/*
Copyright (c) 2008, George Blouin Jr. (skyhigh@solaris7.com)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of
conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list
of conditions and the following disclaimer in the documentation and/or other materials
provided with the distribution.
    * Neither the name of George Blouin Jr nor the names of contributors may be
used to endorse or promote products derived from this software without specific prior
written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package BFB.GUI;

import BFB.*;
import BFB.IO.XMLWriter;
import BFB.IO.XMLReader;
import BFB.Common.CommonTools;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author  justin
 */
public class frmMain extends javax.swing.JFrame {
    public Force leftForce = new Force();
    public Force rightForce = new Force();

    /** Creates new form frmMain */
    public frmMain() {
        initComponents();
        Load();
    }

    public void Load(){
        tblForce.setModel( leftForce );
        tblForce2.setModel ( rightForce );

        txtForceName.setText(leftForce.ForceName);
        txtForceName1.setText(rightForce.ForceName);
    }

    public void RefreshDisplay() {
        leftForce.RefreshBV();
        rightForce.RefreshBV();
        
        leftForce.fireTableDataChanged();
        rightForce.fireTableDataChanged();
        
        lblUnitCount.setText("Units: " + leftForce.Units.size());
        lblUnitCount1.setText("Units: " + rightForce.Units.size());

        lblTotalTonnage.setText("Tonnage: " + String.format( "%1$,.2f", leftForce.TotalTonnage));
        lblTotalTonnage1.setText("Tonnage: " + String.format( "%1$,.2f", rightForce.TotalTonnage));

        lblTotalBV.setText("Base BV: " + String.format("%1$,.0f", leftForce.TotalBaseBV));
        lblTotalBV1.setText("Base BV: " + String.format("%1$,.0f", rightForce.TotalBaseBV));

        lblC3BV.setText("C3 BV: " + String.format("%1$,.0f", leftForce.TotalC3BV));
        lblC3BV1.setText("C3 BV: " + String.format("%1$,.0f", rightForce.TotalC3BV));

        lblTotalModifierBV.setText("Mod BV: " + String.format("%1$,.0f", leftForce.TotalModifierBV));
        lblTotalModifierBV1.setText("Mod BV: " + String.format("%1$,.0f", rightForce.TotalModifierBV));

        lblForceBV.setText("Force BV: " + String.format( "%1$,.0f", leftForce.TotalForceBV));
        lblForceBV1.setText("Force BV: " + String.format( "%1$,.0f", rightForce.TotalForceBV));

        lblForceMult.setText("Force Multiplier: " + String.format( "%1$,.2f", CommonTools.GetForceSizeMultiplier(leftForce.Units.size(), rightForce.Units.size())));
        if (leftForce.Units.size() > rightForce.Units.size()) {
            lblForceMultiplierAdjusted.setText(String.format("Force BV Adjusted By Multiplier: %1$,.0f", leftForce.TotalForceBV * CommonTools.GetForceSizeMultiplier(leftForce.Units.size(), rightForce.Units.size())));
            lblForce1MultiplierAdjusted.setText(String.format("Force BV Adjusted By Multiplier: %1$,.0f", rightForce.TotalForceBV));
        } else if (rightForce.Units.size() > leftForce.Units.size()) {
            lblForce1MultiplierAdjusted.setText(String.format("Force BV Adjusted By Multiplier: %1$,.0f", rightForce.TotalForceBV * CommonTools.GetForceSizeMultiplier(leftForce.Units.size(), rightForce.Units.size())));
            lblForceMultiplierAdjusted.setText(String.format("Force BV Adjusted By Multiplier: %1$,.0f", leftForce.TotalForceBV));
        } else {
            lblForceMultiplierAdjusted.setText(String.format("Force BV Adjusted By Multiplier: %1$,.0f", leftForce.TotalForceBV));
            lblForce1MultiplierAdjusted.setText(String.format("Force BV Adjusted By Multiplier: %1$,.0f", rightForce.TotalForceBV));
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mnuPopUp = new javax.swing.JPopupMenu();
        mnuAdd = new javax.swing.JMenuItem();
        mnuEdit = new javax.swing.JMenuItem();
        mnuDelete = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblForce = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        btnAddUnit = new javax.swing.JButton();
        btnEditUnit = new javax.swing.JButton();
        btnRemoveUnit = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        lblForceName = new javax.swing.JLabel();
        txtForceName = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        btnAddUnit1 = new javax.swing.JButton();
        btnEditUnit1 = new javax.swing.JButton();
        btnRemoveUnit1 = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        lblForceName1 = new javax.swing.JLabel();
        txtForceName1 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblForce2 = new javax.swing.JTable();
        lblTotalBaseBV = new javax.swing.JPanel();
        lblUnitCount = new javax.swing.JLabel();
        lblTotalTonnage = new javax.swing.JLabel();
        lblTotalBV = new javax.swing.JLabel();
        lblC3BV = new javax.swing.JLabel();
        lblTotalModifierBV = new javax.swing.JLabel();
        lblForceBV = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lblUnitCount1 = new javax.swing.JLabel();
        lblTotalTonnage1 = new javax.swing.JLabel();
        lblTotalBV1 = new javax.swing.JLabel();
        lblC3BV1 = new javax.swing.JLabel();
        lblTotalModifierBV1 = new javax.swing.JLabel();
        lblForceBV1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        lblForceMultiplierAdjusted = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        lblForceMult = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        lblForce1MultiplierAdjusted = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        mnuSave = new javax.swing.JMenuItem();
        mnuLoad = new javax.swing.JMenuItem();
        mnuExit = new javax.swing.JMenuItem();
        mnuAbout = new javax.swing.JMenu();
        mnuAboutCalc = new javax.swing.JMenuItem();

        mnuAdd.setText("Add Unit");
        mnuAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddActionPerformed(evt);
            }
        });
        mnuPopUp.add(mnuAdd);

        mnuEdit.setText("Edit Unit");
        mnuEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEditActionPerformed(evt);
            }
        });
        mnuPopUp.add(mnuEdit);

        mnuDelete.setText("Delete Unit");
        mnuDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDeleteActionPerformed(evt);
            }
        });
        mnuPopUp.add(mnuDelete);

        mnuPopUp.getAccessibleContext().setAccessibleParent(jScrollPane1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(950, 350));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(400, 200));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(400, 200));
        jScrollPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane1MouseClicked(evt);
            }
        });

        tblForce.setAutoCreateRowSorter(true);
        tblForce.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblForce.setShowVerticalLines(false);
        tblForce.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblForceMouseClicked(evt);
            }
        });
        tblForce.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblForceKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tblForce);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(jScrollPane1, gridBagConstraints);

        jPanel1.setMaximumSize(new java.awt.Dimension(101, 110));
        jPanel1.setMinimumSize(new java.awt.Dimension(101, 30));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        btnAddUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/add2.png"))); // NOI18N
        btnAddUnit.setToolTipText("Add Unit");
        btnAddUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddUnitActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(btnAddUnit, gridBagConstraints);

        btnEditUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/edit.png"))); // NOI18N
        btnEditUnit.setToolTipText("Edit Unit");
        btnEditUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditUnitActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(btnEditUnit, gridBagConstraints);

        btnRemoveUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/delete2.png"))); // NOI18N
        btnRemoveUnit.setToolTipText("Delete Unit");
        btnRemoveUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveUnitActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(btnRemoveUnit, gridBagConstraints);

        jSeparator4.setForeground(java.awt.SystemColor.control);
        jSeparator4.setPreferredSize(new java.awt.Dimension(40, 1));
        jPanel1.add(jSeparator4, new java.awt.GridBagConstraints());

        lblForceName.setText("Force Name: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(lblForceName, gridBagConstraints);

        txtForceName.setPreferredSize(new java.awt.Dimension(200, 20));
        txtForceName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtForceNameFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(txtForceName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 1);
        getContentPane().add(jPanel1, gridBagConstraints);

        jPanel4.setMinimumSize(new java.awt.Dimension(101, 30));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        btnAddUnit1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/add2.png"))); // NOI18N
        btnAddUnit1.setToolTipText("Add Unit");
        btnAddUnit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddUnit1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel4.add(btnAddUnit1, gridBagConstraints);

        btnEditUnit1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/edit.png"))); // NOI18N
        btnEditUnit1.setToolTipText("Edit Unit");
        btnEditUnit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditUnit1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel4.add(btnEditUnit1, gridBagConstraints);

        btnRemoveUnit1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/delete2.png"))); // NOI18N
        btnRemoveUnit1.setToolTipText("Delete Unit");
        btnRemoveUnit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveUnit1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel4.add(btnRemoveUnit1, gridBagConstraints);

        jSeparator5.setForeground(new java.awt.Color(236, 233, 216));
        jSeparator5.setPreferredSize(new java.awt.Dimension(50, 1));
        jPanel4.add(jSeparator5, new java.awt.GridBagConstraints());

        lblForceName1.setText("Force Name: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel4.add(lblForceName1, gridBagConstraints);

        txtForceName1.setPreferredSize(new java.awt.Dimension(200, 20));
        txtForceName1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtForceName1FocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel4.add(txtForceName1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(jPanel4, gridBagConstraints);

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setMinimumSize(new java.awt.Dimension(400, 200));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(400, 200));
        jScrollPane2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane2MouseClicked(evt);
            }
        });

        tblForce2.setAutoCreateRowSorter(true);
        tblForce2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblForce2.setShowVerticalLines(false);
        tblForce2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblForce2MouseClicked(evt);
            }
        });
        tblForce2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblForce2KeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tblForce2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(jScrollPane2, gridBagConstraints);

        lblUnitCount.setText("Units: 0");
        lblTotalBaseBV.add(lblUnitCount);

        lblTotalTonnage.setText("Tonnage: 0");
        lblTotalTonnage.setFocusable(false);
        lblTotalBaseBV.add(lblTotalTonnage);

        lblTotalBV.setText("Base BV: 0");
        lblTotalBaseBV.add(lblTotalBV);

        lblC3BV.setText("C3 BV: 0");
        lblTotalBaseBV.add(lblC3BV);

        lblTotalModifierBV.setText("Mod BV: 0");
        lblTotalBaseBV.add(lblTotalModifierBV);

        lblForceBV.setText("Force BV: 0");
        lblForceBV.setFocusable(false);
        lblTotalBaseBV.add(lblForceBV);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(lblTotalBaseBV, gridBagConstraints);

        lblUnitCount1.setText("Units: 0");
        jPanel3.add(lblUnitCount1);

        lblTotalTonnage1.setText("Tonnage: 0");
        lblTotalTonnage1.setFocusable(false);
        jPanel3.add(lblTotalTonnage1);

        lblTotalBV1.setText("Base BV: 0");
        jPanel3.add(lblTotalBV1);

        lblC3BV1.setText("C3 BV: 0");
        jPanel3.add(lblC3BV1);

        lblTotalModifierBV1.setText("Mod BV: 0");
        jPanel3.add(lblTotalModifierBV1);

        lblForceBV1.setText("Force BV: 0");
        lblForceBV1.setFocusable(false);
        jPanel3.add(lblForceBV1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(jPanel3, gridBagConstraints);

        jSeparator1.setMaximumSize(new java.awt.Dimension(0, 2));
        jSeparator1.setMinimumSize(new java.awt.Dimension(0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.ipady = 4;
        getContentPane().add(jSeparator1, gridBagConstraints);

        lblForceMultiplierAdjusted.setText("Force BV Adjusted By Multipler: 0");
        jPanel2.add(lblForceMultiplierAdjusted);

        jSeparator3.setForeground(new java.awt.Color(236, 233, 216));
        jSeparator3.setMaximumSize(new java.awt.Dimension(200, 10));
        jSeparator3.setMinimumSize(new java.awt.Dimension(200, 10));
        jSeparator3.setPreferredSize(new java.awt.Dimension(100, 1));
        jPanel2.add(jSeparator3);

        lblForceMult.setText("Force Multiplier: 0");
        jPanel2.add(lblForceMult);

        jSeparator2.setForeground(new java.awt.Color(236, 233, 216));
        jSeparator2.setMaximumSize(new java.awt.Dimension(200, 10));
        jSeparator2.setMinimumSize(new java.awt.Dimension(200, 10));
        jSeparator2.setPreferredSize(new java.awt.Dimension(100, 1));
        jPanel2.add(jSeparator2);

        lblForce1MultiplierAdjusted.setText("Force BV Adjusted By Multipler: 0");
        jPanel2.add(lblForce1MultiplierAdjusted);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(jPanel2, gridBagConstraints);

        mnuFile.setText("File");

        mnuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        mnuSave.setText("Save");
        mnuSave.setName("mnuSave"); // NOI18N
        mnuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaveActionPerformed(evt);
            }
        });
        mnuFile.add(mnuSave);

        mnuLoad.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.ALT_MASK));
        mnuLoad.setText("Load");
        mnuLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLoadActionPerformed(evt);
            }
        });
        mnuFile.add(mnuLoad);

        mnuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK));
        mnuExit.setText("Exit");
        mnuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExitActionPerformed(evt);
            }
        });
        mnuFile.add(mnuExit);

        jMenuBar1.add(mnuFile);

        mnuAbout.setText("Help");

        mnuAboutCalc.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        mnuAboutCalc.setText("About");
        mnuAboutCalc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAboutCalcActionPerformed(evt);
            }
        });
        mnuAbout.add(mnuAboutCalc);

        jMenuBar1.add(mnuAbout);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void btnAddUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddUnitActionPerformed
    dlgAddEdit Editor = new dlgAddEdit( this, true );
    Editor.setLocationRelativeTo( this );
    Editor.setVisible( true );
    if( Editor.GetResult() ) {
        leftForce.AddUnit(Editor.GetUnit());
        RefreshDisplay();
    }
}//GEN-LAST:event_btnAddUnitActionPerformed

private void btnEditUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditUnitActionPerformed
    Unit u = (Unit) leftForce.Units.get( tblForce.getSelectedRow() );
    dlgAddEdit Editor = new dlgAddEdit( this, true, u );
    Editor.setLocationRelativeTo( this );
    Editor.setVisible( true );
    if( Editor.GetResult() ) {
        leftForce.AddUnit(Editor.GetUnit());
        RefreshDisplay();
    }
}//GEN-LAST:event_btnEditUnitActionPerformed

private void btnRemoveUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveUnitActionPerformed
    leftForce.RemoveUnit((Unit) leftForce.Units.get(tblForce.getSelectedRow()));
    RefreshDisplay();
}//GEN-LAST:event_btnRemoveUnitActionPerformed

private void tblForceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblForceMouseClicked
    if (evt.getClickCount() >= 2) { btnEditUnitActionPerformed(null); }
    if (evt.getButton() == MouseEvent.BUTTON3) {
        mnuPopUp.setLocation(evt.getLocationOnScreen());
        mnuPopUp.setInvoker(evt.getComponent());
        mnuEdit.setEnabled(true);
        mnuDelete.setEnabled(true);
        mnuPopUp.setVisible(true);
    }
}//GEN-LAST:event_tblForceMouseClicked

private void mnuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExitActionPerformed
    if ((leftForce.isDirty) || (rightForce.isDirty)) {
        switch (javax.swing.JOptionPane.showConfirmDialog(this, "Would you like to save your changes?")) {
            case javax.swing.JOptionPane.YES_OPTION:
                this.mnuSaveActionPerformed(evt);
            case javax.swing.JOptionPane.NO_OPTION:
                this.dispose();
        }
    } else {
        this.dispose();
    }
}//GEN-LAST:event_mnuExitActionPerformed

private void tblForce2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblForce2MouseClicked
    if (evt.getClickCount() >= 2) { btnEditUnit1ActionPerformed(null); }
    if (evt.getButton() == MouseEvent.BUTTON3) {
        mnuPopUp.setLocation(evt.getLocationOnScreen());
        mnuPopUp.setInvoker(evt.getComponent());
        mnuEdit.setEnabled(true);
        mnuDelete.setEnabled(true);
        mnuPopUp.setVisible(true);
    }
}//GEN-LAST:event_tblForce2MouseClicked

private void btnAddUnit1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddUnit1ActionPerformed
    dlgAddEdit Editor = new dlgAddEdit( this, true );
    Editor.setLocationRelativeTo( this );
    Editor.setVisible( true );
    if( Editor.GetResult() ) {
        rightForce.AddUnit( Editor.GetUnit() );
        RefreshDisplay();
    }
}//GEN-LAST:event_btnAddUnit1ActionPerformed

private void btnEditUnit1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditUnit1ActionPerformed
    Unit u = (Unit) rightForce.Units.get( tblForce2.getSelectedRow() );
    dlgAddEdit Editor = new dlgAddEdit( this, true, u );
    Editor.setLocationRelativeTo( this );
    Editor.setVisible( true );
    if( Editor.GetResult() ) {
        rightForce.AddUnit( Editor.GetUnit() );
        RefreshDisplay();
    }
}//GEN-LAST:event_btnEditUnit1ActionPerformed

private void btnRemoveUnit1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveUnit1ActionPerformed
    rightForce.RemoveUnit((Unit) rightForce.Units.get(tblForce2.getSelectedRow()));
    RefreshDisplay();
}//GEN-LAST:event_btnRemoveUnit1ActionPerformed

private void jScrollPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane1MouseClicked
    if (evt.getButton() == MouseEvent.BUTTON3) {
        mnuPopUp.setLocation(evt.getLocationOnScreen());
        mnuPopUp.setInvoker(evt.getComponent());
        mnuEdit.setEnabled(false);
        mnuDelete.setEnabled(false);
        mnuPopUp.setVisible(true);
    } else {
        btnAddUnitActionPerformed(null);
    }
}//GEN-LAST:event_jScrollPane1MouseClicked

private void tblForceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblForceKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
        btnRemoveUnitActionPerformed(null);
    }
}//GEN-LAST:event_tblForceKeyPressed

private void jScrollPane2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane2MouseClicked
    if (evt.getButton() == MouseEvent.BUTTON3) {
        mnuPopUp.setLocation(evt.getLocationOnScreen());
        mnuPopUp.setInvoker(evt.getComponent());
        mnuEdit.setEnabled(false);
        mnuDelete.setEnabled(false);
        mnuPopUp.setVisible(true);
    } else {
       btnAddUnit1ActionPerformed(null);
    }
}//GEN-LAST:event_jScrollPane2MouseClicked

private void tblForce2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblForce2KeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
        btnRemoveUnit1ActionPerformed(null);
    }
}//GEN-LAST:event_tblForce2KeyPressed

private void mnuAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddActionPerformed
    if (mnuPopUp.getInvoker().equals(tblForce)) {
        btnAddUnitActionPerformed(evt);
    } else if (mnuPopUp.getInvoker().equals(tblForce2)) {
        btnAddUnit1ActionPerformed(evt);
    } else if (mnuPopUp.getInvoker().equals(jScrollPane1)) {
        btnAddUnitActionPerformed(evt);
    } else if (mnuPopUp.getInvoker().equals(jScrollPane2)) {
        btnAddUnit1ActionPerformed(evt);
    }
}//GEN-LAST:event_mnuAddActionPerformed

private void mnuEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEditActionPerformed
     if (mnuPopUp.getInvoker().equals(tblForce)) {
         btnEditUnitActionPerformed(evt);
    } else if (mnuPopUp.getInvoker().equals(tblForce2)) {
        btnEditUnit1ActionPerformed(evt);
    }
}//GEN-LAST:event_mnuEditActionPerformed

private void mnuDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDeleteActionPerformed
    if (mnuPopUp.getInvoker().equals(tblForce)) {
        btnRemoveUnitActionPerformed(evt);
    } else if (mnuPopUp.getInvoker().equals(tblForce2)) {
        btnRemoveUnit1ActionPerformed(evt);
    }
}//GEN-LAST:event_mnuDeleteActionPerformed

private void mnuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveActionPerformed
    if ((!txtForceName.getText().isEmpty()) && (!txtForceName1.getText().isEmpty())) {
        try {
            String filename = (txtForceName.getText() + " vs " + txtForceName1.getText() + ".xml").replace(" ", "_");
            XMLWriter write = new XMLWriter(this.leftForce, this.rightForce);
            write.WriteXML(filename);
            javax.swing.JOptionPane.showMessageDialog(this, "Forces saved to " + filename);
        } catch (java.io.IOException e) {

        }
    } else {
        javax.swing.JOptionPane.showMessageDialog(this, "Please enter Force Names before saving.");
    }
}//GEN-LAST:event_mnuSaveActionPerformed

private void mnuAboutCalcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAboutCalcActionPerformed
    dlgAbout About = new dlgAbout();
    About.setTitle("About Battle Value Calculator");
    About.setLocationRelativeTo(this);
    About.setVisible(true);
}//GEN-LAST:event_mnuAboutCalcActionPerformed

private void txtForceNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtForceNameFocusLost
    leftForce.ForceName = txtForceName.getText();
    leftForce.isDirty = true;
}//GEN-LAST:event_txtForceNameFocusLost

private void txtForceName1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtForceName1FocusLost
    rightForce.ForceName = txtForceName1.getText();
    rightForce.isDirty = true;
}//GEN-LAST:event_txtForceName1FocusLost

private void mnuLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLoadActionPerformed
    JFileChooser fc = new JFileChooser();
    fc.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {

            @Override
            public boolean accept(File f) {
                if (f.isDirectory()){
                    return true;
                }

                String extension = "" + f.getName().substring(f.getName().indexOf("."));
                if (extension.equals(".xml")){
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public String getDescription() {
                return "*.xml";
            }
        });
     fc.setAcceptAllFileFilterUsed( false );
     int returnVal = fc.showDialog( this, "Load Force Listing" );
       if( returnVal != JFileChooser.APPROVE_OPTION ) { return; }
       File ForceFile = fc.getSelectedFile();
       String filename = "";
       try {
           filename = ForceFile.getCanonicalPath();
       } catch( Exception e ) {
           javax.swing.JOptionPane.showMessageDialog( this, "There was a problem opening the file:\n" + e.getMessage() );
           return;
       }

       XMLReader reader = new XMLReader();
       try {
           reader.ReadFile(this, filename);
       } catch (Exception e) {
           javax.swing.JOptionPane.showMessageDialog( this, "Issue loading file:\n " + e.getMessage() );
           return;
       }

}//GEN-LAST:event_mnuLoadActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    mnuExitActionPerformed(null);
}//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddUnit;
    private javax.swing.JButton btnAddUnit1;
    private javax.swing.JButton btnEditUnit;
    private javax.swing.JButton btnEditUnit1;
    private javax.swing.JButton btnRemoveUnit;
    private javax.swing.JButton btnRemoveUnit1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JLabel lblC3BV;
    private javax.swing.JLabel lblC3BV1;
    private javax.swing.JLabel lblForce1MultiplierAdjusted;
    private javax.swing.JLabel lblForceBV;
    private javax.swing.JLabel lblForceBV1;
    private javax.swing.JLabel lblForceMult;
    private javax.swing.JLabel lblForceMultiplierAdjusted;
    private javax.swing.JLabel lblForceName;
    private javax.swing.JLabel lblForceName1;
    private javax.swing.JLabel lblTotalBV;
    private javax.swing.JLabel lblTotalBV1;
    private javax.swing.JPanel lblTotalBaseBV;
    private javax.swing.JLabel lblTotalModifierBV;
    private javax.swing.JLabel lblTotalModifierBV1;
    private javax.swing.JLabel lblTotalTonnage;
    private javax.swing.JLabel lblTotalTonnage1;
    private javax.swing.JLabel lblUnitCount;
    private javax.swing.JLabel lblUnitCount1;
    private javax.swing.JMenu mnuAbout;
    private javax.swing.JMenuItem mnuAboutCalc;
    private javax.swing.JMenuItem mnuAdd;
    private javax.swing.JMenuItem mnuDelete;
    private javax.swing.JMenuItem mnuEdit;
    private javax.swing.JMenuItem mnuExit;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenuItem mnuLoad;
    private javax.swing.JPopupMenu mnuPopUp;
    private javax.swing.JMenuItem mnuSave;
    private javax.swing.JTable tblForce;
    private javax.swing.JTable tblForce2;
    private javax.swing.JTextField txtForceName;
    private javax.swing.JTextField txtForceName1;
    // End of variables declaration//GEN-END:variables

}
