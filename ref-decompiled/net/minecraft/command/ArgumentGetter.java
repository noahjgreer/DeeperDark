package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

@FunctionalInterface
public interface ArgumentGetter {
   Object apply(Object context) throws CommandSyntaxException;
}
