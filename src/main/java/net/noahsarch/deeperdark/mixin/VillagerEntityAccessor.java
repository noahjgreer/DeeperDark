package net.noahsarch.deeperdark.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.noahsarch.deeperdark.villager.ModVillagers;

@Mixin(AbstractVillager.class)
public interface VillagerEntityAccessor {
    default void deeperdark$fillRecipesFromPool(ServerLevel world, MerchantOffers recipeList, ModVillagers.TradeFactory[] pool, int count) {
        // fillRecipesFromPool was removed in 26.1.2; trades added directly in PotionMasterMixin
    }
}
