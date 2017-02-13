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

public class RedirectCheckSimple {

    public static void main(String[] args) throws Exception {
    String URLString = args[0];
    String inputLine;

     URL web = new URL(URLString);
        HttpURLConnection  webconn = (HttpURLConnection) web.openConnection();
        webconn.setInstanceFollowRedirects(false);

        int rCode = webconn.getResponseCode();
        String newUrl = webconn.getHeaderField(2);

        // System.out.println("Response Message: "+webconn.getHeaderFields());
	    if(rCode == 302 || rCode == 301 ) {
          System.out.println(URLString + " Redirects_to " + newUrl);
        }
        else {
          System.out.println(URLString + " FAILS NoRedirect");
        }
    }
}

