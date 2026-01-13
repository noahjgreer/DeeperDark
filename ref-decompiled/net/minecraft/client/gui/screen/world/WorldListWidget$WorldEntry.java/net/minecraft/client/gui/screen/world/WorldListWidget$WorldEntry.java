/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.gui.screen.world.SymlinkWarningScreen;
import net.minecraft.client.gui.screen.world.WorldIcon;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.SquareWidgetEntry;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.nbt.NbtCrashException;
import net.minecraft.nbt.NbtException;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.path.SymlinkEntry;
import net.minecraft.util.path.SymlinkValidationException;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public final class WorldListWidget.WorldEntry
extends WorldListWidget.Entry
implements SquareWidgetEntry {
    private static final int field_64210 = 32;
    private final WorldListWidget parent;
    private final MinecraftClient client;
    private final Screen screen;
    final LevelSummary level;
    private final WorldIcon icon;
    private final TextWidget displayNameWidget;
    private final TextWidget nameWidget;
    private final TextWidget detailsWidget;
    private @Nullable Path iconPath;

    public WorldListWidget.WorldEntry(WorldListWidget parent, LevelSummary level) {
        this.parent = parent;
        this.client = parent.client;
        this.screen = parent.getParent();
        this.level = level;
        this.icon = WorldIcon.forWorld(this.client.getTextureManager(), level.getName());
        this.iconPath = level.getIconPath();
        int i = parent.getRowWidth() - this.getTextX() - 2;
        MutableText text = Text.literal(level.getDisplayName());
        this.displayNameWidget = new TextWidget(text, this.client.textRenderer);
        this.displayNameWidget.setMaxWidth(i);
        if (this.client.textRenderer.getWidth(text) > i) {
            this.displayNameWidget.setTooltip(Tooltip.of(text));
        }
        Object string = level.getName();
        long l = level.getLastPlayed();
        if (l != -1L) {
            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault());
            string = (String)string + " (" + DATE_FORMAT.format(zonedDateTime) + ")";
        }
        MutableText text2 = Text.literal((String)string).withColor(-8355712);
        this.nameWidget = new TextWidget(text2, this.client.textRenderer);
        this.nameWidget.setMaxWidth(i);
        if (this.client.textRenderer.getWidth((String)string) > i) {
            this.nameWidget.setTooltip(Tooltip.of(text2));
        }
        Text text3 = Texts.withStyle(level.getDetails(), Style.EMPTY.withColor(-8355712));
        this.detailsWidget = new TextWidget(text3, this.client.textRenderer);
        this.detailsWidget.setMaxWidth(i);
        if (this.client.textRenderer.getWidth(text3) > i) {
            this.detailsWidget.setTooltip(Tooltip.of(text3));
        }
        this.validateIconPath();
        this.loadIcon();
    }

    private void validateIconPath() {
        if (this.iconPath == null) {
            return;
        }
        try {
            BasicFileAttributes basicFileAttributes = Files.readAttributes(this.iconPath, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            if (basicFileAttributes.isSymbolicLink()) {
                List<SymlinkEntry> list = this.client.getSymlinkFinder().validate(this.iconPath);
                if (!list.isEmpty()) {
                    LOGGER.warn("{}", (Object)SymlinkValidationException.getMessage(this.iconPath, list));
                    this.iconPath = null;
                } else {
                    basicFileAttributes = Files.readAttributes(this.iconPath, BasicFileAttributes.class, new LinkOption[0]);
                }
            }
            if (!basicFileAttributes.isRegularFile()) {
                this.iconPath = null;
            }
        }
        catch (NoSuchFileException noSuchFileException) {
            this.iconPath = null;
        }
        catch (IOException iOException) {
            LOGGER.error("could not validate symlink", (Throwable)iOException);
            this.iconPath = null;
        }
    }

    @Override
    public Text getNarration() {
        MutableText text = Text.translatable("narrator.select.world_info", this.level.getDisplayName(), Text.of(new Date(this.level.getLastPlayed())), this.level.getDetails());
        if (this.level.isLocked()) {
            text = ScreenTexts.joinSentences(text, LOCKED_TEXT);
        }
        if (this.level.isExperimental()) {
            text = ScreenTexts.joinSentences(text, EXPERIMENTAL_TEXT);
        }
        return Text.translatable("narrator.select", text);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        int i = this.getTextX();
        this.displayNameWidget.setPosition(i, this.getContentY() + 1);
        this.displayNameWidget.render(context, mouseX, mouseY, deltaTicks);
        this.nameWidget.setPosition(i, this.getContentY() + this.client.textRenderer.fontHeight + 3);
        this.nameWidget.render(context, mouseX, mouseY, deltaTicks);
        this.detailsWidget.setPosition(i, this.getContentY() + this.client.textRenderer.fontHeight + this.client.textRenderer.fontHeight + 3);
        this.detailsWidget.render(context, mouseX, mouseY, deltaTicks);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, this.icon.getTextureId(), this.getContentX(), this.getContentY(), 0.0f, 0.0f, 32, 32, 32, 32);
        if (this.parent.worldListType == WorldListWidget.WorldListType.SINGLEPLAYER && (this.client.options.getTouchscreen().getValue().booleanValue() || hovered)) {
            Identifier identifier4;
            context.fill(this.getContentX(), this.getContentY(), this.getContentX() + 32, this.getContentY() + 32, -1601138544);
            int j = mouseX - this.getContentX();
            int k = mouseY - this.getContentY();
            boolean bl = this.isInside(j, k, 32);
            Identifier identifier = bl ? JOIN_HIGHLIGHTED_TEXTURE : JOIN_TEXTURE;
            Identifier identifier2 = bl ? WARNING_HIGHLIGHTED_TEXTURE : WARNING_TEXTURE;
            Identifier identifier3 = bl ? ERROR_HIGHLIGHTED_TEXTURE : ERROR_TEXTURE;
            Identifier identifier5 = identifier4 = bl ? MARKED_JOIN_HIGHLIGHTED_TEXTURE : MARKED_JOIN_TEXTURE;
            if (this.level instanceof LevelSummary.SymlinkLevelSummary || this.level instanceof LevelSummary.RecoveryWarning) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier3, this.getContentX(), this.getContentY(), 32, 32);
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier4, this.getContentX(), this.getContentY(), 32, 32);
                return;
            }
            if (this.level.isLocked()) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier3, this.getContentX(), this.getContentY(), 32, 32);
                if (bl) {
                    context.drawTooltip(this.client.textRenderer.wrapLines(LOCKED_TEXT, 175), mouseX, mouseY);
                }
            } else if (this.level.requiresConversion()) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier3, this.getContentX(), this.getContentY(), 32, 32);
                if (bl) {
                    context.drawTooltip(this.client.textRenderer.wrapLines(CONVERSION_TOOLTIP, 175), mouseX, mouseY);
                }
            } else if (!this.level.isVersionAvailable()) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier3, this.getContentX(), this.getContentY(), 32, 32);
                if (bl) {
                    context.drawTooltip(this.client.textRenderer.wrapLines(INCOMPATIBLE_TOOLTIP, 175), mouseX, mouseY);
                }
            } else if (this.level.shouldPromptBackup()) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier4, this.getContentX(), this.getContentY(), 32, 32);
                if (this.level.wouldBeDowngraded()) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier3, this.getContentX(), this.getContentY(), 32, 32);
                    if (bl) {
                        context.drawTooltip((List<OrderedText>)ImmutableList.of((Object)FROM_NEWER_VERSION_FIRST_LINE.asOrderedText(), (Object)FROM_NEWER_VERSION_SECOND_LINE.asOrderedText()), mouseX, mouseY);
                    }
                } else if (!SharedConstants.getGameVersion().stable()) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier2, this.getContentX(), this.getContentY(), 32, 32);
                    if (bl) {
                        context.drawTooltip((List<OrderedText>)ImmutableList.of((Object)SNAPSHOT_FIRST_LINE.asOrderedText(), (Object)SNAPSHOT_SECOND_LINE.asOrderedText()), mouseX, mouseY);
                    }
                }
                if (bl) {
                    WorldListWidget.this.setCursor(context);
                }
            } else {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, this.getContentX(), this.getContentY(), 32, 32);
                if (bl) {
                    WorldListWidget.this.setCursor(context);
                }
            }
        }
    }

    private int getTextX() {
        return this.getContentX() + 32 + 3;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.allowConfirmationByKeyboard()) {
            int i = (int)click.x() - this.getContentX();
            int j = (int)click.y() - this.getContentY();
            if (doubled || this.isInside(i, j, 32) && this.parent.worldListType == WorldListWidget.WorldListType.SINGLEPLAYER) {
                this.client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                Consumer<WorldListWidget.WorldEntry> consumer = this.parent.confirmationCallback;
                if (consumer != null) {
                    consumer.accept(this);
                    return true;
                }
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.isEnterOrSpace() && this.allowConfirmationByKeyboard()) {
            this.client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            Consumer<WorldListWidget.WorldEntry> consumer = this.parent.confirmationCallback;
            if (consumer != null) {
                consumer.accept(this);
                return true;
            }
        }
        return super.keyPressed(input);
    }

    public boolean allowConfirmationByKeyboard() {
        return this.level.isSelectable() || this.parent.worldListType == WorldListWidget.WorldListType.UPLOAD_WORLD;
    }

    public void play() {
        if (!this.level.isSelectable()) {
            return;
        }
        if (this.level instanceof LevelSummary.SymlinkLevelSummary) {
            this.client.setScreen(SymlinkWarningScreen.world(() -> this.client.setScreen(this.screen)));
            return;
        }
        this.client.createIntegratedServerLoader().start(this.level.getName(), this.parent::refresh);
    }

    public void deleteIfConfirmed() {
        this.client.setScreen(new ConfirmScreen(confirmed -> {
            if (confirmed) {
                this.client.setScreen(new ProgressScreen(true));
                this.delete();
            }
            this.parent.refresh();
        }, Text.translatable("selectWorld.deleteQuestion"), Text.translatable("selectWorld.deleteWarning", this.level.getDisplayName()), Text.translatable("selectWorld.deleteButton"), ScreenTexts.CANCEL));
    }

    public void delete() {
        LevelStorage levelStorage = this.client.getLevelStorage();
        String string = this.level.getName();
        try (LevelStorage.Session session = levelStorage.createSessionWithoutSymlinkCheck(string);){
            session.deleteSessionLock();
        }
        catch (IOException iOException) {
            SystemToast.addWorldDeleteFailureToast(this.client, string);
            LOGGER.error("Failed to delete world {}", (Object)string, (Object)iOException);
        }
    }

    public void edit() {
        EditWorldScreen editWorldScreen;
        LevelStorage.Session session;
        this.openReadingWorldScreen();
        String string = this.level.getName();
        try {
            session = this.client.getLevelStorage().createSession(string);
        }
        catch (IOException iOException) {
            SystemToast.addWorldAccessFailureToast(this.client, string);
            LOGGER.error("Failed to access level {}", (Object)string, (Object)iOException);
            this.parent.load();
            return;
        }
        catch (SymlinkValidationException symlinkValidationException) {
            LOGGER.warn("{}", (Object)symlinkValidationException.getMessage());
            this.client.setScreen(SymlinkWarningScreen.world(() -> this.client.setScreen(this.screen)));
            return;
        }
        try {
            editWorldScreen = EditWorldScreen.create(this.client, session, edited -> {
                session.tryClose();
                this.parent.refresh();
            });
        }
        catch (IOException | NbtCrashException | NbtException exception) {
            session.tryClose();
            SystemToast.addWorldAccessFailureToast(this.client, string);
            LOGGER.error("Failed to load world data {}", (Object)string, (Object)exception);
            this.parent.load();
            return;
        }
        this.client.setScreen(editWorldScreen);
    }

    public void recreate() {
        this.openReadingWorldScreen();
        try (LevelStorage.Session session = this.client.getLevelStorage().createSession(this.level.getName());){
            Pair<LevelInfo, GeneratorOptionsHolder> pair = this.client.createIntegratedServerLoader().loadForRecreation(session);
            LevelInfo levelInfo = (LevelInfo)pair.getFirst();
            GeneratorOptionsHolder generatorOptionsHolder = (GeneratorOptionsHolder)pair.getSecond();
            Path path = CreateWorldScreen.copyDataPack(session.getDirectory(WorldSavePath.DATAPACKS), this.client);
            generatorOptionsHolder.initializeIndexedFeaturesLists();
            if (generatorOptionsHolder.generatorOptions().isLegacyCustomizedType()) {
                this.client.setScreen(new ConfirmScreen(confirmed -> this.client.setScreen(confirmed ? CreateWorldScreen.create(this.client, this.parent::refresh, levelInfo, generatorOptionsHolder, path) : this.screen), Text.translatable("selectWorld.recreate.customized.title"), Text.translatable("selectWorld.recreate.customized.text"), ScreenTexts.PROCEED, ScreenTexts.CANCEL));
            } else {
                this.client.setScreen(CreateWorldScreen.create(this.client, this.parent::refresh, levelInfo, generatorOptionsHolder, path));
            }
        }
        catch (SymlinkValidationException symlinkValidationException) {
            LOGGER.warn("{}", (Object)symlinkValidationException.getMessage());
            this.client.setScreen(SymlinkWarningScreen.world(() -> this.client.setScreen(this.screen)));
        }
        catch (Exception exception) {
            LOGGER.error("Unable to recreate world", (Throwable)exception);
            this.client.setScreen(new NoticeScreen(() -> this.client.setScreen(this.screen), Text.translatable("selectWorld.recreate.error.title"), (Text)Text.translatable("selectWorld.recreate.error.text")));
        }
    }

    private void openReadingWorldScreen() {
        this.client.setScreenAndRender(new MessageScreen(Text.translatable("selectWorld.data_read")));
    }

    private void loadIcon() {
        boolean bl;
        boolean bl2 = bl = this.iconPath != null && Files.isRegularFile(this.iconPath, new LinkOption[0]);
        if (bl) {
            try (InputStream inputStream = Files.newInputStream(this.iconPath, new OpenOption[0]);){
                this.icon.load(NativeImage.read(inputStream));
            }
            catch (Throwable throwable) {
                LOGGER.error("Invalid icon for world {}", (Object)this.level.getName(), (Object)throwable);
                this.iconPath = null;
            }
        } else {
            this.icon.destroy();
        }
    }

    @Override
    public void close() {
        if (!this.icon.isClosed()) {
            this.icon.close();
        }
    }

    public String getLevelDisplayName() {
        return this.level.getDisplayName();
    }

    @Override
    public LevelSummary getLevel() {
        return this.level;
    }
}
