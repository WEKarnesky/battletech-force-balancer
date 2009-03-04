/*
 * frmMain.java
 *
 * Created on November 21, 2008, 9:56 AM
 */

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

package BFB.IO;

import BFB.GUI.frmMain2;
import java.io.File;
import javax.swing.JFileChooser;

public class FileSelector {
    JFileChooser fileChooser = new JFileChooser();
    frmMain2 Parent = null;

    public void FileSelector(frmMain2 parent) {
        Parent = parent;
    }

    public File SelectFile(String defaultDirectory, String Extension, String commandName) {
        File tempFile = new File(defaultDirectory);
        fileChooser.addChoosableFileFilter(new ExtensionFilter(Extension));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(tempFile);
        int returnVal = fileChooser.showDialog(null, commandName);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return fileChooser.getSelectedFile();
    }

    public File[] SelectFiles(String defaultDirectory, String Extension, String commandName) {
        File[] files = null;
        File tempFile = new File(defaultDirectory);

        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.addChoosableFileFilter(new ExtensionFilter(Extension));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(tempFile);
        int returnVal = fileChooser.showDialog(null, commandName);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            files = fileChooser.getSelectedFiles();
        }
        return files;
    }
    
    private class ExtensionFilter extends javax.swing.filechooser.FileFilter {
        String Extension = "";

        private ExtensionFilter(String Extension) {
            this.Extension = Extension.toLowerCase();
        }

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().endsWith("." + Extension);
        }

        @Override
        public String getDescription() {
            return "*." + Extension;
        }

    }
}
