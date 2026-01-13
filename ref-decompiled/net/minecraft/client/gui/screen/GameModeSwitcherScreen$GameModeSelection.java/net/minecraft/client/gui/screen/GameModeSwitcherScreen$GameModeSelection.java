/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
static final class GameModeSwitcherScreen.GameModeSelection
extends Enum<GameModeSwitcherScreen.GameModeSelection> {
    public static final /* enum */ GameModeSwitcherScreen.GameModeSelection CREATIVE = new GameModeSwitcherScreen.GameModeSelection(Text.translatable("gameMode.creative"), GameMode.CREATIVE, new ItemStack(Blocks.GRASS_BLOCK));
    public static final /* enum */ GameModeSwitcherScreen.GameModeSelection SURVIVAL = new GameModeSwitcherScreen.GameModeSelection(Text.translatable("gameMode.survival"), GameMode.SURVIVAL, new ItemStack(Items.IRON_SWORD));
    public static final /* enum */ GameModeSwitcherScreen.GameModeSelection ADVENTURE = new GameModeSwitcherScreen.GameModeSelection(Text.translatable("gameMode.adventure"), GameMode.ADVENTURE, new ItemStack(Items.MAP));
    public static final /* enum */ GameModeSwitcherScreen.GameModeSelection SPECTATOR = new GameModeSwitcherScreen.GameModeSelection(Text.translatable("gameMode.spectator"), GameMode.SPECTATOR, new ItemStack(Items.ENDER_EYE));
    static final GameModeSwitcherScreen.GameModeSelection[] VALUES;
    private static final int field_32317 = 16;
    private static final int field_32316 = 5;
    final Text text;
    final GameMode gameMode;
    private final ItemStack icon;
    private static final /* synthetic */ GameModeSwitcherScreen.GameModeSelection[] field_24584;

    public static GameModeSwitcherScreen.GameModeSelection[] values() {
        return (GameModeSwitcherScreen.GameModeSelection[])field_24584.clone();
    }

    public static GameModeSwitcherScreen.GameModeSelection valueOf(String string) {
        return Enum.valueOf(GameModeSwitcherScreen.GameModeSelection.class, string);
    }

    private GameModeSwitcherScreen.GameModeSelection(Text text, GameMode gameMode, ItemStack icon) {
        this.text = text;
        this.gameMode = gameMode;
        this.icon = icon;
    }

    void renderIcon(DrawContext context, int x, int y) {
        context.drawItem(this.icon, x, y);
    }

    GameModeSwitcherScreen.GameModeSelection next() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> SURVIVAL;
            case 1 -> ADVENTURE;
            case 2 -> SPECTATOR;
            case 3 -> CREATIVE;
        };
    }

    static GameModeSwitcherScreen.GameModeSelection of(GameMode gameMode) {
        return switch (gameMode) {
            default -> throw new MatchException(null, null);
            case GameMode.SPECTATOR -> SPECTATOR;
            case GameMode.SURVIVAL -> SURVIVAL;
            case GameMode.CREATIVE -> CREATIVE;
            case GameMode.ADVENTURE -> ADVENTURE;
        };
    }

    private static /* synthetic */ GameModeSwitcherScreen.GameModeSelection[] method_36886() {
        return new GameModeSwitcherScreen.GameModeSelection[]{CREATIVE, SURVIVAL, ADVENTURE, SPECTATOR};
    }

    static {
        field_24584 = GameModeSwitcherScreen.GameModeSelection.method_36886();
        VALUES = GameModeSwitcherScreen.GameModeSelection.values();
    }
}
