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

import BFB.Common.CommonTools;
import BFB.GUI.frmBase;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import ssw.gui.ImageFilter;
import ssw.gui.ImagePreview;

public class FileSelector {
    MediaTracker Tracker = new MediaTracker(new JLabel());
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    JFileChooser fileChooser = new JFileChooser();
    frmBase Parent = null;

    public void FileSelector(frmBase parent) {
        Parent = parent;
    }

    public File SelectFile(String defaultDirectory, String[] Extensions, String commandName) {
        File tempFile = new File(defaultDirectory);
        for (int i=0; i < Extensions.length; i++) {
            fileChooser.addChoosableFileFilter(new ExtensionFilter(Extensions[i]));
        }
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(tempFile);
        int returnVal = fileChooser.showDialog(null, commandName);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return fileChooser.getSelectedFile();
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

    public File SelectImage(String defaultDirectory, String commandName) {
        File tempFile = new File(defaultDirectory);
        fileChooser.addChoosableFileFilter(new ImageFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(tempFile);
        fileChooser.setAccessory(new ImagePreview(fileChooser));
        int returnVal = fileChooser.showDialog(null, commandName);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return fileChooser.getSelectedFile();
    }

    public Image GetImage(String filename) {
        Image retval = toolkit.getImage( filename );
        Tracker.addImage( retval, 0 );
        try {
            Tracker.waitForID( 0 );
        } catch (InterruptedException ie) {
            // do nothing
        }
        Tracker.removeImage(retval);
        return retval;
    }

    public String GetDirectorySelection( ) {
        return GetDirectorySelection( "" );
    }

    public String GetDirectorySelection( String defaultPath ) {
        String path = defaultPath;

        fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(new File(defaultPath));

        //Show it.
        int returnVal = fileChooser.showDialog( null, "Choose directory");

        //Process the results.  If no file is chosen, the default is used.
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            path = fileChooser.getSelectedFile().getPath();
        } else {
            path = "";
        }
        return path;
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
