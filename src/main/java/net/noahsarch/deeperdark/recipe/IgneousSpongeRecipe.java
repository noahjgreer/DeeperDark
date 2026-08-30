package net.noahsarch.deeperdark.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.block.ModBlocks;

/**
 * Magma block + empty bucket = igneous sponge, leaving a lava bucket behind in
 * the slot the empty bucket occupied.
 */
public class IgneousSpongeRecipe extends CustomRecipe {

    public static final Identifier SERIALIZER_ID = Identifier.fromNamespaceAndPath("deeperdark", "igneous_sponge");

    private static final IgneousSpongeRecipe INSTANCE = new IgneousSpongeRecipe();

    public static final MapCodec<IgneousSpongeRecipe> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, IgneousSpongeRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final RecipeSerializer<IgneousSpongeRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.ingredientCount() != 2) return false;
        boolean hasMagma = false;
        boolean hasBucket = false;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            // A stack of more than one would be silently consumed for a single
            // output, so require exactly one of each.
            if (stack.getCount() != 1) return false;
            if (stack.is(Items.MAGMA_BLOCK) && !hasMagma) {
                hasMagma = true;
            } else if (stack.is(Items.BUCKET) && !hasBucket) {
                hasBucket = true;
            } else {
                return false;
            }
        }
        return hasMagma && hasBucket;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        return new ItemStack(ModBlocks.IGNEOUS_SPONGE);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        for (int i = 0; i < input.size(); i++) {
            if (input.getItem(i).is(Items.BUCKET)) {
                remaining.set(i, new ItemStack(Items.LAVA_BUCKET));
                break;
            }
        }
        return remaining;
    }

    @Override
    public RecipeSerializer<IgneousSpongeRecipe> getSerializer() {
        return SERIALIZER;
    }
}
