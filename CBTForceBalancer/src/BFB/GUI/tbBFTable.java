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
import ssw.battleforce.BattleForceStats;

public class tbBFTable extends abTable {
    private Force force;
    
    public tbBFTable( Force f ) {
        force = f;
    }

    public void setupTable( JTable tbl ) {
        //Create a sorting class and apply it to the list
        TableRowSorter Leftsorter = new TableRowSorter<tbBFTable>(this);
        List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(4, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(8, SortOrder.ASCENDING));
        Leftsorter.setSortKeys(sortKeys);
        tbl.setRowSorter(Leftsorter);

        tbl.getColumnModel().getColumn(0).setPreferredWidth(150);
        tbl.getColumnModel().getColumn(1).setPreferredWidth(50);
        tbl.getColumnModel().getColumn(2).setPreferredWidth(100);
        tbl.getColumnModel().getColumn(3).setPreferredWidth(40);
        tbl.getColumnModel().getColumn(4).setPreferredWidth(20);
        tbl.getColumnModel().getColumn(5).setPreferredWidth(20);
        tbl.getColumnModel().getColumn(6).setPreferredWidth(20);
        tbl.getColumnModel().getColumn(7).setPreferredWidth(20);
    }

    @Override
    public String getColumnName( int col ) {
        switch( col ) {
            case 0:
                return "Unit";
            case 1:
                return "Type";
            case 2:
                return "Lance/Star";
            case 3:
                return "MV";
            case 4:
                return "S";
            case 5:
                return "M";
            case 6:
                return "L";
            case 7:
                return "E";
            case 8:
                return "Wt";
            case 9:
                return "OV";
            case 10:
                return "Arm/Int";
            case 11:
                return "Base PV";
            case 12:
                return "Adj PV";
        }
        return "";
    }
    public int getRowCount() { return force.Units.size(); }
    public int getColumnCount() { return 13; }
    @Override
    public Class getColumnClass(int c) {
        if (force.Units.size() > 0) {
            return getClassOf(0, c).getClass();
        } else {
            return String.class;
        }
    }
    public Object getClassOf( int row, int col ) {
        Unit u = force.Units.get( row );
        BattleForceStats stat = u.getBFStats();
        switch( col ) {
            case 0:
                return stat.getElement();
            case 1:
                return "";
            case 2:
                return u.Group;
            case 3:
                return stat.getMovement();
            case 4:
                return stat.getShort();
            case 5:
                return stat.getMedium();
            case 6:
                return stat.getLong();
            case 7:
                return stat.getExtreme();
            case 8:
                return stat.getWeight();
            case 9:
                return stat.getOverheat();
            case 10:
                return stat.getArmor() + " (" + stat.getInternal() + ")";
            case 11:
                return stat.getBasePV();
            case 12:
                return stat.getPointValue();
        }
        return "";
    }
    public Object getValueAt( int row, int col ) {
        Unit u = force.Units.get( row );
        BattleForceStats stat = u.getBFStats();
        switch( col ) {
            case 0:
                return stat.getElement();
            case 1:
                return Constants.UnitTypes[u.UnitType];
            case 2:
                return u.Group;
            case 3:
                return stat.getMovement();
            case 4:
                return stat.getShort();
            case 5:
                return stat.getMedium();
            case 6:
                return stat.getLong();
            case 7:
                return stat.getExtreme();
            case 8:
                return stat.getWeight();
            case 9:
                return stat.getOverheat();
            case 10:
                return stat.getArmor() + " (" + stat.getInternal() + ")";
            case 11:
                return stat.getBasePV();
            case 12:
                return stat.getPointValue();
        }
        return null;
    }
    @Override
    public boolean isCellEditable( int row, int col ) {
        return false;
    }
}
