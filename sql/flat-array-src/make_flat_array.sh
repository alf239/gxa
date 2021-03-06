#!/bin/bash
# a pretty fast exporter from Oracle to tab-delimited files (much faster than sqlplus)
#�thanks to http://asktom.oracle.com/pls/asktom/f?p=100:11:0::::P11_QUESTION_ID:459020243348

# you will need $ORACLE_HOME/bin in the $PATH, with Pro*C and respective Oracle headers and
#�client libraries installed, as well as gcc.

proc ./flat_array.pc MODE=ORACLE DEFINE=__x86_64__
gcc -o flat_array ./flat_array.c -L $ORACLE_HOME/lib -lclntsh
