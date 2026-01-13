/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
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

@Environment(value=EnvType.CLIENT)
public class LevelLoadingScreen
extends Screen {
    private static final Text DOWNLOADING_TERRAIN_TEXT = Text.translatable("multiplayer.downloadingTerrain");
    private static final Text READY_TO_PLAY_MESSAGE = Text.translatable("narrator.ready_to_play");
    private static final long NARRATION_DELAY = 2000L;
    private static final int field_61630 = 200;
    private ClientChunkLoadProgress chunkLoadProgress;
    private float loadProgress;
    private long lastNarrationTime = -1L;
    private WorldEntryReason reason;
    private @Nullable Sprite netherPortalSprite;
    private static final Object2IntMap<ChunkStatus> STATUS_TO_COLOR = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), map -> {
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

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected boolean hasUsageText() {
        return false;
    }

    @Override
    protected void addElementNarrations(NarrationMessageBuilder builder) {
        if (this.chunkLoadProgress.hasProgress()) {
            builder.put(NarrationPart.TITLE, (Text)Text.translatable("loading.progress", MathHelper.floor(this.chunkLoadProgress.getLoadProgress() * 100.0f)));
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.loadProgress += (this.chunkLoadProgress.getLoadProgress() - this.loadProgress) * 0.2f;
        if (this.chunkLoadProgress.isDone()) {
            this.close();
        }
    }

    @Override
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
            LevelLoadingScreen.drawChunkMap(context, i, j, 2, 0, chunkLoadMap);
            m = j - chunkLoadMap.getRadius() * 2 - this.textRenderer.fontHeight * 3;
        } else {
            m = j - 50;
        }
        context.drawCenteredTextWithShadow(this.textRenderer, DOWNLOADING_TERRAIN_TEXT, i, m, -1);
        if (this.chunkLoadProgress.hasProgress()) {
            this.drawLoadingBar(context, i - 100, m + this.textRenderer.fontHeight + 3, 200, 2, this.loadProgress);
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
                context.fill(p, q, p + chunkLength, q + chunkLength, ColorHelper.fullAlpha(STATUS_TO_COLOR.getInt((Object)chunkStatus)));
            }
        }
    }

    @Override
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
                TextureSetup textureSetup = TextureSetup.of(abstractTexture.getGlTextureView(), abstractTexture.getSampler(), abstractTexture2.getGlTextureView(), abstractTexture2.getSampler());
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

    @Override
    public void close() {
        this.client.getNarratorManager().narrateSystemImmediately(READY_TO_PLAY_MESSAGE);
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class WorldEntryReason
    extends Enum<WorldEntryReason> {
        public static final /* enum */ WorldEntryReason NETHER_PORTAL = new WorldEntryReason();
        public static final /* enum */ WorldEntryReason END_PORTAL = new WorldEntryReason();
        public static final /* enum */ WorldEntryReason OTHER = new WorldEntryReason();
        private static final /* synthetic */ WorldEntryReason[] field_51490;

        public static WorldEntryReason[] values() {
            return (WorldEntryReason[])field_51490.clone();
        }

        public static WorldEntryReason valueOf(String string) {
            return Enum.valueOf(WorldEntryReason.class, string);
        }

        private static /* synthetic */ WorldEntryReason[] method_59839() {
            return new WorldEntryReason[]{NETHER_PORTAL, END_PORTAL, OTHER};
        }

        static {
            field_51490 = WorldEntryReason.method_59839();
        }
    }
}
