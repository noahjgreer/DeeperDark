/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.model;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelCuboidData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelTransform;

@Environment(value=EnvType.CLIENT)
public class ModelPartData {
    private final List<ModelCuboidData> cuboidData;
    private final ModelTransform transform;
    private final Map<String, ModelPartData> children = Maps.newHashMap();

    ModelPartData(List<ModelCuboidData> cuboidData, ModelTransform transform) {
        this.cuboidData = cuboidData;
        this.transform = transform;
    }

    public ModelPartData addChild(String name, ModelPartBuilder builder, ModelTransform transform) {
        ModelPartData modelPartData = new ModelPartData(builder.build(), transform);
        return this.addChild(name, modelPartData);
    }

    public ModelPartData addChild(String name, ModelPartData data) {
        ModelPartData modelPartData = this.children.put(name, data);
        if (modelPartData != null) {
            data.children.putAll(modelPartData.children);
        }
        return data;
    }

    public ModelPartData resetChildrenParts() {
        for (String string : this.children.keySet()) {
            this.resetChildrenParts(string).resetChildrenParts();
        }
        return this;
    }

    public ModelPartData resetChildrenParts(String name) {
        ModelPartData modelPartData = this.children.get(name);
        if (modelPartData == null) {
            throw new IllegalArgumentException("No child with name: " + name);
        }
        return this.addChild(name, ModelPartBuilder.create(), modelPartData.transform);
    }

    public void resetChildrenExcept(Set<String> names) {
        for (Map.Entry<String, ModelPartData> entry : this.children.entrySet()) {
            ModelPartData modelPartData = entry.getValue();
            if (names.contains(entry.getKey())) continue;
            this.addChild(entry.getKey(), ModelPartBuilder.create(), modelPartData.transform).resetChildrenExcept(names);
        }
    }

    public void resetChildrenExceptExact(Set<String> names) {
        for (Map.Entry<String, ModelPartData> entry : this.children.entrySet()) {
            ModelPartData modelPartData = entry.getValue();
            if (names.contains(entry.getKey())) {
                modelPartData.resetChildrenParts();
                continue;
            }
            this.addChild(entry.getKey(), ModelPartBuilder.create(), modelPartData.transform).resetChildrenExceptExact(names);
        }
    }

    public ModelPart createPart(int textureWidth, int textureHeight) {
        Object2ObjectArrayMap object2ObjectArrayMap = this.children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> ((ModelPartData)entry.getValue()).createPart(textureWidth, textureHeight), (name, partData) -> name, Object2ObjectArrayMap::new));
        List<ModelPart.Cuboid> list = this.cuboidData.stream().map(data -> data.createCuboid(textureWidth, textureHeight)).toList();
        ModelPart modelPart = new ModelPart(list, (Map<String, ModelPart>)object2ObjectArrayMap);
        modelPart.setDefaultTransform(this.transform);
        modelPart.setTransform(this.transform);
        return modelPart;
    }

    public ModelPartData getChild(String name) {
        return this.children.get(name);
    }

    public Set<Map.Entry<String, ModelPartData>> getChildren() {
        return this.children.entrySet();
    }

    public ModelPartData applyTransformer(UnaryOperator<ModelTransform> transformer) {
        ModelPartData modelPartData = new ModelPartData(this.cuboidData, (ModelTransform)transformer.apply(this.transform));
        modelPartData.children.putAll(this.children);
        return modelPartData;
    }
}
