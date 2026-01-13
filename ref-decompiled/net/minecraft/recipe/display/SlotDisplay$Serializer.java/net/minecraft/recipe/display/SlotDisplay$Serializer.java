/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.recipe.display;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.display.SlotDisplay;

public record SlotDisplay.Serializer<T extends SlotDisplay>(MapCodec<T> codec, PacketCodec<RegistryByteBuf, T> streamCodec) {
}
