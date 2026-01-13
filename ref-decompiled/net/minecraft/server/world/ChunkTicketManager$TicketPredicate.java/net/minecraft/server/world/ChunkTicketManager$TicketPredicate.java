/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.world;

import net.minecraft.server.world.ChunkTicket;

public static interface ChunkTicketManager.TicketPredicate {
    public boolean test(ChunkTicket var1, long var2);
}
