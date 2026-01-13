/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.item.property.select.CustomModelDataStringProperty
 *  net.minecraft.client.render.item.property.select.SelectProperty
 *  net.minecraft.client.render.item.property.select.SelectProperty$Type
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.CustomModelDataComponent
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.dynamic.Codecs
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.property.select;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record CustomModelDataStringProperty(int index) implements SelectProperty<String>
{
    private final int index;
    public static final PrimitiveCodec<String> VALUE_CODEC = Codec.STRING;
    public static final SelectProperty.Type<CustomModelDataStringProperty, String> TYPE = SelectProperty.Type.create((MapCodec)RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.NON_NEGATIVE_INT.optionalFieldOf("index", (Object)0).forGetter(CustomModelDataStringProperty::index)).apply((Applicative)instance, CustomModelDataStringProperty::new)), (Codec)VALUE_CODEC);

    public CustomModelDataStringProperty(int index) {
        this.index = index;
    }

    public @Nullable String getValue(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
        CustomModelDataComponent customModelDataComponent = (CustomModelDataComponent)itemStack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        if (customModelDataComponent != null) {
            return customModelDataComponent.getString(this.index);
        }
        return null;
    }

    public SelectProperty.Type<CustomModelDataStringProperty, String> getType() {
        return TYPE;
    }

    public Codec<String> valueCodec() {
        return VALUE_CODEC;
    }

    public int index() {
        return this.index;
    }

    public /* synthetic */ @Nullable Object getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ItemDisplayContext displayContext) {
        return this.getValue(stack, world, user, seed, displayContext);
    }
}

