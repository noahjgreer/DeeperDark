package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.noahsarch.deeperdark.util.MobEntityExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(MobEntity.class)
public class MobEntityMixin implements MobEntityExtension {

    @Unique
    private RegistryEntry<StatusEffect> deeperdark$storedEffect;

    @Override
    public void deeperdark$setStoredEffect(RegistryEntry<StatusEffect> effect) {
        this.deeperdark$storedEffect = effect;
    }

    @Override
    public RegistryEntry<StatusEffect> deeperdark$getStoredEffect() {
        return this.deeperdark$storedEffect;
    }

    @Inject(method = "initialize", at = @At("TAIL"))
    private void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, CallbackInfoReturnable<EntityData> cir) {
        if (world.toServerWorld().isClient) return;

        float chance = 0.0f;
        Difficulty diff = world.getDifficulty();
        if (diff == Difficulty.EASY) {
            chance = 0.1f;
        } else if (diff == Difficulty.NORMAL) {
            chance = 0.2f;
        } else if (diff == Difficulty.HARD) {
            chance = 0.8f;
        }

        if (world.getRandom().nextFloat() < chance) {
            List<RegistryEntry<StatusEffect>> effects = List.of(
                    StatusEffects.POISON,
                    StatusEffects.SLOWNESS,
                    StatusEffects.WEAKNESS,
                    StatusEffects.HUNGER,
                    StatusEffects.WITHER,
                    StatusEffects.BLINDNESS,
                    StatusEffects.MINING_FATIGUE,
                    StatusEffects.INVISIBILITY,
                    StatusEffects.LEVITATION,
                    StatusEffects.DARKNESS
            );
            this.deeperdark$storedEffect = effects.get(world.getRandom().nextInt(effects.size()));

            MobEntity self = (MobEntity) (Object) this;
            StatusEffect effect = this.deeperdark$storedEffect.value();
            if (effect != StatusEffects.LEVITATION.value() && effect != StatusEffects.WEAKNESS.value()) {
                self.addStatusEffect(new StatusEffectInstance(this.deeperdark$storedEffect, -1, 0));
            }
        }
    }

    @Inject(method = "tryAttack", at = @At("RETURN"))
    private void onTryAttack(ServerWorld world, Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && this.deeperdark$storedEffect != null && target instanceof LivingEntity livingTarget) {
            livingTarget.addStatusEffect(new StatusEffectInstance(this.deeperdark$storedEffect, 140, 0));
        }
    }


    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void onWriteCustomData(WriteView nbt, CallbackInfo ci) {
        if (this.deeperdark$storedEffect != null) {
            this.deeperdark$storedEffect.getKey().ifPresent(key -> nbt.putString("DeeperDarkStoredEffect", key.getValue().toString()));
        }
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void onReadCustomData(ReadView nbt, CallbackInfo ci) {
        nbt.getOptionalString("DeeperDarkStoredEffect").ifPresent(idString -> {
            Identifier id = Identifier.tryParse(idString);
            if (id != null) {
                StatusEffect effect = Registries.STATUS_EFFECT.get(id);
                if (effect != null) {
                    this.deeperdark$storedEffect = Registries.STATUS_EFFECT.getEntry(effect);

                    MobEntity self = (MobEntity) (Object) this;
                    if (effect != StatusEffects.LEVITATION.value() && effect != StatusEffects.WEAKNESS.value()) {
                        if (!self.hasStatusEffect(this.deeperdark$storedEffect)) {
                             self.addStatusEffect(new StatusEffectInstance(this.deeperdark$storedEffect, -1, 0));
                        }
                    }
                }
            }
        });
    }
}
