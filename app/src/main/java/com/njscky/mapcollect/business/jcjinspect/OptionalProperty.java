package com.njscky.mapcollect.business.jcjinspect;

import android.text.TextUtils;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class OptionalProperty extends Property {
    public String[] options;

    public int[] textOptionIndex;

    public SparseArray<String> optionIndexWithValue;

    public OptionalProperty(@NonNull String[] options) {
        super();
        this.options = options;

    }

    public OptionalProperty(String name, String value, @NonNull String[] options) {
        this(name, value, options, null);
    }

    public OptionalProperty(String name, String value, @NonNull String[] options, int[] textOptionIndex) {
        super(name, value);
        this.options = options;
        this.textOptionIndex = textOptionIndex;
    }

    public int getSelection() {
        for (int i = 0; i < options.length; i++) {
            if (TextUtils.equals(options[i], value)) {
                return i;
            }
        }
        return -1;
    }

    public boolean containsTextOptionIndex(int index) {
        if (index < 0 || textOptionIndex == null) {
            return false;
        }

        int rst = Arrays.binarySearch(textOptionIndex, index);

        return rst != -1;
    }
}
