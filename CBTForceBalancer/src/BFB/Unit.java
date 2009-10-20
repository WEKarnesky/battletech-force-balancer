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

import BFB.IO.PrintSheet;
import BFB.Common.CommonTools;
import BFB.Common.Constants;
import java.io.BufferedWriter;
import java.io.IOException;
import org.w3c.dom.Node;
import ssw.battleforce.BattleForceStats;
import ssw.components.Mech;
import ssw.filehandlers.MechListData;
import ssw.filehandlers.XMLReader;

public class Unit implements ifSerializable {
    public String TypeModel = "",
                  Type = "",
                  Model = "";
    private String Mechwarrior = "";
    public String Filename = "",
                  Configuration = "",
                  Group = "";
    private String MechwarriorQuirks = "";
    public String UnitQuirks = "";
    public float BaseBV = 0.0f,
                 MiscMod = 1.0f,
                 Tonnage = 20.0f,
                 SkillsBV = 0.0f,
                 ModifierBV = 0.0f,
                 C3BV = 0.0f,
                 TotalBV = 0.0f;
    private int Piloting = 5;
    private int Gunnery = 4;
    public int UnitType = Constants.BattleMech;
    public Warrior warrior = new Warrior();
    public boolean UsingC3 = false;
    public Mech m = null;
    private BattleForceStats BFStats = new BattleForceStats();

    public Unit(){
    }

    public Unit( MechListData m ) {
        this.Type = m.getName();
        this.Model = m.getModel();
        this.TypeModel = m.getName() + " "  + m.getModel();
        this.Tonnage = m.getTonnage();
        this.BaseBV = m.getBV();
        this.Filename = m.getFilename();
        this.Configuration = m.getConfig();
        this.BFStats = m.getBattleForceStats();
    }

    public Unit( Node n ) throws Exception {
        for (int i=0; i < n.getChildNodes().getLength(); i++) {
            String nodeName = n.getChildNodes().item(i).getNodeName();

            if ( !nodeName.equals("#text") ) {
                //Previous File structure
                if (nodeName.equals("type")) {Type = n.getChildNodes().item(i).getTextContent().trim();}
                if (nodeName.equals("model")) {Model = n.getChildNodes().item(i).getTextContent().trim();}
                if (nodeName.equals("config")) {Configuration = n.getChildNodes().item(i).getTextContent().trim();}
                if (nodeName.equals("tonnage")) {Tonnage = Float.parseFloat(n.getChildNodes().item(i).getTextContent());}
                if (nodeName.equals("basebv")) {BaseBV = Float.parseFloat(n.getChildNodes().item(i).getTextContent());}
                if (nodeName.equals("modifier")) {MiscMod = Float.parseFloat(n.getChildNodes().item(i).getTextContent());}
                if (nodeName.equals("piloting")) {Piloting = Integer.parseInt(n.getChildNodes().item(i).getTextContent());}
                if (nodeName.equals("gunnery")) {Gunnery = Integer.parseInt(n.getChildNodes().item(i).getTextContent());}
                if (nodeName.equals("unittype")) {UnitType = Integer.parseInt(n.getChildNodes().item(i).getTextContent());}
                if (nodeName.equals("usingc3")) {UsingC3 = Boolean.parseBoolean(n.getChildNodes().item(i).getTextContent());}
                if (nodeName.equals("mechwarrior")) {Mechwarrior = n.getChildNodes().item(i).getTextContent().trim();}
                if (nodeName.equals("ssw")) {Filename = n.getChildNodes().item(i).getTextContent().trim();}
                if (nodeName.equals("group")) {Group = n.getChildNodes().item(i).getTextContent().trim();}
                if (nodeName.equals("mechwarriorquirks")) {MechwarriorQuirks = n.getChildNodes().item(i).getTextContent().trim();}
                if (nodeName.equals("unitquirks")) {UnitQuirks = n.getChildNodes().item(i).getTextContent().trim();}
            }
        }
        this.Refresh();
        TypeModel = Type + " " + Model;
        this.warrior.setGunnery(Gunnery);
        this.warrior.setPiloting(Piloting);
        this.warrior.setName(Mechwarrior);
        this.warrior.setQuirks(MechwarriorQuirks);
    }

