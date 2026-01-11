package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.entity.SpawnReason;
import net.minecraft.village.VillagerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.UUID;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.noahsarch.deeperdark.duck.WitchConversionAccessor;
import net.minecraft.registry.Registries;

import net.minecraft.village.VillagerProfession;
import net.noahsarch.deeperdark.duck.PotionMasterDuck;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Mixin(WitchEntity.class)
public abstract class WitchEntityMixin extends RaiderEntity implements WitchConversionAccessor {

    protected WitchEntityMixin(EntityType<? extends RaiderEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private boolean converting;
    @Unique
    private int conversionTimer;
    @Unique
    private UUID converter;

    @Override
    public void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        if (this.converting) {
            view.putInt("ConversionTime", this.conversionTimer);
            if (this.converter != null) {
                view.putString("ConversionPlayer", this.converter.toString());
            }
        }
    }

    @Override
    public void readCustomData(ReadView view) {
        super.readCustomData(view);
        int time = view.getInt("ConversionTime", -1);
        if (time > -1) {
            this.converting = true;
            this.conversionTimer = time;
            String uuidString = view.getString("ConversionPlayer", "");
            if (!uuidString.isEmpty()) {
                try {
                    this.converter = UUID.fromString(uuidString);
                } catch (IllegalArgumentException e) {
                    // Ignore invalid UUID
                }
            }
        }
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

    @Unique
    public boolean deeperdark$isConverting() {
        return this.converting;
    }

    @Unique
    public void deeperdark$setConverting(@Nullable UUID uuid, int delay) {
        this.converter = uuid;
        this.conversionTimer = delay;
        this.converting = true;
        this.removeStatusEffect(StatusEffects.WEAKNESS);
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, delay, Math.min(this.getWorld().getDifficulty().getId() - 1, 0)));
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, this.getSoundCategory(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F);
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

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.GOLDEN_APPLE)) {
            if (this.hasStatusEffect(StatusEffects.WEAKNESS)) {
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
                if (!this.getWorld().isClient) {
                    this.deeperdark$setConverting(player.getUuid(), this.random.nextInt(2401) + 3600);
                }
                return ActionResult.SUCCESS;
            }
        }
        return super.interactMob(player, hand);
    }

    @Unique
    private void deeperdark$finishConversion(ServerWorld world) {
        this.convertTo(EntityType.VILLAGER, EntityConversionContext.create(this, false, false), (villager) -> {
            villager.initialize(world, world.getLocalDifficulty(villager.getBlockPos()), SpawnReason.CONVERSION, null);
            // Use NONE profession but mark as Potion Master
            villager.setVillagerData(villager.getVillagerData().withProfession(Registries.VILLAGER_PROFESSION.getOrThrow(VillagerProfession.NONE)).withType(Registries.VILLAGER_TYPE.getOrThrow(VillagerType.SWAMP)));

            if (villager instanceof PotionMasterDuck potionMaster) {
                potionMaster.deeperdark$setPotionMaster(true);
            }

            villager.setExperience(1);
             if (this.converter != null) {
                // PlayerEntity playerEntity = world.getPlayerByUuid(this.converter);
                // Custom criteria could be triggered here
            }
        });
    }
}
