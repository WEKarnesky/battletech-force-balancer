/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package BFB.IO;

import Force.Scenario;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.*;

public class PrintScenario implements Printable {
    public Graphics2D Graphic;
    private Scenario scenario = null;
    private PageFormat format = null;
    private String Title = "Scenario Information";

    public int currentX = 0;
    public int currentY = 0;

    public PrintScenario() {

    }

    public PrintScenario( Scenario scenario ) {
        this.scenario = scenario;
    }

    public void SetScenario( Scenario scenario ) {
        this.scenario = scenario;
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if( scenario == null ) { return Printable.NO_SUCH_PAGE; }
        Graphic = (Graphics2D) graphics;
        format = pageFormat;
        Reset();
        Graphic.translate( pageFormat.getImageableX(), pageFormat.getImageableY() );
        PreparePrint();
        return Printable.PAGE_EXISTS;
    }

    private void PreparePrint() {
        Reset();
    }

    public void Reset() {
        currentX = (int) format.getImageableX();
        currentY = (int) format.getImageableY();
    }
}
