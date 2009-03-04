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
import BFB.IO.*;
import BFB.Common.CommonTools;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.print.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.*;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;
import ssw.components.*;
import ssw.print.Printer;

/**
 *
 * @author  justin
 */
public class frmMain2 extends javax.swing.JFrame {
    public Force leftForce = new Force();
    public Force rightForce = new Force();
    private Preferences Prefs;

    /** Creates new form frmMain */
    public frmMain2() {
        initComponents();
        Load();
        Prefs = Preferences.userNodeForPackage(this.getClass());
    }

    public void Load(){
        tblForce.setModel( leftForce );
        tblForce2.setModel ( rightForce );

        txtForceNameTop.setText(leftForce.ForceName);
        txtForceNameBottom.setText(rightForce.ForceName);

        //Create a sorting class and apply it to the list
        TableRowSorter Leftsorter = new TableRowSorter<Force>(leftForce);
        List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(7, SortOrder.ASCENDING));
        Leftsorter.setSortKeys(sortKeys);
        tblForce.setRowSorter(Leftsorter);

        tblForce.getColumnModel().getColumn(0).setPreferredWidth(150);
        tblForce.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblForce.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblForce.getColumnModel().getColumn(3).setPreferredWidth(40);
        tblForce.getColumnModel().getColumn(4).setPreferredWidth(20);
        tblForce.getColumnModel().getColumn(5).setPreferredWidth(20);
        tblForce.getColumnModel().getColumn(6).setPreferredWidth(20);
        tblForce.getColumnModel().getColumn(7).setPreferredWidth(20);

                //Create a sorting class and apply it to the list
        TableRowSorter Rightsorter = new TableRowSorter<Force>(rightForce);
        List <RowSorter.SortKey> sortKeys2 = new ArrayList<RowSorter.SortKey>();
        sortKeys2.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
        sortKeys2.add(new RowSorter.SortKey(7, SortOrder.ASCENDING));
        Rightsorter.setSortKeys(sortKeys2);
        tblForce2.setRowSorter(Rightsorter);

