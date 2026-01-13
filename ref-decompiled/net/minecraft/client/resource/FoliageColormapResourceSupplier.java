/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.resource.FoliageColormapResourceSupplier
 *  net.minecraft.client.util.RawTextureDataLoader
 *  net.minecraft.resource.ResourceManager
 *  net.minecraft.resource.SinglePreparationResourceReloader
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.profiler.Profiler
 *  net.minecraft.world.biome.FoliageColors
 */
package net.minecraft.client.resource;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.biome.FoliageColors;

@Environment(value=EnvType.CLIENT)
public class FoliageColormapResourceSupplier
extends SinglePreparationResourceReloader<int[]> {
    private static final Identifier FOLIAGE_COLORMAP = Identifier.ofVanilla((String)"textures/colormap/foliage.png");

    protected int[] reload(ResourceManager resourceManager, Profiler profiler) {
        try {
            return RawTextureDataLoader.loadRawTextureData((ResourceManager)resourceManager, (Identifier)FOLIAGE_COLORMAP);
        }
        catch (IOException iOException) {
            throw new IllegalStateException("Failed to load foliage color texture", iOException);
        }
    }

    protected void apply(int[] is, ResourceManager resourceManager, Profiler profiler) {
        FoliageColors.setColorMap((int[])is);
    }

    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.reload(manager, profiler);
    }
}

