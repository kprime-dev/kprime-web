# Build And Run

## Requirements 
 
- Java 11 already installed.

 We suggest to use <a href="https://sdkman.io/">SDKMAN</a>.

## DOWNALOAD, UNZIP AND RUN (for linux)

1 - Download latest relase from:

    https://bitbucket.org/npedot/kprime-webapp/downloads/

e.g. kprime-beta6.zip

2 - Choose your kprime folder and
set the root of file configs, it MUST BE  an exixstent writeable absolute path dir complete with ending / e.g.:

    export KPRIME_HOME=/home/nipe/kprime/

    cd $KPRIME_HOME

unzip into your prefered app folder

    unzip

3 - run

    sh $KPRIME_HOME/bin/app

open your browser at

    http://localhost:7000/


## TO BUILD A DOCKER IMAGE

    sudo docker build -t kprime-b6_0 . 

## TO RUN WITH DOCKER

    sudo docker run -it -v ~/Workspaces/semint-kprime-cases/:/cases -p 7000:7000 --rm kprime-b6_0

then you could use a kprime working dir as:

    /cases/sakila/
    
