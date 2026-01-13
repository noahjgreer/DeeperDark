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
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SingleStackRecipe;
import net.minecraft.recipe.book.CookingRecipeCategory;

public static class AbstractCookingRecipe.Serializer<T extends AbstractCookingRecipe>
implements RecipeSerializer<T> {
    private final MapCodec<T> codec = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.STRING.optionalFieldOf("group", (Object)"").forGetter(SingleStackRecipe::getGroup), (App)CookingRecipeCategory.CODEC.fieldOf("category").orElse((Object)CookingRecipeCategory.MISC).forGetter(AbstractCookingRecipe::getCategory), (App)Ingredient.CODEC.fieldOf("ingredient").forGetter(SingleStackRecipe::ingredient), (App)ItemStack.VALIDATED_UNCOUNTED_CODEC.fieldOf("result").forGetter(SingleStackRecipe::result), (App)Codec.FLOAT.fieldOf("experience").orElse((Object)Float.valueOf(0.0f)).forGetter(AbstractCookingRecipe::getExperience), (App)Codec.INT.fieldOf("cookingtime").orElse((Object)defaultCookingTime).forGetter(AbstractCookingRecipe::getCookingTime)).apply((Applicative)instance, factory::create));
    private final PacketCodec<RegistryByteBuf, T> packetCodec = PacketCodec.tuple(PacketCodecs.STRING, SingleStackRecipe::getGroup, CookingRecipeCategory.PACKET_CODEC, AbstractCookingRecipe::getCategory, Ingredient.PACKET_CODEC, SingleStackRecipe::ingredient, ItemStack.PACKET_CODEC, SingleStackRecipe::result, PacketCodecs.FLOAT, AbstractCookingRecipe::getExperience, PacketCodecs.INTEGER, AbstractCookingRecipe::getCookingTime, factory::create);

    public AbstractCookingRecipe.Serializer(AbstractCookingRecipe.RecipeFactory<T> factory, int defaultCookingTime) {
    }

    @Override
    public MapCodec<T> codec() {
        return this.codec;
    }

    @Override
    public PacketCodec<RegistryByteBuf, T> packetCodec() {
        return this.packetCodec;
    }
}
