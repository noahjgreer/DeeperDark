package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BiConsumer;
import net.minecraft.world.level.gamerules.GameRules;

@Mixin(ServerExplosion.class)
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
            target = "Lnet/minecraft/block/BlockState;onExploded(Lnet/minecraft/server/world/ServerLevel;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/explosion/Explosion;Ljava/util/function/BiConsumer;)V"
        )
    )
    private void deeperdark$forceAllBlockDrops(BlockState state, ServerLevel world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        // Only process non-air blocks
        if (!state.isAir() && explosion.getDestructionType() != Explosion.DestructionType.TRIGGER_BLOCK) {
            // Get the block entity if present
            BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;

            // Build loot context WITHOUT the explosion radius parameter
            // This bypasses the survives_explosion condition that causes random drops
            LootParams.Builder builder = new LootParams.Builder(world)
                .add(LootContextParams.ORIGIN, Vec3.ofCenter(pos))
                .add(LootContextParams.TOOL, ItemStack.EMPTY)
                .addOptional(LootContextParams.BLOCK_ENTITY, blockEntity)
                .addOptional(LootContextParams.THIS_ENTITY, explosion.getEntity());
            // NOTE: We intentionally do NOT add EXPLOSION_RADIUS here

            // Call onStacksDropped for experience/other effects
            state.onStacksDropped(world, pos, ItemStack.EMPTY, explosion.getCausingEntity() != null);

            // Get all drops and add them to the merger
            state.getDroppedStacks(builder).forEach(stack -> stackMerger.accept(stack, pos));

            // Set block to air
            world.setBlockState(pos, net.minecraft.world.level.block.Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
        }
    }

    /**
     * Force drops to spawn even if doTileDrops is false
     */
    @Redirect(
        method = "destroyBlocks(Ljava/util/List;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;dropStack(Lnet/minecraft/world/Level;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)V"
        )
    )
    private void deeperdark$alwaysDropStack(Level world, BlockPos pos, ItemStack stack) {
        if (world instanceof ServerLevel serverWorld && !stack.isEmpty()) {
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

