package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MerchantEntity.class)
public interface VillagerEntityAccessor {
    @Invoker("fillRecipesFromPool")
    void deeperdark$fillRecipesFromPool(TradeOfferList recipeList, TradeOffers.Factory[] pool, int count);
}

