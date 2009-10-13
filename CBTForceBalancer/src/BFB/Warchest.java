/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package BFB;

import BFB.Common.CommonTools;
import BFB.Common.Constants;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import org.w3c.dom.Node;

public class Warchest implements ifSerializable {
    private int TrackCost = 0;
    private Vector<Bonus> bonuses = new Vector<Bonus>();
    private Vector<Objective> objectives = new Vector<Objective>();

    public Warchest() {
        
    }
    
    public Warchest( Node node ) {
        setTrackCost(Integer.parseInt(node.getAttributes().getNamedItem("cost").getTextContent().trim()));
        for (int i=0; i < node.getChildNodes().getLength(); i++) {
            Node n = node.getChildNodes().item(i);

            if (n.getNodeName().equals("bonus")) {bonuses.add(new Bonus(n)); }
            if (n.getNodeName().equals("objective")) {objectives.add(new Objective(n)); }
        }
    }

    public void SerializeXML(BufferedWriter file) throws IOException {
        if ( bonuses.size() > 0 && objectives.size() > 0 ) {
            file.write( CommonTools.Tabs(1) + "<warchest cost=\"" + this.TrackCost + "\">" );
            file.newLine();

            for ( Bonus b : bonuses ) {
                b.SerializeXML(file);
            }

            for ( Objective o : objectives ) {
                o.SerializeXML(file);
            }

            file.write( CommonTools.Tabs(1) + "</warchest>" );
            file.newLine();
        }
    }

    public String SerializeClipboard() {
        String data = "";
        int counter = 1;

        data += "Warchest" + Constants.NL;
        data += "  Track Cost: " + TrackCost + Constants.NL;
        data += "  Optional Bonuses" + Constants.NL;
        for ( Bonus b : bonuses ) {
            data += b.SerializeClipboard();
        }
        data += "  Objectives" + Constants.NL;
        for ( Objective o : objectives ) {
            data += counter + ". " + o.SerializeClipboard();
            counter += 1;
        }
        return data;
    }

    public String SerializeData() {
        return "";
    }

    public int getTrackCost() {
        return TrackCost;
    }

    public void setTrackCost(int TrackCost) {
        this.TrackCost = TrackCost;
    }

    public Vector<Bonus> getBonuses() {
        return bonuses;
    }

    public void setBonuses(Vector<Bonus> bonuses) {
        this.bonuses = bonuses;
    }

    public Vector<Objective> getObjectives() {
        return objectives;
    }

    public void setObjectives(Vector<Objective> objectives) {
        this.objectives = objectives;
    }

    public AbstractTableModel getBonusTable() {
        AbstractTableModel model = new AbstractTableModel() {

            public int getRowCount() {
                return bonuses.size();
            }

            public int getColumnCount() {
                return 2;
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                switch (columnIndex) {
                    case 0: return bonuses.get(rowIndex).getValue();
                    case 1: return bonuses.get(rowIndex).getDescription();
                }
                return null;
            }

            @Override
            public String getColumnName( int col ) {
                switch( col ) {
                    case 0: return "Amount";
                    case 1: return "Bonus";
                }
                return null;
            }
        };

        return model;
    }

    public AbstractTableModel getObjectiveTable() {
        AbstractTableModel model = new AbstractTableModel() {

            public int getRowCount() {
                return objectives.size();
            }

            public int getColumnCount() {
                return 2;
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                switch (columnIndex) {
                    case 0: return objectives.get(rowIndex).getDescription();
                    case 1: return objectives.get(rowIndex).getValue();
                }
                return null;
            }

            @Override
            public String getColumnName( int col ) {
                switch( col ) {
                    case 0: return "Objective";
                    case 1: return "Reward";
                }
                return null;
            }
        };

        return model;
    }
    
}
