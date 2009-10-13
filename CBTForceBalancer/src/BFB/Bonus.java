/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package BFB;

import BFB.Common.CommonTools;
import java.io.BufferedWriter;
import java.io.IOException;
import org.w3c.dom.Node;

public class Bonus extends abWarchestItem implements ifSerializable {
    public Bonus( String Description, int Value ) {
        super(Description, Value);
    }
    
    public Bonus() {
        super("", 0);
    }

    public Bonus( Node n ) {
        super(n);
    }

    @Override
    public void SerializeXML(BufferedWriter file) throws IOException {
        file.write( CommonTools.Tabs(2) + "<bonus value=\"" + getValue() + "\">" + getDescription() + "</bonus>" );
        file.newLine();
    }

    @Override
    public String SerializeClipboard() {
        String data = "";

        data += "+" + getValue() + " " + getDescription();
        return data;
    }
}