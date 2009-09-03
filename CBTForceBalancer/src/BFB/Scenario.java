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

import BFB.Common.CommonTools;
import BFB.Common.Constants;
import java.io.BufferedWriter;
import java.io.IOException;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;

public class Scenario {
    private String Name = "",
                    Notes = "";
    public Force[] Forces = new Force[]{new Force(), new Force()};

    public Scenario() {

    }

    /**
     * @return the Name
     */
    public String getName() {
        return Name;
    }

    /**
     * @param Name the Name to set
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     * @return the Notes
     */
    public String getNotes() {
        return Notes;
    }

    /**
     * @param Notes the Notes to set
     */
    public void setNotes(String Notes) {
        this.Notes = Notes;
    }

    public Force topForce() {
        return Forces[0];
    }

    public Force bottomForce() {
        return Forces[1];
    }

    public void AddListener( TableModelListener listener ) {
        for ( Force force : Forces ) {
            force.addTableModelListener(listener);
        }
    }

    public void setupTable(JTable[] tables) {
        int i = 0;
        for ( Force force : Forces ) {
            force.setupTable(tables[i]);
            i++;
        }

        Forces[0].OpForSize = Forces[1].Units.size();
        Forces[1].OpForSize = Forces[0].Units.size();
    }

    public void SerializeXML(BufferedWriter file) throws IOException {
        file.write( "<scenario>" );
        file.newLine();

        file.write( CommonTools.tab + "<name>" + this.Name + "</name>" );
        file.newLine();

        file.write( CommonTools.tab + "<notes>" + this.Notes + "</notes>" );
        file.newLine();

        file.write( CommonTools.tab + "<forces>" );
        file.newLine();

        for ( Force force : Forces ) {
            force.SerializeXML(file);
        }

        file.write( CommonTools.tab + "</forces>" );
        file.newLine();

        file.write("</scenario>");
    }

    public String SerializeClipboard() {
        String data = "";

        data += this.Name + Constants.NL;
        data += this.Notes + Constants.NL;

        for ( Force force : Forces ) {
            data += force.SerializeClipboard();
        }

        return data;
    }
    
}
