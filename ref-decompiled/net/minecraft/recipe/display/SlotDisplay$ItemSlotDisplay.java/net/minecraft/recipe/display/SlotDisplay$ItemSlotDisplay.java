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
import java.util.stream.Stream;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.display.DisplayedItemFactory;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.context.ContextParameterMap;

public record SlotDisplay.ItemSlotDisplay(RegistryEntry<Item> item) implements SlotDisplay
{
    public static final MapCodec<SlotDisplay.ItemSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Item.ENTRY_CODEC.fieldOf("item").forGetter(SlotDisplay.ItemSlotDisplay::item)).apply((Applicative)instance, SlotDisplay.ItemSlotDisplay::new));
    public static final PacketCodec<RegistryByteBuf, SlotDisplay.ItemSlotDisplay> PACKET_CODEC = PacketCodec.tuple(Item.ENTRY_PACKET_CODEC, SlotDisplay.ItemSlotDisplay::item, SlotDisplay.ItemSlotDisplay::new);
    public static final SlotDisplay.Serializer<SlotDisplay.ItemSlotDisplay> SERIALIZER = new SlotDisplay.Serializer<SlotDisplay.ItemSlotDisplay>(CODEC, PACKET_CODEC);

    public SlotDisplay.ItemSlotDisplay(Item item) {
        this(item.getRegistryEntry());
    }

    public SlotDisplay.Serializer<SlotDisplay.ItemSlotDisplay> serializer() {
        return SERIALIZER;
    }

    @Override
    public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
        if (factory instanceof DisplayedItemFactory.FromStack) {
            DisplayedItemFactory.FromStack fromStack = (DisplayedItemFactory.FromStack)factory;
            return Stream.of(fromStack.toDisplayed(this.item));
        }
        return Stream.empty();
    }

    @Override
    public boolean isEnabled(FeatureSet features) {
        return this.item.value().isEnabled(features);
    }
}
