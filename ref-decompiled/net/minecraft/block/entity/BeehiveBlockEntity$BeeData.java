/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LoadedEntityProcessor;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TypedEntityData;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

public static final class BeehiveBlockEntity.BeeData
extends Record {
    final TypedEntityData<EntityType<?>> entityData;
    private final int ticksInHive;
    final int minTicksInHive;
    public static final Codec<BeehiveBlockEntity.BeeData> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)TypedEntityData.createCodec(EntityType.CODEC).fieldOf("entity_data").forGetter(BeehiveBlockEntity.BeeData::entityData), (App)Codec.INT.fieldOf("ticks_in_hive").forGetter(BeehiveBlockEntity.BeeData::ticksInHive), (App)Codec.INT.fieldOf("min_ticks_in_hive").forGetter(BeehiveBlockEntity.BeeData::minTicksInHive)).apply((Applicative)instance, BeehiveBlockEntity.BeeData::new));
    public static final Codec<List<BeehiveBlockEntity.BeeData>> LIST_CODEC = CODEC.listOf();
    public static final PacketCodec<RegistryByteBuf, BeehiveBlockEntity.BeeData> PACKET_CODEC = PacketCodec.tuple(TypedEntityData.createPacketCodec(EntityType.PACKET_CODEC), BeehiveBlockEntity.BeeData::entityData, PacketCodecs.VAR_INT, BeehiveBlockEntity.BeeData::ticksInHive, PacketCodecs.VAR_INT, BeehiveBlockEntity.BeeData::minTicksInHive, BeehiveBlockEntity.BeeData::new);

    public BeehiveBlockEntity.BeeData(TypedEntityData<EntityType<?>> entityData, int ticksInHive, int minTicksInHive) {
        this.entityData = entityData;
        this.ticksInHive = ticksInHive;
        this.minTicksInHive = minTicksInHive;
    }

    public static BeehiveBlockEntity.BeeData of(Entity entity) {
        try (ErrorReporter.Logging logging = new ErrorReporter.Logging(entity.getErrorReporterContext(), LOGGER);){
            NbtWriteView nbtWriteView = NbtWriteView.create(logging, entity.getRegistryManager());
            entity.saveData(nbtWriteView);
            IRRELEVANT_BEE_NBT_KEYS.forEach(nbtWriteView::remove);
            NbtCompound nbtCompound = nbtWriteView.getNbt();
            boolean bl = nbtCompound.getBoolean("HasNectar", false);
            BeehiveBlockEntity.BeeData beeData = new BeehiveBlockEntity.BeeData(TypedEntityData.create(entity.getType(), nbtCompound), 0, bl ? 2400 : 600);
            return beeData;
        }
    }

    public static BeehiveBlockEntity.BeeData create(int ticksInHive) {
        return new BeehiveBlockEntity.BeeData(TypedEntityData.create(EntityType.BEE, new NbtCompound()), ticksInHive, 600);
    }

    public @Nullable Entity loadEntity(World world, BlockPos pos) {
        NbtCompound nbtCompound = this.entityData.copyNbtWithoutId();
        IRRELEVANT_BEE_NBT_KEYS.forEach(nbtCompound::remove);
        Entity entity = EntityType.loadEntityWithPassengers(this.entityData.getType(), nbtCompound, world, SpawnReason.LOAD, LoadedEntityProcessor.NOOP);
        if (entity == null || !entity.getType().isIn(EntityTypeTags.BEEHIVE_INHABITORS)) {
            return null;
        }
        entity.setNoGravity(true);
        if (entity instanceof BeeEntity) {
            BeeEntity beeEntity = (BeeEntity)entity;
            beeEntity.setHivePos(pos);
            BeehiveBlockEntity.BeeData.tickEntity(this.ticksInHive, beeEntity);
        }
        return entity;
    }

    private static void tickEntity(int ticksInHive, BeeEntity beeEntity) {
        int i = beeEntity.getBreedingAge();
        if (i < 0) {
            beeEntity.setBreedingAge(Math.min(0, i + ticksInHive));
        } else if (i > 0) {
            beeEntity.setBreedingAge(Math.max(0, i - ticksInHive));
        }
        beeEntity.setLoveTicks(Math.max(0, beeEntity.getLoveTicks() - ticksInHive));
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BeehiveBlockEntity.BeeData.class, "entityData;ticksInHive;minTicksInHive", "entityData", "ticksInHive", "minTicksInHive"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BeehiveBlockEntity.BeeData.class, "entityData;ticksInHive;minTicksInHive", "entityData", "ticksInHive", "minTicksInHive"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BeehiveBlockEntity.BeeData.class, "entityData;ticksInHive;minTicksInHive", "entityData", "ticksInHive", "minTicksInHive"}, this, object);
    }

    public TypedEntityData<EntityType<?>> entityData() {
        return this.entityData;
    }

    public int ticksInHive() {
        return this.ticksInHive;
    }

    public int minTicksInHive() {
        return this.minTicksInHive;
    }
}
