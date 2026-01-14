package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ExplosionImpl.class)
public class ExplosionImplMixin {

    @Redirect(
        method = "destroyBlocks(Ljava/util/List;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;dropStack(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)V"
        )
    )
    private static void deeperdark$alwaysDropStack(World world, BlockPos pos, ItemStack stack) {
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

