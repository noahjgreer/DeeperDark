/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.scoreboard;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public static final class AbstractTeam.CollisionRule
extends Enum<AbstractTeam.CollisionRule>
implements StringIdentifiable {
    public static final /* enum */ AbstractTeam.CollisionRule ALWAYS = new AbstractTeam.CollisionRule("always", 0);
    public static final /* enum */ AbstractTeam.CollisionRule NEVER = new AbstractTeam.CollisionRule("never", 1);
    public static final /* enum */ AbstractTeam.CollisionRule PUSH_OTHER_TEAMS = new AbstractTeam.CollisionRule("pushOtherTeams", 2);
    public static final /* enum */ AbstractTeam.CollisionRule PUSH_OWN_TEAM = new AbstractTeam.CollisionRule("pushOwnTeam", 3);
    public static final Codec<AbstractTeam.CollisionRule> CODEC;
    private static final IntFunction<AbstractTeam.CollisionRule> INDEX_MAPPER;
    public static final PacketCodec<ByteBuf, AbstractTeam.CollisionRule> PACKET_CODEC;
    public final String name;
    public final int index;
    private static final /* synthetic */ AbstractTeam.CollisionRule[] field_1439;

    public static AbstractTeam.CollisionRule[] values() {
        return (AbstractTeam.CollisionRule[])field_1439.clone();
    }

    public static AbstractTeam.CollisionRule valueOf(String string) {
        return Enum.valueOf(AbstractTeam.CollisionRule.class, string);
    }

    private AbstractTeam.CollisionRule(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public Text getDisplayName() {
        return Text.translatable("team.collision." + this.name);
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static /* synthetic */ AbstractTeam.CollisionRule[] method_36797() {
        return new AbstractTeam.CollisionRule[]{ALWAYS, NEVER, PUSH_OTHER_TEAMS, PUSH_OWN_TEAM};
    }

    static {
        field_1439 = AbstractTeam.CollisionRule.method_36797();
        CODEC = StringIdentifiable.createCodec(AbstractTeam.CollisionRule::values);
        INDEX_MAPPER = ValueLists.createIndexToValueFunction(collisionRule -> collisionRule.index, AbstractTeam.CollisionRule.values(), ValueLists.OutOfBoundsHandling.ZERO);
        PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, collisionRule -> collisionRule.index);
    }
}
