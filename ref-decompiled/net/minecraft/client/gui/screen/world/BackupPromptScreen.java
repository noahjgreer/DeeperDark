/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.Alignment
 *  net.minecraft.client.font.DrawnTextConsumer
 *  net.minecraft.client.font.MultilineText
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.world.BackupPromptScreen
 *  net.minecraft.client.gui.screen.world.BackupPromptScreen$Callback
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.CheckboxWidget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.screen.world;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.BackupPromptScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class BackupPromptScreen
extends Screen {
    private static final Text SKIP_BUTTON_TEXT = Text.translatable((String)"selectWorld.backupJoinSkipButton");
    public static final Text CONFIRM_BUTTON_TEXT = Text.translatable((String)"selectWorld.backupJoinConfirmButton");
    private final Runnable onCancel;
    protected final Callback callback;
    private final Text subtitle;
    private final boolean showEraseCacheCheckbox;
    private MultilineText wrappedText = MultilineText.EMPTY;
    final Text firstButtonText;
    protected int field_32236;
    private CheckboxWidget eraseCacheCheckbox;

    public BackupPromptScreen(Runnable onCancel, Callback callback, Text title, Text subtitle, boolean showEraseCacheCheckbox) {
        this(onCancel, callback, title, subtitle, CONFIRM_BUTTON_TEXT, showEraseCacheCheckbox);
    }

    public BackupPromptScreen(Runnable onCancel, Callback callback, Text title, Text subtitle, Text firstButtonText, boolean showEraseCacheCheckbox) {
        super(title);
        this.onCancel = onCancel;
        this.callback = callback;
        this.subtitle = subtitle;
        this.showEraseCacheCheckbox = showEraseCacheCheckbox;
        this.firstButtonText = firstButtonText;
    }

    protected void init() {
        super.init();
        this.wrappedText = MultilineText.create((TextRenderer)this.textRenderer, (Text)this.subtitle, (int)(this.width - 50));
        int n = this.wrappedText.getLineCount() + 1;
        Objects.requireNonNull(this.textRenderer);
        int i = n * 9;
        this.eraseCacheCheckbox = CheckboxWidget.builder((Text)Text.translatable((String)"selectWorld.backupEraseCache").withColor(-2039584), (TextRenderer)this.textRenderer).pos(this.width / 2 - 155 + 80, 76 + i).build();
        if (this.showEraseCacheCheckbox) {
            this.addDrawableChild((Element)this.eraseCacheCheckbox);
        }
        this.addDrawableChild((Element)ButtonWidget.builder((Text)this.firstButtonText, button -> this.callback.proceed(true, this.eraseCacheCheckbox.isChecked())).dimensions(this.width / 2 - 155, 100 + i, 150, 20).build());
        this.addDrawableChild((Element)ButtonWidget.builder((Text)SKIP_BUTTON_TEXT, button -> this.callback.proceed(false, this.eraseCacheCheckbox.isChecked())).dimensions(this.width / 2 - 155 + 160, 100 + i, 150, 20).build());
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> this.onCancel.run()).dimensions(this.width / 2 - 155 + 80, 124 + i, 150, 20).build());
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        DrawnTextConsumer drawnTextConsumer = context.getTextConsumer();
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 50, -1);
        int n = this.width / 2;
        Objects.requireNonNull(this.textRenderer);
        this.wrappedText.draw(Alignment.CENTER, n, 70, 9, drawnTextConsumer);
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    public boolean keyPressed(KeyInput input) {
        if (input.key() == 256) {
            this.onCancel.run();
            return true;
        }
        return super.keyPressed(input);
    }
}

