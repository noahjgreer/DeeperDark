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
package net.minecraft.server.network;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import org.jspecify.annotations.Nullable;

public static final class ServerPlayerEntity.Respawn
extends Record {
    final WorldProperties.SpawnPoint respawnData;
    final boolean forced;
    public static final Codec<ServerPlayerEntity.Respawn> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)WorldProperties.SpawnPoint.MAP_CODEC.forGetter(ServerPlayerEntity.Respawn::respawnData), (App)Codec.BOOL.optionalFieldOf("forced", (Object)false).forGetter(ServerPlayerEntity.Respawn::forced)).apply((Applicative)instance, ServerPlayerEntity.Respawn::new));

    public ServerPlayerEntity.Respawn(WorldProperties.SpawnPoint respawnData, boolean forced) {
        this.respawnData = respawnData;
        this.forced = forced;
    }

    static RegistryKey<World> getDimension(@Nullable ServerPlayerEntity.Respawn respawn) {
        return respawn != null ? respawn.respawnData().getDimension() : World.OVERWORLD;
    }

    public boolean posEquals(@Nullable ServerPlayerEntity.Respawn respawn) {
        return respawn != null && this.respawnData.globalPos().equals(respawn.respawnData.globalPos());
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ServerPlayerEntity.Respawn.class, "respawnData;forced", "respawnData", "forced"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ServerPlayerEntity.Respawn.class, "respawnData;forced", "respawnData", "forced"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ServerPlayerEntity.Respawn.class, "respawnData;forced", "respawnData", "forced"}, this, object);
    }

    public WorldProperties.SpawnPoint respawnData() {
        return this.respawnData;
    }

    public boolean forced() {
        return this.forced;
    }
}
