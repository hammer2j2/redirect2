Redirect
========

Testing program for Redirects
  564  git init
  565  git config --global user.name "Clayton Han-Mitchell"
  566  git config --global user.email "hammer2j2@gmail.com"
  567  git credential-osxkeychain
  568  git config --global credential.helper osxkeychain

  571  git clone https://github.com/hammer2j2/redirect.git

  596  git remote add origin https://github.com/hammer2j2/redirect.git
  626  git pull origin master

  633  history | grep git >> Redirect/README.md 
java -cp target/redirect-1.0-SNAPSHOT-jar-with-dependencies.jar Redirect/RedirectCheckFile -i data/web-nonpci.dat -p http://www.yahoo.com -r
200,301,302
