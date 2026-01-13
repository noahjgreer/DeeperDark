/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import java.net.URI;
import net.minecraft.util.Util;

final class Util.OperatingSystem.1
extends Util.OperatingSystem {
    Util.OperatingSystem.1(String string2) {
    }

    @Override
    protected String[] getURIOpenCommand(URI uri) {
        return new String[]{"rundll32", "url.dll,FileProtocolHandler", uri.toString()};
    }
}
