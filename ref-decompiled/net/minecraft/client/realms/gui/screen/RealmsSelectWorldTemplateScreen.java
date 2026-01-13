/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ConfirmLinkScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.realms.RealmsClient
 *  net.minecraft.client.realms.dto.RealmsServer$WorldType
 *  net.minecraft.client.realms.dto.WorldTemplate
 *  net.minecraft.client.realms.dto.WorldTemplatePaginatedList
 *  net.minecraft.client.realms.exception.RealmsServiceException
 *  net.minecraft.client.realms.gui.screen.RealmsScreen
 *  net.minecraft.client.realms.gui.screen.RealmsSelectWorldTemplateScreen
 *  net.minecraft.client.realms.gui.screen.RealmsSelectWorldTemplateScreen$WorldTemplateObjectSelectionList
 *  net.minecraft.client.realms.util.TextRenderingUtils$Line
 *  net.minecraft.client.realms.util.TextRenderingUtils$LineSegment
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.dto.WorldTemplatePaginatedList;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.gui.screen.RealmsSelectWorldTemplateScreen;
import net.minecraft.client.realms.util.TextRenderingUtils;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RealmsSelectWorldTemplateScreen
extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    static final Identifier SLOT_FRAME_TEXTURE = Identifier.ofVanilla((String)"widget/slot_frame");
    private static final Text SELECT_TEXT = Text.translatable((String)"mco.template.button.select");
    private static final Text TRAILER_TEXT = Text.translatable((String)"mco.template.button.trailer");
    private static final Text PUBLISHER_TEXT = Text.translatable((String)"mco.template.button.publisher");
    private static final int field_45974 = 100;
    final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this);
    final Consumer<WorldTemplate> callback;
    WorldTemplateObjectSelectionList templateList;
    private final RealmsServer.WorldType worldType;
    private final List<Text> field_62460;
    private ButtonWidget selectButton;
    private ButtonWidget trailerButton;
    private ButtonWidget publisherButton;
    @Nullable WorldTemplate selectedTemplate = null;
    @Nullable String currentLink;
    @Nullable List<// Could not load outer class - annotation placement on inner may be incorrect
    TextRenderingUtils.Line> noTemplatesMessage;

    public RealmsSelectWorldTemplateScreen(Text title, Consumer<WorldTemplate> callback, RealmsServer.WorldType worldType, @Nullable WorldTemplatePaginatedList worldTemplatePaginatedList) {
        this(title, callback, worldType, worldTemplatePaginatedList, List.of());
    }

    public RealmsSelectWorldTemplateScreen(Text title, Consumer<WorldTemplate> callback, RealmsServer.WorldType worldType, @Nullable WorldTemplatePaginatedList templateList, List<Text> list) {
        super(title);
        this.callback = callback;
        this.worldType = worldType;
        if (templateList == null) {
            this.templateList = new WorldTemplateObjectSelectionList(this);
            this.setPagination(new WorldTemplatePaginatedList(10));
        } else {
            this.templateList = new WorldTemplateObjectSelectionList(this, (Iterable)Lists.newArrayList((Iterable)templateList.templates()));
            this.setPagination(templateList);
        }
        this.field_62460 = list;
    }

    public void init() {
        int n = this.field_62460.size();
        Objects.requireNonNull(this.getTextRenderer());
        this.layout.setHeaderHeight(33 + n * (9 + 4));
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addHeader((Widget)DirectionalLayoutWidget.vertical().spacing(4));
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        directionalLayoutWidget.add((Widget)new TextWidget(this.title, this.textRenderer));
        this.field_62460.forEach(text -> directionalLayoutWidget.add((Widget)new TextWidget(text, this.textRenderer)));
        this.templateList = (WorldTemplateObjectSelectionList)this.layout.addBody((Widget)new WorldTemplateObjectSelectionList(this, (Iterable)this.templateList.getValues()));
        DirectionalLayoutWidget directionalLayoutWidget2 = (DirectionalLayoutWidget)this.layout.addFooter((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget2.getMainPositioner().alignHorizontalCenter();
        this.trailerButton = (ButtonWidget)directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)TRAILER_TEXT, button -> this.onTrailer()).width(100).build());
        this.selectButton = (ButtonWidget)directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)SELECT_TEXT, button -> this.selectTemplate()).width(100).build());
        directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> this.close()).width(100).build());
        this.publisherButton = (ButtonWidget)directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)PUBLISHER_TEXT, button -> this.onPublish()).width(100).build());
        this.updateButtonStates();
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        this.templateList.position(this.width, this.layout);
        this.layout.refreshPositions();
    }

    public Text getNarratedTitle() {
        ArrayList list = Lists.newArrayListWithCapacity((int)2);
        list.add(this.title);
        list.addAll(this.field_62460);
        return ScreenTexts.joinLines((Collection)list);
    }

    void updateButtonStates() {
        this.publisherButton.visible = this.selectedTemplate != null && !this.selectedTemplate.link().isEmpty();
        this.trailerButton.visible = this.selectedTemplate != null && !this.selectedTemplate.trailer().isEmpty();
        this.selectButton.active = this.selectedTemplate != null;
    }

    public void close() {
        this.callback.accept(null);
    }

    private void selectTemplate() {
        if (this.selectedTemplate != null) {
            this.callback.accept(this.selectedTemplate);
        }
    }

    private void onTrailer() {
        if (this.selectedTemplate != null && !this.selectedTemplate.trailer().isBlank()) {
            ConfirmLinkScreen.open((Screen)this, (String)this.selectedTemplate.trailer());
        }
    }

    private void onPublish() {
        if (this.selectedTemplate != null && !this.selectedTemplate.link().isBlank()) {
            ConfirmLinkScreen.open((Screen)this, (String)this.selectedTemplate.link());
        }
    }

    private void setPagination(WorldTemplatePaginatedList templateList) {
        new /* Unavailable Anonymous Inner Class!! */.start();
    }

    Either<WorldTemplatePaginatedList, Exception> fetchWorldTemplates(WorldTemplatePaginatedList templateList, RealmsClient realms) {
        try {
            return Either.left((Object)realms.fetchWorldTemplates(templateList.page() + 1, templateList.size(), this.worldType));
        }
        catch (RealmsServiceException realmsServiceException) {
            return Either.right((Object)((Object)realmsServiceException));
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.currentLink = null;
        if (this.noTemplatesMessage != null) {
            this.renderMessages(context, mouseX, mouseY, this.noTemplatesMessage);
        }
    }

    private void renderMessages(DrawContext context, int x, int y, List<TextRenderingUtils.Line> messages) {
        for (int i = 0; i < messages.size(); ++i) {
            TextRenderingUtils.Line line = messages.get(i);
            int j = RealmsSelectWorldTemplateScreen.row((int)(4 + i));
            int k = line.segments.stream().mapToInt(segment -> this.textRenderer.getWidth(segment.renderedText())).sum();
            int l = this.width / 2 - k / 2;
            for (TextRenderingUtils.LineSegment lineSegment : line.segments) {
                int m = lineSegment.isLink() ? -13408581 : -1;
                String string = lineSegment.renderedText();
                context.drawTextWithShadow(this.textRenderer, string, l, j, m);
                int n = l + this.textRenderer.getWidth(string);
                if (lineSegment.isLink() && x > l && x < n && y > j - 3 && y < j + 8) {
                    context.drawTooltip((Text)Text.literal((String)lineSegment.getLinkUrl()), x, y);
                    this.currentLink = lineSegment.getLinkUrl();
                }
                l = n;
            }
        }
    }

    static /* synthetic */ MinecraftClient method_25229(RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen) {
        return realmsSelectWorldTemplateScreen.client;
    }

    static /* synthetic */ TextRenderer method_53516(RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen) {
        return realmsSelectWorldTemplateScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_25238(RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen) {
        return realmsSelectWorldTemplateScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_25239(RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen) {
        return realmsSelectWorldTemplateScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_25240(RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen) {
        return realmsSelectWorldTemplateScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_25241(RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen) {
        return realmsSelectWorldTemplateScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_25242(RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen) {
        return realmsSelectWorldTemplateScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_25243(RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen) {
        return realmsSelectWorldTemplateScreen.textRenderer;
    }
}

