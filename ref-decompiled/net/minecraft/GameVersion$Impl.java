/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft;

import java.util.Date;
import net.minecraft.GameVersion;
import net.minecraft.SaveVersion;
import net.minecraft.resource.PackVersion;
import net.minecraft.resource.ResourceType;

public record GameVersion.Impl(String id, String name, SaveVersion dataVersion, int protocolVersion, PackVersion resourcePackVersion, PackVersion datapackVersion, Date buildTime, boolean stable) implements GameVersion
{
    @Override
    public PackVersion packVersion(ResourceType type) {
        return switch (type) {
            default -> throw new MatchException(null, null);
            case ResourceType.CLIENT_RESOURCES -> this.resourcePackVersion;
            case ResourceType.SERVER_DATA -> this.datapackVersion;
        };
    }
}
