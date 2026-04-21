package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.duck.EntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to add a rare chance to fish up a charged creeper.
 */
@Mixin(FishingHook.class)
public abstract class FishingBobberEntityMixin {

    @Shadow
    public abstract Player getPlayerOwner();

    /**
     * Inject into the use method to potentially spawn a charged creeper instead of giving loot
     */
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void deeperdark$fishUpChargedCreeper(ItemStack usedItem, CallbackInfoReturnable<Integer> cir) {
        FishingHook self = (FishingHook) (Object) this;
        Player player = this.getPlayerOwner();

        if (player == null) {
            return;
        }

        Level world = ((EntityAccessor)self).deeperdark$getWorld();
        if (world.isClient()) {
            return;
        }

        if (world instanceof ServerLevel serverWorld) {
            int chance = DeeperDarkConfig.get().fishingChargedCreeperChance;
            if (chance > 0 && serverWorld.getRandom().nextInt(chance) == 0) {
                // Spawn a charged creeper at the bobber position
                Creeper creeper = EntityType.CREEPER.create(serverWorld, EntitySpawnReason.TRIGGERED);
                if (creeper != null) {
                    creeper.refreshPositionAndAngles(self.getX(), self.getY(), self.getZ(),
                        serverWorld.getRandom().nextFloat() * 360.0F, 0.0F);

                    // Spawn the creeper first
                    serverWorld.spawnEntity(creeper);

                    // Strike it with silent lightning to make it charged
                    LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverWorld, EntitySpawnReason.TRIGGERED);
                    if (lightning != null) {
                        lightning.refreshPositionAfterTeleport(Vec3.ofBottomCenter(creeper.getBlockPos()));
                        lightning.setCosmetic(true); // No fire, no damage to other entities
                        serverWorld.spawnEntity(lightning);
                    }

                    // Give some velocity towards the player (like caught fish)
                    double dx = player.getX() - self.getX();
                    double dy = player.getY() - self.getY();
                    double dz = player.getZ() - self.getZ();
                    creeper.setVelocity(dx * 0.1, dy * 0.1 + Math.sqrt(Math.sqrt(dx * dx + dy * dy + dz * dz)) * 0.08, dz * 0.1);

                    // Discard the bobber
                    self.discard();

                    // Return 5 (like hooking an entity)
                    cir.setReturnValue(5);
                }
            }
        }
    }
}
