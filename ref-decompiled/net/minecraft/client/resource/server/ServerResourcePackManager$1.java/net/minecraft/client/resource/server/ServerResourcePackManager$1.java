/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.server;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.server.PackStateChangeCallback;
import net.minecraft.client.resource.server.ReloadScheduler;
import net.minecraft.client.resource.server.ServerResourcePackManager;

@Environment(value=EnvType.CLIENT)
class ServerResourcePackManager.1
implements ReloadScheduler.ReloadContext {
    final /* synthetic */ List field_47635;
    final /* synthetic */ List field_47636;

    ServerResourcePackManager.1() {
        this.field_47635 = list;
        this.field_47636 = list2;
    }

    @Override
    public void onSuccess() {
        for (ServerResourcePackManager.PackEntry packEntry : this.field_47635) {
            packEntry.status = ServerResourcePackManager.Status.ACTIVE;
            if (packEntry.discardReason != null) continue;
            ServerResourcePackManager.this.stateChangeCallback.onFinish(packEntry.id, PackStateChangeCallback.FinishState.APPLIED);
        }
        for (ServerResourcePackManager.PackEntry packEntry : this.field_47636) {
            packEntry.status = ServerResourcePackManager.Status.INACTIVE;
        }
        ServerResourcePackManager.this.onPackChanged();
    }

    @Override
    public void onFailure(boolean force) {
        if (!force) {
            this.field_47635.clear();
            for (ServerResourcePackManager.PackEntry packEntry : ServerResourcePackManager.this.packs) {
                switch (packEntry.status.ordinal()) {
                    case 2: {
                        this.field_47635.add(packEntry);
                        break;
                    }
                    case 1: {
                        packEntry.status = ServerResourcePackManager.Status.INACTIVE;
                        packEntry.discard(ServerResourcePackManager.DiscardReason.ACTIVATION_FAILED);
                        break;
                    }
                    case 0: {
                        packEntry.discard(ServerResourcePackManager.DiscardReason.DISCARDED);
                    }
                }
            }
            ServerResourcePackManager.this.onPackChanged();
        } else {
            for (ServerResourcePackManager.PackEntry packEntry : ServerResourcePackManager.this.packs) {
                if (packEntry.status != ServerResourcePackManager.Status.PENDING) continue;
                packEntry.status = ServerResourcePackManager.Status.INACTIVE;
            }
        }
    }

    @Override
    public List<ReloadScheduler.PackInfo> getPacks() {
        return this.field_47635.stream().map(pack -> new ReloadScheduler.PackInfo(pack.id, pack.path)).toList();
    }
}
