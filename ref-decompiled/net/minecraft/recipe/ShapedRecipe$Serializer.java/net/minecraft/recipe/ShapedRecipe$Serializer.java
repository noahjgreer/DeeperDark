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
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;

public static class ShapedRecipe.Serializer
implements RecipeSerializer<ShapedRecipe> {
    public static final MapCodec<ShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.STRING.optionalFieldOf("group", (Object)"").forGetter(recipe -> recipe.group), (App)CraftingRecipeCategory.CODEC.fieldOf("category").orElse((Object)CraftingRecipeCategory.MISC).forGetter(recipe -> recipe.category), (App)RawShapedRecipe.CODEC.forGetter(recipe -> recipe.raw), (App)ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result), (App)Codec.BOOL.optionalFieldOf("show_notification", (Object)true).forGetter(recipe -> recipe.showNotification)).apply((Applicative)instance, ShapedRecipe::new));
    public static final PacketCodec<RegistryByteBuf, ShapedRecipe> PACKET_CODEC = PacketCodec.ofStatic(ShapedRecipe.Serializer::write, ShapedRecipe.Serializer::read);

    @Override
    public MapCodec<ShapedRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, ShapedRecipe> packetCodec() {
        return PACKET_CODEC;
    }

    private static ShapedRecipe read(RegistryByteBuf buf) {
        String string = buf.readString();
        CraftingRecipeCategory craftingRecipeCategory = buf.readEnumConstant(CraftingRecipeCategory.class);
        RawShapedRecipe rawShapedRecipe = (RawShapedRecipe)RawShapedRecipe.PACKET_CODEC.decode(buf);
        ItemStack itemStack = (ItemStack)ItemStack.PACKET_CODEC.decode(buf);
        boolean bl = buf.readBoolean();
        return new ShapedRecipe(string, craftingRecipeCategory, rawShapedRecipe, itemStack, bl);
    }

    private static void write(RegistryByteBuf buf, ShapedRecipe recipe) {
        buf.writeString(recipe.group);
        buf.writeEnumConstant(recipe.category);
        RawShapedRecipe.PACKET_CODEC.encode(buf, recipe.raw);
        ItemStack.PACKET_CODEC.encode(buf, recipe.result);
        buf.writeBoolean(recipe.showNotification);
    }
}
