/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * dlgPrint.java
 *
 * Created on Aug 31, 2009, 12:56:26 PM
 */

package BFB.GUI;

import Print.*;
import Force.*;
import common.*;
import battleforce.BattleForce;

import BFB.Preview.dlgPreview;
import java.awt.Cursor;
import java.util.Vector;
import java.util.prefs.Preferences;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.DefaultComboBoxModel;


public class dlgPrint extends javax.swing.JDialog {
    private PrintService[] service = PrintServiceLookup.lookupPrintServices(null, null);
    private frmBase parent;
    private Runtime runtime = Runtime.getRuntime();
    private Preferences bfbPrefs = Preferences.userNodeForPackage("/bfb/gui/frmBase".getClass());
    private Preferences sswPrefs = Preferences.userNodeForPackage("/ssw/gui/frmMain".getClass());

    /** Creates new form dlgPrint */
    public dlgPrint(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.parent = (frmBase) parent;

        cmbPrinters.setModel(new DefaultComboBoxModel());

        chkCanon.setSelected(sswPrefs.getBoolean(Constants.Format_CanonPattern, false));
        chkRS.setSelected(sswPrefs.getBoolean(Constants.Format_Recordsheet, false));
        chkTables.setSelected(sswPrefs.getBoolean(Constants.Format_Tables, false));
        chkUseHexConversion.setSelected(sswPrefs.getBoolean(Constants.Format_ConvertTerrain, false));
        cmbHexConvFactor.setSelectedItem(sswPrefs.getInt(Constants.Format_TerrainModifier, 1));

        chkPrintForce.setSelected(bfbPrefs.getBoolean(Constants.Print_ForceList, true));
        chkPrintFireChits.setSelected(bfbPrefs.getBoolean(Constants.Print_FireDeclaration, false));
        chkPrintRecordsheets.setSelected(bfbPrefs.getBoolean(Constants.Print_Recordsheet, true));
        chkPrintBattleforce.setSelected(bfbPrefs.getBoolean(Constants.Print_BattleForce, false));
        chkBFOnePerPage.setSelected(bfbPrefs.getBoolean(Constants.Format_OneForcePerPage, false));


        PrintService defaultPrinter = PrintServiceLookup.lookupDefaultPrintService();
        if ( defaultPrinter != null ) { ((DefaultComboBoxModel) cmbPrinters.getModel()).addElement(defaultPrinter.getName()); }
        for (PrintService printer : service ) {
            if ( !printer.equals(defaultPrinter) ) {
                ((DefaultComboBoxModel) cmbPrinters.getModel()).addElement(printer.getName());
            }
        }

        Verify();
    }

    private void Verify() {
        setStatus("Verifying...");

        chkRS.setEnabled(chkPrintRecordsheets.isSelected());
        chkTables.setEnabled(chkPrintRecordsheets.isSelected());
        chkCanon.setEnabled(chkPrintRecordsheets.isSelected());
        chkImage.setEnabled(chkPrintRecordsheets.isSelected() || chkPrintBattleforce.isSelected());
        chkLogo.setEnabled(chkPrintRecordsheets.isSelected() || chkPrintBattleforce.isSelected());
        chkBFOnePerPage.setEnabled(chkPrintBattleforce.isSelected());

        chkUseHexConversion.setEnabled(chkPrintRecordsheets.isSelected());
        cmbHexConvFactor.setEnabled(chkPrintRecordsheets.isSelected());

        if ( chkRS.isSelected() ) {
            chkTables.setSelected(false);
            chkTables.setEnabled(false);
            chkCanon.setSelected(true);
            chkCanon.setEnabled(false);

            chkUseHexConversion.setSelected(false);
            chkUseHexConversion.setEnabled(false);
            cmbHexConvFactor.setSelectedItem(1);
            cmbHexConvFactor.setEnabled(false);
        }
        
        if ( chkPrintRecordsheets.isSelected() ) {
            if ( chkTables.isSelected() ) {
                lblRecordsheetIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/RecordsheetTables_BG.png")));
            } else {
                lblRecordsheetIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/Recordsheet_BG.png")));
            }
        }

        if ( ( runtime.maxMemory() / 1024 ) < 260160 ) {
            chkRS.setSelected(false);
            chkRS.setEnabled(false);
            chkCanon.setSelected(false);
            chkCanon.setEnabled(false);
            setStatus("Not enough memory to print canon patterns");
            this.repaint();
        }
    }

