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
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.SmithingTrimRecipe;
import net.minecraft.recipe.display.DisplayedItemFactory;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.math.random.Random;

public record SlotDisplay.SmithingTrimSlotDisplay(SlotDisplay base, SlotDisplay material, RegistryEntry<ArmorTrimPattern> pattern) implements SlotDisplay
{
    public static final MapCodec<SlotDisplay.SmithingTrimSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)CODEC.fieldOf("base").forGetter(SlotDisplay.SmithingTrimSlotDisplay::base), (App)CODEC.fieldOf("material").forGetter(SlotDisplay.SmithingTrimSlotDisplay::material), (App)ArmorTrimPattern.ENTRY_CODEC.fieldOf("pattern").forGetter(SlotDisplay.SmithingTrimSlotDisplay::pattern)).apply((Applicative)instance, SlotDisplay.SmithingTrimSlotDisplay::new));
    public static final PacketCodec<RegistryByteBuf, SlotDisplay.SmithingTrimSlotDisplay> PACKET_CODEC = PacketCodec.tuple(PACKET_CODEC, SlotDisplay.SmithingTrimSlotDisplay::base, PACKET_CODEC, SlotDisplay.SmithingTrimSlotDisplay::material, ArmorTrimPattern.ENTRY_PACKET_CODEC, SlotDisplay.SmithingTrimSlotDisplay::pattern, SlotDisplay.SmithingTrimSlotDisplay::new);
    public static final SlotDisplay.Serializer<SlotDisplay.SmithingTrimSlotDisplay> SERIALIZER = new SlotDisplay.Serializer<SlotDisplay.SmithingTrimSlotDisplay>(CODEC, PACKET_CODEC);

    public SlotDisplay.Serializer<SlotDisplay.SmithingTrimSlotDisplay> serializer() {
        return SERIALIZER;
    }

    @Override
    public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
        if (factory instanceof DisplayedItemFactory.FromStack) {
            DisplayedItemFactory.FromStack fromStack = (DisplayedItemFactory.FromStack)factory;
            RegistryWrapper.WrapperLookup wrapperLookup = parameters.getNullable(SlotDisplayContexts.REGISTRIES);
            if (wrapperLookup != null) {
                Random random = Random.create(System.identityHashCode(this));
                List<ItemStack> list = this.base.getStacks(parameters);
                if (list.isEmpty()) {
                    return Stream.empty();
                }
                List<ItemStack> list2 = this.material.getStacks(parameters);
                if (list2.isEmpty()) {
                    return Stream.empty();
                }
                return Stream.generate(() -> {
                    ItemStack itemStack = (ItemStack)Util.getRandom(list, random);
                    ItemStack itemStack2 = (ItemStack)Util.getRandom(list2, random);
                    return SmithingTrimRecipe.craft(wrapperLookup, itemStack, itemStack2, this.pattern);
                }).limit(256L).filter(stack -> !stack.isEmpty()).limit(16L).map(fromStack::toDisplayed);
            }
        }
        return Stream.empty();
    }
}
