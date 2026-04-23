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
import net.minecraft.world.entity.npc.villager.VillagerProfession;
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
import net.minecraft.core.registries.Registries;

import net.noahsarch.deeperdark.duck.PotionMasterDuck;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.noahsarch.deeperdark.duck.EntityAccessor;

@Mixin(Witch.class)
public abstract class WitchEntityMixin extends Raider implements WitchConversionAccessor {

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
    public void addAdditionalSaveData(ValueOutput view) {
        super.addAdditionalSaveData(view);
        if (this.converting) {
            view.putInt("ConversionTime", this.conversionTimer);
            if (this.converter != null) {
                view.putString("ConversionPlayer", this.converter.toString());
            }
        }
    }

    @Override
    public void readAdditionalSaveData(ValueInput view) {
        super.readAdditionalSaveData(view);
        int time = view.getIntOr("ConversionTime", -1);
        if (time > -1) {
            this.converting = true;
            this.conversionTimer = time;
            String uuidString = view.getStringOr("ConversionPlayer", "");
            if (!uuidString.isEmpty()) {
                try {
                    this.converter = UUID.fromString(uuidString);
                } catch (IllegalArgumentException e) {
                    // Ignore invalid UUID
                }
            }
        }
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    public void aiStep(CallbackInfo ci) {
        Level world = ((EntityAccessor)this).deeperdark$getWorld();
        if (!world.isClientSide() && this.isAlive() && this.deeperdark$isConverting()) {
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
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffectInstance(MobEffects.STRENGTH, delay, Math.min(world.getDifficulty().getId() - 1, 0)));
        world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F);
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
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() == Items.GOLDEN_APPLE) {
            if (this.hasEffect(MobEffects.WEAKNESS)) {
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
                if (!((EntityAccessor)this).deeperdark$getWorld().isClientSide()) {
                    this.deeperdark$setConverting(player.getUUID(), this.random.nextInt(2401) + 3600);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Unique
    private void deeperdark$finishConversion(ServerLevel world) {
        this.convertTo(EntityType.VILLAGER, ConversionParams.single((net.minecraft.world.entity.Mob)(Object)this, false, false), (villager) -> {
            villager.finalizeSpawn(world, world.getCurrentDifficultyAt(villager.blockPosition()), EntitySpawnReason.CONVERSION, null);
            villager.setVillagerData(villager.getVillagerData()
                .withProfession(world.registryAccess().lookupOrThrow(Registries.VILLAGER_PROFESSION).getOrThrow(VillagerProfession.NONE))
                .withType(world.registryAccess().lookupOrThrow(Registries.VILLAGER_TYPE).getOrThrow(VillagerType.SWAMP)));

            if (villager instanceof PotionMasterDuck potionMaster) {
                potionMaster.deeperdark$setPotionMaster(true);
            }

            villager.setVillagerXp(1);
            if (this.converter != null) {
                // Custom criteria could be triggered here
            }
        });
    }
}
