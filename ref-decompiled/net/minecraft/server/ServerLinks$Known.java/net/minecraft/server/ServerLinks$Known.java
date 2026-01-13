/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.server;

import io.netty.buffer.ByteBuf;
import java.net.URI;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.Text;
import net.minecraft.util.function.ValueLists;

public static final class ServerLinks.Known
extends Enum<ServerLinks.Known> {
    public static final /* enum */ ServerLinks.Known BUG_REPORT = new ServerLinks.Known(0, "report_bug");
    public static final /* enum */ ServerLinks.Known COMMUNITY_GUIDELINES = new ServerLinks.Known(1, "community_guidelines");
    public static final /* enum */ ServerLinks.Known SUPPORT = new ServerLinks.Known(2, "support");
    public static final /* enum */ ServerLinks.Known STATUS = new ServerLinks.Known(3, "status");
    public static final /* enum */ ServerLinks.Known FEEDBACK = new ServerLinks.Known(4, "feedback");
    public static final /* enum */ ServerLinks.Known COMMUNITY = new ServerLinks.Known(5, "community");
    public static final /* enum */ ServerLinks.Known WEBSITE = new ServerLinks.Known(6, "website");
    public static final /* enum */ ServerLinks.Known FORUMS = new ServerLinks.Known(7, "forums");
    public static final /* enum */ ServerLinks.Known NEWS = new ServerLinks.Known(8, "news");
    public static final /* enum */ ServerLinks.Known ANNOUNCEMENTS = new ServerLinks.Known(9, "announcements");
    private static final IntFunction<ServerLinks.Known> FROM_ID;
    public static final PacketCodec<ByteBuf, ServerLinks.Known> CODEC;
    private final int id;
    private final String name;
    private static final /* synthetic */ ServerLinks.Known[] field_51986;

    public static ServerLinks.Known[] values() {
        return (ServerLinks.Known[])field_51986.clone();
    }

    public static ServerLinks.Known valueOf(String string) {
        return Enum.valueOf(ServerLinks.Known.class, string);
    }

    private ServerLinks.Known(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private Text getText() {
        return Text.translatable("known_server_link." + this.name);
    }

    public ServerLinks.Entry createEntry(URI link) {
        return ServerLinks.Entry.create(this, link);
    }

    private static /* synthetic */ ServerLinks.Known[] method_60669() {
        return new ServerLinks.Known[]{BUG_REPORT, COMMUNITY_GUIDELINES, SUPPORT, STATUS, FEEDBACK, COMMUNITY, WEBSITE, FORUMS, NEWS, ANNOUNCEMENTS};
    }

    static {
        field_51986 = ServerLinks.Known.method_60669();
        FROM_ID = ValueLists.createIndexToValueFunction(known -> known.id, ServerLinks.Known.values(), ValueLists.OutOfBoundsHandling.ZERO);
        CODEC = PacketCodecs.indexed(FROM_ID, known -> known.id);
    }
}
