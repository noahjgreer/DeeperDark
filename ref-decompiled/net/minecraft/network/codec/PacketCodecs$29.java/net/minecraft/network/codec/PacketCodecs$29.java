/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.codec;

import net.minecraft.registry.entry.RegistryEntry;

static class PacketCodecs.29 {
    static final /* synthetic */ int[] field_60515;

    static {
        field_60515 = new int[RegistryEntry.Type.values().length];
        try {
            PacketCodecs.29.field_60515[RegistryEntry.Type.REFERENCE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PacketCodecs.29.field_60515[RegistryEntry.Type.DIRECT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
