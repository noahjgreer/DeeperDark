package net.minecraft.world.entity;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public interface EntityQueriable {
   @Nullable
   UniquelyIdentifiable getEntity(UUID uuid);
}
