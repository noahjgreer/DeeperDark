/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.text;

import org.jspecify.annotations.Nullable;

class Style.Writer {
    private boolean shouldAppendComma;
    final /* synthetic */ StringBuilder field_39010;

    Style.Writer() {
        this.field_39010 = stringBuilder;
    }

    private void appendComma() {
        if (this.shouldAppendComma) {
            this.field_39010.append(',');
        }
        this.shouldAppendComma = true;
    }

    void append(String key, @Nullable Boolean value) {
        if (value != null) {
            this.appendComma();
            if (!value.booleanValue()) {
                this.field_39010.append('!');
            }
            this.field_39010.append(key);
        }
    }

    void append(String key, @Nullable Object value) {
        if (value != null) {
            this.appendComma();
            this.field_39010.append(key);
            this.field_39010.append('=');
            this.field_39010.append(value);
        }
    }
}