        tblForce2.getColumnModel().getColumn(0).setPreferredWidth(150);
        tblForce2.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblForce2.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblForce2.getColumnModel().getColumn(3).setPreferredWidth(40);
        tblForce2.getColumnModel().getColumn(4).setPreferredWidth(20);
        tblForce2.getColumnModel().getColumn(5).setPreferredWidth(20);
        tblForce2.getColumnModel().getColumn(6).setPreferredWidth(20);
        tblForce2.getColumnModel().getColumn(7).setPreferredWidth(20);
    }

    public void RefreshDisplay() {
        leftForce.OpForSize = rightForce.Units.size();
        rightForce.OpForSize = leftForce.Units.size();
        leftForce.RefreshBV();
        rightForce.RefreshBV();

        lblUnitCountVal.setText(""+leftForce.Units.size());
        lblUnitsCountVal1.setText(""+rightForce.Units.size());

        lblTotalTonnageVal.setText(String.format( "%1$,.2f", leftForce.TotalTonnage));
        lblTotalTonnageVal1.setText(String.format( "%1$,.2f", rightForce.TotalTonnage));

        lblTotalBVVal.setText(String.format("%1$,.0f", leftForce.TotalBaseBV));
        lblTotalBVVal1.setText(String.format("%1$,.0f", rightForce.TotalBaseBV));

        lblC3BVVal.setText(String.format("%1$,.0f", leftForce.TotalC3BV));
        lblC3BVVal1.setText(String.format("%1$,.0f", rightForce.TotalC3BV));

        lblTotalModifierBVVal.setText(String.format("%1$,.0f", leftForce.TotalModifierBV));
        lblTotalModifierBVVal1.setText(String.format("%1$,.0f", rightForce.TotalModifierBV));

        lblForceBVVal.setText(String.format( "%1$,.0f", leftForce.TotalForceBV));
        lblForceBVVal1.setText(String.format( "%1$,.0f", rightForce.TotalForceBV));

        lblForceMultVal.setText(String.format( "%1$,.2f", CommonTools.GetForceSizeMultiplier(leftForce.Units.size(), rightForce.Units.size())));
        lblForceMultiplierAdjustedVal.setText(String.format("%1$,.0f", leftForce.TotalForceBVAdjusted));
        lblForceMultiplierAdjustedVal1.setText(String.format("%1$,.0f", rightForce.TotalForceBVAdjusted));
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
        btnAddUnitTop = new javax.swing.JButton();
        btnEditUnitTop = new javax.swing.JButton();
        btnRemoveUnitTop = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        lblForceName = new javax.swing.JLabel();
        txtForceNameTop = new javax.swing.JTextField();
        btnLoadFromFileTop = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        btnAddUnitBottom = new javax.swing.JButton();
        btnEditUnitBottom = new javax.swing.JButton();
        btnRemoveUnitBottom = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        lblForceName1 = new javax.swing.JLabel();
        txtForceNameBottom = new javax.swing.JTextField();
        btnLoadFromFileBottom = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblForce2 = new javax.swing.JTable();
        lblTotalBaseBV = new javax.swing.JPanel();
        lblUnitCount = new javax.swing.JLabel();
        lblTotalTonnage = new javax.swing.JLabel();
        lblTotalBV = new javax.swing.JLabel();
        lblC3BV = new javax.swing.JLabel();
        lblTotalModifierBV = new javax.swing.JLabel();
        lblForceBV = new javax.swing.JLabel();
        lblForceMultiplierAdjusted = new javax.swing.JLabel();
        lblUnitCountVal = new javax.swing.JLabel();
        lblTotalTonnageVal = new javax.swing.JLabel();
        lblTotalBVVal = new javax.swing.JLabel();
        lblC3BVVal = new javax.swing.JLabel();
        lblTotalModifierBVVal = new javax.swing.JLabel();
        lblForceBVVal = new javax.swing.JLabel();
        lblForceMultiplierAdjustedVal = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lblUnitCount1 = new javax.swing.JLabel();
        lblTotalTonnage1 = new javax.swing.JLabel();
        lblTotalBV1 = new javax.swing.JLabel();
        lblC3BV1 = new javax.swing.JLabel();
        lblTotalModifierBV1 = new javax.swing.JLabel();
        lblForceBV1 = new javax.swing.JLabel();
        lblForceMultiplierAdjusted1 = new javax.swing.JLabel();
        lblUnitsCountVal1 = new javax.swing.JLabel();
        lblTotalTonnageVal1 = new javax.swing.JLabel();
        lblTotalBVVal1 = new javax.swing.JLabel();
        lblC3BVVal1 = new javax.swing.JLabel();
        lblTotalModifierBVVal1 = new javax.swing.JLabel();
        lblForceBVVal1 = new javax.swing.JLabel();
        lblForceMultiplierAdjustedVal1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSplitPane1 = new javax.swing.JSplitPane();
        jLabel1 = new javax.swing.JLabel();
        lblForceMultVal = new javax.swing.JLabel();
        lblForceMult = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        lblSelectedMech = new javax.swing.JLabel();
        txtMechwarrior1 = new javax.swing.JTextField();
        txtGunnery1 = new javax.swing.JTextField();
        txtPiloting1 = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        mnuNew = new javax.swing.JMenuItem();
        mnuSave = new javax.swing.JMenuItem();
        mnuLoad = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        mnuPrint = new javax.swing.JMenuItem();
        mnuPrintDesigns = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        mnuExit = new javax.swing.JMenuItem();
        mnuDesign = new javax.swing.JMenu();
        mnuDesignMech = new javax.swing.JMenuItem();
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
        setTitle("CBT Force Balancer");
        setMinimumSize(new java.awt.Dimension(550, 570));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setHorizontalScrollBar(null);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(750, 250));
        jScrollPane1.setOpaque(false);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(750, 250));
        jScrollPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane1MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fileDropped(evt);
            }
        });

        tblForce.setAutoCreateRowSorter(true);
        tblForce.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblForce.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblForce.setShowVerticalLines(false);
        tblForce.getTableHeader().setReorderingAllowed(false);
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
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jScrollPane1, gridBagConstraints);

        jPanel1.setMaximumSize(new java.awt.Dimension(101, 110));
        jPanel1.setMinimumSize(new java.awt.Dimension(101, 30));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        btnAddUnitTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/add2.png"))); // NOI18N
        btnAddUnitTop.setText("Add");
        btnAddUnitTop.setToolTipText("Add Unit");
        btnAddUnitTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddUnitTopActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(btnAddUnitTop, gridBagConstraints);

        btnEditUnitTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/edit.png"))); // NOI18N
        btnEditUnitTop.setText("Edit");
        btnEditUnitTop.setToolTipText("Edit Unit");
        btnEditUnitTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditUnitTopActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(btnEditUnitTop, gridBagConstraints);

        btnRemoveUnitTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/delete2.png"))); // NOI18N
        btnRemoveUnitTop.setText("Delete");
        btnRemoveUnitTop.setToolTipText("Delete Unit");
        btnRemoveUnitTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveUnitTopActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(btnRemoveUnitTop, gridBagConstraints);

        jSeparator4.setForeground(java.awt.SystemColor.control);
        jSeparator4.setPreferredSize(new java.awt.Dimension(50, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        jPanel1.add(jSeparator4, gridBagConstraints);

        lblForceName.setText("Force Name: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(lblForceName, gridBagConstraints);

        txtForceNameTop.setMinimumSize(new java.awt.Dimension(150, 20));
        txtForceNameTop.setPreferredSize(new java.awt.Dimension(200, 20));
        txtForceNameTop.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtForceNameTopFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(txtForceNameTop, gridBagConstraints);

        btnLoadFromFileTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/export1.png"))); // NOI18N
        btnLoadFromFileTop.setText("Load");
        btnLoadFromFileTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadFromFileTopActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel1.add(btnLoadFromFileTop, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 1);
        getContentPane().add(jPanel1, gridBagConstraints);

        jPanel4.setMinimumSize(new java.awt.Dimension(101, 30));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        btnAddUnitBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/add2.png"))); // NOI18N
        btnAddUnitBottom.setText("Add");
        btnAddUnitBottom.setToolTipText("Add Unit");
        btnAddUnitBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddUnitBottomActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel4.add(btnAddUnitBottom, gridBagConstraints);

        btnEditUnitBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/edit.png"))); // NOI18N
        btnEditUnitBottom.setText("Edit");
        btnEditUnitBottom.setToolTipText("Edit Unit");
        btnEditUnitBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditUnitBottomActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel4.add(btnEditUnitBottom, gridBagConstraints);

        btnRemoveUnitBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/delete2.png"))); // NOI18N
        btnRemoveUnitBottom.setText("Delete");
        btnRemoveUnitBottom.setToolTipText("Delete Unit");
        btnRemoveUnitBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveUnitBottomActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel4.add(btnRemoveUnitBottom, gridBagConstraints);

        jSeparator5.setForeground(new java.awt.Color(236, 233, 216));
        jSeparator5.setPreferredSize(new java.awt.Dimension(50, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        jPanel4.add(jSeparator5, gridBagConstraints);

        lblForceName1.setText("Force Name: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel4.add(lblForceName1, gridBagConstraints);

        txtForceNameBottom.setMinimumSize(new java.awt.Dimension(150, 20));
        txtForceNameBottom.setPreferredSize(new java.awt.Dimension(200, 20));
        txtForceNameBottom.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtForceNameBottomFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel4.add(txtForceNameBottom, gridBagConstraints);

        btnLoadFromFileBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/export1.png"))); // NOI18N
        btnLoadFromFileBottom.setText("Load");
        btnLoadFromFileBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadFromFileBottomActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel4.add(btnLoadFromFileBottom, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(jPanel4, gridBagConstraints);

        jScrollPane2.setHorizontalScrollBar(null);
        jScrollPane2.setMinimumSize(new java.awt.Dimension(750, 250));
        jScrollPane2.setOpaque(false);
        jScrollPane2.setPreferredSize(new java.awt.Dimension(750, 250));
        jScrollPane2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane2MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fileDropped(evt);
            }
        });

        tblForce2.setAutoCreateRowSorter(true);
        tblForce2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblForce2.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblForce2.setShowVerticalLines(false);
        tblForce2.getTableHeader().setReorderingAllowed(false);
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
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jScrollPane2, gridBagConstraints);

        lblTotalBaseBV.setLayout(new java.awt.GridBagLayout());

        lblUnitCount.setFont(new java.awt.Font("Trebuchet MS", 0, 12));
        lblUnitCount.setText("Units");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        lblTotalBaseBV.add(lblUnitCount, gridBagConstraints);

        lblTotalTonnage.setFont(new java.awt.Font("Trebuchet MS", 0, 12));
        lblTotalTonnage.setText("Tonnage");
        lblTotalTonnage.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        lblTotalBaseBV.add(lblTotalTonnage, gridBagConstraints);

        lblTotalBV.setFont(new java.awt.Font("Trebuchet MS", 0, 12));
        lblTotalBV.setText("Base BV");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        lblTotalBaseBV.add(lblTotalBV, gridBagConstraints);

        lblC3BV.setFont(new java.awt.Font("Trebuchet MS", 0, 12));
        lblC3BV.setText("C3 BV");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        lblTotalBaseBV.add(lblC3BV, gridBagConstraints);

        lblTotalModifierBV.setFont(new java.awt.Font("Trebuchet MS", 0, 12));
        lblTotalModifierBV.setText("Mod BV");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        lblTotalBaseBV.add(lblTotalModifierBV, gridBagConstraints);

        lblForceBV.setFont(new java.awt.Font("Trebuchet MS", 0, 12));
        lblForceBV.setText("Force BV");
        lblForceBV.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        lblTotalBaseBV.add(lblForceBV, gridBagConstraints);

        lblForceMultiplierAdjusted.setFont(new java.awt.Font("Trebuchet MS", 0, 12));
        lblForceMultiplierAdjusted.setText("Multiplier Adj");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        lblTotalBaseBV.add(lblForceMultiplierAdjusted, gridBagConstraints);

        lblUnitCountVal.setText("0");
        lblUnitCountVal.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        lblTotalBaseBV.add(lblUnitCountVal, gridBagConstraints);

        lblTotalTonnageVal.setText("0");
        lblTotalTonnageVal.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        lblTotalBaseBV.add(lblTotalTonnageVal, gridBagConstraints);

        lblTotalBVVal.setText("0");
        lblTotalBVVal.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        lblTotalBaseBV.add(lblTotalBVVal, gridBagConstraints);

        lblC3BVVal.setText("0");
        lblC3BVVal.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        lblTotalBaseBV.add(lblC3BVVal, gridBagConstraints);

        lblTotalModifierBVVal.setText("0");
        lblTotalModifierBVVal.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        lblTotalBaseBV.add(lblTotalModifierBVVal, gridBagConstraints);

        lblForceBVVal.setText("0");
        lblForceBVVal.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        lblTotalBaseBV.add(lblForceBVVal, gridBagConstraints);

        lblForceMultiplierAdjustedVal.setText("0");
        lblForceMultiplierAdjustedVal.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        lblTotalBaseBV.add(lblForceMultiplierAdjustedVal, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        getContentPane().add(lblTotalBaseBV, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        lblUnitCount1.setFont(new java.awt.Font("Trebuchet MS", 0, 12));
        lblUnitCount1.setText("Units");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(lblUnitCount1, gridBagConstraints);

        lblTotalTonnage1.setFont(new java.awt.Font("Trebuchet MS", 0, 12));
        lblTotalTonnage1.setText("Tonnage");
        lblTotalTonnage1.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(lblTotalTonnage1, gridBagConstraints);

        lblTotalBV1.setFont(new java.awt.Font("Trebuchet MS", 0, 12));
        lblTotalBV1.setText("Base BV");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(lblTotalBV1, gridBagConstraints);

        lblC3BV1.setFont(new java.awt.Font("Trebuchet MS", 0, 12));
        lblC3BV1.setText("C3 BV");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(lblC3BV1, gridBagConstraints);

        lblTotalModifierBV1.setFont(new java.awt.Font("Trebuchet MS", 0, 12));
        lblTotalModifierBV1.setText("Mod BV");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(lblTotalModifierBV1, gridBagConstraints);

        lblForceBV1.setFont(new java.awt.Font("Trebuchet MS", 0, 12));
        lblForceBV1.setText("Force BV");
        lblForceBV1.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(lblForceBV1, gridBagConstraints);

        lblForceMultiplierAdjusted1.setFont(new java.awt.Font("Trebuchet MS", 0, 12));
        lblForceMultiplierAdjusted1.setText("Multiplier Adj");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        jPanel3.add(lblForceMultiplierAdjusted1, gridBagConstraints);

        lblUnitsCountVal1.setText("0");
        lblUnitsCountVal1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblUnitsCountVal1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        jPanel3.add(lblUnitsCountVal1, gridBagConstraints);

        lblTotalTonnageVal1.setText("0");
        lblTotalTonnageVal1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        jPanel3.add(lblTotalTonnageVal1, gridBagConstraints);

        lblTotalBVVal1.setText("0");
        lblTotalBVVal1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        jPanel3.add(lblTotalBVVal1, gridBagConstraints);

        lblC3BVVal1.setText("0");
        lblC3BVVal1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        jPanel3.add(lblC3BVVal1, gridBagConstraints);

        lblTotalModifierBVVal1.setText("0");
        lblTotalModifierBVVal1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        jPanel3.add(lblTotalModifierBVVal1, gridBagConstraints);

        lblForceBVVal1.setText("0");
        lblForceBVVal1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        jPanel3.add(lblForceBVVal1, gridBagConstraints);

        lblForceMultiplierAdjustedVal1.setText("0");
        lblForceMultiplierAdjustedVal1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        jPanel3.add(lblForceMultiplierAdjustedVal1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        getContentPane().add(jPanel3, gridBagConstraints);

        jSeparator1.setMaximumSize(new java.awt.Dimension(0, 2));
        jSeparator1.setMinimumSize(new java.awt.Dimension(0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.ipady = 4;
        getContentPane().add(jSeparator1, gridBagConstraints);

        jSplitPane1.setBorder(null);

        jLabel1.setText("jLabel1");
        jSplitPane1.setLeftComponent(jLabel1);

        lblForceMultVal.setText("0");
        jSplitPane1.setRightComponent(lblForceMultVal);

        lblForceMult.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblForceMult.setText("Force Multiplier");
        lblForceMult.setToolTipText("The modifier applied to the larger force");
        lblForceMult.setAlignmentX(0.5F);
        lblForceMult.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jSplitPane1.setLeftComponent(lblForceMult);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        getContentPane().add(jSplitPane1, gridBagConstraints);

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(jPanel2, gridBagConstraints);

        lblSelectedMech.setText("Selected Mech");
        jPanel5.add(lblSelectedMech);

        txtMechwarrior1.setText("Mechwarrior");
        txtMechwarrior1.setPreferredSize(new java.awt.Dimension(200, 20));
        jPanel5.add(txtMechwarrior1);

        txtGunnery1.setText("Gunnery");
        txtGunnery1.setMaximumSize(new java.awt.Dimension(100, 20));
        txtGunnery1.setMinimumSize(new java.awt.Dimension(100, 20));
        txtGunnery1.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel5.add(txtGunnery1);

        txtPiloting1.setText("Piloting");
        txtPiloting1.setMaximumSize(new java.awt.Dimension(100, 20));
        txtPiloting1.setMinimumSize(new java.awt.Dimension(100, 20));
        txtPiloting1.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel5.add(txtPiloting1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(jPanel5, gridBagConstraints);

        mnuFile.setText("File");

        mnuNew.setText("New");
        mnuNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNewActionPerformed(evt);
            }
        });
        mnuFile.add(mnuNew);

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
        mnuFile.add(jSeparator3);

        mnuPrint.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK));
        mnuPrint.setText("Print");
        mnuPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintActionPerformed(evt);
            }
        });
        mnuFile.add(mnuPrint);

        mnuPrintDesigns.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuPrintDesigns.setText("Print Designs");
        mnuPrintDesigns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintDesignsActionPerformed(evt);
            }
        });
        mnuFile.add(mnuPrintDesigns);
        mnuFile.add(jSeparator2);

        mnuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK));
        mnuExit.setText("Exit");
        mnuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExitActionPerformed(evt);
            }
        });
        mnuFile.add(mnuExit);

        jMenuBar1.add(mnuFile);

        mnuDesign.setText("Design");

        mnuDesignMech.setText("BattleMech");
        mnuDesignMech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDesignMechActionPerformed(evt);
            }
        });
        mnuDesign.add(mnuDesignMech);

        jMenuBar1.add(mnuDesign);

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

    private void btnAddUnitTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddUnitTopActionPerformed
    dlgAddEdit Editor = new dlgAddEdit( this, true );
    Editor.setLocationRelativeTo( this );
    Editor.setVisible( true );
    if( Editor.GetResult() ) {
        leftForce.AddUnit(Editor.GetUnit());
        RefreshDisplay();
    }
}//GEN-LAST:event_btnAddUnitTopActionPerformed

