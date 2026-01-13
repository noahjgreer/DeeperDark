/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.Alignment
 *  net.minecraft.client.font.DrawnTextConsumer
 *  net.minecraft.client.font.DrawnTextConsumer$ClickHandler
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.DrawContext$HoverType
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.BookScreen
 *  net.minecraft.client.gui.screen.ingame.BookScreen$Contents
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.PageTurnWidget
 *  net.minecraft.client.input.AbstractInput
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.ClickEvent
 *  net.minecraft.text.ClickEvent$ChangePage
 *  net.minecraft.text.ClickEvent$RunCommand
 *  net.minecraft.text.OrderedText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.text.Texts
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import java.lang.runtime.SwitchBootstraps;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class BookScreen
extends Screen {
    public static final int field_32328 = 16;
    public static final int field_32329 = 36;
    public static final int field_32330 = 30;
    private static final int field_52807 = 256;
    private static final int field_52808 = 256;
    private static final Text TITLE_TEXT = Text.translatable((String)"book.view.title");
    private static final Style STYLE = Style.EMPTY.withoutShadow().withColor(-16777216);
    public static final Contents EMPTY_PROVIDER = new Contents(List.of());
    public static final Identifier BOOK_TEXTURE = Identifier.ofVanilla((String)"textures/gui/book.png");
    protected static final int MAX_TEXT_WIDTH = 114;
    protected static final int MAX_TEXT_HEIGHT = 128;
    protected static final int WIDTH = 192;
    private static final int field_63904 = 148;
    protected static final int HEIGHT = 192;
    private static final int field_63905 = 157;
    private static final int field_63906 = 43;
    private static final int field_63907 = 116;
    private Contents contents;
    private int pageIndex;
    private List<OrderedText> cachedPage = Collections.emptyList();
    private int cachedPageIndex = -1;
    private Text pageIndexText = ScreenTexts.EMPTY;
    private PageTurnWidget nextPageButton;
    private PageTurnWidget previousPageButton;
    private final boolean pageTurnSound;

    public BookScreen(Contents pageProvider) {
        this(pageProvider, true);
    }

    public BookScreen() {
        this(EMPTY_PROVIDER, false);
    }

    private BookScreen(Contents contents, boolean playPageTurnSound) {
        super(TITLE_TEXT);
        this.contents = contents;
        this.pageTurnSound = playPageTurnSound;
    }

    public void setPageProvider(Contents pageProvider) {
        this.contents = pageProvider;
        this.pageIndex = MathHelper.clamp((int)this.pageIndex, (int)0, (int)pageProvider.getPageCount());
        this.updatePageButtons();
        this.cachedPageIndex = -1;
    }

    public boolean setPage(int index) {
        int i = MathHelper.clamp((int)index, (int)0, (int)(this.contents.getPageCount() - 1));
        if (i != this.pageIndex) {
            this.pageIndex = i;
            this.updatePageButtons();
            this.cachedPageIndex = -1;
            return true;
        }
        return false;
    }

    protected boolean jumpToPage(int page) {
        return this.setPage(page);
    }

    protected void init() {
        this.addCloseButton();
        this.addPageButtons();
    }

    public Text getNarratedTitle() {
        return ScreenTexts.joinLines((Text[])new Text[]{super.getNarratedTitle(), this.getPageIndicatorText(), this.contents.getPage(this.pageIndex)});
    }

    private Text getPageIndicatorText() {
        return Text.translatable((String)"book.pageIndicator", (Object[])new Object[]{this.pageIndex + 1, Math.max(this.getPageCount(), 1)}).fillStyle(STYLE);
    }

    protected void addCloseButton() {
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> this.close()).position((this.width - 200) / 2, this.getCloseButtonY()).width(200).build());
    }

    protected void addPageButtons() {
        int i = this.getLeft();
        int j = this.getTop();
        this.nextPageButton = (PageTurnWidget)this.addDrawableChild((Element)new PageTurnWidget(i + 116, j + 157, true, button -> this.goToNextPage(), this.pageTurnSound));
        this.previousPageButton = (PageTurnWidget)this.addDrawableChild((Element)new PageTurnWidget(i + 43, j + 157, false, button -> this.goToPreviousPage(), this.pageTurnSound));
        this.updatePageButtons();
    }

    private int getPageCount() {
        return this.contents.getPageCount();
    }

    protected void goToPreviousPage() {
        if (this.pageIndex > 0) {
            --this.pageIndex;
        }
        this.updatePageButtons();
    }

    protected void goToNextPage() {
        if (this.pageIndex < this.getPageCount() - 1) {
            ++this.pageIndex;
        }
        this.updatePageButtons();
    }

    private void updatePageButtons() {
        this.nextPageButton.visible = this.pageIndex < this.getPageCount() - 1;
        this.previousPageButton.visible = this.pageIndex > 0;
    }

    public boolean keyPressed(KeyInput input) {
        if (super.keyPressed(input)) {
            return true;
        }
        return switch (input.key()) {
            case 266 -> {
                this.previousPageButton.onPress((AbstractInput)input);
                yield true;
            }
            case 267 -> {
                this.nextPageButton.onPress((AbstractInput)input);
                yield true;
            }
            default -> false;
        };
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.render(context.getTextConsumer(DrawContext.HoverType.TOOLTIP_AND_CURSOR), false);
    }

    private void render(DrawnTextConsumer drawer, boolean click) {
        if (this.cachedPageIndex != this.pageIndex) {
            Text stringVisitable = Texts.withStyle((Text)this.contents.getPage(this.pageIndex), (Style)STYLE);
            this.cachedPage = this.textRenderer.wrapLines((StringVisitable)stringVisitable, 114);
            this.pageIndexText = this.getPageIndicatorText();
            this.cachedPageIndex = this.pageIndex;
        }
        int i = this.getLeft();
        int j = this.getTop();
        if (!click) {
            drawer.text(Alignment.RIGHT, i + 148, j + 16, this.pageIndexText);
        }
        Objects.requireNonNull(this.textRenderer);
        int k = Math.min(128 / 9, this.cachedPage.size());
        for (int l = 0; l < k; ++l) {
            OrderedText orderedText = (OrderedText)this.cachedPage.get(l);
            Objects.requireNonNull(this.textRenderer);
            drawer.text(i + 36, j + 30 + l * 9, orderedText);
        }
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderBackground(context, mouseX, mouseY, deltaTicks);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, BOOK_TEXTURE, this.getLeft(), this.getTop(), 0.0f, 0.0f, 192, 192, 256, 256);
    }

    private int getLeft() {
        return (this.width - 192) / 2;
    }

    private int getTop() {
        return 2;
    }

    protected int getCloseButtonY() {
        return this.getTop() + 192 + 2;
    }

    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() == 0) {
            DrawnTextConsumer.ClickHandler clickHandler = new DrawnTextConsumer.ClickHandler(this.textRenderer, (int)click.x(), (int)click.y());
            this.render((DrawnTextConsumer)clickHandler, true);
            Style style = clickHandler.getStyle();
            if (style != null && this.handleClickEvent(style.getClickEvent())) {
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected boolean handleClickEvent(@Nullable ClickEvent clickEvent) {
        if (clickEvent == null) {
            return false;
        }
        ClientPlayerEntity clientPlayerEntity = Objects.requireNonNull(this.client.player, "Player not available");
        ClickEvent clickEvent2 = clickEvent;
        Objects.requireNonNull(clickEvent2);
        ClickEvent clickEvent3 = clickEvent2;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ClickEvent.ChangePage.class, ClickEvent.RunCommand.class}, (Object)clickEvent3, n)) {
            case 0: {
                int i;
                ClickEvent.ChangePage changePage = (ClickEvent.ChangePage)clickEvent3;
                try {
                    int n2;
                    i = n2 = changePage.page();
                }
                catch (Throwable throwable) {
                    throw new MatchException(throwable.toString(), throwable);
                }
                this.jumpToPage(i - 1);
                return true;
            }
            case 1: {
                String string2;
                ClickEvent.RunCommand runCommand = (ClickEvent.RunCommand)clickEvent3;
                {
                    String string;
                    string2 = string = runCommand.command();
                }
                this.closeScreen();
                BookScreen.handleRunCommand((ClientPlayerEntity)clientPlayerEntity, (String)string2, null);
                return true;
            }
        }
        BookScreen.handleClickEvent((ClickEvent)clickEvent, (MinecraftClient)this.client, (Screen)this);
        return true;
    }

    protected void closeScreen() {
    }

    public boolean deferSubtitles() {
        return true;
    }
}

