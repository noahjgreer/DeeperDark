package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.mob.RaiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.UUID;
import net.noahsarch.deeperdark.villager.ModVillagers;
import net.minecraft.entity.conversion.EntityConversionContext;

@Mixin(WitchEntity.class)
public abstract class WitchEntityMixin extends RaiderEntity {

    @Unique
    private static final TrackedData<Boolean> CONVERTING = DataTracker.registerData(WitchEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private int conversionTimer;
    @Unique
    private UUID converter;

    protected WitchEntityMixin(EntityType<? extends RaiderEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(CONVERTING, false);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("ConversionTime", this.isConverting() ? this.conversionTimer : -1);
        if (this.converter != null) {
            nbt.putUuid("ConversionPlayer", this.converter);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("ConversionTime", 99) && nbt.getInt("ConversionTime") > -1) {
            this.setConverting(nbt.containsUuid("ConversionPlayer") ? nbt.getUuid("ConversionPlayer") : null, nbt.getInt("ConversionTime"));
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if (!this.getWorld().isClient && this.isAlive() && this.isConverting()) {
            this.conversionTimer--;
            if (this.conversionTimer <= 0) {
                this.finishConversion((ServerWorld) this.getWorld());
            }
        }
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    public void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.GOLDEN_APPLE)) {
            if (this.hasStatusEffect(StatusEffects.WEAKNESS)) {
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
                if (!this.getWorld().isClient) {
                    this.setConverting(player.getUuid(), this.random.nextInt(2401) + 3600);
                }
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }

    @Inject(method = "handleStatus", at = @At("HEAD"))
    public void handleStatus(byte status, CallbackInfo ci) {
        if (status == 16) {
            if (!this.isSilent()) {
                this.getWorld().playSoundClient(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, this.getSoundCategory(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }
        }
    }

    @Unique
    public boolean isConverting() {
        return this.dataTracker.get(CONVERTING);
    }

    @Unique
    private void setConverting(@Nullable UUID uuid, int delay) {
        this.converter = uuid;
        this.conversionTimer = delay;
        this.dataTracker.set(CONVERTING, true);
        this.removeStatusEffect(StatusEffects.WEAKNESS);
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, delay, Math.min(this.getWorld().getDifficulty().getId() - 1, 0)));
        this.getWorld().sendEntityStatus(this, (byte) 16);
    }

    @Unique
    private void finishConversion(ServerWorld world) {
        this.convertTo(EntityType.VILLAGER, EntityConversionContext.create(this, false, false), (villager) -> {
            villager.initialize(world, world.getLocalDifficulty(villager.getBlockPos()), SpawnReason.CONVERSION, null);
            villager.setVillagerData(villager.getVillagerData().withProfession(ModVillagers.POTION_MASTER).withType(VillagerType.SWAMP));
             if (this.converter != null) {
                PlayerEntity playerEntity = world.getPlayerByUuid(this.converter);
                if (playerEntity instanceof ServerPlayerEntity) {
                    // Custom criteria could be triggered here
                }
            }
        });
    }
}

