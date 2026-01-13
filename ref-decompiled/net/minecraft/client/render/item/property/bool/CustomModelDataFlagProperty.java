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
 *  net.minecraft.client.render.item.property.bool.BooleanProperty
 *  net.minecraft.client.render.item.property.bool.CustomModelDataFlagProperty
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.CustomModelDataComponent
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.dynamic.Codecs
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.property.bool;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record CustomModelDataFlagProperty(int index) implements BooleanProperty
{
    private final int index;
    public static final MapCodec<CustomModelDataFlagProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.NON_NEGATIVE_INT.optionalFieldOf("index", (Object)0).forGetter(CustomModelDataFlagProperty::index)).apply((Applicative)instance, CustomModelDataFlagProperty::new));

    public CustomModelDataFlagProperty(int index) {
        this.index = index;
    }

    public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        CustomModelDataComponent customModelDataComponent = (CustomModelDataComponent)stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        if (customModelDataComponent != null) {
            return customModelDataComponent.getFlag(this.index) == Boolean.TRUE;
        }
        return false;
    }

    public MapCodec<CustomModelDataFlagProperty> getCodec() {
        return CODEC;
    }

    public int index() {
        return this.index;
    }
}

