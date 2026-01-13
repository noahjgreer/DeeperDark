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
public static final class LevelLoadingScreen.WorldEntryReason
extends Enum<LevelLoadingScreen.WorldEntryReason> {
    public static final /* enum */ LevelLoadingScreen.WorldEntryReason NETHER_PORTAL = new LevelLoadingScreen.WorldEntryReason();
    public static final /* enum */ LevelLoadingScreen.WorldEntryReason END_PORTAL = new LevelLoadingScreen.WorldEntryReason();
    public static final /* enum */ LevelLoadingScreen.WorldEntryReason OTHER = new LevelLoadingScreen.WorldEntryReason();
    private static final /* synthetic */ LevelLoadingScreen.WorldEntryReason[] field_51490;

    public static LevelLoadingScreen.WorldEntryReason[] values() {
        return (LevelLoadingScreen.WorldEntryReason[])field_51490.clone();
    }

    public static LevelLoadingScreen.WorldEntryReason valueOf(String string) {
        return Enum.valueOf(LevelLoadingScreen.WorldEntryReason.class, string);
    }

    private static /* synthetic */ LevelLoadingScreen.WorldEntryReason[] method_59839() {
        return new LevelLoadingScreen.WorldEntryReason[]{NETHER_PORTAL, END_PORTAL, OTHER};
    }

    static {
        field_51490 = LevelLoadingScreen.WorldEntryReason.method_59839();
    }
}
