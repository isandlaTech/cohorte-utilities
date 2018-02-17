/**
 * Script "test1.js"
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

gTracer.trace("---------- Script [test1.js] BEGIN ---");

//------------------------------------------------------------------------------------------

gTracer.trace("Test acces to an Engine attribute begin");

gTracer.trace(gFormatter.format("gTestEngineScope=[%s]",gTestEngineScope));

gTracer.trace("Test acces to an Engine attribute end\n");

//------------------------------------------------------------------------------------------

// to test if an importPackage could be in at on other place than the begining 
importPackage(java.util);

//------------------------------------------------------------------------------------------

gTracer.trace("Test include begin  (from test1)");

// not a comment ! include from 
#include "./lib-scripts/test-inclusion.js"

gTracer.trace(gFormatter.format("Multply 100*100=[%s]",doMultiply(100,100)));

//not a comment ! include from 
#include "./test2.js"


gTracer.trace("Test include end (from test1)");

//------------------------------------------------------------------------------------------

gTracer.trace("Test dump source infos : \n" + gScriptSource.toDescription());

//------------------------------------------------------------------------------------------

gTracer.trace("---------- Script [test1.js] END ---");

// eof