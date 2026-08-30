package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.noahsarch.deeperdark.recipe.IgneousSpongeRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Bulk igneous sponge crafting in the auto-crafter.
 *
 * <p>{@code dispenseFrom} dispenses {@code recipe.assemble(...)} but always
 * shrinks each input slot by one. Since
 * {@link IgneousSpongeRecipe} scales its output to the smaller input stack,
 * leaving that alone would let a crafter fed 64 magma blocks and 64 buckets
 * emit 64 sponges for one of each — so the craft is taken over here and the
 * inputs are consumed in full, mirroring what a player gets at a crafting table.
 */
@Mixin(CrafterBlock.class)
public abstract class CrafterBlockMixin {

    @Shadow
    private native void dispenseItem(ServerLevel level, BlockPos pos, CrafterBlockEntity blockEntity,
            ItemStack results, BlockState blockState, RecipeHolder<?> recipe);

    @Inject(method = "dispenseFrom", at = @At("HEAD"), cancellable = true)
    private void deeperdark$bulkIgneousSpongeCraft(BlockState state, ServerLevel level, BlockPos pos, CallbackInfo ci) {
        if (!(level.getBlockEntity(pos) instanceof CrafterBlockEntity blockEntity)) return;

        int crafts = IgneousSpongeRecipe.craftCount(blockEntity);
        if (crafts <= 1) return; // one-per-slot is already correct; let vanilla run

        var recipe = CrafterBlock.getPotentialResults(level, blockEntity.asCraftInput());
        if (recipe.isEmpty() || !(recipe.get().value() instanceof IgneousSpongeRecipe)) return;

        RecipeHolder<CraftingRecipe> pickedRecipe = recipe.get();
        ItemStack results = new ItemStack(net.noahsarch.deeperdark.block.ModBlocks.IGNEOUS_SPONGE, crafts);

        blockEntity.setCraftingTicksRemaining(6);
        level.setBlock(pos, state.setValue(CrafterBlock.CRAFTING, true), 2);
        results.onCraftedBySystem(level);
        this.dispenseItem(level, pos, blockEntity, results, state, pickedRecipe);
        this.dispenseItem(level, pos, blockEntity, new ItemStack(Items.LAVA_BUCKET, crafts), state, pickedRecipe);

        for (ItemStack stack : blockEntity.getItems()) {
            if (!stack.isEmpty()) {
                stack.shrink(crafts);
            }
        }
        blockEntity.setChanged();
        ci.cancel();
    }
}
