package net.noahsarch.deeperdark.mixin.compat;

import io.github.xiaocihua.stacktonearbychests.ForEachBlockContainerTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.Set;

/**
 * Makes Item Vault blocks visible to Stack to Nearby Chests regardless of what
 * is in the user's container filter list (ModOptions → stackingTargets).
 *
 * The mod's findAndOpenNextContainer() skips any block whose registry ID is not
 * in the filter collection.  We redirect that contains() call so vault blocks
 * always return true, while all other blocks still go through the normal filter.
 *
 * The rest of the stacking pipeline (quickMoveStack → VaultMenu deposit) already
 * works without changes because VaultMenu handles QUICK_MOVE from player inventory
 * slots directly.
 *
 * This mixin is guarded by StackToNearbyChestsMixinPlugin and will not be applied
 * when the mod is absent.
 */
@Mixin(value = ForEachBlockContainerTask.class, remap = false)
public class ForEachBlockContainerTaskMixin {

    private static final Set<String> VAULT_IDS = Set.of(
        "deeperdark:small_item_vault",
        "deeperdark:medium_item_vault",
        "deeperdark:large_item_vault"
    );

    @Redirect(
        method = "findAndOpenNextContainer",
        at = @At(value = "INVOKE", target = "Ljava/util/Collection;contains(Ljava/lang/Object;)Z"),
        remap = false
    )
    private boolean deeperdark$includeVaults(Collection<String> filter, Object key) {
        if (key instanceof String id && VAULT_IDS.contains(id)) {
            return true;
        }
        return filter.contains(key);
    }
}
