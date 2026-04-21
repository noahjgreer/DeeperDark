package net.noahsarch.deeperdark.mixin;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import net.minecraft.world.item.context.UseOnContext;

@Mixin(ShearsItem.class)
public class ShearsItemMixin {
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void onUseOnBlock(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        Map<Block, Block> strippedBlocks = AxeItemAccessor.getStrippedBlocks();

        Block strippedBlock = strippedBlocks.get(blockState.getBlock());
        if (strippedBlock != null) {
            Player playerEntity = context.getPlayer();
            ItemStack itemStack = context.getStack();
            if (playerEntity instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)playerEntity, blockPos, itemStack);
            }

            world.playSound(playerEntity, blockPos, SoundEvents.ITEM_AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);

            BlockState newState = strippedBlock.getDefaultState();
            if (blockState.contains(RotatedPillarBlock.AXIS)) {
                newState = newState.with(RotatedPillarBlock.AXIS, blockState.get(RotatedPillarBlock.AXIS));
            }

            world.setBlockState(blockPos, newState, 11);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, newState));

            if (playerEntity != null) {
                itemStack.damage(1, playerEntity, context.getHand());
            }

            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}

