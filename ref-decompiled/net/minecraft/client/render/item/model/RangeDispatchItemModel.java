/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.item.ItemRenderState
 *  net.minecraft.client.render.item.model.ItemModel
 *  net.minecraft.client.render.item.model.RangeDispatchItemModel
 *  net.minecraft.client.render.item.property.numeric.NumericProperty
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.HeldItemContext
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.model;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RangeDispatchItemModel
implements ItemModel {
    private static final int field_55353 = 16;
    private final NumericProperty property;
    private final float scale;
    private final float[] thresholds;
    private final ItemModel[] models;
    private final ItemModel fallback;

    RangeDispatchItemModel(NumericProperty property, float scale, float[] thresholds, ItemModel[] models, ItemModel fallback) {
        this.property = property;
        this.thresholds = thresholds;
        this.models = models;
        this.fallback = fallback;
        this.scale = scale;
    }

    private static int getIndex(float[] thresholds, float value) {
        if (thresholds.length < 16) {
            for (int i = 0; i < thresholds.length; ++i) {
                if (!(thresholds[i] > value)) continue;
                return i - 1;
            }
            return thresholds.length - 1;
        }
        int i = Arrays.binarySearch(thresholds, value);
        if (i < 0) {
            int j = ~i;
            return j - 1;
        }
        return i;
    }

    public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable HeldItemContext heldItemContext, int seed) {
        int i;
        state.addModelKey((Object)this);
        float f = this.property.getValue(stack, world, heldItemContext, seed) * this.scale;
        ItemModel itemModel = Float.isNaN(f) ? this.fallback : ((i = RangeDispatchItemModel.getIndex((float[])this.thresholds, (float)f)) == -1 ? this.fallback : this.models[i]);
        itemModel.update(state, stack, resolver, displayContext, world, heldItemContext, seed);
    }
}

