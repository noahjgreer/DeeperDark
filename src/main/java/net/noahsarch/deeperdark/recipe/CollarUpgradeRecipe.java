package net.noahsarch.deeperdark.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.item.CollarItem;
import net.noahsarch.deeperdark.item.CollarTier;

import java.util.ArrayList;
import java.util.List;

/**
 * Shaped upgrade recipe: 8 material items surrounding a collar in a 3x3 grid.
 * Copies all data components (dye, trinkets, fuel, custom name) from the source collar to the result.
 */
public class CollarUpgradeRecipe extends CustomRecipe {

    public static final Identifier SERIALIZER_ID = Identifier.fromNamespaceAndPath("deeperdark", "collar_upgrade");

    public static final MapCodec<CollarUpgradeRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
        i -> i.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("from").forGetter(r -> r.fromItem),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("to").forGetter(r -> r.toItem),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("material").forGetter(r -> r.material)
        ).apply(i, CollarUpgradeRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CollarUpgradeRecipe> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.registry(Registries.ITEM), r -> r.fromItem,
        ByteBufCodecs.registry(Registries.ITEM), r -> r.toItem,
        ByteBufCodecs.registry(Registries.ITEM), r -> r.material,
        CollarUpgradeRecipe::new
    );

    public static final RecipeSerializer<CollarUpgradeRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final Item fromItem;
    private final Item toItem;
    private final Item material;

    public CollarUpgradeRecipe(Item fromItem, Item toItem, Item material) {
        this.fromItem = fromItem;
        this.toItem = toItem;
        this.material = material;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != 3 || input.height() != 3) return false;
        ItemStack center = input.getItem(4);
        if (!center.is(fromItem) || !(center.getItem() instanceof CollarItem)) return false;
        for (int i = 0; i < input.size(); i++) {
            if (i == 4) continue;
            if (!input.getItem(i).is(material)) return false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack source = input.getItem(4);
        ItemStack result = new ItemStack(toItem);
        result.applyComponents(source.getComponentsPatch());

        // Remap trinkets: source active slots → destination active slots in order.
        // applyComponents copies CONTAINER as-is (source slot indices), but the
        // new tier may use different active slot indices, so we must repack.
        if (fromItem instanceof CollarItem fromCollar && toItem instanceof CollarItem toCollar) {
            CollarTier fromTier = fromCollar.getTier();
            CollarTier toTier   = toCollar.getTier();

            NonNullList<ItemStack> src = NonNullList.withSize(5, ItemStack.EMPTY);
            result.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(src);

            List<ItemStack> ordered = new ArrayList<>();
            for (int idx : fromTier.activeSlotIndices) {
                if (!src.get(idx).isEmpty()) ordered.add(src.get(idx).copy());
            }

            NonNullList<ItemStack> dest = NonNullList.withSize(5, ItemStack.EMPTY);
            int[] destSlots = toTier.activeSlotIndices;
            for (int i = 0; i < Math.min(ordered.size(), destSlots.length); i++) {
                dest.set(destSlots[i], ordered.get(i));
            }
            result.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(dest));
        }

        return result;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of();
    }

    @Override
    public RecipeSerializer<CollarUpgradeRecipe> getSerializer() {
        return SERIALIZER;
    }
}
