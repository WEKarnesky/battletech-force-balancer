/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package BFB;

import java.io.BufferedWriter;
import java.io.IOException;
import org.w3c.dom.Node;

public class abWarchestItem implements ifSerializable {
    private String Description = "";
    private int Value = 0;

    public abWarchestItem( String Description, int Value ) {
        this.Description = Description;
        this.Value = Value;
    }

    public abWarchestItem( Node n ) {
        this.Description = n.getAttributes().getNamedItem("description").getTextContent().trim();
        this.Value = Integer.parseInt(n.getAttributes().getNamedItem("value").getTextContent().trim());
    }

    public void SerializeXML(BufferedWriter file) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String SerializeClipboard() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String SerializeData() {
        return SerializeClipboard();
    }
    
    public String getDescription() {
        return Description;
    }

    public void setDescription( String description ) {
        this.Description = description;
    }

    public int getValue() {
        return Value;
    }

    public void setValue( int value ) {
        this.Value = value;
    }
}
