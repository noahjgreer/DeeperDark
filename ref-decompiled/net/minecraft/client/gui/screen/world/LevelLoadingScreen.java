/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.GpuSampler
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.hud.debug.DebugHudEntries
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.screen.narration.NarrationPart
 *  net.minecraft.client.gui.screen.world.LevelLoadingScreen
 *  net.minecraft.client.gui.screen.world.LevelLoadingScreen$WorldEntryReason
 *  net.minecraft.client.render.block.entity.AbstractEndPortalBlockEntityRenderer
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.texture.TextureManager
 *  net.minecraft.client.texture.TextureSetup
 *  net.minecraft.client.util.NarratorManager
 *  net.minecraft.client.world.ClientChunkLoadProgress
 *  net.minecraft.text.Text
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.chunk.ChunkLoadMap
 *  net.minecraft.world.chunk.ChunkStatus
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import com.mojang.blaze3d.textures.GpuTextureView;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import net.minecraft.client.render.block.entity.AbstractEndPortalBlockEntityRenderer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.world.ClientChunkLoadProgress;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkLoadMap;
import net.minecraft.world.chunk.ChunkStatus;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class LevelLoadingScreen
extends Screen {
    private static final Text DOWNLOADING_TERRAIN_TEXT = Text.translatable((String)"multiplayer.downloadingTerrain");
    private static final Text READY_TO_PLAY_MESSAGE = Text.translatable((String)"narrator.ready_to_play");
    private static final long NARRATION_DELAY = 2000L;
    private static final int field_61630 = 200;
    private ClientChunkLoadProgress chunkLoadProgress;
    private float loadProgress;
    private long lastNarrationTime = -1L;
    private WorldEntryReason reason;
    private @Nullable Sprite netherPortalSprite;
    private static final Object2IntMap<ChunkStatus> STATUS_TO_COLOR = (Object2IntMap)Util.make((Object)new Object2IntOpenHashMap(), map -> {
        map.defaultReturnValue(0);
        map.put((Object)ChunkStatus.EMPTY, 0x545454);
        map.put((Object)ChunkStatus.STRUCTURE_STARTS, 0x999999);
        map.put((Object)ChunkStatus.STRUCTURE_REFERENCES, 6250897);
        map.put((Object)ChunkStatus.BIOMES, 8434258);
        map.put((Object)ChunkStatus.NOISE, 0xD1D1D1);
        map.put((Object)ChunkStatus.SURFACE, 7497737);
        map.put((Object)ChunkStatus.CARVERS, 3159410);
        map.put((Object)ChunkStatus.FEATURES, 2213376);
        map.put((Object)ChunkStatus.INITIALIZE_LIGHT, 0xCCCCCC);
        map.put((Object)ChunkStatus.LIGHT, 16769184);
        map.put((Object)ChunkStatus.SPAWN, 15884384);
        map.put((Object)ChunkStatus.FULL, 0xFFFFFF);
    });

    public LevelLoadingScreen(ClientChunkLoadProgress progressProvider, WorldEntryReason reason) {
        super(NarratorManager.EMPTY);
        this.chunkLoadProgress = progressProvider;
        this.reason = reason;
    }

    public void init(ClientChunkLoadProgress chunkLoadProgress, WorldEntryReason reason) {
        this.chunkLoadProgress = chunkLoadProgress;
        this.reason = reason;
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    protected boolean hasUsageText() {
        return false;
    }

    protected void addElementNarrations(NarrationMessageBuilder builder) {
        if (this.chunkLoadProgress.hasProgress()) {
            builder.put(NarrationPart.TITLE, (Text)Text.translatable((String)"loading.progress", (Object[])new Object[]{MathHelper.floor((float)(this.chunkLoadProgress.getLoadProgress() * 100.0f))}));
        }
    }

    public void tick() {
        super.tick();
        this.loadProgress += (this.chunkLoadProgress.getLoadProgress() - this.loadProgress) * 0.2f;
        if (this.chunkLoadProgress.isDone()) {
            this.close();
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int m;
        super.render(context, mouseX, mouseY, deltaTicks);
        long l = Util.getMeasuringTimeMs();
        if (l - this.lastNarrationTime > 2000L) {
            this.lastNarrationTime = l;
            this.narrateScreenIfNarrationEnabled(true);
        }
        int i = this.width / 2;
        int j = this.height / 2;
        ChunkLoadMap chunkLoadMap = this.chunkLoadProgress.getChunkLoadMap();
        if (chunkLoadMap != null) {
            int k = 2;
            LevelLoadingScreen.drawChunkMap((DrawContext)context, (int)i, (int)j, (int)2, (int)0, (ChunkLoadMap)chunkLoadMap);
            int n = j - chunkLoadMap.getRadius() * 2;
            Objects.requireNonNull(this.textRenderer);
            m = n - 9 * 3;
        } else {
            m = j - 50;
        }
        context.drawCenteredTextWithShadow(this.textRenderer, DOWNLOADING_TERRAIN_TEXT, i, m, -1);
        if (this.chunkLoadProgress.hasProgress()) {
            Objects.requireNonNull(this.textRenderer);
            this.drawLoadingBar(context, i - 100, m + 9 + 3, 200, 2, this.loadProgress);
        }
    }

    private void drawLoadingBar(DrawContext context, int x1, int y1, int width, int height, float delta) {
        context.fill(x1, y1, x1 + width, y1 + height, -16777216);
        context.fill(x1, y1, x1 + Math.round(delta * (float)width), y1 + height, -16711936);
    }

    public static void drawChunkMap(DrawContext context, int centerX, int centerY, int chunkLength, int chunkGap, ChunkLoadMap map) {
        int n;
        int i = chunkLength + chunkGap;
        int j = map.getRadius() * 2 + 1;
        int k = j * i - chunkGap;
        int l = centerX - k / 2;
        int m = centerY - k / 2;
        if (MinecraftClient.getInstance().debugHudEntryList.isEntryVisible(DebugHudEntries.VISUALIZE_CHUNKS_ON_SERVER)) {
            n = i / 2 + 1;
            context.fill(centerX - n, centerY - n, centerX + n, centerY + n, -65536);
        }
        for (n = 0; n < j; ++n) {
            for (int o = 0; o < j; ++o) {
                ChunkStatus chunkStatus = map.getStatus(n, o);
                int p = l + n * i;
                int q = m + o * i;
                context.fill(p, q, p + chunkLength, q + chunkLength, ColorHelper.fullAlpha((int)STATUS_TO_COLOR.getInt((Object)chunkStatus)));
            }
        }
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        switch (this.reason.ordinal()) {
            case 2: {
                this.renderPanoramaBackground(context, deltaTicks);
                this.applyBlur(context);
                this.renderDarkening(context);
                break;
            }
            case 0: {
                context.drawSpriteStretched(RenderPipelines.GUI_OPAQUE_TEX_BG, this.getNetherPortalSprite(), 0, 0, context.getScaledWindowWidth(), context.getScaledWindowHeight());
                break;
            }
            case 1: {
                TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
                AbstractTexture abstractTexture = textureManager.getTexture(AbstractEndPortalBlockEntityRenderer.SKY_TEXTURE);
                AbstractTexture abstractTexture2 = textureManager.getTexture(AbstractEndPortalBlockEntityRenderer.PORTAL_TEXTURE);
                TextureSetup textureSetup = TextureSetup.of((GpuTextureView)abstractTexture.getGlTextureView(), (GpuSampler)abstractTexture.getSampler(), (GpuTextureView)abstractTexture2.getGlTextureView(), (GpuSampler)abstractTexture2.getSampler());
                context.fill(RenderPipelines.END_PORTAL, textureSetup, 0, 0, this.width, this.height);
            }
        }
    }

    private Sprite getNetherPortalSprite() {
        if (this.netherPortalSprite != null) {
            return this.netherPortalSprite;
        }
        this.netherPortalSprite = this.client.getBlockRenderManager().getModels().getModelParticleSprite(Blocks.NETHER_PORTAL.getDefaultState());
        return this.netherPortalSprite;
    }

    public void close() {
        this.client.getNarratorManager().narrateSystemImmediately(READY_TO_PLAY_MESSAGE);
        super.close();
    }

    public boolean shouldPause() {
        return false;
    }
}

