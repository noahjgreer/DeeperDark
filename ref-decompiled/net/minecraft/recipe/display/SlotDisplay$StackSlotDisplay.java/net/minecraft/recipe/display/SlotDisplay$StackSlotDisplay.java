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
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.display.DisplayedItemFactory;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.context.ContextParameterMap;

public record SlotDisplay.StackSlotDisplay(ItemStack stack) implements SlotDisplay
{
    public static final MapCodec<SlotDisplay.StackSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)ItemStack.VALIDATED_CODEC.fieldOf("item").forGetter(SlotDisplay.StackSlotDisplay::stack)).apply((Applicative)instance, SlotDisplay.StackSlotDisplay::new));
    public static final PacketCodec<RegistryByteBuf, SlotDisplay.StackSlotDisplay> PACKET_CODEC = PacketCodec.tuple(ItemStack.PACKET_CODEC, SlotDisplay.StackSlotDisplay::stack, SlotDisplay.StackSlotDisplay::new);
    public static final SlotDisplay.Serializer<SlotDisplay.StackSlotDisplay> SERIALIZER = new SlotDisplay.Serializer<SlotDisplay.StackSlotDisplay>(CODEC, PACKET_CODEC);

    public SlotDisplay.Serializer<SlotDisplay.StackSlotDisplay> serializer() {
        return SERIALIZER;
    }

    @Override
    public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
        if (factory instanceof DisplayedItemFactory.FromStack) {
            DisplayedItemFactory.FromStack fromStack = (DisplayedItemFactory.FromStack)factory;
            return Stream.of(fromStack.toDisplayed(this.stack));
        }
        return Stream.empty();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SlotDisplay.StackSlotDisplay)) return false;
        SlotDisplay.StackSlotDisplay stackSlotDisplay = (SlotDisplay.StackSlotDisplay)o;
        if (!ItemStack.areEqual(this.stack, stackSlotDisplay.stack)) return false;
        return true;
    }

    @Override
    public boolean isEnabled(FeatureSet features) {
        return this.stack.getItem().isEnabled(features);
    }
}
