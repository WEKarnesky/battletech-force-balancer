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

package BFB.GUI;

import BFB.*;
import BFB.Common.*;
import BFB.GUI.abTable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;

public class tbTWTable extends abTable {
    private Force force;

    public tbTWTable( Force f ) {
        force = f;
    }

    public void setupTable( JTable tbl ) {
        //Create a sorting class and apply it to the list
        TableRowSorter Leftsorter = new TableRowSorter<tbTWTable>(this);
        List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(4, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(8, SortOrder.ASCENDING));
        Leftsorter.setSortKeys(sortKeys);
        tbl.setRowSorter(Leftsorter);

        tbl.getColumnModel().getColumn(0).setPreferredWidth(150);
        tbl.getColumnModel().getColumn(1).setPreferredWidth(50);
        tbl.getColumnModel().getColumn(2).setPreferredWidth(150);
        tbl.getColumnModel().getColumn(3).setPreferredWidth(100);
        tbl.getColumnModel().getColumn(4).setPreferredWidth(40);
        tbl.getColumnModel().getColumn(5).setPreferredWidth(20);
        tbl.getColumnModel().getColumn(6).setPreferredWidth(20);
        tbl.getColumnModel().getColumn(7).setPreferredWidth(20);
        tbl.getColumnModel().getColumn(8).setPreferredWidth(30);
        tbl.getColumnModel().getColumn(9).setPreferredWidth(30);
    }

    @Override
    public String getColumnName( int col ) {
        switch( col ) {
            case 0:
                return "Unit";
            case 1:
                return "Type";
            case 2:
                return "Mechwarrior";
            case 3:
                return "Lance/Star";
            case 4:
                return "Tons";
            case 5:
                return "Base BV";
            case 6:
                return "G";
            case 7:
                return "P";
            case 8:
                return "Mod";
            case 9:
                return "C3";
            case 10:
                return "Adj BV";
        }
        return "";
    }
    public int getRowCount() { return force.Units.size(); }
    public int getColumnCount() { return 11; }
    @Override
    public Class getColumnClass(int c) {
        if (force.Units.size() > 0) {
            return getClassOf(0, c).getClass();
        } else {
            return String.class;
        }
    }
    public Object getClassOf( int row, int col ) {
        Unit u = (Unit) force.Units.get( row );
        switch( col ) {
            case 0:
                return u.TypeModel;
            case 1:
                return "";
            case 2:
                return u.Mechwarrior;
            case 3:
                return u.Group;
            case 4:
                return u.Tonnage;
            case 5:
                return u.BaseBV;
            case 6:
                return u.Gunnery;
            case 7:
                return u.Piloting;
            case 8:
                return u.MiscMod;
            case 9:
                return "";
            case 10:
                return "";
        }
        return "";
    }
    public Object getValueAt( int row, int col ) {
        Unit u = (Unit) force.Units.get( row );
        switch( col ) {
            case 0:
                return u.TypeModel;
            case 1:
                return Constants.UnitTypes[u.UnitType];
            case 2:
                return u.Mechwarrior;
            case 3:
                return u.Group;
            case 4:
                return u.Tonnage;
            case 5:
                return u.BaseBV;
            case 6:
                return u.Gunnery;
            case 7:
                return u.Piloting;
            case 8:
                return u.MiscMod;
            case 9:
                if( u.UsingC3 ) {
                    return "Yes";
                } else {
                    return "No";
                }
            case 10:
                return String.format( "%1$,.0f", u.TotalBV );
        }
        return null;
    }
    @Override
    public boolean isCellEditable( int row, int col ) {
        switch( col ) {
            case 2:
                return true;
            case 3:
                return true;
            case 6:
                return true;
            case 7:
                return true;
            case 8:
                return true;
        }
        return false;
    }
    @Override
    public void setValueAt( Object value, int row, int col ) {
        Unit u = (Unit) force.Units.get( row );
        switch( col ) {
            case 2:
                u.Mechwarrior = value.toString();
                break;
            case 3:
                u.Group = value.toString();
                break;
            case 6:
                u.Gunnery = Integer.parseInt(value.toString());
                break;
            case 7:
                u.Piloting = Integer.parseInt(value.toString());
                break;
            case 8:
                u.MiscMod = Float.parseFloat(value.toString());
                break;
        }
        force.isDirty = true;
        u.Refresh();
        force.RefreshBV();
    }

}
