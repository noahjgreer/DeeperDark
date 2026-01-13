/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.Orientation;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.block.enums.VaultState;

@Environment(value=EnvType.CLIENT)
static class BlockStateModelGenerator.1 {
    static final /* synthetic */ int[] field_23399;
    static final /* synthetic */ int[] field_48979;
    static final /* synthetic */ int[] field_47499;
    static final /* synthetic */ int[] field_22833;
    static final /* synthetic */ int[] field_43383;

    static {
        field_43383 = new int[DoubleBlockHalf.values().length];
        try {
            BlockStateModelGenerator.1.field_43383[DoubleBlockHalf.UPPER.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_43383[DoubleBlockHalf.LOWER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_22833 = new int[RailShape.values().length];
        try {
            BlockStateModelGenerator.1.field_22833[RailShape.NORTH_SOUTH.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_22833[RailShape.EAST_WEST.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_22833[RailShape.ASCENDING_EAST.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_22833[RailShape.ASCENDING_WEST.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_22833[RailShape.ASCENDING_NORTH.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_22833[RailShape.ASCENDING_SOUTH.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_47499 = new int[TrialSpawnerState.values().length];
        try {
            BlockStateModelGenerator.1.field_47499[TrialSpawnerState.INACTIVE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_47499[TrialSpawnerState.COOLDOWN.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_47499[TrialSpawnerState.WAITING_FOR_PLAYERS.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_47499[TrialSpawnerState.ACTIVE.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_47499[TrialSpawnerState.WAITING_FOR_REWARD_EJECTION.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_47499[TrialSpawnerState.EJECTING_REWARD.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_48979 = new int[VaultState.values().length];
        try {
            BlockStateModelGenerator.1.field_48979[VaultState.INACTIVE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_48979[VaultState.ACTIVE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_48979[VaultState.UNLOCKING.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_48979[VaultState.EJECTING.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_23399 = new int[Orientation.values().length];
        try {
            BlockStateModelGenerator.1.field_23399[Orientation.DOWN_NORTH.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_23399[Orientation.DOWN_SOUTH.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_23399[Orientation.DOWN_WEST.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_23399[Orientation.DOWN_EAST.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_23399[Orientation.UP_NORTH.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_23399[Orientation.UP_SOUTH.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_23399[Orientation.UP_WEST.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_23399[Orientation.UP_EAST.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_23399[Orientation.NORTH_UP.ordinal()] = 9;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_23399[Orientation.SOUTH_UP.ordinal()] = 10;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_23399[Orientation.WEST_UP.ordinal()] = 11;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockStateModelGenerator.1.field_23399[Orientation.EAST_UP.ordinal()] = 12;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
