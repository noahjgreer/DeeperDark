/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.recipe.display;

import com.mojang.serialization.MapCodec;
import java.util.stream.Stream;
import net.minecraft.item.FuelRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.display.DisplayedItemFactory;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.util.context.ContextParameterMap;

public static class SlotDisplay.AnyFuelSlotDisplay
implements SlotDisplay {
    public static final SlotDisplay.AnyFuelSlotDisplay INSTANCE = new SlotDisplay.AnyFuelSlotDisplay();
    public static final MapCodec<SlotDisplay.AnyFuelSlotDisplay> CODEC = MapCodec.unit((Object)INSTANCE);
    public static final PacketCodec<RegistryByteBuf, SlotDisplay.AnyFuelSlotDisplay> PACKET_CODEC = PacketCodec.unit(INSTANCE);
    public static final SlotDisplay.Serializer<SlotDisplay.AnyFuelSlotDisplay> SERIALIZER = new SlotDisplay.Serializer<SlotDisplay.AnyFuelSlotDisplay>(CODEC, PACKET_CODEC);

    private SlotDisplay.AnyFuelSlotDisplay() {
    }

    public SlotDisplay.Serializer<SlotDisplay.AnyFuelSlotDisplay> serializer() {
        return SERIALIZER;
    }

    public String toString() {
        return "<any fuel>";
    }

    @Override
    public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
        if (factory instanceof DisplayedItemFactory.FromStack) {
            DisplayedItemFactory.FromStack fromStack = (DisplayedItemFactory.FromStack)factory;
            FuelRegistry fuelRegistry = parameters.getNullable(SlotDisplayContexts.FUEL_REGISTRY);
            if (fuelRegistry != null) {
                return fuelRegistry.getFuelItems().stream().map(fromStack::toDisplayed);
            }
        }
        return Stream.empty();
    }
}
