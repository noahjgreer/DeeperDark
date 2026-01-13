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
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingTrimRecipe;

public static class SmithingTrimRecipe.Serializer
implements RecipeSerializer<SmithingTrimRecipe> {
    private static final MapCodec<SmithingTrimRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Ingredient.CODEC.fieldOf("template").forGetter(recipe -> recipe.template), (App)Ingredient.CODEC.fieldOf("base").forGetter(recipe -> recipe.base), (App)Ingredient.CODEC.fieldOf("addition").forGetter(recipe -> recipe.addition), (App)ArmorTrimPattern.ENTRY_CODEC.fieldOf("pattern").forGetter(recipe -> recipe.pattern)).apply((Applicative)instance, SmithingTrimRecipe::new));
    public static final PacketCodec<RegistryByteBuf, SmithingTrimRecipe> PACKET_CODEC = PacketCodec.tuple(Ingredient.PACKET_CODEC, recipe -> recipe.template, Ingredient.PACKET_CODEC, recipe -> recipe.base, Ingredient.PACKET_CODEC, recipe -> recipe.addition, ArmorTrimPattern.ENTRY_PACKET_CODEC, recipe -> recipe.pattern, SmithingTrimRecipe::new);

    @Override
    public MapCodec<SmithingTrimRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, SmithingTrimRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
