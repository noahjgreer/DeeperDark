package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.Deeperdark;
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

    @Unique
    private static final Identifier ORGAN_SOUND_ID = Identifier.fromNamespaceAndPath(Deeperdark.MOD_ID, "block.note_block.organ");

    /**
     * Inject into onSyncedBlockEvent to play organ sound when copper is below
     */
    @Inject(method = "onSyncedBlockEvent", at = @At("HEAD"), cancellable = true)
    private void deeperdark$playOrganSound(BlockState state, Level world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
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

                // Play organ sound by manually sending the packet (like /playsound does)
                // This avoids Fabric registry sync issues since we use a Direct Holder
                if (world instanceof ServerLevel serverWorld) {
                    deeperdark$sendOrganSound(serverWorld, pos, pitch);
                }

                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(false);
            }
        }
    }

    /**
     * Send the organ sound packet to all nearby players using Direct Holder.
     * This mirrors how the vanilla /playsound command works, bypassing registry sync.
     */
    @Unique
    private void deeperdark$sendOrganSound(ServerLevel world, BlockPos pos, float pitch) {
        // Create a Direct Holder (not Reference) - same as /playsound command does
        // This bypasses Fabric's registry sync because Direct entries aren't synced
        Holder<SoundEvent> soundEntry = Holder.of(SoundEvent.of(ORGAN_SOUND_ID));

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        float volume = 3.0F;
        long seed = world.getRandom().nextLong();

        // Calculate max hearing distance (volume affects range)
        double maxDistSq = Math.pow(soundEntry.value().getDistanceToTravel(volume), 2);

        // Send packet to all players within hearing range
        for (ServerPlayer player : world.getPlayers()) {
            double distSq = player.squaredDistanceTo(x, y, z);
            if (distSq < maxDistSq) {
                player.networkHandler.sendPacket(new ClientboundSoundPacket(
                    soundEntry,
                    SoundSource.RECORDS,
                    x, y, z,
                    volume,
                    pitch,
                    seed
                ));
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
