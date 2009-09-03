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

import BFB.IO.XMLReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import org.w3c.dom.*;

public class Warriors extends AbstractTableModel {
    private Vector list = new Vector();
    private String defaultPath = "data/WarriorList.xml";

    public Warriors() {
        Load(defaultPath);
    }

    public Warriors ( String filename ) {
        Load(filename);
    }

    public void Load( String filename ) {
        XMLReader reader = new XMLReader();
        Node node;
        try {
            node = reader.ReadWarriors(filename);
            if ( node.hasChildNodes() ) {
                for (int i=0; i < node.getChildNodes().getLength(); i++) {
                    if ( node.getChildNodes().item(i).getNodeName().equals("warrior")) {
                        list.add(new Warrior(node.getChildNodes().item(i)));
                    }
                }
            }
        } catch (Exception ex) {
            //do something else?
        }
    }

    public void Add( Warrior warrior ) {
        list.add(warrior);
    }

    public Warrior Get( int index ) {
        return (Warrior) list.get(index);
    }

    public void Remove( Warrior warrior ) {
        list.remove(warrior);
    }

    public void SerializeXML(BufferedWriter file) throws IOException {
        file.write( "<warriors>" );
        file.newLine();

        for (int i = 0; i < list.size(); i++) {
            Warrior w = (Warrior) list.get(i);
            w.SerializeXML(file);
        }

        file.write("</warriors>" );
    }


    public void setupTable( JTable tbl ) {
        tbl.setModel(this);

        //Create a sorting class and apply it to the list
        TableRowSorter Leftsorter = new TableRowSorter<Warriors>(this);
        List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        Leftsorter.setSortKeys(sortKeys);
        tbl.setRowSorter(Leftsorter);

        tbl.getColumnModel().getColumn(0).setPreferredWidth(80);
        tbl.getColumnModel().getColumn(1).setPreferredWidth(80);
    }
        
    @Override
    public String getColumnName( int col ) {
        switch( col ) {
            case 0:
                return "Name";
            case 1:
                return "Affiliation";
            case 2:
                return "Skills";
        }
        return "";
    }
    public int getRowCount() { return list.size(); }
    public int getColumnCount() { return 3; }
    @Override
    public Class getColumnClass(int c) {
        if (list.size() > 0) {
            return getClassOf(0, c).getClass();
        } else {
            return String.class;
        }
    }
    public Object getClassOf( int row, int col ) {
        Warrior w = (Warrior) list.get( row );
        switch( col ) {
            case 0:
                return w.getName();
            case 1:
                return "";
            case 2:
                return "";
        }
        return "";
    }
    public Object getValueAt( int row, int col ) {
        Warrior w = (Warrior) list.get( row );
        switch( col ) {
            case 0:
                return w.getName();
            case 1:
                return (w.getFaction() + " (" + w.getRank() + ")").replace(" ()", "");
            case 2:
                return w.getGunnery() + "/" + w.getPiloting();
        }
        return null;
    }
    @Override
    public boolean isCellEditable( int row, int col ) {
        return false;
    }
    @Override
    public void setValueAt( Object value, int row, int col ) {
    }


}