private void btnEditUnitTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditUnitTopActionPerformed
    Unit u = (Unit) leftForce.Units.get( tblForce.convertRowIndexToModel(tblForce.getSelectedRow() ) );
    dlgAddEdit Editor = new dlgAddEdit( this, true, u );
    Editor.setTitle("Edit Unit");
    Editor.setLocationRelativeTo( this );
    Editor.setVisible( true );
    if( Editor.GetResult() ) {
        leftForce.AddUnit(Editor.GetUnit());
        RefreshDisplay();
    }
}//GEN-LAST:event_btnEditUnitTopActionPerformed

private void btnRemoveUnitTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveUnitTopActionPerformed
    int[] rows = tblForce.getSelectedRows();
    for (int i=0; i <= rows.length; i++ ) {
        leftForce.RemoveUnit((Unit) leftForce.Units.get(tblForce.convertRowIndexToModel(rows[i])));
    }
    RefreshDisplay();
}//GEN-LAST:event_btnRemoveUnitTopActionPerformed

private void tblForceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblForceMouseClicked
    if (evt.getClickCount() >= 2) { btnEditUnitTopActionPerformed(null); }
    if (evt.getButton() == MouseEvent.BUTTON3) {
        mnuPopUp.setLocation(evt.getLocationOnScreen());
        mnuPopUp.setInvoker(evt.getComponent());
        mnuEdit.setEnabled(true);
        mnuDelete.setEnabled(true);
        mnuPopUp.setVisible(true);
    } else {
        Unit u = (Unit) leftForce.Units.get(tblForce.convertRowIndexToModel(tblForce.getSelectedRow()));
        lblSelectedMech.setText(u.TypeModel);
        txtMechwarrior1.setText(u.Mechwarrior);
        txtGunnery1.setText(u.Gunnery + "");
        txtPiloting1.setText(u.Piloting + "");
    }
}//GEN-LAST:event_tblForceMouseClicked

