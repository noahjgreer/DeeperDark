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

@Mixin(AnvilMenu.class)
public abstract class AnvilScreenHandlerMixin extends ItemCombinerMenu {

    @Shadow
    @Final
    private DataSlot cost;

    @Shadow
    private String itemName;

    @Shadow
    private int repairItemCountCost;

    @Shadow
    private boolean onlyRenaming;

    public AnvilScreenHandlerMixin(MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context, ItemCombinerMenuSlotDefinition forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void deeperdark$customUpdateResult(CallbackInfo ci) {
        ItemStack inputStack = this.inputSlots.getItem(0);

        if (inputStack.isEmpty() || !EnchantmentHelper.canStoreEnchantments(inputStack)) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(0);
            ci.cancel();
            return;
        }

        ItemStack resultStack = inputStack.copy();
        ItemStack secondStack = this.inputSlots.getItem(1);
        ItemEnchantments.Mutable enchantmentBuilder = new ItemEnchantments.Mutable(
                EnchantmentHelper.getEnchantmentsForCrafting(resultStack));

        this.repairItemCountCost = 0;
        this.onlyRenaming = false;
        int totalCost = 0;
        int enchantmentsAdded = 0;
        boolean isRenameOnly = secondStack.isEmpty();

        if (!secondStack.isEmpty()) {
            boolean hasStoredEnchantments = secondStack.has(DataComponents.STORED_ENCHANTMENTS);

            if (resultStack.isDamageableItem() && inputStack.isValidRepairItem(secondStack)) {
                int repairAmount = Math.min(resultStack.getDamageValue(), resultStack.getMaxDamage() / 4);
                if (repairAmount <= 0) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.cost.set(0);
                    ci.cancel();
                    return;
                }

                int repairCount;
                for (repairCount = 0; repairAmount > 0 && repairCount < secondStack.getCount(); repairCount++) {
                    int newDamage = resultStack.getDamageValue() - repairAmount;
                    resultStack.setDamageValue(newDamage);
                    repairAmount = Math.min(resultStack.getDamageValue(), resultStack.getMaxDamage() / 4);
                }

                this.repairItemCountCost = repairCount;
                totalCost = 5;
            } else {
                if (!hasStoredEnchantments && (resultStack.getItem() != secondStack.getItem() || !resultStack.isDamageableItem())) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.cost.set(0);
                    ci.cancel();
                    return;
                }

                if (resultStack.isDamageableItem() && !hasStoredEnchantments) {
                    int firstDurability = inputStack.getMaxDamage() - inputStack.getDamageValue();
                    int secondDurability = secondStack.getMaxDamage() - secondStack.getDamageValue();
                    int bonusDurability = resultStack.getMaxDamage() * 12 / 100;
                    int totalDurability = firstDurability + secondDurability + bonusDurability;
                    int newDamage = resultStack.getMaxDamage() - totalDurability;
                    if (newDamage < 0) {
                        newDamage = 0;
                    }

                    if (newDamage < resultStack.getDamageValue()) {
                        resultStack.setDamageValue(newDamage);
                    }
                }

                ItemEnchantments sourceEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(secondStack);
                boolean anyEnchantmentAdded = false;
                boolean anyEnchantmentFailed = false;

                for (Object2IntMap.Entry<Holder<Enchantment>> entry : sourceEnchantments.entrySet()) {
                    Holder<Enchantment> enchantmentEntry = entry.getKey();
                    int currentLevel = enchantmentBuilder.getLevel(enchantmentEntry);
                    int sourceLevel = entry.getIntValue();
                    int newLevel = currentLevel == sourceLevel ? sourceLevel + 1 : Math.max(sourceLevel, currentLevel);

                    Enchantment enchantment = enchantmentEntry.value();
                    boolean canApply = enchantment.canEnchant(inputStack);
                    if (this.player.hasInfiniteMaterials() || inputStack.getItem() == net.minecraft.world.item.Items.ENCHANTED_BOOK) {
                        canApply = true;
                    }

                    for (Holder<Enchantment> existingEnchantment : enchantmentBuilder.keySet()) {
                        if (!existingEnchantment.equals(enchantmentEntry) && !Enchantment.areCompatible(enchantmentEntry, existingEnchantment)) {
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

                        if (currentLevel < newLevel || currentLevel == 0) {
                            enchantmentsAdded++;
                        }

                        enchantmentBuilder.set(enchantmentEntry, newLevel);
                    }
                }

                if (anyEnchantmentFailed && !anyEnchantmentAdded) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.cost.set(0);
                    ci.cancel();
                    return;
                }

                totalCost = 6;
            }
        }

        boolean renamed = false;
        if (this.itemName != null && !StringUtil.isBlank(this.itemName)) {
            if (!this.itemName.equals(inputStack.getHoverName().getString())) {
                renamed = true;
                resultStack.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName));
                if (isRenameOnly) {
                    totalCost = 1;
                }
            }
        } else if (inputStack.has(DataComponents.CUSTOM_NAME)) {
            renamed = true;
            resultStack.remove(DataComponents.CUSTOM_NAME);
            if (isRenameOnly) {
                totalCost = 1;
            }
        }

        if (isRenameOnly && renamed) {
            this.onlyRenaming = true;
        }

        this.cost.set(totalCost);

        if (totalCost <= 0 && !renamed && secondStack.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(0);
            ci.cancel();
            return;
        }

        resultStack.set(DataComponents.REPAIR_COST, 0);
        EnchantmentHelper.setEnchantments(resultStack, enchantmentBuilder.toImmutable());

        this.resultSlots.setItem(0, resultStack);
        this.broadcastChanges();
        ci.cancel();
    }
}
