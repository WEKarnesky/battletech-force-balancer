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
import java.io.BufferedWriter;
import java.io.IOException;
import org.w3c.dom.*;

public class Warrior {
    private String  Name = "",
                    Rank = "",
                    Faction = "",
                    Quirks = "",
                    Status = "Actives",
                    Notes = "";
    private int     Gunnery = 4,
                    Piloting = 5;
    private double  ManeiDomini = 1.0d;

    public Warrior() {

    }

    public Warrior( Node n ) throws Exception {
        NamedNodeMap map = n.getAttributes();
        Name = map.getNamedItem("name").getTextContent().trim();
        Status = map.getNamedItem("status").getTextContent().trim();

        for (int i=0; i < n.getChildNodes().getLength(); i++) {
            String nodeName = n.getChildNodes().item(i).getNodeName();

            if (nodeName.equals("affiliation")) {
                map = n.getChildNodes().item(i).getAttributes();
                Faction = map.getNamedItem("faction").getTextContent().trim();
                Rank = map.getNamedItem("rank").getTextContent().trim();
            }

            if (nodeName.equals("skills")) {
                map = n.getChildNodes().item(i).getAttributes();
                Gunnery = Integer.parseInt(map.getNamedItem("gunnery").getTextContent().trim());
                Piloting = Integer.parseInt(map.getNamedItem("piloting").getTextContent().trim());
                ManeiDomini = Double.parseDouble(map.getNamedItem("mod").getTextContent().trim());
            }

            if (nodeName.equals("quirks")) {Quirks = n.getChildNodes().item(i).getTextContent().trim();}
            if (nodeName.equals("notes")) {Notes = n.getChildNodes().item(i).getTextContent().trim();}
        }
    }

    public void SerializeXML(BufferedWriter file) throws IOException {
        file.write(CommonTools.Tabs(5) + "<warrior name=\"" + this.Name.trim() + "\" status=\"" + this.Status.trim() + "\">");
        file.newLine();
        file.write(CommonTools.Tabs(6) + "<affiliation faction=\"" + this.Faction.trim() + "\" rank=\"" + this.getRank().trim() + "\" />");
        file.newLine();
        file.write(CommonTools.Tabs(6) + "<skills gunnery=\"" + this.Gunnery + "\" piloting=\"" + this.Piloting + "\" mod=\"" + this.getManeiDomini() + "\" />");
        file.newLine();
        file.write(CommonTools.Tabs(6) + "<quirks>" + this.Quirks.trim() + "</quirks>");
        file.newLine();
        file.write(CommonTools.Tabs(6) + "<notes>" + this.Notes + "</notes>");
        file.newLine();
        file.write(CommonTools.Tabs(5) + "</warrior>");
        file.newLine();
    }

    public void SerializeMUL(BufferedWriter file) throws IOException {
        file.write(CommonTools.Tabs(2) + "<pilot name=\"" + this.Name + "\" gunnery=\"" + this.Gunnery + "\" piloting=\"" + this.Piloting + "\" />");
        file.newLine();
    }

    @Override
    public String toString() {
        return (this.Rank + " " + this.Name + " " + this.Gunnery + "/" + this.Piloting).trim();
    }

    /**
     * @return the Name
     */
    public String getName() {
        return Name;
    }

    /**
     * @param Name the Name to set
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     * @return the Quirks
     */
    public String getQuirks() {
        return Quirks;
    }

    /**
     * @param Quirks the Quirks to set
     */
    public void setQuirks(String Quirks) {
        this.Quirks = Quirks;
    }

    /**
     * @return the Status
     */
    public String getStatus() {
        return Status;
    }

    /**
     * @param Status the Status to set
     */
    public void setStatus(String Status) {
        this.Status = Status;
    }

    /**
     * @return the Gunnery
     */
    public int getGunnery() {
        return Gunnery;
    }

    /**
     * @param Gunnery the Gunnery to set
     */
    public void setGunnery(int Gunnery) {
        this.Gunnery = Gunnery;
    }

    /**
     * @return the Piloting
     */
    public int getPiloting() {
        return Piloting;
    }

    /**
     * @param Piloting the Piloting to set
     */
    public void setPiloting(int Piloting) {
        this.Piloting = Piloting;
    }

    /**
     * @return the Notes
     */
    public String getNotes() {
        return Notes;
    }

    /**
     * @param Notes the Notes to set
     */
    public void setNotes(String Notes) {
        this.Notes = Notes;
    }

    /**
     * @return the Faction
     */
    public String getFaction() {
        return Faction;
    }

    /**
     * @param Faction the Faction to set
     */
    public void setFaction(String Faction) {
        this.Faction = Faction;
    }

    /**
     * @return the Rank
     */
    public String getRank() {
        return Rank;
    }

    /**
     * @param Rank the Rank to set
     */
    public void setRank(String Rank) {
        this.Rank = Rank;
    }

    /**
     * @return the ManeiDomini
     */
    public double getManeiDomini() {
        return ManeiDomini;
    }

    /**
     * @param ManeiDomini the ManeiDomini to set
     */
    public void setManeiDomini(double ManeiDomini) {
        this.ManeiDomini = ManeiDomini;
    }

    public String getSkills() {
        return this.Gunnery + "/" + this.Piloting;
    }


}
