package net.noahsarch.deeperdark.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

public class ComponentIngredient implements CustomIngredient {
    public static final Identifier ID = Identifier.fromNamespaceAndPath("deeperdark", "component_ingredient");

    private final ItemStack prototype;

    public ComponentIngredient(ItemStack prototype) {
        this.prototype = prototype;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean test(ItemStack stack) {
        if (stack.getItem() != prototype.getItem()) return false;

        // Match explicit component changes required by the recipe
        net.minecraft.core.component.ComponentChanges changes = prototype.getComponentChanges();
        for (java.util.Map.Entry<net.minecraft.core.component.ComponentType<?>, java.util.Optional<?>> entry : changes.entrySet()) {
            net.minecraft.core.component.ComponentType<?> type = entry.getKey();
            java.util.Optional<?> requiredValue = entry.getValue();

            if (requiredValue.isPresent()) {
                // Requirement: Stack must have this exact value
                if (!java.util.Objects.equals(stack.get(type), requiredValue.get())) {
                    return false;
                }
            } else {
                // Requirement: Stack must NOT have this component
                if (stack.contains(type)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Stream<Holder<Item>> getMatchingItems() {
        return Stream.of(prototype.getItem().getRegistryEntry());
    }

    // @Override // Not part of interface in this version, but useful as helper or for other mods
    public List<ItemStack> getMatchingStacks() {
        return List.of(prototype);
    }

    @Override
    public boolean requiresTesting() {
        return true;
    }

    @Override
    public CustomIngredientSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements CustomIngredientSerializer<ComponentIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        // Codec for JSON/NBT
        private static final MapCodec<ComponentIngredient> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                ItemStack.CODEC.fieldOf("stack").forGetter(i -> i.prototype)
            ).apply(instance, ComponentIngredient::new)
        );

        // Packet Codec for Sync
        private static final StreamCodec<RegistryFriendlyByteBuf, ComponentIngredient> PACKET_CODEC = ItemStack.PACKET_CODEC
            .xmap(ComponentIngredient::new, i -> i.prototype);

        @Override
        public Identifier getIdentifier() {
            return ID;
        }

        @Override
        public MapCodec<ComponentIngredient> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ComponentIngredient> getPacketCodec() {
            return PACKET_CODEC;
        }
    }
}

