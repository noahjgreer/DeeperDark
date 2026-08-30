package net.noahsarch.deeperdark.item;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * Makes water and lava buckets stack to 64.
 *
 * <p>Filled buckets are the only blocker to bulk-crafting igneous sponges
 * ({@link net.noahsarch.deeperdark.recipe.IgneousSpongeRecipe}), and stacking
 * them is safe in every vanilla path that consumes one:
 * <ul>
 *   <li>Using one in hand goes through {@code ItemUtils.createFilledResult},
 *       which shrinks the held stack and routes the empty bucket to the
 *       inventory (or the ground).</li>
 *   <li>Dispensers go through {@code consumeWithRemainder}, which does the
 *       same for the dispenser's own inventory.</li>
 *   <li>Cauldron interactions also use {@code createFilledResult}.</li>
 * </ul>
 *
 * <p>The one path that is <em>not</em> stack-safe is furnace fuel: vanilla only
 * returns the empty bucket once the fuel stack runs out, so a stack of 64 lava
 * buckets would burn 63 buckets away. That is handled by capping the fuel slot
 * at one filled bucket in {@code FurnaceFuelSlotMixin} and
 * {@code FurnaceFuelInsertMixin}, matching how vanilla already caps empty
 * buckets there.
 *
 * <p>Note that this is a client-and-server change: stack sizes live in the
 * item's default components and are not synced, so a vanilla client connecting
 * to a server running this mod would disagree about them.
 */
public final class StackableBuckets {
    /** Max stack size applied to filled buckets. */
    public static final int STACK_SIZE = 64;

    private StackableBuckets() {
    }

    public static boolean isStackableFilledBucket(Item item) {
        return item == Items.WATER_BUCKET || item == Items.LAVA_BUCKET;
    }

    public static void initialize() {
        DefaultItemComponentEvents.MODIFY.register(context -> context.modify(
                item -> isStackableFilledBucket(item),
                (builder, item) -> builder.set(DataComponents.MAX_STACK_SIZE, STACK_SIZE)));
    }
}
