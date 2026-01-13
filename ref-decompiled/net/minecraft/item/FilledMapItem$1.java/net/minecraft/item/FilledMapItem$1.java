/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.component.type.MapPostProcessingComponent;

static class FilledMapItem.1 {
    static final /* synthetic */ int[] field_49271;

    static {
        field_49271 = new int[MapPostProcessingComponent.values().length];
        try {
            FilledMapItem.1.field_49271[MapPostProcessingComponent.LOCK.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            FilledMapItem.1.field_49271[MapPostProcessingComponent.SCALE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
