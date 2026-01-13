/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.recipe.display;

import com.mojang.serialization.MapCodec;
import java.util.stream.Stream;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.display.DisplayedItemFactory;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.util.context.ContextParameterMap;

public static class SlotDisplay.EmptySlotDisplay
implements SlotDisplay {
    public static final SlotDisplay.EmptySlotDisplay INSTANCE = new SlotDisplay.EmptySlotDisplay();
    public static final MapCodec<SlotDisplay.EmptySlotDisplay> CODEC = MapCodec.unit((Object)INSTANCE);
    public static final PacketCodec<RegistryByteBuf, SlotDisplay.EmptySlotDisplay> PACKET_CODEC = PacketCodec.unit(INSTANCE);
    public static final SlotDisplay.Serializer<SlotDisplay.EmptySlotDisplay> SERIALIZER = new SlotDisplay.Serializer<SlotDisplay.EmptySlotDisplay>(CODEC, PACKET_CODEC);

    private SlotDisplay.EmptySlotDisplay() {
    }

    public SlotDisplay.Serializer<SlotDisplay.EmptySlotDisplay> serializer() {
        return SERIALIZER;
    }

    public String toString() {
        return "<empty>";
    }

    @Override
    public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
        return Stream.empty();
    }
}