    private PagePrinter SetupPrinter() {
        PagePrinter printer = new PagePrinter();

        printer.setJobName(parent.getScenario());
        printer.selectService(cmbPrinters.getSelectedItem().toString());

        if ( chkPrintForce.isSelected() ) {
            ForceList sheet = new ForceList();
            sheet.AddForces(new Force[]{parent.scenario.getAttackerForce(), parent.scenario.getDefenderForce()});
            printer.Append( BFBPrinter.Letter.toPage(), sheet );
        }

        if ( chkPrintFireChits.isSelected() ) {
            PrintDeclaration fire = new PrintDeclaration();
            fire.AddForces(new Force[]{parent.scenario.getAttackerForce(), parent.scenario.getDefenderForce()});
            printer.Append( BFBPrinter.Letter.toPage(), fire );
        }

        if ( chkPrintBattleforce.isSelected() ) {
            if ( chkBFOnePerPage.isSelected() ) {
                Vector<BattleForce> forces = new Vector<BattleForce>();
                forces.addAll(parent.scenario.getAttackerForce().toBattleForceByGroup());
                forces.addAll(parent.scenario.getDefenderForce().toBattleForceByGroup());

                for ( BattleForce f : forces ) {
                    Battleforce bf = new Battleforce(f);
                    bf.setPrintLogo(chkLogo.isSelected());
                    bf.setPrintMechs(chkImage.isSelected());
                    printer.Append( BFBPrinter.Letter.toPage(), bf);
                }
            } else {
                Battleforce topBF = new Battleforce(parent.scenario.getAttackerForce().toBattleForce());
                topBF.setPrintLogo(chkLogo.isSelected());
                topBF.setPrintMechs(chkImage.isSelected());

                Battleforce bottomBF = new Battleforce(parent.scenario.getDefenderForce().toBattleForce());
                bottomBF.setPrintLogo(chkLogo.isSelected());
                bottomBF.setPrintMechs(chkImage.isSelected());

                printer.Append( BFBPrinter.Letter.toPage(), topBF );
                printer.Append( BFBPrinter.Letter.toPage(), bottomBF );
            }
        }

        if ( chkPrintRecordsheets.isSelected() ) {
            Force[] forces = new Force[]{parent.scenario.getAttackerForce(), parent.scenario.getDefenderForce()};

            for ( int f=0; f < forces.length; f++ ) {
                Force force = forces[f];

                for ( int m=0; m < force.Units.size(); m++ ) {
                    Unit u = (Unit) force.Units.get(m);
                    u.LoadMech();
                    PrintMech pm = new PrintMech(u.m,u.getMechwarrior(), u.getGunnery(), u.getPiloting());
                    pm.setCanon(chkCanon.isSelected());
                    pm.setCharts(chkTables.isSelected());
                    if ( chkUseHexConversion.isSelected() ) {
                        pm.SetMiniConversion(cmbHexConvFactor.getSelectedIndex());
                    }
                    if ( !chkImage.isSelected() ) {
                        pm.setMechImage(null);
                    }
                    if ( chkLogo.isSelected() ) {
                        pm.setLogoImage(force.getLogo());
                    }
                    printer.Append( BFBPrinter.Letter.toPage(), pm);
                }
            }
        }

        return printer;
    }

    private void setPreferences() {
        sswPrefs.putBoolean(Constants.Format_CanonPattern, chkCanon.isSelected());
        sswPrefs.putBoolean(Constants.Format_Recordsheet, chkRS.isSelected());
        sswPrefs.putBoolean(Constants.Format_Tables, chkTables.isSelected());
        sswPrefs.putBoolean(Constants.Format_ConvertTerrain, chkUseHexConversion.isSelected());
        sswPrefs.putInt(Constants.Format_TerrainModifier, cmbHexConvFactor.getSelectedIndex());

        bfbPrefs.putBoolean(Constants.Print_ForceList, chkPrintForce.isSelected());
        bfbPrefs.putBoolean(Constants.Print_FireDeclaration, chkPrintFireChits.isSelected());
        bfbPrefs.putBoolean(Constants.Print_Recordsheet, chkPrintRecordsheets.isSelected());
        bfbPrefs.putBoolean(Constants.Print_BattleForce, chkPrintBattleforce.isSelected());
        bfbPrefs.putBoolean(Constants.Format_OneForcePerPage, chkBFOnePerPage.isSelected());
    }