private void mnuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExitActionPerformed
        try {
            Prefs.exportNode(new FileOutputStream("preferences.xml"));
        } catch (IOException ex) {
            Logger.getLogger(frmMain2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BackingStoreException ex) {
            Logger.getLogger(frmMain2.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    if (evt.getClickCount() >= 2) { btnEditUnitBottomActionPerformed(null); }
    if (evt.getButton() == MouseEvent.BUTTON3) {
        mnuPopUp.setLocation(evt.getLocationOnScreen());
        mnuPopUp.setInvoker(evt.getComponent());
        mnuEdit.setEnabled(true);
        mnuDelete.setEnabled(true);
        mnuPopUp.setVisible(true);
    }
}//GEN-LAST:event_tblForce2MouseClicked

private void btnAddUnitBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddUnitBottomActionPerformed
    dlgAddEdit Editor = new dlgAddEdit( this, true );
    Editor.setLocationRelativeTo( this );
    Editor.setVisible( true );
    if( Editor.GetResult() ) {
        rightForce.AddUnit( Editor.GetUnit() );
        RefreshDisplay();
    }
}//GEN-LAST:event_btnAddUnitBottomActionPerformed

private void btnEditUnitBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditUnitBottomActionPerformed
    Unit u = (Unit) rightForce.Units.get( tblForce2.convertRowIndexToModel(tblForce2.getSelectedRow() ) );
    dlgAddEdit Editor = new dlgAddEdit( this, true, u );
    Editor.setTitle("Edit Unit");
    Editor.setLocationRelativeTo( this );
    Editor.setVisible( true );
    if( Editor.GetResult() ) {
        rightForce.AddUnit( Editor.GetUnit() );
        RefreshDisplay();
    }
}//GEN-LAST:event_btnEditUnitBottomActionPerformed

