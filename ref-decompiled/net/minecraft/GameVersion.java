/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.GameVersion
 *  net.minecraft.SaveVersion
 *  net.minecraft.resource.PackVersion
 *  net.minecraft.resource.ResourceType
 */
package net.minecraft;

import java.util.Date;
import net.minecraft.SaveVersion;
import net.minecraft.resource.PackVersion;
import net.minecraft.resource.ResourceType;

public interface GameVersion {
    public SaveVersion dataVersion();

    public String id();

    public String name();

    public int protocolVersion();

    public PackVersion packVersion(ResourceType var1);

    public Date buildTime();

    public boolean stable();
}

