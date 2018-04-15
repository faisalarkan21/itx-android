package com.itx.android.android_itx.Utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by faisal on 2/22/18.
 */

public class RupiahCurrency {

    static String unformatedRp = null;
    static String removeDots = null;
    static String removedTwoLastNumber = null;


    public static String toRupiahFormat(double nominal) {
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setMaximumFractionDigits(0);
        kursIndonesia.setDecimalFormatSymbols(formatRp);
        return kursIndonesia.format(nominal) ;

    }

    public static String unformatRupiah(String nominal) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        unformatedRp = nominal.replaceAll("[Rp,]", "");
        removeDots = unformatedRp.replaceAll("\\.", "");
        return removeDots;
    }

}
