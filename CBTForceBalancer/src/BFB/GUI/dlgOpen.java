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

import BFB.Common.Constants;
import BFB.FSL;
import BFB.Force;
import BFB.IO.FileSelector;
import BFB.IO.RUSReader;
import BFB.IO.TXTWriter;
import BFB.RUS;
import BFB.Unit;
import java.awt.Cursor;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;
import ssw.filehandlers.ListFilter;
import ssw.filehandlers.MechList;
import ssw.filehandlers.MechListData;
import ssw.filehandlers.Media;

public class dlgOpen extends javax.swing.JFrame implements java.awt.datatransfer.ClipboardOwner {
    private frmBase parent;
    private MechList list,
                     filtered,
                     chosen = new MechList();
    private String MechListPath = "",
                    BaseRUSPath = "./random/tables/",
                    RUSDirectory = "",
                    RUSPath = BaseRUSPath;
    private Force force;
    private RUS rus = new RUS();
    private FSL fsl  = new FSL();
    private boolean useIndex = true;

    /** Creates new form dlgOpen */
    public dlgOpen(java.awt.Frame parent, boolean modal) {
        initComponents();
        this.parent = (frmBase) parent;

        cmbTech.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Tech", "Clan", "Inner Sphere", "Mixed" }));
        cmbEra.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Era", "Age of War/Star League", "Succession Wars", "Clan Invasion" }));
        cmbMechType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Type", "BattleMech", "IndustrialMech", "Primitive BattleMech", "Primitive IndustrialMech" }));
        cmbMotive.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Motive", "Biped", "Quad" }));

        LoadList();
        loadChosen();
        loadFSL();
        LoadRUSOptions();
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

    private void addChosen() {
        if ( tblMechData.getSelectedRows().length > 0 ) {
            int[] Rows = tblMechData.getSelectedRows();
            for (int i=0; i < Rows.length; i++ )
            {
                MechListData Data = ((MechList) tblMechData.getModel()).Get( tblMechData.convertRowIndexToModel( Rows[i] ) );
                chosen.Add(Data);
            }
            loadChosen();
        }
    }

    private void loadChosen() {
        lstChosen.setModel(new DefaultListModel());
        
        for ( int i=0; i < chosen.Size(); i++ ) {
            ((DefaultListModel) lstChosen.getModel()).addElement(chosen.Get(i).getFullName() + " (" + chosen.Get(i).getBV() + ") " + chosen.Get(i).getInfo());
        }
    }

    private void LoadRUSOptions() {
        try
        {
            DefaultListModel listModel = new DefaultListModel();
            File file = new File(BaseRUSPath);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for ( int i=0; i < files.length; i++ ) {
                    if ( files[i].isDirectory() ) {
                        listModel.addElement(files[i].getName());
                    }
                }
            }

            //sort the list
             int numItems = listModel.getSize();
             String[] a = new String[numItems];
             for (int i=0;i<numItems;i++){
               a[i] = (String)listModel.getElementAt(i);
             }
             sortArray(Collator.getInstance(),a);
             for (int i=0;i<numItems;i++) {
                listModel.setElementAt(a[i], i);
             }

            lstDirectories.setModel(listModel);
        } catch (NullPointerException npe) {

        }
    }

