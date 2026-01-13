/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.command.argument;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import net.minecraft.command.DefaultPermissions;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public static final class MessageArgumentType.MessageFormat
extends Record {
    final String contents;
    private final MessageArgumentType.MessageSelector[] selectors;

    public MessageArgumentType.MessageFormat(String contents, MessageArgumentType.MessageSelector[] selectors) {
        this.contents = contents;
        this.selectors = selectors;
    }

    Text format(ServerCommandSource source) throws CommandSyntaxException {
        return this.format(source, source.getPermissions().hasPermission(DefaultPermissions.ENTITY_SELECTORS));
    }

    public Text format(ServerCommandSource source, boolean canUseSelectors) throws CommandSyntaxException {
        if (this.selectors.length == 0 || !canUseSelectors) {
            return Text.literal(this.contents);
        }
        MutableText mutableText = Text.literal(this.contents.substring(0, this.selectors[0].start()));
        int i = this.selectors[0].start();
        for (MessageArgumentType.MessageSelector messageSelector : this.selectors) {
            Text text = messageSelector.format(source);
            if (i < messageSelector.start()) {
                mutableText.append(this.contents.substring(i, messageSelector.start()));
            }
            mutableText.append(text);
            i = messageSelector.end();
        }
        if (i < this.contents.length()) {
            mutableText.append(this.contents.substring(i));
        }
        return mutableText;
    }

    public static MessageArgumentType.MessageFormat parse(StringReader reader, boolean allowAtSelectors) throws CommandSyntaxException {
        if (reader.getRemainingLength() > 256) {
            throw MESSAGE_TOO_LONG_EXCEPTION.create((Object)reader.getRemainingLength(), (Object)256);
        }
        String string = reader.getRemaining();
        if (!allowAtSelectors) {
            reader.setCursor(reader.getTotalLength());
            return new MessageArgumentType.MessageFormat(string, new MessageArgumentType.MessageSelector[0]);
        }
        ArrayList list = Lists.newArrayList();
        int i = reader.getCursor();
        while (reader.canRead()) {
            if (reader.peek() == '@') {
                EntitySelector entitySelector;
                int j = reader.getCursor();
                try {
                    EntitySelectorReader entitySelectorReader = new EntitySelectorReader(reader, true);
                    entitySelector = entitySelectorReader.read();
                }
                catch (CommandSyntaxException commandSyntaxException) {
                    if (commandSyntaxException.getType() == EntitySelectorReader.MISSING_EXCEPTION || commandSyntaxException.getType() == EntitySelectorReader.UNKNOWN_SELECTOR_EXCEPTION) {
                        reader.setCursor(j + 1);
                        continue;
                    }
                    throw commandSyntaxException;
                }
                list.add(new MessageArgumentType.MessageSelector(j - i, reader.getCursor() - i, entitySelector));
                continue;
            }
            reader.skip();
        }
        return new MessageArgumentType.MessageFormat(string, list.toArray(new MessageArgumentType.MessageSelector[0]));
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MessageArgumentType.MessageFormat.class, "text;parts", "contents", "selectors"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MessageArgumentType.MessageFormat.class, "text;parts", "contents", "selectors"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MessageArgumentType.MessageFormat.class, "text;parts", "contents", "selectors"}, this, object);
    }

    public String contents() {
        return this.contents;
    }

    public MessageArgumentType.MessageSelector[] selectors() {
        return this.selectors;
    }
}
