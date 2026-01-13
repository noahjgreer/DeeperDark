package net.noahsarch.deeperdark.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    @Shadow @Final private Property levelCost;
    @Shadow private int repairItemUsage;

    public AnvilScreenHandlerMixin(net.minecraft.screen.ScreenHandlerType<?> type, int syncId, net.minecraft.entity.player.PlayerInventory playerInventory, net.minecraft.screen.ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context, null);
    }

    @Inject(method = "updateResult", at = @At("RETURN"))
    private void modifyAnvilCosts(CallbackInfo ci) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

        ItemStack input1 = this.input.getStack(0);
        ItemStack input2 = this.input.getStack(1);
        ItemStack output = this.output.getStack(0);

        // If there's no valid output, don't modify anything
        if (output.isEmpty() || input1.isEmpty()) {
            return;
        }

        // Determine if this is a repair or an enchanting operation
        boolean isRepair = false;
        boolean isEnchanting = false;

        if (!input2.isEmpty()) {
            // Check if it's a repair with materials
            if (input1.canRepairWith(input2) && this.repairItemUsage > 0) {
                isRepair = true;
            }
            // Check if it's enchanting (has stored enchantments or is combining enchanted items)
            else if (input2.contains(DataComponentTypes.STORED_ENCHANTMENTS)) {
                isEnchanting = true;
            }
            // Check if combining two of the same item with enchantments
            else if (input1.isOf(input2.getItem())) {
                ItemEnchantmentsComponent enchants1 = EnchantmentHelper.getEnchantments(input1);
                ItemEnchantmentsComponent enchants2 = EnchantmentHelper.getEnchantments(input2);
                if (!enchants1.isEmpty() || !enchants2.isEmpty()) {
                    isEnchanting = true;
                }
            }
        }

        // Apply custom costs
        if (isRepair) {
            // Set repair cost from config
            this.levelCost.set(config.anvilRepairCost);
        } else if (isEnchanting) {
            // Set flat enchant cost from config
            this.levelCost.set(config.anvilEnchantCost);
        }

        // Cap at 39 to prevent the "Too Expensive!" message
        if (this.levelCost.get() >= 40) {
            this.levelCost.set(39);
        }
    }

    @Inject(method = "canTakeOutput", at = @At("HEAD"), cancellable = true)
    private void allowFreeOperations(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        // Allow taking output if there's a valid result, even if cost is 0
        ItemStack output = this.output.getStack(0);
        if (!output.isEmpty() && this.levelCost.get() == 0) {
            // Free operation - allow taking the output
            cir.setReturnValue(true);
        } else if (!output.isEmpty() && (player.isInCreativeMode() || player.experienceLevel >= this.levelCost.get())) {
            // Normal operation with cost - allow if player has enough levels
            cir.setReturnValue(true);
        }
    }
}
