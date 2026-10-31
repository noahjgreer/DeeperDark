package net.noahsarch.deeperdark.mixin;

import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemLadderMixin {

    @Shadow
    public abstract Block getBlock();

    /**
     * Using a ladder on the front of a ladder, or on its top while standing in the space above it,
     * hangs the new ladder under the bottom of that ladder column, so a ladder can be lowered from
     * a ledge one click at a time. Fails once the column reaches something it can't replace.
     */
    @Inject(method = "updatePlacementContext", at = @At("HEAD"), cancellable = true)
    private void deeperdark$extendLadderDown(BlockPlaceContext context,
            CallbackInfoReturnable<BlockPlaceContext> cir) {
        if (getBlock() != Blocks.LADDER || context.replacingClickedOnBlock()
                || !DeeperDarkConfig.get().ladderTweaks) return;

        Level level = context.getLevel();
        Direction face = context.getClickedFace();
        BlockPos target = context.getClickedPos();
        BlockPos below = target.relative(face.getOpposite());
        BlockState state = level.getBlockState(below);
        if (!state.is(Blocks.LADDER)) return;

        Direction facing = state.getValue(LadderBlock.FACING);
        Player player = context.getPlayer();
        boolean standingAbove = face == Direction.UP && player != null
                && player.getBoundingBox().intersects(new AABB(target));
        if (face != facing && !standingAbove) return;

        while (state.is(Blocks.LADDER) && state.getValue(LadderBlock.FACING) == facing) {
            below = below.below();
            state = level.getBlockState(below);
        }

        // DOWN face makes LadderBlockMixin copy the facing of the ladder above.
        BlockPlaceContext next = BlockPlaceContext.at(context, below, Direction.DOWN);
        cir.setReturnValue(level.isInWorldBounds(below) && next.replacingClickedOnBlock() ? next : null);
    }
}
