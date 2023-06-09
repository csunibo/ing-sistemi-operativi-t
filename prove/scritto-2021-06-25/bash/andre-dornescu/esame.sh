# Autore:              Andreea Dornescu
# Anno accademico:     2022/23


#!/bin/bash
#file invoker

#controllo argomenti
if test $# -ne 4
then echo Errore nel numero di parametri
        exit -1
fi

if [[ ! -d "$1" ]]; then
    echo Non è una cartella!
    exit -2
fi

if [[ ! -f "$4" ]]; then
    echo Non è un file!
    exit -3
fi

if [[ "$4" != /* ]]; then  
        echo $4 non è un percorso assoluto valido.
        exit 4
fi

if [[ "$3" != .??? ]]; then
    echo Formato stringa sbagliato
    exit -2
fi

#Salvo gli argomenti in variabili
dirin=$1
string=$2
ext=$3
fout=$4

#gestisco $0
case "$0" in
    # il file comandi è stato invocato con un / Path assoluto.
    /*) 
    dir_name=`dirname $0`
    recursive_command="$dir_name/ricorsione"
    ;;
    */*)
    # il file comandi è stato invocato con un path relativo.
    dir_name=`dirname $0`
    recursive_command="`pwd`/$dir_name/ricorsione"
    ;;
    *)
    #Path né assoluto né relativo, il comando è nel $PATH
    recursive_command=ricorsione
    ;;
esac

#innesco la ricorsione 
"$recursive_command" "$dirin" "$string" "$ext" "$fout"