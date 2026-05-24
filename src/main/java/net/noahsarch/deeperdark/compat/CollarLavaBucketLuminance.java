package net.noahsarch.deeperdark.compat;

import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.noahsarch.deeperdark.duck.CollarHolder;
import net.noahsarch.deeperdark.item.CollarItem;

public final class CollarLavaBucketLuminance implements EntityLuminance {

    public static final CollarLavaBucketLuminance INSTANCE = new CollarLavaBucketLuminance();

    public static final Type TYPE = Type.registerSimple(
        Identifier.fromNamespaceAndPath("deeperdark", "collar_lava_bucket"),
        INSTANCE
    );

    private CollarLavaBucketLuminance() {}

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
            if (trinket.is(Items.LAVA_BUCKET)) return 15;
        }
        return 0;
    }
}
