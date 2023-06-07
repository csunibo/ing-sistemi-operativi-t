#!/bin/bash

# do_recurse_dir.sh fileToSearch start N outfile


dir=$1
fileToSearch=$2
outfile=$3

echo "Entro in $dir"
cd $dir

for file in *
do
  echo "Sto confrontando $file a $fileToSearch"
  if [[ -d "$file" ]]
  then
    # Ricorsione su directory  
  	"$0" "$file" "$fileToSearch" "$outfile"
  elif [[ -f "$file" ]] && [[ "$file" = "$fileToSearch" ]]
  then
    echo "`date`:: We found  `pwd`/$file" >> "$outfile"
  fi
done

