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
package bvcalc;

import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
/**
 *
 * @author gblouin
 */
public class SSWReader {
    frmMain2 Parent;
    Force force;

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

    private Unit BuildUnit( Document d ) throws Exception {
        Unit u = new Unit();
        NodeList n = d.getElementsByTagName( "mech" );
        NamedNodeMap map = n.item( 0 ).getAttributes();
        String Model = "";
        int Tonnage = 0;
        // basics
        boolean omnimech = Boolean.parseBoolean( map.getNamedItem( "omnimech" ).getTextContent() );

        if (omnimech) {
            Model = map.getNamedItem( "name" ).getTextContent() + " " + map.getNamedItem( "model" ).getTextContent();
            Tonnage = Integer.parseInt( map.getNamedItem( "tons" ).getTextContent());

            dlgOmnis Omnis = new dlgOmnis(Parent, true);

            NodeList Variants = d.getElementsByTagName("loadout");
            for (int i=0; i<=Variants.getLength()-1; i++) {
                if (Variants.item(i).getNodeName().equals("loadout")) {
                    NamedNodeMap atts = Variants.item(i).getAttributes();
                    Unit unit = new Unit();
                    unit.TypeModel = Model + " " + atts.getNamedItem("name").getTextContent();
                    unit.Tonnage = Tonnage;
                    unit.BaseBV = Float.parseFloat(Variants.item(i).getFirstChild().getNextSibling().getTextContent());
                    Omnis.choices.add(unit);
                }
            }
            Omnis.Load();
            Omnis.setLocationRelativeTo(Parent);
            Omnis.setVisible(true);
            if (Omnis.result) {
                u = Omnis.Variant;
            }

        } else {
            u.TypeModel = map.getNamedItem( "name" ).getTextContent() + " " + map.getNamedItem( "model" ).getTextContent();
            u.Tonnage = Integer.parseInt( map.getNamedItem( "tons" ).getTextContent());
            u.BaseBV = Float.parseFloat( d.getElementsByTagName("battle_value").item(0).getTextContent() );
        }
        u.Refresh();
        // all done, return the unit
        return u;
    }
}
