/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.state.property.Property;

@Environment(value=EnvType.CLIENT)
record ModelGrouper.GroupKey(Object equalityGroup, List<Object> coloringValues) {
    public static ModelGrouper.GroupKey of(BlockState state, BlockStateModel.UnbakedGrouped model, List<Property<?>> properties) {
        List<Object> list = ModelGrouper.GroupKey.getColoringValues(state, properties);
        Object object = model.getEqualityGroup(state);
        return new ModelGrouper.GroupKey(object, list);
    }

    private static List<Object> getColoringValues(BlockState state, List<Property<?>> properties) {
        Object[] objects = new Object[properties.size()];
        for (int i = 0; i < properties.size(); ++i) {
            objects[i] = state.get(properties.get(i));
        }
        return List.of(objects);
    }
}
