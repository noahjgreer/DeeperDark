/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Formatting;

public static class TeamS2CPacket.SerializableTeam {
    private final Text displayName;
    private final Text prefix;
    private final Text suffix;
    private final AbstractTeam.VisibilityRule nameTagVisibilityRule;
    private final AbstractTeam.CollisionRule collisionRule;
    private final Formatting color;
    private final int friendlyFlags;

    public TeamS2CPacket.SerializableTeam(Team team) {
        this.displayName = team.getDisplayName();
        this.friendlyFlags = team.getFriendlyFlagsBitwise();
        this.nameTagVisibilityRule = team.getNameTagVisibilityRule();
        this.collisionRule = team.getCollisionRule();
        this.color = team.getColor();
        this.prefix = team.getPrefix();
        this.suffix = team.getSuffix();
    }

    public TeamS2CPacket.SerializableTeam(RegistryByteBuf buf) {
        this.displayName = (Text)TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.decode(buf);
        this.friendlyFlags = buf.readByte();
        this.nameTagVisibilityRule = (AbstractTeam.VisibilityRule)AbstractTeam.VisibilityRule.PACKET_CODEC.decode(buf);
        this.collisionRule = (AbstractTeam.CollisionRule)AbstractTeam.CollisionRule.PACKET_CODEC.decode(buf);
        this.color = buf.readEnumConstant(Formatting.class);
        this.prefix = (Text)TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.decode(buf);
        this.suffix = (Text)TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.decode(buf);
    }

    public Text getDisplayName() {
        return this.displayName;
    }

    public int getFriendlyFlagsBitwise() {
        return this.friendlyFlags;
    }

    public Formatting getColor() {
        return this.color;
    }

    public AbstractTeam.VisibilityRule getNameTagVisibilityRule() {
        return this.nameTagVisibilityRule;
    }

    public AbstractTeam.CollisionRule getCollisionRule() {
        return this.collisionRule;
    }

    public Text getPrefix() {
        return this.prefix;
    }

    public Text getSuffix() {
        return this.suffix;
    }

    public void write(RegistryByteBuf buf) {
        TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.encode(buf, this.displayName);
        buf.writeByte(this.friendlyFlags);
        AbstractTeam.VisibilityRule.PACKET_CODEC.encode(buf, this.nameTagVisibilityRule);
        AbstractTeam.CollisionRule.PACKET_CODEC.encode(buf, this.collisionRule);
        buf.writeEnumConstant(this.color);
        TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.encode(buf, this.prefix);
        TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.encode(buf, this.suffix);
    }
}