    public Unit( Node n, int Version ) {
        this.Type = n.getAttributes().getNamedItem("type").getTextContent().trim();
        this.Model = n.getAttributes().getNamedItem("model").getTextContent().trim();
        TypeModel = Type + " " + Model;
        this.Configuration = n.getAttributes().getNamedItem("config").getTextContent().trim();
        this.Tonnage = Float.parseFloat(n.getAttributes().getNamedItem("tonnage").getTextContent().trim());
        this.BaseBV = Float.parseFloat(n.getAttributes().getNamedItem("bv").getTextContent().trim());
        this.UnitType = Integer.parseInt(n.getAttributes().getNamedItem("design").getTextContent().trim());
        this.Filename = n.getAttributes().getNamedItem("file").getTextContent().trim();
        this.UsingC3 = Boolean.parseBoolean(n.getAttributes().getNamedItem("c3status").getTextContent().trim());

        for (int i=0; i < n.getChildNodes().getLength(); i++) {
            Node node = n.getChildNodes().item(i);
            if ( node.getNodeName().equals("quirks") ) { this.UnitQuirks = node.getTextContent().trim(); }
            if ( node.getNodeName().equals("warrior") ) {
                try {
                    this.warrior = new Warrior(node);
                    this.Gunnery = warrior.getGunnery();
                    this.Piloting = warrior.getPiloting();
                    this.MechwarriorQuirks = warrior.getQuirks();
                    this.Mechwarrior = (warrior.getRank() + " " + warrior.getName()).trim();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if ( node.getNodeName().equals("battleforce") ) {
                this.BFStats = new BattleForceStats( node );
                this.BFStats.setElement(this.TypeModel);
            }
        }
        this.Refresh();
    }

    public void Refresh() {
        SkillsBV = 0;
        ModifierBV = 0;
        TotalBV = 0;
        SkillsBV += CommonTools.GetSkillBV(BaseBV, getGunnery(), getPiloting());
        ModifierBV += CommonTools.GetModifierBV(SkillsBV, MiscMod);
        TotalBV += CommonTools.GetFullAdjustedBV(BaseBV, getGunnery(), getPiloting(), MiscMod);
        if (UsingC3) { C3BV += TotalBV * .05;}

        if ( BFStats.getPointValue() == 0 ) {
            LoadMech();
            if ( m != null ) {
                BFStats = new BattleForceStats(m);
            }
        }
    }

    public void UpdateByMech() {
        TypeModel = m.GetFullName();
        Configuration = m.GetLoadout().GetName();
        BaseBV = m.GetCurrentBV();
        Refresh();
    }

    public String GetSkills(){
        return getGunnery() + "/" + getPiloting();
    }

    public void RenderPrint(PrintSheet p) {
        p.setFont(CommonTools.PlainFont);
        p.WriteStr(TypeModel, 120);
        p.WriteStr(getMechwarrior(), 140);
        p.WriteStr(Constants.UnitTypes[UnitType], 60);
        p.WriteStr(String.format("%1$,.2f", Tonnage), 50);
        p.WriteStr(String.format("%1$,.0f", BaseBV), 40);
        p.WriteStr(GetSkills(), 30);
        p.WriteStr(String.format("%1$,.2f", MiscMod), 40);
        p.WriteStr(Boolean.valueOf(UsingC3).toString(), 30);
        p.WriteStr(String.format("%1$,.0f", TotalBV), 0);
        p.NewLine();
    }

    public void SerializeXML(BufferedWriter file) throws IOException {
        file.write(CommonTools.Tabs(4) + "<unit type=\"" + this.Type + "\" model=\"" + this.Model + "\" config=\"" + this.Configuration + "\" tonnage=\"" + this.Tonnage + "\" bv=\"" + this.BaseBV + "\" design=\"" + this.UnitType + "\" file=\"" + this.Filename + "\" c3status=\"" + this.UsingC3 + "\">");
        file.newLine();
        BFStats.SerializeXML(file, 5);
        file.newLine();
        file.write(CommonTools.Tabs(5) + "<quirks>" + this.UnitQuirks + "</quirks>");
        file.newLine();
        warrior.SerializeXML(file);
        file.write(CommonTools.Tabs(4) + "</unit>");
        file.newLine();
    }

    public void SerializeMUL(BufferedWriter file) throws IOException {
        if ( this.Type.contains("(") && this.Type.contains(")") ) {
            this.Type = this.Type.substring(0, this.Type.indexOf(" (")).trim();
        }

        this.Model.replace("Alternate Configuration", "");
        this.Model.replace("Alternate", "");
        this.Model.replace("Alt", "");
        this.Model.trim();

        file.write(CommonTools.tab + "<entity chassis=\"" + this.Type + "\" model=\"" + this.Model + "\">");
        file.newLine();
        file.write(CommonTools.tab + CommonTools.tab + "<pilot name=\"" + this.getMechwarrior() + "\" gunnery=\"" + this.getGunnery() + "\" piloting=\"" + this.getPiloting() + "\" />");
        file.newLine();
        file.write(CommonTools.tab + "</entity>");
        file.newLine();
    }

    public String SerializeClipboard() {
        String data = "";

        data += CommonTools.spaceRight(this.TypeModel.trim(), 30) + Constants.Tab;
        data += String.format("%1$,.0f", Tonnage) + Constants.Tab;
        data += String.format("%1$,.0f", BaseBV) + "" + Constants.Tab;
        data += CommonTools.spaceRight(this.getMechwarrior(), 30) + Constants.Tab;
        data += CommonTools.spaceRight(this.Group, 20) + Constants.Tab;
        data += this.getGunnery() + "/" + this.getPiloting() + Constants.Tab;
        data += String.format("%1$,.0f", TotalBV) + "";

        return data;
    }

    public String SerializeData() {
        return "";
    }

    public void LoadMech() {
        if ( m == null ) {
            try {
                XMLReader reader = new XMLReader();
                this.m = reader.ReadMech( this.Filename );
                if ( ! this.Configuration.isEmpty() ) {
                    this.m.SetCurLoadout(this.Configuration.trim());
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public BattleForceStats getBFStats() {
        if ( BFStats != null ) { return BFStats; }

        LoadMech();
        if ( m != null ) {
            BFStats = new BattleForceStats(m, Group, getGunnery(), getPiloting());
        }
        return BFStats;
    }

    public String getMechwarrior() {
        return warrior.getName();
    }

    public void setMechwarrior(String Mechwarrior) {
        warrior.setName(Mechwarrior);
    }

    public String getMechwarriorQuirks() {
        return warrior.getQuirks();
    }

    public void setMechwarriorQuirks(String MechwarriorQuirks) {
        warrior.setQuirks(MechwarriorQuirks);
    }

    public int getPiloting() {
        return warrior.getPiloting();
    }

    public void setPiloting(int Piloting) {
        warrior.setPiloting(Piloting);
    }

    public int getGunnery() {
        return warrior.getGunnery();
    }

    public void setGunnery(int Gunnery) {
        warrior.setGunnery(Gunnery);
    }
}