/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import java.net.URI;
import net.minecraft.util.Util;

final class Util.OperatingSystem.2
extends Util.OperatingSystem {
    Util.OperatingSystem.2(String string2) {
    }

    @Override
    protected String[] getURIOpenCommand(URI uri) {
        return new String[]{"open", uri.toString()};
    }
}
