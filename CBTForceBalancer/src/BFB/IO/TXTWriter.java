/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package BFB.IO;

import BFB.Force;
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
}
