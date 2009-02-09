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

import BFB.GUI.frmMain2;
import BFB.*;
import BFB.Common.CommonTools;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

import ssw.filehandlers.XMLReader;
import ssw.components.*;
/**
 *
 * @author gblouin
 */
public class SSWReader {
    frmMain2 Parent;
    Force force;

    public void ReadFile( Force f, String filename ) throws Exception {
        XMLReader r = new XMLReader();
        try
        {
            Mech m = r.ReadMech(filename);
            f.Units.add(BuildUnit(m));
            f.RefreshBV();
        } catch (Exception e) {
            throw new Exception("ReadFile error using SSW Mech Loader. [" + e.getMessage() + "]");
        }
    }

    public void ReadFile(frmMain2 p, Force f, String filename ) throws Exception {
        Parent = p;
        force = f;
        Document load;
        filename = CommonTools.SafeFileName( filename );

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        load = db.parse( filename );
        force.Units.add(BuildUnit(load));
        force.RefreshBV();
        Parent.RefreshDisplay();
    }

    private Unit BuildUnit( ssw.components.Mech m ) {
        Unit u = new Unit();
        u.TypeModel = m.GetFullName();
        u.BaseBV = m.GetCurrentBV();
        u.Tonnage = m.GetTonnage();
        u.UnitType = BFB.Common.Constants.BattleMech;
        u.Refresh();
        return u;
    }

    private Unit BuildUnit( Document d ) throws Exception {
        Unit u = new Unit();
        NodeList n = d.getElementsByTagName( "mech" );
        NamedNodeMap map = n.item( 0 ).getAttributes();

        // basics
        boolean omnimech = Boolean.parseBoolean( map.getNamedItem( "omnimech" ).getTextContent() );

        if (omnimech) {
            u.TypeModel = map.getNamedItem( "name" ).getTextContent() + " " + map.getNamedItem( "model" ).getTextContent();
            u.Tonnage = Integer.parseInt( map.getNamedItem( "tons" ).getTextContent());
        } else {
            u.TypeModel = map.getNamedItem( "name" ).getTextContent() + " " + map.getNamedItem( "model" ).getTextContent();
            u.Tonnage = Integer.parseInt( map.getNamedItem( "tons" ).getTextContent());
            //u.BaseBV = Float.parseFloat( map.getNamedItem("bv").getTextContent());
        }

        // all done, return the unit
        return u;
    }
}
