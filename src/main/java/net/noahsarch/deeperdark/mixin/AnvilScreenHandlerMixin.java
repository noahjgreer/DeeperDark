package net.noahsarch.deeperdark.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to modify anvil behavior:
 * - Repairs cost 0 XP
 * - Adding enchanted books or enchanted items costs 5 levels per enchantment
 */
@Mixin(AnvilMenu.class)
public abstract class AnvilScreenHandlerMixin extends ItemCombinerMenu {

    @Shadow
    @Final
    private DataSlot levelCost;

    @Shadow
    private String newItemName;

    @Shadow
    private int repairItemUsage;

    public AnvilScreenHandlerMixin(MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context, ItemCombinerMenuSlotDefinition forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    /**
     * Override the updateResult method to implement custom cost logic.
     * - Repairs (using materials) = 0 XP
     * - Adding enchantments = 5 levels per enchantment being added
     * - Renaming only = 1 level (vanilla behavior)
     */
    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void deeperdark$customUpdateResult(CallbackInfo ci) {
        ItemStack inputStack = this.input.getStack(0);

        if (inputStack.isEmpty() || !EnchantmentHelper.canHaveEnchantments(inputStack)) {
            this.output.setStack(0, ItemStack.EMPTY);
            this.levelCost.set(0);
            ci.cancel();
            return;
        }

        ItemStack resultStack = inputStack.copy();
        ItemStack secondStack = this.input.getStack(1);
        ItemEnchantments.Builder enchantmentBuilder = new ItemEnchantments.Builder(
                EnchantmentHelper.getEnchantments(resultStack));

        this.repairItemUsage = 0;
        int totalCost = 0;
        int enchantmentsAdded = 0;
        boolean isRenameOnly = true;

        if (!secondStack.isEmpty()) {
            isRenameOnly = false;
            boolean hasStoredEnchantments = secondStack.contains(DataComponents.STORED_ENCHANTMENTS);

            // Check if this is a material repair (e.g., diamond to repair diamond tool)
            if (resultStack.isDamageable() && inputStack.canRepairWith(secondStack)) {
                // Material repair - 0 XP cost
                int repairAmount = Math.min(resultStack.getDamage(), resultStack.getMaxDamage() / 4);
                if (repairAmount <= 0) {
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.levelCost.set(0);
                    ci.cancel();
                    return;
                }

                int repairCount;
                for (repairCount = 0; repairAmount > 0 && repairCount < secondStack.getCount(); repairCount++) {
                    int newDamage = resultStack.getDamage() - repairAmount;
                    resultStack.setDamage(newDamage);
                    repairAmount = Math.min(resultStack.getDamage(), resultStack.getMaxDamage() / 4);
                }

                this.repairItemUsage = repairCount;
                // Repair cost is 0
                totalCost = 0;
            } else {
                // Check if it's a same-item repair or enchantment transfer
                if (!hasStoredEnchantments && (!resultStack.isOf(secondStack.getItem()) || !resultStack.isDamageable())) {
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.levelCost.set(0);
                    ci.cancel();
                    return;
                }

                // Same-item repair (combining durability)
                if (resultStack.isDamageable() && !hasStoredEnchantments) {
                    int firstDurability = inputStack.getMaxDamage() - inputStack.getDamage();
                    int secondDurability = secondStack.getMaxDamage() - secondStack.getDamage();
                    int bonusDurability = resultStack.getMaxDamage() * 12 / 100;
                    int totalDurability = firstDurability + secondDurability + bonusDurability;
                    int newDamage = resultStack.getMaxDamage() - totalDurability;
                    if (newDamage < 0) {
                        newDamage = 0;
                    }

                    if (newDamage < resultStack.getDamage()) {
                        resultStack.setDamage(newDamage);
                        // Durability repair is free
                    }
                }

                // Transfer enchantments from second item (enchanted book or item)
                ItemEnchantments sourceEnchantments = EnchantmentHelper.getEnchantments(secondStack);
                boolean anyEnchantmentAdded = false;
                boolean anyEnchantmentFailed = false;

                for (Object2IntMap.Entry<Holder<Enchantment>> entry : sourceEnchantments.getEnchantmentEntries()) {
                    Holder<Enchantment> enchantmentEntry = entry.getKey();
                    int currentLevel = enchantmentBuilder.getLevel(enchantmentEntry);
                    int sourceLevel = entry.getIntValue();
                    int newLevel = currentLevel == sourceLevel ? sourceLevel + 1 : Math.max(sourceLevel, currentLevel);

                    Enchantment enchantment = enchantmentEntry.value();
                    boolean canApply = enchantment.isAcceptableItem(inputStack);
                    if (this.player.isInCreativeMode() || inputStack.isOf(net.minecraft.world.item.Items.ENCHANTED_BOOK)) {
                        canApply = true;
                    }

                    // Check for enchantment conflicts
                    for (Holder<Enchantment> existingEnchantment : enchantmentBuilder.getEnchantments()) {
                        if (!existingEnchantment.equals(enchantmentEntry) && !Enchantment.canBeCombined(enchantmentEntry, existingEnchantment)) {
                            canApply = false;
                        }
                    }

                    if (!canApply) {
                        anyEnchantmentFailed = true;
                    } else {
                        anyEnchantmentAdded = true;
                        if (newLevel > enchantment.getMaxLevel()) {
                            newLevel = enchantment.getMaxLevel();
                        }

                        // Only count as "added" if it's actually new or upgraded
                        if (currentLevel < newLevel || currentLevel == 0) {
                            enchantmentsAdded++;
                        }

                        enchantmentBuilder.set(enchantmentEntry, newLevel);
                    }
                }

                if (anyEnchantmentFailed && !anyEnchantmentAdded) {
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.levelCost.set(0);
                    ci.cancel();
                    return;
                }

                // Cost: 5 levels per enchantment added/upgraded
                totalCost = enchantmentsAdded * 5;
            }
        }

        // Handle renaming
        boolean renamed = false;
        if (this.newItemName != null && !StringUtil.isBlank(this.newItemName)) {
            if (!this.newItemName.equals(inputStack.getName().getString())) {
                renamed = true;
                resultStack.set(DataComponents.CUSTOM_NAME, Component.literal(this.newItemName));
                if (isRenameOnly) {
                    totalCost = 1; // Rename-only costs 1 level
                }
            }
        } else if (inputStack.contains(DataComponents.CUSTOM_NAME)) {
            renamed = true;
            resultStack.remove(DataComponents.CUSTOM_NAME);
            if (isRenameOnly) {
                totalCost = 1; // Removing name costs 1 level
            }
        }

        // Set the cost
        this.levelCost.set(totalCost);

        if (totalCost <= 0 && !renamed && secondStack.isEmpty()) {
            // Nothing changed
            this.output.setStack(0, ItemStack.EMPTY);
            this.levelCost.set(0);
            ci.cancel();
            return;
        }

        // Update repair cost on the result to prevent "Too Expensive"
        // We set repair cost to 0 to allow unlimited operations
        resultStack.set(DataComponents.REPAIR_COST, 0);

        // Apply enchantments
        EnchantmentHelper.set(resultStack, enchantmentBuilder.build());

        this.output.setStack(0, resultStack);
        this.sendContentUpdates();
        ci.cancel();
    }
}
