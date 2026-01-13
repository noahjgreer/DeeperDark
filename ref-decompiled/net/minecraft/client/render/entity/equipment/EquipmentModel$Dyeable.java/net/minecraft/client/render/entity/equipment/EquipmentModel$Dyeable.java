/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.equipment;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record EquipmentModel.Dyeable(Optional<Integer> colorWhenUndyed) {
    public static final Codec<EquipmentModel.Dyeable> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.RGB.optionalFieldOf("color_when_undyed").forGetter(EquipmentModel.Dyeable::colorWhenUndyed)).apply((Applicative)instance, EquipmentModel.Dyeable::new));
}
