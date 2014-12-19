/**
 * Copyright (c) 2001-2012 Steve Purcell.
 * Copyright (c) 2002      Vidar Holen.
 * Copyright (c) 2002      Michal Ceresna.
 * Copyright (c) 2005      Ewan Mellor.
 * Copyright (c) 2010-2012 penSec.IT UG (haftungsbeschränkt).
 * Copyright (c) 2014      Oliver Gattaz.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of the copyright holder nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.cohorte.utilities.tests;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.psem2m.utilities.CXStringUtils;

/**
 * Largely GNU-compatible command-line options parser. Has short (-v) and
 * long-form (--verbose) option support, and also allows options with associated
 * values (-d 2, --debug 2, --debug=2). Option processing can be explicitly
 * terminated by the argument '--'.
 * 
 * @author Olivier Gattaz - isandlaTech
 * @author Steve Purcell
 * @author penSec.IT UG (haftungsbeschränkt)
 * 
 * @version 2.0
 * @see com.sanityinc.jargs.examples.OptionTest
 */
public abstract class CAppCommandLineParser implements IAppOptions {

	/**
	 * @author ogattaz
	 * 
	 */
	public static class CastOptionException extends OptionException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7908462558238889167L;
		private final String optionName;
		private final Class<?> pClassToCast;

		/**
		 * @param optionName
		 */
		CastOptionException(String optionName, Class<?> aClassToCast) {
			this(optionName, aClassToCast, String.format(
					"Unable to cast the value of the option '%s'  to '%s'.",
					optionName, aClassToCast.getSimpleName()));
		}

		/**
		 * @param optionName
		 * @param msg
		 */
		CastOptionException(String optionName, Class<?> aClassToCast, String msg) {
			super(msg);
			this.optionName = optionName;
			pClassToCast = aClassToCast;
		}

		/**
		 * @return
		 */
		public Class<?> getClassToCast() {
			return this.pClassToCast;
		}

