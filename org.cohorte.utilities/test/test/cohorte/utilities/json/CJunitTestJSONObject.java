package test.cohorte.utilities.json;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.psem2m.utilities.CXException;
import org.psem2m.utilities.json.CDL;
import org.psem2m.utilities.json.Cookie;
import org.psem2m.utilities.json.CookieList;
import org.psem2m.utilities.json.HTTP;
import org.psem2m.utilities.json.JSONArray;
import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.json.JSONString;
import org.psem2m.utilities.json.JSONStringer;
import org.psem2m.utilities.json.JSONTokener;
import org.psem2m.utilities.json.XML;
import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * Test class. This file is not formally a member of the org.json library. It is
 * just a casual test tool.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CJunitTestJSONObject {

	/**
	 * Obj is a typical class that implements JSONString. It also provides some
	 * beanie methods that can be used to construct a JSONObject. It also
	 * demonstrates constructing a JSONObject with an array of names.
	 */
	class Obj implements JSONString {
		public boolean aBoolean;
		public double aNumber;
		public String aString;

		public Obj(String string, double n, boolean b) {
			this.aString = string;
			this.aNumber = n;
			this.aBoolean = b;
		}

		public String getBENT() {
			return "All uppercase key";
		}

		public double getNumber() {
			return this.aNumber;
		}

		public String getString() {
			return this.aString;
		}

		public String getX() {
			return "x";
		}

		public boolean isBoolean() {
			return this.aBoolean;
		}

		@Override
		public String toJSONString() {
			return "{" + JSONObject.quote(this.aString) + ":" + JSONObject.doubleToString(this.aNumber) + "}";
		}

		@Override
		public String toString() {
			return this.getString() + " " + this.getNumber() + " " + this.isBoolean() + "." + this.getBENT() + " "
					+ this.getX();
		}
	}

	JSONArray a;

	Iterator<String> it;
	JSONObject j;
	JSONStringer jj;
	IActivityLogger pLogger = CActivityLoggerBasicConsole.getInstance();
	String s;

	public CJunitTestJSONObject() {
		super();
	}

	/**
	 * Entry point.
	 * 
	 * @param args
	 */
	@Test
	public void Test01() throws Exception {

		pLogger.logInfo(this, "Test01", "Begin");
		try {

			Obj obj = new Obj("A beany object", 42, true);

			j = XML.toJSONObject("<![CDATA[This is a collection of test patterns and examples for org.json.]]>  Ignore the stuff past the end.  ");
			pLogger.logInfo(this, "Test01", " %s", j.toString());

			j = new JSONObject(obj);
			pLogger.logInfo(this, "Test01", " %s", j.toString());

			jj = new JSONStringer();
			s = jj.object().key("foo").value("bar").key("baz").array().object().key("quux").value("Thanks, Josh!")
					.endObject().endArray().key("obj keys").value(JSONObject.getNames(obj)).endObject().toString();
			pLogger.logInfo(this, "Test01", " %s", s);

			pLogger.logInfo(this, "Test01", " %s",
					new JSONStringer().object().key("a").array().array().array().value("b").endArray().endArray()
							.endArray().endObject().toString());

			jj = new JSONStringer();
			jj.array();
			jj.value(1);
			jj.array();
			jj.value(null);
			jj.array();
			jj.object();
			jj.key("empty-array").array().endArray();
			jj.key("answer").value(42);
			jj.key("null").value(null);
			jj.key("false").value(false);
			jj.key("true").value(true);
			jj.key("big").value(123456789e+88);
			jj.key("small").value(123456789e-88);
			jj.key("empty-object").object().endObject();
			jj.key("long");
			jj.value(9223372036854775807L);
			jj.endObject();
			jj.value("two");
			jj.endArray();
			jj.value(true);
			jj.endArray();
			jj.value(98.6);
			jj.value(-100.0);
			jj.object();
			jj.endObject();
			jj.object();
			jj.key("one");
			jj.value(1.00);
			jj.endObject();
			jj.value(obj);
			jj.endArray();
			pLogger.logInfo(this, "Test01", " %s", jj.toString());

			pLogger.logInfo(this, "Test01", " %s", new JSONArray(jj.toString()).toString(4));

		} catch (Exception e) {
			pLogger.logInfo(this, "Test01", "ERROR: %s", CXException.eCauseMessagesInString(e));
			throw e;
		} finally {
			pLogger.logInfo(this, "Test01", "end");
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void Test03() throws Exception {

		pLogger.logInfo(this, "Test03", "Begin");
		try {
			Obj obj = new Obj("A beany object", 42, true);
			int ar[] = { 1, 2, 3 };
			JSONArray ja = new JSONArray(ar);
			pLogger.logInfo(this, "Test03", " %s", ja.toString());

			String sa[] = { "aString", "aNumber", "aBoolean" };
			j = new JSONObject(obj, sa);
			j.put("Testing JSONString interface", obj);
			pLogger.logInfo(this, "Test03", " %s", j.toString(4));

			j = new JSONObject(
					"{slashes: '///', closetag: '</script>', backslash:'\\\\', ei: {quotes: '\"\\''},eo: {a: '\"quoted\"', b:\"don't\"}, quotes: [\"'\", '\"']}");
			pLogger.logInfo(this, "Test03", " %s", j.toString(2));
			pLogger.logInfo(this, "Test03", " %s", XML.toString(j));
			pLogger.logInfo(this, "Test03", " %s", "");

			j = new JSONObject(
					"/*comment*/{foo: [true, false,9876543210,    0.0, 1.00000001,  1.000000000001, 1.00000000000000001,"
							+ " .00000000000000001, 2.00, 0.1, 2e100, -32,[],{}, \"string\"], "
							+ "  to   : null, op : 'Good'," + "ten:10} postfix comment");
			j.put("String", "98.6");
			j.put("JSONObject", new JSONObject());
			j.put("JSONArray", new JSONArray());
			j.put("int", 57);
			j.put("double", 123456789012345678901234567890.);
			j.put("true", true);
			j.put("false", false);
			j.put("null", JSONObject.NULL);
			j.put("bool", "true");
			j.put("zero", -0.0);
			j.put("\\u2028", "\u2028");
			j.put("\\u2029", "\u2029");
			a = j.getJSONArray("foo");
			a.put(666);
			a.put(2001.99);
			a.put("so \"fine\".");
			a.put("so <fine>.");
			a.put(true);
			a.put(false);
			a.put(new JSONArray());
			a.put(new JSONObject());
			j.put("keys", JSONObject.getNames(j));
			pLogger.logInfo(this, "Test03", " %s", j.toString(4));
			pLogger.logInfo(this, "Test03", " %s", XML.toString(j));

			pLogger.logInfo(this, "Test03", " %s", "String: " + j.getDouble("String"));
			pLogger.logInfo(this, "Test03", " %s", "  bool: " + j.getBoolean("bool"));
			pLogger.logInfo(this, "Test03", " %s", "    to: " + j.getString("to"));
			pLogger.logInfo(this, "Test03", " %s", "  true: " + j.getString("true"));
			pLogger.logInfo(this, "Test03", " %s", "   foo: " + j.getJSONArray("foo"));
			pLogger.logInfo(this, "Test03", " %s", "    op: " + j.getString("op"));
			pLogger.logInfo(this, "Test03", " %s", "   ten: " + j.getInt("ten"));
			pLogger.logInfo(this, "Test03", " %s", "  oops: " + j.optBoolean("oops"));

			j = XML.toJSONObject("<xml one = 1 two=' \"2\" '><five></five>First \u0009&lt;content&gt;<five></five> This is \"content\". <three>  3  </three>JSON does not preserve the sequencing of elements and contents.<three>  III  </three>  <three>  T H R E E</three><four/>Content text is an implied structure in XML. <six content=\"6\"/>JSON does not have implied structure:<seven>7</seven>everything is explicit.<![CDATA[CDATA blocks<are><supported>!]]></xml>");
			pLogger.logInfo(this, "Test03", " %s", j.toString(2));
			pLogger.logInfo(this, "Test03", " %s", XML.toString(j));
			pLogger.logInfo(this, "Test03", " %s", "");

			j = XML.toJSONObject("<mapping><empty/>   <class name = \"Customer\">      <field name = \"ID\" type = \"string\">         <bind-xml name=\"ID\" node=\"attribute\"/>      </field>      <field name = \"FirstName\" type = \"FirstName\"/>      <field name = \"MI\" type = \"MI\"/>      <field name = \"LastName\" type = \"LastName\"/>   </class>   <class name = \"FirstName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"MI\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"LastName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class></mapping>");

			pLogger.logInfo(this, "Test03", " %s", j.toString(2));
			pLogger.logInfo(this, "Test03", " %s", XML.toString(j));
			pLogger.logInfo(this, "Test03", " %s", "");

			j = XML.toJSONObject("<?xml version=\"1.0\" ?><Book Author=\"Anonymous\"><Title>Sample Book</Title><Chapter id=\"1\">This is chapter 1. It is not very long or interesting.</Chapter><Chapter id=\"2\">This is chapter 2. Although it is longer than chapter 1, it is not any more interesting.</Chapter></Book>");
			pLogger.logInfo(this, "Test03", " %s", j.toString(2));
			pLogger.logInfo(this, "Test03", " %s", XML.toString(j));
			pLogger.logInfo(this, "Test03", " %s", "");

			j = XML.toJSONObject("<!DOCTYPE bCard 'http://www.cs.caltech.edu/~adam/schemas/bCard'><bCard><?xml default bCard        firstname = ''        lastname  = '' company   = '' email = '' homepage  = ''?><bCard        firstname = 'Rohit'        lastname  = 'Khare'        company   = 'MCI'        email     = 'khare@mci.net'        homepage  = 'http://pest.w3.org/'/><bCard        firstname = 'Adam'        lastname  = 'Rifkin'        company   = 'Caltech Infospheres Project'        email     = 'adam@cs.caltech.edu'        homepage  = 'http://www.cs.caltech.edu/~adam/'/></bCard>");
			pLogger.logInfo(this, "Test03", " %s", j.toString(2));
			pLogger.logInfo(this, "Test03", " %s", XML.toString(j));
			pLogger.logInfo(this, "Test03", " %s", "");

			j = XML.toJSONObject("<?xml version=\"1.0\"?><customer>    <firstName>        <text>Fred</text>    </firstName>    <ID>fbs0001</ID>    <lastName> <text>Scerbo</text>    </lastName>    <MI>        <text>B</text>    </MI></customer>");
			pLogger.logInfo(this, "Test03", " %s", j.toString(2));
			pLogger.logInfo(this, "Test03", " %s", XML.toString(j));
			pLogger.logInfo(this, "Test03", " %s", "");

			j = XML.toJSONObject("<!ENTITY tp-address PUBLIC '-//ABC University::Special Collections Library//TEXT (titlepage: name and address)//EN' 'tpspcoll.sgm'><list type='simple'><head>Repository Address </head><item>Special Collections Library</item><item>ABC University</item><item>Main Library, 40 Circle Drive</item><item>Ourtown, Pennsylvania</item><item>17654 USA</item></list>");
			pLogger.logInfo(this, "Test03", " %s", j.toString());
			pLogger.logInfo(this, "Test03", " %s", XML.toString(j));
			pLogger.logInfo(this, "Test03", " %s", "");

			j = XML.toJSONObject("<test intertag status=ok><empty/>deluxe<blip sweet=true>&amp;&quot;toot&quot;&toot;&#x41;</blip><x>eks</x><w>bonus</w><w>bonus2</w></test>");
			pLogger.logInfo(this, "Test03", " %s", j.toString(2));
			pLogger.logInfo(this, "Test03", " %s", XML.toString(j));
			pLogger.logInfo(this, "Test03", " %s", "");

			j = HTTP.toJSONObject("GET / HTTP/1.0\nAccept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\nAccept-Language: en-us\nUser-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\nHost: www.nokko.com\nConnection: keep-alive\nAccept-encoding: gzip, deflate\n");
			pLogger.logInfo(this, "Test03", " %s", j.toString(2));
			pLogger.logInfo(this, "Test03", " %s", HTTP.toString(j));
			pLogger.logInfo(this, "Test03", " %s", "");

			j = HTTP.toJSONObject("HTTP/1.1 200 Oki Doki\nDate: Sun, 26 May 2002 17:38:52 GMT\nServer: Apache/1.3.23 (Unix) mod_perl/1.26\nKeep-Alive: timeout=15, max=100\nConnection: Keep-Alive\nTransfer-Encoding: chunked\nContent-Type: text/html\n");
			pLogger.logInfo(this, "Test03", " %s", j.toString(2));
			pLogger.logInfo(this, "Test03", " %s", HTTP.toString(j));
			pLogger.logInfo(this, "Test03", " %s", "");

			j = new JSONObject(
					"{nix: null, nux: false, null: 'null', 'Request-URI': '/', Method: 'GET', 'HTTP-Version': 'HTTP/1.0'}");
			pLogger.logInfo(this, "Test03", " %s", j.toString(2));
			pLogger.logInfo(this, "Test03", " %s", "isNull: " + j.isNull("nix"));
			pLogger.logInfo(this, "Test03", " %s", "   has: " + j.has("nix"));
			pLogger.logInfo(this, "Test03", " %s", XML.toString(j));
			pLogger.logInfo(this, "Test03", " %s", HTTP.toString(j));
			pLogger.logInfo(this, "Test03", " %s", "");

			j = XML.toJSONObject("<?xml version='1.0' encoding='UTF-8'?>" + "\n\n" + "<SOAP-ENV:Envelope"
					+ " xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\""
					+ " xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\""
					+ " xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">" + "<SOAP-ENV:Body><ns1:doGoogleSearch"
					+ " xmlns:ns1=\"urn:GoogleSearch\""
					+ " SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
					+ "<key xsi:type=\"xsd:string\">GOOGLEKEY</key> <q"
					+ " xsi:type=\"xsd:string\">'+search+'</q> <start" + " xsi:type=\"xsd:int\">0</start> <maxResults"
					+ " xsi:type=\"xsd:int\">10</maxResults> <filter"
					+ " xsi:type=\"xsd:boolean\">true</filter> <restrict"
					+ " xsi:type=\"xsd:string\"></restrict> <safeSearch"
					+ " xsi:type=\"xsd:boolean\">false</safeSearch> <lr" + " xsi:type=\"xsd:string\"></lr> <ie"
					+ " xsi:type=\"xsd:string\">latin1</ie> <oe" + " xsi:type=\"xsd:string\">latin1</oe>"
					+ "</ns1:doGoogleSearch>" + "</SOAP-ENV:Body></SOAP-ENV:Envelope>");
			pLogger.logInfo(this, "Test03", " %s", j.toString(2));
			pLogger.logInfo(this, "Test03", " %s", XML.toString(j));
			pLogger.logInfo(this, "Test03", " %s", "");

			j = new JSONObject(
					"{Envelope: {Body: {\"ns1:doGoogleSearch\": {oe: \"latin1\", filter: true, q: \"'+search+'\", key: \"GOOGLEKEY\", maxResults: 10, \"SOAP-ENV:encodingStyle\": \"http://schemas.xmlsoap.org/soap/encoding/\", start: 0, ie: \"latin1\", safeSearch:false, \"xmlns:ns1\": \"urn:GoogleSearch\"}}}}");
			pLogger.logInfo(this, "Test03", " %s", j.toString(2));
			pLogger.logInfo(this, "Test03", " %s", XML.toString(j));
			pLogger.logInfo(this, "Test03", " %s", "");

			j = CookieList.toJSONObject("  f%oo = b+l=ah  ; o;n%40e = t.wo ");
			pLogger.logInfo(this, "Test03", " %s", j.toString(2));
			pLogger.logInfo(this, "Test03", " %s", CookieList.toString(j));
			pLogger.logInfo(this, "Test03", " %s", "");

			j = Cookie.toJSONObject("f%oo=blah; secure ;expires = April 24, 2002");
			pLogger.logInfo(this, "Test03", " %s", j.toString(2));
			pLogger.logInfo(this, "Test03", " %s", Cookie.toString(j));
			pLogger.logInfo(this, "Test03", " %s", "");

			j = new JSONObject(
					"{script: 'It is not allowed in HTML to send a close script tag in a string<script>because it confuses browsers</script>so we insert a backslash before the /'}");
			pLogger.logInfo(this, "Test03", " %s", j.toString());
			pLogger.logInfo(this, "Test03", " %s", "");

			JSONTokener jt = new JSONTokener("{op:'test', to:'session', pre:1}{op:'test', to:'session', pre:2}");
			j = new JSONObject(jt);
			pLogger.logInfo(this, "Test03", " %s", j.toString());
			pLogger.logInfo(this, "Test03", " %s", "pre: " + j.optInt("pre"));
			int i = jt.skipTo('{');
			pLogger.logInfo(this, "Test03", " %s", i);
			j = new JSONObject(jt);
			pLogger.logInfo(this, "Test03", " %s", j.toString());
			pLogger.logInfo(this, "Test03", " %s", "");

			a = CDL.toJSONArray("No quotes, 'Single Quotes', \"Double Quotes\"\n1,'2',\"3\"\n,'It is \"good,\"', \"It works.\"\n\n");

			pLogger.logInfo(this, "Test03", " %s", CDL.toString(a));
			pLogger.logInfo(this, "Test03", " %s", "");
			pLogger.logInfo(this, "Test03", " %s", a.toString(4));
			pLogger.logInfo(this, "Test03", " %s", "");

			a = new JSONArray(" [\"<escape>\", next is an implied null , , ok,] ");
			pLogger.logInfo(this, "Test03", " %s", a.toString());
			pLogger.logInfo(this, "Test03", " %s", "");
			pLogger.logInfo(this, "Test03", " %s", XML.toString(a));
			pLogger.logInfo(this, "Test03", " %s", "");

			j = new JSONObject(
					"{ fun => with non-standard forms ; forgiving => This package can be used to parse formats that are similar to but not stricting conforming to JSON; why=To make it easier to migrate existing data to JSON,one = [[1.00]]; uno=[[{1=>1}]];'+':+6e66 ;pluses=+++;empty = '' , 'double':0.666,true: TRUE, false: FALSE, null=NULL;[true] = [[!,@;*]]; string=>  o. k. ; # comment\r oct=0666; hex=0x666; dec=666; o=0999; noh=0x0x}");
			pLogger.logInfo(this, "Test03", " %s", j.toString(4));
			pLogger.logInfo(this, "Test03", " %s", "");
			if (j.getBoolean("true") && !j.getBoolean("false")) {
				pLogger.logInfo(this, "Test03", " %s", "It's all good");
			}

			pLogger.logInfo(this, "Test03", " %s", "");
			j = new JSONObject(j, new String[] { "dec", "oct", "hex", "missing" });
			pLogger.logInfo(this, "Test03", " %s", j.toString(4));

			pLogger.logInfo(this, "Test03", " %s", "");
			pLogger.logInfo(this, "Test03", " %s", new JSONStringer().array().value(a).value(j).endArray());

			j = new JSONObject(
					"{string: \"98.6\", long: 2147483648, int: 2147483647, longer: 9223372036854775807, double: 9223372036854775808}");
			pLogger.logInfo(this, "Test03", " %s", j.toString(4));

			pLogger.logInfo(this, "Test03", " %s", "\ngetInt");
			pLogger.logInfo(this, "Test03", " %s", "int    " + j.getInt("int"));
			pLogger.logInfo(this, "Test03", " %s", "long   " + j.getInt("long"));
			pLogger.logInfo(this, "Test03", " %s", "longer " + j.getInt("longer"));
			pLogger.logInfo(this, "Test03", " %s", "double " + j.getInt("double"));
			pLogger.logInfo(this, "Test03", " %s", "string " + j.getInt("string"));

			pLogger.logInfo(this, "Test03", " %s", "\ngetLong");
			pLogger.logInfo(this, "Test03", " %s", "int    " + j.getLong("int"));
			pLogger.logInfo(this, "Test03", " %s", "long   " + j.getLong("long"));
			pLogger.logInfo(this, "Test03", " %s", "longer " + j.getLong("longer"));
			pLogger.logInfo(this, "Test03", " %s", "double " + j.getLong("double"));
			pLogger.logInfo(this, "Test03", " %s", "string " + j.getLong("string"));

			pLogger.logInfo(this, "Test03", " %s", "\ngetDouble");
			pLogger.logInfo(this, "Test03", " %s", "int    " + j.getDouble("int"));
			pLogger.logInfo(this, "Test03", " %s", "long   " + j.getDouble("long"));
			pLogger.logInfo(this, "Test03", " %s", "longer " + j.getDouble("longer"));
			pLogger.logInfo(this, "Test03", " %s", "double " + j.getDouble("double"));
			pLogger.logInfo(this, "Test03", " %s", "string " + j.getDouble("string"));

			j.put("good sized", 9223372036854775807L);
			pLogger.logInfo(this, "Test03", " %s", j.toString(4));

			a = new JSONArray("[2147483647, 2147483648, 9223372036854775807, 9223372036854775808]");
			pLogger.logInfo(this, "Test03", " %s", a.toString(4));

			pLogger.logInfo(this, "Test03", " %s", "\nKeys: ");
			it = j.keys();
			while (it.hasNext()) {
				s = it.next();
				pLogger.logInfo(this, "Test03", " %s", s + ": " + j.getString(s));
			}

			pLogger.logInfo(this, "Test03", " %s", "\naccumulate: ");
			j = new JSONObject();
			j.accumulate("stooge", "Curly");
			j.accumulate("stooge", "Larry");
			j.accumulate("stooge", "Moe");
			a = j.getJSONArray("stooge");
			a.put(5, "Shemp");
			pLogger.logInfo(this, "Test03", " %s", j.toString(4));

			pLogger.logInfo(this, "Test03", " %s", "\nwrite:");
			pLogger.logInfo(this, "Test03", " %s", j.write(new StringWriter()));

			s = "<xml empty><a></a><a>1</a><a>22</a><a>333</a></xml>";
			j = XML.toJSONObject(s);
			pLogger.logInfo(this, "Test03", " %s", j.toString(4));
			pLogger.logInfo(this, "Test03", " %s", XML.toString(j));

			s = "<book><chapter>Content of the first chapter</chapter><chapter>Content of the second chapter      <chapter>Content of the first subchapter</chapter>      <chapter>Content of the second subchapter</chapter></chapter><chapter>Third Chapter</chapter></book>";
			j = XML.toJSONObject(s);
			pLogger.logInfo(this, "Test03", " %s", j.toString(4));
			pLogger.logInfo(this, "Test03", " %s", XML.toString(j));

			Collection<Object> c = null;
			Map<String, Object> m = null;

			j = new JSONObject(m);
			a = new JSONArray(c);
			j.append("stooge", "Joe DeRita");
			j.append("stooge", "Shemp");
			j.accumulate("stooges", "Curly");
			j.accumulate("stooges", "Larry");
			j.accumulate("stooges", "Moe");
			j.accumulate("stoogearray", j.get("stooges"));
			j.put("map", m);
			j.put("collection", c);
			j.put("array", a);
			a.put(m);
			a.put(c);
			pLogger.logInfo(this, "Test03", " %s", j.toString(4));

			pLogger.logInfo(this, "Test03", " %s", "\nTesting Exceptions: ");

		} catch (Exception e) {
			pLogger.logInfo(this, "Test03", "ERROR: %s", CXException.eCauseMessagesInString(e));
			throw e;
		} finally {
			pLogger.logInfo(this, "Test03", "end");
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void Test04() throws Exception {

		pLogger.logInfo(this, "Test04", "Begin");
		try {

			try {
				pLogger.logInfo(this, "Test04", " %s", j.getDouble("stooge"));
			} catch (Exception e) {
				pLogger.logSevere(this, "Test04", "EXPECTED ERROR %s", CXException.eCauseMessagesInString(e));
			}

			try {
				pLogger.logInfo(this, "Test04", " %s", j.getDouble("howard"));
			} catch (Exception e) {
				pLogger.logSevere(this, "Test04", "EXPECTED ERROR %s", CXException.eCauseMessagesInString(e));
			}

			try {
				pLogger.logInfo(this, "Test04", " %s", j.put(null, "howard"));
			} catch (Exception e) {
				pLogger.logSevere(this, "Test04", "EXPECTED ERROR %s", CXException.eCauseMessagesInString(e));
			}

			try {
				pLogger.logInfo(this, "Test04", " %s", a.getDouble(0));
			} catch (Exception e) {
				pLogger.logSevere(this, "Test04", "EXPECTED ERROR %s", CXException.eCauseMessagesInString(e));
			}

			try {
				pLogger.logInfo(this, "Test04", " %s", a.get(-1));
			} catch (Exception e) {
				pLogger.logSevere(this, "Test04", "EXPECTED ERROR %s", CXException.eCauseMessagesInString(e));
			}

			try {
				pLogger.logInfo(this, "Test04", " %s", a.put(Double.NaN));
			} catch (Exception e) {
				pLogger.logSevere(this, "Test04", "EXPECTED ERROR %s", CXException.eCauseMessagesInString(e));
			}

			try {
				j = XML.toJSONObject("<a><b>    ");
			} catch (Exception e) {
				pLogger.logSevere(this, "Test04", "EXPECTED ERROR %s", CXException.eCauseMessagesInString(e));
			}

			try {
				j = XML.toJSONObject("<a></b>    ");
			} catch (Exception e) {
				pLogger.logSevere(this, "Test04", "EXPECTED ERROR %s", CXException.eCauseMessagesInString(e));
			}

			try {
				j = XML.toJSONObject("<a></a    ");
			} catch (Exception e) {
				pLogger.logSevere(this, "Test04", "EXPECTED ERROR %s", CXException.eCauseMessagesInString(e));
			}

			try {
				JSONArray ja = new JSONArray(new Object());
				pLogger.logInfo(this, "Test04", " %s", ja.toString());
			} catch (Exception e) {
				pLogger.logSevere(this, "Test04", "EXPECTED ERROR %s", CXException.eCauseMessagesInString(e));
			}

		} catch (Exception e) {
			pLogger.logSevere(this, "Test04", "ERROR: %s", CXException.eCauseMessagesInString(e));
			throw e;
		} finally {
			pLogger.logInfo(this, "Test04", "end");
		}
	}
}
