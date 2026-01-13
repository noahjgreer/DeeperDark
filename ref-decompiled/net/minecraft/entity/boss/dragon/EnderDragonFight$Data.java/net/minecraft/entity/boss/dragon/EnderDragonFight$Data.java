/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.entity.boss.dragon;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

public static final class EnderDragonFight.Data
extends Record {
    final boolean needsStateScanning;
    final boolean dragonKilled;
    final boolean previouslyKilled;
    final boolean isRespawning;
    final Optional<UUID> dragonUUID;
    final Optional<BlockPos> exitPortalLocation;
    final Optional<List<Integer>> gateways;
    public static final Codec<EnderDragonFight.Data> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.BOOL.fieldOf("NeedsStateScanning").orElse((Object)true).forGetter(EnderDragonFight.Data::needsStateScanning), (App)Codec.BOOL.fieldOf("DragonKilled").orElse((Object)false).forGetter(EnderDragonFight.Data::dragonKilled), (App)Codec.BOOL.fieldOf("PreviouslyKilled").orElse((Object)false).forGetter(EnderDragonFight.Data::previouslyKilled), (App)Codec.BOOL.lenientOptionalFieldOf("IsRespawning", (Object)false).forGetter(EnderDragonFight.Data::isRespawning), (App)Uuids.INT_STREAM_CODEC.lenientOptionalFieldOf("Dragon").forGetter(EnderDragonFight.Data::dragonUUID), (App)BlockPos.CODEC.lenientOptionalFieldOf("ExitPortalLocation").forGetter(EnderDragonFight.Data::exitPortalLocation), (App)Codec.list((Codec)Codec.INT).lenientOptionalFieldOf("Gateways").forGetter(EnderDragonFight.Data::gateways)).apply((Applicative)instance, EnderDragonFight.Data::new));
    public static final EnderDragonFight.Data DEFAULT = new EnderDragonFight.Data(true, false, false, false, Optional.empty(), Optional.empty(), Optional.empty());

    public EnderDragonFight.Data(boolean needsStateScanning, boolean dragonKilled, boolean previouslyKilled, boolean isRespawning, Optional<UUID> dragonUUID, Optional<BlockPos> exitPortalLocation, Optional<List<Integer>> gateways) {
        this.needsStateScanning = needsStateScanning;
        this.dragonKilled = dragonKilled;
        this.previouslyKilled = previouslyKilled;
        this.isRespawning = isRespawning;
        this.dragonUUID = dragonUUID;
        this.exitPortalLocation = exitPortalLocation;
        this.gateways = gateways;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{EnderDragonFight.Data.class, "needsStateScanning;dragonKilled;previouslyKilled;isRespawning;dragonUUID;exitPortalLocation;gateways", "needsStateScanning", "dragonKilled", "previouslyKilled", "isRespawning", "dragonUUID", "exitPortalLocation", "gateways"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{EnderDragonFight.Data.class, "needsStateScanning;dragonKilled;previouslyKilled;isRespawning;dragonUUID;exitPortalLocation;gateways", "needsStateScanning", "dragonKilled", "previouslyKilled", "isRespawning", "dragonUUID", "exitPortalLocation", "gateways"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{EnderDragonFight.Data.class, "needsStateScanning;dragonKilled;previouslyKilled;isRespawning;dragonUUID;exitPortalLocation;gateways", "needsStateScanning", "dragonKilled", "previouslyKilled", "isRespawning", "dragonUUID", "exitPortalLocation", "gateways"}, this, object);
    }

    public boolean needsStateScanning() {
        return this.needsStateScanning;
    }

    public boolean dragonKilled() {
        return this.dragonKilled;
    }

    public boolean previouslyKilled() {
        return this.previouslyKilled;
    }

    public boolean isRespawning() {
        return this.isRespawning;
    }

    public Optional<UUID> dragonUUID() {
        return this.dragonUUID;
    }

    public Optional<BlockPos> exitPortalLocation() {
        return this.exitPortalLocation;
    }

    public Optional<List<Integer>> gateways() {
        return this.gateways;
    }
}
