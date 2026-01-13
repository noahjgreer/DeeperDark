/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import org.jspecify.annotations.Nullable;

public static final class Leashable.LeashData {
    public static final Codec<Leashable.LeashData> CODEC = Codec.xor((Codec)Uuids.INT_STREAM_CODEC.fieldOf("UUID").codec(), BlockPos.CODEC).xmap(Leashable.LeashData::new, data -> {
        Entity entity = data.leashHolder;
        if (entity instanceof LeashKnotEntity) {
            LeashKnotEntity leashKnotEntity = (LeashKnotEntity)entity;
            return Either.right((Object)leashKnotEntity.getAttachedBlockPos());
        }
        if (data.leashHolder != null) {
            return Either.left((Object)data.leashHolder.getUuid());
        }
        return Objects.requireNonNull(data.unresolvedLeashData, "Invalid LeashData had no attachment");
    });
    int unresolvedLeashHolderId;
    public @Nullable Entity leashHolder;
    public @Nullable Either<UUID, BlockPos> unresolvedLeashData;
    public double momentum;

    private Leashable.LeashData(Either<UUID, BlockPos> unresolvedLeashData) {
        this.unresolvedLeashData = unresolvedLeashData;
    }

    Leashable.LeashData(Entity leashHolder) {
        this.leashHolder = leashHolder;
    }

    Leashable.LeashData(int unresolvedLeashHolderId) {
        this.unresolvedLeashHolderId = unresolvedLeashHolderId;
    }

    public void setLeashHolder(Entity leashHolder) {
        this.leashHolder = leashHolder;
        this.unresolvedLeashData = null;
        this.unresolvedLeashHolderId = 0;
    }
}
