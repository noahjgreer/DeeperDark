/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Oxidizable;

@Environment(value=EnvType.CLIENT)
static class ChestBlockEntityRenderer.1 {
    static final /* synthetic */ int[] field_62658;

    static {
        field_62658 = new int[Oxidizable.OxidationLevel.values().length];
        try {
            ChestBlockEntityRenderer.1.field_62658[Oxidizable.OxidationLevel.UNAFFECTED.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ChestBlockEntityRenderer.1.field_62658[Oxidizable.OxidationLevel.EXPOSED.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ChestBlockEntityRenderer.1.field_62658[Oxidizable.OxidationLevel.WEATHERED.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ChestBlockEntityRenderer.1.field_62658[Oxidizable.OxidationLevel.OXIDIZED.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
