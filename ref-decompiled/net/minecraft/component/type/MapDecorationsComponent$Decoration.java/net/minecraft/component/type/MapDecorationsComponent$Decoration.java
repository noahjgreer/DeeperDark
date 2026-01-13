/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.component.type;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.registry.entry.RegistryEntry;

public record MapDecorationsComponent.Decoration(RegistryEntry<MapDecorationType> type, double x, double z, float rotation) {
    public static final Codec<MapDecorationsComponent.Decoration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)MapDecorationType.CODEC.fieldOf("type").forGetter(MapDecorationsComponent.Decoration::type), (App)Codec.DOUBLE.fieldOf("x").forGetter(MapDecorationsComponent.Decoration::x), (App)Codec.DOUBLE.fieldOf("z").forGetter(MapDecorationsComponent.Decoration::z), (App)Codec.FLOAT.fieldOf("rotation").forGetter(MapDecorationsComponent.Decoration::rotation)).apply((Applicative)instance, MapDecorationsComponent.Decoration::new));
}
