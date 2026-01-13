/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.Heightmap;

public static final class StructurePool.Projection
extends Enum<StructurePool.Projection>
implements StringIdentifiable {
    public static final /* enum */ StructurePool.Projection TERRAIN_MATCHING = new StructurePool.Projection("terrain_matching", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new GravityStructureProcessor(Heightmap.Type.WORLD_SURFACE_WG, -1)));
    public static final /* enum */ StructurePool.Projection RIGID = new StructurePool.Projection("rigid", (ImmutableList<StructureProcessor>)ImmutableList.of());
    public static final StringIdentifiable.EnumCodec<StructurePool.Projection> CODEC;
    private final String id;
    private final ImmutableList<StructureProcessor> processors;
    private static final /* synthetic */ StructurePool.Projection[] field_16683;

    public static StructurePool.Projection[] values() {
        return (StructurePool.Projection[])field_16683.clone();
    }

    public static StructurePool.Projection valueOf(String string) {
        return Enum.valueOf(StructurePool.Projection.class, string);
    }

    private StructurePool.Projection(String id, ImmutableList<StructureProcessor> processors) {
        this.id = id;
        this.processors = processors;
    }

    public String getId() {
        return this.id;
    }

    public static StructurePool.Projection getById(String id) {
        return CODEC.byId(id);
    }

    public ImmutableList<StructureProcessor> getProcessors() {
        return this.processors;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ StructurePool.Projection[] method_36758() {
        return new StructurePool.Projection[]{TERRAIN_MATCHING, RIGID};
    }

    static {
        field_16683 = StructurePool.Projection.method_36758();
        CODEC = StringIdentifiable.createCodec(StructurePool.Projection::values);
    }
}
