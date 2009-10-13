/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package BFB;

import BFB.Common.CommonTools;
import java.io.BufferedWriter;
import java.io.IOException;
import org.w3c.dom.Node;

public class Objective extends abWarchestItem implements ifSerializable {
    public Objective( String Description, int Value ) {
        super(Description, Value);
    }

    public Objective() {
        super("", 0);
    }

    public Objective( Node n ) {
        super(n);
    }

    @Override
    public void SerializeXML(BufferedWriter file) throws IOException {
        file.write( CommonTools.Tabs(2) + "<objective value=\"" + getValue() + "\">" + getDescription() + "</objective>" );
        file.newLine();
    }

    @Override
    public String SerializeClipboard() {
        String data = "";

        data += getDescription() + " (Reward:" + getValue() + ")";
        return data;
    }
}
