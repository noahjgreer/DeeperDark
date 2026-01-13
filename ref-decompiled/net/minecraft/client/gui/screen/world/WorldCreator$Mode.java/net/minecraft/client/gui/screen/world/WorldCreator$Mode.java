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
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public static final class WorldCreator.Mode
extends Enum<WorldCreator.Mode> {
    public static final /* enum */ WorldCreator.Mode SURVIVAL = new WorldCreator.Mode("survival", GameMode.SURVIVAL);
    public static final /* enum */ WorldCreator.Mode HARDCORE = new WorldCreator.Mode("hardcore", GameMode.SURVIVAL);
    public static final /* enum */ WorldCreator.Mode CREATIVE = new WorldCreator.Mode("creative", GameMode.CREATIVE);
    public static final /* enum */ WorldCreator.Mode DEBUG = new WorldCreator.Mode("spectator", GameMode.SPECTATOR);
    public final GameMode defaultGameMode;
    public final Text name;
    private final Text info;
    private static final /* synthetic */ WorldCreator.Mode[] field_20630;

    public static WorldCreator.Mode[] values() {
        return (WorldCreator.Mode[])field_20630.clone();
    }

    public static WorldCreator.Mode valueOf(String string) {
        return Enum.valueOf(WorldCreator.Mode.class, string);
    }

    private WorldCreator.Mode(String name, GameMode defaultGameMode) {
        this.defaultGameMode = defaultGameMode;
        this.name = Text.translatable("selectWorld.gameMode." + name);
        this.info = Text.translatable("selectWorld.gameMode." + name + ".info");
    }

    public Text getInfo() {
        return this.info;
    }

    private static /* synthetic */ WorldCreator.Mode[] method_36891() {
        return new WorldCreator.Mode[]{SURVIVAL, HARDCORE, CREATIVE, DEBUG};
    }

    static {
        field_20630 = WorldCreator.Mode.method_36891();
    }
}
