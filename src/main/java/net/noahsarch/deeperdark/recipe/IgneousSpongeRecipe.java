package net.noahsarch.deeperdark.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.block.ModBlocks;

import java.util.function.IntFunction;

/**
 * Magma block + empty bucket = igneous sponge, leaving a lava bucket behind in
 * the slot the empty bucket occupied.
 *
 * <p>Because filled buckets stack (see
 * {@link net.noahsarch.deeperdark.item.StackableBuckets}), the recipe crafts in
 * bulk: the output count is the smaller of the two input stacks. Consuming that
 * many inputs at once is not something vanilla's crafting plumbing does on its
 * own, so the two places that actually run a craft are patched to match:
 * {@code CraftingResultSlotMixin} for menus and {@code CrafterBlockMixin} for
 * the auto-crafter. Both consume exactly {@link #craftCount} of each input, so
 * the scaled output can never outrun what was paid for it.
 */
public class IgneousSpongeRecipe extends CustomRecipe {

    public static final Identifier SERIALIZER_ID = Identifier.fromNamespaceAndPath("deeperdark", "igneous_sponge");

    private static final IgneousSpongeRecipe INSTANCE = new IgneousSpongeRecipe();

    public static final MapCodec<IgneousSpongeRecipe> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, IgneousSpongeRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final RecipeSerializer<IgneousSpongeRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    /**
     * How many igneous sponges the given contents craft at once, which is also
     * how many of each input a single craft consumes.
     *
     * @return the craft count, or 0 if the contents are not a valid craft
     */
    private static int craftCount(int size, IntFunction<ItemStack> slot) {
        int magma = 0;
        int buckets = 0;
        for (int i = 0; i < size; i++) {
            ItemStack stack = slot.apply(i);
            if (stack.isEmpty()) continue;
            // A non-empty stack always has a count of at least one, so zero
            // doubles as "not seen yet" and rejects a second stack of either.
            if (stack.is(Items.MAGMA_BLOCK) && magma == 0) {
                magma = stack.getCount();
            } else if (stack.is(Items.BUCKET) && buckets == 0) {
                buckets = stack.getCount();
            } else {
                return 0;
            }
        }
        return Math.min(magma, buckets);
    }

    public static int craftCount(CraftingInput input) {
        return craftCount(input.size(), input::getItem);
    }

    public static int craftCount(Container container) {
        return craftCount(container.getContainerSize(), container::getItem);
    }

    /** @return the first slot holding {@code item}, or -1 */
    public static int findSlot(Container container, net.minecraft.world.item.Item item) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (container.getItem(i).is(item)) return i;
        }
        return -1;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return craftCount(input) > 0;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        int count = craftCount(input);
        return count > 0 ? new ItemStack(ModBlocks.IGNEOUS_SPONGE, count) : ItemStack.EMPTY;
    }

    /**
     * Vanilla's remainder mechanism is one-per-slot, so this only describes a
     * single craft. The bulk case is handled by the two craft sites named in
     * the class docs, which fill the bucket slot themselves.
     */
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
