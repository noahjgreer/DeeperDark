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
import java.util.Optional;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.ClickEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public record ClickEvent.Custom(Identifier id, Optional<NbtElement> payload) implements ClickEvent
{
    public static final MapCodec<ClickEvent.Custom> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("id").forGetter(ClickEvent.Custom::id), (App)Codecs.NBT_ELEMENT.optionalFieldOf("payload").forGetter(ClickEvent.Custom::payload)).apply((Applicative)instance, ClickEvent.Custom::new));

    @Override
    public ClickEvent.Action getAction() {
        return ClickEvent.Action.CUSTOM;
    }
}
