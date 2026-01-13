/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.item;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public record ItemAsset.Properties(boolean handAnimationOnSwap, boolean oversizedInGui, float swapAnimationScale) {
    public static final ItemAsset.Properties DEFAULT = new ItemAsset.Properties(true, false, 1.0f);
    public static final MapCodec<ItemAsset.Properties> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.BOOL.optionalFieldOf("hand_animation_on_swap", (Object)true).forGetter(ItemAsset.Properties::handAnimationOnSwap), (App)Codec.BOOL.optionalFieldOf("oversized_in_gui", (Object)false).forGetter(ItemAsset.Properties::oversizedInGui), (App)Codec.FLOAT.optionalFieldOf("swap_animation_scale", (Object)Float.valueOf(1.0f)).forGetter(ItemAsset.Properties::swapAnimationScale)).apply((Applicative)instance, ItemAsset.Properties::new));
}
