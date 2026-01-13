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
import java.util.Optional;
import net.minecraft.block.entity.TestInstanceBlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.test.TestInstance;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Vec3i;

public record TestInstanceBlockEntity.Data(Optional<RegistryKey<TestInstance>> test, Vec3i size, BlockRotation rotation, boolean ignoreEntities, TestInstanceBlockEntity.Status status, Optional<Text> errorMessage) {
    public static final Codec<TestInstanceBlockEntity.Data> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)RegistryKey.createCodec(RegistryKeys.TEST_INSTANCE).optionalFieldOf("test").forGetter(TestInstanceBlockEntity.Data::test), (App)Vec3i.CODEC.fieldOf("size").forGetter(TestInstanceBlockEntity.Data::size), (App)BlockRotation.CODEC.fieldOf("rotation").forGetter(TestInstanceBlockEntity.Data::rotation), (App)Codec.BOOL.fieldOf("ignore_entities").forGetter(TestInstanceBlockEntity.Data::ignoreEntities), (App)TestInstanceBlockEntity.Status.CODEC.fieldOf("status").forGetter(TestInstanceBlockEntity.Data::status), (App)TextCodecs.CODEC.optionalFieldOf("error_message").forGetter(TestInstanceBlockEntity.Data::errorMessage)).apply((Applicative)instance, TestInstanceBlockEntity.Data::new));
    public static final PacketCodec<RegistryByteBuf, TestInstanceBlockEntity.Data> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.optional(RegistryKey.createPacketCodec(RegistryKeys.TEST_INSTANCE)), TestInstanceBlockEntity.Data::test, Vec3i.PACKET_CODEC, TestInstanceBlockEntity.Data::size, BlockRotation.PACKET_CODEC, TestInstanceBlockEntity.Data::rotation, PacketCodecs.BOOLEAN, TestInstanceBlockEntity.Data::ignoreEntities, TestInstanceBlockEntity.Status.PACKET_CODEC, TestInstanceBlockEntity.Data::status, PacketCodecs.optional(TextCodecs.REGISTRY_PACKET_CODEC), TestInstanceBlockEntity.Data::errorMessage, TestInstanceBlockEntity.Data::new);

    public TestInstanceBlockEntity.Data withSize(Vec3i size) {
        return new TestInstanceBlockEntity.Data(this.test, size, this.rotation, this.ignoreEntities, this.status, this.errorMessage);
    }

    public TestInstanceBlockEntity.Data withStatus(TestInstanceBlockEntity.Status status) {
        return new TestInstanceBlockEntity.Data(this.test, this.size, this.rotation, this.ignoreEntities, status, Optional.empty());
    }

    public TestInstanceBlockEntity.Data withErrorMessage(Text errorMessage) {
        return new TestInstanceBlockEntity.Data(this.test, this.size, this.rotation, this.ignoreEntities, TestInstanceBlockEntity.Status.FINISHED, Optional.of(errorMessage));
    }
}
