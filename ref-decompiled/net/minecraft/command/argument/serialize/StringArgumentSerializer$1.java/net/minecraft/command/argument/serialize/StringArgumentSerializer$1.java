/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.StringArgumentType$StringType
 */
package net.minecraft.command.argument.serialize;

import com.mojang.brigadier.arguments.StringArgumentType;

static class StringArgumentSerializer.1 {
    static final /* synthetic */ int[] field_10952;

    static {
        field_10952 = new int[StringArgumentType.StringType.values().length];
        try {
            StringArgumentSerializer.1.field_10952[StringArgumentType.StringType.SINGLE_WORD.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StringArgumentSerializer.1.field_10952[StringArgumentType.StringType.QUOTABLE_PHRASE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StringArgumentSerializer.1.field_10952[StringArgumentType.StringType.GREEDY_PHRASE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
