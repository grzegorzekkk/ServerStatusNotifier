package com.github.grzegorzekkk.serverstatusnotifier.inputfilters

import android.text.InputFilter
import android.text.Spanned

class IntegerRangeInputFilter(val min: Int, val max: Int) : InputFilter {
    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        try {
            // Remove the string out of destination that is to be replaced
            var newVal = dest.toString().substring(0, dstart) + dest.toString().substring(dend, dest.toString().length)
            // Add the new string in
            newVal = newVal.substring(0, dstart) + source.toString() + newVal.substring(dstart, newVal.length)
            //****Add this line (below) to allow Negative values***//
            if (newVal.equals("-", ignoreCase = true) && min < 0) return null
            val input = Integer.parseInt(newVal)
            if (isInRange(min, max, input))
                return null
        } catch (nfe: NumberFormatException) {
            return ""
        }

        return ""
    }

    private fun isInRange(a: Int, b: Int, c: Int): Boolean {
        return if (b > a) c in a..b else c in b..a
    }
}