package net.noahsarch.deeperdark.menu;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.noahsarch.deeperdark.duck.CollarHolder;
import net.noahsarch.deeperdark.item.CollarItem;

public class CollarSlot extends Slot {

    private final CollarHolder holder;

    public CollarSlot(CollarHolder holder, int x, int y) {
        super(new SimpleContainer(1), 0, x, y);
        this.holder = holder;
    }

    @Override
    public ItemStack getItem() {
        return holder.deeperdark$getCollarItem();
    }

    @Override
    public void set(ItemStack stack) {
        holder.deeperdark$setCollarItem(stack.isEmpty() ? ItemStack.EMPTY : stack);
        checkAndEjectPassengers(stack.isEmpty() ? ItemStack.EMPTY : stack);
        this.setChanged();
    }

    @Override
    public void setByPlayer(ItemStack stack) {
        set(stack);
    }

    @Override
    public boolean hasItem() {
        return !holder.deeperdark$getCollarItem().isEmpty();
    }

    @Override
    public ItemStack remove(int amount) {
        ItemStack current = holder.deeperdark$getCollarItem();
        if (current.isEmpty()) return ItemStack.EMPTY;
        if (amount >= current.getCount()) {
            holder.deeperdark$setCollarItem(ItemStack.EMPTY);
            checkAndEjectPassengers(ItemStack.EMPTY);
            return current;
        }
        ItemStack removed = current.split(amount);
        holder.deeperdark$setCollarItem(current.isEmpty() ? ItemStack.EMPTY : current);
        checkAndEjectPassengers(current.isEmpty() ? ItemStack.EMPTY : current);
        return removed;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof CollarItem;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public Identifier getNoItemIcon() {
        return Identifier.fromNamespaceAndPath("minecraft", "container/slot/collar");
    }

    private void checkAndEjectPassengers(ItemStack newCollar) {
        if (!(holder instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (!player.isVehicle()) return;
        // Keep riders if a saddle is in the head slot
        if (player.getItemBySlot(EquipmentSlot.HEAD).is(Items.SADDLE)) return;
        // Keep riders if the new collar still contains a saddle trinket
        if (!newCollar.isEmpty()) {
            ItemContainerContents contents = newCollar.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            if (contents.nonEmptyItemCopyStream().anyMatch(s -> s.is(Items.SADDLE))) return;
        }
        player.ejectPassengers();
    }
}
