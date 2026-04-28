package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class PlayerLeashHandler {

    private static final double LEASH_SNAP_DIST = 24.0;
    private static final double LEASH_ELASTIC_DIST = 6.0;
    private static final double PULL_STRENGTH = 0.12;

    private static final Map<UUID, UUID> leashedToHolder = new HashMap<>();

    public static void register() {
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (!(entity instanceof Player target)) return InteractionResult.PASS;
            if (!player.getItemInHand(hand).is(Items.LEAD)) return InteractionResult.PASS;
            if (level.isClientSide()) return InteractionResult.SUCCESS;

            UUID targetId = target.getUUID();
            UUID holderId = player.getUUID();

            if (leashedToHolder.containsKey(targetId) && holderId.equals(leashedToHolder.get(targetId))) {
                leashedToHolder.remove(targetId);
                level.playSound(null, player.blockPosition(), SoundEvents.LEAD_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
            } else if (!targetId.equals(holderId)) {
                leashedToHolder.put(targetId, holderId);
                level.playSound(null, player.blockPosition(), SoundEvents.LEAD_TIED, SoundSource.PLAYERS, 1.0F, 1.0F);
            } else {
                return InteractionResult.PASS;
            }

            return InteractionResult.SUCCESS;
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<Map.Entry<UUID, UUID>> iter = leashedToHolder.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<UUID, UUID> entry = iter.next();
                ServerPlayer leashed = server.getPlayerList().getPlayer(entry.getKey());
                ServerPlayer holder = server.getPlayerList().getPlayer(entry.getValue());

                if (leashed == null || holder == null) {
                    iter.remove();
                    continue;
                }

                double dist = leashed.distanceTo(holder);
                if (dist > LEASH_SNAP_DIST) {
                    iter.remove();
                    leashed.level().playSound(null, leashed.blockPosition(), SoundEvents.LEAD_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                    continue;
                }

                if (dist > LEASH_ELASTIC_DIST) {
                    Vec3 pull = holder.position().subtract(leashed.position()).normalize().scale(PULL_STRENGTH * (dist - LEASH_ELASTIC_DIST));
                    leashed.setDeltaMovement(leashed.getDeltaMovement().add(pull));
                    leashed.hurtMarked = true;
                }
            }
        });
    }

    public static boolean isLeashed(Player player) {
        return leashedToHolder.containsKey(player.getUUID());
    }

    public static void releaseLeash(Player player) {
        leashedToHolder.remove(player.getUUID());
    }
}
