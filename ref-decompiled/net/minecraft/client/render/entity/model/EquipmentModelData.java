/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.model.TexturedModelData
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayer
 *  net.minecraft.client.render.entity.model.EquipmentModelData
 *  net.minecraft.client.render.entity.model.EquipmentModelData$1
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.entity.EquipmentSlot
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableMap;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.entity.EquipmentSlot;

@Environment(value=EnvType.CLIENT)
public record EquipmentModelData<T>(T head, T chest, T legs, T feet) {
    private final T head;
    private final T chest;
    private final T legs;
    private final T feet;

    public EquipmentModelData(T head, T chest, T legs, T feet) {
        this.head = head;
        this.chest = chest;
        this.legs = legs;
        this.feet = feet;
    }

    public T getModelData(EquipmentSlot slot) {
        return (T)(switch (1.field_61795[slot.ordinal()]) {
            case 1 -> this.head;
            case 2 -> this.chest;
            case 3 -> this.legs;
            case 4 -> this.feet;
            default -> throw new IllegalStateException("No model for slot: " + String.valueOf(slot));
        });
    }

    public <U> EquipmentModelData<U> map(Function<? super T, ? extends U> f) {
        return new EquipmentModelData(f.apply(this.head), f.apply(this.chest), f.apply(this.legs), f.apply(this.feet));
    }

    public void addTo(EquipmentModelData<TexturedModelData> texturedModelData, ImmutableMap.Builder<T, TexturedModelData> builder) {
        builder.put(this.head, (Object)((TexturedModelData)texturedModelData.head));
        builder.put(this.chest, (Object)((TexturedModelData)texturedModelData.chest));
        builder.put(this.legs, (Object)((TexturedModelData)texturedModelData.legs));
        builder.put(this.feet, (Object)((TexturedModelData)texturedModelData.feet));
    }

    public static <M extends BipedEntityModel<?>> EquipmentModelData<M> mapToEntityModel(EquipmentModelData<EntityModelLayer> data, LoadedEntityModels models, Function<ModelPart, M> modelPartToModel) {
        return data.map(layer -> (BipedEntityModel)modelPartToModel.apply(models.getModelPart(layer)));
    }

    public T head() {
        return (T)this.head;
    }

    public T chest() {
        return (T)this.chest;
    }

    public T legs() {
        return (T)this.legs;
    }

    public T feet() {
        return (T)this.feet;
    }
}

