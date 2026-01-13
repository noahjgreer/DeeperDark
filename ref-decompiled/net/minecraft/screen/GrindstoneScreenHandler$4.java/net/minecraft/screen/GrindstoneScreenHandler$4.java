/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 */
package net.minecraft.screen;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

class GrindstoneScreenHandler.4
extends Slot {
    final /* synthetic */ ScreenHandlerContext field_16779;

    GrindstoneScreenHandler.4(Inventory inventory, int i, int j, int k, ScreenHandlerContext screenHandlerContext) {
        this.field_16779 = screenHandlerContext;
        super(inventory, i, j, k);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        this.field_16779.run((world, pos) -> {
            if (world instanceof ServerWorld) {
                ExperienceOrbEntity.spawn((ServerWorld)world, Vec3d.ofCenter(pos), this.getExperience((World)world));
            }
            world.syncWorldEvent(1042, (BlockPos)pos, 0);
        });
        GrindstoneScreenHandler.this.input.setStack(0, ItemStack.EMPTY);
        GrindstoneScreenHandler.this.input.setStack(1, ItemStack.EMPTY);
    }

    private int getExperience(World world) {
        int i = 0;
        i += this.getExperience(GrindstoneScreenHandler.this.input.getStack(0));
        if ((i += this.getExperience(GrindstoneScreenHandler.this.input.getStack(1))) > 0) {
            int j = (int)Math.ceil((double)i / 2.0);
            return j + world.random.nextInt(j);
        }
        return 0;
    }

    private int getExperience(ItemStack stack) {
        int i = 0;
        ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(stack);
        for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : itemEnchantmentsComponent.getEnchantmentEntries()) {
            RegistryEntry registryEntry = (RegistryEntry)entry.getKey();
            int j = entry.getIntValue();
            if (registryEntry.isIn(EnchantmentTags.CURSE)) continue;
            i += ((Enchantment)registryEntry.value()).getMinPower(j);
        }
        return i;
    }
}
