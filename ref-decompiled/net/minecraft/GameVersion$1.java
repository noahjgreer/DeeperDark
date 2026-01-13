/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft;

import net.minecraft.resource.ResourceType;

static class GameVersion.1 {
    static final /* synthetic */ int[] field_59620;

    static {
        field_59620 = new int[ResourceType.values().length];
        try {
            GameVersion.1.field_59620[ResourceType.CLIENT_RESOURCES.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GameVersion.1.field_59620[ResourceType.SERVER_DATA.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
