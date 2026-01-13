/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource.metadata;

import net.minecraft.resource.ResourceType;

static class PackOverlaysMetadata.1 {
    static final /* synthetic */ int[] field_61147;

    static {
        field_61147 = new int[ResourceType.values().length];
        try {
            PackOverlaysMetadata.1.field_61147[ResourceType.CLIENT_RESOURCES.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PackOverlaysMetadata.1.field_61147[ResourceType.SERVER_DATA.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
