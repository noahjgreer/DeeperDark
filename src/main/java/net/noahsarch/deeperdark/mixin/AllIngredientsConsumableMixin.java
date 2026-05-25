package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.util.IngredientItemRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class AllIngredientsConsumableMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void injectIngredientUse(Level level, Player player, InteractionHand hand,
                                     CallbackInfoReturnable<InteractionResult> cir) {
        if (!DeeperDarkConfig.get().allIngredientsConsumable) return;
        ItemStack stack = player.getItemInHand(hand);
        if (stack.has(DataComponents.CONSUMABLE)) return;
        if (!IngredientItemRegistry.ingredientItems.contains(stack.getItem())) return;

        player.startUsingItem(hand);
        cir.setReturnValue(InteractionResult.CONSUME);
    }

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void injectIngredientDuration(ItemStack stack, LivingEntity entity,
                                          CallbackInfoReturnable<Integer> cir) {
        if (!DeeperDarkConfig.get().allIngredientsConsumable) return;
        if (stack.has(DataComponents.CONSUMABLE)) return;
        if (!IngredientItemRegistry.ingredientItems.contains(stack.getItem())) return;

        cir.setReturnValue(32);
    }

    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void injectIngredientAnimation(ItemStack stack,
                                           CallbackInfoReturnable<ItemUseAnimation> cir) {
        if (!DeeperDarkConfig.get().allIngredientsConsumable) return;
        if (stack.has(DataComponents.CONSUMABLE)) return;
        if (!IngredientItemRegistry.ingredientItems.contains(stack.getItem())) return;

        cir.setReturnValue(ItemUseAnimation.EAT);
    }

    @Inject(method = "onUseTick", at = @At("HEAD"))
    private void injectIngredientUseTick(Level level, LivingEntity livingEntity, ItemStack stack,
                                         int ticksRemaining, CallbackInfo ci) {
        if (!DeeperDarkConfig.get().allIngredientsConsumable) return;
        if (stack.has(DataComponents.CONSUMABLE)) return;
        if (!IngredientItemRegistry.ingredientItems.contains(stack.getItem())) return;

        if (Consumables.DEFAULT_FOOD.shouldEmitParticlesAndSounds(ticksRemaining)) {
            Consumables.DEFAULT_FOOD.emitParticlesAndSounds(livingEntity.getRandom(), livingEntity, stack, 5);
        }
    }

    @Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    private void injectIngredientFinish(ItemStack stack, Level level, LivingEntity entity,
                                        CallbackInfoReturnable<ItemStack> cir) {
        if (!DeeperDarkConfig.get().allIngredientsConsumable) return;
        if (stack.has(DataComponents.CONSUMABLE)) return;
        if (!IngredientItemRegistry.ingredientItems.contains(stack.getItem())) return;

        stack.consume(1, entity);
        cir.setReturnValue(stack);
    }
}
