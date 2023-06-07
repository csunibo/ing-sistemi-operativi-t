#!/bin/bash

# Usage: esame fileToSearch S M dir1 … dirN


### CONTROLLO ARGOMENTI

# Controllo numero argomenti
if [[ $# -le 3 ]]; then
    echo "Errore: numero di argomenti non corretto" 
    echo -e "Usage:\n\t$0 fileToSearch S M dir1 … dirN" 
    exit 1
fi

fileToSearch=$1
S=$2
M=$3
 
shift 3

for dir in $@
do
    # Controllo 
    if [[ ! "$dir" = /* ]]
    then
        echo "Errore parametro: $dir deve essere un path assoluto"
        exit 1
    fi

    if ! [[ -d $dir ]]; then
        echo "Errore parametro: $dir non è una directory"
        exit 3
    fi
done




# Controllo che il terzo argomento sia un intero positivo
if [[ $S = *[!0-9]* ]] ; then
    echo "Errore parametro S: $S non è un intero positivo" 1>&2
    exit 1
elif [[ $M = *[!0-9]* ]]; then
    echo "Errore parametro M: $M non è un intero positivo" 1>&2
    exit 1
fi 

### FINE CONTROLLO ARGOMENTI


outfile="$HOME/$$$fileToSearch.log"
> "$outfile"

# In questo caso, se avessi utilizzato test oppure [, 
# che sono comandi esterni, non avrei avuto l'espansione dell'*,
# in quanto l'espansione dei metacaratteri avviene dopo l'esecuzione
# di comandi in bash.
if [[ "$0" = /* ]] ; then
    #Iniziando con /, si tratta di un path assoluto
    #(eg /home/andrea/recurse_dir.sh)

    #Estrazione di parti di path: man dirname oppure man basename
    dir_name=`dirname "$0"`
    recursive_command="$dir_name/do_recurse_dir.sh"
elif [[ "$0" = */* ]] ; then
    # C'è uno slash nel comando, ma non inizia con /. Path relativo
    dir_name=`dirname "$0"`
    recursive_command="`pwd`/$dir_name/do_recurse_dir.sh"
else 
    # Non si tratta ne di un path relativo, ne di uno assoluto.
    # E' un path "secco": il comando sarà dunque cercato
    # nelle cartelle indicate dalla variabile d'ambiente $PATH.
    recursive_command=do_recurse_dir.sh
fi

i=1
while [[ $i -le "$M" ]]
do
    echo "volta: $i"
    # Invoco il comando ricorsivo
    for dir in $@
    do
        "$recursive_command" "$dir" "$fileToSearch" "$outfile"
    done
    echo "Aspetto $S secondi"
    sleep "$S"
    i=`expr $i + 1`
done 


