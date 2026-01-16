package net.noahsarch.deeperdark.duck;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public interface BeaconDuck {
    void deeperDark$addTime(int seconds);
    void deeperDark$resetBeacon();
    void deeperDark$checkPayment(ItemStack payment);
}

