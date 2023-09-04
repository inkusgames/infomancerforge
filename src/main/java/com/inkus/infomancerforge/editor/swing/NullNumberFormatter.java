package com.inkus.infomancerforge.editor.swing;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.text.NumberFormatter;

public class NullNumberFormatter extends NumberFormatter {
	private static final long serialVersionUID = 1L;

	private NullNumberFormatter(DecimalFormat decimalFormat){
		super(decimalFormat);
		setCommitsOnValidEdit(true);
	}
	
	public Object stringToValue(String text) throws ParseException {
		if ( text.length() == 0 ) { 
			return null;
		}
		return super.stringToValue(text);
	}

	public String valueToString(Object value) throws ParseException {
		if (value == null) {
			return "";
		}
		if (value instanceof Number) {
			return super.valueToString(value);
		}
		return value.toString();
	}    

	public static NullNumberFormatter createDoubleFormatter() {
		DecimalFormat decimalFormat=new DecimalFormat("####.#####################################");
		decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		return new NullNumberFormatter(decimalFormat);
	}
	
	public static NullNumberFormatter createDoubleFormatter(int precision) {
		DecimalFormat decimalFormat=new DecimalFormat("####."+"0".repeat(precision)+"#####################################");
		decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		return new NullNumberFormatter(decimalFormat);
	}

	public static NullNumberFormatter createIntegerFormatter() {
		return new NullNumberFormatter(new DecimalFormat("####"));
	}	
}
