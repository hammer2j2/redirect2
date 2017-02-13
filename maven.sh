  mvn help:describe -Dplugin=help -Dfull
  echo "press enter for next command";
  read x 
  mvn help:describe -Dplugin=compiler -Dmojo=compile -Dfull
