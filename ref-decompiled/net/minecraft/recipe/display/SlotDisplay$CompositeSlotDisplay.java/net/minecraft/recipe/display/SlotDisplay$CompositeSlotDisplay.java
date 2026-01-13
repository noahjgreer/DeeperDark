/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.recipe.display;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.display.DisplayedItemFactory;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.context.ContextParameterMap;

public record SlotDisplay.CompositeSlotDisplay(List<SlotDisplay> contents) implements SlotDisplay
{
    public static final MapCodec<SlotDisplay.CompositeSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)CODEC.listOf().fieldOf("contents").forGetter(SlotDisplay.CompositeSlotDisplay::contents)).apply((Applicative)instance, SlotDisplay.CompositeSlotDisplay::new));
    public static final PacketCodec<RegistryByteBuf, SlotDisplay.CompositeSlotDisplay> PACKET_CODEC = PacketCodec.tuple(PACKET_CODEC.collect(PacketCodecs.toList()), SlotDisplay.CompositeSlotDisplay::contents, SlotDisplay.CompositeSlotDisplay::new);
    public static final SlotDisplay.Serializer<SlotDisplay.CompositeSlotDisplay> SERIALIZER = new SlotDisplay.Serializer<SlotDisplay.CompositeSlotDisplay>(CODEC, PACKET_CODEC);

    public SlotDisplay.Serializer<SlotDisplay.CompositeSlotDisplay> serializer() {
        return SERIALIZER;
    }

    @Override
    public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
        return this.contents.stream().flatMap(display -> display.appendStacks(parameters, factory));
    }

    @Override
    public boolean isEnabled(FeatureSet features) {
        return this.contents.stream().allMatch(child -> child.isEnabled(features));
    }
}
