/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource.metadata;

import net.minecraft.resource.ResourceType;

static class PackResourceMetadata.1 {
    static final /* synthetic */ int[] field_61158;

    static {
        field_61158 = new int[ResourceType.values().length];
        try {
            PackResourceMetadata.1.field_61158[ResourceType.CLIENT_RESOURCES.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PackResourceMetadata.1.field_61158[ResourceType.SERVER_DATA.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
