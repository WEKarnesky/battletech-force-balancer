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
import Force.View.*;
import Print.*;
import Print.preview.*;
import dialog.*;
import filehandlers.Media;
import filehandlers.ImageTracker;
import common.CommonTools;
import battleforce.BattleForce;

import BFB.IO.*;

import Force.Skills.Skill;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.prefs.*;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class frmBase extends javax.swing.JFrame implements java.awt.datatransfer.ClipboardOwner {
    public Scenario scenario = new Scenario();
    public Preferences Prefs;
    private dlgOpen dOpen;
    private Media media = new Media();
    private ImageTracker images = new ImageTracker();

    private KeyListener KeyTyped = new KeyListener() {
        public void keyTyped(KeyEvent e) {
            scenario.setName(txtScenarioName.getText());
            scenario.setSetup(edtSetup.getText());
            scenario.setSituation(edtSituation.getText());
            scenario.setAttacker(edtAttacker.getText());
            scenario.setDefender(edtDefender.getText());
            scenario.setSpecialRules(edtSpecialRules.getText());
            scenario.setVictoryConditions(edtVictoryConditions.getText());
            scenario.setAftermath(edtAftermath.getText());
        }

        public void keyPressed(KeyEvent e) {
            //do nothing
        }

        public void keyReleased(KeyEvent e) {
            keyTyped(e);
        }
    };

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

        dOpen = new dlgOpen(this, true);
        dOpen.setMechListPath(Prefs.get("ListPath", ""));

        scenario.setModel(new tbTotalWarfare());
        scenario.AddListener(ForceChanged);
        scenario.updateOpFor(chkUseForceModifier.isSelected());

        txtScenarioName.addKeyListener(KeyTyped);
        edtSituation.addKeyListener(KeyTyped);
        edtSetup.addKeyListener(KeyTyped);
        edtAttacker.addKeyListener(KeyTyped);
        edtDefender.addKeyListener(KeyTyped);
        edtSpecialRules.addKeyListener(KeyTyped);
        edtVictoryConditions.addKeyListener(KeyTyped);
        edtAftermath.addKeyListener(KeyTyped);
        
        Refresh();

        if ( !Prefs.get("LastOpenFile", "").isEmpty() ) { loadScenario(Prefs.get("LastOpenFile", "")); }
    }

    public void Refresh() {
        scenario.setupTables(tblTop, tblBottom);

        setLogo( lblUnitLogoTop, new File(scenario.getAttackerForce().LogoPath) );
        setLogo( lblUnitLogoBottom, new File(scenario.getDefenderForce().LogoPath) );

        txtUnitNameTop.setText(scenario.getAttackerForce().ForceName);
        txtUnitNameBottom.setText(scenario.getDefenderForce().ForceName);

        if ( scenario.getAttackerForce().getType().equals(BattleForce.Comstar) ) {
            btnCSTop.setSelected(true);
        } else if ( scenario.getAttackerForce().getType().equals(BattleForce.Clan) ) {
            btnCLTop.setSelected(true);
        } else {
            btnISTop.setSelected(true);
        }
        
        if ( scenario.getDefenderForce().getType().equals(BattleForce.Comstar) ) {
            btnCSBottom.setSelected(true);
        } else if ( scenario.getDefenderForce().getType().equals(BattleForce.Clan) ) {
            btnCLBottom.setSelected(true);
        } else {
            btnISBottom.setSelected(true);
        }

        lblForceMod.setText( String.format( "%1$,.2f", CommonTools.GetForceSizeMultiplier( scenario.getAttackerForce().getUnits().size(), scenario.getDefenderForce().getUnits().size() )) );
        
        updateFields();
    }

    private void updateFields() {
        lblUnitsTop.setText(scenario.getAttackerForce().getUnits().size()+"");
        lblTonnageTop.setText( String.format("%1$,.0f", scenario.getAttackerForce().TotalTonnage) );
        lblBaseBVTop.setText( String.format("%1$,.0f", scenario.getAttackerForce().TotalBaseBV) );
        lblTotalBVTop.setText( String.format("%1$,.0f", scenario.getAttackerForce().TotalForceBVAdjusted) );

        lblUnitsBottom.setText(scenario.getDefenderForce().getUnits().size()+"");
        lblTonnageBottom.setText( String.format("%1$,.0f", scenario.getDefenderForce().TotalTonnage) );
        lblBaseBVBottom.setText( String.format("%1$,.0f", scenario.getDefenderForce().TotalBaseBV) );
        lblTotalBVBottom.setText( String.format("%1$,.0f", scenario.getDefenderForce().TotalForceBVAdjusted) );
    }

    private void loadScenario( String filename ) {
        if ( filename.isEmpty() ) { return; }
        
        XMLReader reader = new XMLReader();
        //Force[] forces;
        try {
            scenario = reader.ReadScenario(filename);

            scenario.setModel(new tbTotalWarfare());
            scenario.AddListener(ForceChanged);
            chkUseForceModifier.setSelected(scenario.UseForceSizeModifier());
            scenario.Refresh();

            lstObjectives.setModel(scenario.getWarchest().getObjectiveList());
            lstBonuses.setModel(scenario.getWarchest().getBonusList());

            //Load scenario info into fields
            txtScenarioName.setText(scenario.getName());
            edtSituation.setText(scenario.getSituation());
            edtSetup.setText(scenario.getSetup());
            edtAttacker.setText(scenario.getAttacker());
            edtDefender.setText(scenario.getDefender());
            edtSpecialRules.setText(scenario.getSpecialRules());
            edtVictoryConditions.setText(scenario.getVictoryConditions());
            edtAftermath.setText(scenario.getAftermath());

            Refresh();

            Prefs.put("LastOpenFile", filename);
            Prefs.put("CurrentBFBFile", filename);

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
            if ( Logo == null ) {
                if ( !force.LogoPath.isEmpty() ) {
                    if ( javax.swing.JOptionPane.showConfirmDialog(this, "Would you like to remove your current logo?", "Remove Logo", javax.swing.JOptionPane.YES_NO_OPTION) == javax.swing.JOptionPane.YES_OPTION ) {
                        setLogo(lblLogo, null);
                        force.LogoPath = "";
                        force.isDirty = true;
                    }
                }
            } else {
                force.LogoPath = Logo.getCanonicalPath();
                force.isDirty = true;
                setLogo(lblLogo, Logo);
            }
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
        if ( Table.getSelectedRowCount() > 0 ) {
            Unit u = (Unit) force.getUnits().get(Table.convertRowIndexToModel(Table.getSelectedRow()));
            dlgUnit dUnit = new dlgUnit(this, true, force, u, images);
            dUnit.setLocationRelativeTo(this);
            dUnit.setVisible(true);
            force.RefreshBV();
        }
    }

    private void removeUnits( javax.swing.JTable Table, Force force ) {
         int[] rows = Table.getSelectedRows();
         Unit[] units = new Unit[rows.length];
         for (int i=0; i < rows.length; i++ ) {
             Unit u = (Unit) force.getUnits().get(Table.convertRowIndexToModel(rows[i]));
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
            Unit u = (Unit) forceFrom.getUnits().get(Table.convertRowIndexToModel(rows[i]));
            units[i] = u;
        }
        for (int j=0; j < units.length; j++) {
            forceFrom.RemoveUnit(units[j]);
            forceTo.AddUnit(units[j]);
        }
        forceFrom.clearEmptyGroups();
        forceTo.clearEmptyGroups();
    }

    private void SetType( Force force, String type ) {
        force.setType(type);
    }

    private void validateChanges() {
        if ((scenario.getAttackerForce().isDirty) || (scenario.getDefenderForce().isDirty) || scenario.IsDirty()) {
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
        dOpen.setSize(1024, 768);
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
            Media.Messager(this, "Please enter a unit name and at least one unit before saving.");
            return;
        }
        String dirPath = media.GetDirectorySelection(this, Prefs.get("LastOpenUnit", ""));
        if ( dirPath.isEmpty() ) { return;}

        XMLWriter write = new XMLWriter();
        try {
            String filename = dirPath + File.separator + CommonTools.FormatFileName(force.ForceName) + ".force";
            write.SerializeForce(force, filename);
            Media.Messager( this, "Force written to " + filename );
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
        for ( int i=0; i < force.getUnits().size(); i++ ) {
            Unit u = (Unit) force.getUnits().get(i);
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

    private void balanceSkills( JTable table, Force force ) {
        dlgBalance balance = new dlgBalance(this, true, force);
        balance.setLocationRelativeTo(this);
        balance.setVisible(true);

        Skills skills;
        if ( balance.Result != dlgBalance.SK_CANCEL ) {
            if ( table.getSelectedRowCount() == 0 ) {
                table.selectAll();
            }
        }
        switch ( balance.Result ) {
            case dlgBalance.SK_BESTSKILLS:
                skills = balance.skills;
                for ( int i : table.getSelectedRows() ) {
                    Unit u = (Unit) force.getUnits().get(table.convertRowIndexToModel(i));
                    skills.setBV(u.BaseBV);
                    Skill skill = skills.getBestSkills();
                    u.setGunnery(skill.getGunnery());
                    u.setPiloting(skill.getPiloting());
                    u.Refresh();
                }
                force.isDirty = true;
                break;

            case dlgBalance.SK_RANDOMSKILLS:
                skills = balance.skills;
                for ( int i : table.getSelectedRows() ) {
                    Unit u = (Unit) force.getUnits().get(table.convertRowIndexToModel(i));
                    Skill skill = skills.generateRandomSkill();
                    u.setGunnery(skill.getGunnery());
                    u.setPiloting(skill.getPiloting());
                    u.Refresh();
                }
                force.isDirty = true;
                break;
        }
        force.RefreshBV();
    }

    private void ManageGroup() {
        dlgGroup dlggroup = new dlgGroup(this, false, scenario);
        dlggroup.setLocationRelativeTo(this);
        dlggroup.setVisible(true);
    }

    private void ChangeC3( Force force, JComboBox selection ) {
        boolean UseC3 = false;
        if ( selection.getSelectedItem().toString().equals("On") ) { UseC3 = true; }
        for ( Unit u : force.getUnits() ) {
            u.LoadMech();
            if ( u.m != null ) {
                if ( u.m.HasC3() ) {
                    u.UsingC3 = UseC3;
                }
            }
        }
        force.RefreshBV();
    }

    private PagePrinter SetupPrinter() {
        PagePrinter printer = new PagePrinter();

        printer.setJobName(scenario.getName());

        //Force List
        ForceListPrinter sheet = new ForceListPrinter(images);
        sheet.setTitle(scenario.getName());
        sheet.AddForces(scenario.getForces());
        printer.Append( BFBPrinter.Letter.toPage(), sheet );

        /*
        //Fire Chits
        PrintDeclaration fire = new PrintDeclaration();
        fire.AddForces(new Force[]{scenario.getAttackerForce(), scenario.getDefenderForce()});
        printer.Append( Printer.Letter.toPage(), fire );

        //BattleForce
        PrintBattleforce topBF = new PrintBattleforce(scenario.getAttackerForce().toBattleForce());
        PrintBattleforce bottomBF = new PrintBattleforce(scenario.getDefenderForce().toBattleForce());

        printer.Append( Printer.Letter.toPage(), topBF );
        printer.Append( Printer.Letter.toPage(), bottomBF );

        //Recordsheets
        Force[] forces = new Force[]{scenario.getAttackerForce(), scenario.getDefenderForce()};

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
        btnGroupTop = new javax.swing.JButton();
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
        cmbC3Bottom = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
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
        cmbC3Top = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        pnlSituation = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        edtSituation = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        edtSetup = new javax.swing.JTextPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        edtVictoryConditions = new javax.swing.JTextPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        edtAftermath = new javax.swing.JTextPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        btnAddBonus = new javax.swing.JButton();
        txtAmount = new javax.swing.JFormattedTextField();
        txtTrackCost = new javax.swing.JFormattedTextField();
        txtObjective = new javax.swing.JTextField();
        txtBonus = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        txtReward = new javax.swing.JFormattedTextField();
        btnAddObjective = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        lstObjectives = new javax.swing.JList();
        jScrollPane6 = new javax.swing.JScrollPane();
        lstBonuses = new javax.swing.JList();
        jScrollPane7 = new javax.swing.JScrollPane();
        edtAttacker = new javax.swing.JTextPane();
        jScrollPane8 = new javax.swing.JScrollPane();
        edtDefender = new javax.swing.JTextPane();
        jScrollPane9 = new javax.swing.JScrollPane();
        edtSpecialRules = new javax.swing.JTextPane();
        jLabel24 = new javax.swing.JLabel();
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
        rmnuInformation = new javax.swing.JRadioButtonMenuItem();
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

        btnGroupTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/photo-album.png"))); // NOI18N
        btnGroupTop.setToolTipText("Group Information");
        btnGroupTop.setFocusable(false);
        btnGroupTop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGroupTop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGroupTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGroupTopActionPerformed(evt);
            }
        });
        jToolBar1.add(btnGroupTop);

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
        txtBottomGun.setPreferredSize(new java.awt.Dimension(15, 20));
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
        txtBottomPilot.setPreferredSize(new java.awt.Dimension(15, 20));
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

        cmbC3Bottom.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Off", "On" }));
        cmbC3Bottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbC3BottomActionPerformed(evt);
            }
        });

        jLabel17.setText("C3");

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
                                .addGap(57, 57, 57)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBottomGun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addGap(3, 3, 3)
                                .addComponent(txtBottomPilot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbC3Bottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                                .addComponent(tlbBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(spnBottom, javax.swing.GroupLayout.DEFAULT_SIZE, 986, Short.MAX_VALUE)))
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 409, Short.MAX_VALUE)
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
                                .addComponent(jLabel11)
                                .addComponent(cmbC3Bottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel17))
                            .addComponent(tlbBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnBottom, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)))
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
        txtTopGun.setPreferredSize(new java.awt.Dimension(15, 20));
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
        txtTopPilot.setPreferredSize(new java.awt.Dimension(15, 20));
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

        cmbC3Top.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Off", "On" }));
        cmbC3Top.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbC3TopActionPerformed(evt);
            }
        });

        jLabel16.setText("C3");

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
                                .addGap(26, 26, 26)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbC3Top, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
                                .addComponent(tlbTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(spnTop, javax.swing.GroupLayout.DEFAULT_SIZE, 987, Short.MAX_VALUE)))
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 414, Short.MAX_VALUE)
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
                                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtTopPilot, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmbC3Top, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16))
                                .addGroup(pnlTopLayout.createSequentialGroup()
                                    .addGap(4, 4, 4)
                                    .addComponent(jLabel8))
                                .addComponent(txtTopGun, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(pnlTopLayout.createSequentialGroup()
                                    .addGap(4, 4, 4)
                                    .addComponent(jLabel9))
                                .addComponent(tlbTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnTop, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)))
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

        jLabel12.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel12.setText("Setup");

        jLabel13.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel13.setText("Victory Conditions");

        jLabel14.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel14.setText("Aftermath");

        jLabel15.setFont(new java.awt.Font("Tahoma", 2, 11));
        jLabel15.setText("Only used for non-warchest system scenarios");

        jLabel1.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel1.setText("Situation");

        jScrollPane1.setViewportView(edtSituation);

        javax.swing.GroupLayout pnlSituationLayout = new javax.swing.GroupLayout(pnlSituation);
        pnlSituation.setLayout(pnlSituationLayout);
        pnlSituationLayout.setHorizontalGroup(
            pnlSituationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSituationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSituationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1112, Short.MAX_VALUE)
                    .addComponent(jLabel1))
                .addContainerGap())
        );
        pnlSituationLayout.setVerticalGroup(
            pnlSituationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSituationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPane2.setViewportView(edtSetup);

        jScrollPane3.setViewportView(edtVictoryConditions);

        jScrollPane5.setViewportView(edtAftermath);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel15)))
                .addContainerGap(790, Short.MAX_VALUE))
            .addComponent(pnlSituation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addContainerGap(1089, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1112, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1112, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1112, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(pnlSituation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                .addGap(22, 22, 22)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                .addGap(19, 19, 19))
        );

        jTabbedPane1.addTab("Scenario Information", jPanel2);

        jLabel19.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel19.setText("Attacker");

        jLabel20.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel20.setText("Defender");

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel22.setText("Track Cost:");

        btnAddBonus.setText("Add Bonus");
        btnAddBonus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddBonusActionPerformed(evt);
            }
        });

        txtAmount.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));

        txtTrackCost.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel21.setText("Optional Bonuses");

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel23.setText("Objectives");

        txtReward.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));

        btnAddObjective.setText("Add Objective");
        btnAddObjective.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddObjectiveActionPerformed(evt);
            }
        });

        lstObjectives.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstObjectives.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lstObjectivesKeyReleased(evt);
            }
        });
        jScrollPane4.setViewportView(lstObjectives);

        lstBonuses.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstBonuses.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lstBonusesKeyReleased(evt);
            }
        });
        jScrollPane6.setViewportView(lstBonuses);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBonus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddBonus, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTrackCost, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel21)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 558, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(txtReward, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtObjective, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddObjective))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtTrackCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtObjective, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnAddBonus)
                        .addComponent(btnAddObjective)
                        .addComponent(txtReward, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtBonus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jScrollPane7.setViewportView(edtAttacker);

        jScrollPane8.setViewportView(edtDefender);

        jScrollPane9.setViewportView(edtSpecialRules);

        jLabel24.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel24.setText("Special Rules");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 559, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 1112, Short.MAX_VALUE)
                    .addComponent(jLabel24))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane8)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Warchest Information", jPanel3);

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

        rmnuInformation.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        btnGrpViews.add(rmnuInformation);
        rmnuInformation.setText("Information Line");
        rmnuInformation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rmnuInformationActionPerformed(evt);
            }
        });
        jMenu6.add(rmnuInformation);

        jMenu5.add(jMenu6);

        jMenuBar1.add(jMenu5);

        jMenu2.setText("Design");

        mnuDesignBattleMech.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuDesignBattleMech.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/madcat-pencil.png"))); // NOI18N
        mnuDesignBattleMech.setText("BattleMech");
        mnuDesignBattleMech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDesignBattleMechActionPerformed(evt);
            }
        });
        jMenu2.add(mnuDesignBattleMech);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Combat Vehicle");
        jMenuItem2.setEnabled(false);
        jMenu2.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("Battle Armor");
        jMenuItem3.setEnabled(false);
        jMenu2.add(jMenuItem3);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText("Aero/Conv Fighter");
        jMenuItem4.setEnabled(false);
        jMenu2.add(jMenuItem4);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setText("Warship/Dropship");
        jMenuItem5.setEnabled(false);
        jMenu2.add(jMenuItem5);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem6.setText("Support Vehicle");
        jMenuItem6.setEnabled(false);
        jMenu2.add(jMenuItem6);

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
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
            .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1157, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblScenarioName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtScenarioName, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkUseForceModifier)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblForceMod)
                .addContainerGap(558, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1137, Short.MAX_VALUE)
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
        if ((scenario.getAttackerForce().isDirty) || (scenario.getDefenderForce().isDirty)) {
            switch (javax.swing.JOptionPane.showConfirmDialog(this, "Would you like to save your changes?")) {
                case javax.swing.JOptionPane.YES_OPTION:
                    this.mnuSaveActionPerformed(null);
                case javax.swing.JOptionPane.CANCEL_OPTION:
                    return;
            }
        }

        File forceFile = media.SelectFile(Prefs.get("LastOpenFile", ""), "bfb", "Load Force List");

        if (forceFile != null) {
            WaitCursor();

            try {
               loadScenario(forceFile.getCanonicalPath());
            } catch (Exception e) {
               Media.Messager("Issue loading file:\n " + e.getMessage() );
               System.out.println(e.getMessage());
               return;
            }
            
            DefaultCursor();
        }
}//GEN-LAST:event_mnuLoadActionPerformed

    private void lblUnitLogoBottomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblUnitLogoBottomMouseClicked
        updateLogo(lblUnitLogoBottom, scenario.getDefenderForce());
    }//GEN-LAST:event_lblUnitLogoBottomMouseClicked

    private void lblUnitLogoTopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblUnitLogoTopMouseClicked
        updateLogo(lblUnitLogoTop, scenario.getAttackerForce());
    }//GEN-LAST:event_lblUnitLogoTopMouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if ((scenario.getAttackerForce().isDirty) || (scenario.getDefenderForce().isDirty)) {
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
        scenario.getAttackerForce().ForceName = txtUnitNameTop.getText();
    }//GEN-LAST:event_txtUnitNameTopFocusLost

    private void txtUnitNameBottomFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUnitNameBottomFocusLost
        scenario.getDefenderForce().ForceName = txtUnitNameBottom.getText();
    }//GEN-LAST:event_txtUnitNameBottomFocusLost

    private void mnuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExitActionPerformed
        formWindowClosing(null);
    }//GEN-LAST:event_mnuExitActionPerformed

    private void mnuNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNewActionPerformed
        if ((scenario.getAttackerForce().isDirty) || (scenario.getDefenderForce().isDirty)) {
                    switch (javax.swing.JOptionPane.showConfirmDialog(this, "Would you like to save your changes?")) {
                        case javax.swing.JOptionPane.YES_OPTION:
                            this.mnuSaveActionPerformed(null);
                        case javax.swing.JOptionPane.CANCEL_OPTION:
                            return;
                    }
        }

        Prefs.put("CurrentBFBFile", "");
        this.scenario = new Scenario();
        edtAftermath.setText("");
        edtAttacker.setText("");
        edtDefender.setText("");
        edtSpecialRules.setText("");
        edtSetup.setText("");
        edtSituation.setText("");
        edtVictoryConditions.setText("");

        txtTrackCost.setText("");
        lstBonuses.setModel(scenario.getWarchest().getBonusList());
        lstObjectives.setModel(scenario.getWarchest().getObjectiveList());

        txtScenarioName.setText("");
        txtUnitNameTop.setText("");
        txtUnitNameBottom.setText("");
        lblUnitLogoTop.setIcon(null);
        lblUnitLogoBottom.setIcon(null);
        chkUseForceModifier.setSelected(false);

        Refresh();
    }//GEN-LAST:event_mnuNewActionPerformed

    private void mnuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveActionPerformed
        //Update fields
        KeyTyped.keyTyped(null);

        WaitCursor();
        try {
            File file;
            if ( !Prefs.get("CurrentBFBFile", "").isEmpty() && scenario.isOverwriteable() ) {
                file = new File(Prefs.get("CurrentBFBFile", ""));
            } else {
                file = media.SelectFile(txtScenarioName.getText() + ".bfb", "bfb", "Save");
                if (file == null) {
                    DefaultCursor();
                    return;
                }
            }
            String filename = file.getCanonicalPath();
            if ( ! filename.endsWith(".bfb") ) { filename += ".bfb";}

            //XMLWriter write = new XMLWriter(txtScenarioName.getText(), this.scenario.getAttackerForce(), this.scenario.getDefenderForce());
            //write.WriteXML(filename);

            XMLWriter writer = new XMLWriter();
            writer.WriteScenario(scenario, filename);
            
            Prefs.put("LastOpenBFBFile", filename);
            Prefs.put("CurrentBFBFile", filename);
            Media.Messager(this, "Forces saved to " + filename);
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
       BFBPrinter printer = new BFBPrinter(images);
       printer.setJobName(this.txtScenarioName.getText());
       printer.setTitle(this.txtScenarioName.getText());
       printer.Print();
       DefaultCursor();
}//GEN-LAST:event_mnuPrintForceActionPerformed

    private void mnuPrintUnitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintUnitsActionPerformed
        WaitCursor();
        images.preLoadMechImages();

        PagePrinter printer = new PagePrinter();
        for ( Force printForce : scenario.getForces() ) {

            //printer.setLogoPath(printForce.LogoPath);
            printer.setJobName(printForce.ForceName);

            for ( Unit u : printForce.getUnits() ) {
                u.LoadMech();
                PrintMech pm = new PrintMech(u.m, u.getMechwarrior(), u.getGunnery(), u.getPiloting(),images);
                pm.setLogoImage(images.getImage(printForce.LogoPath));
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
        } catch (Exception ex) {
            Media.Messager("Error while trying to open SSW\n" + ex.getMessage());
            System.out.println(ex.getMessage());
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
        if ( evt.getClickCount() == 2 ) { editUnit(tblTop, scenario.getAttackerForce()); }
    }//GEN-LAST:event_tblTopMouseClicked

    private void tblBottomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBottomMouseClicked
        if ( evt.getClickCount() == 2 ) { editUnit(tblBottom, scenario.getDefenderForce()); }
    }//GEN-LAST:event_tblBottomMouseClicked

    private void btnMULExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMULExportActionPerformed
        WaitCursor();
        MULWriter mw = new MULWriter();
        String dir = "";
        dir = media.GetDirectorySelection(this, Prefs.get("MULDirectory", ""));
        if ( dir.isEmpty() ) { 
            DefaultCursor();
            return;
        }

        Prefs.put("MULDirectory", dir);
        mw.setForce(scenario.getAttackerForce());
        try {
            mw.Write( dir + File.separator + scenario.getAttackerForce().ForceName );
        } catch (IOException ex) {
            Media.Messager("Unable to save " + scenario.getAttackerForce().ForceName + "\n" + ex.getMessage() );
            System.out.println(ex.getMessage());
        }

        mw.setForce(scenario.getDefenderForce());
        try {
            mw.Write( dir + File.separator + scenario.getDefenderForce().ForceName );
        } catch ( IOException ex ) {
            Media.Messager("Unable to save " + scenario.getDefenderForce().ForceName + "\n" + ex.getMessage() );
            System.out.println(ex.getMessage());
        }

        Media.Messager(this, "Your forces have been exported to " + dir);
        DefaultCursor();
}//GEN-LAST:event_btnMULExportActionPerformed

    private void chkUseForceModifierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUseForceModifierActionPerformed
        //lblForceMod.setVisible( chkUseForceModifier.isSelected() );
        scenario.updateOpFor(chkUseForceModifier.isSelected());
        updateFields();
    }//GEN-LAST:event_chkUseForceModifierActionPerformed

    private void btnOpenBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenBottomActionPerformed
        openForce( scenario.getDefenderForce() );
    }//GEN-LAST:event_btnOpenBottomActionPerformed

    private void btnSaveBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveBottomActionPerformed
        scenario.getDefenderForce().ForceName = txtUnitNameBottom.getText();
        saveForce( scenario.getDefenderForce() );
    }//GEN-LAST:event_btnSaveBottomActionPerformed

    private void btnOpenTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenTopActionPerformed
        openForce( scenario.getAttackerForce() );
}//GEN-LAST:event_btnOpenTopActionPerformed

    private void btnSaveTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveTopActionPerformed
        scenario.getAttackerForce().ForceName = txtUnitNameTop.getText();
        saveForce( scenario.getAttackerForce() );
}//GEN-LAST:event_btnSaveTopActionPerformed

    private void btnAddBottom1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBottom1ActionPerformed
        OpenDialog(scenario.getDefenderForce());
    }//GEN-LAST:event_btnAddBottom1ActionPerformed

    private void btnAddTop1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddTop1ActionPerformed
        OpenDialog(scenario.getAttackerForce());
    }//GEN-LAST:event_btnAddTop1ActionPerformed

    private void btnEditTop1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditTop1ActionPerformed
        editUnit(tblTop, scenario.getAttackerForce());
    }//GEN-LAST:event_btnEditTop1ActionPerformed

    private void btnEditBottom1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditBottom1ActionPerformed
        editUnit(tblBottom, scenario.getDefenderForce());
    }//GEN-LAST:event_btnEditBottom1ActionPerformed

    private void btnDeleteTop1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteTop1ActionPerformed
        removeUnits( tblTop, scenario.getAttackerForce() );
    }//GEN-LAST:event_btnDeleteTop1ActionPerformed

    private void btnDeleteBottom1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteBottom1ActionPerformed
        removeUnits( tblBottom, scenario.getDefenderForce() );
    }//GEN-LAST:event_btnDeleteBottom1ActionPerformed

    private void btnClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClipboardActionPerformed
        toClipboard( new Force[]{ scenario.getAttackerForce(), scenario.getDefenderForce() } );
    }//GEN-LAST:event_btnClipboardActionPerformed

    private void btnClipboardTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClipboardTopActionPerformed
        toClipboard( new Force[]{ scenario.getAttackerForce() } );
    }//GEN-LAST:event_btnClipboardTopActionPerformed

    private void btnClipboardBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClipboardBottomActionPerformed
        toClipboard( new Force[]{ scenario.getDefenderForce() } );
    }//GEN-LAST:event_btnClipboardBottomActionPerformed

    private void mnuSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveAsActionPerformed
        if ( txtScenarioName.getText().isEmpty() ) {
            Media.Messager("Please enter a scenario name before saving.");
            return;
        }

        if ( !scenario.getAttackerForce().isSaveable() || !scenario.getDefenderForce().isSaveable() ) {
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

            XMLWriter write = new XMLWriter(txtScenarioName.getText(), this.scenario.getAttackerForce(), this.scenario.getDefenderForce());
            write.WriteXML(filename);
            Prefs.put("LastOpenBFBFile", filename);
            Prefs.put("CurrentBFBFile", filename);
            Media.Messager(this, "Forces saved to " + filename);
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
            Media.Messager(e.getMessage());
        }
        DefaultCursor();
}//GEN-LAST:event_mnuSaveAsActionPerformed

    private void mnuExportClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportClipboardActionPerformed
        toClipboard( new Force[]{ scenario.getAttackerForce(), scenario.getDefenderForce() } );
    }//GEN-LAST:event_mnuExportClipboardActionPerformed

    private void mnuExportMULActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportMULActionPerformed
        btnMULExportActionPerformed(evt);
    }//GEN-LAST:event_mnuExportMULActionPerformed

    private void mnuExportTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportTextActionPerformed
        WaitCursor();
        TXTWriter txtWrite = new TXTWriter( new Force[]{ scenario.getAttackerForce(), scenario.getDefenderForce() } );
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
            overrideSkill( scenario.getAttackerForce(), Integer.parseInt(txtTopGun.getText()), Integer.parseInt(txtTopPilot.getText()) );
        }
}//GEN-LAST:event_txtTopGunKeyReleased

    private void txtTopGunFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTopGunFocusGained
        txtTopGun.selectAll();
    }//GEN-LAST:event_txtTopGunFocusGained

    private void txtTopPilotKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTopPilotKeyReleased
        if ( !txtTopGun.getText().isEmpty() && !txtTopPilot.getText().isEmpty() ) {
            overrideSkill( scenario.getAttackerForce(), Integer.parseInt(txtTopGun.getText()), Integer.parseInt(txtTopPilot.getText()) );
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
            overrideSkill( scenario.getDefenderForce(), Integer.parseInt(txtBottomGun.getText()), Integer.parseInt(txtBottomPilot.getText()) );
        }
}//GEN-LAST:event_txtBottomGunKeyReleased

    private void txtBottomPilotFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBottomPilotFocusGained
        txtBottomPilot.selectAll();
}//GEN-LAST:event_txtBottomPilotFocusGained

    private void txtBottomPilotKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBottomPilotKeyReleased
        if ( !txtBottomGun.getText().isEmpty() && !txtBottomPilot.getText().isEmpty() ) {
            overrideSkill( scenario.getDefenderForce(), Integer.parseInt(txtBottomGun.getText()), Integer.parseInt(txtBottomPilot.getText()) );
        }
}//GEN-LAST:event_txtBottomPilotKeyReleased

    private void btnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviewActionPerformed
        WaitCursor();
        PagePrinter printer = SetupPrinter();
        dlgPreview prv = new dlgPreview("Print Preview", this, printer, scenario, images);
        prv.setLocationRelativeTo(this);
        prv.setVisible(true);
        DefaultCursor();
    }//GEN-LAST:event_btnPreviewActionPerformed

    private void btnManageImagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManageImagesActionPerformed
        WaitCursor();
        dlgImageMgr img = new dlgImageMgr(this, scenario.getForces(), images);
        if ( img.hasWork ) {
            img.setLocationRelativeTo(this);
            img.setVisible(true);
        }
        DefaultCursor();
    }//GEN-LAST:event_btnManageImagesActionPerformed

    private void mnuPrintDlgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintDlgActionPerformed
        dlgPrint print = new dlgPrint(this, true, scenario, images);
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
        images.preLoadMechImages();

        PagePrinter printer = new PagePrinter();
        for ( Force printForce : scenario.getForces() ) {
            printer.setJobName(printForce.ForceName);

            for ( Unit u : printForce.getUnits() ) {
                u.LoadMech();
                PrintMech pm = new PrintMech(u.m, u.getMechwarrior(), u.getGunnery(), u.getPiloting(),images);
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
        scenario.getAttackerForce().ForceName = txtUnitNameTop.getText();
    }//GEN-LAST:event_txtUnitNameTopKeyReleased

    private void txtUnitNameBottomKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUnitNameBottomKeyReleased
        scenario.getDefenderForce().ForceName = txtUnitNameBottom.getText();
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
        balanceSkills( tblTop, scenario.getAttackerForce() );
    }//GEN-LAST:event_btnBalanceTopActionPerformed

    private void btnBalanceBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBalanceBottomActionPerformed
        balanceSkills( tblBottom, scenario.getDefenderForce() );
    }//GEN-LAST:event_btnBalanceBottomActionPerformed

    private void btnSwitchTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSwitchTopActionPerformed
        switchUnits( tblTop, scenario.getAttackerForce(), scenario.getDefenderForce() );
    }//GEN-LAST:event_btnSwitchTopActionPerformed

    private void btnSwitchBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSwitchBottomActionPerformed
        switchUnits( tblBottom, scenario.getDefenderForce(), scenario.getAttackerForce() );
    }//GEN-LAST:event_btnSwitchBottomActionPerformed

    private void btnISTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnISTopActionPerformed
        scenario.getAttackerForce().setType(BattleForce.InnerSphere);
    }//GEN-LAST:event_btnISTopActionPerformed

    private void btnCLTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCLTopActionPerformed
        scenario.getAttackerForce().setType(BattleForce.Clan);
    }//GEN-LAST:event_btnCLTopActionPerformed

    private void btnCSTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCSTopActionPerformed
        scenario.getAttackerForce().setType(BattleForce.Comstar);
    }//GEN-LAST:event_btnCSTopActionPerformed

    private void btnISBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnISBottomActionPerformed
        scenario.getDefenderForce().setType(BattleForce.InnerSphere);
    }//GEN-LAST:event_btnISBottomActionPerformed

    private void btnCLBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCLBottomActionPerformed
        scenario.getDefenderForce().setType(BattleForce.Clan);
    }//GEN-LAST:event_btnCLBottomActionPerformed

    private void btnCSBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCSBottomActionPerformed
        scenario.getDefenderForce().setType(BattleForce.Comstar);
    }//GEN-LAST:event_btnCSBottomActionPerformed

    private void mnuPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintActionPerformed
        dlgPrint print = new dlgPrint(this, true, scenario, images);
        print.setLocationRelativeTo(this);
        print.setVisible(true);
    }//GEN-LAST:event_mnuPrintActionPerformed

    private void mnuPrintPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintPreviewActionPerformed
        btnPreviewActionPerformed(evt);
}//GEN-LAST:event_mnuPrintPreviewActionPerformed

    private void mnuBVListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBVListActionPerformed
        Media.Messager("This will output a csv list of mechs and also a list of EVERY SINGLE Mech's cost and BV2 calculation!");
        WaitCursor();
        if ( dOpen.getList() == null ) { dOpen.LoadList(true); }
        
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
        if ( dOpen.getList() == null ) { dOpen.LoadList(true); }

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
        WaitCursor();
        scenario.setModel(new tbTotalWarfare());
        Refresh();
        DefaultCursor();
    }//GEN-LAST:event_rmnuTWModelActionPerformed

    private void rmnuBFModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rmnuBFModelActionPerformed
        WaitCursor();
        scenario.setModel(new tbBattleForce());
        Refresh();
        DefaultCursor();
    }//GEN-LAST:event_rmnuBFModelActionPerformed

    private void btnAddObjectiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddObjectiveActionPerformed
        if ( !txtReward.getText().isEmpty() && !txtObjective.getText().isEmpty() ) {
            scenario.getWarchest().getObjectives().add(new Objective(txtObjective.getText(), Integer.parseInt(txtReward.getText())));
            txtReward.setText("");
            txtObjective.setText("");
            lstObjectives.setModel(scenario.getWarchest().getObjectiveList());
            scenario.MakeDirty(true);
        }
    }//GEN-LAST:event_btnAddObjectiveActionPerformed

    private void rmnuInformationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rmnuInformationActionPerformed
        WaitCursor();
        scenario.setModel(new tbChatInfo());
        Refresh();
        DefaultCursor();
}//GEN-LAST:event_rmnuInformationActionPerformed

    private void btnAddBonusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBonusActionPerformed
        if ( !txtAmount.getText().isEmpty() && !txtBonus.getText().isEmpty() ) {
            scenario.getWarchest().getBonuses().add(new Bonus(txtBonus.getText(), Integer.parseInt(txtAmount.getText())));
            txtAmount.setText("");
            txtBonus.setText("");
            lstBonuses.setModel(scenario.getWarchest().getBonusList());
            scenario.MakeDirty(true);
        }
    }//GEN-LAST:event_btnAddBonusActionPerformed

    private void lstObjectivesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lstObjectivesKeyReleased
        if ( lstObjectives.getSelectedValues().length > 0 ) {
            if ( evt.getKeyCode() == KeyEvent.VK_DELETE ) {
                Object[] rows = lstObjectives.getSelectedValues();
                for ( int i=0; i < rows.length; i++ ) {
                    scenario.getWarchest().getObjectives().remove(rows[i]);
                }
                lstObjectives.setModel(scenario.getWarchest().getObjectiveList());
            }
        }
    }//GEN-LAST:event_lstObjectivesKeyReleased

    private void lstBonusesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lstBonusesKeyReleased
        if ( lstBonuses.getSelectedValues().length > 0 ) {
            if ( evt.getKeyCode() == KeyEvent.VK_DELETE ) {
                Object[] rows = lstBonuses.getSelectedValues();
                for ( int i=0; i < rows.length; i++ ) {
                    scenario.getWarchest().getBonuses().remove(rows[i]);
                }
                lstBonuses.setModel(scenario.getWarchest().getObjectiveList());
            }
        }
    }//GEN-LAST:event_lstBonusesKeyReleased

    private void btnGroupTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGroupTopActionPerformed
        ManageGroup(  );
    }//GEN-LAST:event_btnGroupTopActionPerformed

    private void cmbC3TopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbC3TopActionPerformed
        ChangeC3( scenario.getAttackerForce(), cmbC3Top );
    }//GEN-LAST:event_cmbC3TopActionPerformed

    private void cmbC3BottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbC3BottomActionPerformed
        ChangeC3( scenario.getDefenderForce(), cmbC3Bottom );
    }//GEN-LAST:event_cmbC3BottomActionPerformed

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
    private javax.swing.JButton btnGroupTop;
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
    private javax.swing.JComboBox cmbC3Bottom;
    private javax.swing.JComboBox cmbC3Top;
    private javax.swing.JTextPane edtAftermath;
    private javax.swing.JTextPane edtAttacker;
    private javax.swing.JTextPane edtDefender;
    private javax.swing.JTextPane edtSetup;
    private javax.swing.JTextPane edtSituation;
    private javax.swing.JTextPane edtSpecialRules;
    private javax.swing.JTextPane edtVictoryConditions;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
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
    private javax.swing.JList lstBonuses;
    private javax.swing.JList lstObjectives;
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
    private javax.swing.JPanel pnlSituation;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JRadioButtonMenuItem rmnuBFModel;
    private javax.swing.JRadioButtonMenuItem rmnuInformation;
    private javax.swing.JRadioButtonMenuItem rmnuTWModel;
    private javax.swing.JScrollPane spnBottom;
    private javax.swing.JScrollPane spnTop;
    private javax.swing.JTable tblBottom;
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

    /**
     * @return the images
     */
    public ImageTracker getImageTracker() {
        return images;
    }

}
