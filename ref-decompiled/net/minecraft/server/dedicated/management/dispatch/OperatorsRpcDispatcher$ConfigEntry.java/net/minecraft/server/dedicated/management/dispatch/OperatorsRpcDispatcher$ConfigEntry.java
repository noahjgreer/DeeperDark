/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.dedicated.management.dispatch;

import java.util.Optional;
import net.minecraft.command.permission.PermissionLevel;
import net.minecraft.server.PlayerConfigEntry;

record OperatorsRpcDispatcher.ConfigEntry(PlayerConfigEntry user, Optional<PermissionLevel> permissionLevel, Optional<Boolean> bypassesPlayerLimit) {
}
