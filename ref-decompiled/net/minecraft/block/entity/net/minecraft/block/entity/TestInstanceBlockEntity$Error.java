/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.block.entity;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.math.BlockPos;

public record TestInstanceBlockEntity.Error(BlockPos pos, Text text) {
    public static final Codec<TestInstanceBlockEntity.Error> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockPos.CODEC.fieldOf("pos").forGetter(TestInstanceBlockEntity.Error::pos), (App)TextCodecs.CODEC.fieldOf("text").forGetter(TestInstanceBlockEntity.Error::text)).apply((Applicative)instance, TestInstanceBlockEntity.Error::new));
    public static final Codec<List<TestInstanceBlockEntity.Error>> LIST_CODEC = CODEC.listOf();
}
