/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Ticker
 */
package net.minecraft.util;

import com.google.common.base.Ticker;

class Util.1
extends Ticker {
    Util.1() {
    }

    public long read() {
        return nanoTimeSupplier.getAsLong();
    }
}
