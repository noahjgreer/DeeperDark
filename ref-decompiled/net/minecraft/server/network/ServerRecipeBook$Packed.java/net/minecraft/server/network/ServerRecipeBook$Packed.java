/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.server.network;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBookOptions;
import net.minecraft.registry.RegistryKey;

public static final class ServerRecipeBook.Packed
extends Record {
    final RecipeBookOptions settings;
    final List<RegistryKey<Recipe<?>>> known;
    final List<RegistryKey<Recipe<?>>> highlight;
    public static final Codec<ServerRecipeBook.Packed> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)RecipeBookOptions.CODEC.forGetter(ServerRecipeBook.Packed::settings), (App)Recipe.KEY_CODEC.listOf().fieldOf("recipes").forGetter(ServerRecipeBook.Packed::known), (App)Recipe.KEY_CODEC.listOf().fieldOf("toBeDisplayed").forGetter(ServerRecipeBook.Packed::highlight)).apply((Applicative)instance, ServerRecipeBook.Packed::new));

    public ServerRecipeBook.Packed(RecipeBookOptions settings, List<RegistryKey<Recipe<?>>> known, List<RegistryKey<Recipe<?>>> highlight) {
        this.settings = settings;
        this.known = known;
        this.highlight = highlight;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ServerRecipeBook.Packed.class, "settings;known;highlight", "settings", "known", "highlight"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ServerRecipeBook.Packed.class, "settings;known;highlight", "settings", "known", "highlight"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ServerRecipeBook.Packed.class, "settings;known;highlight", "settings", "known", "highlight"}, this, object);
    }

    public RecipeBookOptions settings() {
        return this.settings;
    }

    public List<RegistryKey<Recipe<?>>> known() {
        return this.known;
    }

    public List<RegistryKey<Recipe<?>>> highlight() {
        return this.highlight;
    }
}
