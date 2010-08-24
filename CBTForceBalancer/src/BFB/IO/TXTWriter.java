/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package BFB.IO;

import filehandlers.Media;
import Force.Force;
import Force.Group;
import Force.Scenario;
import Force.Unit;
import list.*;
import components.Mech;
import utilities.CostBVBreakdown;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

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

        FR.write( CSVFormat("unit_type") );
        FR.write( CSVFormat("sub_unit_type") );
        FR.write( CSVFormat("unit_name") );
        FR.write( CSVFormat("model_number") );
        FR.write( CSVFormat("tonnage") );
        FR.write( CSVFormat("bv2") );
        FR.write( CSVFormat("tw rules_level") );
        FR.write( CSVFormat("technology base") );
        FR.write( CSVFormat("source") );
        FR.write( CSVFormat("date") );
        FR.write( CSVFormat("era") );
        FR.write( "PV,Wt,MV,S,M,L,E,OV,Armor,Internal,Special Abilities" );
        FR.newLine();

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
            FR.write( CSVFormat(data.getBV()+"") );
            FR.write( CSVFormat(data.getLevel()) );
            FR.write( CSVFormat(data.getTech()) );
            FR.write( CSVFormat(data.getSource()) );
            FR.write( CSVFormat(data.getYear()+"") );
            FR.write( CSVFormat(data.getEra()) );
            FR.write( data.getBattleForceStats().SerializeCSV( false ) );
            FR.newLine();
        }
        FR.close();
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
            FR.write(mech.getBattleForceStats().SerializeCSV( true ));
            FR.newLine();
        }

        FR.close();

        if ( !message.isEmpty() ) {
            Media.Messager("Could not write out the following:\n" + message);
        }
    }

    public void WriteFactorList(String filename, Scenario scenario ) throws IOException {
        if ( !filename.endsWith(".csv") ) { filename += ".csv"; }
        BufferedWriter FR = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );

        String message = "";
        FR.write("Unit,Type,Prb,ECM,Spd,Jmp,TSM,Phys,Armr,TC,8+,10+,Hd Cap,Tot Dmg,Base BV, Adj BV");
        FR.newLine();
        for ( Force f : scenario.getForces() ) {
            if ( !f.ForceName.isEmpty()) {FR.write(f.ForceName);}
            for ( Group g : f.Groups ) {
                FR.newLine();
                for ( Unit u : g.getUnits() ) {
                    FR.write(u.SerializeFactors());
                    FR.newLine();
                }
            }
            FR.write(f.SerializeFactors());
            FR.newLine();
            FR.newLine();
        }

        FR.close();

        if ( !message.isEmpty() ) {
            Media.Messager("Could not write out the following:\n" + message);
        }
    }
    
    public String CSVFormat( String data ) {
        if ( data.contains(",") )
            return "\"" + data + "\",";
        else
            return data + ",";
    }
}
