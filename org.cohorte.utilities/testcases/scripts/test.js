
/*
 * 
 * 
 */

importPackage(java.lang);

TRACER.trace("---------- Script [test.js] BEGIN ---");

//------------------------------------------------------------------------------------------

TRACER.trace("Test acces to an Engine attribute begin");

TRACER.trace(FORMATER.format("ENGSCOP=[%s]",ENGSCOP));

TRACER.trace("Test acces to an Engine attribute end\n");

//------------------------------------------------------------------------------------------

// to test if an importPackage could be in at on other place than the begining 
importPackage(java.util);

//------------------------------------------------------------------------------------------

TRACER.trace("Test include begin");

// not a comment !
#include "./lib-scripts/test-inclusion.js"

TRACER.trace(FORMATER.format("100*100=[%s]",doMultiply(100,100)));

TRACER.trace("Test include end\n");

//------------------------------------------------------------------------------------------

var wNbMinutes = Math.floor((Math.random() * 30) + 10);
TRACER.trace(FORMATER.format("NbMinutes=[%s]",wNbMinutes));
var wSleepDuration = wNbMinutes * 60 * 1000;
TRACER.trace(FORMATER.format("SleepDuration=[%s]",new Double(wSleepDuration).intValue()));

//------------------------------------------------------------------------------------------

TRACER.trace("Test sleep begin : 500 ms");
Thread.sleep(new Double(500).intValue());
TRACER.trace("Test sleep end\n");

//------------------------------------------------------------------------------------------

TRACER.trace("Test Exception 1 begin");

try{
	// throw an exception and catch it for test 1
	var wDouble =  new Double(4 * 2,1);
	TRACER.trace(FORMATER.format("wResult=[%s]",wDouble))
}catch(e){
	TRACER.trace("CATCHED ERROR: "+ e);
}

TRACER.trace("Test Exception 1 end\n");

//------------------------------------------------------------------------------------------

TRACER.trace("Test Exception 2 begin");

// throw an exception for test 2
var wDouble =  new Double(4 * 2,1);

TRACER.trace("Test Exception 2 end\n");

//------------------------------------------------------------------------------------------



// eof