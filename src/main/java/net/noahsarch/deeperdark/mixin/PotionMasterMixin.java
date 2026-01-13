package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.RegistryKey;
import net.noahsarch.deeperdark.duck.PotionMasterDuck;
import net.noahsarch.deeperdark.villager.ModVillagers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class PotionMasterMixin implements PotionMasterDuck {

    @Shadow public abstract VillagerData getVillagerData();

    @Unique
    private boolean isPotionMaster;

    @Override
    public boolean deeperdark$isPotionMaster() {
        return this.isPotionMaster;
    }

    @Override
    public void deeperdark$setPotionMaster(boolean isPotionMaster) {
        this.isPotionMaster = isPotionMaster;
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    public void writeCustomData(WriteView view, CallbackInfo ci) {
        if (this.isPotionMaster) {
            view.putBoolean("DeeperDarkPotionMaster", true);
        }
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    public void readCustomData(ReadView view, CallbackInfo ci) {
        this.isPotionMaster = view.getBoolean("DeeperDarkPotionMaster", false);
    }

    @Inject(method = "fillRecipes", at = @At("HEAD"), cancellable = true)
    public void fillRecipes(ServerWorld world, CallbackInfo ci) {
        if (this.isPotionMaster) {
            VillagerEntity villager = (VillagerEntity) (Object) this;
            VillagerData villagerData = this.getVillagerData();
            // We use the villager's level to determine trades from our custom map
            if(ModVillagers.POTION_MASTER_TRADES.containsKey(villagerData.level())) {
                TradeOffers.Factory[] factories = ModVillagers.POTION_MASTER_TRADES.get(villagerData.level());
                if (factories != null) {
                    TradeOfferList tradeOfferList = villager.getOffers();
                    ((VillagerEntityAccessor)villager).deeperdark$fillRecipesFromPool(world, tradeOfferList, factories, 2);
                }
            }
            ci.cancel(); // Prevent vanilla logic which might look for profession-based trades
        }
    }

    // We redirect the matchesKey check in mobTick to prevent the villager from resetting its customer
    // when it has the NONE profession (which we forcing for visuals) but is a Potion Master.
    @Redirect(method = "mobTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/entry/RegistryEntry;matchesKey(Lnet/minecraft/registry/RegistryKey;)Z"))
    public boolean deeperdark$preventCustomerReset(RegistryEntry<VillagerProfession> instance, RegistryKey<VillagerProfession> key) {
        if (this.isPotionMaster && key.equals(VillagerProfession.NONE)) {
            // Logic: if we are a Potion Master and the code checks if we match NONE, return false!
            // This tricks the logic: if (profession.matchesKey(NONE)) -> false
            // So resetCustomer() is NOT called.
            return false;
        }
        return instance.matchesKey(key);
    }
}
