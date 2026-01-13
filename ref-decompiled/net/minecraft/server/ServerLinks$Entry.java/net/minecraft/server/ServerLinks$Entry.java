/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 */
package net.minecraft.server;

import com.mojang.datafixers.util.Either;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.net.URI;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.Text;

public static final class ServerLinks.Entry
extends Record {
    final Either<ServerLinks.Known, Text> type;
    final URI link;

    public ServerLinks.Entry(Either<ServerLinks.Known, Text> type, URI link) {
        this.type = type;
        this.link = link;
    }

    public static ServerLinks.Entry create(ServerLinks.Known known, URI link) {
        return new ServerLinks.Entry((Either<ServerLinks.Known, Text>)Either.left((Object)((Object)known)), link);
    }

    public static ServerLinks.Entry create(Text name, URI link) {
        return new ServerLinks.Entry((Either<ServerLinks.Known, Text>)Either.right((Object)name), link);
    }

    public Text getText() {
        return (Text)this.type.map(ServerLinks.Known::getText, text -> text);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ServerLinks.Entry.class, "type;link", "type", "link"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ServerLinks.Entry.class, "type;link", "type", "link"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ServerLinks.Entry.class, "type;link", "type", "link"}, this, object);
    }

    public Either<ServerLinks.Known, Text> type() {
        return this.type;
    }

    public URI link() {
        return this.link;
    }
}
