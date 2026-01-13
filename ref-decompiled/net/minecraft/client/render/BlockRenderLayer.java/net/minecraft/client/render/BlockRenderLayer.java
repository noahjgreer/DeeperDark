/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;

@Environment(value=EnvType.CLIENT)
public final class BlockRenderLayer
extends Enum<BlockRenderLayer> {
    public static final /* enum */ BlockRenderLayer SOLID = new BlockRenderLayer(RenderPipelines.SOLID_TERRAIN, 0x400000, false);
    public static final /* enum */ BlockRenderLayer CUTOUT = new BlockRenderLayer(RenderPipelines.CUTOUT_TERRAIN, 0x400000, false);
    public static final /* enum */ BlockRenderLayer TRANSLUCENT = new BlockRenderLayer(RenderPipelines.TRANSLUCENT, 786432, true);
    public static final /* enum */ BlockRenderLayer TRIPWIRE = new BlockRenderLayer(RenderPipelines.TRIPWIRE_TERRAIN, 1536, true);
    private final RenderPipeline pipeline;
    private final int size;
    private final boolean translucent;
    private final String name;
    private static final /* synthetic */ BlockRenderLayer[] field_60933;

    public static BlockRenderLayer[] values() {
        return (BlockRenderLayer[])field_60933.clone();
    }

    public static BlockRenderLayer valueOf(String string) {
        return Enum.valueOf(BlockRenderLayer.class, string);
    }

    private BlockRenderLayer(RenderPipeline pipeline, int size, boolean mipmap) {
        this.pipeline = pipeline;
        this.size = size;
        this.translucent = mipmap;
        this.name = this.toString().toLowerCase(Locale.ROOT);
    }

    public RenderPipeline getPipeline() {
        return this.pipeline;
    }

    public int getBufferSize() {
        return this.size;
    }

    public String getName() {
        return this.name;
    }

    public boolean isTranslucent() {
        return this.translucent;
    }

    private static /* synthetic */ BlockRenderLayer[] method_72026() {
        return new BlockRenderLayer[]{SOLID, CUTOUT, TRANSLUCENT, TRIPWIRE};
    }

    static {
        field_60933 = BlockRenderLayer.method_72026();
    }
}
