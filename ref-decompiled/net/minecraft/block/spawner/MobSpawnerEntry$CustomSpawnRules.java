/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.block.spawner;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Range;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

public record MobSpawnerEntry.CustomSpawnRules(Range<Integer> blockLightLimit, Range<Integer> skyLightLimit) {
    private static final Range<Integer> DEFAULT = new Range<Integer>(0, 15);
    public static final Codec<MobSpawnerEntry.CustomSpawnRules> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)MobSpawnerEntry.CustomSpawnRules.createLightLimitCodec("block_light_limit").forGetter(rules -> rules.blockLightLimit), (App)MobSpawnerEntry.CustomSpawnRules.createLightLimitCodec("sky_light_limit").forGetter(rules -> rules.skyLightLimit)).apply((Applicative)instance, MobSpawnerEntry.CustomSpawnRules::new));

    private static DataResult<Range<Integer>> validate(Range<Integer> provider) {
        if (!DEFAULT.contains(provider)) {
            return DataResult.error(() -> "Light values must be withing range " + String.valueOf(DEFAULT));
        }
        return DataResult.success(provider);
    }

    private static MapCodec<Range<Integer>> createLightLimitCodec(String name) {
        return Range.CODEC.lenientOptionalFieldOf(name, DEFAULT).validate(MobSpawnerEntry.CustomSpawnRules::validate);
    }

    public boolean canSpawn(BlockPos pos, ServerWorld world) {
        return this.blockLightLimit.contains(world.getLightLevel(LightType.BLOCK, pos)) && this.skyLightLimit.contains(world.getLightLevel(LightType.SKY, pos));
    }
}
