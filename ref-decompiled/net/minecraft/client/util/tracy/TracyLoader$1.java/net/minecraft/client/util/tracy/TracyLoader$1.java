/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.event.Level
 */
package net.minecraft.client.util.tracy;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.event.Level;

@Environment(value=EnvType.CLIENT)
static class TracyLoader.1 {
    static final /* synthetic */ int[] field_54251;

    static {
        field_54251 = new int[Level.values().length];
        try {
            TracyLoader.1.field_54251[Level.DEBUG.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TracyLoader.1.field_54251[Level.WARN.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TracyLoader.1.field_54251[Level.ERROR.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
