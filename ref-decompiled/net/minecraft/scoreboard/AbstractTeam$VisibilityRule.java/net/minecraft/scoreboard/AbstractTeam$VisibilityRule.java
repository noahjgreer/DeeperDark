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

public static final class AbstractTeam.VisibilityRule
extends Enum<AbstractTeam.VisibilityRule>
implements StringIdentifiable {
    public static final /* enum */ AbstractTeam.VisibilityRule ALWAYS = new AbstractTeam.VisibilityRule("always", 0);
    public static final /* enum */ AbstractTeam.VisibilityRule NEVER = new AbstractTeam.VisibilityRule("never", 1);
    public static final /* enum */ AbstractTeam.VisibilityRule HIDE_FOR_OTHER_TEAMS = new AbstractTeam.VisibilityRule("hideForOtherTeams", 2);
    public static final /* enum */ AbstractTeam.VisibilityRule HIDE_FOR_OWN_TEAM = new AbstractTeam.VisibilityRule("hideForOwnTeam", 3);
    public static final Codec<AbstractTeam.VisibilityRule> CODEC;
    private static final IntFunction<AbstractTeam.VisibilityRule> INDEX_MAPPER;
    public static final PacketCodec<ByteBuf, AbstractTeam.VisibilityRule> PACKET_CODEC;
    public final String name;
    public final int index;
    private static final /* synthetic */ AbstractTeam.VisibilityRule[] field_1448;

    public static AbstractTeam.VisibilityRule[] values() {
        return (AbstractTeam.VisibilityRule[])field_1448.clone();
    }

    public static AbstractTeam.VisibilityRule valueOf(String string) {
        return Enum.valueOf(AbstractTeam.VisibilityRule.class, string);
    }

    private AbstractTeam.VisibilityRule(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public Text getDisplayName() {
        return Text.translatable("team.visibility." + this.name);
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static /* synthetic */ AbstractTeam.VisibilityRule[] method_36798() {
        return new AbstractTeam.VisibilityRule[]{ALWAYS, NEVER, HIDE_FOR_OTHER_TEAMS, HIDE_FOR_OWN_TEAM};
    }

    static {
        field_1448 = AbstractTeam.VisibilityRule.method_36798();
        CODEC = StringIdentifiable.createCodec(AbstractTeam.VisibilityRule::values);
        INDEX_MAPPER = ValueLists.createIndexToValueFunction(visibilityRule -> visibilityRule.index, AbstractTeam.VisibilityRule.values(), ValueLists.OutOfBoundsHandling.ZERO);
        PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, visibilityRule -> visibilityRule.index);
    }
}