private void btnRemoveUnitBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveUnitBottomActionPerformed
    int[] rows = tblForce2.getSelectedRows();
    for (int i=0; i <= rows.length; i++ ) {
        rightForce.RemoveUnit((Unit) rightForce.Units.get(tblForce2.convertRowIndexToModel(rows[i])));
    }
    
    RefreshDisplay();
}//GEN-LAST:event_btnRemoveUnitBottomActionPerformed

private void jScrollPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane1MouseClicked
    if (evt.getButton() == MouseEvent.BUTTON3) {
        mnuPopUp.setLocation(evt.getLocationOnScreen());
        mnuPopUp.setInvoker(evt.getComponent());
        mnuEdit.setEnabled(false);
        mnuDelete.setEnabled(false);
        mnuPopUp.setVisible(true);
    } else {
        btnAddUnitTopActionPerformed(null);
    }
}//GEN-LAST:event_jScrollPane1MouseClicked

private void tblForceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblForceKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
        btnRemoveUnitTopActionPerformed(null);
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
       btnAddUnitBottomActionPerformed(null);
    }
}//GEN-LAST:event_jScrollPane2MouseClicked

private void tblForce2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblForce2KeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
        btnRemoveUnitBottomActionPerformed(null);
    }
}//GEN-LAST:event_tblForce2KeyPressed

