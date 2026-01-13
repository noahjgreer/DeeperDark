/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class WorldListWidget.WorldListType
extends Enum<WorldListWidget.WorldListType> {
    public static final /* enum */ WorldListWidget.WorldListType SINGLEPLAYER = new WorldListWidget.WorldListType();
    public static final /* enum */ WorldListWidget.WorldListType UPLOAD_WORLD = new WorldListWidget.WorldListType();
    private static final /* synthetic */ WorldListWidget.WorldListType[] field_62203;

    public static WorldListWidget.WorldListType[] values() {
        return (WorldListWidget.WorldListType[])field_62203.clone();
    }

    public static WorldListWidget.WorldListType valueOf(String string) {
        return Enum.valueOf(WorldListWidget.WorldListType.class, string);
    }

    private static /* synthetic */ WorldListWidget.WorldListType[] method_73464() {
        return new WorldListWidget.WorldListType[]{SINGLEPLAYER, UPLOAD_WORLD};
    }

    static {
        field_62203 = WorldListWidget.WorldListType.method_73464();
    }
}
