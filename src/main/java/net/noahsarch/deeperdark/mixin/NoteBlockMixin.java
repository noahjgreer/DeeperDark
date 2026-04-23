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

@Mixin(NoteBlock.class)
public class NoteBlockMixin {

    @Unique
    private static final Identifier ORGAN_SOUND_ID = Identifier.fromNamespaceAndPath(Deeperdark.MOD_ID, "block.note_block.organ");

    @Inject(method = "triggerEvent", at = @At("HEAD"), cancellable = true)
    private void deeperdark$playOrganSound(BlockState state, Level world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
        BlockState belowState = world.getBlockState(pos.below());
        if (deeperdark$isCopperBlock(belowState)) {
            NoteBlockInstrument instrument = state.getValue(BlockStateProperties.NOTEBLOCK_INSTRUMENT);
            if (!instrument.isTunable() || world.getBlockState(pos.above()).isAir()) {
                int note = state.getValue(BlockStateProperties.NOTE);
                float pitch = NoteBlock.getPitchFromNote(note);

                world.addParticle(ParticleTypes.NOTE,
                    pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
                    note / 24.0, 0.0, 0.0);

                if (world instanceof ServerLevel serverWorld) {
                    deeperdark$sendOrganSound(serverWorld, pos, pitch);
                }

                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(false);
            }
        }
    }

    @Unique
    private void deeperdark$sendOrganSound(ServerLevel world, BlockPos pos, float pitch) {
        Holder<SoundEvent> soundEntry = Holder.direct(SoundEvent.createVariableRangeEvent(ORGAN_SOUND_ID));

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        float volume = 3.0F;
        long seed = world.getRandom().nextLong();

        double maxDistSq = Math.pow(soundEntry.value().getRange(volume), 2);

        for (ServerPlayer player : world.players()) {
            double distSq = player.distanceToSqr(x, y, z);
            if (distSq < maxDistSq) {
                player.connection.send(new ClientboundSoundPacket(
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
