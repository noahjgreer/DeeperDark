package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.Deeperdark;
import net.noahsarch.deeperdark.duck.EntityAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Mixin to play a sound when players craft items on the crafting table.
 * Uses the same Direct Holder approach as the organ noteblock sound.
 */
@Mixin(ResultSlot.class)
public class CraftingResultSlotMixin {

    @Shadow @Final private Player player;

    @Unique
    private static final Identifier CRAFTING_SOUND_ID = Identifier.fromNamespaceAndPath(Deeperdark.MOD_ID, "block.crafting_table.craft");

    /**
     * Debounce map to track last craft time per player UUID.
     * Prevents sound spam when shift-clicking to craft multiple items.
     */
    @Unique
    private static final Map<UUID, Long> LAST_CRAFT_TIME = new WeakHashMap<>();

    /**
     * Minimum time between craft sounds in milliseconds.
     * 200ms is enough to prevent spam while still feeling responsive.
     */
    @Unique
    private static final long CRAFT_SOUND_COOLDOWN_MS = 200;

    /**
     * Play crafting sound when item is taken from crafting result slot.
     * Uses debouncing to prevent sound spam when shift-clicking to craft multiple items.
     */
    @Inject(method = "onTake", at = @At("HEAD"))
    private void deeperdark$playCraftingSound(Player player, ItemStack stack, CallbackInfo ci) {
        Level world = ((EntityAccessor)player).deeperdark$getWorld();
        if (player instanceof ServerPlayer serverPlayer && world instanceof ServerLevel serverWorld) {
            // Check debounce - only play sound if enough time has passed since last craft
            UUID playerUUID = player.getUUID();
            long currentTime = System.currentTimeMillis();
            Long lastCraftTime = LAST_CRAFT_TIME.get(playerUUID);

            if (lastCraftTime == null || (currentTime - lastCraftTime) >= CRAFT_SOUND_COOLDOWN_MS) {
                LAST_CRAFT_TIME.put(playerUUID, currentTime);
                deeperdark$sendCraftingSound(serverWorld, serverPlayer);
            }
        }
    }

    /**
     * Send the crafting sound packet to all nearby players using Direct Holder.
     * This mirrors how the vanilla /playsound command works, bypassing registry sync.
     */
    @Unique
    private void deeperdark$sendCraftingSound(ServerLevel world, ServerPlayer crafter) {
        // Create a Direct Holder (not Reference) - same as /playsound command does
        // This bypasses Fabric's registry sync because Direct entries aren't synced
        Holder<SoundEvent> soundEntry = Holder.direct(SoundEvent.createVariableRangeEvent(CRAFTING_SOUND_ID));

        double x = crafter.getX();
        double y = crafter.getY();
        double z = crafter.getZ();
        float volume = 0.5F;
        float pitch = 0.9F + world.getRandom().nextFloat() * 0.2F; // Slight random pitch variation
        long seed = world.getRandom().nextLong();

        // Calculate max hearing distance (volume affects range)
        double maxDistSq = Math.pow(soundEntry.value().getRange(volume), 2);

        // Send packet to all players within hearing range
        for (ServerPlayer player : world.players()) {
            double distSq = player.distanceToSqr(x, y, z);
            if (distSq < maxDistSq) {
                player.connection.send(new ClientboundSoundPacket(
                    soundEntry,
                    SoundSource.BLOCKS,
                    x, y, z,
                    volume,
                    pitch,
                    seed
                ));
            }
        }
    }
}