    public void sortArray(Collator collator, String[] strArray) {
        String tmp;
        if (strArray.length == 1) return;
        for (int i = 0; i < strArray.length; i++) {
            for (int j = i + 1; j < strArray.length; j++) {
                if( collator.compare(strArray[i], strArray[j] ) > 0 ) {
                    tmp = strArray[i];
                    strArray[i] = strArray[j];
                    strArray[j] = tmp;
                }
            }
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

    private void loadFSL() {
        try {
            fsl.Load("./data/FactionList.txt");
        } catch ( IOException ie ) {
            //do nothing
        }

        tblFSL.setModel(fsl);
        cmbFaction.setModel( fsl.getFactions() );
        cmbType.setModel( fsl.getTypes() );
        cmbSource.setModel( fsl.getSources() );
        cmbFSLEra.setModel( fsl.getEras() );

        //javax.swing.JOptionPane.showMessageDialog( this, fsl.Size() );
    }

    private void Calculate() {
        txtSelected.setText("0 Units Selected for 0 BV and 0 C-Bills");

        int BV = 0;
        float Cost = 0;

        int[] rows = tblMechData.getSelectedRows();
        for ( int i=0; i < rows.length; i++ ) {
            MechListData data = ((MechList) tblMechData.getModel()).Get(tblMechData.convertRowIndexToModel(rows[i]));
            BV += data.getBV();
            Cost += data.getCost();
            setTooltip( data );
        }

        txtSelected.setText(rows.length + " Units Selected for " + String.format("%,d", BV) + " BV and " + String.format("%,.2f", Cost) + " C-Bills");
    }

    private void setTooltip( MechListData data ) {
        spnMechTable.setToolTipText( data.getInfo() );
        txtInfo.setText(data.getInfo());
    }

    public void LoadList() {
        if (MechListPath.isEmpty()) {
            if ( MechListPath.isEmpty() && this.isVisible() ) {
                Media media = new Media();
                MechListPath = media.GetDirectorySelection(null);
                this.parent.Prefs.put("ListPath", MechListPath);
            }
        }

        if ( ! MechListPath.isEmpty() ) {

            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            list = new MechList(MechListPath, useIndex);

            if (list.Size() > 0) {
                setupList(list);
            }

            String displayPath = MechListPath;
            if (! MechListPath.isEmpty() ) {
                if (MechListPath.contains(File.separator)) {
                    displayPath = MechListPath.substring(0, 3) + "..." + MechListPath.substring(MechListPath.lastIndexOf(File.separator)) + "";
                }
            }
            useIndex = true;
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

        tblMechData.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblMechData.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblMechData.getColumnModel().getColumn(2).setPreferredWidth(30);
        tblMechData.getColumnModel().getColumn(3).setPreferredWidth(70);
        tblMechData.getColumnModel().getColumn(4).setPreferredWidth(90);
        tblMechData.getColumnModel().getColumn(5).setPreferredWidth(90);
        tblMechData.getColumnModel().getColumn(6).setPreferredWidth(60);
        tblMechData.getColumnModel().getColumn(7).setPreferredWidth(20);
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
        pnlUnits = new javax.swing.JPanel();
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
        jPanel1 = new javax.swing.JPanel();
        cmbMotive = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        cmbMechType = new javax.swing.JComboBox();
        jPanel13 = new javax.swing.JPanel();
        chkOmniOnly = new javax.swing.JCheckBox();
        jPanel15 = new javax.swing.JPanel();
        spnMechTable = new javax.swing.JScrollPane();
        tblMechData = new javax.swing.JTable();
        jPanel18 = new javax.swing.JPanel();
        btnOpenMech = new javax.swing.JButton();
        btnText = new javax.swing.JButton();
        lblLoading = new javax.swing.JLabel();
        txtSelected = new javax.swing.JLabel();
        btnOpenDir = new javax.swing.JButton();
        txtInfo = new javax.swing.JLabel();
        pnlRandom = new javax.swing.JPanel();
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
        jScrollPane5 = new javax.swing.JScrollPane();
        lstDirectories = new javax.swing.JList();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstOptions = new javax.swing.JList();
        pnlFSL = new javax.swing.JPanel();
        spnFSL = new javax.swing.JScrollPane();
        tblFSL = new javax.swing.JTable();
        jPanel17 = new javax.swing.JPanel();
        cmbFaction = new javax.swing.JComboBox();
        cmbType = new javax.swing.JComboBox();
        cmbSource = new javax.swing.JComboBox();
        cmbFSLEra = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstSelected = new javax.swing.JList();
        btnClearSelection = new javax.swing.JButton();
        btnClipboard = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        lstChosen = new javax.swing.JList();
        btnClearChosen = new javax.swing.JButton();
        btnAddUnits = new javax.swing.JButton();

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
                .addComponent(txtMinCost, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtMaxCost, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        txtName.setPreferredSize(new java.awt.Dimension(12, 20));
        txtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNameKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Motive Type"));

        cmbMotive.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Biped", "Quad" }));
        cmbMotive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMotiveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(cmbMotive, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cmbMotive, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Mech Type"));

        cmbMechType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BattleMech", "IndustrialMech", "Primitive BattleMech", "Primitive IndustrialMech" }));
        cmbMechType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMechTypeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(cmbMechType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cmbMechType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));

        chkOmniOnly.setText("Omnis Only");
        chkOmniOnly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOmniOnlyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chkOmniOnly))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(chkOmniOnly)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClearFilter))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(202, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel13, 0, 50, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnFilter)
                            .addComponent(btnClearFilter))))
                .addGap(1, 1, 1)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

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
        tblMechData.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                tblMechDataMouseMoved(evt);
            }
        });
        tblMechData.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblMechDataFocusGained(evt);
            }
        });
        tblMechData.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblMechDataKeyPressed(evt);
            }
        });
        spnMechTable.setViewportView(tblMechData);

        btnOpenMech.setText("Add Units");
        btnOpenMech.setEnabled(false);
        btnOpenMech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenMechActionPerformed(evt);
            }
        });

        btnText.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/document--plus.png"))); // NOI18N
        btnText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTextActionPerformed(evt);
            }
        });

        lblLoading.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLoading.setText("Loading Mechs....");
        lblLoading.setMaximumSize(new java.awt.Dimension(400, 14));

        txtSelected.setText("0 Units Selected");

        btnOpenDir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/folder-open-document.png"))); // NOI18N
        btnOpenDir.setToolTipText("Change Directory");
        btnOpenDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenDirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(txtSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 186, Short.MAX_VALUE)
                        .addComponent(lblLoading, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOpenDir, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnText, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOpenMech)
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblLoading, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSelected))
                        .addGap(1, 1, 1)
                        .addComponent(txtInfo))
                    .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnOpenMech, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnText))
                    .addComponent(btnOpenDir))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spnMechTable, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 865, Short.MAX_VALUE)
            .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addComponent(spnMechTable, javax.swing.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout pnlUnitsLayout = new javax.swing.GroupLayout(pnlUnits);
        pnlUnits.setLayout(pnlUnitsLayout);
        pnlUnitsLayout.setHorizontalGroup(
            pnlUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUnitsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlUnitsLayout.setVerticalGroup(
            pnlUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUnitsLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(26, 26, 26))
        );

        tbpSelections.addTab("Unit Selection", pnlUnits);

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
                .addContainerGap(58, Short.MAX_VALUE))
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

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Random Table Choices"));

        lstFiles.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstFiles.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstFilesValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(lstFiles);

        lstDirectories.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstDirectories.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstDirectoriesValueChanged(evt);
            }
        });
        jScrollPane5.setViewportView(lstDirectories);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Available Items"));

        lstOptions.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstOptions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstOptionsMouseClicked(evt);
            }
        });
        lstOptions.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstOptionsValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(lstOptions);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlRandomLayout = new javax.swing.GroupLayout(pnlRandom);
        pnlRandom.setLayout(pnlRandomLayout);
        pnlRandomLayout.setHorizontalGroup(
            pnlRandomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRandomLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlRandomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(390, 390, 390))
        );
        pnlRandomLayout.setVerticalGroup(
            pnlRandomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRandomLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRandomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlRandomLayout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tbpSelections.addTab("Random Selections", pnlRandom);

        tblFSL.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        spnFSL.setViewportView(tblFSL);

        cmbFaction.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbSource.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbFSLEra.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText("Faction");

        jLabel4.setText("Unit Type");

        jLabel6.setText("Source");

        jLabel7.setText("Era");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbFaction, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbType, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbSource, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(cmbFSLEra, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(197, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbFaction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbFSLEra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlFSLLayout = new javax.swing.GroupLayout(pnlFSL);
        pnlFSL.setLayout(pnlFSLLayout);
        pnlFSLLayout.setHorizontalGroup(
            pnlFSLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFSLLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFSLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(spnFSL, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 865, Short.MAX_VALUE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlFSLLayout.setVerticalGroup(
            pnlFSLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFSLLayout.createSequentialGroup()
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnFSL, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
                .addContainerGap())
        );

        tbpSelections.addTab("Faction Specific List", pnlFSL);

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnClearSelection)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
                        .addComponent(btnClipboard)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClipboard)
                    .addComponent(btnClearSelection)))
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Units"));

        lstChosen.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstChosenValueChanged(evt);
            }
        });
        lstChosen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lstChosenKeyPressed(evt);
            }
        });
        jScrollPane4.setViewportView(lstChosen);

        btnClearChosen.setText("Clear");
        btnClearChosen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearChosenActionPerformed(evt);
            }
        });

        btnAddUnits.setText("Add Units");
        btnAddUnits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddUnitsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(btnClearChosen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
                        .addComponent(btnAddUnits)))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddUnits)
                    .addComponent(btnClearChosen)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(tbpSelections, javax.swing.GroupLayout.PREFERRED_SIZE, 890, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(tbpSelections, javax.swing.GroupLayout.DEFAULT_SIZE, 723, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblMechDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMechDataMouseClicked
        if ( evt.getClickCount() == 2 ) {
            if ( tblMechData.getModel().getRowCount() < list.Size() ) {
                addChosen();
            } else {
                LoadMech();
            }
        } else {
            checkSelection();
        }
    }//GEN-LAST:event_tblMechDataMouseClicked

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if (list == null) LoadList();
    }//GEN-LAST:event_formWindowOpened

    private void btnOpenMechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenMechActionPerformed
        LoadMech();
    }//GEN-LAST:event_btnOpenMechActionPerformed

    private void Filter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Filter
        ListFilter filters = new ListFilter();

        if (cmbTech.getSelectedIndex() > 0) {filters.setTech(cmbTech.getSelectedItem().toString());}
        if (cmbEra.getSelectedIndex() > 0) {filters.setEra(cmbEra.getSelectedItem().toString());}
        if (cmbMotive.getSelectedIndex() > 0) {filters.setMotive(cmbMotive.getSelectedItem().toString());}
        if (cmbMechType.getSelectedIndex() > 0) {filters.setType(cmbMechType.getSelectedItem().toString());}
        if (chkOmniOnly.isSelected()) {filters.setIsOmni(true);}
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


        filtered = list.Filter(filters);
        setupList(filtered);
    }//GEN-LAST:event_Filter

    private void btnOpenDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenDirActionPerformed
        setMechListPath(parent.Prefs.get("ListPath", ""));
        FileSelector selector = new FileSelector();
        setMechListPath(selector.GetDirectorySelection(MechListPath));
        
        this.setVisible(true);
        parent.Prefs.put("ListPath", MechListPath);
        useIndex = false;
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
            String Path = BaseRUSPath + File.separator + RUSDirectory + File.separator + filename;
            //javax.swing.JOptionPane.showMessageDialog(this, Path);
            reader.Load( Path, rus);
            lstOptions.setModel(rus.getDisplay());
        } catch ( Exception e ) {

        }

    }//GEN-LAST:event_lstFilesValueChanged

    private void btnRollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRollActionPerformed
        lstSelected.setModel( rus.Generate( Integer.parseInt(spnSelections.getValue().toString()), Integer.parseInt( spnAddOn.getValue().toString()) ) );
}//GEN-LAST:event_btnRollActionPerformed

    private void tbpSelectionsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpSelectionsFocusGained
        //LoadRUSFiles(parent.Prefs.get("RUSPath", ""));
}//GEN-LAST:event_tbpSelectionsFocusGained

    private void btnClearSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearSelectionActionPerformed
        lstSelected.setModel(rus.ClearSelection());
}//GEN-LAST:event_btnClearSelectionActionPerformed

    private void btnClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClipboardActionPerformed
        String data = "";
        
        for (int i=0; i < lstSelected.getModel().getSize(); i++ ) {
            if ( !data.isEmpty() ) { data += Constants.NL; }
            data += lstSelected.getModel().getElementAt(i).toString();
        }
        
        java.awt.datatransfer.StringSelection export = new java.awt.datatransfer.StringSelection( data );
        java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents( export, this );
    }//GEN-LAST:event_btnClipboardActionPerformed

    private void lstSelectedKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lstSelectedKeyPressed
        if ( lstSelected.getSelectedValues().length > 0 ) {
            if ( evt.getKeyCode() == KeyEvent.VK_DELETE ) {
                DefaultListModel model = (DefaultListModel) lstSelected.getModel();
                for (int i=lstSelected.getSelectedValues().length-1; i >= 0; i-- ) {
                    model.removeElement((Object) lstSelected.getSelectedValues()[i]);
                }
                lstSelected.clearSelection();
            }
        }
    }//GEN-LAST:event_lstSelectedKeyPressed

    private void lstSelectedValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstSelectedValueChanged
        if (( lstSelected.getSelectedValues().length > 0 )) {
            String Name = "", Model = "";
            String Item = ((Object) lstSelected.getSelectedValues()[0]).toString();
            if ( Item.contains(" ") ) {
                String[] parts = Item.split(" ");
                Name = parts[1].trim();
                Model = parts[0].trim();
                if ( parts.length == 3 ) { Name += " " + parts[2].trim(); }

                txtName.setText(Name + " " + Model);
                Filter(null);

                if ( filtered.Size() == 0 ) {
                    txtName.setText(Name); 
                    Filter(null);
                }
                tbpSelections.setSelectedComponent(pnlUnits);
            }
        }
    }//GEN-LAST:event_lstSelectedValueChanged

    private void cmbClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbClassActionPerformed
        Filter(evt);
    }//GEN-LAST:event_cmbClassActionPerformed

    private void lstChosenValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstChosenValueChanged
        // TODO add your handling code here:
}//GEN-LAST:event_lstChosenValueChanged

    private void lstChosenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lstChosenKeyPressed
        if ( lstChosen.getSelectedValues().length > 0 ) {
            if ( evt.getKeyCode() == KeyEvent.VK_DELETE ) {
                for (int i=0; i < lstChosen.getSelectedIndices().length; i++ ) {
                    //DefaultListModel model = (DefaultListModel) lstChosen.getModel();
                    //model.removeElementAt(lstChosen.getSelectedIndices()[i]);
                    chosen.Remove(chosen.Get(lstChosen.getSelectedIndices()[i]));
                }
                loadChosen();
            }
        }
}//GEN-LAST:event_lstChosenKeyPressed

    private void btnClearChosenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearChosenActionPerformed
        chosen.RemoveAll();
        loadChosen();
}//GEN-LAST:event_btnClearChosenActionPerformed

    private void btnAddUnitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddUnitsActionPerformed
        if ( chosen.Size() > 0 ) {
            for ( int i=0; i < chosen.Size(); i++ ) {
                force.AddUnit(new Unit(chosen.Get(i)));
            }
            chosen.RemoveAll();
            loadChosen();
            this.setVisible(false);
        }
}//GEN-LAST:event_btnAddUnitsActionPerformed

    private void btnTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTextActionPerformed
        TXTWriter out = new TXTWriter();
        FileSelector fs = new FileSelector();
        String dir = "";
        dir = fs.GetDirectorySelection(parent.Prefs.get("ListDirectory", ""));
        if ( dir.isEmpty() ) { return; }

        parent.Prefs.put("ListDirectory", dir);
        try {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            out.WriteList(dir + File.separator + "MechListing.txt", list);
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            javax.swing.JOptionPane.showMessageDialog(this, "Mech List output to " + dir);
        } catch (IOException ex) {
            //do nothing
            javax.swing.JOptionPane.showMessageDialog(this, "Unable to output list\n" + ex.getMessage() );
        }
}//GEN-LAST:event_btnTextActionPerformed

    private void lstDirectoriesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstDirectoriesValueChanged
        if (( lstDirectories.getSelectedValues().length > 0 )) {
            String Name = ((Object) lstDirectories.getSelectedValues()[0]).toString();
            RUSDirectory = Name;
            RUSPath = BaseRUSPath + File.separator + Name;
            LoadRUSFiles(RUSPath);
        }
    }//GEN-LAST:event_lstDirectoriesValueChanged

    private void lstOptionsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstOptionsValueChanged

    }//GEN-LAST:event_lstOptionsValueChanged

    private void lstOptionsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstOptionsMouseClicked
        if ( evt.getClickCount() == 2 ) {
            String Name = ((Object) lstOptions.getSelectedValue()).toString();
            if ( !Name.isEmpty() ) {
                Name = Name.split(",")[0];
            }
            lstSelected.setModel(rus.Add(Name));
            lstOptions.clearSelection();
        }
    }//GEN-LAST:event_lstOptionsMouseClicked

    private void cmbMotiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMotiveActionPerformed
        Filter(evt);
    }//GEN-LAST:event_cmbMotiveActionPerformed

    private void cmbMechTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMechTypeActionPerformed
        Filter(evt);
    }//GEN-LAST:event_cmbMechTypeActionPerformed

    private void chkOmniOnlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOmniOnlyActionPerformed
        Filter(evt);
    }//GEN-LAST:event_chkOmniOnlyActionPerformed

    private void txtNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyReleased
        Filter(null);
    }//GEN-LAST:event_txtNameKeyReleased

    private void tblMechDataMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMechDataMouseMoved
        setTooltip( (MechListData) ((MechList) tblMechData.getModel()).Get(tblMechData.convertRowIndexToModel(tblMechData.rowAtPoint(evt.getPoint()))) );
    }//GEN-LAST:event_tblMechDataMouseMoved

    private void tblMechDataFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblMechDataFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_tblMechDataFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddUnits;
    private javax.swing.JButton btnClearChosen;
    private javax.swing.JButton btnClearFilter;
    private javax.swing.JButton btnClearSelection;
    private javax.swing.JButton btnClipboard;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnOpenDir;
    private javax.swing.JButton btnOpenMech;
    private javax.swing.JButton btnRoll;
    private javax.swing.JButton btnText;
    private javax.swing.JCheckBox chkOmniOnly;
    private javax.swing.JComboBox cmbClass;
    private javax.swing.JComboBox cmbEra;
    private javax.swing.JComboBox cmbFSLEra;
    private javax.swing.JComboBox cmbFaction;
    private javax.swing.JComboBox cmbMechType;
    private javax.swing.JComboBox cmbMotive;
    private javax.swing.JComboBox cmbSource;
    private javax.swing.JComboBox cmbTech;
    private javax.swing.JComboBox cmbType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
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
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel lblLoading;
    private javax.swing.JList lstChosen;
    private javax.swing.JList lstDirectories;
    private javax.swing.JList lstFiles;
    private javax.swing.JList lstOptions;
    private javax.swing.JList lstSelected;
    private javax.swing.JPanel pnlFSL;
    private javax.swing.JPanel pnlRandom;
    private javax.swing.JPanel pnlUnits;
    private javax.swing.JSpinner spnAddOn;
    private javax.swing.JScrollPane spnFSL;
    private javax.swing.JScrollPane spnMechTable;
    private javax.swing.JSpinner spnSelections;
    private javax.swing.JTable tblFSL;
    private javax.swing.JTable tblMechData;
    private javax.swing.JTabbedPane tbpSelections;
    private javax.swing.JLabel txtInfo;
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
