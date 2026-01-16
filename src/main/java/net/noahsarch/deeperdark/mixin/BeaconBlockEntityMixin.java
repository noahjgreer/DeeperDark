package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BeamEmitter;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.duck.BeaconDuck;
import net.noahsarch.deeperdark.state.ActiveBeaconState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin extends BlockEntity implements BeaconDuck {

    @Shadow @Nullable RegistryEntry<StatusEffect> primary;
    @Shadow @Nullable RegistryEntry<StatusEffect> secondary;
    @Shadow int level;
    @Shadow List<BeamEmitter.BeamSegment> beamSegments;

    @Unique
    private int deeperDark$applicationWindow = 0;
    @Unique
    private final Set<UUID> deeperDark$trackedPlayers = new HashSet<>();

    public BeaconBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "applyPlayerEffects", at = @At("HEAD"), cancellable = true)
    private static void deeperDark$cancelVanillaEffects(World world, BlockPos pos, int beaconLevel, @Nullable RegistryEntry<StatusEffect> primaryEffect, @Nullable RegistryEntry<StatusEffect> secondaryEffect, CallbackInfo ci) {
        // We handle effects via ActiveBeaconState
        ci.cancel();
    }

    @Inject(method = "markRemoved", at = @At("HEAD"))
    private void deeperDark$onRemoved(CallbackInfo ci) {
        if (this.world instanceof ServerWorld serverWorld) {
            ActiveBeaconState.get(serverWorld).removeBeacon(this.pos, serverWorld);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private static void deeperDark$tick(World world, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci) {
        if (world.isClient()) return;
        if (!(world instanceof ServerWorld serverWorld)) return;

        BeaconBlockEntityMixin beacon = (BeaconBlockEntityMixin) (Object) blockEntity;

        // Handle application window (visuals & tracking new players)
        if (beacon.deeperDark$applicationWindow > 0) {
           beacon.deeperDark$applicationWindow--;

           DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
           double radius = switch (beacon.level) {
               case 1 -> config.beaconLevel1Radius;
               case 2 -> config.beaconLevel2Radius;
               case 3 -> config.beaconLevel3Radius;
               case 4 -> config.beaconLevel4Radius;
               default -> config.beaconLevel1Radius; // Fallback (shouldn't happen if active)
           };

           if (beacon.deeperDark$applicationWindow % 5 == 0) { // Check every few ticks

               // Spawn particles for visual border (using SpawnParticles for server-side)
               int r = (int) radius;
               double y = pos.getY() + 1.5;

               for (float i = -r; i <= r; i += 0.5f) {
                   serverWorld.spawnParticles(ParticleTypes.FLAME, pos.getX() + i + 0.5, y, pos.getZ() - r + 0.5, 1, 0, 0, 0, 0);
                   serverWorld.spawnParticles(ParticleTypes.FLAME, pos.getX() + i + 0.5, y, pos.getZ() + r + 0.5, 1, 0, 0, 0, 0);
                   serverWorld.spawnParticles(ParticleTypes.FLAME, pos.getX() - r + 0.5, y, pos.getZ() + i + 0.5, 1, 0, 0, 0, 0);
                   serverWorld.spawnParticles(ParticleTypes.FLAME, pos.getX() + r + 0.5, y, pos.getZ() + i + 0.5, 1, 0, 0, 0, 0);
               }

               // Scan players
               Box box = new Box(pos).expand(radius);
               List<ServerPlayerEntity> players = world.getNonSpectatingEntities(ServerPlayerEntity.class, box);
               for (ServerPlayerEntity player : players) {
                   beacon.deeperDark$trackedPlayers.add(player.getUuid());
               }
           }

           // If window is closing or just periodically, update the global state with new players
           if (beacon.deeperDark$applicationWindow % 20 == 0 || beacon.deeperDark$applicationWindow == 1) {
                beacon.deeperDark$syncToState(serverWorld);
           }
        }

        // Beam Color Logic
        if (world.getTime() % 40 == 0 && beacon.primary != null && !beacon.beamSegments.isEmpty()) {
             ActiveBeaconState.BeaconInfo info = ActiveBeaconState.get(serverWorld).getBeacon(pos);
             if (info != null && info.remainingTime > 0) {
                int baseColor = beacon.primary.value().getColor();

                // Calculate brightness multiplier
                float brightness = 1.0f;
                long fadeThreshold = 6000L;
                if (info.remainingTime < fadeThreshold) {
                    brightness = (float) info.remainingTime / (float) fadeThreshold;
                    if (brightness < 0.2f) brightness = 0.2f;
                }

                int r = (baseColor >> 16) & 0xFF;
                int g = (baseColor >> 8) & 0xFF;
                int b = baseColor & 0xFF;

                r = (int) (r * brightness);
                g = (int) (g * brightness);
                b = (int) (b * brightness);

                int newColor = (r << 16) | (g << 8) | b;

                List<BeamEmitter.BeamSegment> newSegments = new ArrayList<>();
                for (BeamEmitter.BeamSegment segment : beacon.beamSegments) {
                    BeamEmitter.BeamSegment newSegment = new BeamEmitter.BeamSegment(newColor);
                    for(int i=1; i<segment.getHeight(); i++) {
                        newSegment.increaseHeight();
                    }
                    newSegments.add(newSegment);
                }

                beacon.beamSegments = newSegments;

                world.updateListeners(pos, state, state, 3);
             }
        }

        // Periodically sync level/effects if they change (e.g. pyramid changes)
        if (world.getTime() % 100 == 0) {
            ActiveBeaconState stateObj = ActiveBeaconState.get(serverWorld);
            ActiveBeaconState.BeaconInfo info = stateObj.getBeacon(pos);
            if (info != null) {
                stateObj.updateBeacon(pos, info.remainingTime, info.trackedPlayers, beacon.primary, beacon.secondary, beacon.level, serverWorld);
            }
        }
    }

    @Override
    public void deeperDark$addTime(int seconds) {
        if (this.world instanceof ServerWorld serverWorld) {
            ActiveBeaconState state = ActiveBeaconState.get(serverWorld);
            ActiveBeaconState.BeaconInfo info = state.getBeacon(this.pos);
            long current = info != null ? info.remainingTime : 0;
            long newTime = current + (seconds * 20L);

            this.deeperDark$applicationWindow = 100; // 5 seconds
            this.deeperDark$trackedPlayers.clear(); // Clear local cache to start fresh?
            if (info != null) {
                this.deeperDark$trackedPlayers.addAll(info.trackedPlayers);
            }

            state.updateBeacon(this.pos, newTime, this.deeperDark$trackedPlayers, this.primary, this.secondary, this.level, serverWorld);

            this.world.playSound(null, this.pos, SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    @Override
    public void deeperDark$resetBeacon() {
        if (this.world instanceof ServerWorld serverWorld) {
             ActiveBeaconState.get(serverWorld).removeBeacon(this.pos, serverWorld);
             this.deeperDark$trackedPlayers.clear();
             this.deeperDark$applicationWindow = 0;
        }
    }

    @Override
    public void deeperDark$checkPayment(ItemStack payment) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        int seconds = 0;

        if (payment.isOf(Items.IRON_INGOT)) seconds = config.beaconIronTime;
        else if (payment.isOf(Items.GOLD_INGOT)) seconds = config.beaconGoldTime;
        else if (payment.isOf(Items.EMERALD)) seconds = config.beaconEmeraldTime;
        else if (payment.isOf(Items.DIAMOND)) seconds = config.beaconDiamondTime;
        else if (payment.isOf(Items.NETHERITE_INGOT)) seconds = config.beaconNetheriteTime;

        if (seconds > 0) {
            deeperDark$addTime(seconds);
        }
    }

    @Unique
    private void deeperDark$syncToState(ServerWorld world) {
        ActiveBeaconState state = ActiveBeaconState.get(world);
        ActiveBeaconState.BeaconInfo info = state.getBeacon(this.pos);
        long time = info != null ? info.remainingTime : 0;

        // Merge tracked players
        Set<UUID> merged = new HashSet<>(this.deeperDark$trackedPlayers);
        if (info != null) merged.addAll(info.trackedPlayers);

        state.updateBeacon(this.pos, time, merged, this.primary, this.secondary, this.level, world);
    }
}
