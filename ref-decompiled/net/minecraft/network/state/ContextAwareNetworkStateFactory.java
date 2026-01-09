package net.minecraft.network.state;

import java.util.function.Function;

public interface ContextAwareNetworkStateFactory extends NetworkState.Factory {
   NetworkState bind(Function registryBinder, Object context);
}
