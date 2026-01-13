/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.client.realms.RealmsClient
 *  net.minecraft.client.realms.dto.RealmsServer
 *  net.minecraft.client.realms.exception.RealmsServiceException
 *  net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen
 *  net.minecraft.client.realms.gui.screen.RealmsScreen
 *  net.minecraft.client.realms.gui.screen.RealmsTermsScreen
 *  net.minecraft.client.realms.task.LongRunningTask
 *  net.minecraft.client.realms.task.RealmsPrepareConnectionTask
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.util.Urls
 *  net.minecraft.util.Util
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.client.realms.task.RealmsPrepareConnectionTask;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Urls;
import net.minecraft.util.Util;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RealmsTermsScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Text TITLE = Text.translatable((String)"mco.terms.title");
    private static final Text SENTENCE_ONE_TEXT = Text.translatable((String)"mco.terms.sentence.1");
    private static final Text SENTENCE_TWO_TEXT = ScreenTexts.space().append((Text)Text.translatable((String)"mco.terms.sentence.2").fillStyle(Style.EMPTY.withUnderline(Boolean.valueOf(true))));
    private final Screen parent;
    private final RealmsServer realmsServer;
    private boolean onLink;

    public RealmsTermsScreen(Screen parent, RealmsServer realmsServer) {
        super(TITLE);
        this.parent = parent;
        this.realmsServer = realmsServer;
    }

    public void init() {
        int i = this.width / 4 - 2;
        this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"mco.terms.buttons.agree"), button -> this.agreedToTos()).dimensions(this.width / 4, RealmsTermsScreen.row((int)12), i, 20).build());
        this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"mco.terms.buttons.disagree"), button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 + 4, RealmsTermsScreen.row((int)12), i, 20).build());
    }

    public boolean keyPressed(KeyInput input) {
        if (input.key() == 256) {
            this.client.setScreen(this.parent);
            return true;
        }
        return super.keyPressed(input);
    }

    private void agreedToTos() {
        RealmsClient realmsClient = RealmsClient.create();
        try {
            realmsClient.agreeToTos();
            this.client.setScreen((Screen)new RealmsLongRunningMcoTaskScreen(this.parent, new LongRunningTask[]{new RealmsPrepareConnectionTask(this.parent, this.realmsServer)}));
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't agree to TOS", (Throwable)realmsServiceException);
        }
    }

    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.onLink) {
            this.client.keyboard.setClipboard(Urls.REALMS_TERMS.toString());
            Util.getOperatingSystem().open(Urls.REALMS_TERMS);
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences((Text[])new Text[]{super.getNarratedTitle(), SENTENCE_ONE_TEXT}).append(ScreenTexts.SPACE).append(SENTENCE_TWO_TEXT);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 17, -1);
        context.drawTextWithShadow(this.textRenderer, SENTENCE_ONE_TEXT, this.width / 2 - 120, RealmsTermsScreen.row((int)5), -1);
        int i = this.textRenderer.getWidth((StringVisitable)SENTENCE_ONE_TEXT);
        int j = this.width / 2 - 121 + i;
        int k = RealmsTermsScreen.row((int)5);
        int l = j + this.textRenderer.getWidth((StringVisitable)SENTENCE_TWO_TEXT) + 1;
        Objects.requireNonNull(this.textRenderer);
        int m = k + 1 + 9;
        this.onLink = j <= mouseX && mouseX <= l && k <= mouseY && mouseY <= m;
        context.drawTextWithShadow(this.textRenderer, SENTENCE_TWO_TEXT, this.width / 2 - 120 + i, RealmsTermsScreen.row((int)5), this.onLink ? -9670204 : -13408581);
    }
}

