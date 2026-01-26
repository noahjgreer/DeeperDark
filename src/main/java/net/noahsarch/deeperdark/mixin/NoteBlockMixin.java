package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.noahsarch.deeperdark.sound.ModSounds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to add organ instrument sound when a copper block is placed beneath a noteblock.
 */
@Mixin(NoteBlock.class)
public class NoteBlockMixin {

    /**
     * Inject into onSyncedBlockEvent to play organ sound when copper is below
     */
    @Inject(method = "onSyncedBlockEvent", at = @At("HEAD"), cancellable = true)
    private void deeperdark$playOrganSound(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
        // Check if there's a copper block below
        BlockState belowState = world.getBlockState(pos.down());
        if (deeperdark$isCopperBlock(belowState)) {
            // Check if block above is air (note blocks need air above to play)
            NoteBlockInstrument instrument = state.get(Properties.INSTRUMENT);
            if (instrument.isNotBaseBlock() || world.getBlockState(pos.up()).isAir()) {
                int note = state.get(Properties.NOTE);
                float pitch = NoteBlock.getNotePitch(note);

                // Show note particle
                world.addParticleClient(ParticleTypes.NOTE,
                    pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
                    note / 24.0, 0.0, 0.0);

                // Play organ sound
                world.playSound(null,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    ModSounds.BLOCK_NOTE_BLOCK_ORGAN,
                    SoundCategory.RECORDS,
                    3.0F, pitch, world.random.nextLong());

                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(false);
            }
        }
    }

    /**
     * Checks if the given block state is any type of copper block
     */
    @Unique
    private boolean deeperdark$isCopperBlock(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.COPPER_BLOCK ||
               block == Blocks.EXPOSED_COPPER ||
               block == Blocks.WEATHERED_COPPER ||
               block == Blocks.OXIDIZED_COPPER ||
               block == Blocks.WAXED_COPPER_BLOCK ||
               block == Blocks.WAXED_EXPOSED_COPPER ||
               block == Blocks.WAXED_WEATHERED_COPPER ||
               block == Blocks.WAXED_OXIDIZED_COPPER ||
               block == Blocks.CUT_COPPER ||
               block == Blocks.EXPOSED_CUT_COPPER ||
               block == Blocks.WEATHERED_CUT_COPPER ||
               block == Blocks.OXIDIZED_CUT_COPPER ||
               block == Blocks.WAXED_CUT_COPPER ||
               block == Blocks.WAXED_EXPOSED_CUT_COPPER ||
               block == Blocks.WAXED_WEATHERED_CUT_COPPER ||
               block == Blocks.WAXED_OXIDIZED_CUT_COPPER;
    }
}
