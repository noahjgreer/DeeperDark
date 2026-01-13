/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.server.network;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public record ServerPlayerEntity.SavePos(Optional<RegistryKey<World>> dimension, Optional<Vec3d> position, Optional<Vec2f> rotation) {
    public static final MapCodec<ServerPlayerEntity.SavePos> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)World.CODEC.optionalFieldOf(ServerPlayerEntity.DIMENSION_KEY).forGetter(ServerPlayerEntity.SavePos::dimension), (App)Vec3d.CODEC.optionalFieldOf("Pos").forGetter(ServerPlayerEntity.SavePos::position), (App)Vec2f.CODEC.optionalFieldOf("Rotation").forGetter(ServerPlayerEntity.SavePos::rotation)).apply((Applicative)instance, ServerPlayerEntity.SavePos::new));
    public static final ServerPlayerEntity.SavePos EMPTY = new ServerPlayerEntity.SavePos(Optional.empty(), Optional.empty(), Optional.empty());
}
