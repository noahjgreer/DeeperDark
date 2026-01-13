/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.text;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.HoverEvent;

public record HoverEvent.ShowEntity(HoverEvent.EntityContent entity) implements HoverEvent
{
    public static final MapCodec<HoverEvent.ShowEntity> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)HoverEvent.EntityContent.CODEC.forGetter(HoverEvent.ShowEntity::entity)).apply((Applicative)instance, HoverEvent.ShowEntity::new));

    @Override
    public HoverEvent.Action getAction() {
        return HoverEvent.Action.SHOW_ENTITY;
    }
}
