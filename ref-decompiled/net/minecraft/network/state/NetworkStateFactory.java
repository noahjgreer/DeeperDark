package net.minecraft.network.state;

import java.util.function.Function;

public interface NetworkStateFactory extends NetworkState.Factory {
   NetworkState bind(Function registryBinder);
}
