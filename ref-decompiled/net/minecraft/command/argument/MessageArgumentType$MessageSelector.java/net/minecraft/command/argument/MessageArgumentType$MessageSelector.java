/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.EntitySelector;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public record MessageArgumentType.MessageSelector(int start, int end, EntitySelector selector) {
    public Text format(ServerCommandSource source) throws CommandSyntaxException {
        return EntitySelector.getNames(this.selector.getEntities(source));
    }
}
