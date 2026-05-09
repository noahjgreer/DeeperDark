package net.noahsarch.deeperdark.compat;

import com.mojang.serialization.MapCodec;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.noahsarch.deeperdark.duck.CollarHolder;
import net.noahsarch.deeperdark.item.CollarItem;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;

/**
 * Entity luminance that returns 11 (75% of torch brightness) when the player's
 * collar has glow berries in one of its trinket slots.
 */
public final class CollarGlowBerriesLuminance implements EntityLuminance {

    public static final CollarGlowBerriesLuminance INSTANCE = new CollarGlowBerriesLuminance();

    public static final Type TYPE = Type.registerSimple(
        Identifier.fromNamespaceAndPath("deeperdark", "collar_glow_berries"),
        INSTANCE
    );

    private CollarGlowBerriesLuminance() {}

    @Override
    public Type type() {
        return TYPE;
    }

    @Override
    public int getLuminance(ItemLightSourceManager itemLightSourceManager, Entity entity) {
        if (!(entity instanceof CollarHolder holder)) return 0;
        ItemStack collar = holder.deeperdark$getCollarItem();
        if (collar.isEmpty() || !(collar.getItem() instanceof CollarItem)) return 0;
        ItemContainerContents contents = collar.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        for (ItemStack trinket : (Iterable<ItemStack>) contents.nonEmptyItemCopyStream()::iterator) {
            if (trinket.is(Items.GLOW_BERRIES)) return 11; // ~75% of torch (15)
        }
        return 0;
    }
}
