/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.renderer.v1.sprite.FabricStitchResult
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.texture;

import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.sprite.FabricStitchResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.TextureFilteringMode;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteOpener;
import net.minecraft.client.texture.TextureStitcher;
import net.minecraft.client.texture.TextureStitcherCannotFitException;
import net.minecraft.client.texture.atlas.AtlasLoader;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SpriteLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Identifier id;
    private final int maxTextureSize;

    public SpriteLoader(Identifier id, int maxTextureSize) {
        this.id = id;
        this.maxTextureSize = maxTextureSize;
    }

    public static SpriteLoader fromAtlas(SpriteAtlasTexture atlasTexture) {
        return new SpriteLoader(atlasTexture.getId(), atlasTexture.getMaxTextureSize());
    }

    private StitchResult stitch(List<SpriteContents> sprites, int mipLevel, Executor executor) {
        try (ScopedProfiler scopedProfiler = Profilers.get().scoped(() -> "stitch " + String.valueOf(this.id));){
            int l;
            int i = this.maxTextureSize;
            int j = Integer.MAX_VALUE;
            int k = 1 << mipLevel;
            for (SpriteContents spriteContents : sprites) {
                j = Math.min(j, Math.min(spriteContents.getWidth(), spriteContents.getHeight()));
                l = Math.min(Integer.lowestOneBit(spriteContents.getWidth()), Integer.lowestOneBit(spriteContents.getHeight()));
                if (l >= k) continue;
                LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", new Object[]{spriteContents.getId(), spriteContents.getWidth(), spriteContents.getHeight(), MathHelper.floorLog2(k), MathHelper.floorLog2(l)});
                k = l;
            }
            int m = Math.min(j, k);
            int n = MathHelper.floorLog2(m);
            if (n < mipLevel) {
                LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", new Object[]{this.id, mipLevel, n, m});
                l = n;
            } else {
                l = mipLevel;
            }
            GameOptions gameOptions = MinecraftClient.getInstance().options;
            int o = l == 0 || gameOptions.getTextureFiltering().getValue() != TextureFilteringMode.ANISOTROPIC ? 0 : gameOptions.getMaxAnisotropy().getValue();
            TextureStitcher<SpriteContents> textureStitcher = new TextureStitcher<SpriteContents>(i, i, l, o);
            for (SpriteContents spriteContents2 : sprites) {
                textureStitcher.add(spriteContents2);
            }
            try {
                textureStitcher.stitch();
            }
            catch (TextureStitcherCannotFitException textureStitcherCannotFitException) {
                CrashReport crashReport = CrashReport.create(textureStitcherCannotFitException, "Stitching");
                CrashReportSection crashReportSection = crashReport.addElement("Stitcher");
                crashReportSection.add("Sprites", textureStitcherCannotFitException.getSprites().stream().map(sprite -> String.format(Locale.ROOT, "%s[%dx%d]", sprite.getId(), sprite.getWidth(), sprite.getHeight())).collect(Collectors.joining(",")));
                crashReportSection.add("Max Texture Size", i);
                throw new CrashException(crashReport);
            }
            int p = textureStitcher.getWidth();
            int q = textureStitcher.getHeight();
            Map<Identifier, Sprite> map = this.collectStitchedSprites(textureStitcher, p, q);
            Sprite sprite2 = map.get(MissingSprite.getMissingSpriteId());
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> map.values().forEach(sprite -> sprite.getContents().generateMipmaps(l)), executor);
            StitchResult stitchResult = new StitchResult(p, q, l, sprite2, map, completableFuture);
            return stitchResult;
        }
    }

    /*
     * Issues handling annotations - annotations may be inaccurate
     */
    private static CompletableFuture<List<SpriteContents>> loadAll(SpriteOpener opener, List<AtlasSource.SpriteSource> sources, Executor executor) {
        List<@Nullable CompletableFuture> list = sources.stream().map(source -> CompletableFuture.supplyAsync(() -> source.load(opener), executor)).toList();
        return Util.combineSafe(list).thenApply(sprites -> sprites.stream().filter(Objects::nonNull).toList());
    }

    public CompletableFuture<StitchResult> load(ResourceManager resourceManager, Identifier path, int mipLevel, Executor executor, Set<ResourceMetadataSerializer<?>> additionalMetadata) {
        SpriteOpener spriteOpener = SpriteOpener.create(additionalMetadata);
        return ((CompletableFuture)CompletableFuture.supplyAsync(() -> AtlasLoader.of(resourceManager, path).loadSources(resourceManager), executor).thenCompose(sources -> SpriteLoader.loadAll(spriteOpener, sources, executor))).thenApply(sprites -> this.stitch((List<SpriteContents>)sprites, mipLevel, executor));
    }

    private Map<Identifier, Sprite> collectStitchedSprites(TextureStitcher<SpriteContents> stitcher, int atlasWidth, int atlasHeight) {
        HashMap<Identifier, Sprite> map = new HashMap<Identifier, Sprite>();
        stitcher.getStitchedSprites((info, x, y, padding) -> map.put(info.getId(), new Sprite(this.id, (SpriteContents)info, atlasWidth, atlasHeight, x, y, padding)));
        return map;
    }

    @Environment(value=EnvType.CLIENT)
    public record StitchResult(int width, int height, int mipLevel, Sprite missing, Map<Identifier, Sprite> sprites, CompletableFuture<Void> readyForUpload) implements FabricStitchResult
    {
        public @Nullable Sprite getSprite(Identifier id) {
            return this.sprites.get(id);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{StitchResult.class, "width;height;mipLevel;missing;regions;readyForUpload", "width", "height", "mipLevel", "missing", "sprites", "readyForUpload"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{StitchResult.class, "width;height;mipLevel;missing;regions;readyForUpload", "width", "height", "mipLevel", "missing", "sprites", "readyForUpload"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{StitchResult.class, "width;height;mipLevel;missing;regions;readyForUpload", "width", "height", "mipLevel", "missing", "sprites", "readyForUpload"}, this, object);
        }
    }
}