private void mnuAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddActionPerformed
    if (mnuPopUp.getInvoker().equals(tblForce)) {
        btnAddUnitTopActionPerformed(evt);
    } else if (mnuPopUp.getInvoker().equals(tblForce2)) {
        btnAddUnitBottomActionPerformed(evt);
    } else if (mnuPopUp.getInvoker().equals(jScrollPane1)) {
        btnAddUnitTopActionPerformed(evt);
    } else if (mnuPopUp.getInvoker().equals(jScrollPane2)) {
        btnAddUnitBottomActionPerformed(evt);
    }
}//GEN-LAST:event_mnuAddActionPerformed

private void mnuEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEditActionPerformed
     if (mnuPopUp.getInvoker().equals(tblForce)) {
         btnEditUnitTopActionPerformed(evt);
    } else if (mnuPopUp.getInvoker().equals(tblForce2)) {
        btnEditUnitBottomActionPerformed(evt);
    }
}//GEN-LAST:event_mnuEditActionPerformed

private void mnuDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDeleteActionPerformed
    if (mnuPopUp.getInvoker().equals(tblForce)) {
        btnRemoveUnitTopActionPerformed(evt);
    } else if (mnuPopUp.getInvoker().equals(tblForce2)) {
        btnRemoveUnitBottomActionPerformed(evt);
    }
}//GEN-LAST:event_mnuDeleteActionPerformed

