/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.item.model.SelectItemModel$UnbakedSwitch
 *  net.minecraft.client.render.item.property.select.ComponentSelectProperty
 *  net.minecraft.client.render.item.property.select.SelectProperty
 *  net.minecraft.client.render.item.property.select.SelectProperty$Type
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.component.ComponentType
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.Registries
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.property.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.SelectItemModel;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public record ComponentSelectProperty<T>(ComponentType<T> componentType) implements SelectProperty<T>
{
    private final ComponentType<T> componentType;
    private static final SelectProperty.Type<? extends ComponentSelectProperty<?>, ?> TYPE = ComponentSelectProperty.createType();

    public ComponentSelectProperty(ComponentType<T> componentType) {
        this.componentType = componentType;
    }

    private static <T> SelectProperty.Type<ComponentSelectProperty<T>, T> createType() {
        Codec codec;
        Codec codec2 = codec = Registries.DATA_COMPONENT_TYPE.getCodec().validate(componentType -> {
            if (componentType.shouldSkipSerialization()) {
                return DataResult.error(() -> "Component can't be serialized");
            }
            return DataResult.success((Object)componentType);
        });
        MapCodec mapCodec = codec2.dispatchMap("component", unbakedSwitch -> ((ComponentSelectProperty)unbakedSwitch.property()).componentType, componentType -> SelectProperty.Type.createCaseListCodec((Codec)componentType.getCodecOrThrow()).xmap(cases -> new SelectItemModel.UnbakedSwitch((SelectProperty)new ComponentSelectProperty(componentType), cases), SelectItemModel.UnbakedSwitch::cases));
        return new SelectProperty.Type(mapCodec);
    }

    public static <T> SelectProperty.Type<ComponentSelectProperty<T>, T> getTypeInstance() {
        return TYPE;
    }

    public @Nullable T getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ItemDisplayContext displayContext) {
        return (T)stack.get(this.componentType);
    }

    public SelectProperty.Type<ComponentSelectProperty<T>, T> getType() {
        return ComponentSelectProperty.getTypeInstance();
    }

    public Codec<T> valueCodec() {
        return this.componentType.getCodecOrThrow();
    }

    public ComponentType<T> componentType() {
        return this.componentType;
    }
}

