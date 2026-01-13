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
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;

public static class ShapelessRecipe.Serializer
implements RecipeSerializer<ShapelessRecipe> {
    private static final MapCodec<ShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.STRING.optionalFieldOf("group", (Object)"").forGetter(recipe -> recipe.group), (App)CraftingRecipeCategory.CODEC.fieldOf("category").orElse((Object)CraftingRecipeCategory.MISC).forGetter(recipe -> recipe.category), (App)ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result), (App)Ingredient.CODEC.listOf(1, 9).fieldOf("ingredients").forGetter(recipe -> recipe.ingredients)).apply((Applicative)instance, ShapelessRecipe::new));
    public static final PacketCodec<RegistryByteBuf, ShapelessRecipe> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.STRING, recipe -> recipe.group, CraftingRecipeCategory.PACKET_CODEC, recipe -> recipe.category, ItemStack.PACKET_CODEC, recipe -> recipe.result, Ingredient.PACKET_CODEC.collect(PacketCodecs.toList()), recipe -> recipe.ingredients, ShapelessRecipe::new);

    @Override
    public MapCodec<ShapelessRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, ShapelessRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
