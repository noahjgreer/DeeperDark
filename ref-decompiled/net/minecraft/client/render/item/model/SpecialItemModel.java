/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.item.ItemRenderState
 *  net.minecraft.client.render.item.ItemRenderState$Glint
 *  net.minecraft.client.render.item.ItemRenderState$LayerRenderState
 *  net.minecraft.client.render.item.model.ItemModel
 *  net.minecraft.client.render.item.model.SpecialItemModel
 *  net.minecraft.client.render.item.model.special.SpecialModelRenderer
 *  net.minecraft.client.render.model.ModelSettings
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.HeldItemContext
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.model;

import com.google.common.base.Suppliers;
import java.util.HashSet;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.model.ModelSettings;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SpecialItemModel<T>
implements ItemModel {
    private final SpecialModelRenderer<T> specialModelType;
    private final ModelSettings settings;
    private final Supplier<Vector3fc[]> field_64591;

    public SpecialItemModel(SpecialModelRenderer<T> specialModelType, ModelSettings settings) {
        this.specialModelType = specialModelType;
        this.settings = settings;
        this.field_64591 = Suppliers.memoize(() -> {
            HashSet set = new HashSet();
            specialModelType.collectVertices(set::add);
            return set.toArray(new Vector3fc[0]);
        });
    }

    public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable HeldItemContext heldItemContext, int seed) {
        state.addModelKey((Object)this);
        ItemRenderState.LayerRenderState layerRenderState = state.newLayer();
        if (stack.hasGlint()) {
            ItemRenderState.Glint glint = ItemRenderState.Glint.STANDARD;
            layerRenderState.setGlint(glint);
            state.markAnimated();
            state.addModelKey((Object)glint);
        }
        Object object = this.specialModelType.getData(stack);
        layerRenderState.setVertices(this.field_64591);
        layerRenderState.setSpecialModel(this.specialModelType, object);
        if (object != null) {
            state.addModelKey(object);
        }
        this.settings.addSettings(layerRenderState, displayContext);
    }
}

