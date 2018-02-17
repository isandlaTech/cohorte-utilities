/**
 * Script "test.js"
 * 
 * Available globals (see IXJsConstants) :
 * - VAR_FORMATTER_ID    = "gFormatter";
 * - VAR_SCRIPTCTX_ID    = "gScriptCtx";
 * - VAR_SCRIPTID_ID     = "gScriptId";
 * - VAR_SCRIPTRUN_ID    = "gScriptRun";
 * - VAR_SCRIPTSOURCE_ID = "gScriptSource";
 * - VAR_SCRIPTTS_ID     = "gScriptTS";
 * - VAR_TRACER_ID       = "gTracer";
 * 
 */

importPackage(java.lang);

gTracer.trace("---------- Script [test.js] BEGIN ---");

//------------------------------------------------------------------------------------------

gTracer.trace("Test acces to an Engine attribute begin");

gTracer.trace(gFormatter.format("gTestEngineScope=[%s]",gTestEngineScope));

gTracer.trace("Test acces to an Engine attribute end\n");

//------------------------------------------------------------------------------------------

// to test if an importPackage could be in at on other place than the begining 
importPackage(java.util);

//------------------------------------------------------------------------------------------

gTracer.trace("Test include begin");

// not a comment !
#include "./lib-scripts/test-inclusion.js"

gTracer.trace(gFormatter.format("100*100=[%s]",doMultiply(100,100)));

gTracer.trace("Test include end\n");

//------------------------------------------------------------------------------------------

var wNbMinutes = Math.floor((Math.random() * 30) + 10);
gTracer.trace(gFormatter.format("NbMinutes=[%s]",wNbMinutes));
var wSleepDuration = wNbMinutes * 60 * 1000;
gTracer.trace(gFormatter.format("SleepDuration=[%s]",new Double(wSleepDuration).intValue()));

//------------------------------------------------------------------------------------------

gTracer.trace("Test sleep begin : 500 ms");
Thread.sleep(new Double(500).intValue());
gTracer.trace("Test sleep end\n");

//------------------------------------------------------------------------------------------

gTracer.trace("Test Exception 1 begin");

try{
	// throw an exception and catch it for test 1
	var wDouble =  new Double( 4 * 2,1);
	gTracer.trace(gFormatter.format("wResult=[%s]",wDouble))
}catch(e){
	gTracer.trace("CATCHED ERROR: "+ e);
}

gTracer.trace("Test Exception 1 end\n");

//------------------------------------------------------------------------------------------

gTracer.trace("Test Exception 2 begin");

// throw an exception for test 2
var wDouble =  new Double(4 * 2,1);

gTracer.trace("Test Exception 2 end\n");

//------------------------------------------------------------------------------------------

gTracer.trace("---------- Script [test.js] END ---");

// eof