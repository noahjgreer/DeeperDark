/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import net.minecraft.resource.ResourceType;

static class PackVersion.1 {
    static final /* synthetic */ int[] field_61152;

    static {
        field_61152 = new int[ResourceType.values().length];
        try {
            PackVersion.1.field_61152[ResourceType.CLIENT_RESOURCES.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PackVersion.1.field_61152[ResourceType.SERVER_DATA.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
