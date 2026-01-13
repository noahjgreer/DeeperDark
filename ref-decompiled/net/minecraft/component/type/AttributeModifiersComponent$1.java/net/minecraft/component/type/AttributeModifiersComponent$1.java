/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.component.type;

import net.minecraft.entity.attribute.EntityAttributeModifier;

static class AttributeModifiersComponent.1 {
    static final /* synthetic */ int[] field_49330;

    static {
        field_49330 = new int[EntityAttributeModifier.Operation.values().length];
        try {
            AttributeModifiersComponent.1.field_49330[EntityAttributeModifier.Operation.ADD_VALUE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AttributeModifiersComponent.1.field_49330[EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AttributeModifiersComponent.1.field_49330[EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