		/**
		 * @return the name of the option that was unknown (e.g. "-u")
		 */
		public String getOptionName() {
			return this.optionName;
		}
	}

	/**
	 * Thrown when an illegal or missing value is given by the user for an
	 * option that takes a value. <code>getMessage()</code> returns an error
	 * string suitable for reporting the error to the user (in English).
	 * 
	 * No generic class can ever extend <code>java.lang.Throwable</code>, so we
	 * have to return <code>Option&lt;?&gt;</code> instead of
	 * <code>Option&lt;T&gt;</code>.
	 */
	public static class IllegalOptionValueException extends OptionException {

		private static final long serialVersionUID = -1823780596958372812L;

		private final Option<?> option;

		private final String value;

		/**
		 * @param opt
		 * @param value
		 */
		public <T> IllegalOptionValueException(Option<T> opt, String value) {
			super("Illegal value '"
					+ value
					+ "' for option "
					+ (opt.shortForm() != null ? "-" + opt.shortForm() + "/"
							: "") + "--" + opt.longForm());
			this.option = opt;
			this.value = value;
		}

		/**
		 * @return the name of the option whose value was illegal (e.g. "-u")
		 */
		public Option<?> getOption() {
			return this.option;
		}

		/**
		 * @return the illegal value
		 */
		public String getValue() {
			return this.value;
		}
	}

	/**
	 * @author ogattaz
	 * 
	 */
	public static class MandatoryOptionException extends OptionException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7908462558238889167L;
		private final String optionName;

		/**
		 * @param optionName
		 */
		MandatoryOptionException(String optionName) {
			this(optionName, "Mandatory option '" + optionName + "'");
		}

		/**
		 * @param optionName
		 * @param msg
		 */
		MandatoryOptionException(String optionName, String msg) {
			super(msg);
			this.optionName = optionName;
		}

		/**
		 * @return the name of the option that was unknown (e.g. "-u")
		 */
		public String getOptionName() {
			return this.optionName;
		}
	}

	/**
	 * Thrown when the parsed commandline contains multiple concatenated short
	 * options, such as -abcd, where one or more requires a value.
	 * <code>getMessage()</code> returns an english human-readable error string.
	 * 
	 * @author Vidar Holen
	 */
	public static class NotFlagException extends UnknownOptionException {

		private static final long serialVersionUID = -6310272911361077473L;

		private final char notflag;

		/**
		 * @param option
		 * @param unflaggish
		 */
		NotFlagException(String option, char unflaggish) {
			super(option, "Illegal option: '" + option + "', '" + unflaggish
					+ "' requires a value");
			notflag = unflaggish;
		}

		/**
		 * @return the first character which wasn't a boolean (e.g 'c')
		 */
		public char getOptionChar() {
			return notflag;
		}
	}

	/**
	 * Representation of a command-line option
	 * 
	 * @param T
	 *            Type of data configured by this option
	 */
	public static abstract class Option<T> {

		/**
		 * An option that expects a boolean value
		 */
		public static class BooleanOption extends Option<Boolean> {
			public BooleanOption(char shortForm, String longForm) {
				super(shortForm, longForm, false);
			}

			public BooleanOption(String longForm) {
				super(longForm, false);
			}

			@Override
			public Boolean getDefaultValue() {
				return Boolean.TRUE;
			}

			@Override
			public Boolean parseValue(String arg, Locale lcoale) {
				return Boolean.TRUE;
			}
		}

		/**
		 * An option that expects a floating-point value
		 */
		public static class DoubleOption extends Option<Double> {
			public DoubleOption(char shortForm, String longForm) {
				super(shortForm, longForm, true);
			}

			public DoubleOption(String longForm) {
				super(longForm, true);
			}

			@Override
			protected Double parseValue(String arg, Locale locale)
					throws IllegalOptionValueException {
				try {
					NumberFormat format = NumberFormat
							.getNumberInstance(locale);
					Number num = format.parse(arg);
					return new Double(num.doubleValue());
				} catch (ParseException e) {
					throw new IllegalOptionValueException(this, arg);
				}
			}
		}

		/**
		 * An option that expects an integer value
		 */
		public static class IntegerOption extends Option<Integer> {
			public IntegerOption(char shortForm, String longForm) {
				super(shortForm, longForm, true);
			}

			public IntegerOption(String longForm) {
				super(longForm, true);
			}

			@Override
			protected Integer parseValue(String arg, Locale locale)
					throws IllegalOptionValueException {
				try {
					return new Integer(arg);
				} catch (NumberFormatException e) {
					throw new IllegalOptionValueException(this, arg);
				}
			}
		}

		/**
		 * An option that expects a long integer value
		 */
		public static class LongOption extends Option<Long> {
			public LongOption(char shortForm, String longForm) {
				super(shortForm, longForm, true);
			}

			public LongOption(String longForm) {
				super(longForm, true);
			}

			@Override
			protected Long parseValue(String arg, Locale locale)
					throws IllegalOptionValueException {
				try {
					return new Long(arg);
				} catch (NumberFormatException e) {
					throw new IllegalOptionValueException(this, arg);
				}
			}
		}

		/**
		 * An option that expects a string value
		 */
		public static class StringOption extends Option<String> {
			public StringOption(char shortForm, String longForm) {
				super(shortForm, longForm, true);
			}

			public StringOption(String longForm) {
				super(longForm, true);
			}

			@Override
			protected String parseValue(String arg, Locale locale) {
				return arg;
			}
		}

		private final String longForm;

		// ogattaz
		private boolean mandatory = false;

		private final String shortForm;

		private final boolean wantsValue;

		/**
		 * @param shortForm
		 * @param longForm
		 * @param wantsValue
		 */
		protected Option(char shortForm, String longForm, boolean wantsValue) {
			this(new String(new char[] { shortForm }), longForm, wantsValue);
		}

		/**
		 * @param longForm
		 * @param wantsValue
		 */
		protected Option(String longForm, boolean wantsValue) {
			this(null, longForm, wantsValue);
		}

		/**
		 * @param shortForm
		 * @param longForm
		 * @param wantsValue
		 */
		private Option(String shortForm, String longForm, boolean wantsValue) {
			if (longForm == null) {
				throw new IllegalArgumentException("Null longForm not allowed");
			}
			this.shortForm = shortForm;
			this.longForm = longForm;
			this.wantsValue = wantsValue;
		}

		/**
		 * Override to define default value returned by getValue if option does
		 * not want a value
		 */
		protected T getDefaultValue() {
			return null;
		}

		/**
		 * ogattaz
		 * 
		 * @return
		 */
		public String getMandatoryLib(int aLen) {
			return CXStringUtils.strAdjustLeft((isMandatory()) ? "mandatory"
					: "optional", aLen, ' ');
		}

		public final T getValue(String arg, Locale locale)
				throws IllegalOptionValueException {
			if (this.wantsValue) {
				if (arg == null) {
					throw new IllegalOptionValueException(this, "");
				}
				return this.parseValue(arg, locale);
			} else {
				return this.getDefaultValue();
			}
		}

		/**
		 * ogattaz
		 * 
		 * @return
		 */
		public boolean isMandatory() {
			return this.mandatory;
		}

		/**
		 * ogattaz
		 * 
		 * @return
		 */
		public boolean isUsage() {
			return "?".equalsIgnoreCase(shortForm())
					|| "usage".equalsIgnoreCase(longForm())
					|| "help".equalsIgnoreCase(longForm());
		}

		/**
		 * @return
		 */
		public String longForm() {
			return this.longForm;
		}

		/**
		 * ogattaz
		 * 
		 * @return
		 */
		public String longForm(int aLen) {
			return CXStringUtils.strAdjustLeft(longForm(), aLen, ' ');
		}

		/**
		 * @return
		 */
		public String longFormArg() {
			return "--" + longForm();
		}

		/**
		 * Override to extract and convert an option value passed on the
		 * command-line
		 */
		protected T parseValue(String arg, Locale locale)
				throws IllegalOptionValueException {

			return null;
		}

		/**
		 * ogattaz
		 */
		public void setMandatory() {
			this.mandatory = true;
		}

		/**
		 * @return
		 */
		public String shortForm() {
			return this.shortForm;
		}

		/**
		 * ogattaz
		 * 
		 * @return
		 */
		public String shortForm(int aLen) {
			return CXStringUtils.strAdjustLeft(shortForm(), aLen, ' ');
		}

		/**
		 * @return
		 */
		public String shortFormArg() {
			return "-" + shortForm();
		}

		/**
		 * Tells whether or not this option wants a value
		 */
		public boolean wantsValue() {
			return this.wantsValue;
		}
	}

	/**
	 * Base class for exceptions that may be thrown when options are parsed
	 */
	public static abstract class OptionException extends Exception {

		private static final long serialVersionUID = -1395173158411414042L;

		OptionException(String msg) {
			super(msg);
		}
	}

	/**
	 * Thrown when the parsed command-line contains an option that is not
	 * recognised. <code>getMessage()</code> returns an error string suitable
	 * for reporting the error to the user (in English).
	 */
	public static class UnknownOptionException extends OptionException {

		private static final long serialVersionUID = 8787640707334372052L;

		private final String optionName;

		/**
		 * @param optionName
		 */
		UnknownOptionException(String optionName) {
			this(optionName, "Unknown option '" + optionName + "'");
		}

		/**
		 * @param optionName
		 * @param msg
		 */
		public UnknownOptionException(String optionName, String msg) {
			super(msg);
			this.optionName = optionName;
		}

		/**
		 * @return the name of the option that was unknown (e.g. "-u")
		 */
		public String getOptionName() {
			return this.optionName;
		}
	}

	/**
	 * Thrown when the parsed commandline contains multiple concatenated short
	 * options, such as -abcd, where one is unknown. <code>getMessage()</code>
	 * returns an english human-readable error string.
	 * 
	 * @author Vidar Holen
	 */
	public static class UnknownSuboptionException extends
			UnknownOptionException {

		private static final long serialVersionUID = -3845269102125590349L;

		private final char suboption;

		/**
		 * @param option
		 * @param suboption
		 */
		public UnknownSuboptionException(String option, char suboption) {
			super(option, "Illegal option: '" + suboption + "' in '" + option
					+ "'");
			this.suboption = suboption;
		}

		public char getSuboption() {
			return suboption;
		}
	}

	private final Map<String, Option<?>> pOptionsMap = new HashMap<String, Option<?>>(
			10);

	private final List<Option<?>> pOptionsList = new ArrayList<Option<?>>();

	private String[] pRemainingArgs = null;

	private final Map<String, List<?>> pValues = new HashMap<String, List<?>>(
			10);

	/**
	 * Convenience method for adding a boolean option.
	 * 
	 * @return the new Option
	 */
	public final Option<Boolean> addBooleanOption(char shortForm,
			String longForm) {
		return addOption(new Option.BooleanOption(shortForm, longForm));
	}

	/**
	 * Convenience method for adding a boolean option.
	 * 
	 * @return the new Option
	 */
	public final Option<Boolean> addBooleanOption(String longForm) {
		return addOption(new Option.BooleanOption(longForm));
	}

	/**
	 * Convenience method for adding a double option.
	 * 
	 * @return the new Option
	 */
	public final Option<Double> addDoubleOption(char shortForm, String longForm) {
		return addOption(new Option.DoubleOption(shortForm, longForm));
	}

	/**
	 * Convenience method for adding a double option.
	 * 
	 * @return the new Option
	 */
	public final Option<Double> addDoubleOption(String longForm) {
		return addOption(new Option.DoubleOption(longForm));
	}

	/**
	 * Convenience method for adding an integer option.
	 * 
	 * @return the new Option
	 */
	public final Option<Integer> addIntegerOption(char shortForm,
			String longForm) {
		return addOption(new Option.IntegerOption(shortForm, longForm));
	}

	/**
	 * Convenience method for adding an integer option.
	 * 
	 * @return the new Option
	 */
	public final Option<Integer> addIntegerOption(String longForm) {
		return addOption(new Option.IntegerOption(longForm));
	}

	/**
	 * Convenience method for adding a long integer option.
	 * 
	 * @return the new Option
	 */
	public final Option<Long> addLongOption(char shortForm, String longForm) {
		return addOption(new Option.LongOption(shortForm, longForm));
	}

	/**
	 * Convenience method for adding a long integer option.
	 * 
	 * @return the new Option
	 */
	public final Option<Long> addLongOption(String longForm) {
		return addOption(new Option.LongOption(longForm));
	}

	/**
	 * Add the specified Option to the list of accepted options
	 */
	public final <T> Option<T> addOption(Option<T> opt) {
		if (opt.shortForm() != null) {
			this.pOptionsMap.put("-" + opt.shortForm(), opt);
		}
		this.pOptionsMap.put("--" + opt.longForm(), opt);

		pOptionsList.add(opt);

		return opt;
	}

	/**
	 * Convenience method for adding a string option.
	 * 
	 * @return the new Option
	 */
	public final Option<String> addStringOption(char shortForm, String longForm) {
		return addOption(new Option.StringOption(shortForm, longForm));
	}

	/**
	 * Convenience method for adding a string option.
	 * 
	 * @return the new Option
	 */
	public final Option<String> addStringOption(String longForm) {
		return addOption(new Option.StringOption(longForm));
	}

	private <T> void addValue(Option<T> opt, String valueArg, Locale locale)
			throws IllegalOptionValueException {

		T value = opt.getValue(valueArg, locale);
		String lf = opt.longForm();

		/*
		 * Cast is typesafe because the only location we add elements to the
		 * values map is in this method.
		 */
		@SuppressWarnings("unchecked")
		List<T> v = (List<T>) pValues.get(lf);

		if (v == null) {
			v = new ArrayList<T>();
			pValues.put(lf, v);
		}

		v.add(value);
	}

	/**
	 * @param aOptionName
	 * @return
	 * @throws OptionException
	 */
	public boolean getBooleanOptionValue(String aOptionName)
			throws OptionException {
		return getBooleanOptionValue(aOptionName, false);
	}

	/**
	 * @param aOptionName
	 * @param aDefault
	 * @return
	 * @throws OptionException
	 */
	public boolean getBooleanOptionValue(String aOptionName, boolean aDefault)
			throws OptionException {

		aOptionName = validOptionName(aOptionName);
		try {
			@SuppressWarnings("unchecked")
			Option<Boolean> wOption = (Option<Boolean>) this.pOptionsMap
					.get(aOptionName);

			if (wOption == null)
				throw new UnknownOptionException(aOptionName);

			Boolean wBool = getOptionValue(wOption, new Boolean(aDefault));

			return (wBool != null) ? wBool.booleanValue() : aDefault;

		} catch (ClassCastException e) {
			throw new CastOptionException(aOptionName, String.class);
		}
	}

	/**
	 * @param aOptionName
	 * @return
	 * @throws OptionException
	 */
	public int getIntegerOptionValue(String aOptionName) throws OptionException {
		return getIntegerOptionValue(aOptionName, -1);
	}

	/**
	 * @param aOptionName
	 * @param aDefault
	 * @return
	 * @throws OptionException
	 */
	public int getIntegerOptionValue(String aOptionName, int aDefault)
			throws OptionException {

		aOptionName = validOptionName(aOptionName);
		try {
			@SuppressWarnings("unchecked")
			Option<Integer> wOption = (Option<Integer>) this.pOptionsMap
					.get(aOptionName);

			if (wOption == null)
				throw new UnknownOptionException(aOptionName);

			Integer wInteger = getOptionValue(wOption, new Integer(aDefault));

			return (wInteger != null) ? wInteger.intValue() : aDefault;

		} catch (ClassCastException e) {
			throw new CastOptionException(aOptionName, String.class);
		}
	}

	/**
	 * Equivalent to {@link #getOptionValue(Option, Object) getOptionValue(o,
	 * null)}.
	 */
	public final <T> T getOptionValue(Option<T> o) {
		return getOptionValue(o, null);
	}

	/**
	 * @return the parsed value of the given Option, or the given default 'def'
	 *         if the option was not set
	 */
	public final <T> T getOptionValue(Option<T> o, T def) {
		List<?> v = pValues.get(o.longForm());

		if (v == null) {
			return def;
		} else if (v.isEmpty()) {
			return null;
		} else {

			/*
			 * Cast should be safe because Option.parseValue has to return an
			 * instance of type T or null
			 */
			@SuppressWarnings("unchecked")
			T result = (T) v.get(0);
			return result;
		}
	}

	/**
	 * @return A Collection giving the parsed values of all the occurrences of
	 *         the given Option, or an empty Collection if the option was not
	 *         set.
	 */
	@SuppressWarnings("unchecked")
	public final <T> Collection<T> getOptionValues(Option<T> option) {
		Collection<T> result = new ArrayList<T>();

		List<?> wValueList = pValues.get(option.longForm());

		if (wValueList == null || wValueList.isEmpty()) {
			return null;
		}

		for (Object wValue : wValueList) {

			if (wValue != null) {
				result.add((T) wValue);
			}
		}
		return result;
	}

	/**
	 * @return the non-option arguments
	 */
	public final String[] getRemainingArgs() {
		return this.pRemainingArgs;
	}

	/**
	 * @param aOptionName
	 * @return
	 * @throws UnknownOptionException
	 */
	public String getStringOptionValue(String aOptionName)
			throws OptionException {
		return getStringOptionValue(aOptionName, null);
	}

	/**
	 * @param aOptionName
	 * @param aDefault
	 * @return
	 * @throws UnknownOptionException
	 */
	public String getStringOptionValue(String aOptionName, String aDefault)
			throws OptionException {

		aOptionName = validOptionName(aOptionName);
		try {
			@SuppressWarnings("unchecked")
			Option<String> wOption = (Option<String>) this.pOptionsMap
					.get(aOptionName);

			if (wOption == null)
				throw new UnknownOptionException(aOptionName);

			return getOptionValue(wOption, aDefault);

		} catch (ClassCastException e) {
			throw new CastOptionException(aOptionName, String.class);
		}
	}

	/**
	 * ogattaz
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Option<Boolean> getUsageOption() {
		for (Option<?> wOption : this.pOptionsMap.values()) {
			if (wOption.isUsage()) {
				return (Option<Boolean>) wOption;
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	public boolean hasUsageOn() {

		Option<Boolean> wUsageOption = getUsageOption();
		if (wUsageOption != null) {
			Boolean wValue = getOptionValue(wUsageOption);
			if (wValue != null) {
				return wValue.booleanValue();
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * @return
	 */
	public boolean hasValue() {
		return pValues.size() > 0;
	}

	/**
	 * Extract the options and non-option arguments from the given list of
	 * command-line arguments. The default locale is used for parsing options
	 * whose values might be locale-specific.
	 */
	public final void parse(String[] argv) throws OptionException {
		parse(argv, Locale.getDefault());
	}

	/**
	 * Extract the options and non-option arguments from the given list of
	 * command-line arguments. The specified locale is used for parsing options
	 * whose values might be locale-specific.
	 */
	public final void parse(String[] aArgv, Locale locale)
			throws OptionException {

		// reset
		pValues.clear();

		ArrayList<Object> otherArgs = new ArrayList<Object>();
		int wPosition = 0;

		while (wPosition < aArgv.length) {
			String curArg = aArgv[wPosition];
			if (curArg.startsWith("-")) {
				if (curArg.equals("--")) { // end of options
					wPosition += 1;
					break;
				}
				String valueArg = null;
				if (curArg.startsWith("--")) { // handle --arg=value
					int equalsPos = curArg.indexOf("=");
					if (equalsPos != -1) {
						valueArg = curArg.substring(equalsPos + 1);
						curArg = curArg.substring(0, equalsPos);
					}
				} else if (curArg.length() > 2) { // handle -abcd
					for (int i = 1; i < curArg.length(); i++) {
						Option<?> opt = this.pOptionsMap.get("-"
								+ curArg.charAt(i));
						if (opt == null) {
							throw new UnknownSuboptionException(curArg,
									curArg.charAt(i));
						}
						if (opt.wantsValue()) {
							throw new NotFlagException(curArg, curArg.charAt(i));
						}
						addValue(opt, null, locale);
					}
					wPosition++;
					continue;
				}

				Option<?> opt = this.pOptionsMap.get(curArg);
				if (opt == null) {
					throw new UnknownOptionException(curArg);
				}

				if (opt.wantsValue()) {
					if (valueArg == null) {
						wPosition += 1;
						if (wPosition < aArgv.length) {
							valueArg = aArgv[wPosition];
						}
					}
					addValue(opt, valueArg, locale);
				} else {
					addValue(opt, null, locale);
				}

				wPosition += 1;
			} else {
				otherArgs.add(curArg);
				wPosition += 1;
			}
		}

		// ogattaz
		validCmdLine();

		for (; wPosition < aArgv.length; ++wPosition) {
			otherArgs.add(aArgv[wPosition]);
		}

		this.pRemainingArgs = new String[otherArgs.size()];
		pRemainingArgs = otherArgs.toArray(pRemainingArgs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder wSB = new StringBuilder();
		for (Option<?> wOption : pOptionsList) {

			Object wValue = getOptionValue(wOption, null);
			if (wValue != null) {
				wSB.append(String.format(
						"\nname=[%15s]%s Mandatory=[%s] value=[%s]", wOption
								.longFormArg(), CXStringUtils.strAdjustLeft(
								String.format("(%s)", wOption.shortFormArg()),
								5, ' '), CXStringUtils.strAdjustLeft(
								String.valueOf(wOption.isMandatory()), 5, ' '),
						wValue));
			}
		}
		return wSB.toString();
	}

	/**
	 * ogattaz
	 * 
	 * @throws OptionException
	 */
	private void validCmdLine() throws OptionException {

		if (hasUsageOn()) {
			return;
		}

		for (Option<?> wOption : this.pOptionsMap.values()) {

			if (wOption.isMandatory()) {
				String wOptionName = wOption.longForm();

				List<?> wOptValues = pValues.get(wOptionName);
				if (wOptValues == null || wOptValues.size() == 0)
					throw new MandatoryOptionException(wOptionName);
			}
		}
	}

	/**
	 * @param aOptionName
	 * @return
	 * @throws OptionException
	 */
	String validOptionName(String aOptionName) throws OptionException {

		if (aOptionName == null)
			throw new UnknownOptionException("null");
		if (aOptionName.isEmpty())
			throw new UnknownOptionException("empty");

		// eg. "r"
		if (aOptionName.length() == 1)
			return '-' + aOptionName;

		if (aOptionName.length() == 2) {
			// eg. "re"
			if (aOptionName.charAt(0) != '-') {
				return "--" + aOptionName;
			} else {
				// eg. "-r"
				if (aOptionName.charAt(1) != '-') {
					return aOptionName;
				}
				// eg. --
				else {
					throw new UnknownOptionException("null");
				}
			}
		}

		// eg. "req"
		if (aOptionName.charAt(0) != '-') {
			return "--" + aOptionName;
		} else {
			if (aOptionName.charAt(0) != '-')
				return '-' + aOptionName;

		}

		return aOptionName;
	}
}
