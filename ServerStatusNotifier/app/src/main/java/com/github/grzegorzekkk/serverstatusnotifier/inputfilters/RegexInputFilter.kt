package com.github.grzegorzekkk.serverstatusnotifier.inputfilters

import android.text.InputFilter
import android.text.Spanned

class RegexInputFilter(vararg val patterns: Regex) : InputFilter {
    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        // Remove the string out of destination that is to be replaced
        var newVal = dest.toString().substring(0, dstart) + dest.toString().substring(dend, dest.toString().length)
        // Add the new string in
        newVal = newVal.substring(0, dstart) + source.toString() + newVal.substring(dstart, newVal.length)

        for (pattern in patterns) {
            if (!newVal.matches(pattern)) return ""
        }

        return null
    }
}