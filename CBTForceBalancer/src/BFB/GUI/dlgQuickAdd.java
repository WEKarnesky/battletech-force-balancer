
package BFB.GUI;

import Force.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.table.DefaultTableModel;
import list.*;
import list.view.*;

public class dlgQuickAdd extends javax.swing.JDialog {
    
    private frmBase parent;
    private Force force;
    private MechList list,  filtered = new MechList();
    private abView viewModel;

    KeyListener filterKey = new KeyListener() {
        public void keyTyped(KeyEvent e) {}
        public void keyPressed(KeyEvent e) {}
        public void keyReleased(KeyEvent e) {
            Filter(null);
        }
    };

    public dlgQuickAdd(java.awt.Frame parent, boolean modal, Force force) {
        super(parent, modal);
        initComponents();

        this.parent = (frmBase) parent;
        this.force = force;

        txtName.addKeyListener(filterKey);
        jScrollPane1.addKeyListener(filterKey);
        tblList.addKeyListener(filterKey);
        spnGunnery.addKeyListener(filterKey);
        spnPiloting.addKeyListener(filterKey);

        list = new MechList(this.parent.Prefs.get("ListPath", ""), true);
        viewModel = new tbTotalWarfareCompact(list);
        tblList.setModel(new DefaultTableModel());
        //setupList(list);
    }

    private void Filter(java.awt.event.ActionEvent evt) {
        ListFilter filters = new ListFilter();

        if (!txtName.getText().isEmpty()) {
            filters.setName(txtName.getText());
        }

        filtered = list.Filter(filters);
        setupList(filtered);
    }

    private void setupList(MechList mechList) {
        viewModel.list = mechList;
        viewModel.setupTable(tblList);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        spnGunnery = new javax.swing.JSpinner();
        spnPiloting = new javax.swing.JSpinner();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblList = new javax.swing.JTable();
        btnAdd = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Name:");

        spnGunnery.setModel(new javax.swing.SpinnerNumberModel(4, 0, 9, 1));

        spnPiloting.setModel(new javax.swing.SpinnerNumberModel(5, 0, 9, 1));

        tblList.setModel(new javax.swing.table.DefaultTableModel(
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
        tblList.setRowMargin(5);
        tblList.setShowVerticalLines(false);
        tblList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblList);

        btnAdd.setText("Add Unit");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnGunnery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnPiloting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClose)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btnClose)
                    .addComponent(spnGunnery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnPiloting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdd))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        if ( tblList.getSelectedRowCount() > 0 ) {
            int[] Rows = tblList.getSelectedRows();
            for (int i = 0; i < Rows.length; i++) {
                Unit u = new Unit( ((abView) tblList.getModel()).list.Get(tblList.convertRowIndexToModel(Rows[i])) );
                u.setGunnery(Integer.parseInt(spnGunnery.getValue().toString()));
                u.setPiloting(Integer.parseInt(spnPiloting.getValue().toString()));
                u.Refresh();
                force.AddUnit(u);
            }
            parent.Refresh();
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void tblListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblListMouseClicked
        if ( evt.getClickCount() == 2 ) {
            btnAddActionPerformed(null);
        }
    }//GEN-LAST:event_tblListMouseClicked

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
}//GEN-LAST:event_btnCloseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnClose;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner spnGunnery;
    private javax.swing.JSpinner spnPiloting;
    private javax.swing.JTable tblList;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables

}
