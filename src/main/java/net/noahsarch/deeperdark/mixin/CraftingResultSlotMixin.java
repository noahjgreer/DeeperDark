package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
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
 * Uses the same Direct RegistryEntry approach as the organ noteblock sound.
 */
@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin {

    @Shadow @Final private PlayerEntity player;

    @Unique
    private static final Identifier CRAFTING_SOUND_ID = Identifier.of(Deeperdark.MOD_ID, "block.crafting_table.craft");

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
    @Inject(method = "onTakeItem", at = @At("HEAD"))
    private void deeperdark$playCraftingSound(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        World world = ((EntityAccessor)player).deeperdark$getWorld();
        if (player instanceof ServerPlayerEntity serverPlayer && world instanceof ServerWorld serverWorld) {
            // Check debounce - only play sound if enough time has passed since last craft
            UUID playerUUID = player.getUuid();
            long currentTime = System.currentTimeMillis();
            Long lastCraftTime = LAST_CRAFT_TIME.get(playerUUID);

            if (lastCraftTime == null || (currentTime - lastCraftTime) >= CRAFT_SOUND_COOLDOWN_MS) {
                LAST_CRAFT_TIME.put(playerUUID, currentTime);
                deeperdark$sendCraftingSound(serverWorld, serverPlayer);
            }
        }
    }

    /**
     * Send the crafting sound packet to all nearby players using Direct RegistryEntry.
     * This mirrors how the vanilla /playsound command works, bypassing registry sync.
     */
    @Unique
    private void deeperdark$sendCraftingSound(ServerWorld world, ServerPlayerEntity crafter) {
        // Create a Direct RegistryEntry (not Reference) - same as /playsound command does
        // This bypasses Fabric's registry sync because Direct entries aren't synced
        RegistryEntry<SoundEvent> soundEntry = RegistryEntry.of(SoundEvent.of(CRAFTING_SOUND_ID));

        double x = crafter.getX();
        double y = crafter.getY();
        double z = crafter.getZ();
        float volume = 0.8F;
        float pitch = 0.9F + world.getRandom().nextFloat() * 0.2F; // Slight random pitch variation
        long seed = world.getRandom().nextLong();

        // Calculate max hearing distance (volume affects range)
        double maxDistSq = Math.pow(soundEntry.value().getDistanceToTravel(volume), 2);

        // Send packet to all players within hearing range
        for (ServerPlayerEntity player : world.getPlayers()) {
            double distSq = player.squaredDistanceTo(x, y, z);
            if (distSq < maxDistSq) {
                player.networkHandler.sendPacket(new PlaySoundS2CPacket(
                    soundEntry,
                    SoundCategory.BLOCKS,
                    x, y, z,
                    volume,
                    pitch,
                    seed
                ));
            }
        }
    }
}
