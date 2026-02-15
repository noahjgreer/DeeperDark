package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Mixin to make endermen optionally pick up all blocks (except bedrock).
 */
@Mixin(targets = "net.minecraft.entity.mob.EndermanEntity$PickUpBlockGoal")
public class EndermanPickUpBlockMixin {

    /**
     * Redirect the block tag check to allow all blocks when config is enabled
     */
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    private boolean deeperdark$allowAllBlocks(BlockState state, net.minecraft.registry.tag.TagKey<?> tag) {
        if (DeeperDarkConfig.get().endermanPickUpAllBlocks) {
            // Allow all blocks except bedrock, end portal frame, and barrier
            return !state.isOf(Blocks.BEDROCK) &&
                   !state.isOf(Blocks.END_PORTAL_FRAME) &&
                   !state.isOf(Blocks.BARRIER) &&
                   !state.isOf(Blocks.COMMAND_BLOCK) &&
                   !state.isOf(Blocks.CHAIN_COMMAND_BLOCK) &&
                   !state.isOf(Blocks.REPEATING_COMMAND_BLOCK) &&
                   !state.isOf(Blocks.STRUCTURE_BLOCK) &&
                   !state.isOf(Blocks.JIGSAW) &&
                   !state.isAir();
        }
        // Default behavior: use the original tag check
        return state.isIn(BlockTags.ENDERMAN_HOLDABLE);
    }
}
