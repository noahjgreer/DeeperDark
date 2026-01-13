/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.item.ItemRenderState
 *  net.minecraft.client.render.item.model.CompositeItemModel
 *  net.minecraft.client.render.item.model.ItemModel
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.HeldItemContext
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.model;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CompositeItemModel
implements ItemModel {
    private final List<ItemModel> models;

    public CompositeItemModel(List<ItemModel> models) {
        this.models = models;
    }

    public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable HeldItemContext heldItemContext, int seed) {
        state.addModelKey((Object)this);
        state.addLayers(this.models.size());
        for (ItemModel itemModel : this.models) {
            itemModel.update(state, stack, resolver, displayContext, world, heldItemContext, seed);
        }
    }
}

