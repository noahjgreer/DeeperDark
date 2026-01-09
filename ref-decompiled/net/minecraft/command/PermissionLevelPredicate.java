package net.minecraft.command;

import java.util.function.Predicate;

public interface PermissionLevelPredicate extends Predicate {
   int requiredLevel();
}
