/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.network.message;

import java.util.Map;
import net.minecraft.network.message.SignedCommandArguments;
import net.minecraft.network.message.SignedMessage;
import org.jspecify.annotations.Nullable;

public record SignedCommandArguments.Impl(Map<String, SignedMessage> arguments) implements SignedCommandArguments
{
    @Override
    public @Nullable SignedMessage getMessage(String argumentName) {
        return this.arguments.get(argumentName);
    }
}
