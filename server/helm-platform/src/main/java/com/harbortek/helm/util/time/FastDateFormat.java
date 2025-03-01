/*
 * Copyright [2025] [Harbortek Corp.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.harbortek.helm.util.time;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FastDateFormat extends Format {

    private static final FormatCache<FastDateFormat> cache = new FormatCache<FastDateFormat>() {
        protected FastDateFormat createInstance(final String pattern, final TimeZone timeZone, final Locale locale) {
            return new FastDateFormat(pattern, timeZone, locale);
        }
    };

    private final FastDatePrinter printer;
    private final FastDateParser parser;

    //-----------------------------------------------------------------------

    public static FastDateFormat getInstance() {
        return cache.getInstance();
    }


    public static FastDateFormat getInstance(final String pattern) {
        return cache.getInstance(pattern, null, null);
    }


    public static FastDateFormat getInstance(final String pattern, final TimeZone timeZone) {
        return cache.getInstance(pattern, timeZone, null);
    }


    public static FastDateFormat getInstance(final String pattern, final Locale locale) {
        return cache.getInstance(pattern, null, locale);
    }


    public static FastDateFormat getInstance(final String pattern, final TimeZone timeZone, final Locale locale) {
        return cache.getInstance(pattern, timeZone, locale);
    }

    //-----------------------------------------------------------------------

    protected FastDateFormat(final String pattern, final TimeZone timeZone, final Locale locale) {
        this(pattern, timeZone, locale, null);
    }

    // Constructor
    //-----------------------------------------------------------------------

    protected FastDateFormat(final String pattern, final TimeZone timeZone, final Locale locale, final Date centuryStart) {
        printer = new FastDatePrinter(pattern, timeZone, locale);
        parser = new FastDateParser(pattern, timeZone, locale, centuryStart);
    }

    // Format methods
    //-----------------------------------------------------------------------

    public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
        return printer.format(obj, toAppendTo, pos);
    }

    public String format(final long millis) {
        return printer.format(millis);
    }

    public String format(final Date date) {
        return printer.format(date);
    }

    public String format(final Calendar calendar) {
        return printer.format(calendar);
    }

    public StringBuffer format(final long millis, final StringBuffer buf) {
        return printer.format(millis, buf);
    }

    public StringBuffer format(final Date date, final StringBuffer buf) {
        return printer.format(date, buf);
    }

    public StringBuffer format(final Calendar calendar, final StringBuffer buf) {
        return printer.format(calendar, buf);
    }

    // Parsing
    //-----------------------------------------------------------------------
    public Date parse(final String source) throws ParseException {
        return parser.parse(source);
    }

    public Date parse(final String source, final ParsePosition pos) {
        return parser.parse(source, pos);
    }

    public Object parseObject(final String source, final ParsePosition pos) {
        return parser.parseObject(source, pos);
    }

}
