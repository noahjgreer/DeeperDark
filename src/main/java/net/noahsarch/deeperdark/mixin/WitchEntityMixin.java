package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.entity.SpawnReason;
import net.minecraft.village.VillagerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.UUID;
import net.noahsarch.deeperdark.villager.ModVillagers;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.noahsarch.deeperdark.duck.WitchConversionAccessor;
import net.minecraft.registry.Registries;

@Mixin(WitchEntity.class)
public abstract class WitchEntityMixin extends RaiderEntity implements WitchConversionAccessor {

    protected WitchEntityMixin(EntityType<? extends RaiderEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private static final TrackedData<Boolean> CONVERTING = DataTracker.registerData(WitchEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private int conversionTimer;
    @Unique
    private UUID converter;

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(CONVERTING, false);
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    public void tickMovement(CallbackInfo ci) {
        if (!this.getWorld().isClient && this.isAlive() && this.deeperdark$isConverting()) {
            this.conversionTimer--;
            if (this.conversionTimer <= 0) {
                this.deeperdark$finishConversion((ServerWorld) this.getWorld());
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
    public boolean deeperdark$isConverting() {
        return this.dataTracker.get(CONVERTING);
    }

    @Unique
    public void deeperdark$setConverting(@Nullable UUID uuid, int delay) {
        this.converter = uuid;
        this.conversionTimer = delay;
        this.dataTracker.set(CONVERTING, true);
        this.removeStatusEffect(StatusEffects.WEAKNESS);
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, delay, Math.min(this.getWorld().getDifficulty().getId() - 1, 0)));
        this.getWorld().sendEntityStatus(this, (byte) 16);
    }

    @Unique
    public int deeperdark$getConversionTimer() {
        return this.conversionTimer;
    }

    @Unique
    public void deeperdark$setConversionTimer(int time) {
        this.conversionTimer = time;
    }

    @Unique
    @Nullable
    public UUID deeperdark$getConverter() {
        return this.converter;
    }

    @Unique
    public void deeperdark$setConverter(@Nullable UUID uuid) {
        this.converter = uuid;
    }

    @Unique
    private void deeperdark$finishConversion(ServerWorld world) {
        this.convertTo(EntityType.VILLAGER, EntityConversionContext.create(this, false, false), (villager) -> {
            villager.initialize(world, world.getLocalDifficulty(villager.getBlockPos()), SpawnReason.CONVERSION, null);
            villager.setVillagerData(villager.getVillagerData().withProfession(Registries.VILLAGER_PROFESSION.getEntry(ModVillagers.POTION_MASTER)).withType(VillagerType.SWAMP));
             if (this.converter != null) {
                PlayerEntity playerEntity = world.getPlayerByUuid(this.converter);
                if (playerEntity instanceof ServerPlayerEntity) {
                    // Custom criteria could be triggered here
                }
            }
        });
    }
}

