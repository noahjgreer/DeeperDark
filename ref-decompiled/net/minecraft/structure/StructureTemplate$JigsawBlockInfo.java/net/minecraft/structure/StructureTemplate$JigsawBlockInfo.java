/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.Identifier;

public static final class StructureTemplate.JigsawBlockInfo
extends Record {
    final StructureTemplate.StructureBlockInfo info;
    private final JigsawBlockEntity.Joint jointType;
    private final Identifier name;
    private final RegistryKey<StructurePool> pool;
    private final Identifier target;
    private final int placementPriority;
    private final int selectionPriority;

    public StructureTemplate.JigsawBlockInfo(StructureTemplate.StructureBlockInfo info, JigsawBlockEntity.Joint jointType, Identifier name, RegistryKey<StructurePool> pool, Identifier target, int placementPriority, int selectionPriority) {
        this.info = info;
        this.jointType = jointType;
        this.name = name;
        this.pool = pool;
        this.target = target;
        this.placementPriority = placementPriority;
        this.selectionPriority = selectionPriority;
    }

    public static StructureTemplate.JigsawBlockInfo of(StructureTemplate.StructureBlockInfo structureBlockInfo) {
        NbtCompound nbtCompound = Objects.requireNonNull(structureBlockInfo.nbt(), () -> String.valueOf(structureBlockInfo) + " nbt was null");
        return new StructureTemplate.JigsawBlockInfo(structureBlockInfo, StructureTemplate.readJoint(nbtCompound, structureBlockInfo.state()), nbtCompound.get("name", Identifier.CODEC).orElse(JigsawBlockEntity.DEFAULT_NAME), nbtCompound.get("pool", JigsawBlockEntity.STRUCTURE_POOL_KEY_CODEC).orElse(StructurePools.EMPTY), nbtCompound.get("target", Identifier.CODEC).orElse(JigsawBlockEntity.DEFAULT_NAME), nbtCompound.getInt("placement_priority", 0), nbtCompound.getInt("selection_priority", 0));
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "<JigsawBlockInfo | %s | %s | name: %s | pool: %s | target: %s | placement: %d | selection: %d | %s>", this.info.pos, this.info.state, this.name, this.pool.getValue(), this.target, this.placementPriority, this.selectionPriority, this.info.nbt);
    }

    public StructureTemplate.JigsawBlockInfo withInfo(StructureTemplate.StructureBlockInfo structureBlockInfo) {
        return new StructureTemplate.JigsawBlockInfo(structureBlockInfo, this.jointType, this.name, this.pool, this.target, this.placementPriority, this.selectionPriority);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{StructureTemplate.JigsawBlockInfo.class, "info;jointType;name;pool;target;placementPriority;selectionPriority", "info", "jointType", "name", "pool", "target", "placementPriority", "selectionPriority"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{StructureTemplate.JigsawBlockInfo.class, "info;jointType;name;pool;target;placementPriority;selectionPriority", "info", "jointType", "name", "pool", "target", "placementPriority", "selectionPriority"}, this, object);
    }

    public StructureTemplate.StructureBlockInfo info() {
        return this.info;
    }

    public JigsawBlockEntity.Joint jointType() {
        return this.jointType;
    }

    public Identifier name() {
        return this.name;
    }

    public RegistryKey<StructurePool> pool() {
        return this.pool;
    }

    public Identifier target() {
        return this.target;
    }

    public int placementPriority() {
        return this.placementPriority;
    }

    public int selectionPriority() {
        return this.selectionPriority;
    }
}
