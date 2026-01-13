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
import net.minecraft.recipe.display.DisplayedItemFactory;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.context.ContextParameterMap;

public record SlotDisplay.WithRemainderSlotDisplay(SlotDisplay input, SlotDisplay remainder) implements SlotDisplay
{
    public static final MapCodec<SlotDisplay.WithRemainderSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)CODEC.fieldOf("input").forGetter(SlotDisplay.WithRemainderSlotDisplay::input), (App)CODEC.fieldOf("remainder").forGetter(SlotDisplay.WithRemainderSlotDisplay::remainder)).apply((Applicative)instance, SlotDisplay.WithRemainderSlotDisplay::new));
    public static final PacketCodec<RegistryByteBuf, SlotDisplay.WithRemainderSlotDisplay> PACKET_CODEC = PacketCodec.tuple(PACKET_CODEC, SlotDisplay.WithRemainderSlotDisplay::input, PACKET_CODEC, SlotDisplay.WithRemainderSlotDisplay::remainder, SlotDisplay.WithRemainderSlotDisplay::new);
    public static final SlotDisplay.Serializer<SlotDisplay.WithRemainderSlotDisplay> SERIALIZER = new SlotDisplay.Serializer<SlotDisplay.WithRemainderSlotDisplay>(CODEC, PACKET_CODEC);

    public SlotDisplay.Serializer<SlotDisplay.WithRemainderSlotDisplay> serializer() {
        return SERIALIZER;
    }

    @Override
    public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
        if (factory instanceof DisplayedItemFactory.FromRemainder) {
            DisplayedItemFactory.FromRemainder fromRemainder = (DisplayedItemFactory.FromRemainder)factory;
            List list = this.remainder.appendStacks(parameters, factory).toList();
            return this.input.appendStacks(parameters, factory).map(input -> fromRemainder.toDisplayed(input, list));
        }
        return this.input.appendStacks(parameters, factory);
    }

    @Override
    public boolean isEnabled(FeatureSet features) {
        return this.input.isEnabled(features) && this.remainder.isEnabled(features);
    }
}
