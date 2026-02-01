package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
    private static final Identifier ORGAN_SOUND_ID = Identifier.of(Deeperdark.MOD_ID, "block.note_block.organ");

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

                // Play organ sound by manually sending the packet (like /playsound does)
                // This avoids Fabric registry sync issues since we use a Direct RegistryEntry
                if (world instanceof ServerWorld serverWorld) {
                    deeperdark$sendOrganSound(serverWorld, pos, pitch);
                }

                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(false);
            }
        }
    }

    /**
     * Send the organ sound packet to all nearby players using Direct RegistryEntry.
     * This mirrors how the vanilla /playsound command works, bypassing registry sync.
     */
    @Unique
    private void deeperdark$sendOrganSound(ServerWorld world, BlockPos pos, float pitch) {
        // Create a Direct RegistryEntry (not Reference) - same as /playsound command does
        // This bypasses Fabric's registry sync because Direct entries aren't synced
        RegistryEntry<SoundEvent> soundEntry = RegistryEntry.of(SoundEvent.of(ORGAN_SOUND_ID));

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        float volume = 3.0F;
        long seed = world.getRandom().nextLong();

        // Calculate max hearing distance (volume affects range)
        double maxDistSq = Math.pow(soundEntry.value().getDistanceToTravel(volume), 2);

        // Send packet to all players within hearing range
        for (ServerPlayerEntity player : world.getPlayers()) {
            double distSq = player.squaredDistanceTo(x, y, z);
            if (distSq < maxDistSq) {
                player.networkHandler.sendPacket(new PlaySoundS2CPacket(
                    soundEntry,
                    SoundCategory.RECORDS,
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
