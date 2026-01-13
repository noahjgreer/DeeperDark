/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.server.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import net.minecraft.resource.ResourcePackProfile;

static interface DatapackCommand.PackAdder {
    public void apply(List<ResourcePackProfile> var1, ResourcePackProfile var2) throws CommandSyntaxException;
}
