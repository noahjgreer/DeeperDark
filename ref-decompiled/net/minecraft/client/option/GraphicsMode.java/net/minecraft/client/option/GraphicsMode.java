/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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

@Environment(value=EnvType.CLIENT)
public final class GraphicsMode
extends Enum<GraphicsMode>
implements StringIdentifiable {
    public static final /* enum */ GraphicsMode FAST = new GraphicsMode("fast", "options.graphics.fast");
    public static final /* enum */ GraphicsMode FANCY = new GraphicsMode("fancy", "options.graphics.fancy");
    public static final /* enum */ GraphicsMode FABULOUS = new GraphicsMode("fabulous", "options.graphics.fabulous");
    public static final /* enum */ GraphicsMode CUSTOM = new GraphicsMode("custom", "options.graphics.custom");
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

    @Override
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
                this.applyOption(gameOptionsScreen, client.options.getBiomeBlendRadius(), 1);
                this.applyOption(gameOptionsScreen, client.options.getViewDistance(), 8);
                this.applyOption(gameOptionsScreen, client.options.getChunkBuilderMode(), ChunkBuilderMode.NONE);
                this.applyOption(gameOptionsScreen, client.options.getSimulationDistance(), 6);
                this.applyOption(gameOptionsScreen, client.options.getAo(), false);
                this.applyOption(gameOptionsScreen, client.options.getCloudRenderMode(), CloudRenderMode.FAST);
                this.applyOption(gameOptionsScreen, client.options.getParticles(), ParticlesMode.DECREASED);
                this.applyOption(gameOptionsScreen, client.options.getMipmapLevels(), 2);
                this.applyOption(gameOptionsScreen, client.options.getEntityShadows(), false);
                this.applyOption(gameOptionsScreen, client.options.getEntityDistanceScaling(), 0.75);
                this.applyOption(gameOptionsScreen, client.options.getMenuBackgroundBlurriness(), 2);
                this.applyOption(gameOptionsScreen, client.options.getCloudRenderDistance(), 32);
                this.applyOption(gameOptionsScreen, client.options.getCutoutLeaves(), false);
                this.applyOption(gameOptionsScreen, client.options.getImprovedTransparency(), false);
                this.applyOption(gameOptionsScreen, client.options.getWeatherRadius(), 5);
                this.applyOption(gameOptionsScreen, client.options.getMaxAnisotropy(), 1);
                this.applyOption(gameOptionsScreen, client.options.getTextureFiltering(), TextureFilteringMode.NONE);
                break;
            }
            case 1: {
                int i = 16;
                this.applyOption(gameOptionsScreen, client.options.getBiomeBlendRadius(), 2);
                this.applyOption(gameOptionsScreen, client.options.getViewDistance(), 16);
                this.applyOption(gameOptionsScreen, client.options.getChunkBuilderMode(), ChunkBuilderMode.PLAYER_AFFECTED);
                this.applyOption(gameOptionsScreen, client.options.getSimulationDistance(), 12);
                this.applyOption(gameOptionsScreen, client.options.getAo(), true);
                this.applyOption(gameOptionsScreen, client.options.getCloudRenderMode(), CloudRenderMode.FANCY);
                this.applyOption(gameOptionsScreen, client.options.getParticles(), ParticlesMode.ALL);
                this.applyOption(gameOptionsScreen, client.options.getMipmapLevels(), 4);
                this.applyOption(gameOptionsScreen, client.options.getEntityShadows(), true);
                this.applyOption(gameOptionsScreen, client.options.getEntityDistanceScaling(), 1.0);
                this.applyOption(gameOptionsScreen, client.options.getMenuBackgroundBlurriness(), 5);
                this.applyOption(gameOptionsScreen, client.options.getCloudRenderDistance(), 64);
                this.applyOption(gameOptionsScreen, client.options.getCutoutLeaves(), true);
                this.applyOption(gameOptionsScreen, client.options.getImprovedTransparency(), false);
                this.applyOption(gameOptionsScreen, client.options.getWeatherRadius(), 10);
                this.applyOption(gameOptionsScreen, client.options.getMaxAnisotropy(), 1);
                this.applyOption(gameOptionsScreen, client.options.getTextureFiltering(), TextureFilteringMode.RGSS);
                break;
            }
            case 2: {
                int i = 32;
                this.applyOption(gameOptionsScreen, client.options.getBiomeBlendRadius(), 2);
                this.applyOption(gameOptionsScreen, client.options.getViewDistance(), 32);
                this.applyOption(gameOptionsScreen, client.options.getChunkBuilderMode(), ChunkBuilderMode.PLAYER_AFFECTED);
                this.applyOption(gameOptionsScreen, client.options.getSimulationDistance(), 12);
                this.applyOption(gameOptionsScreen, client.options.getAo(), true);
                this.applyOption(gameOptionsScreen, client.options.getCloudRenderMode(), CloudRenderMode.FANCY);
                this.applyOption(gameOptionsScreen, client.options.getParticles(), ParticlesMode.ALL);
                this.applyOption(gameOptionsScreen, client.options.getMipmapLevels(), 4);
                this.applyOption(gameOptionsScreen, client.options.getEntityShadows(), true);
                this.applyOption(gameOptionsScreen, client.options.getEntityDistanceScaling(), 1.25);
                this.applyOption(gameOptionsScreen, client.options.getMenuBackgroundBlurriness(), 5);
                this.applyOption(gameOptionsScreen, client.options.getCloudRenderDistance(), 128);
                this.applyOption(gameOptionsScreen, client.options.getCutoutLeaves(), true);
                this.applyOption(gameOptionsScreen, client.options.getImprovedTransparency(), Util.getOperatingSystem() != Util.OperatingSystem.OSX);
                this.applyOption(gameOptionsScreen, client.options.getWeatherRadius(), 10);
                this.applyOption(gameOptionsScreen, client.options.getMaxAnisotropy(), 2);
                if (GpuDeviceInfo.get(gpuDevice).shouldUseRgssOnFabulous()) {
                    this.applyOption(gameOptionsScreen, client.options.getTextureFiltering(), TextureFilteringMode.RGSS);
                    break;
                }
                this.applyOption(gameOptionsScreen, client.options.getTextureFiltering(), TextureFilteringMode.ANISOTROPIC);
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
