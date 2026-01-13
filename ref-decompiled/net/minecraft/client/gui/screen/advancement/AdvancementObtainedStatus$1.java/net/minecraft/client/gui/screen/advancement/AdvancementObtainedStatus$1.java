/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementFrame;

@Environment(value=EnvType.CLIENT)
static class AdvancementObtainedStatus.1 {
    static final /* synthetic */ int[] field_45430;

    static {
        field_45430 = new int[AdvancementFrame.values().length];
        try {
            AdvancementObtainedStatus.1.field_45430[AdvancementFrame.TASK.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AdvancementObtainedStatus.1.field_45430[AdvancementFrame.CHALLENGE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AdvancementObtainedStatus.1.field_45430[AdvancementFrame.GOAL.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
