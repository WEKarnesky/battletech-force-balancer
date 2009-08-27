/*
 * dlgUnit.java
 *
 * Created on Apr 8, 2009, 12:22:17 PM
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

import BFB.Skills;
import BFB.Unit;
import java.awt.Color;
import javax.swing.SpinnerNumberModel;
import ssw.components.ifLoadout;
import ssw.filehandlers.TXTWriter;

public class dlgUnit extends javax.swing.JDialog {
    private Unit unit;
    private boolean Default = true;
    private frmBase Parent;
    public boolean Result = false;
    private Skills skills;

    public dlgUnit(java.awt.Frame parent, boolean modal, Unit u) {
        super(parent, modal);
        Parent = (frmBase) parent;
        unit = u;
        initComponents();

        lblModel.setText(u.TypeModel);
        lblTonnage.setText(u.Tonnage + " Tons");
        txtMechwarrior.setText(u.Mechwarrior);
        cmbGunnery.setSelectedIndex(u.Gunnery);
        cmbPiloting.setSelectedIndex(u.Piloting);
        chkC3Active.setSelected(u.UsingC3);
        txtMod.setText(u.MiscMod+"");
        lblFilename.setText(u.Filename);
        spnSkillSeperationLimit.setModel(new SpinnerNumberModel(3, 0, 7, 1));

        skills = new Skills(u.BaseBV);
        setSkills();

        u.LoadMech();
        if ( u.m != null ) {
            if ( u.m.IsOmnimech() ) {
                String curConfig = u.m.GetLoadout().GetName();
                int BV = u.m.GetCurrentBV();
                for (int i=0; i < u.m.GetLoadouts().size(); i++) {
                    ifLoadout config = (ifLoadout) u.m.GetLoadouts().get(i);
                    u.m.SetCurLoadout(config.GetName());
                    cmbConfiguration.addItem(config.GetName() + " (" + u.m.GetCurrentBV() + ")");
                }
                u.m.SetCurLoadout(curConfig);
                cmbConfiguration.setSelectedItem(curConfig + " (" + BV + ")");
                Default = false;
            } else {
                pnlConfiguration.setVisible(false);
            }
            setC3();
            setTRO();
        } else {
            pnlConfiguration.setVisible(false);
            lblFilename.setForeground(new Color(Color.red.getRGB()));
        }
    }

    private void setBV() {
        lblBaseBV.setText( String.format("%1$,.0f", unit.BaseBV) );
        lblTotalBV.setText( String.format("%1$,.0f", unit.TotalBV) );
    }

    private void setTRO() {
        TXTWriter txt = new TXTWriter(unit.m);
        txt.CurrentLoadoutOnly = true;
        tpnTRO.setText(txt.GetMiniTextExport());
        tpnTRO.setCaretPosition(0);
    }

    private void setC3() {
        if ( ! unit.m.HasC3() ) {
            chkC3Active.setSelected(false);
            chkC3Active.setVisible(false);
        }
    }

    private void setSkills() {
        lstSkills.setModel(skills.getListModel());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGrpSkill = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        txtMechwarrior = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cmbPiloting = new javax.swing.JComboBox();
        lblTonnage = new javax.swing.JLabel();
        cmbGunnery = new javax.swing.JComboBox();
        pnlConfiguration = new javax.swing.JPanel();
        cmbConfiguration = new javax.swing.JComboBox();
        lblConfig = new javax.swing.JLabel();
        txtMod = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        lblTotalBV = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblBaseBV = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        lblModel = new javax.swing.JLabel();
        chkC3Active = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        spnTRO = new javax.swing.JScrollPane();
        tpnTRO = new javax.swing.JTextPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstSkills = new javax.swing.JList();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtBVLimit = new javax.swing.JTextField();
        btnFilter = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        spnSkillSeperationLimit = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        rdoGunnery = new javax.swing.JRadioButton();
        rdoPiloting = new javax.swing.JRadioButton();
        rdoNeither = new javax.swing.JRadioButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        cmbSkillLevel = new javax.swing.JComboBox();
        lblRandomSkill = new javax.swing.JLabel();
        btnRandomGen = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        btnApply = new javax.swing.JButton();
        pnlFile = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblFilename = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Modify Unit");

        jLabel4.setText("Gun");

        jLabel3.setText("Mechwarrior");

        cmbPiloting.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7" }));
        cmbPiloting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPilotingActionPerformed(evt);
            }
        });

        lblTonnage.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblTonnage.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTonnage.setText("100 Tons");

        cmbGunnery.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7" }));
        cmbGunnery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbGunneryActionPerformed(evt);
            }
        });

        cmbConfiguration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbConfigurationActionPerformed(evt);
            }
        });

        lblConfig.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblConfig.setText("Selected Configuration:");

        javax.swing.GroupLayout pnlConfigurationLayout = new javax.swing.GroupLayout(pnlConfiguration);
        pnlConfiguration.setLayout(pnlConfigurationLayout);
        pnlConfigurationLayout.setHorizontalGroup(
            pnlConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlConfigurationLayout.createSequentialGroup()
                .addComponent(lblConfig, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbConfiguration, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlConfigurationLayout.setVerticalGroup(
            pnlConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(cmbConfiguration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lblConfig))
        );

        txtMod.setText("1.0");
        txtMod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtModActionPerformed(evt);
            }
        });

        jLabel13.setText("Adjusted:");

        lblTotalBV.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblTotalBV.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalBV.setText("0,000 BV");

        jLabel8.setText("Base:");

        lblBaseBV.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblBaseBV.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBaseBV.setText("0,000 BV");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblBaseBV)
                    .addComponent(lblTotalBV)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBaseBV)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalBV)
                    .addComponent(jLabel13)))
        );

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(btnSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancel))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnSave)
                .addComponent(btnCancel))
        );

        jLabel6.setText("Mod");

        lblModel.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblModel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblModel.setText("Sirocco SRC-3C BattleMech");

        chkC3Active.setText("C3 Active");

        jLabel5.setText("Plt");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(pnlConfiguration, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(243, 243, 243)
                        .addComponent(lblTonnage))
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(txtMechwarrior, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbGunnery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(39, 39, 39)
                                .addComponent(jLabel6))
                            .addComponent(cmbPiloting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(39, 39, 39)
                                .addComponent(txtMod, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(lblModel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkC3Active))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblModel)
                    .addComponent(lblTonnage))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlConfiguration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkC3Active))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMechwarrior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbGunnery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbPiloting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(3, 3, 3)
                        .addComponent(txtMod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        spnTRO.setBorder(null);
        spnTRO.setPreferredSize(new java.awt.Dimension(400, 550));

        tpnTRO.setBorder(null);
        tpnTRO.setEditable(false);
        tpnTRO.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        tpnTRO.setText("--------------------------------------------------------------------------------");
        tpnTRO.setPreferredSize(new java.awt.Dimension(400, 550));
        spnTRO.setViewportView(tpnTRO);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spnTRO, javax.swing.GroupLayout.DEFAULT_SIZE, 591, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spnTRO, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Technical Readout", jPanel4);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        lstSkills.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "0 / 0  (1000)", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstSkills.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstSkillsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(lstSkills);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Limiters"));

        jLabel2.setText("Max BV:");

        txtBVLimit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBVLimitActionPerformed(evt);
            }
        });
        txtBVLimit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtBVLimitKeyTyped(evt);
            }
        });

        btnFilter.setText("Filter");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

        jLabel7.setText("Max Skill Seperation:");

        spnSkillSeperationLimit.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnSkillSeperationLimitStateChanged(evt);
            }
        });

        jLabel11.setText("Minimize:");

        rdoGunnery.setBackground(new java.awt.Color(255, 255, 255));
        btnGrpSkill.add(rdoGunnery);
        rdoGunnery.setText("Gunnery");
        rdoGunnery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoGunneryActionPerformed(evt);
            }
        });

        rdoPiloting.setBackground(new java.awt.Color(255, 255, 255));
        btnGrpSkill.add(rdoPiloting);
        rdoPiloting.setText("Piloting");
        rdoPiloting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoPilotingActionPerformed(evt);
            }
        });

        rdoNeither.setBackground(new java.awt.Color(255, 255, 255));
        btnGrpSkill.add(rdoNeither);
        rdoNeither.setSelected(true);
        rdoNeither.setText("Neither");
        rdoNeither.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoNeitherActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rdoNeither)
                            .addComponent(rdoPiloting)
                            .addComponent(rdoGunnery)))
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtBVLimit))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                            .addComponent(jLabel7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(spnSkillSeperationLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(btnFilter)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtBVLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(spnSkillSeperationLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(rdoNeither))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdoGunnery)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdoPiloting)
                .addGap(18, 18, 18)
                .addComponent(btnFilter)
                .addContainerGap(126, Short.MAX_VALUE))
        );

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Skill Choices");

        jLabel10.setForeground(new java.awt.Color(204, 204, 204));
        jLabel10.setText("Double click a skill choice to update the unit");

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Random Selection"));

        jLabel12.setText("Skill Level:");

        cmbSkillLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Random", "Green", "Regular", "Veteran", "Elite" }));

        lblRandomSkill.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        lblRandomSkill.setText("0 / 0");

        btnRandomGen.setText("Generate");
        btnRandomGen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRandomGenActionPerformed(evt);
            }
        });

        jLabel15.setText("Generated Skill:");

        btnApply.setText("Apply");
        btnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnRandomGen)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbSkillLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(52, 52, 52)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblRandomSkill)
                            .addComponent(btnApply))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(cmbSkillLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRandomGen)
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(lblRandomSkill))
                .addGap(18, 18, 18)
                .addComponent(btnApply)
                .addContainerGap(155, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel10))
                .addContainerGap(87, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Skill Selection", jPanel5);

        jLabel1.setText("File:");

        lblFilename.setText("k:\\location");

        javax.swing.GroupLayout pnlFileLayout = new javax.swing.GroupLayout(pnlFile);
        pnlFile.setLayout(pnlFileLayout);
        pnlFileLayout.setHorizontalGroup(
            pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFileLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFilename)
                .addContainerGap(520, Short.MAX_VALUE))
        );
        pnlFileLayout.setVerticalGroup(
            pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(lblFilename))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                            .addComponent(pnlFile, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.setVisible( false );
}//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        unit.Mechwarrior = txtMechwarrior.getText();
        unit.Gunnery = cmbGunnery.getSelectedIndex();
        unit.Piloting = cmbPiloting.getSelectedIndex();
        unit.MiscMod = Float.parseFloat(txtMod.getText());
        unit.UsingC3 = chkC3Active.isSelected();
        unit.Refresh();
        Result = true;
        this.setVisible( false );
    }//GEN-LAST:event_btnSaveActionPerformed

    private void cmbGunneryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbGunneryActionPerformed
        unit.Gunnery = cmbGunnery.getSelectedIndex();
        unit.Refresh();
        setBV();
    }//GEN-LAST:event_cmbGunneryActionPerformed

    private void cmbPilotingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPilotingActionPerformed
        unit.Piloting = cmbPiloting.getSelectedIndex();
        unit.Refresh();
        setBV();
    }//GEN-LAST:event_cmbPilotingActionPerformed

    private void txtModActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtModActionPerformed
        unit.MiscMod = Float.parseFloat(txtMod.getText());
        unit.Refresh();
        setBV();
    }//GEN-LAST:event_txtModActionPerformed

    private void cmbConfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbConfigurationActionPerformed
        if ( Default ) { return; }
        unit.m.SetCurLoadout(cmbConfiguration.getSelectedItem().toString().substring(0, cmbConfiguration.getSelectedItem().toString().indexOf(" ")));
        unit.UpdateByMech();
        setC3();
        setBV();
        setTRO();
    }//GEN-LAST:event_cmbConfigurationActionPerformed

    private void spnSkillSeperationLimitStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnSkillSeperationLimitStateChanged
        btnFilterActionPerformed(null);
    }//GEN-LAST:event_spnSkillSeperationLimitStateChanged

    private void txtBVLimitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBVLimitActionPerformed
        btnFilterActionPerformed(evt);
    }//GEN-LAST:event_txtBVLimitActionPerformed

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
        skills.setMaxSeperation(Integer.parseInt(spnSkillSeperationLimit.getValue().toString()));
        if (!txtBVLimit.getText().isEmpty()) { skills.setMaxBV(Float.parseFloat(txtBVLimit.getText())); }
        skills.setMaxSkill("");
        if ( rdoGunnery.isSelected() ) { skills.setMaxSkill("Gunnery"); }
        if ( rdoPiloting.isSelected() ) { skills.setMaxSkill("Piloting"); }
        setSkills();
    }//GEN-LAST:event_btnFilterActionPerformed

    private void lstSkillsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstSkillsMouseClicked
        if ( evt.getClickCount() == 2 ) {
            String[] selection = lstSkills.getSelectedValue().toString().substring(0, 5).trim().split("/");
            cmbGunnery.setSelectedIndex(Integer.parseInt(selection[0].trim()));
            cmbPiloting.setSelectedIndex(Integer.parseInt(selection[1].trim()));
        }
    }//GEN-LAST:event_lstSkillsMouseClicked

    private void txtBVLimitKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBVLimitKeyTyped
        btnFilterActionPerformed(null);
    }//GEN-LAST:event_txtBVLimitKeyTyped

    private void rdoGunneryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoGunneryActionPerformed
        btnFilterActionPerformed(evt);
    }//GEN-LAST:event_rdoGunneryActionPerformed

    private void rdoPilotingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoPilotingActionPerformed
        btnFilterActionPerformed(evt);
    }//GEN-LAST:event_rdoPilotingActionPerformed

    private void rdoNeitherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoNeitherActionPerformed
        btnFilterActionPerformed(evt);
}//GEN-LAST:event_rdoNeitherActionPerformed

    private void btnRandomGenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRandomGenActionPerformed
        lblRandomSkill.setText("9/9");
        lblRandomSkill.setText(skills.generateRandomSkill(cmbSkillLevel.getSelectedItem().toString()));
    }//GEN-LAST:event_btnRandomGenActionPerformed

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        String[] items = lblRandomSkill.getText().split("/");
        cmbGunnery.setSelectedIndex(Integer.parseInt(items[0]));
        cmbPiloting.setSelectedIndex(Integer.parseInt(items[1]));
    }//GEN-LAST:event_btnApplyActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnFilter;
    private javax.swing.ButtonGroup btnGrpSkill;
    private javax.swing.JButton btnRandomGen;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkC3Active;
    private javax.swing.JComboBox cmbConfiguration;
    private javax.swing.JComboBox cmbGunnery;
    private javax.swing.JComboBox cmbPiloting;
    private javax.swing.JComboBox cmbSkillLevel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblBaseBV;
    private javax.swing.JLabel lblConfig;
    private javax.swing.JLabel lblFilename;
    private javax.swing.JLabel lblModel;
    private javax.swing.JLabel lblRandomSkill;
    private javax.swing.JLabel lblTonnage;
    private javax.swing.JLabel lblTotalBV;
    private javax.swing.JList lstSkills;
    private javax.swing.JPanel pnlConfiguration;
    private javax.swing.JPanel pnlFile;
    private javax.swing.JRadioButton rdoGunnery;
    private javax.swing.JRadioButton rdoNeither;
    private javax.swing.JRadioButton rdoPiloting;
    private javax.swing.JSpinner spnSkillSeperationLimit;
    private javax.swing.JScrollPane spnTRO;
    private javax.swing.JTextPane tpnTRO;
    private javax.swing.JTextField txtBVLimit;
    private javax.swing.JTextField txtMechwarrior;
    private javax.swing.JTextField txtMod;
    // End of variables declaration//GEN-END:variables

}
