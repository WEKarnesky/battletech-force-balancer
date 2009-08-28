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

package BFB.IO;

import BFB.Common.CommonTools;
import BFB.Force;
import BFB.Unit;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Vector;

public class PrintDeclaration implements Printable {
    public Graphics2D Graphic;
    private Vector forces = new Vector();
    private PageFormat format = null;
    private String[] Types = new String[]{"  Primary", "Secondary", "Secondary"};
    private String Title = "Fire Declaration Markers";

    public int currentX = 0;
    public int currentY = 0;

    public PrintDeclaration() {

    }

    public PrintDeclaration( Force[] forces ) {
        for ( Force f : forces ) {
            this.forces.add(f);
        }
    }

    public void AddForces( Force[] forces ) {
        for ( Force f : forces ) {
            this.forces.add(f);
        }
    }

    public void AddForce( Force force ) {
        this.forces.add(force);
    }

    public void clearForces() {
        forces.clear();
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if( forces.size() == 0 ) { return Printable.NO_SUCH_PAGE; }
        Graphic = (Graphics2D) graphics;
        format = pageFormat;
        Reset();
        Graphic.translate( pageFormat.getImageableX(), pageFormat.getImageableY() );
        PreparePrint();
        return Printable.PAGE_EXISTS;
    }

    private void PreparePrint() {
        Reset();
        for (int f=0; f < forces.size(); f++) {
            Force force = (Force) forces.get(f);
            Image logo = force.getLogo();
            for (int j=0; j<force.Units.size(); j++) {
                Unit unit = (Unit) force.Units.get(j);
                for (int k=0; k<Types.length; k++) {
                    int shift = 5;
                    Graphic.setFont(CommonTools.SmallFont);
                    if ( logo != null ) {
                        Graphic.drawImage(logo, currentX+1, currentY-10, 25, 25, null);
                        shift = 30;
                    }
                    Graphic.drawString(unit.TypeModel, currentX+shift, currentY);
                    Graphic.drawString((unit.Group + " (" + unit.Mechwarrior + ")").replace("()", ""), currentX+shift, currentY+10);
                    Graphic.drawRect(currentX, currentY-12, 175, 30);
                    Graphic.setFont(CommonTools.BoldFont);
                    Graphic.drawString(Types[k], currentX+125, currentY+3);

                    currentX += 175;
                }
                currentX = (int) format.getImageableX();
                currentY += 30;
            }
        }
    }

    public void Reset() {
        currentX = (int) format.getImageableX();
        currentY = (int) format.getImageableY();
    }
}
