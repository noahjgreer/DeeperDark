/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.CommandBlockBlockEntity;

@Environment(value=EnvType.CLIENT)
static class CommandBlockScreen.1 {
    static final /* synthetic */ int[] field_2875;

    static {
        field_2875 = new int[CommandBlockBlockEntity.Type.values().length];
        try {
            CommandBlockScreen.1.field_2875[CommandBlockBlockEntity.Type.SEQUENCE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CommandBlockScreen.1.field_2875[CommandBlockBlockEntity.Type.AUTO.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CommandBlockScreen.1.field_2875[CommandBlockBlockEntity.Type.REDSTONE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
