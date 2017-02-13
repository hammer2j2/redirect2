#!/bin/bash

# a wrapper shell script for the Redirect/RedirectCheckFile class

CLASSPATH=target/redirect-1.0-SNAPSHOT-jar-with-dependencies.jar 
CMD=java
CLASS=com.slamgarden.webtest.Redirect/RedirectCheckFile
RESPLIST=200,301,302,503

usage_fn() {
    echo
    echo "Usage: $0 [-c CLASSPATH] [-r RESPLIST] [-f|F] -p <prefix> -i <testfile>"

    cat<<EOF

        where 
        -c CLASSPATH - the full path to the redirect jar file
            default: $CLASSPATH
        -r RESPLIST - a comma separated list of HTTP response codes allowed
            default: $RESPLIST
        -f|F - follow redirects (-f) (default), or do not follow redirects (-F)
        -p prefix - the website prefixed with http:// to test against 
        -i testfile - the file to read test cases from
        -l url - if just want to test one url
        -h - help

        Example:

        $0 -c $CLASSPATH -r 200,304 -p http://origin-myweb-www.foodev.com -i data/test.dat
        
EOF

    exit;
}

cmdargs=" -f"  # start adding from right to left

while getopts "c:i:p:r:l:fh" opt; do
    case $opt in
        c ) CLASSPATH=$CLASSPATH:"$OPTARG";;
        r ) RESPLIST="$OPTARG";;
        h ) usage_fn;;
        F ) cmdargs="";;  # start adding from right to left
        i ) testfile="$OPTARG";;
        p ) prefix="$OPTARG";;
        l ) url="$OPTARG";;
        \?) echo "Invalid option: $OPTARG" >&2
            usage_fn;;
        : ) echo "Option, $OPTARG, requires an argument." >&2
            usage_fn;;
    esac
done

if [ $# -eq 0 ];then
  usage_fn
fi

if [ ! -z "$url" ]; then # just test one url
    CLASS=com.slamgarden.webtest.Redirect/RedirectCheckOne
    cmdargs="-cp $CLASSPATH $CLASS -l $url -r $RESPLIST $cmdargs"
elif [ -z "$prefix" ]; then
    echo "no value set for prefix"
    usage_fn
elif [ ! -f $testfile ]; then
    echo "testfile, $testfile, not found"
    usage_fn
else
    cmdargs="-cp $CLASSPATH $CLASS -i $testfile -p $prefix -r $RESPLIST $cmdargs"
fi



echo

$CMD $cmdargs
