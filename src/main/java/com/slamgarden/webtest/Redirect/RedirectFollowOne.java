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

public class RedirectFollowOne {

// input is two url strings: source and target 

    public static void main(String[] args) throws Exception {
    String URLSource = args[0];
    String URLTarget = args[1];
    String inputLine;

     URL web = new URL(URLSource);
        HttpURLConnection  webconn = (HttpURLConnection) web.openConnection();
        webconn.setInstanceFollowRedirects(true);

        int rCode = webconn.getResponseCode();
        String newUrl = webconn.getHeaderField("Location");

        // System.out.println("Response Message: "+webconn.getHeaderFields());
	    if(rCode == 503 || rCode == 200 ) {
          if(newUrl.equals(URLTarget)) {
          System.out.println(URLSource + " Redirects_to " + newUrl);
          }
          else {
          	System.out.println(URLSource + " FAILS Redirects_to " + newUrl + " expected " + URLTarget);
          }
        }
        else {
          System.out.println(URLSource + " FAILS:NoRedirect");
        }
    }
}
