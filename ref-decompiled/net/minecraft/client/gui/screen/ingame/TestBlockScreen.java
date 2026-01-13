/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.entity.TestBlockEntity
 *  net.minecraft.block.enums.TestBlockMode
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.TestBlockScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.CyclingButtonWidget
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.SetTestBlockC2SPacket
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.math.BlockPos
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.TestBlockEntity;
import net.minecraft.block.enums.TestBlockMode;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.SetTestBlockC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TestBlockScreen
extends Screen {
    private static final List<TestBlockMode> MODES = List.of(TestBlockMode.values());
    private static final Text TITLE_TEXT = Text.translatable((String)Blocks.TEST_BLOCK.getTranslationKey());
    private static final Text MESSAGE_TEXT = Text.translatable((String)"test_block.message");
    private final BlockPos pos;
    private TestBlockMode mode;
    private String message;
    private @Nullable TextFieldWidget textField;

    public TestBlockScreen(TestBlockEntity blockEntity) {
        super(TITLE_TEXT);
        this.pos = blockEntity.getPos();
        this.mode = blockEntity.getMode();
        this.message = blockEntity.getMessage();
    }

    public void init() {
        this.textField = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 80, 240, 20, (Text)Text.translatable((String)"test_block.message"));
        this.textField.setMaxLength(128);
        this.textField.setText(this.message);
        this.addDrawableChild((Element)this.textField);
        this.setMode(this.mode);
        this.addDrawableChild((Element)CyclingButtonWidget.builder(TestBlockMode::getName, (Object)this.mode).values((Collection)MODES).omitKeyText().build(this.width / 2 - 4 - 150, 185, 50, 20, TITLE_TEXT, (button, mode) -> this.setMode(mode)));
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> this.onDone()).dimensions(this.width / 2 - 4 - 150, 210, 150, 20).build());
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> this.onCancel()).dimensions(this.width / 2 + 4, 210, 150, 20).build());
    }

    protected void setInitialFocus() {
        if (this.textField != null) {
            this.setInitialFocus((Element)this.textField);
        } else {
            super.setInitialFocus();
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, -1);
        if (this.mode != TestBlockMode.START) {
            context.drawTextWithShadow(this.textRenderer, MESSAGE_TEXT, this.width / 2 - 153, 70, -6250336);
        }
        context.drawTextWithShadow(this.textRenderer, this.mode.getInfo(), this.width / 2 - 153, 174, -6250336);
    }

    public boolean shouldPause() {
        return false;
    }

    public boolean deferSubtitles() {
        return true;
    }

    private void onDone() {
        this.message = this.textField.getText();
        this.client.getNetworkHandler().sendPacket((Packet)new SetTestBlockC2SPacket(this.pos, this.mode, this.message));
        this.close();
    }

    public void close() {
        this.onCancel();
    }

    private void onCancel() {
        this.client.setScreen(null);
    }

    private void setMode(TestBlockMode mode) {
        this.mode = mode;
        this.textField.visible = mode != TestBlockMode.START;
    }
}

