/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.item.property.select.ContextEntityTypeProperty
 *  net.minecraft.client.render.item.property.select.SelectProperty
 *  net.minecraft.client.render.item.property.select.SelectProperty$Type
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.property.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ContextEntityTypeProperty() implements SelectProperty<RegistryKey<EntityType<?>>>
{
    public static final Codec<RegistryKey<EntityType<?>>> VALUE_CODEC = RegistryKey.createCodec((RegistryKey)RegistryKeys.ENTITY_TYPE);
    public static final SelectProperty.Type<ContextEntityTypeProperty, RegistryKey<EntityType<?>>> TYPE = SelectProperty.Type.create((MapCodec)MapCodec.unit((Object)new ContextEntityTypeProperty()), (Codec)VALUE_CODEC);

    public @Nullable RegistryKey<EntityType<?>> getValue(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
        return livingEntity == null ? null : livingEntity.getType().getRegistryEntry().registryKey();
    }

    public SelectProperty.Type<ContextEntityTypeProperty, RegistryKey<EntityType<?>>> getType() {
        return TYPE;
    }

    public Codec<RegistryKey<EntityType<?>>> valueCodec() {
        return VALUE_CODEC;
    }

    public /* synthetic */ @Nullable Object getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ItemDisplayContext displayContext) {
        return this.getValue(stack, world, user, seed, displayContext);
    }
}

