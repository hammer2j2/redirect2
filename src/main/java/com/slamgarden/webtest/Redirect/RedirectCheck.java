package com.slamgarden.webtest.Redirect;

import org.apache.commons.codec.binary.Base64;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

import java.net.*;
import java.io.*;

public class RedirectCheck {

    public void RedirectCheck() throws Exception {};

    boolean httpPrepend = false;
    boolean followRedirects = false;
    boolean useBasicAuth = false;
    boolean setCustomHeaders = false;
    boolean debug = false;
    String httpPrependStr = "";
    String customHeaderStr = "";
    int[] responseList = new int[100];

    BufferedReader br = null;
    String Sep = "\\s+";
    String url = null;
    String SourceURL = null;
    String TargetURL = null;
    String baencoded = null;

    public void fileOpen(String Filename ) {
        try {
 
	    br = new BufferedReader(new FileReader(Filename));
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }

    public boolean isflp(String line) throws Exception {
        if(this.debug) System.out.println("INFO: In isflp()");
        if(line.length() > 1 ) {
            String[] flpURL = line.split("\\?flp=");
            if (flpURL.length == 2) return(true);
            if(this.debug) System.out.println("INFO: In isflp() No flp found");
        }
    return(false);
    }

    public String flp(String line) throws Exception {
        if(this.debug) System.out.println("INFO: In flp()");
        String[] flpURL = line.split("\\?flp=");
        line = flpURL[1];
        return(this.httpPrependStr + "/us/en_us/" + line);
    }

    public boolean getRule() throws Exception {
        String line = "";
        boolean needline = true; 

        while( needline ) {
            if( (line = br.readLine()) == null)  return(false);
            if ( line.length() <= 0  ) continue;
            if (line.matches("^\\s*#.*")) continue;

            needline=false;
 
            String[] URL = line.split(Sep);
            SourceURL = URL[0];

            if(URL.length < 2 ) {
                TargetURL = "";
            } 
            else {
                TargetURL = URL[1];
            }    
            if ( this.httpPrepend == true ) {
	        // unless this already has the protocol://domain 
                if ( ! SourceURL.matches("http(s|)://.*") ) {  
                    SourceURL= this.httpPrependStr + SourceURL; 
                }
	        // unless this already has the protocol://domain 
                if ( ! TargetURL.matches("http(s|)://.*") && URL.length > 1 ) {  
                    TargetURL= this.httpPrependStr + TargetURL; 
                }
            }
        }
        return true;
    }

    public boolean checkRule(String mySourceURL) throws Exception {

        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

        // switch the values if provided an override
        if(mySourceURL == null )  mySourceURL = SourceURL;

        URL web = new URL(mySourceURL);
        String newUrl = "";
        if(this.debug) System.out.println("INFO Checking new Source "
                +mySourceURL + ";");

        HttpURLConnection  webconn = (HttpURLConnection) web.openConnection();
        if ( this.useBasicAuth ) {
	    webconn.setRequestProperty("Authorization", "Basic "+ baencoded);
        }
        webconn.setInstanceFollowRedirects(this.followRedirects);
        //if modifying headers, have to hit origin, so match uri_path not domain
        // set a flag somewhere for this
        // wrap this in something pretty
        webconn.setRequestProperty("Host","jumpman23.com");

        int rCode = webconn.getResponseCode();

        if ( rCode == 302 || rCode == 301 ) {
            newUrl = webconn.getHeaderField("Location");
	    if ( followRedirects ) {  // this will happen on protocol redirects
                if(this.debug) System.out.println("WARNING Could not follow redirect;");
                //SourceURL = newUrl;
                return(checkRule(newUrl));
            }
        }
        else {  // not a redirect, but..
            newUrl = webconn.toString();
            newUrl = newUrl.split(":")[1] + ":" + newUrl.split(":")[2];
	    if ( followRedirects ) {
                if ( isflp(newUrl) ) {  // fake transform and recurse
                    if(this.debug) System.out.println("INFO handling ?flp;");
                    return(checkRule(flp(newUrl)));
                }
            }
        }

        if(this.debug) System.out.println("Response Message: "+webconn.getHeaderFields());

	    // check that the response code and final url meet the input rule

        for (int i = 0; i < this.responseList.length && this.responseList[i] != 0; i++) {
            if(this.debug) System.out.println("Checking responseList "+this.responseList[i]);
            if ( rCode == this.responseList[i] ) { // found response code

                if( (rCode == 301 || rCode == 302) ) {
                    System.out.println(SourceURL + " PASS on " + newUrl + " with Code " +rCode);
                    return(true);
                }
        
                if(TargetURL.length() < 1 || newUrl.equals(TargetURL) 
                        || newUrl.equals("sun.net.www.protocol.http.HttpURLConnection:"
                                +TargetURL) ) {
                    System.out.println(SourceURL + " PASS on " + TargetURL + " with Code " +rCode);
                    return(true);
                }
                else { 
                    System.out.println(SourceURL + " FAIL target URL: " 
                            + newUrl + " found; expected " + TargetURL);
                    return(false);
                }
            }
        }
	    // should not get here unless no response code matched

        System.out.println(SourceURL + " FAIL Response Code " + rCode );
        return(false);
    }
}
