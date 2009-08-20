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

import BFB.Unit;
import java.awt.Color;
import ssw.components.ifLoadout;
import ssw.filehandlers.TXTWriter;

public class dlgUnit extends javax.swing.JDialog {
    private Unit unit;
    private boolean Default = true;
    private frmBase Parent;
    public boolean Result = false;

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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        txtMechwarrior = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cmbPiloting = new javax.swing.JComboBox();
        lblTonnage = new javax.swing.JLabel();
        pnlFile = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblFilename = new javax.swing.JLabel();
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
        spnTRO = new javax.swing.JScrollPane();
        tpnTRO = new javax.swing.JTextPane();

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
                .addContainerGap(358, Short.MAX_VALUE))
        );
        pnlFileLayout.setVerticalGroup(
            pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(lblFilename))
        );

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

        lblTotalBV.setFont(new java.awt.Font("Tahoma", 1, 12));
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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
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
                            .addComponent(chkC3Active, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblModel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblModel)
                    .addComponent(lblTonnage))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlConfiguration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkC3Active))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        spnTRO.setBorder(javax.swing.BorderFactory.createTitledBorder("Technical Readout"));
        spnTRO.setPreferredSize(new java.awt.Dimension(400, 550));

        tpnTRO.setBackground(javax.swing.UIManager.getDefaults().getColor("control"));
        tpnTRO.setBorder(null);
        tpnTRO.setEditable(false);
        tpnTRO.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        tpnTRO.setText("--------------------------------------------------------------------------------");
        tpnTRO.setCaretPosition(0);
        tpnTRO.setPreferredSize(new java.awt.Dimension(400, 550));
        spnTRO.setViewportView(tpnTRO);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(210, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(spnTRO, javax.swing.GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(402, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(158, 158, 158)
                    .addComponent(spnTRO, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)))
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkC3Active;
    private javax.swing.JComboBox cmbConfiguration;
    private javax.swing.JComboBox cmbGunnery;
    private javax.swing.JComboBox cmbPiloting;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblBaseBV;
    private javax.swing.JLabel lblConfig;
    private javax.swing.JLabel lblFilename;
    private javax.swing.JLabel lblModel;
    private javax.swing.JLabel lblTonnage;
    private javax.swing.JLabel lblTotalBV;
    private javax.swing.JPanel pnlConfiguration;
    private javax.swing.JPanel pnlFile;
    private javax.swing.JScrollPane spnTRO;
    private javax.swing.JTextPane tpnTRO;
    private javax.swing.JTextField txtMechwarrior;
    private javax.swing.JTextField txtMod;
    // End of variables declaration//GEN-END:variables

}
