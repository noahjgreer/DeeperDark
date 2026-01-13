/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.GameModeSwitcherScreen
 *  net.minecraft.client.gui.screen.GameModeSwitcherScreen$ButtonWidget
 *  net.minecraft.client.gui.screen.GameModeSwitcherScreen$GameModeSelection
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.client.network.ClientPlayerInteractionManager
 *  net.minecraft.client.util.NarratorManager
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.ChangeGameModeC2SPacket
 *  net.minecraft.server.command.GameModeCommand
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  net.minecraft.world.GameMode
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameModeSwitcherScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ChangeGameModeC2SPacket;
import net.minecraft.server.command.GameModeCommand;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class GameModeSwitcherScreen
extends Screen {
    static final Identifier SLOT_TEXTURE = Identifier.ofVanilla((String)"gamemode_switcher/slot");
    static final Identifier SELECTION_TEXTURE = Identifier.ofVanilla((String)"gamemode_switcher/selection");
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/gui/container/gamemode_switcher.png");
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
        this.gameMode = this.currentGameMode = GameModeSelection.of((GameMode)this.getPreviousGameMode());
    }

    private GameMode getPreviousGameMode() {
        ClientPlayerInteractionManager clientPlayerInteractionManager = MinecraftClient.getInstance().interactionManager;
        GameMode gameMode = clientPlayerInteractionManager.getPreviousGameMode();
        if (gameMode != null) {
            return gameMode;
        }
        return clientPlayerInteractionManager.getCurrentGameMode() == GameMode.CREATIVE ? GameMode.SURVIVAL : GameMode.CREATIVE;
    }

    protected void init() {
        super.init();
        this.gameModeButtons.clear();
        this.gameMode = this.currentGameMode;
        for (int i = 0; i < GameModeSelection.VALUES.length; ++i) {
            GameModeSelection gameModeSelection = GameModeSelection.VALUES[i];
            this.gameModeButtons.add(new ButtonWidget(gameModeSelection, this.width / 2 - UI_WIDTH / 2 + i * 31, this.height / 2 - 31));
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.drawCenteredTextWithShadow(this.textRenderer, this.gameMode.text, this.width / 2, this.height / 2 - 31 - 20, -1);
        MutableText mutableText = Text.translatable((String)"debug.gamemodes.select_next", (Object[])new Object[]{this.client.options.debugSwitchGameModeKey.getBoundKeyLocalizedText().copy().formatted(Formatting.AQUA)});
        context.drawCenteredTextWithShadow(this.textRenderer, (Text)mutableText, this.width / 2, this.height / 2 + 5, -1);
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

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int i = this.width / 2 - 62;
        int j = this.height / 2 - 31 - 27;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0f, 0.0f, 125, 75, 128, 128);
    }

    private void apply() {
        GameModeSwitcherScreen.apply((MinecraftClient)this.client, (GameModeSelection)this.gameMode);
    }

    private static void apply(MinecraftClient client, GameModeSelection gameModeSelection) {
        if (!client.canSwitchGameMode()) {
            return;
        }
        GameModeSelection gameModeSelection2 = GameModeSelection.of((GameMode)client.interactionManager.getCurrentGameMode());
        if (gameModeSelection != gameModeSelection2 && GameModeCommand.PERMISSION_CHECK.allows(client.player.getPermissions())) {
            client.player.networkHandler.sendPacket((Packet)new ChangeGameModeC2SPacket(gameModeSelection.gameMode));
        }
    }

    public boolean keyPressed(KeyInput input) {
        if (this.client.options.debugSwitchGameModeKey.matchesKey(input)) {
            this.mouseUsedForSelection = false;
            this.gameMode = this.gameMode.next();
            return true;
        }
        return super.keyPressed(input);
    }

    public boolean keyReleased(KeyInput input) {
        if (this.client.options.debugModifierKey.matchesKey(input)) {
            this.apply();
            this.client.setScreen(null);
            return true;
        }
        return super.keyReleased(input);
    }

    public boolean mouseReleased(Click click) {
        if (this.client.options.debugModifierKey.matchesMouse(click)) {
            this.apply();
            this.client.setScreen(null);
            return true;
        }
        return super.mouseReleased(click);
    }

    public boolean shouldPause() {
        return false;
    }
}

