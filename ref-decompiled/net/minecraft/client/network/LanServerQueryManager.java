/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.network.LanServerQueryManager
 *  org.slf4j.Logger
 */
package net.minecraft.client.network;

import com.mojang.logging.LogUtils;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class LanServerQueryManager {
    static final AtomicInteger THREAD_ID = new AtomicInteger(0);
    static final Logger LOGGER = LogUtils.getLogger();
}

