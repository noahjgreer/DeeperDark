/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.server;

import java.util.UUID;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

public record MinecraftServer.ServerResourcePackProperties(UUID id, String url, String hash, boolean isRequired, @Nullable Text prompt) {
}
