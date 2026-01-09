package net.minecraft.util.profiler;

import java.util.Set;
import java.util.function.Supplier;

public interface SamplerSource {
   Set getSamplers(Supplier profilerSupplier);
}
