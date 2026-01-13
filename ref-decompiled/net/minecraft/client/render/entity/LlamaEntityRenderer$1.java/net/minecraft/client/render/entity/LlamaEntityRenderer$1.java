/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.LlamaEntity;

@Environment(value=EnvType.CLIENT)
static class LlamaEntityRenderer.1 {
    static final /* synthetic */ int[] field_41635;

    static {
        field_41635 = new int[LlamaEntity.Variant.values().length];
        try {
            LlamaEntityRenderer.1.field_41635[LlamaEntity.Variant.CREAMY.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LlamaEntityRenderer.1.field_41635[LlamaEntity.Variant.WHITE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LlamaEntityRenderer.1.field_41635[LlamaEntity.Variant.BROWN.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LlamaEntityRenderer.1.field_41635[LlamaEntity.Variant.GRAY.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