private void mnuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveActionPerformed
    if ((!txtForceNameTop.getText().isEmpty()) && (!txtForceNameBottom.getText().isEmpty())) {
        try {
            String filename = (Prefs.get("LastOpenBFBDirectory", "") + CommonTools.FormatFileName(txtForceNameTop.getText() + "_vs_" + txtForceNameBottom.getText() + ".xml"));
            XMLWriter write = new XMLWriter(this.leftForce, this.rightForce);
            write.WriteXML(filename);
            Prefs.put("LastOpenBFBFile", filename);
            javax.swing.JOptionPane.showMessageDialog(this, "Forces saved to " + filename);
        } catch (java.io.IOException e) {
            javax.swing.JOptionPane.showMessageDialog(this, e.getMessage());
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

private void txtForceNameTopFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtForceNameTopFocusLost
    leftForce.ForceName = txtForceNameTop.getText();
    leftForce.isDirty = true;
}//GEN-LAST:event_txtForceNameTopFocusLost

private void txtForceNameBottomFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtForceNameBottomFocusLost
    rightForce.ForceName = txtForceNameBottom.getText();
    rightForce.isDirty = true;
}//GEN-LAST:event_txtForceNameBottomFocusLost

private void mnuLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLoadActionPerformed
    FileSelector openFile = new FileSelector();
    File forceFile = openFile.SelectFile(Prefs.get("LastOpenBFBDirectory", ""), "xml", "Load Force List");

    if (forceFile != null) {
        XMLReader reader = new XMLReader();
        try {
           reader.ReadFile(this, forceFile.getCanonicalPath());

           Prefs.put("LastOpenBFBDirectory", forceFile.getCanonicalPath().replace(forceFile.getName(), ""));
           Prefs.put("LastOpenBFBFile", forceFile.getName());
        } catch (Exception e) {
           javax.swing.JOptionPane.showMessageDialog( this, "Issue loading file:\n " + e.getMessage() );
           return;
        }
    }
}//GEN-LAST:event_mnuLoadActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    mnuExitActionPerformed(null);
}//GEN-LAST:event_formWindowClosing

private void mnuPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintActionPerformed
   PrinterJob job = PrinterJob.getPrinterJob();
   PrintSheet p = new PrintSheet(this, 576, 756);
   Paper paper = new Paper();
   paper.setImageableArea(18, 18, 576, 756 );
   PageFormat page = new PageFormat();
   page.setPaper( paper );
   job.setPrintable( p, page );
   boolean DoPrint = job.printDialog();
   if( DoPrint ) {
       try {
           job.print();
       } catch( PrinterException e ) {
           System.err.println( e.getMessage() );
           System.out.println( e.getStackTrace() );
       }
  }
}//GEN-LAST:event_mnuPrintActionPerformed

private void LoadFromFile(Force f){
    FileSelector openFile = new FileSelector();
    SSWReader reader = new SSWReader();
    File[] files = null;
    try
    {
        files = openFile.SelectFiles(Prefs.get("LastOpenSSWDirectory", ""), "ssw", "Select File(s)");
        if (files != null) {
            if (files.length > 0)
            {
                Prefs.put("LastOpenSSWDirectory", files[0].getCanonicalPath());
                for (int i = 0; i<= files.length-1; i++) {
                    try {
                       reader.ReadFile(f, files[i].getCanonicalPath());
                       RefreshDisplay();
                    } catch (Exception e) {
                       javax.swing.JOptionPane.showMessageDialog( this, "Issue loading file:\n " + e.getMessage() );
                       return;
                    }
                }
            }
        }
    } catch (IOException ie) {

    }
}

private void fileDropped(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileDropped

}//GEN-LAST:event_fileDropped

private void btnLoadFromFileTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadFromFileTopActionPerformed
    LoadFromFile(this.leftForce);
}//GEN-LAST:event_btnLoadFromFileTopActionPerformed

private void btnLoadFromFileBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadFromFileBottomActionPerformed
    LoadFromFile(this.rightForce);
}//GEN-LAST:event_btnLoadFromFileBottomActionPerformed

