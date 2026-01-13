/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.RateLimiter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.TitleScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.client.realms.SizeUnit
 *  net.minecraft.client.realms.dto.RealmsSettingDto
 *  net.minecraft.client.realms.dto.RealmsSlot
 *  net.minecraft.client.realms.dto.RealmsWorldOptions
 *  net.minecraft.client.realms.exception.RealmsUploadException
 *  net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen
 *  net.minecraft.client.realms.gui.screen.RealmsCreateWorldScreen
 *  net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen
 *  net.minecraft.client.realms.gui.screen.RealmsMainScreen
 *  net.minecraft.client.realms.gui.screen.RealmsScreen
 *  net.minecraft.client.realms.gui.screen.RealmsUploadScreen
 *  net.minecraft.client.realms.task.LongRunningTask
 *  net.minecraft.client.realms.task.SwitchSlotTask
 *  net.minecraft.client.realms.task.WorldCreationTask
 *  net.minecraft.client.realms.util.RealmsUploader
 *  net.minecraft.client.realms.util.UploadProgress
 *  net.minecraft.client.realms.util.UploadProgressTracker
 *  net.minecraft.client.util.NarratorManager
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.world.level.LevelInfo
 *  net.minecraft.world.level.storage.LevelSummary
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.realms.SizeUnit;
import net.minecraft.client.realms.dto.RealmsSettingDto;
import net.minecraft.client.realms.dto.RealmsSlot;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.exception.RealmsUploadException;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsCreateWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.client.realms.task.SwitchSlotTask;
import net.minecraft.client.realms.task.WorldCreationTask;
import net.minecraft.client.realms.util.RealmsUploader;
import net.minecraft.client.realms.util.UploadProgress;
import net.minecraft.client.realms.util.UploadProgressTracker;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelSummary;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsUploadScreen
extends RealmsScreen
implements UploadProgressTracker {
    private static final int field_41776 = 200;
    private static final int field_41773 = 80;
    private static final int field_41774 = 95;
    private static final int field_41775 = 1;
    private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
    private static final Text VERIFYING_TEXT = Text.translatable((String)"mco.upload.verifying");
    private final RealmsCreateWorldScreen parent;
    private final LevelSummary selectedLevel;
    private final @Nullable WorldCreationTask creationTask;
    private final long worldId;
    private final int slotId;
    final AtomicReference<@Nullable RealmsUploader> uploader = new AtomicReference();
    private final UploadProgress uploadProgress;
    private final RateLimiter narrationRateLimiter;
    private volatile Text @Nullable [] statusTexts;
    private volatile Text status = Text.translatable((String)"mco.upload.preparing");
    private volatile @Nullable String progress;
    private volatile boolean cancelled;
    private volatile boolean uploadFinished;
    private volatile boolean showDots = true;
    private volatile boolean uploadStarted;
    private @Nullable ButtonWidget backButton;
    private @Nullable ButtonWidget cancelButton;
    private int animTick;
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this);

    public RealmsUploadScreen(@Nullable WorldCreationTask creationTask, long worldId, int slotId, RealmsCreateWorldScreen parent, LevelSummary selectedLevel) {
        super(NarratorManager.EMPTY);
        this.creationTask = creationTask;
        this.worldId = worldId;
        this.slotId = slotId;
        this.parent = parent;
        this.selectedLevel = selectedLevel;
        this.uploadProgress = new UploadProgress();
        this.narrationRateLimiter = RateLimiter.create((double)0.1f);
    }

    public void init() {
        this.backButton = (ButtonWidget)this.layout.addFooter((Widget)ButtonWidget.builder((Text)ScreenTexts.BACK, button -> this.onBack()).build());
        this.backButton.visible = false;
        this.cancelButton = (ButtonWidget)this.layout.addFooter((Widget)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> this.onCancel()).build());
        if (!this.uploadStarted) {
            if (this.parent.slot == -1) {
                this.uploadStarted = true;
                this.upload();
            } else {
                ArrayList<Object> list = new ArrayList<Object>();
                if (this.creationTask != null) {
                    list.add(this.creationTask);
                }
                list.add(new SwitchSlotTask(this.worldId, this.parent.slot, () -> {
                    if (!this.uploadStarted) {
                        this.uploadStarted = true;
                        this.client.execute(() -> {
                            this.client.setScreen((Screen)this);
                            this.upload();
                        });
                    }
                }));
                this.client.setScreen((Screen)new RealmsLongRunningMcoTaskScreen((Screen)this.parent, list.toArray(new LongRunningTask[0])));
            }
        }
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
    }

    private void onBack() {
        this.client.setScreen((Screen)new RealmsConfigureWorldScreen(new RealmsMainScreen((Screen)new TitleScreen()), this.worldId));
    }

    private void onCancel() {
        this.cancelled = true;
        RealmsUploader realmsUploader = (RealmsUploader)this.uploader.get();
        if (realmsUploader != null) {
            realmsUploader.cancel();
        } else {
            this.client.setScreen((Screen)this.parent);
        }
    }

    public boolean keyPressed(KeyInput input) {
        if (input.key() == 256) {
            if (this.showDots) {
                this.onCancel();
            } else {
                this.onBack();
            }
            return true;
        }
        return super.keyPressed(input);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        Text[] texts;
        super.render(context, mouseX, mouseY, deltaTicks);
        if (!this.uploadFinished && this.uploadProgress.hasWrittenBytes() && this.uploadProgress.hasWrittenAllBytes() && this.cancelButton != null) {
            this.status = VERIFYING_TEXT;
            this.cancelButton.active = false;
        }
        context.drawCenteredTextWithShadow(this.textRenderer, this.status, this.width / 2, 50, -1);
        if (this.showDots) {
            context.drawTextWithShadow(this.textRenderer, DOTS[this.animTick / 10 % DOTS.length], this.width / 2 + this.textRenderer.getWidth((StringVisitable)this.status) / 2 + 5, 50, -1);
        }
        if (this.uploadProgress.hasWrittenBytes() && !this.cancelled) {
            this.drawProgressBar(context);
            this.drawUploadSpeed(context);
        }
        if ((texts = this.statusTexts) != null) {
            for (int i = 0; i < texts.length; ++i) {
                context.drawCenteredTextWithShadow(this.textRenderer, texts[i], this.width / 2, 110 + 12 * i, -65536);
            }
        }
    }

    private void drawProgressBar(DrawContext context) {
        double d = this.uploadProgress.getFractionBytesWritten();
        this.progress = String.format(Locale.ROOT, "%.1f", d * 100.0);
        int i = (this.width - 200) / 2;
        int j = i + (int)Math.round(200.0 * d);
        context.fill(i - 1, 79, j + 1, 96, -1);
        context.fill(i, 80, j, 95, -8355712);
        context.drawCenteredTextWithShadow(this.textRenderer, (Text)Text.translatable((String)"mco.upload.percent", (Object[])new Object[]{this.progress}), this.width / 2, 84, -1);
    }

    private void drawUploadSpeed(DrawContext context) {
        this.drawUploadSpeed0(context, this.uploadProgress.getBytesPerSecond());
    }

    private void drawUploadSpeed0(DrawContext context, long bytesPerSecond) {
        String string = this.progress;
        if (bytesPerSecond > 0L && string != null) {
            int i = this.textRenderer.getWidth(string);
            String string2 = "(" + SizeUnit.getUserFriendlyString((long)bytesPerSecond) + "/s)";
            context.drawTextWithShadow(this.textRenderer, string2, this.width / 2 + i / 2 + 15, 84, -1);
        }
    }

    public void tick() {
        super.tick();
        ++this.animTick;
        this.uploadProgress.tick();
        if (this.narrationRateLimiter.tryAcquire(1)) {
            Text text = this.getNarration();
            this.client.getNarratorManager().narrateSystemImmediately(text);
        }
    }

    private Text getNarration() {
        Text[] texts;
        ArrayList list = Lists.newArrayList();
        list.add(this.status);
        if (this.progress != null) {
            list.add(Text.translatable((String)"mco.upload.percent", (Object[])new Object[]{this.progress}));
        }
        if ((texts = this.statusTexts) != null) {
            list.addAll(Arrays.asList(texts));
        }
        return ScreenTexts.joinLines((Collection)list);
    }

    private void upload() {
        RealmsWorldOptions realmsWorldOptions;
        RealmsSlot realmsSlot;
        Path path = this.client.runDirectory.toPath().resolve("saves").resolve(this.selectedLevel.getName());
        RealmsUploader realmsUploader = new RealmsUploader(path, realmsSlot = new RealmsSlot(this.slotId, realmsWorldOptions = RealmsWorldOptions.create((LevelInfo)this.selectedLevel.getLevelInfo(), (String)this.selectedLevel.getVersionInfo().getVersionName()), List.of(RealmsSettingDto.ofHardcore((boolean)this.selectedLevel.getLevelInfo().isHardcore()))), this.client.getSession(), this.worldId, (UploadProgressTracker)this);
        if (!this.uploader.compareAndSet(null, realmsUploader)) {
            throw new IllegalStateException("Tried to start uploading but was already uploading");
        }
        realmsUploader.upload().handleAsync((v, throwable) -> {
            if (throwable != null) {
                if (throwable instanceof CompletionException) {
                    CompletionException completionException = (CompletionException)throwable;
                    throwable = completionException.getCause();
                }
                if (throwable instanceof RealmsUploadException) {
                    RealmsUploadException realmsUploadException = (RealmsUploadException)throwable;
                    if (realmsUploadException.getStatus() != null) {
                        this.status = realmsUploadException.getStatus();
                    }
                    this.setStatusTexts(realmsUploadException.getStatusTexts());
                } else {
                    this.status = Text.translatable((String)"mco.upload.failed", (Object[])new Object[]{throwable.getMessage()});
                }
            } else {
                this.status = Text.translatable((String)"mco.upload.done");
                if (this.backButton != null) {
                    this.backButton.setMessage(ScreenTexts.DONE);
                }
            }
            this.uploadFinished = true;
            this.showDots = false;
            if (this.backButton != null) {
                this.backButton.visible = true;
            }
            if (this.cancelButton != null) {
                this.cancelButton.visible = false;
            }
            this.uploader.set(null);
            return null;
        }, (Executor)this.client);
    }

    private void setStatusTexts(Text ... statusTexts) {
        this.statusTexts = statusTexts;
    }

    public UploadProgress getUploadProgress() {
        return this.uploadProgress;
    }

    public void updateProgressDisplay() {
        this.status = Text.translatable((String)"mco.upload.uploading", (Object[])new Object[]{this.selectedLevel.getDisplayName()});
    }
}

