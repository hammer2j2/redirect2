package  com.slamgarden.webtest.Redirect;

import org.apache.commons.codec.binary.Base64;
import static org.kohsuke.args4j.ExampleMode.ALL;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.BooleanOptionHandler;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;

import java.net.*;
import java.io.*;


public class RedirectCheckFile {

    @Option(name="-h",usage="help")
    private boolean help;

    @Option(name="-d",usage="debug")
    private boolean debug;

    @Option(name="-f",usage="follow redirects")
    private boolean followRedirects;

    @Option(name="-H",usage="custom headers")
    private static String customHeaderStr = "";

    @Option(name="-l",usage="url <http://domain>")
    private static String url ="";

    @Option(name="-r",usage="responseList <301,302,200,..>")
    private static String responseListStr = "";

    @Option(name="-p",usage="prepend <protocol://domain>")
    private static String prepend ="";

    @Option(name="-o",usage="output to this file",metaVar="OUTPUT")
    private File fileout = new File(".");

    @Option(name="-i",usage="input data file")
    private static String filein = "";

    @Option(name="-u",usage="useBasicAuth <username:password>")
    private static String basicAuthStr = "";

    // receives other command line parameters than options
    @Argument
    private List<String> arguments = new ArrayList<String>();


    public static void main(String[] args) throws Exception {

	int result = 0;

    RedirectCheck redirect = new RedirectCheck();

    if(!new RedirectCheckFile().doMain(args, redirect)) System.exit(-1);

     redirect.fileOpen(filein);

     while(redirect.getRule() ) {
        if(!redirect.checkRule(null)) {
		result = 1 ; // just hang on to this. It will stay 1 if ever 1 
        }
    }

    System.exit(result); // 1 is bad. An error was found
    }

    public boolean doMain(String[] args, RedirectCheck redirect) throws IOException {
    CmdLineParser parser = new CmdLineParser(this);
    
    // if you have a wider console, you could increase the value;
    // here 80 is also the default
    parser.setUsageWidth(80);

    try {
        // parse the arguments.
        parser.parseArgument(args);

        // you can parse additional arguments if you want.
        // parser.parseArgument("more","args");

        // after parsing arguments, you should check
        // if enough arguments are given.
        /* if( arguments.isEmpty() )
          throw new CmdLineException(parser,"No argument is given");
        */

    } catch( CmdLineException e ) {
        // if there's a problem in the command line,
        // you'll get this exception. this will report
        // an error message.
        System.err.println(e.getMessage());
        System.err.println("java SampleMain [options...] arguments...");
        // print the list of available options
        parser.printUsage(System.err);
        System.err.println();

        // print option sample. This is useful some time
        System.err.println(" Example: java SampleMain"+parser.printExample(ALL));

        return false;
    }
	if ( help ) { 
        parser.printUsage(System.err);
        System.err.println(" Example: java SampleMain"+parser.printExample(ALL));
        return false;
	}
	if (  (url.length() == 0) && (filein.length() == 0)) { 
	    System.out.println("Must specify one of -u or -f for input.\n");
        parser.printUsage(System.err);
        System.err.println(" Example: java SampleMain"+parser.printExample(ALL));
        return false;
	}

    if( debug ) {
        System.out.println("-d flag is set");
	    redirect.debug=true;
	}

    if( followRedirects ) {
	    redirect.followRedirects=true;
	}

    if( responseListStr.length() > 0 ) {
	    String[] strList = responseListStr.split(",");
	    for( int i=0;i< strList.length;i++) {
	    redirect.responseList[i] = Integer.parseInt(strList[i]);
	    }
	}

    if( prepend.length()>0 ) {
	    redirect.httpPrepend=true;
	    redirect.httpPrependStr=prepend;
    }

    if( basicAuthStr.length()>0 ) {
        redirect.useBasicAuth=true;
	redirect.baencoded = Base64.encodeBase64String(basicAuthStr.getBytes()); 
    }

    if( customHeaderStr.length()>0 ) {
        redirect.setCustomHeaders=true;
        redirect.customHeaderStr = customHeaderStr;
    }

    for( String s : arguments ) {
        System.out.println("other arguments are:");
        System.out.println(s);
	}
	return true;
    }
}
