package net.noahsarch.deeperdark.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundSource;
import net.noahsarch.deeperdark.event.ItemMagnetHandler;
import net.noahsarch.deeperdark.sound.ModSounds;

public class ItemMagnetItem extends Item {

    public enum MagnetType {
        COPPER(30, 14),
        IRON(67, 14),
        GOLDEN(15, 22),
        DIAMOND(127, 10),
        NETHERITE(284, 15);

        public final int maxDurability;
        public final int enchantability;

        MagnetType(int maxDurability, int enchantability) {
            this.maxDurability = maxDurability;
            this.enchantability = enchantability;
        }
    }

    private final MagnetType magnetType;

    public ItemMagnetItem(MagnetType type, Item.Properties properties) {
        super(properties
                .durability(type.maxDurability)
                .component(DataComponents.ENCHANTABLE, new Enchantable(type.enchantability)));
        this.magnetType = type;
    }

    public MagnetType getMagnetType() {
        return magnetType;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && !player.getCooldowns().isOnCooldown(stack)) {
            if (ItemMagnetHandler.activateMagnet(player.getUUID(), hand)) {
                stack.hurtAndBreak(1, player, hand);
                player.getCooldowns().addCooldown(stack, 30);

                float pitch = 0.8f + level.getRandom().nextFloat() * 0.4f;
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.ITEM_MAGNET_ACTIVATE, SoundSource.PLAYERS, 1.0f, pitch);
            }
        }

        return InteractionResult.SUCCESS;
    }
}