    private void setStatus( String message ) {
        lblStatus.setText(message);
        lblStatus.firePropertyChange("Text", 0, 1);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlWhat = new javax.swing.JPanel();
        chkPrintForce = new javax.swing.JCheckBox();
        chkPrintFireChits = new javax.swing.JCheckBox();
        chkPrintRecordsheets = new javax.swing.JCheckBox();
        chkPrintBattleforce = new javax.swing.JCheckBox();
        lblForceIcon = new javax.swing.JLabel();
        lblFireDecIcon = new javax.swing.JLabel();
        lblRecordsheetIcon = new javax.swing.JLabel();
        lblBattleForceIcon = new javax.swing.JLabel();
        chkPrintScenario = new javax.swing.JCheckBox();
        chkPrintBattleforce1 = new javax.swing.JCheckBox();
        lblRecordsheetIcon1 = new javax.swing.JLabel();
        lblBattleForceIcon1 = new javax.swing.JLabel();
        pnlWhere = new javax.swing.JPanel();
        cmbPrinters = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        pnlHow = new javax.swing.JPanel();
        chkTables = new javax.swing.JCheckBox();
        chkCanon = new javax.swing.JCheckBox();
        chkUseHexConversion = new javax.swing.JCheckBox();
        chkImage = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        chkLogo = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        cmbHexConvFactor = new javax.swing.JComboBox();
        lblInches = new javax.swing.JLabel();
        lblOneHex = new javax.swing.JLabel();
        btnImageMgr = new javax.swing.JButton();
        chkRS = new javax.swing.JCheckBox();
        chkBFOnePerPage = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        btnPrint = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnPreview = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Printing Options");
        setModal(true);
        setResizable(false);

        pnlWhat.setBorder(javax.swing.BorderFactory.createTitledBorder("What To Print"));

        chkPrintForce.setSelected(true);
        chkPrintForce.setText("Force List");

        chkPrintFireChits.setText("Fire Declaration Chits");

        chkPrintRecordsheets.setSelected(true);
        chkPrintRecordsheets.setText("Unit Recordsheets");
        chkPrintRecordsheets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Verify(evt);
            }
        });

        chkPrintBattleforce.setText("BattleForce Sheets");
        chkPrintBattleforce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Verify(evt);
            }
        });

        lblForceIcon.setBackground(new java.awt.Color(255, 255, 255));
        lblForceIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/ForceList_BG.png"))); // NOI18N
        lblForceIcon.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        lblFireDecIcon.setBackground(new java.awt.Color(255, 255, 255));
        lblFireDecIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/FireDec_BG.png"))); // NOI18N
        lblFireDecIcon.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        lblRecordsheetIcon.setBackground(new java.awt.Color(255, 255, 255));
        lblRecordsheetIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/Recordsheet_BG.png"))); // NOI18N
        lblRecordsheetIcon.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        lblBattleForceIcon.setBackground(new java.awt.Color(255, 255, 255));
        lblBattleForceIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/BattleForce_BG.png"))); // NOI18N
        lblBattleForceIcon.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        chkPrintScenario.setSelected(true);
        chkPrintScenario.setText("Scenario Sheet");
        chkPrintScenario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPrintScenarioVerify(evt);
            }
        });

        chkPrintBattleforce1.setText("BattleForce Sheets");
        chkPrintBattleforce1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPrintBattleforce1Verify(evt);
            }
        });

        lblRecordsheetIcon1.setBackground(new java.awt.Color(255, 255, 255));
        lblRecordsheetIcon1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/Recordsheet_BG.png"))); // NOI18N
        lblRecordsheetIcon1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        lblBattleForceIcon1.setBackground(new java.awt.Color(255, 255, 255));
        lblBattleForceIcon1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/BattleForce_BG.png"))); // NOI18N
        lblBattleForceIcon1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout pnlWhatLayout = new javax.swing.GroupLayout(pnlWhat);
        pnlWhat.setLayout(pnlWhatLayout);
        pnlWhatLayout.setHorizontalGroup(
            pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWhatLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlWhatLayout.createSequentialGroup()
                        .addGroup(pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkPrintRecordsheets)
                            .addGroup(pnlWhatLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(lblRecordsheetIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlWhatLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(lblBattleForceIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chkPrintBattleforce)))
                    .addGroup(pnlWhatLayout.createSequentialGroup()
                        .addGroup(pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkPrintScenario)
                            .addGroup(pnlWhatLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(lblRecordsheetIcon1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlWhatLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(lblBattleForceIcon1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chkPrintBattleforce1)))
                    .addGroup(pnlWhatLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(chkPrintForce)
                            .addComponent(lblForceIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(38, 38, 38)
                        .addGroup(pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlWhatLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(lblFireDecIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chkPrintFireChits))))
                .addGap(10, 10, 10))
        );
        pnlWhatLayout.setVerticalGroup(
            pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWhatLayout.createSequentialGroup()
                .addGroup(pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlWhatLayout.createSequentialGroup()
                        .addComponent(chkPrintForce)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblForceIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlWhatLayout.createSequentialGroup()
                        .addComponent(chkPrintFireChits)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblFireDecIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlWhatLayout.createSequentialGroup()
                        .addComponent(chkPrintScenario)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRecordsheetIcon1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlWhatLayout.createSequentialGroup()
                        .addComponent(chkPrintBattleforce1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblBattleForceIcon1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPrintRecordsheets)
                    .addComponent(chkPrintBattleforce))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblRecordsheetIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBattleForceIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlWhere.setBorder(javax.swing.BorderFactory.createTitledBorder("Where To Print"));

        jLabel2.setText("Printer:");

        javax.swing.GroupLayout pnlWhereLayout = new javax.swing.GroupLayout(pnlWhere);
        pnlWhere.setLayout(pnlWhereLayout);
        pnlWhereLayout.setHorizontalGroup(
            pnlWhereLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWhereLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbPrinters, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(314, Short.MAX_VALUE))
        );
        pnlWhereLayout.setVerticalGroup(
            pnlWhereLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWhereLayout.createSequentialGroup()
                .addGroup(pnlWhereLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cmbPrinters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlHow.setBorder(javax.swing.BorderFactory.createTitledBorder("How To Print"));

        chkTables.setSelected(true);
        chkTables.setText("Print Charts and Tables");
        chkTables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Verify(evt);
            }
        });

        chkCanon.setSelected(true);
        chkCanon.setText("Print Canon Dot Patterns");

        chkUseHexConversion.setText("Print Miniatures Scale");
        chkUseHexConversion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUseHexConversionActionPerformed(evt);
            }
        });

        chkImage.setSelected(true);
        chkImage.setText("Print Mech Images ");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("* Must be added to the file already");

        chkLogo.setSelected(true);
        chkLogo.setText("Print Unit Logo");

        cmbHexConvFactor.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5" }));
        cmbHexConvFactor.setEnabled(false);

        lblInches.setText("Inches");
        lblInches.setEnabled(false);

        lblOneHex.setText("One Hex equals");
        lblOneHex.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblOneHex)
                .addGap(1, 1, 1)
                .addComponent(cmbHexConvFactor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInches)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(cmbHexConvFactor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lblOneHex))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(lblInches))
        );

        btnImageMgr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/images-stack.png"))); // NOI18N
        btnImageMgr.setBorder(null);
        btnImageMgr.setIconTextGap(2);
        btnImageMgr.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnImageMgr.setOpaque(false);
        btnImageMgr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImageMgrActionPerformed(evt);
            }
        });

        chkRS.setText("Print TRO Format");
        chkRS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRSVerify(evt);
            }
        });

        chkBFOnePerPage.setText("Print One Unit Per Page");
        chkBFOnePerPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkBFOnePerPageActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlHowLayout = new javax.swing.GroupLayout(pnlHow);
        pnlHow.setLayout(pnlHowLayout);
        pnlHowLayout.setHorizontalGroup(
            pnlHowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHowLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlHowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkRS)
                    .addComponent(chkTables)
                    .addComponent(chkCanon)
                    .addComponent(chkUseHexConversion)
                    .addGroup(pnlHowLayout.createSequentialGroup()
                        .addComponent(chkImage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnImageMgr))
                    .addComponent(chkLogo)
                    .addGroup(pnlHowLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(pnlHowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(chkBFOnePerPage))
                .addContainerGap(98, Short.MAX_VALUE))
        );
        pnlHowLayout.setVerticalGroup(
            pnlHowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHowLayout.createSequentialGroup()
                .addComponent(chkRS)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkTables)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCanon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUseHexConversion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlHowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(chkImage)
                    .addComponent(btnImageMgr))
                .addGap(1, 1, 1)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkLogo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkBFOnePerPage)
                .addContainerGap(104, Short.MAX_VALUE))
        );

        btnPrint.setText("Print");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(btnPrint)
                .addGap(1, 1, 1)
                .addComponent(btnCancel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnPrint)
                .addComponent(btnCancel))
        );

        btnPreview.setText("Preview");
        btnPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviewActionPerformed(evt);
            }
        });

        lblStatus.setText("Memory Available: ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlWhere, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(pnlWhat, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(pnlHow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnPreview)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlWhere, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlWhat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlHow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnPreview)
                        .addComponent(lblStatus))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkUseHexConversionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUseHexConversionActionPerformed
        lblOneHex.setEnabled( chkUseHexConversion.isSelected() );
        cmbHexConvFactor.setEnabled( chkUseHexConversion.isSelected() );
        lblInches.setEnabled( chkUseHexConversion.isSelected() );
        
        Verify();
}//GEN-LAST:event_chkUseHexConversionActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        setPreferences();

        setStatus("Loading Requested Sheets");
        PagePrinter printer = SetupPrinter();

        setStatus("Sending to Printer");
        printer.Print();

        this.setCursor(Cursor.getDefaultCursor());
        this.setVisible(false);
    }//GEN-LAST:event_btnPrintActionPerformed

    private void Verify(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Verify
        Verify();
    }//GEN-LAST:event_Verify

    private void btnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviewActionPerformed
        setStatus("Loading Requested Sheets");
        PagePrinter printer = SetupPrinter();
        dlgPreview prv = new dlgPreview("Print Preview", this, printer.Preview());
        prv.setLocationRelativeTo(this);
        prv.setVisible(true);
    }//GEN-LAST:event_btnPreviewActionPerformed

    private void btnImageMgrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImageMgrActionPerformed
        dlgMechImages dlgImg = new dlgMechImages(parent, new Force[]{parent.scenario.getAttackerForce(), parent.scenario.getDefenderForce()});
        if ( dlgImg.hasWork ) {
            dlgImg.setLocationRelativeTo(this);
            dlgImg.setVisible(true);
        }
    }//GEN-LAST:event_btnImageMgrActionPerformed

    private void chkRSVerify(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRSVerify
        Verify();
}//GEN-LAST:event_chkRSVerify

    private void chkBFOnePerPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkBFOnePerPageActionPerformed
        Verify();
    }//GEN-LAST:event_chkBFOnePerPageActionPerformed

    private void chkPrintScenarioVerify(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPrintScenarioVerify
        // TODO add your handling code here:
}//GEN-LAST:event_chkPrintScenarioVerify

    private void chkPrintBattleforce1Verify(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPrintBattleforce1Verify
        // TODO add your handling code here:
    }//GEN-LAST:event_chkPrintBattleforce1Verify

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnImageMgr;
    private javax.swing.JButton btnPreview;
    private javax.swing.JButton btnPrint;
    private javax.swing.JCheckBox chkBFOnePerPage;
    private javax.swing.JCheckBox chkCanon;
    private javax.swing.JCheckBox chkImage;
    private javax.swing.JCheckBox chkLogo;
    private javax.swing.JCheckBox chkPrintBattleforce;
    private javax.swing.JCheckBox chkPrintBattleforce1;
    private javax.swing.JCheckBox chkPrintFireChits;
    private javax.swing.JCheckBox chkPrintForce;
    private javax.swing.JCheckBox chkPrintRecordsheets;
    private javax.swing.JCheckBox chkPrintScenario;
    private javax.swing.JCheckBox chkRS;
    private javax.swing.JCheckBox chkTables;
    private javax.swing.JCheckBox chkUseHexConversion;
    private javax.swing.JComboBox cmbHexConvFactor;
    private javax.swing.JComboBox cmbPrinters;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblBattleForceIcon;
    private javax.swing.JLabel lblBattleForceIcon1;
    private javax.swing.JLabel lblFireDecIcon;
    private javax.swing.JLabel lblForceIcon;
    private javax.swing.JLabel lblInches;
    private javax.swing.JLabel lblOneHex;
    private javax.swing.JLabel lblRecordsheetIcon;
    private javax.swing.JLabel lblRecordsheetIcon1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel pnlHow;
    private javax.swing.JPanel pnlWhat;
    private javax.swing.JPanel pnlWhere;
    // End of variables declaration//GEN-END:variables

}
