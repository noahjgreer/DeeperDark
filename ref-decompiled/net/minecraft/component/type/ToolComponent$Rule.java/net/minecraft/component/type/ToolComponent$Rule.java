/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.component.type;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.dynamic.Codecs;

public static final class ToolComponent.Rule
extends Record {
    final RegistryEntryList<Block> blocks;
    final Optional<Float> speed;
    final Optional<Boolean> correctForDrops;
    public static final Codec<ToolComponent.Rule> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)RegistryCodecs.entryList(RegistryKeys.BLOCK).fieldOf("blocks").forGetter(ToolComponent.Rule::blocks), (App)Codecs.POSITIVE_FLOAT.optionalFieldOf("speed").forGetter(ToolComponent.Rule::speed), (App)Codec.BOOL.optionalFieldOf("correct_for_drops").forGetter(ToolComponent.Rule::correctForDrops)).apply((Applicative)instance, ToolComponent.Rule::new));
    public static final PacketCodec<RegistryByteBuf, ToolComponent.Rule> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.registryEntryList(RegistryKeys.BLOCK), ToolComponent.Rule::blocks, PacketCodecs.FLOAT.collect(PacketCodecs::optional), ToolComponent.Rule::speed, PacketCodecs.BOOLEAN.collect(PacketCodecs::optional), ToolComponent.Rule::correctForDrops, ToolComponent.Rule::new);

    public ToolComponent.Rule(RegistryEntryList<Block> blocks, Optional<Float> speed, Optional<Boolean> correctForDrops) {
        this.blocks = blocks;
        this.speed = speed;
        this.correctForDrops = correctForDrops;
    }

    public static ToolComponent.Rule ofAlwaysDropping(RegistryEntryList<Block> blocks, float speed) {
        return new ToolComponent.Rule(blocks, Optional.of(Float.valueOf(speed)), Optional.of(true));
    }

    public static ToolComponent.Rule ofNeverDropping(RegistryEntryList<Block> blocks) {
        return new ToolComponent.Rule(blocks, Optional.empty(), Optional.of(false));
    }

    public static ToolComponent.Rule of(RegistryEntryList<Block> blocks, float speed) {
        return new ToolComponent.Rule(blocks, Optional.of(Float.valueOf(speed)), Optional.empty());
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ToolComponent.Rule.class, "blocks;speed;correctForDrops", "blocks", "speed", "correctForDrops"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ToolComponent.Rule.class, "blocks;speed;correctForDrops", "blocks", "speed", "correctForDrops"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ToolComponent.Rule.class, "blocks;speed;correctForDrops", "blocks", "speed", "correctForDrops"}, this, object);
    }

    public RegistryEntryList<Block> blocks() {
        return this.blocks;
    }

    public Optional<Float> speed() {
        return this.speed;
    }

    public Optional<Boolean> correctForDrops() {
        return this.correctForDrops;
    }
}
