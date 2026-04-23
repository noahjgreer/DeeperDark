package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.noahsarch.deeperdark.duck.PotionMasterDuck;
import net.noahsarch.deeperdark.villager.ModVillagers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
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

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void writeCustomData(ValueOutput view, CallbackInfo ci) {
        if (this.isPotionMaster) {
            view.putBoolean("DeeperDarkPotionMaster", true);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readCustomData(ValueInput view, CallbackInfo ci) {
        this.isPotionMaster = view.getBooleanOr("DeeperDarkPotionMaster", false);
    }

    @Inject(method = "updateTrades", at = @At("HEAD"), cancellable = true)
    public void fillRecipes(ServerLevel world, CallbackInfo ci) {
        if (this.isPotionMaster) {
            Villager villager = (Villager) (Object) this;
            VillagerData villagerData = this.getVillagerData();
            // We use the villager's level to determine trades from our custom map
            if(ModVillagers.POTION_MASTER_TRADES.containsKey(villagerData.level())) {
                ModVillagers.TradeFactory[] factories = ModVillagers.POTION_MASTER_TRADES.get(villagerData.level());
                if (factories != null) {
                    MerchantOffers offers = villager.getOffers();
                    int added = 0;
                    for (int i = 0; i < factories.length && added < 2; i++) {
                        net.minecraft.world.item.trading.MerchantOffer offer = factories[i].create(world, villager, villager.getRandom());
                        if (offer != null) { offers.add(offer); added++; }
                    }
                }
            }
            ci.cancel(); // Prevent vanilla logic which might look for profession-based trades
        }
    }

    // We redirect the matchesKey check in mobTick to prevent the villager from resetting its customer
    // when it has the NONE profession (which we forcing for visuals) but is a Potion Master.
    @Redirect(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;is(Lnet/minecraft/resources/ResourceKey;)Z"))
    public boolean deeperdark$preventCustomerReset(Holder<VillagerProfession> instance, ResourceKey<VillagerProfession> key) {
        if (this.isPotionMaster && key.equals(VillagerProfession.NONE)) {
            // Logic: if we are a Potion Master and the code checks if we match NONE, return false!
            // This tricks the logic: if (profession.matchesKey(NONE)) -> false
            // So resetCustomer() is NOT called.
            return false;
        }
        return instance.is(key);
    }
}
