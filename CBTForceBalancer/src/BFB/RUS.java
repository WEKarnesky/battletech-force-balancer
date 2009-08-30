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

import java.util.Random;
import java.util.Vector;
import javax.swing.DefaultListModel;

public class RUS {
    private DefaultListModel DisplayList = new DefaultListModel();
    private Vector Choices = new Vector();
    private DefaultListModel Selection = new DefaultListModel();

    public RUS() {

    }

    public DefaultListModel getDisplay() {
        return DisplayList;
    }

    /**
     * AddItem creates the entries in the RUS object for later random selection
     *
     * @param Name Any data input in the file about the item to be selected
     * @param Value The numeric value assigned to the item,
     *              determines how often it is available to be selected
     */
    public void AddItem( String Line, String Name, int Value ) {
        DisplayList.addElement(Line);
        for (int i=0; i < Value; i++ ) {
            Choices.add(Name);
        }
    }

    public DefaultListModel Add( String Item ) {
        getSelection().addElement(Item);
        return getSelection();
    }

    public void ClearItems() {
        DisplayList = new DefaultListModel();
        Choices.removeAllElements();
    }

    public DefaultListModel Generate( int Selections, int AddOn ) {
        java.util.Random random = new Random();

        for (int i=0; i < Selections; i++ ) {
            int Row = random.nextInt(Choices.size());
            if ( (Row + AddOn) <= Choices.size() ) { Row += AddOn; }
            getSelection().addElement(Choices.get(Row).toString());
        }

        return getSelection();
    }

    public String Generate() {
        java.util.Random random = new Random();
        int Row = random.nextInt(Choices.size());
        return Choices.get(Row).toString();
    }

    public DefaultListModel ClearSelection() {
        Selection = new DefaultListModel();
        return Selection;
    }

    /**
     * @return the Selection
     */
    public DefaultListModel getSelection() {
        return Selection;
    }
}
