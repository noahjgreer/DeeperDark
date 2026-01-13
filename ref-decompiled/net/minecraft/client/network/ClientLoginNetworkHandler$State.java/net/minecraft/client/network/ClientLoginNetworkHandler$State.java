/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
static final class ClientLoginNetworkHandler.State
extends Enum<ClientLoginNetworkHandler.State> {
    public static final /* enum */ ClientLoginNetworkHandler.State CONNECTING = new ClientLoginNetworkHandler.State(Text.translatable("connect.connecting"), Set.of());
    public static final /* enum */ ClientLoginNetworkHandler.State AUTHORIZING = new ClientLoginNetworkHandler.State(Text.translatable("connect.authorizing"), Set.of(CONNECTING));
    public static final /* enum */ ClientLoginNetworkHandler.State ENCRYPTING = new ClientLoginNetworkHandler.State(Text.translatable("connect.encrypting"), Set.of(AUTHORIZING));
    public static final /* enum */ ClientLoginNetworkHandler.State JOINING = new ClientLoginNetworkHandler.State(Text.translatable("connect.joining"), Set.of(ENCRYPTING, CONNECTING));
    final Text name;
    final Set<ClientLoginNetworkHandler.State> prevStates;
    private static final /* synthetic */ ClientLoginNetworkHandler.State[] field_46199;

    public static ClientLoginNetworkHandler.State[] values() {
        return (ClientLoginNetworkHandler.State[])field_46199.clone();
    }

    public static ClientLoginNetworkHandler.State valueOf(String string) {
        return Enum.valueOf(ClientLoginNetworkHandler.State.class, string);
    }

    private ClientLoginNetworkHandler.State(Text name, Set<ClientLoginNetworkHandler.State> prevStates) {
        this.name = name;
        this.prevStates = prevStates;
    }

    private static /* synthetic */ ClientLoginNetworkHandler.State[] method_53874() {
        return new ClientLoginNetworkHandler.State[]{CONNECTING, AUTHORIZING, ENCRYPTING, JOINING};
    }

    static {
        field_46199 = ClientLoginNetworkHandler.State.method_53874();
    }
}
