/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemDisplayContext;

@Environment(value=EnvType.CLIENT)
static class ModelTransformation.1 {
    static final /* synthetic */ int[] field_4313;

    static {
        field_4313 = new int[ItemDisplayContext.values().length];
        try {
            ModelTransformation.1.field_4313[ItemDisplayContext.THIRD_PERSON_LEFT_HAND.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ModelTransformation.1.field_4313[ItemDisplayContext.THIRD_PERSON_RIGHT_HAND.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ModelTransformation.1.field_4313[ItemDisplayContext.FIRST_PERSON_LEFT_HAND.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ModelTransformation.1.field_4313[ItemDisplayContext.FIRST_PERSON_RIGHT_HAND.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ModelTransformation.1.field_4313[ItemDisplayContext.HEAD.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ModelTransformation.1.field_4313[ItemDisplayContext.GUI.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ModelTransformation.1.field_4313[ItemDisplayContext.GROUND.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ModelTransformation.1.field_4313[ItemDisplayContext.FIXED.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ModelTransformation.1.field_4313[ItemDisplayContext.ON_SHELF.ordinal()] = 9;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