private void mnuPrintDesignsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintDesignsActionPerformed
    Vector forces = new Vector();
    forces.add(leftForce);
    forces.add(rightForce);
    
    for (int f = 0; f <= forces.size()-1; f++){
        Printer printer = new Printer();
        Force printForce = (Force) forces.get(f);

        printer.setJobName(printForce.ForceName);

        for (int i = 0; i < printForce.Units.size(); ++i) {
            Unit u = (Unit) printForce.Units.get(i);
            Mech m = u.m;
            if (m != null) {
                printer.AddMech(m, u.Mechwarrior, u.Gunnery, u.Piloting, true, true, true);
            }
        }

        printer.Print();

    }
}//GEN-LAST:event_mnuPrintDesignsActionPerformed

private void mnuNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNewActionPerformed
    this.leftForce.Clear();
    this.rightForce.Clear();
    this.RefreshDisplay();
}//GEN-LAST:event_mnuNewActionPerformed

private void mnuDesignMechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDesignMechActionPerformed
    ssw.gui.frmMain SSW = new ssw.gui.frmMain();
    SSW.setLocationRelativeTo(null);
    SSW.setVisible(true);
}//GEN-LAST:event_mnuDesignMechActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddUnitBottom;
    private javax.swing.JButton btnAddUnitTop;
    private javax.swing.JButton btnEditUnitBottom;
    private javax.swing.JButton btnEditUnitTop;
    private javax.swing.JButton btnLoadFromFileBottom;
    private javax.swing.JButton btnLoadFromFileTop;
    private javax.swing.JButton btnRemoveUnitBottom;
    private javax.swing.JButton btnRemoveUnitTop;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lblC3BV;
    private javax.swing.JLabel lblC3BV1;
    private javax.swing.JLabel lblC3BVVal;
    private javax.swing.JLabel lblC3BVVal1;
    private javax.swing.JLabel lblForceBV;
    private javax.swing.JLabel lblForceBV1;
    private javax.swing.JLabel lblForceBVVal;
    private javax.swing.JLabel lblForceBVVal1;
    private javax.swing.JLabel lblForceMult;
    private javax.swing.JLabel lblForceMultVal;
    private javax.swing.JLabel lblForceMultiplierAdjusted;
    private javax.swing.JLabel lblForceMultiplierAdjusted1;
    private javax.swing.JLabel lblForceMultiplierAdjustedVal;
    private javax.swing.JLabel lblForceMultiplierAdjustedVal1;
    private javax.swing.JLabel lblForceName;
    private javax.swing.JLabel lblForceName1;
    private javax.swing.JLabel lblSelectedMech;
    private javax.swing.JLabel lblTotalBV;
    private javax.swing.JLabel lblTotalBV1;
    private javax.swing.JLabel lblTotalBVVal;
    private javax.swing.JLabel lblTotalBVVal1;
    private javax.swing.JPanel lblTotalBaseBV;
    private javax.swing.JLabel lblTotalModifierBV;
    private javax.swing.JLabel lblTotalModifierBV1;
    private javax.swing.JLabel lblTotalModifierBVVal;
    private javax.swing.JLabel lblTotalModifierBVVal1;
    private javax.swing.JLabel lblTotalTonnage;
    private javax.swing.JLabel lblTotalTonnage1;
    private javax.swing.JLabel lblTotalTonnageVal;
    private javax.swing.JLabel lblTotalTonnageVal1;
    private javax.swing.JLabel lblUnitCount;
    private javax.swing.JLabel lblUnitCount1;
    private javax.swing.JLabel lblUnitCountVal;
    private javax.swing.JLabel lblUnitsCountVal1;
    private javax.swing.JMenu mnuAbout;
    private javax.swing.JMenuItem mnuAboutCalc;
    private javax.swing.JMenuItem mnuAdd;
    private javax.swing.JMenuItem mnuDelete;
    private javax.swing.JMenu mnuDesign;
    private javax.swing.JMenuItem mnuDesignMech;
    private javax.swing.JMenuItem mnuEdit;
    private javax.swing.JMenuItem mnuExit;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenuItem mnuLoad;
    private javax.swing.JMenuItem mnuNew;
    private javax.swing.JPopupMenu mnuPopUp;
    private javax.swing.JMenuItem mnuPrint;
    private javax.swing.JMenuItem mnuPrintDesigns;
    private javax.swing.JMenuItem mnuSave;
    private javax.swing.JTable tblForce;
    private javax.swing.JTable tblForce2;
    private javax.swing.JTextField txtForceNameBottom;
    private javax.swing.JTextField txtForceNameTop;
    private javax.swing.JTextField txtGunnery1;
    private javax.swing.JTextField txtMechwarrior1;
    private javax.swing.JTextField txtPiloting1;
    // End of variables declaration//GEN-END:variables

}
