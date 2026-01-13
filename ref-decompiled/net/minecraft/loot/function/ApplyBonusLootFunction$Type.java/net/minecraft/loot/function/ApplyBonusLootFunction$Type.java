/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.util.Identifier;

record ApplyBonusLootFunction.Type(Identifier id, Codec<? extends ApplyBonusLootFunction.Formula> codec) {
}
