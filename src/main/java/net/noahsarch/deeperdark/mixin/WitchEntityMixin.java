package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.npc.villager.VillagerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.UUID;
import net.minecraft.world.entity.ConversionParams;
import net.noahsarch.deeperdark.duck.WitchConversionAccessor;
import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.noahsarch.deeperdark.duck.PotionMasterDuck;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.noahsarch.deeperdark.duck.EntityAccessor;
import net.minecraft.core.registries.Registries;

@Mixin(Witch.class)
public abstract class WitchEntityMixin extends Raider implements WitchConversionAccessor {

    // ...existing code...
    protected WitchEntityMixin(EntityType<? extends Raider> entityType, Level world) {
        super(entityType, world);
    }

    @Unique
    private boolean converting;
    @Unique
    private int conversionTimer;
    @Unique
    private UUID converter;

    @Override
    public void writeCustomData(ValueOutput view) {
        super.writeCustomData(view);
        if (this.converting) {
            view.putInt("ConversionTime", this.conversionTimer);
            if (this.converter != null) {
                view.putString("ConversionPlayer", this.converter.toString());
            }
        }
    }

    @Override
    public void readCustomData(ValueInput view) {
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
        Level world = ((EntityAccessor)this).deeperdark$getWorld();
        if (!world.isClient() && this.isAlive() && this.deeperdark$isConverting()) {
            this.conversionTimer--;
            if (this.conversionTimer <= 0) {
                this.deeperdark$finishConversion((ServerLevel) world);
            }
        }
    }

    @Unique
    public boolean deeperdark$isConverting() {
        return this.converting;
    }

    @Unique
    public void deeperdark$setConverting(@Nullable UUID uuid, int delay) {
        Level world = ((EntityAccessor)this).deeperdark$getWorld();
        this.converter = uuid;
        this.conversionTimer = delay;
        this.converting = true;
        this.removeStatusEffect(MobEffects.WEAKNESS);
        this.addStatusEffect(new MobEffectInstance(MobEffects.STRENGTH, delay, Math.min(world.getDifficulty().getId() - 1, 0)));
        world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, this.getSoundCategory(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F);
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
    public InteractionResult interactMob(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.GOLDEN_APPLE)) {
            if (this.hasStatusEffect(MobEffects.WEAKNESS)) {
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
                if (!((EntityAccessor)this).deeperdark$getWorld().isClient()) {
                    this.deeperdark$setConverting(player.getUuid(), this.random.nextInt(2401) + 3600);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.interactMob(player, hand);
    }

    @Unique
    private void deeperdark$finishConversion(ServerLevel world) {
        this.convertTo(EntityType.VILLAGER, ConversionParams.create(this, false, false), (villager) -> {
            villager.initialize(world, world.getLocalDifficulty(villager.getBlockPos()), EntitySpawnReason.CONVERSION, null);
            // Use NONE profession but mark as Potion Master
            villager.setVillagerData(villager.getVillagerData().withProfession(Registries.VILLAGER_PROFESSION.getOrThrow(VillagerProfession.NONE)).withType(Registries.VILLAGER_TYPE.getOrThrow(VillagerType.SWAMP)));

            if (villager instanceof PotionMasterDuck potionMaster) {
                potionMaster.deeperdark$setPotionMaster(true);
            }

            villager.setExperience(1);
             if (this.converter != null) {
                // Player playerEntity = world.getPlayerByUuid(this.converter);
                // Custom criteria could be triggered here
            }
        });
    }
}
