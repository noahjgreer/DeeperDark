/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ChangeGameModeC2SPacket;
import net.minecraft.server.command.GameModeCommand;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class GameModeSwitcherScreen
extends Screen {
    static final Identifier SLOT_TEXTURE = Identifier.ofVanilla("gamemode_switcher/slot");
    static final Identifier SELECTION_TEXTURE = Identifier.ofVanilla("gamemode_switcher/selection");
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/gamemode_switcher.png");
    private static final int TEXTURE_WIDTH = 128;
    private static final int TEXTURE_HEIGHT = 128;
    private static final int BUTTON_SIZE = 26;
    private static final int ICON_OFFSET = 5;
    private static final int field_32314 = 31;
    private static final int field_32315 = 5;
    private static final int UI_WIDTH = GameModeSelection.values().length * 31 - 5;
    private final GameModeSelection currentGameMode;
    private GameModeSelection gameMode;
    private int lastMouseX;
    private int lastMouseY;
    private boolean mouseUsedForSelection;
    private final List<ButtonWidget> gameModeButtons = Lists.newArrayList();

    public GameModeSwitcherScreen() {
        super(NarratorManager.EMPTY);
        this.gameMode = this.currentGameMode = GameModeSelection.of(this.getPreviousGameMode());
    }

    private GameMode getPreviousGameMode() {
        ClientPlayerInteractionManager clientPlayerInteractionManager = MinecraftClient.getInstance().interactionManager;
        GameMode gameMode = clientPlayerInteractionManager.getPreviousGameMode();
        if (gameMode != null) {
            return gameMode;
        }
        return clientPlayerInteractionManager.getCurrentGameMode() == GameMode.CREATIVE ? GameMode.SURVIVAL : GameMode.CREATIVE;
    }

    @Override
    protected void init() {
        super.init();
        this.gameModeButtons.clear();
        this.gameMode = this.currentGameMode;
        for (int i = 0; i < GameModeSelection.VALUES.length; ++i) {
            GameModeSelection gameModeSelection = GameModeSelection.VALUES[i];
            this.gameModeButtons.add(new ButtonWidget(gameModeSelection, this.width / 2 - UI_WIDTH / 2 + i * 31, this.height / 2 - 31));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.drawCenteredTextWithShadow(this.textRenderer, this.gameMode.text, this.width / 2, this.height / 2 - 31 - 20, -1);
        MutableText mutableText = Text.translatable("debug.gamemodes.select_next", this.client.options.debugSwitchGameModeKey.getBoundKeyLocalizedText().copy().formatted(Formatting.AQUA));
        context.drawCenteredTextWithShadow(this.textRenderer, mutableText, this.width / 2, this.height / 2 + 5, -1);
        if (!this.mouseUsedForSelection) {
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
            this.mouseUsedForSelection = true;
        }
        boolean bl = this.lastMouseX == mouseX && this.lastMouseY == mouseY;
        for (ButtonWidget buttonWidget : this.gameModeButtons) {
            buttonWidget.render(context, mouseX, mouseY, deltaTicks);
            buttonWidget.setSelected(this.gameMode == buttonWidget.gameMode);
            if (bl || !buttonWidget.isSelected()) continue;
            this.gameMode = buttonWidget.gameMode;
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int i = this.width / 2 - 62;
        int j = this.height / 2 - 31 - 27;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0f, 0.0f, 125, 75, 128, 128);
    }

    private void apply() {
        GameModeSwitcherScreen.apply(this.client, this.gameMode);
    }

    private static void apply(MinecraftClient client, GameModeSelection gameModeSelection) {
        if (!client.canSwitchGameMode()) {
            return;
        }
        GameModeSelection gameModeSelection2 = GameModeSelection.of(client.interactionManager.getCurrentGameMode());
        if (gameModeSelection != gameModeSelection2 && GameModeCommand.PERMISSION_CHECK.allows(client.player.getPermissions())) {
            client.player.networkHandler.sendPacket(new ChangeGameModeC2SPacket(gameModeSelection.gameMode));
        }
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (this.client.options.debugSwitchGameModeKey.matchesKey(input)) {
            this.mouseUsedForSelection = false;
            this.gameMode = this.gameMode.next();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        if (this.client.options.debugModifierKey.matchesKey(input)) {
            this.apply();
            this.client.setScreen(null);
            return true;
        }
        return super.keyReleased(input);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (this.client.options.debugModifierKey.matchesMouse(click)) {
            this.apply();
            this.client.setScreen(null);
            return true;
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    static final class GameModeSelection
    extends Enum<GameModeSelection> {
        public static final /* enum */ GameModeSelection CREATIVE = new GameModeSelection(Text.translatable("gameMode.creative"), GameMode.CREATIVE, new ItemStack(Blocks.GRASS_BLOCK));
        public static final /* enum */ GameModeSelection SURVIVAL = new GameModeSelection(Text.translatable("gameMode.survival"), GameMode.SURVIVAL, new ItemStack(Items.IRON_SWORD));
        public static final /* enum */ GameModeSelection ADVENTURE = new GameModeSelection(Text.translatable("gameMode.adventure"), GameMode.ADVENTURE, new ItemStack(Items.MAP));
        public static final /* enum */ GameModeSelection SPECTATOR = new GameModeSelection(Text.translatable("gameMode.spectator"), GameMode.SPECTATOR, new ItemStack(Items.ENDER_EYE));
        static final GameModeSelection[] VALUES;
        private static final int field_32317 = 16;
        private static final int field_32316 = 5;
        final Text text;
        final GameMode gameMode;
        private final ItemStack icon;
        private static final /* synthetic */ GameModeSelection[] field_24584;

        public static GameModeSelection[] values() {
            return (GameModeSelection[])field_24584.clone();
        }

        public static GameModeSelection valueOf(String string) {
            return Enum.valueOf(GameModeSelection.class, string);
        }

        private GameModeSelection(Text text, GameMode gameMode, ItemStack icon) {
            this.text = text;
            this.gameMode = gameMode;
            this.icon = icon;
        }

        void renderIcon(DrawContext context, int x, int y) {
            context.drawItem(this.icon, x, y);
        }

        GameModeSelection next() {
            return switch (this.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> SURVIVAL;
                case 1 -> ADVENTURE;
                case 2 -> SPECTATOR;
                case 3 -> CREATIVE;
            };
        }

        static GameModeSelection of(GameMode gameMode) {
            return switch (gameMode) {
                default -> throw new MatchException(null, null);
                case GameMode.SPECTATOR -> SPECTATOR;
                case GameMode.SURVIVAL -> SURVIVAL;
                case GameMode.CREATIVE -> CREATIVE;
                case GameMode.ADVENTURE -> ADVENTURE;
            };
        }

        private static /* synthetic */ GameModeSelection[] method_36886() {
            return new GameModeSelection[]{CREATIVE, SURVIVAL, ADVENTURE, SPECTATOR};
        }

        static {
            field_24584 = GameModeSelection.method_36886();
            VALUES = GameModeSelection.values();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class ButtonWidget
    extends ClickableWidget {
        final GameModeSelection gameMode;
        private boolean selected;

        public ButtonWidget(GameModeSelection gameMode, int x, int y) {
            super(x, y, 26, 26, gameMode.text);
            this.gameMode = gameMode;
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            this.drawBackground(context);
            if (this.selected) {
                this.drawSelectionBox(context);
            }
            this.gameMode.renderIcon(context, this.getX() + 5, this.getY() + 5);
        }

        @Override
        public void appendClickableNarrations(NarrationMessageBuilder builder) {
            this.appendDefaultNarrations(builder);
        }

        @Override
        public boolean isSelected() {
            return super.isSelected() || this.selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        private void drawBackground(DrawContext context) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_TEXTURE, this.getX(), this.getY(), 26, 26);
        }

        private void drawSelectionBox(DrawContext context) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SELECTION_TEXTURE, this.getX(), this.getY(), 26, 26);
        }
    }
}
