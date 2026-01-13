/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsSlot;
import net.minecraft.client.realms.dto.WorldDownload;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.RealmsPopups;
import net.minecraft.client.realms.gui.RealmsWorldSlotButton;
import net.minecraft.client.realms.gui.screen.RealmsDownloadLatestWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.task.OpenServerTask;
import net.minecraft.client.realms.task.SwitchSlotTask;
import net.minecraft.client.realms.util.RealmsTextureManager;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsBrokenWorldScreen
extends RealmsScreen {
    private static final Identifier SLOT_FRAME_TEXTURE = Identifier.ofVanilla("widget/slot_frame");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_32120 = 80;
    private final Screen parent;
    private @Nullable RealmsServer serverData;
    private final long serverId;
    private final Text[] message = new Text[]{Text.translatable("mco.brokenworld.message.line1"), Text.translatable("mco.brokenworld.message.line2")};
    private int left_x;
    private final List<Integer> slotsThatHasBeenDownloaded = Lists.newArrayList();
    private int animTick;

    public RealmsBrokenWorldScreen(Screen parent, long serverId, boolean minigame) {
        super(minigame ? Text.translatable("mco.brokenworld.minigame.title") : Text.translatable("mco.brokenworld.title"));
        this.parent = parent;
        this.serverId = serverId;
    }

    @Override
    public void init() {
        this.left_x = this.width / 2 - 150;
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).dimensions((this.width - 150) / 2, RealmsBrokenWorldScreen.row(13) - 5, 150, 20).build());
        if (this.serverData == null) {
            this.fetchServerData(this.serverId);
        } else {
            this.addButtons();
        }
    }

    @Override
    public Text getNarratedTitle() {
        return Texts.join(Stream.concat(Stream.of(this.title), Stream.of(this.message)).collect(Collectors.toList()), ScreenTexts.SPACE);
    }

    private void addButtons() {
        for (Map.Entry<Integer, RealmsSlot> entry : this.serverData.slots.entrySet()) {
            ButtonWidget buttonWidget;
            boolean bl;
            int i = entry.getKey();
            boolean bl2 = bl = i != this.serverData.activeSlot || this.serverData.isMinigame();
            if (bl) {
                buttonWidget = ButtonWidget.builder(Text.translatable("mco.brokenworld.play"), button -> this.client.setScreen(new RealmsLongRunningMcoTaskScreen(this.parent, new SwitchSlotTask(this.serverData.id, i, this::play)))).dimensions(this.getFramePositionX(i), RealmsBrokenWorldScreen.row(8), 80, 20).build();
                buttonWidget.active = !this.serverData.slots.get((Object)Integer.valueOf((int)i)).options.empty;
            } else {
                buttonWidget = ButtonWidget.builder(Text.translatable("mco.brokenworld.download"), button -> this.client.setScreen(RealmsPopups.createInfoPopup(this, Text.translatable("mco.configure.world.restore.download.question.line1"), popupScreen -> this.downloadWorld(i)))).dimensions(this.getFramePositionX(i), RealmsBrokenWorldScreen.row(8), 80, 20).build();
            }
            if (this.slotsThatHasBeenDownloaded.contains(i)) {
                buttonWidget.active = false;
                buttonWidget.setMessage(Text.translatable("mco.brokenworld.downloaded"));
            }
            this.addDrawableChild(buttonWidget);
        }
    }

    @Override
    public void tick() {
        ++this.animTick;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 17, -1);
        for (int i = 0; i < this.message.length; ++i) {
            context.drawCenteredTextWithShadow(this.textRenderer, this.message[i], this.width / 2, RealmsBrokenWorldScreen.row(-1) + 3 + i * 12, -6250336);
        }
        if (this.serverData == null) {
            return;
        }
        for (Map.Entry<Integer, RealmsSlot> entry : this.serverData.slots.entrySet()) {
            if (entry.getValue().options.templateImage != null && entry.getValue().options.templateId != -1L) {
                this.drawSlotFrame(context, this.getFramePositionX(entry.getKey()), RealmsBrokenWorldScreen.row(1) + 5, mouseX, mouseY, this.serverData.activeSlot == entry.getKey() && !this.isMinigame(), entry.getValue().options.getSlotName(entry.getKey()), entry.getKey(), entry.getValue().options.templateId, entry.getValue().options.templateImage, entry.getValue().options.empty);
                continue;
            }
            this.drawSlotFrame(context, this.getFramePositionX(entry.getKey()), RealmsBrokenWorldScreen.row(1) + 5, mouseX, mouseY, this.serverData.activeSlot == entry.getKey() && !this.isMinigame(), entry.getValue().options.getSlotName(entry.getKey()), entry.getKey(), -1L, null, entry.getValue().options.empty);
        }
    }

    private int getFramePositionX(int i) {
        return this.left_x + (i - 1) * 110;
    }

    public Screen createErrorScreen(RealmsServiceException error) {
        return new RealmsGenericErrorScreen(error, this.parent);
    }

    private void fetchServerData(long worldId) {
        RealmsUtil.runAsync(client -> client.getOwnWorld(worldId), RealmsUtil.openingScreenAndLogging(this::createErrorScreen, "Couldn't get own world")).thenAcceptAsync(serverData -> {
            this.serverData = serverData;
            this.addButtons();
        }, (Executor)this.client);
    }

    public void play() {
        new Thread(() -> {
            RealmsClient realmsClient = RealmsClient.create();
            if (this.serverData.state == RealmsServer.State.CLOSED) {
                this.client.execute(() -> this.client.setScreen(new RealmsLongRunningMcoTaskScreen(this, new OpenServerTask(this.serverData, this, true, this.client))));
            } else {
                try {
                    RealmsServer realmsServer = realmsClient.getOwnWorld(this.serverId);
                    this.client.execute(() -> RealmsMainScreen.play(realmsServer, this));
                }
                catch (RealmsServiceException realmsServiceException) {
                    LOGGER.error("Couldn't get own world", (Throwable)realmsServiceException);
                    this.client.execute(() -> this.client.setScreen(this.createErrorScreen(realmsServiceException)));
                }
            }
        }).start();
    }

    private void downloadWorld(int slotId) {
        RealmsClient realmsClient = RealmsClient.create();
        try {
            WorldDownload worldDownload = realmsClient.download(this.serverData.id, slotId);
            RealmsDownloadLatestWorldScreen realmsDownloadLatestWorldScreen = new RealmsDownloadLatestWorldScreen(this, worldDownload, this.serverData.getWorldName(slotId), successful -> {
                if (successful) {
                    this.slotsThatHasBeenDownloaded.add(slotId);
                    this.clearChildren();
                    this.addButtons();
                } else {
                    this.client.setScreen(this);
                }
            });
            this.client.setScreen(realmsDownloadLatestWorldScreen);
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't download world data", (Throwable)realmsServiceException);
            this.client.setScreen(new RealmsGenericErrorScreen(realmsServiceException, (Screen)this));
        }
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    private boolean isMinigame() {
        return this.serverData != null && this.serverData.isMinigame();
    }

    private void drawSlotFrame(DrawContext context, int x, int y, int mouseX, int mouseY, boolean activeSlot, String slotName, int slotId, long templateId, @Nullable String templateImage, boolean empty) {
        Identifier identifier = empty ? RealmsWorldSlotButton.EMPTY_FRAME : (templateImage != null && templateId != -1L ? RealmsTextureManager.getTextureId(String.valueOf(templateId), templateImage) : (slotId == 1 ? RealmsWorldSlotButton.PANORAMA_0 : (slotId == 2 ? RealmsWorldSlotButton.PANORAMA_2 : (slotId == 3 ? RealmsWorldSlotButton.PANORAMA_3 : RealmsTextureManager.getTextureId(String.valueOf(this.serverData.minigameId), this.serverData.minigameImage)))));
        if (activeSlot) {
            float f = 0.9f + 0.1f * MathHelper.cos((float)this.animTick * 0.2f);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, identifier, x + 3, y + 3, 0.0f, 0.0f, 74, 74, 74, 74, 74, 74, ColorHelper.fromFloats(1.0f, f, f, f));
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_FRAME_TEXTURE, x, y, 80, 80);
        } else {
            int i = ColorHelper.fromFloats(1.0f, 0.56f, 0.56f, 0.56f);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, identifier, x + 3, y + 3, 0.0f, 0.0f, 74, 74, 74, 74, 74, 74, i);
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_FRAME_TEXTURE, x, y, 80, 80, i);
        }
        context.drawCenteredTextWithShadow(this.textRenderer, slotName, x + 40, y + 66, -1);
    }
}
