/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package BFB.IO;

import BFB.Common.CommonTools;
import BFB.Force;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import ssw.battleforce.BattleForceStats;
import ssw.components.Mech;
import ssw.filehandlers.MechList;
import ssw.filehandlers.MechListData;
import ssw.utilities.CostBVBreakdown;

public class TXTWriter {
    Force[] forces;

    public TXTWriter( ) {

    }

    public TXTWriter( Force[] forces ) {
        this.forces = forces;
    }

    public void Write( String filename ) throws IOException {
        if ( !filename.endsWith(".txt") ) { filename += ".txt"; }
        BufferedWriter FR = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );

        for (Force force : forces) {
            FR.write(force.SerializeClipboard());
            FR.newLine();
        }
        FR.close();
    }

    public void WriteList(String filename, MechList list) throws IOException {
        if ( !filename.endsWith(".csv") ) { filename += ".csv"; }
        BufferedWriter FR = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );

        FR.write( CSVFormat("UNIT_TYPE") );
        FR.write( CSVFormat("Sub_Unit_Type") );
        FR.write( CSVFormat("Unit_Name") );
        FR.write( CSVFormat("Model_Number") );
        FR.write( CSVFormat("TONNAGE") );
        FR.write( CSVFormat("Canon Verified") );
        FR.write( CSVFormat("TW RULES_LEVEL") );
        FR.write( CSVFormat("Technology Base") );
        FR.write( CSVFormat("SOURCE") );
        FR.write( CSVFormat("DATE") );
        FR.write( CSVFormat("Era") );
        FR.write( CSVFormat("Introduced") );

        String datum = "";
        for (int i=0; i < list.Size(); i++) {
            MechListData data = (MechListData) list.Get(i);
            FR.write( CSVFormat("BattleMech") );
            datum = "BattleMech";
            if ( data.isOmni() ) { datum = "OmniMech"; }
            FR.write( CSVFormat(datum) );
            FR.write( CSVFormat(data.getName()) );
            FR.write( CSVFormat(data.getModel()) );
            FR.write( CSVFormat(data.getTonnage()+"") );
            FR.write( CSVFormat("N") );
            FR.write( CSVFormat(data.getLevel()) );
            FR.write( CSVFormat(data.getTech()) );
            FR.write( CSVFormat(data.getSource()) );
            FR.write( CSVFormat(data.getYear()+"") );
            FR.write( CSVFormat(data.getEra()) );
            FR.write( "\"\"" );
            FR.newLine();
        }
        FR.close();

        String message = "";
        for ( int i=0; i < list.Size(); i++ ) {
            ssw.Force.Unit u = ((MechListData) list.Get(i)).getUnit();
            u.LoadMech();
            if ( u.m != null ) {
                WriteCost( u.m, filename.replace("MechListing.txt", "") );
            } else {
                message += u.TypeModel + "\n";
            }
        }

        if ( !message.isEmpty() ) {
            CommonTools.Messager("Could not write out the following:\n" + message);
        }
    }

    public void WriteCost( Mech m, String filename ) throws IOException {
        filename += m.GetFullName();
        if ( !filename.endsWith(".txt") ) { filename += ".txt"; }
        BufferedWriter FR = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );

        CostBVBreakdown cost = new CostBVBreakdown(m);
        FR.write(cost.Render());
        FR.close();
    }

    public void WriteBFList(String filename, MechList list) throws IOException {
        if ( !filename.endsWith(".csv") ) { filename += ".csv"; }
        BufferedWriter FR = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );

        String message = "";
        FR.write("Element,PV,Wt,MV,S,M,L,E,OV,Armor,Internal,Special Abilities");
        FR.newLine();
        for ( MechListData mech : list.getList() ) {
            FR.write(mech.getBattleForceStats().SerializeCSV());
            FR.newLine();
        }

        FR.close();

        if ( !message.isEmpty() ) {
            CommonTools.Messager("Could not write out the following:\n" + message);
        }
    }


    
    public String CSVFormat( String data ) {
        return "\"" + data + "\", ";
    }
}
