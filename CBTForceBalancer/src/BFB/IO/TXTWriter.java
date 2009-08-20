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
import ssw.filehandlers.MechList;
import ssw.filehandlers.MechListData;

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
        if ( !filename.endsWith(".txt") ) { filename += ".txt"; }
        BufferedWriter FR = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );

        for (int i=0; i < list.Size(); i++) {
            MechListData data = (MechListData) list.Get(i);
            FR.write( data.getFullName() + CommonTools.tab );
            FR.write( data.getBV() + CommonTools.tab );
            FR.write( data.getCost() + CommonTools.tab );
            FR.write( data.getEra() + CommonTools.tab );
            FR.write( data.getSource() + CommonTools.tab );
            FR.newLine();
        }
    }
}
