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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
/**
 *
 * @author gblouin
 */
public class XMLRPCClient {
   private String Server;
   private String baseXML = "<?xml version=\"1.0\" encoding =\"UTF-8\"?>\r\n" +
                            "<methodCall>\r\n" +
                            "\t<methodName>{Method}</methodName>\r\n" +
                            "\t<params>\r\n" +
                            "\t\t<param>\r\n" +
                            "\t\t\t<value>\r\n" +
                            "\t\t\t\t{Values}\r\n" +
                            "\t\t\t</value>\r\n" +
                            "\t\t</param>\r\n" +
                            "\t</params>\r\n" +
                            "</methodCall>\r\n";
   private String baseValueXML = "<string>{Param}</string>";

   public XMLRPCClient( String server ) {
        Server = server;
    }

    public Document Send(String MethodName, String[] Args) throws Exception {
        String xml = baseXML.replace("{Method}", MethodName);
        String params = "";
        for (int i=0; i <= Args.length; i++) {
            params += baseValueXML.replace("{Param}", Args[i].toString() ) + "\r\n";
        }
        xml.replace("{Values}", params);

        return MethodCall( xml );
    }
    
    private HttpURLConnection GetConnection() throws Exception {
        try {
            URL u = new URL( Server );
            URLConnection uc = u.openConnection();
            HttpURLConnection connection = (HttpURLConnection) uc;
            connection.setDoOutput( true );
            connection.setDoInput( true );
            connection.setRequestMethod( "POST" );
            return connection;
        } catch( Exception e ) {
            throw e;
        }
    }

    private Document MethodCall( String send ) throws Exception {
        HttpURLConnection connection = null;
        OutputStream out = null;
        OutputStreamWriter wout = null;
        InputStream in = null;
        Document dc = null;
        try {
            connection = GetConnection();
            out = connection.getOutputStream();
            wout = new OutputStreamWriter(out, "UTF-8");
            wout.write( "RPCxml=" + URLEncoder.encode( send, "UTF-8" ) );

            wout.flush();
            out.close();

            in = connection.getInputStream();
            dc = GetXML( in );
            if( dc == null ) {
                if( in != null ) { in.close(); }
                if( out != null ) { out.close(); }
                if( connection != null ) { connection.disconnect(); }
                throw new Exception( "An error occured with the server:\nNo data was returned.\nPlease try the request again later." );
            }

            in.close();
            out.close();
            connection.disconnect();
        } catch( Exception e ) {
            if( in != null ) { in.close(); }
            if( out != null ) { out.close(); }
            if( connection != null ) { connection.disconnect(); }
            throw e;
        }
        return dc;
    }

    private Document GetXML( InputStream is ) throws Exception {
        Document retval;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        retval = db.parse( is );
        if( retval.hasChildNodes() ) {
            return retval;
        } else {
            return null;
        }
    }
}
