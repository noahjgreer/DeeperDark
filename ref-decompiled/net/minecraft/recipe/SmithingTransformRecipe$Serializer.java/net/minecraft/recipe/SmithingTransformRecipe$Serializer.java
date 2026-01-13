/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.recipe;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingTransformRecipe;
import net.minecraft.recipe.TransmuteRecipeResult;

public static class SmithingTransformRecipe.Serializer
implements RecipeSerializer<SmithingTransformRecipe> {
    private static final MapCodec<SmithingTransformRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Ingredient.CODEC.optionalFieldOf("template").forGetter(recipe -> recipe.template), (App)Ingredient.CODEC.fieldOf("base").forGetter(recipe -> recipe.base), (App)Ingredient.CODEC.optionalFieldOf("addition").forGetter(recipe -> recipe.addition), (App)TransmuteRecipeResult.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)).apply((Applicative)instance, SmithingTransformRecipe::new));
    public static final PacketCodec<RegistryByteBuf, SmithingTransformRecipe> PACKET_CODEC = PacketCodec.tuple(Ingredient.OPTIONAL_PACKET_CODEC, recipe -> recipe.template, Ingredient.PACKET_CODEC, recipe -> recipe.base, Ingredient.OPTIONAL_PACKET_CODEC, recipe -> recipe.addition, TransmuteRecipeResult.PACKET_CODEC, recipe -> recipe.result, SmithingTransformRecipe::new);

    @Override
    public MapCodec<SmithingTransformRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, SmithingTransformRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
