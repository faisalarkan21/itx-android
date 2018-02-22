package com.itx.android.android_itx.Utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by faisal on 2/22/18.
 */

public class RupiahCurrency {

    Locale locale = null;
    NumberFormat rupiahFormat = null;

    public String toRupiahFormat(double nominal) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(nominal);
    }

}
