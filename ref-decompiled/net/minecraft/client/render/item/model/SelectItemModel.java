/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.item.ItemRenderState
 *  net.minecraft.client.render.item.model.ItemModel
 *  net.minecraft.client.render.item.model.SelectItemModel
 *  net.minecraft.client.render.item.model.SelectItemModel$ModelSelector
 *  net.minecraft.client.render.item.property.select.SelectProperty
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.HeldItemContext
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.SelectItemModel;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SelectItemModel<T>
implements ItemModel {
    private final SelectProperty<T> property;
    private final ModelSelector<T> selector;

    public SelectItemModel(SelectProperty<T> property, ModelSelector<T> selector) {
        this.property = property;
        this.selector = selector;
    }

    public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable HeldItemContext heldItemContext, int seed) {
        state.addModelKey((Object)this);
        Object object = this.property.getValue(stack, world, heldItemContext == null ? null : heldItemContext.getEntity(), seed, displayContext);
        ItemModel itemModel = this.selector.get(object, world);
        if (itemModel != null) {
            itemModel.update(state, stack, resolver, displayContext, world, heldItemContext, seed);
        }
    }
}

