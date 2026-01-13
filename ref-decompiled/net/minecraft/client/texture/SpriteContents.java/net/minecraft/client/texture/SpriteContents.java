/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.MipmapHelper;
import net.minecraft.client.texture.MipmapStrategy;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.client.texture.TextureStitcher;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.ColorHelper;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SpriteContents
implements TextureStitcher.Stitchable,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int SPRITE_INFO_SIZE = new Std140SizeCalculator().putMat4f().putMat4f().putFloat().putFloat().putInt().get();
    final Identifier id;
    final int width;
    final int height;
    private final NativeImage image;
    NativeImage[] mipmapLevelsImages;
    private final @Nullable Animation animation;
    private final List<ResourceMetadataSerializer.Value<?>> additionalMetadata;
    private final MipmapStrategy strategy;
    private final float cutoffBias;

    public SpriteContents(Identifier id, SpriteDimensions dimensions, NativeImage image) {
        this(id, dimensions, image, Optional.empty(), List.of(), Optional.empty());
    }

    public SpriteContents(Identifier id, SpriteDimensions dimensions, NativeImage image, Optional<AnimationResourceMetadata> animationResourceMetadata, List<ResourceMetadataSerializer.Value<?>> additionalMetadata, Optional<TextureResourceMetadata> metadata) {
        this.id = id;
        this.width = dimensions.width();
        this.height = dimensions.height();
        this.additionalMetadata = additionalMetadata;
        this.animation = animationResourceMetadata.map(animationMetadata -> this.createAnimation(dimensions, image.getWidth(), image.getHeight(), (AnimationResourceMetadata)animationMetadata)).orElse(null);
        this.image = image;
        this.mipmapLevelsImages = new NativeImage[]{this.image};
        this.strategy = metadata.map(TextureResourceMetadata::mipmapStrategy).orElse(MipmapStrategy.AUTO);
        this.cutoffBias = metadata.map(TextureResourceMetadata::alphaCutoffBias).orElse(Float.valueOf(0.0f)).floatValue();
    }

    public void generateMipmaps(int mipmapLevels) {
        try {
            this.mipmapLevelsImages = MipmapHelper.getMipmapLevelsImages(this.id, this.mipmapLevelsImages, mipmapLevels, this.strategy, this.cutoffBias);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Generating mipmaps for frame");
            CrashReportSection crashReportSection = crashReport.addElement("Frame being iterated");
            crashReportSection.add("Sprite name", this.id);
            crashReportSection.add("Sprite size", () -> this.width + " x " + this.height);
            crashReportSection.add("Sprite frames", () -> this.getFrameCount() + " frames");
            crashReportSection.add("Mipmap levels", mipmapLevels);
            crashReportSection.add("Original image size", () -> this.image.getWidth() + "x" + this.image.getHeight());
            throw new CrashException(crashReport);
        }
    }

    private int getFrameCount() {
        return this.animation != null ? this.animation.frames.size() : 1;
    }

    public boolean isAnimated() {
        return this.getFrameCount() > 1;
    }

    private @Nullable Animation createAnimation(SpriteDimensions dimensions, int imageWidth, int imageHeight, AnimationResourceMetadata metadata) {
        ArrayList<AnimationFrame> list;
        int i = imageWidth / dimensions.width();
        int j = imageHeight / dimensions.height();
        int k = i * j;
        int l = metadata.defaultFrameTime();
        if (metadata.frames().isEmpty()) {
            list = new ArrayList<AnimationFrame>(k);
            for (int m = 0; m < k; ++m) {
                list.add(new AnimationFrame(m, l));
            }
        } else {
            List<AnimationFrameResourceMetadata> list2 = metadata.frames().get();
            list = new ArrayList(list2.size());
            for (AnimationFrameResourceMetadata animationFrameResourceMetadata : list2) {
                list.add(new AnimationFrame(animationFrameResourceMetadata.index(), animationFrameResourceMetadata.getTime(l)));
            }
            int n = 0;
            IntOpenHashSet intSet = new IntOpenHashSet();
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                AnimationFrame animationFrame = (AnimationFrame)iterator.next();
                boolean bl = true;
                if (animationFrame.time <= 0) {
                    LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}", new Object[]{this.id, n, animationFrame.time});
                    bl = false;
                }
                if (animationFrame.index < 0 || animationFrame.index >= k) {
                    LOGGER.warn("Invalid frame index on sprite {} frame {}: {}", new Object[]{this.id, n, animationFrame.index});
                    bl = false;
                }
                if (bl) {
                    intSet.add(animationFrame.index);
                } else {
                    iterator.remove();
                }
                ++n;
            }
            int[] is = IntStream.range(0, k).filter(arg_0 -> SpriteContents.method_45813((IntSet)intSet, arg_0)).toArray();
            if (is.length > 0) {
                LOGGER.warn("Unused frames in sprite {}: {}", (Object)this.id, (Object)Arrays.toString(is));
            }
        }
        if (list.size() <= 1) {
            return null;
        }
        return new Animation(List.copyOf(list), i, metadata.interpolate());
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    public IntStream getDistinctFrameCount() {
        return this.animation != null ? this.animation.getDistinctFrameCount() : IntStream.of(1);
    }

    public @Nullable Animator createAnimator(GpuBufferSlice bufferSlice, int animationInfoSize) {
        return this.animation != null ? this.animation.createAnimator(bufferSlice, animationInfoSize) : null;
    }

    public <T> Optional<T> getAdditionalMetadataValue(ResourceMetadataSerializer<T> serializer) {
        for (ResourceMetadataSerializer.Value<?> value : this.additionalMetadata) {
            Optional<T> optional = value.getValueIfMatching(serializer);
            if (!optional.isPresent()) continue;
            return optional;
        }
        return Optional.empty();
    }

    @Override
    public void close() {
        for (NativeImage nativeImage : this.mipmapLevelsImages) {
            nativeImage.close();
        }
    }

    public String toString() {
        return "SpriteContents{name=" + String.valueOf(this.id) + ", frameCount=" + this.getFrameCount() + ", height=" + this.height + ", width=" + this.width + "}";
    }

    public boolean isPixelTransparent(int frame, int x, int y) {
        int i = x;
        int j = y;
        if (this.animation != null) {
            i += this.animation.getFrameX(frame) * this.width;
            j += this.animation.getFrameY(frame) * this.height;
        }
        return ColorHelper.getAlpha(this.image.getColorArgb(i, j)) == 0;
    }

    public void upload(GpuTexture texture, int mipmap) {
        RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, this.mipmapLevelsImages[mipmap], mipmap, 0, 0, 0, this.width >> mipmap, this.height >> mipmap, 0, 0);
    }

    private static /* synthetic */ boolean method_45813(IntSet frameIndex, int i) {
        return !frameIndex.contains(i);
    }

    @Environment(value=EnvType.CLIENT)
    class Animation {
        final List<AnimationFrame> frames;
        private final int frameCount;
        final boolean interpolated;

        Animation(List<AnimationFrame> frames, int frameCount, boolean interpolated) {
            this.frames = frames;
            this.frameCount = frameCount;
            this.interpolated = interpolated;
        }

        int getFrameX(int frame) {
            return frame % this.frameCount;
        }

        int getFrameY(int frame) {
            return frame / this.frameCount;
        }

        public Animator createAnimator(GpuBufferSlice bufferSlice, int animationInfoSize) {
            GpuDevice gpuDevice = RenderSystem.getDevice();
            Int2ObjectOpenHashMap int2ObjectMap = new Int2ObjectOpenHashMap();
            GpuBufferSlice[] gpuBufferSlices = new GpuBufferSlice[SpriteContents.this.mipmapLevelsImages.length];
            for (int i : this.getDistinctFrameCount().toArray()) {
                GpuTexture gpuTexture = gpuDevice.createTexture(() -> String.valueOf(SpriteContents.this.id) + " animation frame " + i, 5, TextureFormat.RGBA8, SpriteContents.this.width, SpriteContents.this.height, 1, SpriteContents.this.mipmapLevelsImages.length + 1);
                int j = this.getFrameX(i) * SpriteContents.this.width;
                int k = this.getFrameY(i) * SpriteContents.this.height;
                for (int l = 0; l < SpriteContents.this.mipmapLevelsImages.length; ++l) {
                    RenderSystem.getDevice().createCommandEncoder().writeToTexture(gpuTexture, SpriteContents.this.mipmapLevelsImages[l], l, 0, 0, 0, SpriteContents.this.width >> l, SpriteContents.this.height >> l, j >> l, k >> l);
                }
                int2ObjectMap.put(i, (Object)RenderSystem.getDevice().createTextureView(gpuTexture));
            }
            for (int m = 0; m < SpriteContents.this.mipmapLevelsImages.length; ++m) {
                gpuBufferSlices[m] = bufferSlice.slice(m * animationInfoSize, animationInfoSize);
            }
            return new Animator(SpriteContents.this, this, (Int2ObjectMap<GpuTextureView>)int2ObjectMap, gpuBufferSlices);
        }

        public IntStream getDistinctFrameCount() {
            return this.frames.stream().mapToInt(frame -> frame.index).distinct();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class AnimationFrame
    extends Record {
        final int index;
        final int time;

        AnimationFrame(int index, int time) {
            this.index = index;
            this.time = time;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{AnimationFrame.class, "index;time", "index", "time"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{AnimationFrame.class, "index;time", "index", "time"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{AnimationFrame.class, "index;time", "index", "time"}, this, object);
        }

        public int index() {
            return this.index;
        }

        public int time() {
            return this.time;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class Animator
    implements AutoCloseable {
        private int frame;
        private int elapsedTimeInFrame;
        private final Animation animation;
        private final Int2ObjectMap<GpuTextureView> textureViewsByFrame;
        private final GpuBufferSlice[] animationInfosByFrame;
        private boolean changedFrame = true;

        Animator(SpriteContents spriteContents, Animation animation, Int2ObjectMap<GpuTextureView> textureViewsByFrame, GpuBufferSlice[] bufferSlices) {
            this.animation = animation;
            this.textureViewsByFrame = textureViewsByFrame;
            this.animationInfosByFrame = bufferSlices;
        }

        public void tick() {
            ++this.elapsedTimeInFrame;
            this.changedFrame = false;
            AnimationFrame animationFrame = this.animation.frames.get(this.frame);
            if (this.elapsedTimeInFrame >= animationFrame.time) {
                int i = animationFrame.index;
                this.frame = (this.frame + 1) % this.animation.frames.size();
                this.elapsedTimeInFrame = 0;
                int j = this.animation.frames.get((int)this.frame).index;
                if (i != j) {
                    this.changedFrame = true;
                }
            }
        }

        public GpuBufferSlice getBufferSlice(int frame) {
            return this.animationInfosByFrame[frame];
        }

        public boolean isDirty() {
            return this.animation.interpolated || this.changedFrame;
        }

        public void upload(RenderPass renderPass, GpuBufferSlice bufferSlice) {
            GpuSampler gpuSampler = RenderSystem.getSamplerCache().get(FilterMode.NEAREST, true);
            List<AnimationFrame> list = this.animation.frames;
            int i = list.get((int)this.frame).index;
            float f = (float)this.elapsedTimeInFrame / (float)this.animation.frames.get((int)this.frame).time;
            int j = (int)(f * 1000.0f);
            if (this.animation.interpolated) {
                int k = list.get((int)((this.frame + 1) % list.size())).index;
                renderPass.setPipeline(RenderPipelines.ANIMATE_SPRITE_INTERPOLATE);
                renderPass.bindTexture("CurrentSprite", (GpuTextureView)this.textureViewsByFrame.get(i), gpuSampler);
                renderPass.bindTexture("NextSprite", (GpuTextureView)this.textureViewsByFrame.get(k), gpuSampler);
            } else if (this.changedFrame) {
                renderPass.setPipeline(RenderPipelines.ANIMATE_SPRITE_BLIT);
                renderPass.bindTexture("Sprite", (GpuTextureView)this.textureViewsByFrame.get(i), gpuSampler);
            }
            renderPass.setUniform("SpriteAnimationInfo", bufferSlice);
            renderPass.draw(j << 3, 6);
        }

        @Override
        public void close() {
            for (GpuTextureView gpuTextureView : this.textureViewsByFrame.values()) {
                gpuTextureView.texture().close();
                gpuTextureView.close();
            }
        }
    }
}
