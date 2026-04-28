package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.noahsarch.deeperdark.event.MossGrowthHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(AxeItem.class)
public class AxeItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void onUseOnBlock(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Map<Block, Block> strippedBlocks = AxeItemAccessor.getStrippedBlocks();
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (strippedBlocks.containsKey(state.getBlock())) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Inject(method = "useOn", at = @At("RETURN"), cancellable = true)
    private void deeperdark$stripMoss(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        // Only step in when vanilla left the interaction unhandled
        if (cir.getReturnValue() != InteractionResult.PASS) return;

        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        BlockState state = level.getBlockState(pos);
        Block stripped = MossGrowthHandler.STRIPPED.get(state.getBlock());
        if (stripped == null) return;

        if (!level.isClientSide()) {
            BlockState newState = copyProperties(state, stripped.defaultBlockState());
            level.setBlock(pos, newState, Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0f, 1.0f);
            context.getItemInHand().hurtAndBreak(1, context.getPlayer(), context.getHand());
        }

        cir.setReturnValue(level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER);
    }

    @SuppressWarnings("unchecked")
    private static BlockState copyProperties(BlockState from, BlockState to) {
        for (var prop : from.getProperties()) {
            if (to.hasProperty(prop)) {
                to = copyProp(from, to,
                        (net.minecraft.world.level.block.state.properties.Property) prop);
            }
        }
        return to;
    }

    private static <T extends Comparable<T>> BlockState copyProp(
            BlockState from, BlockState to,
            net.minecraft.world.level.block.state.properties.Property<T> prop) {
        return to.setValue(prop, from.getValue(prop));
    }
}
