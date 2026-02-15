package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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
@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin {

    @Shadow
    public abstract PlayerEntity getPlayerOwner();

    /**
     * Inject into the use method to potentially spawn a charged creeper instead of giving loot
     */
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void deeperdark$fishUpChargedCreeper(ItemStack usedItem, CallbackInfoReturnable<Integer> cir) {
        FishingBobberEntity self = (FishingBobberEntity) (Object) this;
        PlayerEntity player = this.getPlayerOwner();

        if (player == null) {
            return;
        }

        World world = ((EntityAccessor)self).deeperdark$getWorld();
        if (world.isClient()) {
            return;
        }

        if (world instanceof ServerWorld serverWorld) {
            int chance = DeeperDarkConfig.get().fishingChargedCreeperChance;
            if (chance > 0 && serverWorld.getRandom().nextInt(chance) == 0) {
                // Spawn a charged creeper at the bobber position
                CreeperEntity creeper = EntityType.CREEPER.create(serverWorld, SpawnReason.TRIGGERED);
                if (creeper != null) {
                    creeper.refreshPositionAndAngles(self.getX(), self.getY(), self.getZ(),
                        serverWorld.getRandom().nextFloat() * 360.0F, 0.0F);

                    // Spawn the creeper first
                    serverWorld.spawnEntity(creeper);

                    // Strike it with silent lightning to make it charged
                    LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(serverWorld, SpawnReason.TRIGGERED);
                    if (lightning != null) {
                        lightning.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(creeper.getBlockPos()));
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
