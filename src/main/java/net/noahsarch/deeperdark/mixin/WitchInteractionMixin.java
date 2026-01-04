package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(MobEntity.class)
public abstract class WitchInteractionMixin {


    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void deeperdark$interactWitch(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        MobEntity self = (MobEntity) (Object) this;
        if (self instanceof WitchEntity witch) {
            ItemStack itemStack = player.getStackInHand(hand);
            if (itemStack.isOf(Items.GOLDEN_APPLE)) {
                if (witch.hasStatusEffect(StatusEffects.WEAKNESS)) {
                    if (!player.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }
                    if (!witch.getWorld().isClient) {
                        ((net.noahsarch.deeperdark.util.WitchCureAccessor) witch).deeperdark$setConversionTimer(witch.getRandom().nextInt(2401) + 3600);
                        ((net.noahsarch.deeperdark.util.WitchCureAccessor) witch).deeperdark$setConverterUuid(player.getUuid());
                        witch.playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F, 1.0F);
                    }
                    cir.setReturnValue(ActionResult.SUCCESS);
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void deeperdark$tickWitch(CallbackInfo ci) {
        MobEntity self = (MobEntity) (Object) this;
        if (self instanceof WitchEntity witch && !witch.getWorld().isClient) {
            int timer = ((net.noahsarch.deeperdark.util.WitchCureAccessor) witch).deeperdark$getConversionTimer();
            if (timer > 0) {
                ((net.noahsarch.deeperdark.util.WitchCureAccessor) witch).deeperdark$setConversionTimer(timer - 1);
                if (timer - 1 == 0) {
                    deeperdark$finishWitchConversion(witch, (ServerWorld) witch.getWorld());
                }
            }
        }
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void deeperdark$writeCustomData(WriteView view, CallbackInfo ci) {
        MobEntity self = (MobEntity) (Object) this;
        if (self instanceof WitchEntity witch) {
            view.putInt("ConversionTime", ((net.noahsarch.deeperdark.util.WitchCureAccessor) witch).deeperdark$getConversionTimer());
            UUID converterUuid = ((net.noahsarch.deeperdark.util.WitchCureAccessor) witch).deeperdark$getConverterUuid();
            if (converterUuid != null) {
                view.putString("Converter", converterUuid.toString());
            }
        }
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void deeperdark$readCustomData(ReadView view, CallbackInfo ci) {
        MobEntity self = (MobEntity) (Object) this;
        if (self instanceof WitchEntity witch) {
            ((net.noahsarch.deeperdark.util.WitchCureAccessor) witch).deeperdark$setConversionTimer(view.getInt("ConversionTime", 0));

            String uuidString = view.getString("Converter", "");
            if (!uuidString.isEmpty()) {
                try {
                    ((net.noahsarch.deeperdark.util.WitchCureAccessor) witch).deeperdark$setConverterUuid(UUID.fromString(uuidString));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    @Unique
    private void deeperdark$finishWitchConversion(WitchEntity witch, ServerWorld world) {
        VillagerEntity villagerEntity = new VillagerEntity(EntityType.VILLAGER, world);
        villagerEntity.copyPositionAndRotation(witch);

        villagerEntity.setVillagerData(villagerEntity.getVillagerData().withProfession(
            net.minecraft.registry.Registries.VILLAGER_PROFESSION.getOrThrow(VillagerProfession.NONE)
        ).withType(
            net.minecraft.registry.Registries.VILLAGER_TYPE.getOrThrow(VillagerType.SWAMP)
        ));
        villagerEntity.initialize(world, world.getLocalDifficulty(villagerEntity.getBlockPos()), SpawnReason.CONVERSION, null);


        world.spawnEntity(villagerEntity);
        witch.discard();

        world.playSound(null, villagerEntity.getBlockPos(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 2.0F, 1.0F);
    }
}
