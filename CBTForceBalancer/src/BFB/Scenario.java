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

package BFB;

import BFB.Common.CommonTools;
import BFB.Common.Constants;
import BFB.GUI.abTable;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import org.w3c.dom.Node;
import ssw.filehandlers.Media;

public class Scenario implements ifSerializable {
    public int VersionNumber = 2;
    private boolean allowOverwrite = true;
    private String Name = "",
                    Source = "",
                    Situation = "",
                    Setup = "",
                    Attacker = "",
                    Defender = "",
                    VictoryConditions = "",
                    SpecialRules = "",
                    Aftermath = "";
    private Vector<Force> forces = new Vector<Force>();
    private Warchest warchest = new Warchest();

    public Scenario() {
    }

    public Scenario( Node node ) {
        String errorMessage = "";

        this.Name = node.getAttributes().getNamedItem("name").getTextContent().trim();
        this.VersionNumber = Integer.parseInt(node.getAttributes().getNamedItem("version").getTextContent().trim());
        this.allowOverwrite = Boolean.parseBoolean(node.getAttributes().getNamedItem("overwrite").getTextContent().trim());

        for (int i=0; i < node.getChildNodes().getLength(); i++) {
            Node n = node.getChildNodes().item(i);
            if (n.getNodeName().equals("situation")) { setSituation(n.getTextContent()); }
            if (n.getNodeName().equals("setup")) { setSetup(n.getTextContent()); }
            if (n.getNodeName().equals("victoryconditions")) { setVictoryConditions(n.getTextContent()); }
            if (n.getNodeName().equals("aftermath")) { setAftermath(n.getTextContent()); }

            if (n.getNodeName().equals("attacker")) {
                setAttacker(n.getAttributes().getNamedItem("description").getTextContent().trim());
                try {
                    forces.add(new Force(n.getChildNodes().item(1), VersionNumber));
                } catch (Exception ex) {
                    errorMessage += "Error loading Attacker (" + ex.getMessage() + ")\n";
                }
            }
            if (n.getNodeName().equals("defender")) {
                setDefender(n.getAttributes().getNamedItem("description").getTextContent().trim());
                try {
                    forces.add(new Force(n.getChildNodes().item(1), VersionNumber));
                } catch (Exception ex) {
                    errorMessage += "Error loading Defender (" + ex.getMessage() + ")\n";
                }
            }
            if (n.getNodeName().equals("warchest")) {
                setWarchest(new Warchest(n.getFirstChild()));
            }
        }

        if ( !errorMessage.isEmpty() ) {
            Media.Messager("Errors occured during load:\n" + errorMessage);
        }
    }

    public Force topForce() {
        return getForces().get(0);
    }

    public Force bottomForce() {
        return getForces().get(1);
    }

    public void AddForce( Force f ) {
        forces.add(f);
    }

    public void AddListener( TableModelListener listener ) {
        for ( Force force : getForces() ) {
            force.addTableModelListener(listener);
        }
    }

    public void setupTable(JTable[] tables) {
        int i = 0;
        for ( Force force : getForces() ) {
            force.setupTable(tables[i]);
            i++;
        }

        forces.get(0).OpForSize = getForces().get(1).Units.size();
        forces.get(1).OpForSize = getForces().get(0).Units.size();
    }

    public void setModel( abTable model ) {
        for ( Force force : getForces() ) {
            force.setCurrentModel(model);
        }
    }

    public void SerializeXML(BufferedWriter file) throws IOException {
        file.write( "<scenario name=\"" + this.Name + "\" version=\"" + VersionNumber + "\" overwrite=\"" + allowOverwrite + "\">" );
        file.newLine();

        file.write( CommonTools.tab + "<situation>" + this.Situation + "</situation>" );
        file.newLine();

        file.write( CommonTools.tab + "<setup>" + this.Setup + "</setup>" );
        file.newLine();

        file.write( CommonTools.tab + "<attacker description=\"" + this.Attacker + "\">" );
        file.newLine();

        getAttackerForce().SerializeXML(file);

        file.write( CommonTools.tab + "</attacker>" );
        file.newLine();

        file.write( CommonTools.tab + "<defender description=\"" + this.Attacker + "\">" );
        file.newLine();

        getDefenderForce().SerializeXML(file);

        file.write( CommonTools.tab + "</defender>" );
        file.newLine();

        getWarchest().SerializeXML(file);

        file.write( CommonTools.tab + "<victoryconditions>" + this.VictoryConditions + "</victoryconditions>" );
        file.newLine();

        file.write( CommonTools.tab + "<aftermath>" + this.Aftermath + "</aftermath>" );
        file.newLine();

        file.write("</scenario>");
    }

    public String SerializeClipboard() {
        String data = "";

        data += this.Name + Constants.NL + Constants.NL;
        data += "Situation" + Constants.NL + this.Situation + Constants.NL + Constants.NL;
        data += "Setup" + Constants.NL + this.Setup + Constants.NL + Constants.NL;
        data += "Attacker" + Constants.NL + this.Attacker + Constants.NL + Constants.NL;
        data += getAttackerForce().SerializeClipboard() + Constants.NL;
        data += "Defender" + Constants.NL + this.Defender + Constants.NL + Constants.NL;
        data += getDefenderForce().SerializeClipboard() + Constants.NL;
        data += getWarchest().SerializeClipboard() + Constants.NL;
        data += "Victory Conditions" + Constants.NL + this.VictoryConditions + Constants.NL + Constants.NL;
        data += "Aftermath" + Constants.NL + this.Aftermath + Constants.NL + Constants.NL;

        return data;
    }

    public String SerializeData() {
        return SerializeClipboard();
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getSituation() {
        return Situation;
    }

    public void setSituation(String Situation) {
        this.Situation = Situation;
    }

    public String getSetup() {
        return Setup;
    }

    public void setSetup(String Setup) {
        this.Setup = Setup;
    }

    public String getAttacker() {
        return Attacker;
    }

    public void setAttacker(String Attacker) {
        this.Attacker = Attacker;
    }

    public Force getAttackerForce() {
        return topForce();
    }

    public String getDefender() {
        return Defender;
    }

    public void setDefender(String Defender) {
        this.Defender = Defender;
    }

    public Force getDefenderForce() {
        return bottomForce();
    }

    public String getVictoryConditions() {
        return VictoryConditions;
    }

    public void setVictoryConditions(String VictoryConditions) {
        this.VictoryConditions = VictoryConditions;
    }

    public String getSpecialRules() {
        return SpecialRules;
    }

    public void setSpecialRules(String SpecialRules) {
        this.SpecialRules = SpecialRules;
    }

    public String getAftermath() {
        return Aftermath;
    }

    public void setAftermath(String Aftermath) {
        this.Aftermath = Aftermath;
    }

    public Vector<Force> getForces() {
        return forces;
    }

    public void setForces(Vector<Force> forces) {
        this.forces = forces;
    }

    public Warchest getWarchest() {
        return warchest;
    }

    public void setWarchest(Warchest warchest) {
        this.warchest = warchest;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String Source) {
        this.Source = Source;
    }

    public boolean isOverwriteable() {
        return allowOverwrite;
    }

    public void setOverwriteable(boolean allowOverwrite) {
        this.allowOverwrite = allowOverwrite;
    }
}
