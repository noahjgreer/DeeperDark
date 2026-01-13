/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.recipe;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.TransmuteRecipe;
import net.minecraft.recipe.TransmuteRecipeResult;
import net.minecraft.recipe.book.CraftingRecipeCategory;

public static class TransmuteRecipe.Serializer
implements RecipeSerializer<TransmuteRecipe> {
    private static final MapCodec<TransmuteRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.STRING.optionalFieldOf("group", (Object)"").forGetter(recipe -> recipe.group), (App)CraftingRecipeCategory.CODEC.fieldOf("category").orElse((Object)CraftingRecipeCategory.MISC).forGetter(recipe -> recipe.category), (App)Ingredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.input), (App)Ingredient.CODEC.fieldOf("material").forGetter(recipe -> recipe.material), (App)TransmuteRecipeResult.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)).apply((Applicative)instance, TransmuteRecipe::new));
    public static final PacketCodec<RegistryByteBuf, TransmuteRecipe> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.STRING, recipe -> recipe.group, CraftingRecipeCategory.PACKET_CODEC, recipe -> recipe.category, Ingredient.PACKET_CODEC, recipe -> recipe.input, Ingredient.PACKET_CODEC, recipe -> recipe.material, TransmuteRecipeResult.PACKET_CODEC, recipe -> recipe.result, TransmuteRecipe::new);

    @Override
    public MapCodec<TransmuteRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, TransmuteRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
