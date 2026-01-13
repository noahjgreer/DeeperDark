/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.enums.RailShape;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;

static class AbstractRailBlock.1 {
    static final /* synthetic */ int[] field_11372;
    static final /* synthetic */ int[] field_11371;
    static final /* synthetic */ int[] field_11370;

    static {
        field_11370 = new int[BlockMirror.values().length];
        try {
            AbstractRailBlock.1.field_11370[BlockMirror.LEFT_RIGHT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbstractRailBlock.1.field_11370[BlockMirror.FRONT_BACK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_11371 = new int[BlockRotation.values().length];
        try {
            AbstractRailBlock.1.field_11371[BlockRotation.CLOCKWISE_180.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbstractRailBlock.1.field_11371[BlockRotation.COUNTERCLOCKWISE_90.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbstractRailBlock.1.field_11371[BlockRotation.CLOCKWISE_90.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_11372 = new int[RailShape.values().length];
        try {
            AbstractRailBlock.1.field_11372[RailShape.ASCENDING_EAST.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbstractRailBlock.1.field_11372[RailShape.ASCENDING_WEST.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbstractRailBlock.1.field_11372[RailShape.ASCENDING_NORTH.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbstractRailBlock.1.field_11372[RailShape.ASCENDING_SOUTH.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbstractRailBlock.1.field_11372[RailShape.NORTH_SOUTH.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbstractRailBlock.1.field_11372[RailShape.EAST_WEST.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbstractRailBlock.1.field_11372[RailShape.SOUTH_EAST.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbstractRailBlock.1.field_11372[RailShape.SOUTH_WEST.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbstractRailBlock.1.field_11372[RailShape.NORTH_WEST.ordinal()] = 9;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbstractRailBlock.1.field_11372[RailShape.NORTH_EAST.ordinal()] = 10;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
