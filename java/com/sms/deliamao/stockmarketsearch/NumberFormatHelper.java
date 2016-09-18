package com.sms.deliamao.stockmarketsearch;

/**
 * Created by wenjieli on 5/4/16.
 */
public class NumberFormatHelper {
    // Return + or - as prefix for number.
    public static String getNumberPrefix(double value) {
        if (value == 0) {
            return "";
        } else if (value > 0) {
            return "+";
        }
        return "-";
    }
    public static String formateDouble(double number, boolean withPrefix, boolean withPercent, boolean withUnit) {
        String prefix = "";
        if (withPrefix) {
            if (number != 0) {
                prefix = number > 0 ? "+" : "-";
            }
            number = Math.abs(number);
        }
        String percent = withPercent ? "%" : "";
        String unit = "";
        if (number > 10e9) {
            number = number / 10e9;
            unit = " Billion";
        } else if (number > 10e6) {
            number = number / 10e6;
            unit = " Million";
        } else if (number > 1000) {
            number = number / 1000;
            unit = " Thousand";
        }
        return prefix + String.format("%.2f", number) + percent + unit;
    }
}
