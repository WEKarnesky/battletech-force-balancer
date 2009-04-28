/*
Copyright (c) 2008~2009, Justin R. Bengtson (poopshotgun@yahoo.com)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
        this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
        this list of conditions and the following disclaimer in the
        documentation and/or other materials provided with the distribution.
    * Neither the name of Justin R. Bengtson nor the names of contributors may
        be used to endorse or promote products derived from this software
        without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package BFB.GUI;

import BFB.Force;
import BFB.IO.FileSelector;
import BFB.IO.RUSReader;
import BFB.RUS;
import BFB.Unit;
import java.awt.Cursor;
import java.awt.RenderingHints.Key;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;
import ssw.Options;
import ssw.filehandlers.*;

public class dlgOpen extends javax.swing.JFrame implements java.awt.datatransfer.ClipboardOwner {
    private frmBase parent;
    private MechList list;
    private String MechListPath = "";
    private Force force;
    private RUS rus = new RUS();

    /** Creates new form dlgOpen */
    public dlgOpen(java.awt.Frame parent, boolean modal) {
        initComponents();
        this.parent = (frmBase) parent;

        cmbTech.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Tech", "Clan", "Inner Sphere" }));
        cmbEra.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Era", "Age of War/Star League", "Succession Wars", "Clan Invasion" }));

        LoadList();
    }

    public void setForce(Force force) {
        this.force = force;
    }

    private void LoadMech() {
        if ( tblMechData.getSelectedRows().length > 0 ) {
            int[] Rows = tblMechData.getSelectedRows();
            for (int i=0; i < Rows.length; i++ )
            {
                MechListData Data = ((MechList) tblMechData.getModel()).Get( tblMechData.convertRowIndexToModel( Rows[i] ) );
                force.AddUnit(new Unit(Data));
            }
            this.setVisible(false);
        }
    }

    private void LoadRUSFiles( String dirPath ) {
        if ( dirPath.isEmpty() ) { return; }
        try
        {
            DefaultListModel listModel = new DefaultListModel();
            File file = new File(dirPath);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for ( int i=0; i < files.length; i++ ) {
                    if ( files[i].isFile() ) {
                        if ( files[i].getName().endsWith(".txt") ) {
                            listModel.addElement(files[i].getName());
                        }
                    }
                }
            }
            lstFiles.setModel(listModel);
        } catch (NullPointerException npe) {

        }
    }

    private void Calculate() {
        txtSelected.setText("0 Units Selected for 0 BV and 0 C-Bills");

        int BV = 0;
        float Cost = 0;

        int[] rows = tblMechData.getSelectedRows();
        for ( int i=0; i < rows.length; i++ ) {
            MechListData data = list.Get(tblMechData.convertRowIndexToModel(rows[i]));
            BV += data.getBV();
            Cost += data.getCost();
        }

        txtSelected.setText(rows.length + " Units Selected for " + String.format("%,d", BV) + " BV and " + String.format("%,.2f", Cost) + " C-Bills");
    }

    public void LoadList() {
        if (MechListPath.isEmpty()) {
            ssw.Options sswOpts = new Options();
            setMechListPath(sswOpts.SaveLoadPath);
        }

        if ( ! MechListPath.isEmpty() ) {

            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            list = new MechList(MechListPath);

            if (list.Size() > 0) {
                setupList(list);
            }

            String displayPath = MechListPath;
            if (! MechListPath.isEmpty() ) {
                if (MechListPath.contains(File.separator)) {
                    displayPath = MechListPath.substring(0, 3) + "..." + MechListPath.substring(MechListPath.lastIndexOf(File.separator)) + "";
                }
            }
            this.lblLoading.setText(list.Size() + " Mechs loaded from " + displayPath);
            this.lblLoading.setToolTipText(MechListPath);
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    private void setupList(MechList mechList) {
        tblMechData.setModel(mechList);

        //Create a sorting class and apply it to the list
        TableRowSorter sorter = new TableRowSorter<MechList>(mechList);
        List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        tblMechData.setRowSorter(sorter);

        tblMechData.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblMechData.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblMechData.getColumnModel().getColumn(2).setPreferredWidth(40);
        tblMechData.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblMechData.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblMechData.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblMechData.getColumnModel().getColumn(6).setPreferredWidth(80);
        tblMechData.getColumnModel().getColumn(7).setPreferredWidth(40);
    }

    private void checkSelection() {
        if ( tblMechData.getSelectedRowCount() > 0 ) {
            Calculate();
            btnOpenMech.setEnabled(true);
        } else {
            btnOpenMech.setEnabled(false);
            txtSelected.setText("0 Units Selected for 0 BV and 0 C-Bills");
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

        tbpSelections = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        txtSelected = new javax.swing.JLabel();
        lblLoading = new javax.swing.JLabel();
        btnOpenDir = new javax.swing.JButton();
        spnMechTable = new javax.swing.JScrollPane();
        tblMechData = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        txtMaxCost = new javax.swing.JTextField();
        txtMinCost = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        txtMinBV = new javax.swing.JTextField();
        txtMaxBV = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        cmbTech = new javax.swing.JComboBox();
        jPanel11 = new javax.swing.JPanel();
        cmbEra = new javax.swing.JComboBox();
        jPanel12 = new javax.swing.JPanel();
        txtName = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        cmbClass = new javax.swing.JComboBox();
        btnFilter = new javax.swing.JButton();
        btnClearFilter = new javax.swing.JButton();
        btnOpenMech = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        btnRoll = new javax.swing.JButton();
        spnSelections = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        spnAddOn = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstFiles = new javax.swing.JList();
        jToolBar1 = new javax.swing.JToolBar();
        btnDirectory = new javax.swing.JButton();
        lblCurDir = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstOptions = new javax.swing.JList();
        jPanel13 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstSelected = new javax.swing.JList();
        btnClearSelection = new javax.swing.JButton();
        btnClipboard = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Unit Selection");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        tbpSelections.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbpSelectionsFocusGained(evt);
            }
        });

        txtSelected.setText("0 Units Selected");

        lblLoading.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLoading.setText("Loading Mechs....");
        lblLoading.setMaximumSize(new java.awt.Dimension(400, 14));

        btnOpenDir.setText("Open Dir");
        btnOpenDir.setToolTipText("Change Directory");
        btnOpenDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenDirActionPerformed(evt);
            }
        });

        tblMechData.setAutoCreateRowSorter(true);
        tblMechData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblMechData.setIntercellSpacing(new java.awt.Dimension(4, 4));
        tblMechData.setShowVerticalLines(false);
        tblMechData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMechDataMouseClicked(evt);
            }
        });
        tblMechData.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblMechDataKeyPressed(evt);
            }
        });
        spnMechTable.setViewportView(tblMechData);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Filters"));

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("C-Bill Cost"));

        txtMaxCost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaxCostFilter(evt);
            }
        });

        txtMinCost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMinCostFilter(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(txtMinCost, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtMaxCost, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtMinCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtMaxCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Battle Value"));

        txtMaxBV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Filter(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(txtMinBV, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMaxBV, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtMinBV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtMaxBV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Technology"));

        cmbTech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Filter(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cmbTech, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cmbTech, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Era"));

        cmbEra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Filter(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cmbEra, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cmbEra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Name"));

        txtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNameKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("Class"));

        cmbClass.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Light", "Medium", "Heavy", "Assault" }));
        cmbClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbClassActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cmbClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cmbClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        btnFilter.setText("Filter");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Filter(evt);
            }
        });

        btnClearFilter.setText("Clear");
        btnClearFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearFilterFilter(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnFilter)
                    .addComponent(btnClearFilter))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(btnFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnClearFilter))
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        btnOpenMech.setText("Add Mech(s)");
        btnOpenMech.setEnabled(false);
        btnOpenMech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenMechActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spnMechTable, javax.swing.GroupLayout.DEFAULT_SIZE, 930, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 547, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblLoading, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnOpenDir, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnOpenMech)))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLoading, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOpenDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnMechTable, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOpenMech, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tbpSelections.addTab("Unit Selection", jPanel1);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Selection Criteria"));

        jLabel5.setText("# of Selections");

        btnRoll.setText("Roll");
        btnRoll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRollActionPerformed(evt);
            }
        });

        spnSelections.setModel(new javax.swing.SpinnerNumberModel(1, 1, 36, 1));
        spnSelections.setValue(1);

        jLabel1.setText("Add");

        spnAddOn.setModel(new javax.swing.SpinnerNumberModel(0, -6, 6, 1));

        jLabel2.setText("to all rolls");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnSelections, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRoll))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnAddOn, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(spnSelections, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRoll))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(spnAddOn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Files Available"));

        lstFiles.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstFiles.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstFilesValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(lstFiles);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnDirectory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/folder-open-document.png"))); // NOI18N
        btnDirectory.setText("Change Directory");
        btnDirectory.setFocusable(false);
        btnDirectory.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnDirectory.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnDirectory.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDirectoryActionPerformed(evt);
            }
        });
        jToolBar1.add(btnDirectory);

        lblCurDir.setText("Selected Directory...");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCurDir, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCurDir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Available Items"));

        lstOptions.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(lstOptions);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("General Information"));

        jLabel3.setText("This section re-uses the data files available ");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addContainerGap(188, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addContainerGap(80, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tbpSelections.addTab("Random Selections", jPanel2);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Random Selections"));

        lstSelected.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstSelectedValueChanged(evt);
            }
        });
        lstSelected.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lstSelectedKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(lstSelected);

        btnClearSelection.setText("Clear");
        btnClearSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearSelectionActionPerformed(evt);
            }
        });

        btnClipboard.setText("Clipboard");
        btnClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClipboardActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnClearSelection)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnClipboard))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClipboard)
                    .addComponent(btnClearSelection)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tbpSelections, javax.swing.GroupLayout.PREFERRED_SIZE, 945, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(tbpSelections, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblMechDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMechDataMouseClicked
        if ( evt.getClickCount() == 2 ) {
            LoadMech();
        } else {
            checkSelection();
        }
    }//GEN-LAST:event_tblMechDataMouseClicked

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        //if (list.Size() == 0) LoadList();
    }//GEN-LAST:event_formWindowOpened

    private void btnOpenMechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenMechActionPerformed
        LoadMech();
    }//GEN-LAST:event_btnOpenMechActionPerformed

    private void Filter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Filter
        ListFilter filters = new ListFilter();

        if (cmbTech.getSelectedIndex() > 0) {filters.setTech(cmbTech.getSelectedItem().toString());}
        if (cmbEra.getSelectedIndex() > 0) {filters.setEra(cmbEra.getSelectedItem().toString());}
        if (cmbClass.getSelectedIndex() > 0) {
            switch (cmbClass.getSelectedIndex()) {
                case 1:
                    filters.setTonnage(20, 35);
                    break;
                case 2:
                    filters.setTonnage(40, 55);
                    break;
                case 3:
                    filters.setTonnage(60, 75);
                    break;
                case 4:
                    filters.setTonnage(80, 100);
                    break;
            }
        }
        if (! txtMinBV.getText().isEmpty() ) {
            if ( txtMaxBV.getText().isEmpty() ) {
                filters.setBV(0, Integer.parseInt(txtMinBV.getText()));
            } else {
                filters.setBV(Integer.parseInt(txtMinBV.getText()), Integer.parseInt(txtMaxBV.getText()));
            }
        }
        if (! txtMinCost.getText().isEmpty() ) {
            if ( txtMaxCost.getText().isEmpty() ) {
                filters.setCost(0, Float.parseFloat(txtMinCost.getText()));
            } else {
                filters.setCost(Float.parseFloat(txtMinCost.getText()), Float.parseFloat(txtMaxCost.getText()));
            }
        }
        if (! txtName.getText().isEmpty() ) { filters.setName( txtName.getText() ); }

        MechList filtered = list.Filter(filters);
        setupList(filtered);
    }//GEN-LAST:event_Filter

    private void btnOpenDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenDirActionPerformed
        setMechListPath(parent.Prefs.get("ListPath", ""));
        FileSelector selector = new FileSelector();
        setMechListPath(selector.GetDirectorySelection(MechListPath));
        
        this.setVisible(true);
        parent.Prefs.put("ListPath", MechListPath);
        LoadList();
    }//GEN-LAST:event_btnOpenDirActionPerformed

    private void txtMinCostFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMinCostFilter
        // TODO add your handling code here:
}//GEN-LAST:event_txtMinCostFilter

    private void txtMaxCostFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaxCostFilter
        // TODO add your handling code here:
}//GEN-LAST:event_txtMaxCostFilter

    private void btnClearFilterFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearFilterFilter
        setupList(list);
        cmbTech.setSelectedIndex(0);
        cmbEra.setSelectedIndex(0);
        cmbClass.setSelectedIndex(0);
        txtMinBV.setText("");
        txtMaxBV.setText("");
        txtMinCost.setText("");
        txtMaxCost.setText("");
        txtName.setText("");
}//GEN-LAST:event_btnClearFilterFilter

    private void tblMechDataKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblMechDataKeyPressed
        //javax.swing.JOptionPane.showMessageDialog(this, "You typed a " + evt.getKeyChar());
    }//GEN-LAST:event_tblMechDataKeyPressed

    private void lstFilesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstFilesValueChanged
        String filename = lstFiles.getSelectedValue().toString();
        RUSReader reader = new RUSReader();
        try {
            reader.Load( parent.Prefs.get("RUSPath", "") + File.separator + filename, rus);
            lstOptions.setModel(rus.getDisplay());
        } catch ( Exception e ) {

        }

    }//GEN-LAST:event_lstFilesValueChanged

    private void btnRollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRollActionPerformed
        lstSelected.setModel( rus.Generate( Integer.parseInt(spnSelections.getValue().toString()), Integer.parseInt( spnAddOn.getValue().toString()) ) );
}//GEN-LAST:event_btnRollActionPerformed

    private void tbpSelectionsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpSelectionsFocusGained
        LoadRUSFiles(this.parent.Prefs.get("RUSPath", ""));
}//GEN-LAST:event_tbpSelectionsFocusGained

    private void btnDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDirectoryActionPerformed
        String RUSPath = parent.Prefs.get("RUSPath", "");
        FileSelector selector = new FileSelector();
        RUSPath = selector.GetDirectorySelection(RUSPath);
        parent.Prefs.put("RUSPath", RUSPath);

        LoadRUSFiles(RUSPath);
    }//GEN-LAST:event_btnDirectoryActionPerformed

    private void btnClearSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearSelectionActionPerformed
        lstSelected.setModel(rus.ClearSelection());
}//GEN-LAST:event_btnClearSelectionActionPerformed

    private void btnClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClipboardActionPerformed
        String NL = System.getProperty( "line.separator" );
        
        java.awt.datatransfer.StringSelection export = new java.awt.datatransfer.StringSelection( "" );
        java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents( export, this );
    }//GEN-LAST:event_btnClipboardActionPerformed

    private void lstSelectedKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lstSelectedKeyPressed
        if ( lstSelected.getSelectedValues().length > 0 ) {
            if ( evt.getKeyCode() == KeyEvent.VK_DELETE ) {
                for (int i=0; i < lstSelected.getSelectedValues().length; i++ ) {
                    DefaultListModel model = (DefaultListModel) lstSelected.getModel();
                    model.removeElement((Object) lstSelected.getSelectedValues()[i]);
                }
            }
        }
    }//GEN-LAST:event_lstSelectedKeyPressed

    private void txtNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyTyped
        Filter(null);
    }//GEN-LAST:event_txtNameKeyTyped

    private void lstSelectedValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstSelectedValueChanged
        if ( lstSelected.getSelectedValues().length > 0 ) {
            String Name = ((Object) lstSelected.getSelectedValues()[0]).toString();
            txtName.setText(Name.split(" ")[1].substring(0, 6).trim());
            Filter(null);
        }
    }//GEN-LAST:event_lstSelectedValueChanged

    private void cmbClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbClassActionPerformed
        Filter(evt);
    }//GEN-LAST:event_cmbClassActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClearFilter;
    private javax.swing.JButton btnClearSelection;
    private javax.swing.JButton btnClipboard;
    private javax.swing.JButton btnDirectory;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnOpenDir;
    private javax.swing.JButton btnOpenMech;
    private javax.swing.JButton btnRoll;
    private javax.swing.JComboBox cmbClass;
    private javax.swing.JComboBox cmbEra;
    private javax.swing.JComboBox cmbTech;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblCurDir;
    private javax.swing.JLabel lblLoading;
    private javax.swing.JList lstFiles;
    private javax.swing.JList lstOptions;
    private javax.swing.JList lstSelected;
    private javax.swing.JSpinner spnAddOn;
    private javax.swing.JScrollPane spnMechTable;
    private javax.swing.JSpinner spnSelections;
    private javax.swing.JTable tblMechData;
    private javax.swing.JTabbedPane tbpSelections;
    private javax.swing.JTextField txtMaxBV;
    private javax.swing.JTextField txtMaxCost;
    private javax.swing.JTextField txtMinBV;
    private javax.swing.JTextField txtMinCost;
    private javax.swing.JTextField txtName;
    private javax.swing.JLabel txtSelected;
    // End of variables declaration//GEN-END:variables

    /**
     * @param MechListPath the MechListPath to set
     */
    public void setMechListPath(String MechListPath) {
        this.MechListPath = MechListPath;
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        //do nothing
    }

}
