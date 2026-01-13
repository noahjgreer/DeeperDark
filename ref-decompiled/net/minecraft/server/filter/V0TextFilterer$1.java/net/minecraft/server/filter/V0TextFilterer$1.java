/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.server.filter;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.filter.AbstractTextFilterer;

class V0TextFilterer.1
extends AbstractTextFilterer.StreamImpl {
    V0TextFilterer.1(GameProfile gameProfile) {
        super(V0TextFilterer.this, gameProfile);
    }

    @Override
    public void onConnect() {
        V0TextFilterer.this.sendJoinOrLeaveRequest(this.gameProfile, V0TextFilterer.this.joinEndpoint, V0TextFilterer.this.joinEncoder, this.executor);
    }

    @Override
    public void onDisconnect() {
        V0TextFilterer.this.sendJoinOrLeaveRequest(this.gameProfile, V0TextFilterer.this.leaveEndpoint, V0TextFilterer.this.leaveEncoder, this.executor);
    }
}
