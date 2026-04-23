package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.entity.monster.EnderMan$EndermanTakeBlockGoal")
public class EndermanPickUpBlockMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean deeperdark$allowAllBlocks(BlockState state, net.minecraft.tags.TagKey<?> tag) {
        if (DeeperDarkConfig.get().endermanPickUpAllBlocks) {
            return state.getBlock() != Blocks.BEDROCK &&
                   state.getBlock() != Blocks.END_PORTAL_FRAME &&
                   state.getBlock() != Blocks.BARRIER &&
                   state.getBlock() != Blocks.COMMAND_BLOCK &&
                   state.getBlock() != Blocks.CHAIN_COMMAND_BLOCK &&
                   state.getBlock() != Blocks.REPEATING_COMMAND_BLOCK &&
                   state.getBlock() != Blocks.STRUCTURE_BLOCK &&
                   state.getBlock() != Blocks.JIGSAW &&
                   !state.isAir();
        }
        return state.is(BlockTags.ENDERMAN_HOLDABLE);
    }
}
