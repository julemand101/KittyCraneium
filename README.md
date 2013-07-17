KittyCraneium
=============

This project is developed by the software engineer group s406a on Aalborg
University in Spring 2010.

The /lib directory contains the Java library all compiled programs contains
and is create a simple abstraction layer to the LEGO construction and the
language. A copy of the library is placed as a compressed zip file in
/src/codegen/lib.zip and is the file there is unpacked to every compiled
program.

The /src directory contains the compiler itself, The compiler takes
KittyCraneium code and compile it into Java code there can later be compiled
to a LEGO NXT device running [LeJOS NXJ](http://lejos.sourceforge.net/).

Incoming Container Example
--------------------------
Video: http://www.youtube.com/watch?v=O7vcUuEJUgw

Source code in KittyCraneium:

    AREA storageArea = [E1,D2,D1,C2,C1,B2,B1,A2,A1];
    AREA incomingArea = [E2];
    EVENT CONTAINER c IN incomingArea {
        IF (IS storageArea.isFull() EQUAL TO FALSE) { 
            MOVE c TO storageArea;
            GOTO incomingArea;
        } ELSE {
            system.error('Storage area is full.');
        }
    }

Sorting Container Example
--------------------------
Video: http://www.youtube.com/watch?v=KuLcxa54pTA

Source code in KittyCraneium:

    AREA a1 = [A1,A2]; !Used to store group 1 containers
    AREA a2 = [B1,B2]; !Used to store group 2 containers
    AREA a3 = [C1,C2]; !Used to store group 3 containers
    AREA temp  = [D1,D2,E1,E2];

    EVENT CONTAINER c IN a1 WHERE IS c.getGID() EQUAL TO 3 {
        IF (IS a3.isFull() EQUAL TO FALSE) {
            MOVE c TO a3;   
        } ELSE {
            MOVE c TO temp;
        }
    }
    EVENT CONTAINER c IN a2 WHERE IS c.getGID() EQUAL TO 3 {
        IF(IS a3.isFull() EQUAL TO FALSE) {
            MOVE c TO a3;   
        } ELSE {
            MOVE c TO temp;
        }
    }
    EVENT CONTAINER c IN a1 WHERE IS c.getGID() EQUAL TO 2 {
        IF (IS a2.isFull() EQUAL TO FALSE) {
            MOVE c TO a2;   
        } ELSE {
            MOVE c TO temp;
        }
    }
    EVENT CONTAINER c IN a3 WHERE IS c.getGID() EQUAL TO 2 {
        IF (IS a2.isFull() EQUAL TO FALSE) {
            MOVE c TO a2;   
        } ELSE {
            MOVE c TO temp;
        }
    }
    EVENT CONTAINER c IN a2 WHERE IS c.getGID() EQUAL TO 1 {
        IF (IS a1.isFull() EQUAL TO FALSE) {
            MOVE c TO a1;
        } ELSE {
            MOVE c TO temp;
        }
    }
    EVENT CONTAINER c IN a3 WHERE IS c.getGID() EQUAL TO 1 {
        IF (IS a1.isFull() EQUAL TO FALSE) {
            MOVE c TO a1;   
        } ELSE {
            MOVE c TO temp;
        }
    }
    EVENT CONTAINER c IN temp {
        IF (IS c.getGID() EQUAL TO 1) {
            !a3 only has group 1 containers or is empty.
            MOVE c TO a1;
        } ELSEIF (IS c.getGID() EQUAL TO 2) {
            !a2 only has group 2 containers or is empty.
            MOVE c TO a2;
        } ELSEIF (IS c.getGID() EQUAL TO 3) {
            !a3 only has group 3 containers or is empty.
            MOVE c TO a3;
        }
    }
