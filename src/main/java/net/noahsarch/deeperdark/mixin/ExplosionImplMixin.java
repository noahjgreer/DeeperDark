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

    @Redirect(
        method = "interactWithBlocks(Ljava/util/List;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;onExplosionHit(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;Ljava/util/function/BiConsumer;)V"
        )
    )
    private void deeperdark$forceAllBlockDrops(BlockState state, ServerLevel world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (!state.getBlock().dropFromExplosion(explosion)) {
            state.onExplosionHit(world, pos, explosion, stackMerger);
            return;
        }
        if (!state.isAir() && explosion.getBlockInteraction() != Explosion.BlockInteraction.TRIGGER_BLOCK) {
            BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;

            LootParams.Builder builder = new LootParams.Builder(world)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, (net.minecraft.world.item.ItemInstance) ItemStack.EMPTY)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
                .withOptionalParameter(LootContextParams.THIS_ENTITY, explosion.getDirectSourceEntity());

            state.spawnAfterBreak(world, pos, ItemStack.EMPTY, explosion.getDirectSourceEntity() != null);

            state.getDrops(builder).forEach(stack -> stackMerger.accept(stack, pos));

            world.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    @Redirect(
        method = "interactWithBlocks(Ljava/util/List;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/Block;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"
        )
    )
    private void deeperdark$alwaysDropStack(Level world, BlockPos pos, ItemStack stack) {
        if (world instanceof ServerLevel serverWorld && !stack.isEmpty()) {
            ItemEntity itemEntity = new ItemEntity(serverWorld, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
            itemEntity.setDefaultPickUpDelay();
            serverWorld.addFreshEntity(itemEntity);
        } else {
            Block.popResource(world, pos, stack);
        }
    }
}
