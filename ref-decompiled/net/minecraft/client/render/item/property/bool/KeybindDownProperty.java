/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.option.KeyBinding
 *  net.minecraft.client.render.item.property.bool.BooleanProperty
 *  net.minecraft.client.render.item.property.bool.KeybindDownProperty
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.property.bool;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record KeybindDownProperty(KeyBinding keybind) implements BooleanProperty
{
    private final KeyBinding keybind;
    private static final Codec<KeyBinding> KEY_BINDING_CODEC = Codec.STRING.comapFlatMap(id -> {
        KeyBinding keyBinding = KeyBinding.byId((String)id);
        return keyBinding != null ? DataResult.success((Object)keyBinding) : DataResult.error(() -> "Invalid keybind: " + id);
    }, KeyBinding::getId);
    public static final MapCodec<KeybindDownProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)KEY_BINDING_CODEC.fieldOf("keybind").forGetter(KeybindDownProperty::keybind)).apply((Applicative)instance, KeybindDownProperty::new));

    public KeybindDownProperty(KeyBinding keybind) {
        this.keybind = keybind;
    }

    public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        return this.keybind.isPressed();
    }

    public MapCodec<KeybindDownProperty> getCodec() {
        return CODEC;
    }

    public KeyBinding keybind() {
        return this.keybind;
    }
}

