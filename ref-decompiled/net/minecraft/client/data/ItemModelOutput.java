/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.data.ItemModelOutput
 *  net.minecraft.client.item.ItemAsset$Properties
 *  net.minecraft.client.render.item.model.ItemModel$Unbaked
 *  net.minecraft.item.Item
 */
package net.minecraft.client.data;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.item.Item;

@Environment(value=EnvType.CLIENT)
public interface ItemModelOutput {
    default public void accept(Item item, ItemModel.Unbaked model) {
        this.accept(item, model, ItemAsset.Properties.DEFAULT);
    }

    public void accept(Item var1, ItemModel.Unbaked var2, ItemAsset.Properties var3);

    public void acceptAlias(Item var1, Item var2);
}

