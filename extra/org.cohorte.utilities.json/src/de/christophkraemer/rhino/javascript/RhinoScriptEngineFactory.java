/*
 * Copyright (c) 2005, 2006, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package de.christophkraemer.rhino.javascript;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.script.ScriptEngine;

import de.christophkraemer.rhino.util.ScriptEngineFactoryBase;

public class RhinoScriptEngineFactory extends ScriptEngineFactoryBase {

	private static List<String> extensions;

	private static List<String> mimeTypes;

	private static List<String> names;

	static {
		names = new ArrayList<>(6);
		names.add("js");
		names.add("rhino");
		// MOD_OG
		names.add("rhino178");
		names.add("JavaScript");
		names.add("javascript");
		names.add("ECMAScript");
		names.add("ecmascript");
		names = Collections.unmodifiableList(names);

		mimeTypes = new ArrayList<>(4);
		mimeTypes.add("application/javascript");
		mimeTypes.add("application/ecmascript");
		mimeTypes.add("text/javascript");
		mimeTypes.add("text/ecmascript");
		mimeTypes = Collections.unmodifiableList(mimeTypes);

		extensions = new ArrayList<>(1);
		extensions.add("js");
		extensions = Collections.unmodifiableList(extensions);
	}

	public RhinoScriptEngineFactory() {
	}

	@Override
	public List<String> getExtensions() {
		return extensions;
	}

	@Override
	public String getMethodCallSyntax(String obj, String method, String... args) {

		String ret = obj + "." + method + "(";
		int len = args.length;
		if (len == 0) {
			ret += ")";
			return ret;
		}

		for (int i = 0; i < len; i++) {
			ret += args[i];
			if (i != len - 1) {
				ret += ",";
			} else {
				ret += ")";
			}
		}
		return ret;
	}

	@Override
	public List<String> getMimeTypes() {
		return mimeTypes;
	}

	@Override
	public List<String> getNames() {
		return names;
	}

	@Override
	public String getOutputStatement(String toDisplay) {
		StringBuffer buf = new StringBuffer();
		int len = toDisplay.length();
		buf.append("print(\"");
		for (int i = 0; i < len; i++) {
			char ch = toDisplay.charAt(i);
			switch (ch) {
			case '"':
				buf.append("\\\"");
				break;
			case '\\':
				buf.append("\\\\");
				break;
			default:
				buf.append(ch);
				break;
			}
		}
		buf.append("\")");
		return buf.toString();
	}

	/**
	 * MOD_OG
	 * 
	 * https://github.com/mozilla/rhino/releases/tag/Rhino1_7_8_Release
	 * 
	 * 
	 * @see javax.script.ScriptEngineFactory#getParameter(java.lang.String)
	 */
	@Override
	public Object getParameter(String key) {
		if (key.equals(ScriptEngine.NAME)) {
			return "javascript";
		} else if (key.equals(ScriptEngine.ENGINE)) {
			return "Mozilla Rhino";
		} else if (key.equals(ScriptEngine.ENGINE_VERSION)) {
			return "1.7.8 2017 08 24";
		} else if (key.equals(ScriptEngine.LANGUAGE)) {
			return "ECMAScript";
		} else if (key.equals(ScriptEngine.LANGUAGE_VERSION)) {
			return "1.8";
		} else if (key.equals("THREADING")) {
			return "MULTITHREADED";
		} else {
			throw new IllegalArgumentException("Invalid key");
		}
	}

	@Override
	public String getProgram(String... statements) {
		int len = statements.length;
		String ret = "";
		for (int i = 0; i < len; i++) {
			ret += statements[i] + ";";
		}

		return ret;
	}

	@Override
	public ScriptEngine getScriptEngine() {
		RhinoScriptEngine ret = new RhinoScriptEngine();
		ret.setEngineFactory(this);
		return ret;
	}
}