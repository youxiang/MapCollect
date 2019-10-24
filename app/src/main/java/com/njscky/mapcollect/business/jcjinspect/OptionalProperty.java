package com.njscky.mapcollect.business.jcjinspect;

import android.text.TextUtils;

import androidx.annotation.NonNull;

public class OptionalProperty extends Property {
    public String[] options;

    public int textOptionIndex;

    public OptionalProperty(@NonNull String[] options) {
        super();
        this.options = options;

    }

    public OptionalProperty(String name, String value, @NonNull String[] options) {
        this(name, value, options, -1);
    }

    public OptionalProperty(String name, String value, @NonNull String[] options, int textOptionIndex) {
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
}
