/*
 * frmBase.java
 *
 * Created on April 6th, 2009, 9:56 AM
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
import BFB.Common.CommonTools;
import BFB.IO.*;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.print.*;
import java.io.*;
import java.util.Vector;
import java.util.logging.*;
import java.util.prefs.*;
import javax.swing.ImageIcon;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import ssw.components.Mech;
import ssw.print.Printer;

public class frmBase extends javax.swing.JFrame {
    public Force topForce = new Force();
    public Force bottomForce = new Force();
    public Preferences Prefs;
    private dlgOpen dOpen = new dlgOpen(this, true);

    private TableModelListener ForceChanged = new TableModelListener() {

        public void tableChanged(TableModelEvent e) {
            Refresh();
        }
    };

    public frmBase() {
        initComponents();
        Prefs = Preferences.userNodeForPackage(this.getClass());

        //Clear tracking data
        Prefs.put("CurrentBFBFile", "");

        Refresh();
    }

    public void Refresh() {      
        topForce.setupTable(tblTop);
        bottomForce.setupTable(tblBottom);

        topForce.addTableModelListener(ForceChanged);
        bottomForce.addTableModelListener(ForceChanged);

        topForce.OpForSize = bottomForce.Units.size();
        bottomForce.OpForSize = topForce.Units.size();
        
        setLogo( lblUnitLogoTop, new File(topForce.LogoPath) );
        setLogo( lblUnitLogoBottom, new File(bottomForce.LogoPath) );

        txtUnitNameTop.setText(topForce.ForceName);
        txtUnitNameBottom.setText(bottomForce.ForceName);

        lblForceMod.setText( String.format( "%1$,.2f", CommonTools.GetForceSizeMultiplier( topForce.Units.size(), bottomForce.Units.size() )) );

        topForce.OpForSize = bottomForce.Units.size();
        bottomForce.OpForSize = topForce.Units.size();
        
        lblUnitsTop.setText(topForce.Units.size()+"");
        lblTonnageTop.setText( String.format("%1$,.0f", topForce.TotalTonnage) );
        lblBaseBVTop.setText( String.format("%1$,.0f", topForce.TotalBaseBV) );
//        lblC3BVTop.setText( String.format("%1$,.0f", topForce.TotalC3BV) );
//        lblModBVTop.setText( String.format("%1$,.0f", topForce.TotalModifierBV) );
//        lblForceBVTop.setText( String.format("%1$,.0f", topForce.TotalAdjustedBV) );
        lblTotalBVTop.setText( String.format("%1$,.0f", topForce.TotalForceBV) );

        lblUnitsBottom.setText(bottomForce.Units.size()+"");
        lblTonnageBottom.setText( String.format("%1$,.0f", bottomForce.TotalTonnage) );
        lblBaseBVBottom.setText( String.format("%1$,.0f", bottomForce.TotalBaseBV) );
//        lblC3BVBottom.setText( String.format("%1$,.0f", bottomForce.TotalC3BV) );
//        lblModBVBottom.setText( String.format("%1$,.0f", bottomForce.TotalModifierBV) );
//        lblForceBVBottom.setText( String.format("%1$,.0f", bottomForce.TotalAdjustedBV) );
        lblTotalBVBottom.setText( String.format("%1$,.0f", bottomForce.TotalForceBV) );
    }

    private void updateLogo( javax.swing.JLabel lblLogo, Force force ) {
        FileSelector fs = new FileSelector();
        File Logo = fs.SelectImage(Prefs.get("LastOpenLogo", ""), "Select Logo");
        try {
            force.LogoPath = Logo.getCanonicalPath();
            setLogo(lblLogo, Logo);
        } catch (IOException ex) {
            //do nothing
        }
    }

    private void setLogo( javax.swing.JLabel lblLogo, File Logo ) {
        if ( Logo != null ) {
            try {
               Prefs.put("LastOpenLogo", Logo.getPath());
               ImageIcon icon = new ImageIcon(Logo.getPath());

                if( icon == null ) { return; }

                // See if we need to scale
                int h = icon.getIconHeight();
                int w = icon.getIconWidth();
                if ( w > lblLogo.getWidth() || h > lblLogo.getHeight() ) {
                    if ( w > h ) { // resize based on width
                        icon = new ImageIcon(icon.getImage().
                            getScaledInstance(lblLogo.getWidth(), -1, Image.SCALE_DEFAULT));
                    } else { // resize based on height
                        icon = new ImageIcon(icon.getImage().
                            getScaledInstance(-1, lblLogo.getHeight(), Image.SCALE_DEFAULT));
                    }
                }

                lblLogo.setIcon(icon);
            } catch ( Exception e ) {

            }
        }
    }

    private void editUnit( javax.swing.JTable Table, Force force ) {
        Unit u = (Unit) force.Units.get(Table.convertRowIndexToModel(Table.getSelectedRow()));
        dlgUnit dUnit = new dlgUnit(this, true, u);
        dUnit.setLocationRelativeTo(this);
        dUnit.setVisible(true);
        Refresh();
    }

    private void removeUnits( javax.swing.JTable Table, Force force ) {
         int[] rows = Table.getSelectedRows();
         for (int i=0; i <= rows.length; i++ ) {
             force.RemoveUnit((Unit) force.Units.get(Table.convertRowIndexToModel(rows[i])));
         }
    }

    private void OpenDialog( Force force ) {
        dOpen.setForce(force);
        dOpen.setLocationRelativeTo(this);
        dOpen.setVisible(true);
    }

    public void setScenario( String scenario ) {
        txtScenarioName.setText(scenario);
    }

    public void openForce( Force force ) {
        FileSelector openFile = new FileSelector();
        File forceFile = openFile.SelectFile(Prefs.get("LastOpenUnit", ""), "force", "Load Force");

        if (forceFile != null) {
            XMLReader reader = new XMLReader();
            try {
                this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                reader.ReadUnit( force, forceFile.getCanonicalPath() );
                force.RefreshBV();
                Refresh();

               Prefs.put("LastOpenUnit", forceFile.getCanonicalPath());
               this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } catch (Exception e) {
               javax.swing.JOptionPane.showMessageDialog( this, "Issue loading file:\n " + e.getMessage() );
               return;
            }
        }
    }

    public void saveForce( Force force ) {
        if ( ! force.isSaveable() ) {
            javax.swing.JOptionPane.showMessageDialog(this, "Please enter a unit name and at least one unit before saving.");
            return;
        }
        FileSelector fs = new FileSelector();
        String dirPath = fs.GetDirectorySelection(Prefs.get("LastOpenUnit", ""));
        if ( dirPath.isEmpty() ) { return;}

        XMLWriter write = new XMLWriter();
        try {
            String filename = dirPath + File.separator + CommonTools.FormatFileName(force.ForceName) + ".force";
            write.SerializeForce(force, filename);
            javax.swing.JOptionPane.showMessageDialog( this, "Force written to " + filename );
        } catch (IOException ex) {
            Logger.getLogger(frmBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnLoad = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnPrint = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnMULExport = new javax.swing.JButton();
        lblScenarioName = new javax.swing.JLabel();
        txtScenarioName = new javax.swing.JTextField();
        pnlTop = new javax.swing.JPanel();
        spnTop = new javax.swing.JScrollPane();
        tblTop = new javax.swing.JTable();
        btnAddTop = new javax.swing.JButton();
        btnEditTop = new javax.swing.JButton();
        btnDeleteTop = new javax.swing.JButton();
        lblUnitNameTop = new javax.swing.JLabel();
        txtUnitNameTop = new javax.swing.JTextField();
        lblUnitLogoTop = new javax.swing.JLabel();
        lblTotalBVTop = new javax.swing.JLabel();
        lblUnitsTop = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblTonnageTop = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblBaseBVTop = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tlbTop = new javax.swing.JToolBar();
        btnOpenTop = new javax.swing.JButton();
        btnSaveTop = new javax.swing.JButton();
        pnlBottom = new javax.swing.JPanel();
        spnBottom = new javax.swing.JScrollPane();
        tblBottom = new javax.swing.JTable();
        btnAddBottom = new javax.swing.JButton();
        btnEditBottom = new javax.swing.JButton();
        btnDeleteBottom = new javax.swing.JButton();
        lblUnitNameBottom = new javax.swing.JLabel();
        txtUnitNameBottom = new javax.swing.JTextField();
        lblUnitLogoBottom = new javax.swing.JLabel();
        lblTotalBVBottom = new javax.swing.JLabel();
        lblUnitsBottom = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblTonnageBottom = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblBaseBVBottom = new javax.swing.JLabel();
        tlbBottom = new javax.swing.JToolBar();
        btnOpenBottom = new javax.swing.JButton();
        btnSaveBottom = new javax.swing.JButton();
        chkUseForceModifier = new javax.swing.JCheckBox();
        lblForceMod = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        mnuNew = new javax.swing.JMenuItem();
        mnuSave = new javax.swing.JMenuItem();
        mnuLoad = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        mnuPrintAll = new javax.swing.JMenuItem();
        mnuPrintForce = new javax.swing.JMenuItem();
        mnuPrintUnits = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        mnuExit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        mnuDesignBattleMech = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        mnuAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Battletech Force Balancer");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/page_new.gif"))); // NOI18N
        btnNew.setToolTipText("New Force");
        btnNew.setFocusable(false);
        btnNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew);

        btnLoad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/folder.gif"))); // NOI18N
        btnLoad.setToolTipText("Open Force");
        btnLoad.setFocusable(false);
        btnLoad.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLoad.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadActionPerformed(evt);
            }
        });
        jToolBar1.add(btnLoad);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/action_save.gif"))); // NOI18N
        btnSave.setToolTipText("Save Force");
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);
        jToolBar1.add(jSeparator1);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/action_print.gif"))); // NOI18N
        btnPrint.setToolTipText("Print All");
        btnPrint.setFocusable(false);
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/action_print_batch.gif"))); // NOI18N
        jButton7.setToolTipText("Print Designs");
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton7);
        jToolBar1.add(jSeparator4);

        btnMULExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/list_packages.gif"))); // NOI18N
        btnMULExport.setToolTipText("Export Forces to MUL");
        btnMULExport.setFocusable(false);
        btnMULExport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMULExport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMULExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMULExportActionPerformed(evt);
            }
        });
        jToolBar1.add(btnMULExport);

        lblScenarioName.setText("Scenario / Event Name: ");

        txtScenarioName.setToolTipText("Enter the name of the scenario or event");

        pnlTop.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Primary Force Listing", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Trebuchet MS", 1, 12), new java.awt.Color(0, 51, 204))); // NOI18N

        tblTop.setModel(new javax.swing.table.DefaultTableModel(
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
        tblTop.setRowMargin(2);
        tblTop.setShowVerticalLines(false);
        tblTop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTopMouseClicked(evt);
            }
        });
        spnTop.setViewportView(tblTop);

        btnAddTop.setText("Add");
        btnAddTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddTopActionPerformed(evt);
            }
        });

        btnEditTop.setText("Edit");
        btnEditTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditTopActionPerformed(evt);
            }
        });

        btnDeleteTop.setText("Delete");
        btnDeleteTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteTopActionPerformed(evt);
            }
        });

        lblUnitNameTop.setText("Unit Name:");

        txtUnitNameTop.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtUnitNameTopFocusLost(evt);
            }
        });
        txtUnitNameTop.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtUnitNameTopKeyTyped(evt);
            }
        });

        lblUnitLogoTop.setToolTipText("Unit or Faction Logo, Double Click to change");
        lblUnitLogoTop.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblUnitLogoTop.setOpaque(true);
        lblUnitLogoTop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblUnitLogoTopMouseClicked(evt);
            }
        });

        lblTotalBVTop.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        lblTotalBVTop.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalBVTop.setText("0,000 BV");

        lblUnitsTop.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUnitsTop.setText("0");

        jLabel2.setText("Units");

        lblTonnageTop.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTonnageTop.setText("0");

        jLabel4.setText("Tons");

        lblBaseBVTop.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBaseBVTop.setText("0,000");

        jLabel6.setText("BV");

        tlbTop.setFloatable(false);
        tlbTop.setRollover(true);

        btnOpenTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/folder.gif"))); // NOI18N
        btnOpenTop.setFocusable(false);
        btnOpenTop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpenTop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenTopActionPerformed(evt);
            }
        });
        tlbTop.add(btnOpenTop);

        btnSaveTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/action_save.gif"))); // NOI18N
        btnSaveTop.setFocusable(false);
        btnSaveTop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSaveTop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSaveTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveTopActionPerformed(evt);
            }
        });
        tlbTop.add(btnSaveTop);

        javax.swing.GroupLayout pnlTopLayout = new javax.swing.GroupLayout(pnlTop);
        pnlTop.setLayout(pnlTopLayout);
        pnlTopLayout.setHorizontalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTopLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblUnitLogoTop, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnEditTop, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDeleteTop, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAddTop, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spnTop, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblUnitsTop, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addGap(307, 307, 307)
                        .addComponent(lblTonnageTop, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(lblBaseBVTop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 177, Short.MAX_VALUE)
                        .addComponent(lblTotalBVTop, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addComponent(lblUnitNameTop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtUnitNameTop, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 390, Short.MAX_VALUE)
                        .addComponent(tlbTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlTopLayout.setVerticalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTopLayout.createSequentialGroup()
                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlTopLayout.createSequentialGroup()
                        .addComponent(lblUnitLogoTop, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddTop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditTop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeleteTop))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlTopLayout.createSequentialGroup()
                        .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblUnitNameTop)
                                .addComponent(txtUnitNameTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tlbTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnTop, 0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUnitsTop)
                    .addComponent(jLabel2)
                    .addComponent(lblTonnageTop)
                    .addComponent(jLabel4)
                    .addComponent(lblBaseBVTop)
                    .addComponent(jLabel6)
                    .addComponent(lblTotalBVTop))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlBottom.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Secondary Force Listing", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Trebuchet MS", 1, 12), new java.awt.Color(0, 51, 204))); // NOI18N

        tblBottom.setModel(new javax.swing.table.DefaultTableModel(
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
        tblBottom.setRowMargin(2);
        tblBottom.setShowVerticalLines(false);
        tblBottom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBottomMouseClicked(evt);
            }
        });
        spnBottom.setViewportView(tblBottom);

        btnAddBottom.setText("Add");
        btnAddBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddBottomActionPerformed(evt);
            }
        });

        btnEditBottom.setText("Edit");
        btnEditBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditBottomActionPerformed(evt);
            }
        });

        btnDeleteBottom.setText("Delete");
        btnDeleteBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteBottomActionPerformed(evt);
            }
        });

        lblUnitNameBottom.setText("Unit Name:");

        txtUnitNameBottom.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtUnitNameBottomFocusLost(evt);
            }
        });
        txtUnitNameBottom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtUnitNameBottomKeyTyped(evt);
            }
        });

        lblUnitLogoBottom.setToolTipText("Unit or Faction Logo, Double Click to change");
        lblUnitLogoBottom.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblUnitLogoBottom.setOpaque(true);
        lblUnitLogoBottom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblUnitLogoBottomMouseClicked(evt);
            }
        });

        lblTotalBVBottom.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        lblTotalBVBottom.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalBVBottom.setText("0,000 BV");

        lblUnitsBottom.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUnitsBottom.setText("0");

        jLabel3.setText("Units");

        lblTonnageBottom.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTonnageBottom.setText("0");

        jLabel5.setText("Tons");

        jLabel7.setText("BV");

        lblBaseBVBottom.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBaseBVBottom.setText("0,000");

        tlbBottom.setFloatable(false);
        tlbBottom.setRollover(true);

        btnOpenBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/folder.gif"))); // NOI18N
        btnOpenBottom.setFocusable(false);
        btnOpenBottom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpenBottom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenBottomActionPerformed(evt);
            }
        });
        tlbBottom.add(btnOpenBottom);

        btnSaveBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/action_save.gif"))); // NOI18N
        btnSaveBottom.setFocusable(false);
        btnSaveBottom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSaveBottom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSaveBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveBottomActionPerformed(evt);
            }
        });
        tlbBottom.add(btnSaveBottom);

        javax.swing.GroupLayout pnlBottomLayout = new javax.swing.GroupLayout(pnlBottom);
        pnlBottom.setLayout(pnlBottomLayout);
        pnlBottomLayout.setHorizontalGroup(
            pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBottomLayout.createSequentialGroup()
                .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBottomLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblUnitLogoBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnEditBottom, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnDeleteBottom, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnAddBottom, javax.swing.GroupLayout.Alignment.TRAILING)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlBottomLayout.createSequentialGroup()
                                .addComponent(lblUnitNameBottom)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtUnitNameBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 390, Short.MAX_VALUE)
                                .addComponent(tlbBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(spnBottom, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBottomLayout.createSequentialGroup()
                        .addGap(109, 109, 109)
                        .addComponent(lblUnitsBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addGap(313, 313, 313)
                        .addComponent(lblTonnageBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(lblBaseBVBottom)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 172, Short.MAX_VALUE)
                        .addComponent(lblTotalBVBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlBottomLayout.setVerticalGroup(
            pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBottomLayout.createSequentialGroup()
                .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBottomLayout.createSequentialGroup()
                        .addComponent(lblUnitLogoBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddBottom)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditBottom)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeleteBottom))
                    .addGroup(pnlBottomLayout.createSequentialGroup()
                        .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblUnitNameBottom)
                                .addComponent(txtUnitNameBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tlbBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnBottom, 0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUnitsBottom)
                    .addComponent(jLabel3)
                    .addComponent(lblTonnageBottom)
                    .addComponent(jLabel5)
                    .addComponent(lblBaseBVBottom)
                    .addComponent(jLabel7)
                    .addComponent(lblTotalBVBottom))
                .addContainerGap())
        );

        chkUseForceModifier.setSelected(true);
        chkUseForceModifier.setText("Use Force Size Modifier");
        chkUseForceModifier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUseForceModifierActionPerformed(evt);
            }
        });

        lblForceMod.setText("0.00");

        jMenu1.setText("File");

        mnuNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        mnuNew.setText("New");
        mnuNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNewActionPerformed(evt);
            }
        });
        jMenu1.add(mnuNew);

        mnuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        mnuSave.setText("Save");
        mnuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaveActionPerformed(evt);
            }
        });
        jMenu1.add(mnuSave);

        mnuLoad.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        mnuLoad.setText("Load");
        mnuLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLoadActionPerformed(evt);
            }
        });
        jMenu1.add(mnuLoad);
        jMenu1.add(jSeparator2);

        mnuPrintAll.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        mnuPrintAll.setText("Print All");
        mnuPrintAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintAllActionPerformed(evt);
            }
        });
        jMenu1.add(mnuPrintAll);

        mnuPrintForce.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        mnuPrintForce.setText("Print Force");
        mnuPrintForce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintForceActionPerformed(evt);
            }
        });
        jMenu1.add(mnuPrintForce);

        mnuPrintUnits.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        mnuPrintUnits.setText("Print Units");
        mnuPrintUnits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintUnitsActionPerformed(evt);
            }
        });
        jMenu1.add(mnuPrintUnits);
        jMenu1.add(jSeparator3);

        mnuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        mnuExit.setText("Exit");
        mnuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExitActionPerformed(evt);
            }
        });
        jMenu1.add(mnuExit);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Designs");

        mnuDesignBattleMech.setText("BattleMech");
        mnuDesignBattleMech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDesignBattleMechActionPerformed(evt);
            }
        });
        jMenu2.add(mnuDesignBattleMech);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("About");

        mnuAbout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        mnuAbout.setText("Battletech Force Balancer");
        mnuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAboutActionPerformed(evt);
            }
        });
        jMenu3.add(mnuAbout);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 891, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblScenarioName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtScenarioName, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkUseForceModifier)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblForceMod)
                .addContainerGap(292, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlTop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlBottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblScenarioName)
                    .addComponent(txtScenarioName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUseForceModifier)
                    .addComponent(lblForceMod))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlBottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtUnitNameTopKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUnitNameTopKeyTyped
    }//GEN-LAST:event_txtUnitNameTopKeyTyped

    private void txtUnitNameBottomKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUnitNameBottomKeyTyped
    }//GEN-LAST:event_txtUnitNameBottomKeyTyped

    private void mnuLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLoadActionPerformed
        FileSelector openFile = new FileSelector();
        File forceFile = openFile.SelectFile(Prefs.get("LastOpenBFBDirectory", ""), "bfb", "Load Force List");

        if (forceFile != null) {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            XMLReader reader = new XMLReader();
            try {
               reader.ReadFile(this, forceFile.getCanonicalPath());

               Prefs.put("LastOpenBFBDirectory", forceFile.getCanonicalPath().replace(forceFile.getName(), ""));
               Prefs.put("LastOpenBFBFile", forceFile.getName());
               Prefs.put("CurrentBFBFile", forceFile.getPath());
            } catch (Exception e) {
               javax.swing.JOptionPane.showMessageDialog( this, "Issue loading file:\n " + e.getMessage() );
               return;
            }
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
}//GEN-LAST:event_mnuLoadActionPerformed

    private void lblUnitLogoBottomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblUnitLogoBottomMouseClicked
        if ( evt.getClickCount() == 2 ) { updateLogo(lblUnitLogoBottom, bottomForce); }
    }//GEN-LAST:event_lblUnitLogoBottomMouseClicked

    private void lblUnitLogoTopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblUnitLogoTopMouseClicked
        if ( evt.getClickCount() == 2 ) { updateLogo(lblUnitLogoTop, topForce); }
    }//GEN-LAST:event_lblUnitLogoTopMouseClicked

    private void btnAddTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddTopActionPerformed
        OpenDialog(topForce);
    }//GEN-LAST:event_btnAddTopActionPerformed

    private void btnAddBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBottomActionPerformed
        OpenDialog(bottomForce);
    }//GEN-LAST:event_btnAddBottomActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            Prefs.exportNode(new FileOutputStream("preferences.xml"));
        } catch (IOException ex) {
            Logger.getLogger(frmBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BackingStoreException ex) {
            Logger.getLogger(frmBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        if ((topForce.isDirty) || (bottomForce.isDirty)) {
            switch (javax.swing.JOptionPane.showConfirmDialog(this, "Would you like to save your changes?")) {
                case javax.swing.JOptionPane.YES_OPTION:
                    //this.mnuSaveActionPerformed(evt);
                case javax.swing.JOptionPane.NO_OPTION:
                    dOpen.dispose();
                    this.dispose();
            }
        } else {
            dOpen.dispose();
            this.dispose();
        }
    }//GEN-LAST:event_formWindowClosing

    private void txtUnitNameTopFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUnitNameTopFocusLost
        topForce.ForceName = txtUnitNameTop.getText();
    }//GEN-LAST:event_txtUnitNameTopFocusLost

    private void txtUnitNameBottomFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUnitNameBottomFocusLost
        bottomForce.ForceName = txtUnitNameBottom.getText();
    }//GEN-LAST:event_txtUnitNameBottomFocusLost

    private void mnuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExitActionPerformed
        formWindowClosing(null);
    }//GEN-LAST:event_mnuExitActionPerformed

    private void mnuNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNewActionPerformed
        Prefs.put("CurrentBFBFile", "");
        this.topForce.Clear();
        this.bottomForce.Clear();
        this.txtScenarioName.setText("");
        this.Refresh();
    }//GEN-LAST:event_mnuNewActionPerformed

    private void mnuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveActionPerformed
        if ( txtScenarioName.getText().isEmpty() ) {
            javax.swing.JOptionPane.showMessageDialog(this, "Please enter a scenario name before saving.");
            return;
        }
        
        if ( !topForce.isSaveable() || !bottomForce.isSaveable() ) {
            javax.swing.JOptionPane.showMessageDialog(this, "Please enter a force name and at least one unit in each list before saving.");
            return;
        }
        
        try {
            File file;
            if ( !Prefs.get("CurrentBFBFile", "").isEmpty() ) {
                file = new File(Prefs.get("CurrentBFBFile", ""));
            } else {
                FileSelector selector = new FileSelector();
                file = selector.SelectFile(txtScenarioName.getText() + ".bfb", "bfb", "Save");
                if (file == null) {
                    return;
                }
            }
            String filename = file.getCanonicalPath();
            if ( ! filename.endsWith(".bfb") ) { filename += ".bfb";}

            XMLWriter write = new XMLWriter(txtScenarioName.getText(), this.topForce, this.bottomForce);
            write.WriteXML(filename);
            Prefs.put("LastOpenBFBFile", filename);
            Prefs.put("CurrentBFBFile", filename);
            javax.swing.JOptionPane.showMessageDialog(this, "Forces saved to " + filename);
        } catch (java.io.IOException e) {
            javax.swing.JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_mnuSaveActionPerformed

    private void mnuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAboutActionPerformed
        dlgAbout About = new dlgAbout();
        About.setTitle("About Battletech Force Balancer");
        About.setLocationRelativeTo(this);
        About.setVisible(true);
}//GEN-LAST:event_mnuAboutActionPerformed

    private void mnuPrintForceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintForceActionPerformed
       PrinterJob job = PrinterJob.getPrinterJob();
       PrintSheet p = new PrintSheet(this, 576, 756);
       Paper paper = new Paper();
       paper.setImageableArea(18, 18, 576, 756 );
       PageFormat page = new PageFormat();
       page.setPaper( paper );
       job.setPrintable( p, page );
       job.setJobName(txtScenarioName.getText() + " Force List");
       boolean DoPrint = job.printDialog();
       if( DoPrint ) {
           try {
               job.print();
           } catch( PrinterException e ) {
               System.err.println( e.getMessage() );
               System.out.println( e.getStackTrace() );
           }
      }
}//GEN-LAST:event_mnuPrintForceActionPerformed

    private void mnuPrintUnitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintUnitsActionPerformed
        Vector forces = new Vector();
        forces.add(topForce);
        forces.add(bottomForce);

        for (int f = 0; f <= forces.size()-1; f++){
            Printer printer = new Printer();
            Force printForce = (Force) forces.get(f);

            printer.setLogoPath(printForce.LogoPath);
            printer.setJobName(printForce.ForceName);

            for (int i = 0; i < printForce.Units.size(); ++i) {
                Unit u = (Unit) printForce.Units.get(i);
                u.LoadMech();
                Mech m = u.m;
                if (m != null) {
                    printer.AddMech(m, u.Mechwarrior, u.Gunnery, u.Piloting, true, true, true);
                }
            }

            printer.Print();

        }
    }//GEN-LAST:event_mnuPrintUnitsActionPerformed

    private void mnuDesignBattleMechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDesignBattleMechActionPerformed
        ssw.gui.frmMain SSW = new ssw.gui.frmMain();
        SSW.setLocationRelativeTo(null);
        SSW.setVisible(true);
    }//GEN-LAST:event_mnuDesignBattleMechActionPerformed

    private void mnuPrintAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintAllActionPerformed
        mnuPrintForceActionPerformed(evt);
        mnuPrintUnitsActionPerformed(evt);
}//GEN-LAST:event_mnuPrintAllActionPerformed

    private void btnLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadActionPerformed
        mnuLoadActionPerformed(evt);
}//GEN-LAST:event_btnLoadActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        mnuNewActionPerformed(evt);
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        mnuSaveActionPerformed(evt);
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        mnuPrintAllActionPerformed(evt);
}//GEN-LAST:event_btnPrintActionPerformed

    private void btnDeleteTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteTopActionPerformed
        removeUnits( tblTop, topForce );
    }//GEN-LAST:event_btnDeleteTopActionPerformed

    private void btnDeleteBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteBottomActionPerformed
        removeUnits( tblBottom, bottomForce );
    }//GEN-LAST:event_btnDeleteBottomActionPerformed

    private void btnEditTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditTopActionPerformed
        editUnit(tblTop, topForce);
    }//GEN-LAST:event_btnEditTopActionPerformed

    private void btnEditBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditBottomActionPerformed
        editUnit(tblBottom, bottomForce);
    }//GEN-LAST:event_btnEditBottomActionPerformed

    private void tblTopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTopMouseClicked
        if ( evt.getClickCount() == 2 ) {
            editUnit(tblTop, topForce);
            topForce.RefreshBV();
        }
    }//GEN-LAST:event_tblTopMouseClicked

    private void tblBottomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBottomMouseClicked
        if ( evt.getClickCount() == 2 ) {
            editUnit(tblBottom, bottomForce);
            bottomForce.RefreshBV();
        }
    }//GEN-LAST:event_tblBottomMouseClicked

    private void btnMULExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMULExportActionPerformed
        MULWriter mw = new MULWriter();
        FileSelector fs = new FileSelector();
        String dir = "";
        dir = fs.GetDirectorySelection(Prefs.get("MULDirectory", ""));
        if ( dir.isEmpty() ) { return; }

        Prefs.put("MULDirectory", dir);
        mw.setForce(topForce);
        try {
            mw.WriteXML( dir + topForce.ForceName );
        } catch (IOException ex) {
            //do nothing
            javax.swing.JOptionPane.showMessageDialog(this, "Unable to save " + topForce.ForceName + "\n" + ex.getMessage() );
        }

        mw.setForce(bottomForce);
        try {
            mw.WriteXML( dir + bottomForce.ForceName );
        } catch ( IOException ex ) {
            //do nothing
            javax.swing.JOptionPane.showMessageDialog(this, "Unable to save " + bottomForce.ForceName + "\n" + ex.getMessage() );
        }

        javax.swing.JOptionPane.showMessageDialog(this, "Your forces have been exported to " + dir);
}//GEN-LAST:event_btnMULExportActionPerformed

    private void chkUseForceModifierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUseForceModifierActionPerformed
        lblForceMod.setVisible( chkUseForceModifier.isSelected() );
        topForce.useUnevenForceMod = chkUseForceModifier.isSelected();
        topForce.RefreshBV();
        bottomForce.useUnevenForceMod = chkUseForceModifier.isSelected();
        bottomForce.RefreshBV();
        Refresh();
    }//GEN-LAST:event_chkUseForceModifierActionPerformed

    private void btnOpenBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenBottomActionPerformed
        openForce( bottomForce );
    }//GEN-LAST:event_btnOpenBottomActionPerformed

    private void btnSaveBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveBottomActionPerformed
        bottomForce.ForceName = txtUnitNameBottom.getText();
        saveForce( bottomForce );
    }//GEN-LAST:event_btnSaveBottomActionPerformed

    private void btnOpenTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenTopActionPerformed
        openForce( topForce );
}//GEN-LAST:event_btnOpenTopActionPerformed

    private void btnSaveTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveTopActionPerformed
        topForce.ForceName = txtUnitNameTop.getText();
        saveForce( topForce );
}//GEN-LAST:event_btnSaveTopActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddBottom;
    private javax.swing.JButton btnAddTop;
    private javax.swing.JButton btnDeleteBottom;
    private javax.swing.JButton btnDeleteTop;
    private javax.swing.JButton btnEditBottom;
    private javax.swing.JButton btnEditTop;
    private javax.swing.JButton btnLoad;
    private javax.swing.JButton btnMULExport;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnOpenBottom;
    private javax.swing.JButton btnOpenTop;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSaveBottom;
    private javax.swing.JButton btnSaveTop;
    private javax.swing.JCheckBox chkUseForceModifier;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblBaseBVBottom;
    private javax.swing.JLabel lblBaseBVTop;
    private javax.swing.JLabel lblForceMod;
    private javax.swing.JLabel lblScenarioName;
    private javax.swing.JLabel lblTonnageBottom;
    private javax.swing.JLabel lblTonnageTop;
    private javax.swing.JLabel lblTotalBVBottom;
    private javax.swing.JLabel lblTotalBVTop;
    private javax.swing.JLabel lblUnitLogoBottom;
    private javax.swing.JLabel lblUnitLogoTop;
    private javax.swing.JLabel lblUnitNameBottom;
    private javax.swing.JLabel lblUnitNameTop;
    private javax.swing.JLabel lblUnitsBottom;
    private javax.swing.JLabel lblUnitsTop;
    private javax.swing.JMenuItem mnuAbout;
    private javax.swing.JMenuItem mnuDesignBattleMech;
    private javax.swing.JMenuItem mnuExit;
    private javax.swing.JMenuItem mnuLoad;
    private javax.swing.JMenuItem mnuNew;
    private javax.swing.JMenuItem mnuPrintAll;
    private javax.swing.JMenuItem mnuPrintForce;
    private javax.swing.JMenuItem mnuPrintUnits;
    private javax.swing.JMenuItem mnuSave;
    private javax.swing.JPanel pnlBottom;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JScrollPane spnBottom;
    private javax.swing.JScrollPane spnTop;
    private javax.swing.JTable tblBottom;
    private javax.swing.JTable tblTop;
    private javax.swing.JToolBar tlbBottom;
    private javax.swing.JToolBar tlbTop;
    private javax.swing.JTextField txtScenarioName;
    private javax.swing.JTextField txtUnitNameBottom;
    private javax.swing.JTextField txtUnitNameTop;
    // End of variables declaration//GEN-END:variables

}
