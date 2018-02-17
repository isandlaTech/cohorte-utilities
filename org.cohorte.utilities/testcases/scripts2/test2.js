/**
 * Script "test2.js"
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

gTracer.trace("---------- Script [test2.js] BEGIN ---");

//------------------------------------------------------------------------------------------

gTracer.trace("Test include begin (from test2)");

// not a comment ! include from 
#include "./lib-scripts/test-inclusion1.js"

gTracer.trace(gFormatter.format("Add 100+100=[%s]",doAddition(100,100)));

gTracer.trace("Test include end (from test2)");

//------------------------------------------------------------------------------------------
gTracer.trace("---------- Script [test2.js] END ---");

//eof
