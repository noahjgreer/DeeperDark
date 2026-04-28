package net.noahsarch.deeperdark.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class WoolShearingRecipe extends CustomRecipe {

    public static final Identifier SERIALIZER_ID = Identifier.fromNamespaceAndPath("deeperdark", "wool_shearing");

    private static final WoolShearingRecipe INSTANCE = new WoolShearingRecipe();

    public static final MapCodec<WoolShearingRecipe> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, WoolShearingRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final RecipeSerializer<WoolShearingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.ingredientCount() != 2) return false;
        boolean hasWool = false;
        boolean hasShears = false;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.is(ItemTags.WOOL) && !hasWool) {
                hasWool = true;
            } else if (stack.is(Items.SHEARS) && !hasShears) {
                hasShears = true;
            } else {
                return false;
            }
        }
        return hasWool && hasShears;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        return new ItemStack(Items.STRING, 4);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.is(Items.SHEARS)) {
                if (stack.getDamageValue() + 1 < stack.getMaxDamage()) {
                    ItemStack damaged = stack.copy();
                    damaged.setDamageValue(stack.getDamageValue() + 1);
                    remaining.set(i, damaged);
                }
                break;
            }
        }
        return remaining;
    }

    @Override
    public RecipeSerializer<WoolShearingRecipe> getSerializer() {
        return SERIALIZER;
    }
}
