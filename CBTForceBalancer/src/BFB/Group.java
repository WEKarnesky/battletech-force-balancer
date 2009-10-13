
package BFB;

import BFB.Common.CommonTools;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Vector;
import org.w3c.dom.Node;
import ssw.battleforce.BattleForce;
import ssw.battleforce.BattleForceStats;

public class Group {
    private String Name = "",
                   Type = ssw.battleforce.BattleForce.InnerSphere;
    private Vector<Unit> Units = new Vector<Unit>();
    private Force force;
    public float TotalBV = 0.0f;

    public Group( String Name, String Type, Force force ) {
        this.Name = Name;
        this.Type = Type;
        this.force = force;
    }

    public Group( Node node, int Version ) {
        this.Name = node.getAttributes().getNamedItem("name").getTextContent().trim();
        for (int i=0; i < node.getChildNodes().getLength(); i++) {
            Node n = node.getChildNodes().item(i);
            if ( n.getNodeName().equals("unit") ) {
                Unit u = new Unit(n, Version);
                u.Group = Name;
                u.Refresh();
                Units.add( u );
            }
        }
        updateBV();
    }

    public void updateBV() {
        TotalBV = 0.0f;
        for ( Unit u : Units ) {
            TotalBV += u.TotalBV;
        }
    }

    public void AddUnit( Unit u ) {
        Units.add(u);
    }

    public BattleForce toBattleForce() {
        BattleForce bf = new BattleForce();
        bf.Type = getType();
        bf.ForceName = getForce().ForceName;
        bf.LogoPath = getForce().LogoPath;
        for ( int i=0; i < Units.size(); i++ ) {
            Unit u = (Unit) Units.get(i);
            u.LoadMech();
            BattleForceStats stat = new BattleForceStats(u.m, u.Group, u.Gunnery, u.Piloting);
            bf.BattleForceStats.add(stat);
        }
        return bf;
    }

    public void SerializeXML( BufferedWriter file ) throws IOException {
        file.write( CommonTools.Tabs(3) + "<group name=\"" + this.Name + "\">");
        file.newLine();
        for ( Unit u : Units ) {
            u.SerializeXML(file);
        }
        file.write( CommonTools.Tabs(3) + "</group>");
        file.newLine();
    }

    public Vector<Unit> getUnits() {
        return Units;
    }

    public String getName() {
        return Name;
    }

    public String getType() {
        return Type;
    }

    public Force getForce() {
        return force;
    }

    public float getTotalBV() {
        return TotalBV;
    }
}
