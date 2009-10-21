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

import Force.*;
import Print.*;
import filehandlers.Media;
import common.CommonTools;
import battleforce.BattleForce;

import BFB.IO.*;
import BFB.Preview.dlgPreview;
import Force.Objective;
import Print.BFBPrinter;
import Print.PrintMech;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.*;
import javax.swing.ImageIcon;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class frmBase extends javax.swing.JFrame implements java.awt.datatransfer.ClipboardOwner {
    public Scenario scenario = new Scenario();
    public Force topForce = new Force();
    public Force bottomForce = new Force();
    public Preferences Prefs;
    private dlgOpen dOpen;
    private Media media = new Media();

    private TableModelListener ForceChanged = new TableModelListener() {
        public void tableChanged(TableModelEvent e) {
            Refresh();
        }
    };

    public frmBase() {
        initComponents();
        Prefs = Preferences.userNodeForPackage(this.getClass());
        
        //loadScenario( Prefs.get("CurrentBFBFile", "") );
        
        //Clear tracking data
        Prefs.put("CurrentBFBFile", "");

        dOpen = new dlgOpen(this, true);
        dOpen.setMechListPath(Prefs.get("ListPath", ""));
        //dOpen.LoadList();

        topForce.setCurrentModel(new tbTWTable(topForce));
        bottomForce.setCurrentModel(new tbTWTable(bottomForce));

        topForce.addTableModelListener(ForceChanged);
        bottomForce.addTableModelListener(ForceChanged);

        
        Refresh();
    }

    public void Refresh() {
        //javax.swing.JOptionPane.showMessageDialog(this, "Refresh Fired");

        //scenario.setupTable(new JTable[]{tblTop, tblBottom});

        topForce.setupTable(tblTop);
        bottomForce.setupTable(tblBottom);

        topForce.OpForSize = bottomForce.Units.size();
        bottomForce.OpForSize = topForce.Units.size();
        
        setLogo( lblUnitLogoTop, new File(topForce.LogoPath) );
        setLogo( lblUnitLogoBottom, new File(bottomForce.LogoPath) );

        txtUnitNameTop.setText(topForce.ForceName);
        txtUnitNameBottom.setText(bottomForce.ForceName);

        if ( topForce.getType().equals(BattleForce.Comstar) ) {
            btnCSTop.setSelected(true);
        } else if ( topForce.getType().equals(BattleForce.Clan) ) {
            btnCLTop.setSelected(true);
        } else {
            btnISTop.setSelected(true);
        }
        
        if ( bottomForce.getType().equals(BattleForce.Comstar) ) {
            btnCSBottom.setSelected(true);
        } else if ( bottomForce.getType().equals(BattleForce.Clan) ) {
            btnCLBottom.setSelected(true);
        } else {
            btnISBottom.setSelected(true);
        }

        lblForceMod.setText( String.format( "%1$,.2f", CommonTools.GetForceSizeMultiplier( topForce.Units.size(), bottomForce.Units.size() )) );

        if ( chkUseForceModifier.isSelected() ) {
            topForce.OpForSize = bottomForce.Units.size();
            bottomForce.OpForSize = topForce.Units.size();
        }
        
        lblUnitsTop.setText(topForce.Units.size()+"");
        lblTonnageTop.setText( String.format("%1$,.0f", topForce.TotalTonnage) );
        lblBaseBVTop.setText( String.format("%1$,.0f", topForce.TotalBaseBV) );
        lblTotalBVTop.setText( String.format("%1$,.0f", topForce.TotalForceBVAdjusted) );

        lblUnitsBottom.setText(bottomForce.Units.size()+"");
        lblTonnageBottom.setText( String.format("%1$,.0f", bottomForce.TotalTonnage) );
        lblBaseBVBottom.setText( String.format("%1$,.0f", bottomForce.TotalBaseBV) );
        lblTotalBVBottom.setText( String.format("%1$,.0f", bottomForce.TotalForceBVAdjusted) );
    }

    private void loadScenario( String filename ) {
        if ( filename.isEmpty() ) { return; }
        
        XMLReader reader = new XMLReader();
        //Force[] forces;
        try {
            scenario = reader.ReadScenario(filename);

            //Load scenario info into fields
            txtScenarioName.setText(scenario.getName());
            epnSituation.setText(scenario.getSituation());
            epnSetup.setText(scenario.getSetup());
            epnAttacker.setText(scenario.getAttacker());
            epnDefender.setText(scenario.getDefender());
            epnVictoryConditions.setText(scenario.getVictoryConditions());
            epnAftermath.setText(scenario.getAftermath());

            topForce = scenario.topForce();
            bottomForce = scenario.bottomForce();

            topForce.setCurrentModel(new tbTWTable(topForce));
            bottomForce.setCurrentModel(new tbTWTable(bottomForce));

            //forces = reader.ReadFile(this, filename);
            //topForce = forces[0];
            //bottomForce = forces[1];

            topForce.addTableModelListener(ForceChanged);
            bottomForce.addTableModelListener(ForceChanged);

            topForce.RefreshBV();
            bottomForce.RefreshBV();

            Refresh();

        } catch ( IOException ie ) {
            Media.Messager(ie.getMessage());
            System.out.println(ie.getMessage());
            return;
        } catch ( Exception e ) {
            Media.Messager("Issue loading file:\n " + e.getMessage());
            System.out.println(e.getMessage());
            return;
        }
    }

    private void updateLogo( javax.swing.JLabel lblLogo, Force force ) {
        File Logo = media.SelectImage(Prefs.get("LastOpenLogo", ""), "Select Logo");
        try {
            force.LogoPath = Logo.getCanonicalPath();
            setLogo(lblLogo, Logo);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void setLogo( javax.swing.JLabel lblLogo, File Logo ) {
        lblLogo.setIcon(null);
        if ( Logo != null && ! Logo.getPath().isEmpty() ) {
            try {
               Prefs.put("LastOpenLogo", Logo.getPath().toString());
               ImageIcon icon = new ImageIcon(Logo.getPath());

                if( icon == null ) { return; }

                // See if we need to scale
                int lblH = lblLogo.getHeight()-lblLogo.getIconTextGap();
                int lblW = lblLogo.getWidth()-lblLogo.getIconTextGap();

                int h = icon.getIconHeight();
                int w = icon.getIconWidth();
                if ( w > lblW || h > lblH ) {
                    if ( h > lblH ) {
                        icon = new ImageIcon(icon.getImage().
                            getScaledInstance(-1, lblH, Image.SCALE_SMOOTH));
                        w = icon.getIconWidth();
                    }
                    if ( w > lblW ) {
                        icon = new ImageIcon(icon.getImage().
                            getScaledInstance(lblW, -1, Image.SCALE_SMOOTH));
                    }
                }

                lblLogo.setIcon(icon);
            } catch ( Exception e ) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void editUnit( javax.swing.JTable Table, Force force ) {
        Unit u = (Unit) force.Units.get(Table.convertRowIndexToModel(Table.getSelectedRow()));
        dlgUnit dUnit = new dlgUnit(this, true, force, u);
        dUnit.setLocationRelativeTo(this);
        dUnit.setVisible(true);
        force.RefreshBV();
    }

    private void removeUnits( javax.swing.JTable Table, Force force ) {
         int[] rows = Table.getSelectedRows();
         Unit[] units = new Unit[rows.length];
         for (int i=0; i < rows.length; i++ ) {
             Unit u = (Unit) force.Units.get(Table.convertRowIndexToModel(rows[i]));
             units[i] = u;
         }
         for (int j=0; j < units.length; j++) {
             force.RemoveUnit(units[j]);
         }
    }

    private void switchUnits( javax.swing.JTable Table, Force forceFrom, Force forceTo ) {
        int[] rows = Table.getSelectedRows();
        Unit[] units = new Unit[rows.length];
        for (int i=0; i < rows.length; i++ ) {
            Unit u = (Unit) forceFrom.Units.get(Table.convertRowIndexToModel(rows[i]));
            units[i] = u;
        }
        for (int j=0; j < units.length; j++) {
            forceFrom.RemoveUnit(units[j]);
            forceTo.AddUnit(units[j]);
        }
    }

    private void SetType( Force force, String type ) {
        force.setType(type);
    }

    private void validateChanges() {
        if ((topForce.isDirty) || (bottomForce.isDirty)) {
                    switch (javax.swing.JOptionPane.showConfirmDialog(this, "Would you like to save your changes?")) {
                        case javax.swing.JOptionPane.YES_OPTION:
                            this.mnuSaveActionPerformed(null);
                        case javax.swing.JOptionPane.CANCEL_OPTION:
                            return;
                    }
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

    public String getScenario() {
        return txtScenarioName.getText();
    }

    public void openForce( Force force ) {
        File forceFile = media.SelectFile(Prefs.get("LastOpenUnit", ""), "force", "Load Force");

        WaitCursor();
        if (forceFile != null) {
            XMLReader reader = new XMLReader();
            try {
                reader.ReadUnit( force, forceFile.getCanonicalPath() );
                force.RefreshBV();
                Refresh();

               Prefs.put("LastOpenUnit", forceFile.getCanonicalPath());
            } catch (Exception e) {
                Media.Messager("Issue loading file!\n" + e.getMessage());
                System.out.println(e.getMessage());
            }
        }
        DefaultCursor();
    }

    public void saveForce( Force force ) {
        if ( ! force.isSaveable() ) {
            javax.swing.JOptionPane.showMessageDialog(this, "Please enter a unit name and at least one unit before saving.");
            return;
        }
        String dirPath = media.GetDirectorySelection(this, Prefs.get("LastOpenUnit", ""));
        if ( dirPath.isEmpty() ) { return;}

        XMLWriter write = new XMLWriter();
        try {
            String filename = dirPath + File.separator + CommonTools.FormatFileName(force.ForceName) + ".force";
            write.SerializeForce(force, filename);
            javax.swing.JOptionPane.showMessageDialog( this, "Force written to " + filename );
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void toClipboard( Force[] forces ) {
        String data = "";

        data += scenario.SerializeClipboard();

        //for (Force force : forces ) {
        //    data += force.SerializeClipboard() + Constants.NL + Constants.NL;
        //}

        java.awt.datatransfer.StringSelection export = new java.awt.datatransfer.StringSelection( data );
        java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents( export, this );
    }

    private void overrideSkill( Force force, int Gunnery, int Piloting ) {
        for ( int i=0; i < force.Units.size(); i++ ) {
            Unit u = (Unit) force.Units.get(i);
            u.setGunnery(Gunnery);
            u.setPiloting(Piloting);
            u.Refresh();
        }
        force.RefreshBV();
    }

    private void WaitCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    private void DefaultCursor() {
        setCursor(Cursor.getDefaultCursor());
    }

    private void balanceSkills( Force force ) {
        dlgBalance balance = new dlgBalance(this, false, force);
        balance.setLocationRelativeTo(this);
        balance.setVisible(true);
    }

    private PagePrinter SetupPrinter() {
        PagePrinter printer = new PagePrinter();

        printer.setJobName(this.txtScenarioName.getText());

        //Force List
        ForceList sheet = new ForceList();
        sheet.AddForces(new Force[]{topForce, bottomForce});
        printer.Append( BFBPrinter.Letter.toPage(), sheet );

        /*
        //Fire Chits
        PrintDeclaration fire = new PrintDeclaration();
        fire.AddForces(new Force[]{topForce, bottomForce});
        printer.Append( Printer.Letter.toPage(), fire );

        //BattleForce
        PrintBattleforce topBF = new PrintBattleforce(topForce.toBattleForce());
        PrintBattleforce bottomBF = new PrintBattleforce(bottomForce.toBattleForce());

        printer.Append( Printer.Letter.toPage(), topBF );
        printer.Append( Printer.Letter.toPage(), bottomBF );

        //Recordsheets
        Force[] forces = new Force[]{topForce, bottomForce};

        for ( int f=0; f < forces.length; f++ ) {
            Force force = forces[f];

            for ( int m=0; m < force.Units.size(); m++ ) {
                Unit u = (Unit) force.Units.get(m);
                u.LoadMech();
                PrintMech pm = new PrintMech(u.m, u.Mechwarrior, u.Gunnery, u.Piloting);
                printer.Append( Printer.Letter.toPage(), pm);
            }
        }
        */
        return printer;
    }











    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGrpTop = new javax.swing.ButtonGroup();
        btnGrpBottom = new javax.swing.ButtonGroup();
        btnGrpViews = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnLoad = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnPrint = new javax.swing.JButton();
        btnPreview = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnMULExport = new javax.swing.JButton();
        btnClipboard = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        btnManageImages = new javax.swing.JButton();
        btnPersonnel = new javax.swing.JButton();
        lblScenarioName = new javax.swing.JLabel();
        txtScenarioName = new javax.swing.JTextField();
        chkUseForceModifier = new javax.swing.JCheckBox();
        lblForceMod = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        pnlBottom = new javax.swing.JPanel();
        spnBottom = new javax.swing.JScrollPane();
        tblBottom = new javax.swing.JTable();
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
        btnAddBottom1 = new javax.swing.JButton();
        btnEditBottom1 = new javax.swing.JButton();
        btnDeleteBottom1 = new javax.swing.JButton();
        btnSwitchBottom = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        btnBalanceBottom = new javax.swing.JButton();
        jSeparator11 = new javax.swing.JToolBar.Separator();
        btnOpenBottom = new javax.swing.JButton();
        btnSaveBottom = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        btnClipboardBottom = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtBottomGun = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtBottomPilot = new javax.swing.JTextField();
        btnISBottom = new javax.swing.JRadioButton();
        btnCLBottom = new javax.swing.JRadioButton();
        btnCSBottom = new javax.swing.JRadioButton();
        pnlTop = new javax.swing.JPanel();
        spnTop = new javax.swing.JScrollPane();
        tblTop = new javax.swing.JTable();
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
        btnAddTop1 = new javax.swing.JButton();
        btnEditTop1 = new javax.swing.JButton();
        btnDeleteTop1 = new javax.swing.JButton();
        btnSwitchTop = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        btnBalanceTop = new javax.swing.JButton();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        btnOpenTop = new javax.swing.JButton();
        btnSaveTop = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        btnClipboardTop = new javax.swing.JButton();
        btnISTop = new javax.swing.JRadioButton();
        btnCLTop = new javax.swing.JRadioButton();
        btnCSTop = new javax.swing.JRadioButton();
        txtTopGun = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtTopPilot = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        epnSetup = new javax.swing.JEditorPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        epnSituation = new javax.swing.JEditorPane();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        epnAttacker = new javax.swing.JEditorPane();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        epnDefender = new javax.swing.JEditorPane();
        jLabel21 = new javax.swing.JLabel();
        spnBonus = new javax.swing.JScrollPane();
        tblBonuses = new javax.swing.JTable();
        txtAmount = new javax.swing.JFormattedTextField();
        txtBonus = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtTrackCost = new javax.swing.JFormattedTextField();
        jLabel23 = new javax.swing.JLabel();
        txtReward = new javax.swing.JFormattedTextField();
        txtObjective = new javax.swing.JTextField();
        spnObjectives = new javax.swing.JScrollPane();
        tblObjectives = new javax.swing.JTable();
        btnAddBonus = new javax.swing.JButton();
        btnAddObjective = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        epnVictoryConditions = new javax.swing.JEditorPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        epnAftermath = new javax.swing.JEditorPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        mnuNew = new javax.swing.JMenuItem();
        mnuLoad = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JSeparator();
        mnuSave = new javax.swing.JMenuItem();
        mnuSaveAs = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        mnuPrint = new javax.swing.JMenu();
        mnuPrintDlg = new javax.swing.JMenuItem();
        mnuPrintForce = new javax.swing.JMenuItem();
        mnuPrintUnits = new javax.swing.JMenuItem();
        mnuPrintRS = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        mnuPrintPreview = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        mnuExit = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        mnuExportMUL = new javax.swing.JMenuItem();
        mnuExportText = new javax.swing.JMenuItem();
        mnuExportClipboard = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JSeparator();
        mnuBVList = new javax.swing.JMenuItem();
        mnuBFList = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenu6 = new javax.swing.JMenu();
        rmnuTWModel = new javax.swing.JRadioButtonMenuItem();
        rmnuBFModel = new javax.swing.JRadioButtonMenuItem();
        jMenu2 = new javax.swing.JMenu();
        mnuDesignBattleMech = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
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

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/document--plus.png"))); // NOI18N
        btnNew.setToolTipText("New Scenario");
        btnNew.setFocusable(false);
        btnNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew);

        btnLoad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/folder-open-document.png"))); // NOI18N
        btnLoad.setToolTipText("Open Scenario");
        btnLoad.setFocusable(false);
        btnLoad.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLoad.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadActionPerformed(evt);
            }
        });
        jToolBar1.add(btnLoad);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/disk-black.png"))); // NOI18N
        btnSave.setToolTipText("Save Scenario");
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

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/printer.png"))); // NOI18N
        btnPrint.setToolTipText("Print Sheet and Designs");
        btnPrint.setFocusable(false);
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

        btnPreview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/projection-screen.png"))); // NOI18N
        btnPreview.setFocusable(false);
        btnPreview.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPreview.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPreview);
        jToolBar1.add(jSeparator4);

        btnMULExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/map--arrow.png"))); // NOI18N
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

        btnClipboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/clipboard.png"))); // NOI18N
        btnClipboard.setToolTipText("Export Scenario to Clipboard");
        btnClipboard.setFocusable(false);
        btnClipboard.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClipboard.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClipboardActionPerformed(evt);
            }
        });
        jToolBar1.add(btnClipboard);
        jToolBar1.add(jSeparator9);

        btnManageImages.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/images-stack.png"))); // NOI18N
        btnManageImages.setToolTipText("Manage Images");
        btnManageImages.setFocusable(false);
        btnManageImages.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnManageImages.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnManageImages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManageImagesActionPerformed(evt);
            }
        });
        jToolBar1.add(btnManageImages);

        btnPersonnel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/Mechwarrior.png"))); // NOI18N
        btnPersonnel.setToolTipText("Manage Personnel");
        btnPersonnel.setFocusable(false);
        btnPersonnel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPersonnel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPersonnel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPersonnelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPersonnel);

        lblScenarioName.setText("Scenario / Event Name: ");

        txtScenarioName.setToolTipText("Enter the name of the scenario or event");

        chkUseForceModifier.setSelected(true);
        chkUseForceModifier.setText("Use Force Size Modifier");
        chkUseForceModifier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUseForceModifierActionPerformed(evt);
            }
        });

        lblForceMod.setText("0.00");

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
        tblBottom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblBottomKeyReleased(evt);
            }
        });
        spnBottom.setViewportView(tblBottom);

        lblUnitNameBottom.setText("Unit Name:");

        txtUnitNameBottom.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtUnitNameBottomFocusLost(evt);
            }
        });
        txtUnitNameBottom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtUnitNameBottomKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtUnitNameBottomKeyTyped(evt);
            }
        });

        lblUnitLogoBottom.setToolTipText("Logo: Click to change");
        lblUnitLogoBottom.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        lblUnitLogoBottom.setOpaque(true);
        lblUnitLogoBottom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblUnitLogoBottomMouseClicked(evt);
            }
        });

        lblTotalBVBottom.setFont(new java.awt.Font("Verdana", 1, 12));
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

        btnAddBottom1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/shield--plus.png"))); // NOI18N
        btnAddBottom1.setToolTipText("Add Unit");
        btnAddBottom1.setFocusable(false);
        btnAddBottom1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddBottom1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddBottom1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddBottom1ActionPerformed(evt);
            }
        });
        tlbBottom.add(btnAddBottom1);

        btnEditBottom1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/shield--pencil.png"))); // NOI18N
        btnEditBottom1.setToolTipText("Edit Unit");
        btnEditBottom1.setFocusable(false);
        btnEditBottom1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEditBottom1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditBottom1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditBottom1ActionPerformed(evt);
            }
        });
        tlbBottom.add(btnEditBottom1);

        btnDeleteBottom1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/shield--minus.png"))); // NOI18N
        btnDeleteBottom1.setToolTipText("Delete Unit");
        btnDeleteBottom1.setFocusable(false);
        btnDeleteBottom1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDeleteBottom1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDeleteBottom1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteBottom1ActionPerformed(evt);
            }
        });
        tlbBottom.add(btnDeleteBottom1);

        btnSwitchBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/shield--up.png"))); // NOI18N
        btnSwitchBottom.setToolTipText("Move to Primary Force");
        btnSwitchBottom.setFocusable(false);
        btnSwitchBottom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSwitchBottom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSwitchBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSwitchBottomActionPerformed(evt);
            }
        });
        tlbBottom.add(btnSwitchBottom);
        tlbBottom.add(jSeparator5);

        btnBalanceBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/ruler-triangle.png"))); // NOI18N
        btnBalanceBottom.setToolTipText("Auto-Balance");
        btnBalanceBottom.setFocusable(false);
        btnBalanceBottom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBalanceBottom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnBalanceBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBalanceBottomActionPerformed(evt);
            }
        });
        tlbBottom.add(btnBalanceBottom);
        tlbBottom.add(jSeparator11);

        btnOpenBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/folder-open-document.png"))); // NOI18N
        btnOpenBottom.setToolTipText("Open Force");
        btnOpenBottom.setFocusable(false);
        btnOpenBottom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpenBottom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenBottomActionPerformed(evt);
            }
        });
        tlbBottom.add(btnOpenBottom);

        btnSaveBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/disk.png"))); // NOI18N
        btnSaveBottom.setToolTipText("Save Force");
        btnSaveBottom.setFocusable(false);
        btnSaveBottom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSaveBottom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSaveBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveBottomActionPerformed(evt);
            }
        });
        tlbBottom.add(btnSaveBottom);
        tlbBottom.add(jSeparator8);

        btnClipboardBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/clipboard.png"))); // NOI18N
        btnClipboardBottom.setToolTipText("Export Force to Clipboard");
        btnClipboardBottom.setFocusable(false);
        btnClipboardBottom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClipboardBottom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClipboardBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClipboardBottomActionPerformed(evt);
            }
        });
        tlbBottom.add(btnClipboardBottom);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Override  G");

        txtBottomGun.setText("4");
        txtBottomGun.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBottomGunFocusGained(evt);
            }
        });
        txtBottomGun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBottomGunKeyReleased(evt);
            }
        });

        jLabel11.setText("P");

        txtBottomPilot.setText("5");
        txtBottomPilot.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBottomPilotFocusGained(evt);
            }
        });
        txtBottomPilot.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBottomPilotKeyReleased(evt);
            }
        });

        btnGrpBottom.add(btnISBottom);
        btnISBottom.setSelected(true);
        btnISBottom.setText("Inner Sphere");
        btnISBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnISBottomActionPerformed(evt);
            }
        });

        btnGrpBottom.add(btnCLBottom);
        btnCLBottom.setText("Clan");
        btnCLBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCLBottomActionPerformed(evt);
            }
        });

        btnGrpBottom.add(btnCSBottom);
        btnCSBottom.setText("Comstar");
        btnCSBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCSBottomActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlBottomLayout = new javax.swing.GroupLayout(pnlBottom);
        pnlBottom.setLayout(pnlBottomLayout);
        pnlBottomLayout.setHorizontalGroup(
            pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBottomLayout.createSequentialGroup()
                .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBottomLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblUnitLogoBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlBottomLayout.createSequentialGroup()
                                .addComponent(lblUnitNameBottom)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtUnitNameBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnISBottom)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCLBottom)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCSBottom)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBottomGun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addGap(3, 3, 3)
                                .addComponent(txtBottomPilot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(48, 48, 48)
                                .addComponent(tlbBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(spnBottom, javax.swing.GroupLayout.DEFAULT_SIZE, 853, Short.MAX_VALUE)))
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 276, Short.MAX_VALUE)
                        .addComponent(lblTotalBVBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlBottomLayout.setVerticalGroup(
            pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBottomLayout.createSequentialGroup()
                .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblUnitLogoBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlBottomLayout.createSequentialGroup()
                        .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblUnitNameBottom)
                                .addComponent(txtUnitNameBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnISBottom)
                                .addComponent(btnCLBottom)
                                .addComponent(btnCSBottom)
                                .addComponent(txtBottomGun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel10)
                                .addComponent(txtBottomPilot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel11))
                            .addComponent(tlbBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnBottom, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUnitsBottom)
                    .addComponent(jLabel3)
                    .addComponent(lblTonnageBottom)
                    .addComponent(jLabel5)
                    .addComponent(lblBaseBVBottom)
                    .addComponent(jLabel7)
                    .addComponent(lblTotalBVBottom)))
        );

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
        tblTop.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblTopKeyReleased(evt);
            }
        });
        spnTop.setViewportView(tblTop);

        lblUnitNameTop.setText("Unit Name:");

        txtUnitNameTop.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtUnitNameTopFocusLost(evt);
            }
        });
        txtUnitNameTop.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtUnitNameTopKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtUnitNameTopKeyTyped(evt);
            }
        });

        lblUnitLogoTop.setToolTipText("Logo: Click to change");
        lblUnitLogoTop.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        lblUnitLogoTop.setOpaque(true);
        lblUnitLogoTop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblUnitLogoTopMouseClicked(evt);
            }
        });

        lblTotalBVTop.setFont(new java.awt.Font("Verdana", 1, 12));
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

        btnAddTop1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/shield--plus.png"))); // NOI18N
        btnAddTop1.setToolTipText("Add Unit");
        btnAddTop1.setFocusable(false);
        btnAddTop1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddTop1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddTop1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddTop1ActionPerformed(evt);
            }
        });
        tlbTop.add(btnAddTop1);

        btnEditTop1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/shield--pencil.png"))); // NOI18N
        btnEditTop1.setToolTipText("Edit Unit");
        btnEditTop1.setFocusable(false);
        btnEditTop1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEditTop1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditTop1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditTop1ActionPerformed(evt);
            }
        });
        tlbTop.add(btnEditTop1);

        btnDeleteTop1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/shield--minus.png"))); // NOI18N
        btnDeleteTop1.setToolTipText("Delete Unit");
        btnDeleteTop1.setFocusable(false);
        btnDeleteTop1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDeleteTop1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDeleteTop1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteTop1ActionPerformed(evt);
            }
        });
        tlbTop.add(btnDeleteTop1);

        btnSwitchTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/shield--down.png"))); // NOI18N
        btnSwitchTop.setToolTipText("Move to Secondary Force");
        btnSwitchTop.setFocusable(false);
        btnSwitchTop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSwitchTop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSwitchTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSwitchTopActionPerformed(evt);
            }
        });
        tlbTop.add(btnSwitchTop);
        tlbTop.add(jSeparator6);

        btnBalanceTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/ruler-triangle.png"))); // NOI18N
        btnBalanceTop.setToolTipText("Auto-Balance");
        btnBalanceTop.setFocusable(false);
        btnBalanceTop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBalanceTop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnBalanceTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBalanceTopActionPerformed(evt);
            }
        });
        tlbTop.add(btnBalanceTop);
        tlbTop.add(jSeparator10);

        btnOpenTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/folder-open-document.png"))); // NOI18N
        btnOpenTop.setToolTipText("Open Force");
        btnOpenTop.setFocusable(false);
        btnOpenTop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpenTop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenTopActionPerformed(evt);
            }
        });
        tlbTop.add(btnOpenTop);

        btnSaveTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/disk.png"))); // NOI18N
        btnSaveTop.setToolTipText("Save Force");
        btnSaveTop.setFocusable(false);
        btnSaveTop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSaveTop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSaveTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveTopActionPerformed(evt);
            }
        });
        tlbTop.add(btnSaveTop);
        tlbTop.add(jSeparator7);

        btnClipboardTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/clipboard.png"))); // NOI18N
        btnClipboardTop.setToolTipText("Export Force to Clipboard");
        btnClipboardTop.setFocusable(false);
        btnClipboardTop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClipboardTop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClipboardTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClipboardTopActionPerformed(evt);
            }
        });
        tlbTop.add(btnClipboardTop);

        btnGrpTop.add(btnISTop);
        btnISTop.setSelected(true);
        btnISTop.setText("Inner Sphere");
        btnISTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnISTopActionPerformed(evt);
            }
        });

        btnGrpTop.add(btnCLTop);
        btnCLTop.setText("Clan");
        btnCLTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCLTopActionPerformed(evt);
            }
        });

        btnGrpTop.add(btnCSTop);
        btnCSTop.setText("Comstar");
        btnCSTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCSTopActionPerformed(evt);
            }
        });

        txtTopGun.setText("4");
        txtTopGun.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTopGunFocusGained(evt);
            }
        });
        txtTopGun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTopGunKeyReleased(evt);
            }
        });

        jLabel9.setText("  P");

        txtTopPilot.setText("5");
        txtTopPilot.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTopPilotFocusGained(evt);
            }
        });
        txtTopPilot.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTopPilotKeyReleased(evt);
            }
        });

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Override  G ");

        javax.swing.GroupLayout pnlTopLayout = new javax.swing.GroupLayout(pnlTop);
        pnlTop.setLayout(pnlTopLayout);
        pnlTopLayout.setHorizontalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTopLayout.createSequentialGroup()
                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblUnitLogoTop, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlTopLayout.createSequentialGroup()
                                .addComponent(lblUnitNameTop)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtUnitNameTop, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnISTop)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCLTop)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCSTop)
                                .addGap(39, 39, 39)
                                .addComponent(jLabel8)
                                .addComponent(txtTopGun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTopPilot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                                .addComponent(tlbTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(spnTop, javax.swing.GroupLayout.DEFAULT_SIZE, 854, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTopLayout.createSequentialGroup()
                        .addGap(109, 109, 109)
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 281, Short.MAX_VALUE)
                        .addComponent(lblTotalBVTop, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlTopLayout.setVerticalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTopLayout.createSequentialGroup()
                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblUnitLogoTop, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlTopLayout.createSequentialGroup()
                        .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblUnitNameTop)
                                .addComponent(txtUnitNameTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnISTop)
                                .addComponent(btnCLTop)
                                .addComponent(btnCSTop))
                            .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtTopPilot, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(pnlTopLayout.createSequentialGroup()
                                    .addGap(4, 4, 4)
                                    .addComponent(jLabel8))
                                .addComponent(txtTopGun, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(pnlTopLayout.createSequentialGroup()
                                    .addGap(4, 4, 4)
                                    .addComponent(jLabel9))
                                .addComponent(tlbTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnTop, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUnitsTop)
                    .addComponent(jLabel2)
                    .addComponent(lblTonnageTop)
                    .addComponent(jLabel4)
                    .addComponent(lblBaseBVTop)
                    .addComponent(jLabel6)
                    .addComponent(lblTotalBVTop)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlTop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlBottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlTop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlBottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Force Selections", jPanel1);

        jLabel1.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel1.setText("Situation");

        jLabel12.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel12.setText("Setup");

        jLabel13.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel13.setText("Victory Conditions");

        jLabel14.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel14.setText("Aftermath");

        jLabel15.setFont(new java.awt.Font("Tahoma", 2, 11));
        jLabel15.setText("Only used for non-warchest system scenarios");

        jScrollPane2.setViewportView(epnSetup);

        jScrollPane5.setViewportView(epnSituation);

        jLabel19.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel19.setText("Attacker");

        jScrollPane7.setViewportView(epnAttacker);

        jLabel20.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel20.setText("Defender");

        jScrollPane8.setViewportView(epnDefender);

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel21.setText("Bonuses");

        tblBonuses.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Amount", "Bonus"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblBonuses.setShowVerticalLines(false);
        spnBonus.setViewportView(tblBonuses);

        txtAmount.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel22.setText("Track Cost:");

        txtTrackCost.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel23.setText("Objectives");

        txtReward.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));

        tblObjectives.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Objective", "Reward"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblObjectives.setShowVerticalLines(false);
        spnObjectives.setViewportView(tblObjectives);

        btnAddBonus.setText("Add Bonus");

        btnAddObjective.setText("Add Objective");
        btnAddObjective.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddObjectiveActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(epnVictoryConditions);

        jScrollPane3.setViewportView(epnAftermath);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 979, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
                            .addComponent(jLabel12)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel15))
                            .addComponent(jLabel20)
                            .addComponent(jLabel19)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                            .addComponent(jLabel14)
                            .addComponent(jLabel23)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtReward, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtObjective, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAddObjective))
                            .addComponent(spnObjectives, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTrackCost, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel21)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBonus, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAddBonus, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(spnBonus, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22)
                            .addComponent(txtTrackCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnBonus, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtBonus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAddBonus))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnObjectives, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtReward, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtObjective, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAddObjective))
                        .addGap(9, 9, 9)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        jTabbedPane1.addTab("Scenario Information", jPanel2);

        jMenu1.setText("File");

        mnuNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        mnuNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/document--plus.png"))); // NOI18N
        mnuNew.setText("New");
        mnuNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNewActionPerformed(evt);
            }
        });
        jMenu1.add(mnuNew);

        mnuLoad.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        mnuLoad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/folder-open-document.png"))); // NOI18N
        mnuLoad.setText("Load");
        mnuLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLoadActionPerformed(evt);
            }
        });
        jMenu1.add(mnuLoad);
        jMenu1.add(jSeparator13);

        mnuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        mnuSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/disk-black.png"))); // NOI18N
        mnuSave.setText("Save");
        mnuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaveActionPerformed(evt);
            }
        });
        jMenu1.add(mnuSave);

        mnuSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuSaveAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/disk.png"))); // NOI18N
        mnuSaveAs.setText("Save As...");
        mnuSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaveAsActionPerformed(evt);
            }
        });
        jMenu1.add(mnuSaveAs);
        jMenu1.add(jSeparator2);

        mnuPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/printer.png"))); // NOI18N
        mnuPrint.setText("Print");
        mnuPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintActionPerformed(evt);
            }
        });

        mnuPrintDlg.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK));
        mnuPrintDlg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/printer.png"))); // NOI18N
        mnuPrintDlg.setText("Print Options");
        mnuPrintDlg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintDlgActionPerformed(evt);
            }
        });
        mnuPrint.add(mnuPrintDlg);

        mnuPrintForce.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        mnuPrintForce.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/printer.png"))); // NOI18N
        mnuPrintForce.setText("Print Force List");
        mnuPrintForce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintForceActionPerformed(evt);
            }
        });
        mnuPrint.add(mnuPrintForce);

        mnuPrintUnits.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        mnuPrintUnits.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/printer--plus.png"))); // NOI18N
        mnuPrintUnits.setText("Print Unit Sheets");
        mnuPrintUnits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintUnitsActionPerformed(evt);
            }
        });
        mnuPrint.add(mnuPrintUnits);

        mnuPrintRS.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        mnuPrintRS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/printer--plus.png"))); // NOI18N
        mnuPrintRS.setText("Print Record Sheets");
        mnuPrintRS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintRSActionPerformed(evt);
            }
        });
        mnuPrint.add(mnuPrintRS);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/printer--puzzle.png"))); // NOI18N
        jMenuItem1.setText("Print BattleForce");
        mnuPrint.add(jMenuItem1);

        jMenu1.add(mnuPrint);

        mnuPrintPreview.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.SHIFT_MASK));
        mnuPrintPreview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/projection-screen.png"))); // NOI18N
        mnuPrintPreview.setText("Print Preview");
        mnuPrintPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintPreviewActionPerformed(evt);
            }
        });
        jMenu1.add(mnuPrintPreview);
        jMenu1.add(jSeparator3);

        mnuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        mnuExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/burn.png"))); // NOI18N
        mnuExit.setText("Exit");
        mnuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExitActionPerformed(evt);
            }
        });
        jMenu1.add(mnuExit);

        jMenuBar1.add(jMenu1);

        jMenu4.setText("Export");

        mnuExportMUL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/map--arrow.png"))); // NOI18N
        mnuExportMUL.setText("MUL");
        mnuExportMUL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportMULActionPerformed(evt);
            }
        });
        jMenu4.add(mnuExportMUL);

        mnuExportText.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/document-text.png"))); // NOI18N
        mnuExportText.setText("Text");
        mnuExportText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportTextActionPerformed(evt);
            }
        });
        jMenu4.add(mnuExportText);

        mnuExportClipboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/clipboard.png"))); // NOI18N
        mnuExportClipboard.setText("Clipboard");
        mnuExportClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportClipboardActionPerformed(evt);
            }
        });
        jMenu4.add(mnuExportClipboard);
        jMenu4.add(jSeparator12);

        mnuBVList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/document-excel-table.png"))); // NOI18N
        mnuBVList.setText("BV2 List");
        mnuBVList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBVListActionPerformed(evt);
            }
        });
        jMenu4.add(mnuBVList);

        mnuBFList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/document-excel.png"))); // NOI18N
        mnuBFList.setText("BF Stats List");
        mnuBFList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBFListActionPerformed(evt);
            }
        });
        jMenu4.add(mnuBFList);

        jMenuBar1.add(jMenu4);

        jMenu5.setText("View");

        jMenu6.setText("Force List");

        rmnuTWModel.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        btnGrpViews.add(rmnuTWModel);
        rmnuTWModel.setSelected(true);
        rmnuTWModel.setText("Total Warfare");
        rmnuTWModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rmnuTWModelActionPerformed(evt);
            }
        });
        jMenu6.add(rmnuTWModel);

        rmnuBFModel.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        btnGrpViews.add(rmnuBFModel);
        rmnuBFModel.setText("BattleForce");
        rmnuBFModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rmnuBFModelActionPerformed(evt);
            }
        });
        jMenu6.add(rmnuBFModel);

        jMenu5.add(jMenu6);

        jMenuBar1.add(jMenu5);

        jMenu2.setText("Design");

        mnuDesignBattleMech.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.SHIFT_MASK));
        mnuDesignBattleMech.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/madcat-pencil.png"))); // NOI18N
        mnuDesignBattleMech.setText("BattleMech");
        mnuDesignBattleMech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDesignBattleMechActionPerformed(evt);
            }
        });
        jMenu2.add(mnuDesignBattleMech);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.SHIFT_MASK));
        jMenuItem2.setText("Combat Vehicle");
        jMenuItem2.setEnabled(false);
        jMenu2.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK));
        jMenuItem3.setText("Battle Armor");
        jMenuItem3.setEnabled(false);
        jMenu2.add(jMenuItem3);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.SHIFT_MASK));
        jMenuItem4.setText("Aero/Conv Fighter");
        jMenuItem4.setEnabled(false);
        jMenu2.add(jMenuItem4);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.SHIFT_MASK));
        jMenuItem5.setText("Warship/Dropship");
        jMenuItem5.setEnabled(false);
        jMenu2.add(jMenuItem5);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK));
        jMenuItem6.setText("Support Vehicle");
        jMenuItem6.setEnabled(false);
        jMenu2.add(jMenuItem6);

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_MASK));
        jMenuItem7.setText("Protomech");
        jMenuItem7.setEnabled(false);
        jMenu2.add(jMenuItem7);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("About");

        mnuAbout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        mnuAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/projection-screen.png"))); // NOI18N
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
            .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1024, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblScenarioName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtScenarioName, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkUseForceModifier)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblForceMod)
                .addContainerGap(425, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1004, Short.MAX_VALUE)
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
                .addGap(6, 6, 6)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 677, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtUnitNameTopKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUnitNameTopKeyTyped
    }//GEN-LAST:event_txtUnitNameTopKeyTyped

    private void txtUnitNameBottomKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUnitNameBottomKeyTyped
    }//GEN-LAST:event_txtUnitNameBottomKeyTyped

    private void mnuLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLoadActionPerformed
        if ((topForce.isDirty) || (bottomForce.isDirty)) {
                    switch (javax.swing.JOptionPane.showConfirmDialog(this, "Would you like to save your changes?")) {
                        case javax.swing.JOptionPane.YES_OPTION:
                            this.mnuSaveActionPerformed(null);
                        case javax.swing.JOptionPane.CANCEL_OPTION:
                            return;
                    }
        }

        File forceFile = media.SelectFile(Prefs.get("LastOpenBFBDirectory", ""), "bfb", "Load Force List");

        if (forceFile != null) {
            WaitCursor();

            try {
               loadScenario(forceFile.getCanonicalPath());

               Prefs.put("LastOpenBFBDirectory", forceFile.getCanonicalPath().replace(forceFile.getName(), ""));
               Prefs.put("LastOpenBFBFile", forceFile.getName());
               Prefs.put("CurrentBFBFile", forceFile.getPath());
            } catch (Exception e) {
               Media.Messager("Issue loading file:\n " + e.getMessage() );
               System.out.println(e.getMessage());
               return;
            } finally {
                DefaultCursor();
            }
        }
}//GEN-LAST:event_mnuLoadActionPerformed

    private void lblUnitLogoBottomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblUnitLogoBottomMouseClicked
        updateLogo(lblUnitLogoBottom, bottomForce);
    }//GEN-LAST:event_lblUnitLogoBottomMouseClicked

    private void lblUnitLogoTopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblUnitLogoTopMouseClicked
        updateLogo(lblUnitLogoTop, topForce);
    }//GEN-LAST:event_lblUnitLogoTopMouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if ((topForce.isDirty) || (bottomForce.isDirty)) {
            switch (javax.swing.JOptionPane.showConfirmDialog(this, "Would you like to save your changes?")) {
                case javax.swing.JOptionPane.YES_OPTION:
                    this.mnuSaveActionPerformed(null);
                case javax.swing.JOptionPane.NO_OPTION:
                    dOpen.dispose();
                    this.dispose();
                case javax.swing.JOptionPane.CANCEL_OPTION:
                    return;
            }
        } else {
            System.err.flush();
            System.out.flush();
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
        if ((topForce.isDirty) || (bottomForce.isDirty)) {
                    switch (javax.swing.JOptionPane.showConfirmDialog(this, "Would you like to save your changes?")) {
                        case javax.swing.JOptionPane.YES_OPTION:
                            this.mnuSaveActionPerformed(null);
                        case javax.swing.JOptionPane.CANCEL_OPTION:
                            return;
                    }
        }

        Prefs.put("CurrentBFBFile", "");
        this.topForce.Clear();
        this.bottomForce.Clear();
        this.txtScenarioName.setText("");
        this.lblUnitLogoTop.setIcon(null);
        this.lblUnitLogoBottom.setIcon(null);
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

        WaitCursor();
        try {
            File file;
            if ( !Prefs.get("CurrentBFBFile", "").isEmpty() && scenario.isOverwriteable() ) {
                file = new File(Prefs.get("CurrentBFBFile", ""));
            } else {
                file = media.SelectFile(txtScenarioName.getText() + ".bfb", "bfb", "Save");
                if (file == null) {
                    return;
                }
            }
            String filename = file.getCanonicalPath();
            if ( ! filename.endsWith(".bfb") ) { filename += ".bfb";}

            //XMLWriter write = new XMLWriter(txtScenarioName.getText(), this.topForce, this.bottomForce);
            //write.WriteXML(filename);

            XMLWriter writer = new XMLWriter();
            writer.WriteScenario(scenario, filename);
            
            Prefs.put("LastOpenBFBFile", filename);
            Prefs.put("CurrentBFBFile", filename);
            javax.swing.JOptionPane.showMessageDialog(this, "Forces saved to " + filename);
        } catch (java.io.IOException e) {
            Media.Messager(e.getMessage());
            System.out.println(e.getMessage());
        }
        DefaultCursor();
    }//GEN-LAST:event_mnuSaveActionPerformed

    private void mnuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAboutActionPerformed
        dlgAbout About = new dlgAbout();
        About.setTitle("About Battletech Force Balancer");
        About.setLocationRelativeTo(this);
        About.setVisible(true);
}//GEN-LAST:event_mnuAboutActionPerformed

    private void mnuPrintForceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintForceActionPerformed
       WaitCursor();
       BFBPrinter printer = new BFBPrinter();
       printer.setJobName(this.txtScenarioName.getText());
       printer.setTitle(this.txtScenarioName.getText());
       printer.Print();
       DefaultCursor();
}//GEN-LAST:event_mnuPrintForceActionPerformed

    private void mnuPrintUnitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintUnitsActionPerformed
        WaitCursor();
        Vector forces = new Vector();
        forces.add(topForce);
        forces.add(bottomForce);

        PagePrinter printer = new PagePrinter();
        for (int f = 0; f <= forces.size()-1; f++){
            Force printForce = (Force) forces.get(f);

            //printer.setLogoPath(printForce.LogoPath);
            printer.setJobName(printForce.ForceName);

            for (int i = 0; i < printForce.Units.size(); ++i) {
                Unit u = (Unit) printForce.Units.get(i);
                u.LoadMech();
                PrintMech pm = new PrintMech(u.m, u.getMechwarrior(), u.getGunnery(), u.getPiloting());
                pm.setLogoImage(media.GetImage(printForce.LogoPath));
                printer.Append( BFBPrinter.Letter.toPage(), pm);
            }
        }
        printer.Print();
        DefaultCursor();
    }//GEN-LAST:event_mnuPrintUnitsActionPerformed

    private void mnuDesignBattleMechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDesignBattleMechActionPerformed
        String[] call = { "java", "-Xmx256m", "-jar", "ssw.jar" };
        try {
            Runtime.getRuntime().exec(call);
        } catch (IOException ex) {
            Media.Messager(ex.getMessage());
            System.out.println("Error calling SSW jar file");
        }
    }//GEN-LAST:event_mnuDesignBattleMechActionPerformed

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
        //mnuPrintAllActionPerformed(evt);
        mnuPrintActionPerformed(evt);
}//GEN-LAST:event_btnPrintActionPerformed

    private void tblTopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTopMouseClicked
        if ( evt.getClickCount() == 2 ) { editUnit(tblTop, topForce); }
    }//GEN-LAST:event_tblTopMouseClicked

    private void tblBottomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBottomMouseClicked
        if ( evt.getClickCount() == 2 ) { editUnit(tblBottom, bottomForce); }
    }//GEN-LAST:event_tblBottomMouseClicked

    private void btnMULExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMULExportActionPerformed
        WaitCursor();
        MULWriter mw = new MULWriter();
        String dir = "";
        dir = media.GetDirectorySelection(this, Prefs.get("MULDirectory", ""));
        if ( dir.isEmpty() ) { return; }

        Prefs.put("MULDirectory", dir);
        mw.setForce(topForce);
        try {
            mw.Write( dir + File.separator + topForce.ForceName );
        } catch (IOException ex) {
            Media.Messager("Unable to save " + topForce.ForceName + "\n" + ex.getMessage() );
            System.out.println(ex.getMessage());
        }

        mw.setForce(bottomForce);
        try {
            mw.Write( dir + File.separator + bottomForce.ForceName );
        } catch ( IOException ex ) {
            Media.Messager("Unable to save " + bottomForce.ForceName + "\n" + ex.getMessage() );
            System.out.println(ex.getMessage());
        }

        javax.swing.JOptionPane.showMessageDialog(this, "Your forces have been exported to " + dir);
        DefaultCursor();
}//GEN-LAST:event_btnMULExportActionPerformed

    private void chkUseForceModifierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUseForceModifierActionPerformed
        lblForceMod.setVisible( chkUseForceModifier.isSelected() );
        topForce.useUnevenForceMod = chkUseForceModifier.isSelected();
        bottomForce.useUnevenForceMod = chkUseForceModifier.isSelected();
        topForce.RefreshBV();
        bottomForce.RefreshBV();
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

    private void btnAddBottom1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBottom1ActionPerformed
        OpenDialog(bottomForce);
    }//GEN-LAST:event_btnAddBottom1ActionPerformed

    private void btnAddTop1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddTop1ActionPerformed
        OpenDialog(topForce);
    }//GEN-LAST:event_btnAddTop1ActionPerformed

    private void btnEditTop1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditTop1ActionPerformed
        editUnit(tblTop, topForce);
    }//GEN-LAST:event_btnEditTop1ActionPerformed

    private void btnEditBottom1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditBottom1ActionPerformed
        editUnit(tblBottom, bottomForce);
    }//GEN-LAST:event_btnEditBottom1ActionPerformed

    private void btnDeleteTop1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteTop1ActionPerformed
        removeUnits( tblTop, topForce );
    }//GEN-LAST:event_btnDeleteTop1ActionPerformed

    private void btnDeleteBottom1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteBottom1ActionPerformed
        removeUnits( tblBottom, bottomForce );
    }//GEN-LAST:event_btnDeleteBottom1ActionPerformed

    private void btnClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClipboardActionPerformed
        toClipboard( new Force[]{ topForce, bottomForce } );
    }//GEN-LAST:event_btnClipboardActionPerformed

    private void btnClipboardTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClipboardTopActionPerformed
        toClipboard( new Force[]{ topForce } );
    }//GEN-LAST:event_btnClipboardTopActionPerformed

    private void btnClipboardBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClipboardBottomActionPerformed
        toClipboard( new Force[]{ bottomForce } );
    }//GEN-LAST:event_btnClipboardBottomActionPerformed

    private void mnuSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveAsActionPerformed
        if ( txtScenarioName.getText().isEmpty() ) {
            Media.Messager("Please enter a scenario name before saving.");
            return;
        }

        if ( !topForce.isSaveable() || !bottomForce.isSaveable() ) {
            Media.Messager("Please enter a force name and at least one unit in each list before saving.");
            return;
        }

        WaitCursor();
        try {
            File file;
            file = media.SelectFile("", "bfb", "Save");
            if (file == null) {
                return;
            }

            String filename = file.getCanonicalPath();
            if ( ! filename.endsWith(".bfb") ) { filename += ".bfb";}

            XMLWriter write = new XMLWriter(txtScenarioName.getText(), this.topForce, this.bottomForce);
            write.WriteXML(filename);
            Prefs.put("LastOpenBFBFile", filename);
            Prefs.put("CurrentBFBFile", filename);
            javax.swing.JOptionPane.showMessageDialog(this, "Forces saved to " + filename);
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
            Media.Messager(e.getMessage());
        }
        DefaultCursor();
}//GEN-LAST:event_mnuSaveAsActionPerformed

    private void mnuExportClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportClipboardActionPerformed
        toClipboard( new Force[]{ topForce, bottomForce } );
    }//GEN-LAST:event_mnuExportClipboardActionPerformed

    private void mnuExportMULActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportMULActionPerformed
        btnMULExportActionPerformed(evt);
    }//GEN-LAST:event_mnuExportMULActionPerformed

    private void mnuExportTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportTextActionPerformed
        WaitCursor();
        TXTWriter txtWrite = new TXTWriter( new Force[]{ topForce, bottomForce } );
        File filename = media.SelectFile(Prefs.get("TXTDirectory", ""), "txt", "Save");
        if ( filename == null ) { return; }

        try {
            txtWrite.Write(filename.getCanonicalPath());

            Prefs.put("TXTDirectory", filename.getCanonicalPath());
            Media.Messager("Your forces have been exported to " + filename.getCanonicalPath());
        } catch (IOException ex) {
            //do nothing
            System.out.println(ex.getMessage());
            Media.Messager("Unable to save \n" + ex.getMessage() );
        }
        DefaultCursor();
    }//GEN-LAST:event_mnuExportTextActionPerformed

    private void txtTopGunKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTopGunKeyReleased
        if ( !txtTopGun.getText().isEmpty() && !txtTopPilot.getText().isEmpty() ) {
            overrideSkill( topForce, Integer.parseInt(txtTopGun.getText()), Integer.parseInt(txtTopPilot.getText()) );
        }
}//GEN-LAST:event_txtTopGunKeyReleased

    private void txtTopGunFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTopGunFocusGained
        txtTopGun.selectAll();
    }//GEN-LAST:event_txtTopGunFocusGained

    private void txtTopPilotKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTopPilotKeyReleased
        if ( !txtTopGun.getText().isEmpty() && !txtTopPilot.getText().isEmpty() ) {
            overrideSkill( topForce, Integer.parseInt(txtTopGun.getText()), Integer.parseInt(txtTopPilot.getText()) );
        }
    }//GEN-LAST:event_txtTopPilotKeyReleased

    private void txtTopPilotFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTopPilotFocusGained
        txtTopPilot.selectAll();
    }//GEN-LAST:event_txtTopPilotFocusGained

    private void txtBottomGunFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBottomGunFocusGained
        txtBottomGun.selectAll();
}//GEN-LAST:event_txtBottomGunFocusGained

    private void txtBottomGunKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBottomGunKeyReleased
        if ( !txtBottomGun.getText().isEmpty() && !txtBottomPilot.getText().isEmpty() ) {
            overrideSkill( bottomForce, Integer.parseInt(txtBottomGun.getText()), Integer.parseInt(txtBottomPilot.getText()) );
        }
}//GEN-LAST:event_txtBottomGunKeyReleased

    private void txtBottomPilotFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBottomPilotFocusGained
        txtBottomPilot.selectAll();
}//GEN-LAST:event_txtBottomPilotFocusGained

    private void txtBottomPilotKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBottomPilotKeyReleased
        if ( !txtBottomGun.getText().isEmpty() && !txtBottomPilot.getText().isEmpty() ) {
            overrideSkill( bottomForce, Integer.parseInt(txtBottomGun.getText()), Integer.parseInt(txtBottomPilot.getText()) );
        }
}//GEN-LAST:event_txtBottomPilotKeyReleased

    private void btnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviewActionPerformed
        PagePrinter printer = SetupPrinter();
        dlgPreview prv = new dlgPreview("Print Preview", this, printer, new Force[]{topForce, bottomForce});
        prv.setLocationRelativeTo(this);
        prv.setVisible(true);
    }//GEN-LAST:event_btnPreviewActionPerformed

    private void btnManageImagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManageImagesActionPerformed
        WaitCursor();
        dlgMechImages img = new dlgMechImages(this, new Force[]{topForce, bottomForce});
        if ( img.hasWork ) {
            img.setLocationRelativeTo(this);
            img.setVisible(true);
        }
        DefaultCursor();
    }//GEN-LAST:event_btnManageImagesActionPerformed

    private void mnuPrintDlgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintDlgActionPerformed
        dlgPrint print = new dlgPrint(this, false);
        print.setLocationRelativeTo(this);
        print.setVisible(true);
}//GEN-LAST:event_mnuPrintDlgActionPerformed

    private void btnPersonnelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPersonnelActionPerformed
        dlgPersonnel ppl = new dlgPersonnel(this, false);
        ppl.setLocationRelativeTo(this);
        ppl.setVisible(true);
    }//GEN-LAST:event_btnPersonnelActionPerformed

    private void mnuPrintRSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintRSActionPerformed
        WaitCursor();
        Vector forces = new Vector();
        forces.add(topForce);
        forces.add(bottomForce);

        PagePrinter printer = new PagePrinter();
        for (int f = 0; f <= forces.size()-1; f++){
            Force printForce = (Force) forces.get(f);

            //printer.setLogoPath(printForce.LogoPath);
            printer.setJobName(printForce.ForceName);

            for (int i = 0; i < printForce.Units.size(); ++i) {
                Unit u = (Unit) printForce.Units.get(i);
                u.LoadMech();
                PrintMech pm = new PrintMech(u.m, u.getMechwarrior(), u.getGunnery(), u.getPiloting());
                pm.setCanon(true);
                pm.setCharts(false);
                pm.SetMiniConversion(1);
                pm.setPrintPilot(false);
                pm.setTRO(true);

                printer.Append( BFBPrinter.Letter.toPage(), pm);
            }
        }
        printer.Print();
        DefaultCursor();
    }//GEN-LAST:event_mnuPrintRSActionPerformed

    private void txtUnitNameTopKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUnitNameTopKeyReleased
        topForce.ForceName = txtUnitNameTop.getText();
    }//GEN-LAST:event_txtUnitNameTopKeyReleased

    private void txtUnitNameBottomKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUnitNameBottomKeyReleased
        bottomForce.ForceName = txtUnitNameBottom.getText();
    }//GEN-LAST:event_txtUnitNameBottomKeyReleased

    private void tblTopKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblTopKeyReleased
        if ( evt.getKeyCode() == KeyEvent.VK_DELETE ) {
            btnDeleteTop1ActionPerformed(null);
        }
    }//GEN-LAST:event_tblTopKeyReleased

    private void tblBottomKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblBottomKeyReleased
        if ( evt.getKeyCode() == KeyEvent.VK_DELETE ) {
            btnDeleteBottom1ActionPerformed(null);
        }
    }//GEN-LAST:event_tblBottomKeyReleased

    private void btnBalanceTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBalanceTopActionPerformed
        balanceSkills( topForce );
    }//GEN-LAST:event_btnBalanceTopActionPerformed

    private void btnBalanceBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBalanceBottomActionPerformed
        balanceSkills( bottomForce );
    }//GEN-LAST:event_btnBalanceBottomActionPerformed

    private void btnSwitchTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSwitchTopActionPerformed
        switchUnits( tblTop, topForce, bottomForce );
    }//GEN-LAST:event_btnSwitchTopActionPerformed

    private void btnSwitchBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSwitchBottomActionPerformed
        switchUnits( tblBottom, bottomForce, topForce );
    }//GEN-LAST:event_btnSwitchBottomActionPerformed

    private void btnISTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnISTopActionPerformed
        topForce.setType(BattleForce.InnerSphere);
    }//GEN-LAST:event_btnISTopActionPerformed

    private void btnCLTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCLTopActionPerformed
        topForce.setType(BattleForce.Clan);
    }//GEN-LAST:event_btnCLTopActionPerformed

    private void btnCSTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCSTopActionPerformed
        topForce.setType(BattleForce.Comstar);
    }//GEN-LAST:event_btnCSTopActionPerformed

    private void btnISBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnISBottomActionPerformed
        bottomForce.setType(BattleForce.InnerSphere);
    }//GEN-LAST:event_btnISBottomActionPerformed

    private void btnCLBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCLBottomActionPerformed
        bottomForce.setType(BattleForce.Clan);
    }//GEN-LAST:event_btnCLBottomActionPerformed

    private void btnCSBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCSBottomActionPerformed
        bottomForce.setType(BattleForce.Comstar);
    }//GEN-LAST:event_btnCSBottomActionPerformed

    private void mnuPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintActionPerformed
        dlgPrint print = new dlgPrint(this, false);
        print.setLocationRelativeTo(this);
        print.setVisible(true);
    }//GEN-LAST:event_mnuPrintActionPerformed

    private void mnuPrintPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintPreviewActionPerformed
        btnPreviewActionPerformed(evt);
}//GEN-LAST:event_mnuPrintPreviewActionPerformed

    private void mnuBVListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBVListActionPerformed
        Media.Messager("This will output a csv list of mechs and also a list of EVERY SINGLE Mech's cost and BV2 calculation!");
        WaitCursor();
        if ( dOpen.getList() == null ) { dOpen.LoadList(); }
        
        TXTWriter out = new TXTWriter();
        String dir = "";
        dir = media.GetDirectorySelection(this, Prefs.get("ListDirectory", ""));
        if ( dir.isEmpty() ) { 
            DefaultCursor();
            return;
        }

        Prefs.put("ListDirectory", dir);
        try {
            out.WriteList(dir + File.separator + "MechListing.csv", dOpen.getList());
            Media.Messager("Mech List output to " + dir);
        } catch (IOException ex) {
            //do nothing
            System.out.println(ex.getMessage());
            Media.Messager("Unable to output list\n" + ex.getMessage() );
        }
        DefaultCursor();
    }//GEN-LAST:event_mnuBVListActionPerformed

    private void mnuBFListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBFListActionPerformed
        WaitCursor();
        if ( dOpen.getList() == null ) { dOpen.LoadList(); }

        TXTWriter out = new TXTWriter();
        String dir = "";
        dir = media.GetDirectorySelection(this, Prefs.get("ListDirectory", ""));
        if ( dir.isEmpty() ) {
            DefaultCursor();
            return;
        }

        Prefs.put("ListDirectory", dir);
        try {
            out.WriteBFList(dir + File.separator + "BattleForceListing.csv", dOpen.getList());
            Media.Messager("BattleForce List output to " + dir);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            Media.Messager("Unable to output list\n" + ex.getMessage() );
        }
        DefaultCursor();
    }//GEN-LAST:event_mnuBFListActionPerformed

    private void rmnuTWModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rmnuTWModelActionPerformed
        topForce.setCurrentModel(new tbTWTable(topForce));
        bottomForce.setCurrentModel(new tbTWTable(bottomForce));
        Refresh();
    }//GEN-LAST:event_rmnuTWModelActionPerformed

    private void rmnuBFModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rmnuBFModelActionPerformed
        WaitCursor();
        topForce.setCurrentModel(new tbBFTable(topForce));
        bottomForce.setCurrentModel(new tbBFTable(bottomForce));
        Refresh();
        DefaultCursor();
    }//GEN-LAST:event_rmnuBFModelActionPerformed

    private void btnAddObjectiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddObjectiveActionPerformed
        if ( !txtReward.getText().isEmpty() && !txtObjective.getText().isEmpty() ) {
            scenario.getWarchest().getObjectives().add(new Objective(txtObjective.getText(), Integer.parseInt(txtReward.getText())));
            txtReward.setText("");
            txtObjective.setText("");
            tblObjectives.setModel(scenario.getWarchest().getObjectiveTable());
        }
    }//GEN-LAST:event_btnAddObjectiveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddBonus;
    private javax.swing.JButton btnAddBottom1;
    private javax.swing.JButton btnAddObjective;
    private javax.swing.JButton btnAddTop1;
    private javax.swing.JButton btnBalanceBottom;
    private javax.swing.JButton btnBalanceTop;
    private javax.swing.JRadioButton btnCLBottom;
    private javax.swing.JRadioButton btnCLTop;
    private javax.swing.JRadioButton btnCSBottom;
    private javax.swing.JRadioButton btnCSTop;
    private javax.swing.JButton btnClipboard;
    private javax.swing.JButton btnClipboardBottom;
    private javax.swing.JButton btnClipboardTop;
    private javax.swing.JButton btnDeleteBottom1;
    private javax.swing.JButton btnDeleteTop1;
    private javax.swing.JButton btnEditBottom1;
    private javax.swing.JButton btnEditTop1;
    private javax.swing.ButtonGroup btnGrpBottom;
    private javax.swing.ButtonGroup btnGrpTop;
    private javax.swing.ButtonGroup btnGrpViews;
    private javax.swing.JRadioButton btnISBottom;
    private javax.swing.JRadioButton btnISTop;
    private javax.swing.JButton btnLoad;
    private javax.swing.JButton btnMULExport;
    private javax.swing.JButton btnManageImages;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnOpenBottom;
    private javax.swing.JButton btnOpenTop;
    private javax.swing.JButton btnPersonnel;
    private javax.swing.JButton btnPreview;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSaveBottom;
    private javax.swing.JButton btnSaveTop;
    private javax.swing.JButton btnSwitchBottom;
    private javax.swing.JButton btnSwitchTop;
    private javax.swing.JCheckBox chkUseForceModifier;
    private javax.swing.JEditorPane epnAftermath;
    private javax.swing.JEditorPane epnAttacker;
    private javax.swing.JEditorPane epnDefender;
    private javax.swing.JEditorPane epnSetup;
    private javax.swing.JEditorPane epnSituation;
    private javax.swing.JEditorPane epnVictoryConditions;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JTabbedPane jTabbedPane1;
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
    private javax.swing.JMenuItem mnuBFList;
    private javax.swing.JMenuItem mnuBVList;
    private javax.swing.JMenuItem mnuDesignBattleMech;
    private javax.swing.JMenuItem mnuExit;
    private javax.swing.JMenuItem mnuExportClipboard;
    private javax.swing.JMenuItem mnuExportMUL;
    private javax.swing.JMenuItem mnuExportText;
    private javax.swing.JMenuItem mnuLoad;
    private javax.swing.JMenuItem mnuNew;
    private javax.swing.JMenu mnuPrint;
    private javax.swing.JMenuItem mnuPrintDlg;
    private javax.swing.JMenuItem mnuPrintForce;
    private javax.swing.JMenuItem mnuPrintPreview;
    private javax.swing.JMenuItem mnuPrintRS;
    private javax.swing.JMenuItem mnuPrintUnits;
    private javax.swing.JMenuItem mnuSave;
    private javax.swing.JMenuItem mnuSaveAs;
    private javax.swing.JPanel pnlBottom;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JRadioButtonMenuItem rmnuBFModel;
    private javax.swing.JRadioButtonMenuItem rmnuTWModel;
    private javax.swing.JScrollPane spnBonus;
    private javax.swing.JScrollPane spnBottom;
    private javax.swing.JScrollPane spnObjectives;
    private javax.swing.JScrollPane spnTop;
    private javax.swing.JTable tblBonuses;
    private javax.swing.JTable tblBottom;
    private javax.swing.JTable tblObjectives;
    private javax.swing.JTable tblTop;
    private javax.swing.JToolBar tlbBottom;
    private javax.swing.JToolBar tlbTop;
    private javax.swing.JFormattedTextField txtAmount;
    private javax.swing.JTextField txtBonus;
    private javax.swing.JTextField txtBottomGun;
    private javax.swing.JTextField txtBottomPilot;
    private javax.swing.JTextField txtObjective;
    private javax.swing.JFormattedTextField txtReward;
    private javax.swing.JTextField txtScenarioName;
    private javax.swing.JTextField txtTopGun;
    private javax.swing.JTextField txtTopPilot;
    private javax.swing.JFormattedTextField txtTrackCost;
    private javax.swing.JTextField txtUnitNameBottom;
    private javax.swing.JTextField txtUnitNameTop;
    // End of variables declaration//GEN-END:variables

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        //do nothing
    }

}
