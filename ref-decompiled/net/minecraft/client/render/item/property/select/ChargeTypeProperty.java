/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.item.property.select.ChargeTypeProperty
 *  net.minecraft.client.render.item.property.select.SelectProperty
 *  net.minecraft.client.render.item.property.select.SelectProperty$Type
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.ChargedProjectilesComponent
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.CrossbowItem$ChargeType
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.property.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ChargeTypeProperty() implements SelectProperty<CrossbowItem.ChargeType>
{
    public static final Codec<CrossbowItem.ChargeType> VALUE_CODEC = CrossbowItem.ChargeType.CODEC;
    public static final SelectProperty.Type<ChargeTypeProperty, CrossbowItem.ChargeType> TYPE = SelectProperty.Type.create((MapCodec)MapCodec.unit((Object)new ChargeTypeProperty()), (Codec)VALUE_CODEC);

    public CrossbowItem.ChargeType getValue(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
        ChargedProjectilesComponent chargedProjectilesComponent = (ChargedProjectilesComponent)itemStack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if (chargedProjectilesComponent == null || chargedProjectilesComponent.isEmpty()) {
            return CrossbowItem.ChargeType.NONE;
        }
        if (chargedProjectilesComponent.contains(Items.FIREWORK_ROCKET)) {
            return CrossbowItem.ChargeType.ROCKET;
        }
        return CrossbowItem.ChargeType.ARROW;
    }

    public SelectProperty.Type<ChargeTypeProperty, CrossbowItem.ChargeType> getType() {
        return TYPE;
    }

    public Codec<CrossbowItem.ChargeType> valueCodec() {
        return VALUE_CODEC;
    }

    public /* synthetic */ Object getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ItemDisplayContext displayContext) {
        return this.getValue(stack, world, user, seed, displayContext);
    }
}

