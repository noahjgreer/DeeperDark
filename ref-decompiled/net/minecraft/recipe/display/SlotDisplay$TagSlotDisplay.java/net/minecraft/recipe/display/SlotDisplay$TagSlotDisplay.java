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
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.context.ContextParameterMap;

public record SlotDisplay.TagSlotDisplay(TagKey<Item> tag) implements SlotDisplay
{
    public static final MapCodec<SlotDisplay.TagSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)TagKey.unprefixedCodec(RegistryKeys.ITEM).fieldOf("tag").forGetter(SlotDisplay.TagSlotDisplay::tag)).apply((Applicative)instance, SlotDisplay.TagSlotDisplay::new));
    public static final PacketCodec<RegistryByteBuf, SlotDisplay.TagSlotDisplay> PACKET_CODEC = PacketCodec.tuple(TagKey.packetCodec(RegistryKeys.ITEM), SlotDisplay.TagSlotDisplay::tag, SlotDisplay.TagSlotDisplay::new);
    public static final SlotDisplay.Serializer<SlotDisplay.TagSlotDisplay> SERIALIZER = new SlotDisplay.Serializer<SlotDisplay.TagSlotDisplay>(CODEC, PACKET_CODEC);

    public SlotDisplay.Serializer<SlotDisplay.TagSlotDisplay> serializer() {
        return SERIALIZER;
    }

    @Override
    public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
        if (factory instanceof DisplayedItemFactory.FromStack) {
            DisplayedItemFactory.FromStack fromStack = (DisplayedItemFactory.FromStack)factory;
            RegistryWrapper.WrapperLookup wrapperLookup = parameters.getNullable(SlotDisplayContexts.REGISTRIES);
            if (wrapperLookup != null) {
                return wrapperLookup.getOrThrow(RegistryKeys.ITEM).getOptional(this.tag).map(tag -> tag.stream().map(fromStack::toDisplayed)).stream().flatMap(values -> values);
            }
        }
        return Stream.empty();
    }
}
