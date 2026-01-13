/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.item.property.numeric.CustomModelDataFloatProperty
 *  net.minecraft.client.render.item.property.numeric.NumericProperty
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.CustomModelDataComponent
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.HeldItemContext
 *  net.minecraft.util.dynamic.Codecs
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.property.numeric;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.dynamic.Codecs;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record CustomModelDataFloatProperty(int index) implements NumericProperty
{
    private final int index;
    public static final MapCodec<CustomModelDataFloatProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.NON_NEGATIVE_INT.optionalFieldOf("index", (Object)0).forGetter(CustomModelDataFloatProperty::index)).apply((Applicative)instance, CustomModelDataFloatProperty::new));

    public CustomModelDataFloatProperty(int index) {
        this.index = index;
    }

    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable HeldItemContext context, int seed) {
        Float float_;
        CustomModelDataComponent customModelDataComponent = (CustomModelDataComponent)stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        if (customModelDataComponent != null && (float_ = customModelDataComponent.getFloat(this.index)) != null) {
            return float_.floatValue();
        }
        return 0.0f;
    }

    public MapCodec<CustomModelDataFloatProperty> getCodec() {
        return CODEC;
    }

    public int index() {
        return this.index;
    }
}

