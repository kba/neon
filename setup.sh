#!/bin/bash

TEXGEN_FILE="JTexGen-1.1.zip"
TEXGEN_URL="https://kenai.com/downloads/jtexgen/JTexGen-1.1.zip"


cd dist
if [[ ! -e $TEXGEN_FILE ]];then
    curl $TEXGEN_URL > $TEXGEN_FILE
fi
unzip $TEXGEN_FILE
cd JTexGen-1.1/
mvn install
