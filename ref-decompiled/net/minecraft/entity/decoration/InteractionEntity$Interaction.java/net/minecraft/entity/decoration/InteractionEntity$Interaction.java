/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.entity.decoration;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.UUID;
import net.minecraft.util.Uuids;

record InteractionEntity.Interaction(UUID player, long timestamp) {
    public static final Codec<InteractionEntity.Interaction> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Uuids.INT_STREAM_CODEC.fieldOf("player").forGetter(InteractionEntity.Interaction::player), (App)Codec.LONG.fieldOf("timestamp").forGetter(InteractionEntity.Interaction::timestamp)).apply((Applicative)instance, InteractionEntity.Interaction::new));
}
