/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.recipe.book;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public static final class RecipeBookOptions.CategoryOption
extends Record {
    final boolean guiOpen;
    final boolean filteringCraftable;
    public static final RecipeBookOptions.CategoryOption DEFAULT = new RecipeBookOptions.CategoryOption(false, false);
    public static final MapCodec<RecipeBookOptions.CategoryOption> CRAFTING = RecipeBookOptions.CategoryOption.createCodec("isGuiOpen", "isFilteringCraftable");
    public static final MapCodec<RecipeBookOptions.CategoryOption> FURNACE = RecipeBookOptions.CategoryOption.createCodec("isFurnaceGuiOpen", "isFurnaceFilteringCraftable");
    public static final MapCodec<RecipeBookOptions.CategoryOption> BLAST_FURNACE = RecipeBookOptions.CategoryOption.createCodec("isBlastingFurnaceGuiOpen", "isBlastingFurnaceFilteringCraftable");
    public static final MapCodec<RecipeBookOptions.CategoryOption> SMOKER = RecipeBookOptions.CategoryOption.createCodec("isSmokerGuiOpen", "isSmokerFilteringCraftable");
    public static final PacketCodec<ByteBuf, RecipeBookOptions.CategoryOption> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.BOOLEAN, RecipeBookOptions.CategoryOption::guiOpen, PacketCodecs.BOOLEAN, RecipeBookOptions.CategoryOption::filteringCraftable, RecipeBookOptions.CategoryOption::new);

    public RecipeBookOptions.CategoryOption(boolean guiOpen, boolean filteringCraftable) {
        this.guiOpen = guiOpen;
        this.filteringCraftable = filteringCraftable;
    }

    @Override
    public String toString() {
        return "[open=" + this.guiOpen + ", filtering=" + this.filteringCraftable + "]";
    }

    public RecipeBookOptions.CategoryOption withGuiOpen(boolean guiOpen) {
        return new RecipeBookOptions.CategoryOption(guiOpen, this.filteringCraftable);
    }

    public RecipeBookOptions.CategoryOption withFilteringCraftable(boolean filteringCraftable) {
        return new RecipeBookOptions.CategoryOption(this.guiOpen, filteringCraftable);
    }

    private static MapCodec<RecipeBookOptions.CategoryOption> createCodec(String guiOpenField, String filteringCraftableField) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.BOOL.optionalFieldOf(guiOpenField, (Object)false).forGetter(RecipeBookOptions.CategoryOption::guiOpen), (App)Codec.BOOL.optionalFieldOf(filteringCraftableField, (Object)false).forGetter(RecipeBookOptions.CategoryOption::filteringCraftable)).apply((Applicative)instance, RecipeBookOptions.CategoryOption::new));
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RecipeBookOptions.CategoryOption.class, "open;filtering", "guiOpen", "filteringCraftable"}, this);
    }

    @Override
    public final boolean equals(Object o) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RecipeBookOptions.CategoryOption.class, "open;filtering", "guiOpen", "filteringCraftable"}, this, o);
    }

    public boolean guiOpen() {
        return this.guiOpen;
    }

    public boolean filteringCraftable() {
        return this.filteringCraftable;
    }
}
