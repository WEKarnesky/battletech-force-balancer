

package BFB.Preview;

import Print.*;
import Force.Force;
import Force.Unit;
import battleforce.BattleForce;

import BFB.GUI.dlgMechImages;
import BFB.GUI.frmBase;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.util.Vector;

public class dlgPreview extends javax.swing.JFrame implements ActionListener {
    private final static double DEFAULT_ZOOM_FACTOR_STEP = .5;
    protected Pageable pageable;
    private Preview preview;
    private frmBase Parent;
    private Force[] forces;
    private PagePrinter printer;

    public dlgPreview(String title, Component owner, Pageable pageable, double zoom) {
        super(title);
        initComponents();
        preview = new Preview(pageable, zoom, spnPreview.getSize());
        spnPreview.setViewportView(preview);

        btnZoomIn.setAction(new ZoomAction("Zoom In", "magnifier-zoom.png", preview, DEFAULT_ZOOM_FACTOR_STEP, false));
        btnZoomOut.setAction(new ZoomAction("Zoom Out", "magnifier-zoom-out.png", preview, -DEFAULT_ZOOM_FACTOR_STEP, false));

        btnBack.setAction(new BrowseAction("Prev", "arrow-180.png", preview, -1));
        btnForward.setAction(new BrowseAction("Next", "arrow.png", preview, 1));

        btnPageWidth.setAction(new ZoomAction("Width", "document-resize.png", preview, preview.getWidthZoom(), true));
        btnPageHeight.setAction(new ZoomAction("Page", "document-resize-actual.png", preview, preview.getHeightZoom(), true));

        spnPreview.addComponentListener( new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                preview.setViewportSize( e.getComponent().getSize() );
                btnPageWidth.setAction(new ZoomAction("Width", "document-resize.png", preview, preview.getWidthZoom(), true));
                btnPageHeight.setAction(new ZoomAction("Page", "document-resize-actual.png", preview, preview.getHeightZoom(), true));
            }
        });
    }

    public dlgPreview(String title, Component owner, Pageable pageable) {
        this(title, owner, pageable, 0.0);
    }

    public dlgPreview(String title, Component owner, PagePrinter printer, Force[] forces) {
        this(title, owner, printer.Preview(), 0.0);
        setForces(forces);
        setPrinter(printer);
    }

    public dlgPreview(String title, Component owner, Printable printable, PageFormat format, int pages, double zoom) {
        this(title, owner, new MyPageable(printable, format, pages), zoom);
    }

    public dlgPreview(String title, Component owner, Printable printable, PageFormat format, int pages) {
        this(title, owner, printable, format, pages, 0.0);
    }

    public void actionPerformed(ActionEvent e) {
        dispose();
    }

    public void setForces(Force[] forces) {
        this.forces = forces;
    }

    public void setPrinter(PagePrinter printer) {
        this.printer = printer;
        PrinterSetup();
    }

    private static class MyPageable implements Pageable {
        public MyPageable(Printable printable, PageFormat format, int pages) {
            this.printable = printable;
            this.format = format;
            this.pages = pages;
        }

        public int getNumberOfPages() {
            return pages;
        }

        public Printable getPrintable(int index) {
            if (index >= pages) throw new IndexOutOfBoundsException();
            return printable;
        }

        public PageFormat getPageFormat(int index) {
            if (index >= pages) throw new IndexOutOfBoundsException();
            return format;
        }

        private Printable printable;
        private PageFormat format;
        private int pages;
    }

    private void refresh() {
        preview.setPageable(printer.Preview());
        preview.repaint();
    }

    private void Verify() {
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
        PrinterSetup();
        refresh();
    }

    private void PrinterSetup() {
        printer.Clear();
        
        if ( chkPrintForce.isSelected() ) {
            ForceList sheet = new ForceList();
            sheet.AddForces(forces);
            printer.Append( BFBPrinter.Letter.toPage(), sheet );
        }

        if ( chkPrintFireChits.isSelected() ) {
            PrintDeclaration fire = new PrintDeclaration();
            fire.AddForces(forces);
            printer.Append( BFBPrinter.Letter.toPage(), fire );
        }

        if ( chkPrintBattleforce.isSelected() ) {
            if ( chkBFOnePerPage.isSelected() ) {
                Vector<BattleForce> forcelist = new Vector<BattleForce>();
                forcelist.addAll(forces[0].toBattleForceByGroup());
                forcelist.addAll(forces[1].toBattleForceByGroup());

                for ( BattleForce f : forcelist ) {
                    Battleforce bf = new Battleforce(f);
                    bf.setPrintLogo(chkLogo.isSelected());
                    bf.setPrintMechs(chkImage.isSelected());
                    printer.Append( BFBPrinter.Letter.toPage(), bf);
                }
            } else {
                Battleforce topBF = new Battleforce(forces[0].toBattleForce());
                topBF.setPrintLogo(chkLogo.isSelected());
                topBF.setPrintMechs(chkImage.isSelected());
                Battleforce bottomBF = new Battleforce(forces[1].toBattleForce());
                bottomBF.setPrintLogo(chkLogo.isSelected());
                bottomBF.setPrintMechs(chkImage.isSelected());

                printer.Append( BFBPrinter.Letter.toPage(), topBF );
                printer.Append( BFBPrinter.Letter.toPage(), bottomBF );
            }
        }

        if ( chkPrintRecordsheets.isSelected() ) {
            for ( int f=0; f < forces.length; f++ ) {
                Force force = forces[f];

                for ( Unit u : force.Units ) {
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
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        spnPreview = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        pnlPrintOptions = new javax.swing.JPanel();
        chkPrintForce = new javax.swing.JCheckBox();
        chkPrintFireChits = new javax.swing.JCheckBox();
        chkPrintRecordsheets = new javax.swing.JCheckBox();
        chkPrintBattleforce = new javax.swing.JCheckBox();
        jToolBar1 = new javax.swing.JToolBar();
        btnBack = new javax.swing.JButton();
        btnForward = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnPageWidth = new javax.swing.JButton();
        btnPageHeight = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnZoomIn = new javax.swing.JButton();
        btnZoomOut = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnPrint = new javax.swing.JButton();
        btnCloseDialog = new javax.swing.JButton();
        pnlHow = new javax.swing.JPanel();
        chkTables = new javax.swing.JCheckBox();
        chkCanon = new javax.swing.JCheckBox();
        chkUseHexConversion = new javax.swing.JCheckBox();
        chkImage = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        chkLogo = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        cmbHexConvFactor = new javax.swing.JComboBox();
        lblInches = new javax.swing.JLabel();
        lblOneHex = new javax.swing.JLabel();
        btnImageMgr = new javax.swing.JButton();
        chkRS = new javax.swing.JCheckBox();
        chkBFOnePerPage = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1024, 768));

        pnlPrintOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("What to Print"));

        chkPrintForce.setSelected(true);
        chkPrintForce.setText("Force List");
        chkPrintForce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPrintForceActionPerformed(evt);
            }
        });

        chkPrintFireChits.setText("Fire Declaration Chits");
        chkPrintFireChits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPrintFireChitsActionPerformed(evt);
            }
        });

        chkPrintRecordsheets.setText("Unit Recordsheets");
        chkPrintRecordsheets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPrintRecordsheetsVerify(evt);
            }
        });

        chkPrintBattleforce.setText("BattleForce Sheets");
        chkPrintBattleforce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPrintBattleforceVerify(evt);
            }
        });

        javax.swing.GroupLayout pnlPrintOptionsLayout = new javax.swing.GroupLayout(pnlPrintOptions);
        pnlPrintOptions.setLayout(pnlPrintOptionsLayout);
        pnlPrintOptionsLayout.setHorizontalGroup(
            pnlPrintOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPrintOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPrintOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkPrintForce)
                    .addComponent(chkPrintFireChits)
                    .addComponent(chkPrintBattleforce)
                    .addComponent(chkPrintRecordsheets))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPrintOptionsLayout.setVerticalGroup(
            pnlPrintOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPrintOptionsLayout.createSequentialGroup()
                .addComponent(chkPrintForce)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkPrintFireChits)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkPrintBattleforce)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkPrintRecordsheets)
                .addContainerGap(53, Short.MAX_VALUE))
        );

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/arrow-180.png"))); // NOI18N
        btnBack.setText("Prev");
        btnBack.setFocusable(false);
        btnBack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBack.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnBack);

        btnForward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/arrow.png"))); // NOI18N
        btnForward.setText("Next");
        btnForward.setFocusable(false);
        btnForward.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnForward.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnForward);
        jToolBar1.add(jSeparator1);

        btnPageWidth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/document-resize.png"))); // NOI18N
        btnPageWidth.setText("Width");
        btnPageWidth.setFocusable(false);
        btnPageWidth.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPageWidth.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnPageWidth);

        btnPageHeight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/document-resize-actual.png"))); // NOI18N
        btnPageHeight.setText("Page");
        btnPageHeight.setFocusable(false);
        btnPageHeight.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPageHeight.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnPageHeight);
        jToolBar1.add(jSeparator3);

        btnZoomIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/magnifier-zoom.png"))); // NOI18N
        btnZoomIn.setText("Zoom In");
        btnZoomIn.setFocusable(false);
        btnZoomIn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnZoomIn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnZoomIn);

        btnZoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/magnifier-zoom-out.png"))); // NOI18N
        btnZoomOut.setText("Zoom Out");
        btnZoomOut.setFocusable(false);
        btnZoomOut.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnZoomOut.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnZoomOut);
        jToolBar1.add(jSeparator2);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/printer.png"))); // NOI18N
        btnPrint.setText("Print");
        btnPrint.setFocusable(false);
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

        btnCloseDialog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/BFB/Images/home.png"))); // NOI18N
        btnCloseDialog.setText("Close");
        btnCloseDialog.setFocusable(false);
        btnCloseDialog.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCloseDialog.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCloseDialog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseDialogActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCloseDialog);

        pnlHow.setBorder(javax.swing.BorderFactory.createTitledBorder("How To Print"));

        chkTables.setSelected(true);
        chkTables.setText("Print Charts and Tables");
        chkTables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkTablesVerify(evt);
            }
        });

        chkCanon.setSelected(true);
        chkCanon.setText("Print Canon Dot Patterns");
        chkCanon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCanonActionPerformed(evt);
            }
        });

        chkUseHexConversion.setText("Print Miniatures Scale");
        chkUseHexConversion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUseHexConversionActionPerformed(evt);
            }
        });

        chkImage.setSelected(true);
        chkImage.setText("Print Mech Images ");
        chkImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkImageActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("* Must be added to the file already");

        chkLogo.setSelected(true);
        chkLogo.setText("Print Unit Logo");
        chkLogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLogoActionPerformed(evt);
            }
        });

        cmbHexConvFactor.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5" }));
        cmbHexConvFactor.setEnabled(false);
        cmbHexConvFactor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbHexConvFactorActionPerformed(evt);
            }
        });

        lblInches.setText("Inches");
        lblInches.setEnabled(false);

        lblOneHex.setText("One Hex equals");
        lblOneHex.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblOneHex)
                .addGap(1, 1, 1)
                .addComponent(cmbHexConvFactor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInches)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(cmbHexConvFactor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lblOneHex))
            .addGroup(jPanel2Layout.createSequentialGroup()
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

        chkRS.setText("Print Recordsheet Format");
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlHowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlHowLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlHowLayout.createSequentialGroup()
                        .addGroup(pnlHowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkRS)
                            .addComponent(chkTables)
                            .addComponent(chkCanon)
                            .addComponent(chkUseHexConversion))
                        .addGap(18, 18, 18)
                        .addGroup(pnlHowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlHowLayout.createSequentialGroup()
                                .addComponent(chkImage)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnImageMgr))
                            .addComponent(chkLogo)
                            .addGroup(pnlHowLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jLabel1))
                            .addComponent(chkBFOnePerPage)))))
        );
        pnlHowLayout.setVerticalGroup(
            pnlHowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHowLayout.createSequentialGroup()
                .addGroup(pnlHowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlHowLayout.createSequentialGroup()
                        .addComponent(chkRS)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkTables)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCanon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkUseHexConversion))
                    .addGroup(pnlHowLayout.createSequentialGroup()
                        .addGroup(pnlHowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(chkImage)
                            .addComponent(btnImageMgr))
                        .addGap(1, 1, 1)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkLogo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkBFOnePerPage)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlPrintOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlHow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 182, Short.MAX_VALUE)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlPrintOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(121, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(spnPreview, javax.swing.GroupLayout.DEFAULT_SIZE, 1038, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnPreview, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseDialogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseDialogActionPerformed
        dispose();
}//GEN-LAST:event_btnCloseDialogActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        printer.Print();
    }//GEN-LAST:event_btnPrintActionPerformed

    private void chkTablesVerify(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTablesVerify
        Verify();
}//GEN-LAST:event_chkTablesVerify

    private void chkUseHexConversionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUseHexConversionActionPerformed
        lblOneHex.setEnabled( chkUseHexConversion.isSelected() );
        cmbHexConvFactor.setEnabled( chkUseHexConversion.isSelected() );
        lblInches.setEnabled( chkUseHexConversion.isSelected() );

        Verify();
}//GEN-LAST:event_chkUseHexConversionActionPerformed

    private void btnImageMgrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImageMgrActionPerformed
        if ( Parent instanceof frmBase ) {
            dlgMechImages dlgImg = new dlgMechImages((frmBase) Parent, new Force[]{Parent.scenario.getAttackerForce(), Parent.scenario.getDefenderForce()});
            if ( dlgImg.hasWork ) {
                dlgImg.setLocationRelativeTo(this);
                dlgImg.setVisible(true);
            }
        }
}//GEN-LAST:event_btnImageMgrActionPerformed

    private void chkRSVerify(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRSVerify
        Verify();
}//GEN-LAST:event_chkRSVerify

    private void chkBFOnePerPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkBFOnePerPageActionPerformed
        Verify();
}//GEN-LAST:event_chkBFOnePerPageActionPerformed

    private void chkPrintRecordsheetsVerify(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPrintRecordsheetsVerify
        Verify();
}//GEN-LAST:event_chkPrintRecordsheetsVerify

    private void chkPrintBattleforceVerify(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPrintBattleforceVerify
        Verify();
}//GEN-LAST:event_chkPrintBattleforceVerify

    private void chkImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkImageActionPerformed
        Verify();
    }//GEN-LAST:event_chkImageActionPerformed

    private void chkLogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLogoActionPerformed
        Verify();
    }//GEN-LAST:event_chkLogoActionPerformed

    private void chkCanonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCanonActionPerformed
        Verify();
    }//GEN-LAST:event_chkCanonActionPerformed

    private void cmbHexConvFactorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbHexConvFactorActionPerformed
        Verify();
    }//GEN-LAST:event_cmbHexConvFactorActionPerformed

    private void chkPrintFireChitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPrintFireChitsActionPerformed
        Verify();
    }//GEN-LAST:event_chkPrintFireChitsActionPerformed

    private void chkPrintForceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPrintForceActionPerformed
        Verify();
    }//GEN-LAST:event_chkPrintForceActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCloseDialog;
    private javax.swing.JButton btnForward;
    private javax.swing.JButton btnImageMgr;
    private javax.swing.JButton btnPageHeight;
    private javax.swing.JButton btnPageWidth;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnZoomIn;
    private javax.swing.JButton btnZoomOut;
    private javax.swing.JCheckBox chkBFOnePerPage;
    private javax.swing.JCheckBox chkCanon;
    private javax.swing.JCheckBox chkImage;
    private javax.swing.JCheckBox chkLogo;
    private javax.swing.JCheckBox chkPrintBattleforce;
    private javax.swing.JCheckBox chkPrintFireChits;
    private javax.swing.JCheckBox chkPrintForce;
    private javax.swing.JCheckBox chkPrintRecordsheets;
    private javax.swing.JCheckBox chkRS;
    private javax.swing.JCheckBox chkTables;
    private javax.swing.JCheckBox chkUseHexConversion;
    private javax.swing.JComboBox cmbHexConvFactor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblInches;
    private javax.swing.JLabel lblOneHex;
    private javax.swing.JPanel pnlHow;
    private javax.swing.JPanel pnlPrintOptions;
    private javax.swing.JScrollPane spnPreview;
    // End of variables declaration//GEN-END:variables
}
