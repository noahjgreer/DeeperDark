package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.noahsarch.deeperdark.block.ModBlocks;
import net.noahsarch.deeperdark.block.QuicksandBlock;

public class BootsOnSoftGroundHandler {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                tick(player);
            }
        });
    }

    private static void tick(ServerPlayer player) {
        if (!player.onGround()) return;

        // Check the block directly under the player's feet.
        BlockState below = player.level().getBlockState(player.blockPosition().below());
        boolean onQuicksand = below.is(ModBlocks.QUICKSAND);
        boolean onPowderSnow = below.is(Blocks.POWDER_SNOW);
        if (!onQuicksand && !onPowderSnow) return;

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.isEmpty()) return;  // no boots → handled by vanilla sinking, not here

        if (!boots.is(Items.LEATHER_BOOTS)) {
            // Any other boots: player walks on the surface but moves like soul sand (speedFactor ~0.4).
            // Slowness IV (amplifier 3) ≈ 40% of normal speed, refreshed every tick.
            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 4, 3, false, false, false));
        }
    }
}
