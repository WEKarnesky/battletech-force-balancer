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

package BFB.IO;

import BFB.Force;
import BFB.GUI.frmBase;
import java.awt.print.*;

public class Printer {
    private String jobName = "Battletech Force Balancer",
                    Title = "Battletech Force Balancer";
    private Boolean useDialog = true;
    private PrintSheet sheet = new PrintSheet();

    private Book pages = new Book();
    private Paper paper = new Paper();
    private PageFormat page = new PageFormat();
    private PrinterJob job = PrinterJob.getPrinterJob();

    //To convert paper size to pixels is inches / 0.0139 rounded down
    public final static PaperSize Letter = new PaperSize(8.5d, 11d);
    public final static PaperSize Landscape = new PaperSize(11d, 8.5d);
    public final static PaperSize A4 = new PaperSize(595, 842, 18, 18, 559, 806);
    public final static PaperSize Legal = new PaperSize(8.5d, 14.0d);

    public Printer() {
        setPaperSize(Letter);
    }

    public Printer( frmBase parent ) {
        this();
        parent.topForce.sortForPrinting();
        parent.bottomForce.sortForPrinting();
        sheet.AddForces(new Force[]{parent.topForce, parent.bottomForce});
    }

    public Printer( Force[] forces ) {
        this();
        sheet.AddForces(forces);
    }

    public Printer( Force force ) {
        this();
        sheet.AddForce(force);
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName( String jobName ) {
        this.jobName = jobName.trim();
    }

    public PrinterJob getJob() {
        return job;
    }

    public void setPaperSize( PaperSize s ) {
        paper.setSize( s.PaperWidth, s.PaperHeight );
        paper.setImageableArea( s.ImageableX, s.ImageableY, s.ImageableWidth, s.ImageableHeight );
    }

    public void Print( boolean useDialog ) {
        this.useDialog = useDialog;
        Print();
    }

    public void Print() {
        job.setJobName(jobName.trim());

        //start building the print objects necessary
        GeneratePrints();

        job.setPageable(pages);
        boolean DoPrint = job.printDialog();
        if( DoPrint ) {
            try {
                job.print();
            } catch( PrinterException e ) {
                System.err.println( e.getMessage() );
                System.out.println( e.getStackTrace() );
            }
        }
    }

    public Book Preview() {
        GeneratePrints();
        return pages;
    }

    private void GeneratePrints() {
        //start building the print objects necessary
        page.setPaper( paper );
        pages.append(sheet, page);
    }

    /**
     * @return the Title
     */
    public String getTitle() {
        return Title;
    }

    /**
     * @param Title the Title to set
     */
    public void setTitle(String Title) {
        this.Title = Title;
        sheet.setTitle(Title);
    }

//    private Boolean PrintDialog(PrintMech pMech) {
//        dlgPrintSavedMechOptions POptions = new dlgPrintSavedMechOptions(Parent, true, pMech);
//        POptions.setTitle( "Printing " + pMech.CurMech.GetFullName() );
//        POptions.setLocationRelativeTo( Parent );
//
//        POptions.setVisible( true );
//
//        if( ! POptions.Result() ) {
//            return false;
//        }
//
//        pMech.setPrintPilot(POptions.PrintPilot());
//        pMech.setCharts(POptions.PrintCharts());
//        pMech.setGunnery(POptions.GetGunnery());
//        pMech.setPiloting(POptions.GetPiloting());
//        pMech.setMechwarrior(POptions.GetWarriorName());
//        pMech.setMechImage(POptions.getImage());
//        pMech.setLogoImage(POptions.getLogo());
//        pMech.setCanon(POptions.getCanon());
//        if ( POptions.UseMiniConversion() ) { pMech.SetMiniConversion( POptions.GetMiniConversionRate() );}
//
//        POptions.dispose();
//        return true;
//    }
//
//    private Boolean BatchDialog() {
//        dlgPrintSavedMechOptions POptions = new dlgPrintSavedMechOptions(Parent, true);
//        POptions.setTitle( "Printing Batched Units");
//        POptions.setLocationRelativeTo( Parent );
//
//        if ( !this.logoPath.isEmpty() ) {
//            POptions.setLogo(new File(this.logoPath));
//        }
//
//        POptions.setVisible( true );
//
//        if( ! POptions.Result() ) {
//            return false;
//        }
//
//        for ( int m=0; m <= Mechs.size()-1; m++ ) {
//            PrintMech pMech = (PrintMech) Mechs.get(m);
//            pMech.setPrintPilot(POptions.PrintPilot());
//            pMech.setCharts(POptions.PrintCharts());
//            pMech.setMechImage(POptions.getImage());
//            pMech.setLogoImage(POptions.getLogo());
//            pMech.setCanon(POptions.getCanon());
//            if ( POptions.UseMiniConversion() ) { pMech.SetMiniConversion( POptions.GetMiniConversionRate() );}
//        }
//
//        POptions.dispose();
//        return true;
//    }

}