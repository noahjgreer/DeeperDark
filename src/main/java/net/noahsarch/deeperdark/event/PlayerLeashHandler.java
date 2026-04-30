package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.noahsarch.deeperdark.payload.PlayerLeashPacket;

public class PlayerLeashHandler {

    private static final double LEASH_ELASTIC_DIST = 6.0;
    private static final double PULL_STRENGTH = 0.12;

    public static void register() {
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (!(entity instanceof Player target)) return InteractionResult.PASS;
            if (!player.getItemInHand(hand).is(Items.LEAD)) return InteractionResult.PASS;
            if (level.isClientSide()) return InteractionResult.SUCCESS;

            Leashable leashable = (Leashable) target;

            if (leashable.isLeashed() && player == leashable.getLeashHolder()) {
                leashable.removeLeash();
                // removeLeash() broadcasts to trackers; custom packet tells the leashed player's own client
                ServerPlayNetworking.send((ServerPlayer) target, new PlayerLeashPacket(target.getId(), -1));
                level.playSound(null, player.blockPosition(), SoundEvents.LEAD_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
            } else if (!target.getUUID().equals(player.getUUID())) {
                leashable.setLeashedTo(player, true);
                // setLeashedTo broadcasts to trackers; custom packet tells the leashed player's own client
                ServerPlayNetworking.send((ServerPlayer) target, new PlayerLeashPacket(target.getId(), player.getId()));
                if (!player.hasInfiniteMaterials()) {
                    player.getItemInHand(hand).shrink(1);
                }
                level.playSound(null, player.blockPosition(), SoundEvents.LEAD_TIED, SoundSource.PLAYERS, 1.0F, 1.0F);
            } else {
                return InteractionResult.PASS;
            }

            return InteractionResult.SUCCESS;
        });

        // Pull leashed players toward their holder. Uses hurtMarked to force velocity sync to client,
        // which is necessary because player movement is client-authoritative.
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer leashed : server.getPlayerList().getPlayers()) {
                Leashable leashable = (Leashable) leashed;
                if (!leashable.isLeashed()) continue;
                Entity holder = leashable.getLeashHolder();
                if (holder == null) continue;

                double dist = leashed.distanceTo(holder);
                if (dist > LEASH_ELASTIC_DIST) {
                    Vec3 pull = holder.position().subtract(leashed.position()).normalize()
                            .scale(PULL_STRENGTH * (dist - LEASH_ELASTIC_DIST));
                    leashed.setDeltaMovement(leashed.getDeltaMovement().add(pull));
                    leashed.hurtMarked = true;
                }
            }
        });
    }
}
