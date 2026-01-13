/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemAsset$Properties
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.item.ItemRenderState
 *  net.minecraft.client.render.item.model.ItemModel
 *  net.minecraft.client.render.model.BakedModelManager
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.HeldItemContext
 *  net.minecraft.util.Identifier
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.item;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ItemModelManager {
    private final Function<Identifier, ItemModel> modelGetter = arg_0 -> ((BakedModelManager)bakedModelManager).getItemModel(arg_0);
    private final Function<Identifier, ItemAsset.Properties> propertiesGetter = arg_0 -> ((BakedModelManager)bakedModelManager).getItemProperties(arg_0);

    public ItemModelManager(BakedModelManager bakedModelManager) {
    }

    public void updateForLivingEntity(ItemRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, LivingEntity entity) {
        this.clearAndUpdate(renderState, stack, displayContext, entity.getEntityWorld(), (HeldItemContext)entity, entity.getId() + displayContext.ordinal());
    }

    public void updateForNonLivingEntity(ItemRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, Entity entity) {
        this.clearAndUpdate(renderState, stack, displayContext, entity.getEntityWorld(), null, entity.getId());
    }

    public void clearAndUpdate(ItemRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, @Nullable World world, @Nullable HeldItemContext heldItemContext, int seed) {
        renderState.clear();
        if (!stack.isEmpty()) {
            renderState.displayContext = displayContext;
            this.update(renderState, stack, displayContext, world, heldItemContext, seed);
        }
    }

    public void update(ItemRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, @Nullable World world, @Nullable HeldItemContext heldItemContext, int seed) {
        ClientWorld clientWorld;
        Identifier identifier = (Identifier)stack.get(DataComponentTypes.ITEM_MODEL);
        if (identifier == null) {
            return;
        }
        renderState.setOversizedInGui(((ItemAsset.Properties)this.propertiesGetter.apply(identifier)).oversizedInGui());
        ((ItemModel)this.modelGetter.apply(identifier)).update(renderState, stack, this, displayContext, world instanceof ClientWorld ? (clientWorld = (ClientWorld)world) : null, heldItemContext, seed);
    }

    public boolean hasHandAnimationOnSwap(ItemStack stack) {
        Identifier identifier = (Identifier)stack.get(DataComponentTypes.ITEM_MODEL);
        if (identifier == null) {
            return true;
        }
        return ((ItemAsset.Properties)this.propertiesGetter.apply(identifier)).handAnimationOnSwap();
    }

    public float getSwapAnimationScale(ItemStack stack) {
        Identifier identifier = (Identifier)stack.get(DataComponentTypes.ITEM_MODEL);
        if (identifier == null) {
            return 1.0f;
        }
        return ((ItemAsset.Properties)this.propertiesGetter.apply(identifier)).swapAnimationScale();
    }
}

