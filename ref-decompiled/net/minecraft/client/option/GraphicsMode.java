/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.GpuDeviceInfo
 *  net.minecraft.client.gui.screen.option.GameOptionsScreen
 *  net.minecraft.client.option.CloudRenderMode
 *  net.minecraft.client.option.GraphicsMode
 *  net.minecraft.client.option.SimpleOption
 *  net.minecraft.client.option.TextureFilteringMode
 *  net.minecraft.client.render.ChunkBuilderMode
 *  net.minecraft.particle.ParticlesMode
 *  net.minecraft.util.StringIdentifiable
 *  net.minecraft.util.Util
 *  net.minecraft.util.Util$OperatingSystem
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.option;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GpuDeviceInfo;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.TextureFilteringMode;
import net.minecraft.client.render.ChunkBuilderMode;
import net.minecraft.particle.ParticlesMode;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class GraphicsMode
extends Enum<GraphicsMode>
implements StringIdentifiable {
    public static final /* enum */ GraphicsMode FAST = new GraphicsMode("FAST", 0, "fast", "options.graphics.fast");
    public static final /* enum */ GraphicsMode FANCY = new GraphicsMode("FANCY", 1, "fancy", "options.graphics.fancy");
    public static final /* enum */ GraphicsMode FABULOUS = new GraphicsMode("FABULOUS", 2, "fabulous", "options.graphics.fabulous");
    public static final /* enum */ GraphicsMode CUSTOM = new GraphicsMode("CUSTOM", 3, "custom", "options.graphics.custom");
    private final String name;
    private final String translationKey;
    public static final Codec<GraphicsMode> CODEC;
    private static final /* synthetic */ GraphicsMode[] field_25433;

    public static GraphicsMode[] values() {
        return (GraphicsMode[])field_25433.clone();
    }

    public static GraphicsMode valueOf(String string) {
        return Enum.valueOf(GraphicsMode.class, string);
    }

    private GraphicsMode(String name, String translationKey) {
        this.name = name;
        this.translationKey = translationKey;
    }

    public String asString() {
        return this.name;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public void apply(MinecraftClient client) {
        GameOptionsScreen gameOptionsScreen = client.currentScreen instanceof GameOptionsScreen ? (GameOptionsScreen)client.currentScreen : null;
        GpuDevice gpuDevice = RenderSystem.getDevice();
        switch (this.ordinal()) {
            case 0: {
                int i = 8;
                this.applyOption(gameOptionsScreen, client.options.getBiomeBlendRadius(), (Object)1);
                this.applyOption(gameOptionsScreen, client.options.getViewDistance(), (Object)8);
                this.applyOption(gameOptionsScreen, client.options.getChunkBuilderMode(), (Object)ChunkBuilderMode.NONE);
                this.applyOption(gameOptionsScreen, client.options.getSimulationDistance(), (Object)6);
                this.applyOption(gameOptionsScreen, client.options.getAo(), (Object)false);
                this.applyOption(gameOptionsScreen, client.options.getCloudRenderMode(), (Object)CloudRenderMode.FAST);
                this.applyOption(gameOptionsScreen, client.options.getParticles(), (Object)ParticlesMode.DECREASED);
                this.applyOption(gameOptionsScreen, client.options.getMipmapLevels(), (Object)2);
                this.applyOption(gameOptionsScreen, client.options.getEntityShadows(), (Object)false);
                this.applyOption(gameOptionsScreen, client.options.getEntityDistanceScaling(), (Object)0.75);
                this.applyOption(gameOptionsScreen, client.options.getMenuBackgroundBlurriness(), (Object)2);
                this.applyOption(gameOptionsScreen, client.options.getCloudRenderDistance(), (Object)32);
                this.applyOption(gameOptionsScreen, client.options.getCutoutLeaves(), (Object)false);
                this.applyOption(gameOptionsScreen, client.options.getImprovedTransparency(), (Object)false);
                this.applyOption(gameOptionsScreen, client.options.getWeatherRadius(), (Object)5);
                this.applyOption(gameOptionsScreen, client.options.getMaxAnisotropy(), (Object)1);
                this.applyOption(gameOptionsScreen, client.options.getTextureFiltering(), (Object)TextureFilteringMode.NONE);
                break;
            }
            case 1: {
                int i = 16;
                this.applyOption(gameOptionsScreen, client.options.getBiomeBlendRadius(), (Object)2);
                this.applyOption(gameOptionsScreen, client.options.getViewDistance(), (Object)16);
                this.applyOption(gameOptionsScreen, client.options.getChunkBuilderMode(), (Object)ChunkBuilderMode.PLAYER_AFFECTED);
                this.applyOption(gameOptionsScreen, client.options.getSimulationDistance(), (Object)12);
                this.applyOption(gameOptionsScreen, client.options.getAo(), (Object)true);
                this.applyOption(gameOptionsScreen, client.options.getCloudRenderMode(), (Object)CloudRenderMode.FANCY);
                this.applyOption(gameOptionsScreen, client.options.getParticles(), (Object)ParticlesMode.ALL);
                this.applyOption(gameOptionsScreen, client.options.getMipmapLevels(), (Object)4);
                this.applyOption(gameOptionsScreen, client.options.getEntityShadows(), (Object)true);
                this.applyOption(gameOptionsScreen, client.options.getEntityDistanceScaling(), (Object)1.0);
                this.applyOption(gameOptionsScreen, client.options.getMenuBackgroundBlurriness(), (Object)5);
                this.applyOption(gameOptionsScreen, client.options.getCloudRenderDistance(), (Object)64);
                this.applyOption(gameOptionsScreen, client.options.getCutoutLeaves(), (Object)true);
                this.applyOption(gameOptionsScreen, client.options.getImprovedTransparency(), (Object)false);
                this.applyOption(gameOptionsScreen, client.options.getWeatherRadius(), (Object)10);
                this.applyOption(gameOptionsScreen, client.options.getMaxAnisotropy(), (Object)1);
                this.applyOption(gameOptionsScreen, client.options.getTextureFiltering(), (Object)TextureFilteringMode.RGSS);
                break;
            }
            case 2: {
                int i = 32;
                this.applyOption(gameOptionsScreen, client.options.getBiomeBlendRadius(), (Object)2);
                this.applyOption(gameOptionsScreen, client.options.getViewDistance(), (Object)32);
                this.applyOption(gameOptionsScreen, client.options.getChunkBuilderMode(), (Object)ChunkBuilderMode.PLAYER_AFFECTED);
                this.applyOption(gameOptionsScreen, client.options.getSimulationDistance(), (Object)12);
                this.applyOption(gameOptionsScreen, client.options.getAo(), (Object)true);
                this.applyOption(gameOptionsScreen, client.options.getCloudRenderMode(), (Object)CloudRenderMode.FANCY);
                this.applyOption(gameOptionsScreen, client.options.getParticles(), (Object)ParticlesMode.ALL);
                this.applyOption(gameOptionsScreen, client.options.getMipmapLevels(), (Object)4);
                this.applyOption(gameOptionsScreen, client.options.getEntityShadows(), (Object)true);
                this.applyOption(gameOptionsScreen, client.options.getEntityDistanceScaling(), (Object)1.25);
                this.applyOption(gameOptionsScreen, client.options.getMenuBackgroundBlurriness(), (Object)5);
                this.applyOption(gameOptionsScreen, client.options.getCloudRenderDistance(), (Object)128);
                this.applyOption(gameOptionsScreen, client.options.getCutoutLeaves(), (Object)true);
                this.applyOption(gameOptionsScreen, client.options.getImprovedTransparency(), (Object)(Util.getOperatingSystem() != Util.OperatingSystem.OSX ? 1 : 0));
                this.applyOption(gameOptionsScreen, client.options.getWeatherRadius(), (Object)10);
                this.applyOption(gameOptionsScreen, client.options.getMaxAnisotropy(), (Object)2);
                if (GpuDeviceInfo.get((GpuDevice)gpuDevice).shouldUseRgssOnFabulous()) {
                    this.applyOption(gameOptionsScreen, client.options.getTextureFiltering(), (Object)TextureFilteringMode.RGSS);
                    break;
                }
                this.applyOption(gameOptionsScreen, client.options.getTextureFiltering(), (Object)TextureFilteringMode.ANISOTROPIC);
            }
        }
    }

    <T> void applyOption(@Nullable GameOptionsScreen screen, SimpleOption<T> option, T value) {
        if (option.getValue() != value) {
            option.setValue(value);
            if (screen != null) {
                screen.update(option);
            }
        }
    }

    private static /* synthetic */ GraphicsMode[] method_36861() {
        return new GraphicsMode[]{FAST, FANCY, FABULOUS, CUSTOM};
    }

    static {
        field_25433 = GraphicsMode.method_36861();
        CODEC = StringIdentifiable.createCodec(GraphicsMode::values);
    }
}

