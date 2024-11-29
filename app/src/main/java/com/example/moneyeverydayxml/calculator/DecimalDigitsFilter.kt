package com.example.moneyeverydayxml.calculator

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern

class DecimalDigitsInputFilter(digitsAfterDecimal: Int) : InputFilter {

    private val pattern = Pattern.compile("^[0-9]*+((\\.[0-9]{0,$digitsAfterDecimal})?)||(\\.)?$")

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val matcher = pattern.matcher(dest.toString() + source)
        return if (!matcher.matches()) "" else null
    }
}