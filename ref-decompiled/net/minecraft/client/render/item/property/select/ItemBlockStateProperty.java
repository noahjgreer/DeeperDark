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
 *  net.minecraft.client.render.item.property.select.ItemBlockStateProperty
 *  net.minecraft.client.render.item.property.select.SelectProperty
 *  net.minecraft.client.render.item.property.select.SelectProperty$Type
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.BlockStateComponent
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
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
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ItemBlockStateProperty(String property) implements SelectProperty<String>
{
    private final String property;
    public static final PrimitiveCodec<String> VALUE_CODEC = Codec.STRING;
    public static final SelectProperty.Type<ItemBlockStateProperty, String> TYPE = SelectProperty.Type.create((MapCodec)RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.STRING.fieldOf("block_state_property").forGetter(ItemBlockStateProperty::property)).apply((Applicative)instance, ItemBlockStateProperty::new)), (Codec)VALUE_CODEC);

    public ItemBlockStateProperty(String property) {
        this.property = property;
    }

    public @Nullable String getValue(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
        BlockStateComponent blockStateComponent = (BlockStateComponent)itemStack.get(DataComponentTypes.BLOCK_STATE);
        if (blockStateComponent == null) {
            return null;
        }
        return (String)blockStateComponent.properties().get(this.property);
    }

    public SelectProperty.Type<ItemBlockStateProperty, String> getType() {
        return TYPE;
    }

    public Codec<String> valueCodec() {
        return VALUE_CODEC;
    }

    public String property() {
        return this.property;
    }

    public /* synthetic */ @Nullable Object getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ItemDisplayContext displayContext) {
        return this.getValue(stack, world, user, seed, displayContext);
    }
}

