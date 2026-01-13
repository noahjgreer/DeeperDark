/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.PropertyMap
 */
package net.minecraft.network.packet.s2c.play;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import java.util.Objects;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.TextCodecs;
import net.minecraft.world.GameMode;

public static final class PlayerListS2CPacket.Action
extends Enum<PlayerListS2CPacket.Action> {
    public static final /* enum */ PlayerListS2CPacket.Action ADD_PLAYER = new PlayerListS2CPacket.Action((serialized, buf) -> {
        String string = (String)PacketCodecs.PLAYER_NAME.decode(buf);
        PropertyMap propertyMap = (PropertyMap)PacketCodecs.PROPERTY_MAP.decode(buf);
        serialized.gameProfile = new GameProfile(serialized.profileId, string, propertyMap);
    }, (buf, entry) -> {
        GameProfile gameProfile = Objects.requireNonNull(entry.profile());
        PacketCodecs.PLAYER_NAME.encode(buf, gameProfile.name());
        PacketCodecs.PROPERTY_MAP.encode(buf, gameProfile.properties());
    });
    public static final /* enum */ PlayerListS2CPacket.Action INITIALIZE_CHAT = new PlayerListS2CPacket.Action((serialized, buf) -> {
        serialized.session = buf.readNullable(PublicPlayerSession.Serialized::fromBuf);
    }, (buf, entry) -> buf.writeNullable(entry.chatSession, PublicPlayerSession.Serialized::write));
    public static final /* enum */ PlayerListS2CPacket.Action UPDATE_GAME_MODE = new PlayerListS2CPacket.Action((serialized, buf) -> {
        serialized.gameMode = GameMode.byIndex(buf.readVarInt());
    }, (buf, entry) -> buf.writeVarInt(entry.gameMode().getIndex()));
    public static final /* enum */ PlayerListS2CPacket.Action UPDATE_LISTED = new PlayerListS2CPacket.Action((serialized, buf) -> {
        serialized.listed = buf.readBoolean();
    }, (buf, entry) -> buf.writeBoolean(entry.listed()));
    public static final /* enum */ PlayerListS2CPacket.Action UPDATE_LATENCY = new PlayerListS2CPacket.Action((serialized, buf) -> {
        serialized.latency = buf.readVarInt();
    }, (buf, entry) -> buf.writeVarInt(entry.latency()));
    public static final /* enum */ PlayerListS2CPacket.Action UPDATE_DISPLAY_NAME = new PlayerListS2CPacket.Action((serialized, buf) -> {
        serialized.displayName = PacketByteBuf.readNullable(buf, TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC);
    }, (buf, entry) -> PacketByteBuf.writeNullable(buf, entry.displayName(), TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC));
    public static final /* enum */ PlayerListS2CPacket.Action UPDATE_LIST_ORDER = new PlayerListS2CPacket.Action((serialized, buf) -> {
        serialized.listOrder = buf.readVarInt();
    }, (buf, entry) -> buf.writeVarInt(entry.listOrder));
    public static final /* enum */ PlayerListS2CPacket.Action UPDATE_HAT = new PlayerListS2CPacket.Action((serialized, buf) -> {
        serialized.showHat = buf.readBoolean();
    }, (buf, entry) -> buf.writeBoolean(entry.showHat));
    final Reader reader;
    final Writer writer;
    private static final /* synthetic */ PlayerListS2CPacket.Action[] field_29141;

    public static PlayerListS2CPacket.Action[] values() {
        return (PlayerListS2CPacket.Action[])field_29141.clone();
    }

    public static PlayerListS2CPacket.Action valueOf(String string) {
        return Enum.valueOf(PlayerListS2CPacket.Action.class, string);
    }

    private PlayerListS2CPacket.Action(Reader reader, Writer writer) {
        this.reader = reader;
        this.writer = writer;
    }

    private static /* synthetic */ PlayerListS2CPacket.Action[] method_36951() {
        return new PlayerListS2CPacket.Action[]{ADD_PLAYER, INITIALIZE_CHAT, UPDATE_GAME_MODE, UPDATE_LISTED, UPDATE_LATENCY, UPDATE_DISPLAY_NAME, UPDATE_LIST_ORDER, UPDATE_HAT};
    }

    static {
        field_29141 = PlayerListS2CPacket.Action.method_36951();
    }

    public static interface Reader {
        public void read(PlayerListS2CPacket.Serialized var1, RegistryByteBuf var2);
    }

    public static interface Writer {
        public void write(RegistryByteBuf var1, PlayerListS2CPacket.Entry var2);
    }
}
