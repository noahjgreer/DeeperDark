package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BiConsumer;

@Mixin(ExplosionImpl.class)
public class ExplosionImplMixin {


    /**
     * Intercept the onExploded call to force all blocks to drop items, bypassing the explosion decay logic.
     * Instead of relying on the block's loot table (which uses survives_explosion condition), we manually
     * calculate drops without the explosion radius parameter.
     */
    @Redirect(
        method = "destroyBlocks(Ljava/util/List;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;onExploded(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/explosion/Explosion;Ljava/util/function/BiConsumer;)V"
        )
    )
    private void deeperdark$forceAllBlockDrops(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        // Only process non-air blocks
        if (!state.isAir() && explosion.getDestructionType() != Explosion.DestructionType.TRIGGER_BLOCK) {
            // Get the block entity if present
            BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;

            // Build loot context WITHOUT the explosion radius parameter
            // This bypasses the survives_explosion condition that causes random drops
            LootWorldContext.Builder builder = new LootWorldContext.Builder(world)
                .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                .add(LootContextParameters.TOOL, ItemStack.EMPTY)
                .addOptional(LootContextParameters.BLOCK_ENTITY, blockEntity)
                .addOptional(LootContextParameters.THIS_ENTITY, explosion.getEntity());
            // NOTE: We intentionally do NOT add EXPLOSION_RADIUS here

            // Call onStacksDropped for experience/other effects
            state.onStacksDropped(world, pos, ItemStack.EMPTY, explosion.getCausingEntity() != null);

            // Get all drops and add them to the merger
            state.getDroppedStacks(builder).forEach(stack -> stackMerger.accept(stack, pos));

            // Set block to air
            world.setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
        }
    }

    /**
     * Force drops to spawn even if doTileDrops is false
     */
    @Redirect(
        method = "destroyBlocks(Ljava/util/List;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;dropStack(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)V"
        )
    )
    private void deeperdark$alwaysDropStack(World world, BlockPos pos, ItemStack stack) {
        if (world instanceof ServerWorld serverWorld && !stack.isEmpty()) {
            // Spawn an ItemEntity directly, ignoring GameRules.DO_TILE_DROPS so explosions always drop items
            ItemEntity itemEntity = new ItemEntity(serverWorld, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
            itemEntity.setToDefaultPickupDelay();
            serverWorld.spawnEntity(itemEntity);
        } else {
            // Fallback to vanilla behavior (covers client/world edge cases)
            Block.dropStack(world, pos, stack);
        }
    }
}

