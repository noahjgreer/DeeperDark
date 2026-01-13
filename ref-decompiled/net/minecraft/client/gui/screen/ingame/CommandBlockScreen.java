/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.entity.CommandBlockBlockEntity
 *  net.minecraft.block.entity.CommandBlockBlockEntity$Type
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen
 *  net.minecraft.client.gui.screen.ingame.CommandBlockScreen
 *  net.minecraft.client.gui.screen.ingame.CommandBlockScreen$1
 *  net.minecraft.client.gui.widget.CyclingButtonWidget
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket
 *  net.minecraft.text.Text
 *  net.minecraft.world.CommandBlockExecutor
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.world.CommandBlockExecutor;

@Environment(value=EnvType.CLIENT)
public class CommandBlockScreen
extends AbstractCommandBlockScreen {
    private final CommandBlockBlockEntity blockEntity;
    private CyclingButtonWidget<CommandBlockBlockEntity.Type> modeButton;
    private CyclingButtonWidget<Boolean> conditionalModeButton;
    private CyclingButtonWidget<Boolean> redstoneTriggerButton;
    private CommandBlockBlockEntity.Type mode = CommandBlockBlockEntity.Type.REDSTONE;
    private boolean conditional;
    private boolean autoActivate;

    public CommandBlockScreen(CommandBlockBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    CommandBlockExecutor getCommandExecutor() {
        return this.blockEntity.getCommandExecutor();
    }

    int getTrackOutputButtonHeight() {
        return 135;
    }

    protected void init() {
        super.init();
        this.setButtonsActive(false);
    }

    protected void addAdditionalButtons() {
        this.modeButton = (CyclingButtonWidget)this.addDrawableChild((Element)CyclingButtonWidget.builder(type -> switch (1.field_2875[type.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> Text.translatable((String)"advMode.mode.sequence");
            case 2 -> Text.translatable((String)"advMode.mode.auto");
            case 3 -> Text.translatable((String)"advMode.mode.redstone");
        }, (Object)this.mode).values((Object[])CommandBlockBlockEntity.Type.values()).omitKeyText().build(this.width / 2 - 50 - 100 - 4, 165, 100, 20, (Text)Text.translatable((String)"advMode.mode"), (button, mode) -> {
            this.mode = mode;
        }));
        this.conditionalModeButton = (CyclingButtonWidget)this.addDrawableChild((Element)CyclingButtonWidget.onOffBuilder((Text)Text.translatable((String)"advMode.mode.conditional"), (Text)Text.translatable((String)"advMode.mode.unconditional"), (boolean)this.conditional).omitKeyText().build(this.width / 2 - 50, 165, 100, 20, (Text)Text.translatable((String)"advMode.type"), (button, conditional) -> {
            this.conditional = conditional;
        }));
        this.redstoneTriggerButton = (CyclingButtonWidget)this.addDrawableChild((Element)CyclingButtonWidget.onOffBuilder((Text)Text.translatable((String)"advMode.mode.autoexec.bat"), (Text)Text.translatable((String)"advMode.mode.redstoneTriggered"), (boolean)this.autoActivate).omitKeyText().build(this.width / 2 + 50 + 4, 165, 100, 20, (Text)Text.translatable((String)"advMode.triggering"), (button, autoActivate) -> {
            this.autoActivate = autoActivate;
        }));
    }

    private void setButtonsActive(boolean active) {
        this.doneButton.active = active;
        this.toggleTrackingOutputButton.active = active;
        this.modeButton.active = active;
        this.conditionalModeButton.active = active;
        this.redstoneTriggerButton.active = active;
    }

    public void updateCommandBlock() {
        CommandBlockExecutor commandBlockExecutor = this.blockEntity.getCommandExecutor();
        this.consoleCommandTextField.setText(commandBlockExecutor.getCommand());
        boolean bl = commandBlockExecutor.isTrackingOutput();
        this.mode = this.blockEntity.getCommandBlockType();
        this.conditional = this.blockEntity.isConditionalCommandBlock();
        this.autoActivate = this.blockEntity.isAuto();
        this.toggleTrackingOutputButton.setValue((Object)bl);
        this.modeButton.setValue((Object)this.mode);
        this.conditionalModeButton.setValue((Object)this.conditional);
        this.redstoneTriggerButton.setValue((Object)this.autoActivate);
        this.setPreviousOutputText(bl);
        this.setButtonsActive(true);
    }

    public void resize(int width, int height) {
        super.resize(width, height);
        this.setButtonsActive(true);
    }

    protected void syncSettingsToServer() {
        this.client.getNetworkHandler().sendPacket((Packet)new UpdateCommandBlockC2SPacket(this.blockEntity.getPos(), this.consoleCommandTextField.getText(), this.mode, this.blockEntity.getCommandExecutor().isTrackingOutput(), this.conditional, this.autoActivate));
    }
}

