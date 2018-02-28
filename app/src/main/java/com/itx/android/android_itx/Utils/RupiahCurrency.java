package com.itx.android.android_itx.Utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by faisal on 2/22/18.
 */

public class RupiahCurrency {

    static String unformated = null;

    public static String toRupiahFormat(double nominal) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(nominal);
    }

    public static String unformatRupiah(String nominal) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        unformated = nominal.replaceAll("[Rp,]", "");
        return unformated;
    }

}
