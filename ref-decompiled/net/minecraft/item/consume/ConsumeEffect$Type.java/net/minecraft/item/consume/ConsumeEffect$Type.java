/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.item.consume;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.consume.ClearAllEffectsConsumeEffect;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.item.consume.PlaySoundConsumeEffect;
import net.minecraft.item.consume.RemoveEffectsConsumeEffect;
import net.minecraft.item.consume.TeleportRandomlyConsumeEffect;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public record ConsumeEffect.Type<T extends ConsumeEffect>(MapCodec<T> codec, PacketCodec<RegistryByteBuf, T> streamCodec) {
    public static final ConsumeEffect.Type<ApplyEffectsConsumeEffect> APPLY_EFFECTS = ConsumeEffect.Type.register("apply_effects", ApplyEffectsConsumeEffect.CODEC, ApplyEffectsConsumeEffect.PACKET_CODEC);
    public static final ConsumeEffect.Type<RemoveEffectsConsumeEffect> REMOVE_EFFECTS = ConsumeEffect.Type.register("remove_effects", RemoveEffectsConsumeEffect.CODEC, RemoveEffectsConsumeEffect.PACKET_CODEC);
    public static final ConsumeEffect.Type<ClearAllEffectsConsumeEffect> CLEAR_ALL_EFFECTS = ConsumeEffect.Type.register("clear_all_effects", ClearAllEffectsConsumeEffect.CODEC, ClearAllEffectsConsumeEffect.PACKET_CODEC);
    public static final ConsumeEffect.Type<TeleportRandomlyConsumeEffect> TELEPORT_RANDOMLY = ConsumeEffect.Type.register("teleport_randomly", TeleportRandomlyConsumeEffect.CODEC, TeleportRandomlyConsumeEffect.PACKET_CODEC);
    public static final ConsumeEffect.Type<PlaySoundConsumeEffect> PLAY_SOUND = ConsumeEffect.Type.register("play_sound", PlaySoundConsumeEffect.CODEC, PlaySoundConsumeEffect.PACKET_CODEC);

    private static <T extends ConsumeEffect> ConsumeEffect.Type<T> register(String id, MapCodec<T> codec, PacketCodec<RegistryByteBuf, T> packetCodec) {
        return Registry.register(Registries.CONSUME_EFFECT_TYPE, id, new ConsumeEffect.Type<T>(codec, packetCodec));
    }
}
