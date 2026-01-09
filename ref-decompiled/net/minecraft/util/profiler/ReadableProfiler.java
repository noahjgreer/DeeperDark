package net.minecraft.util.profiler;

import java.util.Set;
import org.jetbrains.annotations.Nullable;

public interface ReadableProfiler extends Profiler {
   ProfileResult getResult();

   @Nullable
   ProfilerSystem.LocatedInfo getInfo(String name);

   Set getSampleTargets();
}
