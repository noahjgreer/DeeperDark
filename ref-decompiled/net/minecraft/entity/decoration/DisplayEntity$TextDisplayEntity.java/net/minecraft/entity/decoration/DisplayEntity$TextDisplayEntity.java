/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.decoration;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Optional;
import net.minecraft.command.permission.LeveledPermissionPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.Texts;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

public static class DisplayEntity.TextDisplayEntity
extends DisplayEntity {
    public static final String TEXT_NBT_KEY = "text";
    private static final String LINE_WIDTH_NBT_KEY = "line_width";
    private static final String TEXT_OPACITY_NBT_KEY = "text_opacity";
    private static final String BACKGROUND_NBT_KEY = "background";
    private static final String SHADOW_NBT_KEY = "shadow";
    private static final String SEE_THROUGH_NBT_KEY = "see_through";
    private static final String DEFAULT_BACKGROUND_NBT_KEY = "default_background";
    private static final String ALIGNMENT_NBT_KEY = "alignment";
    public static final byte SHADOW_FLAG = 1;
    public static final byte SEE_THROUGH_FLAG = 2;
    public static final byte DEFAULT_BACKGROUND_FLAG = 4;
    public static final byte LEFT_ALIGNMENT_FLAG = 8;
    public static final byte RIGHT_ALIGNMENT_FLAG = 16;
    private static final byte INITIAL_TEXT_OPACITY = -1;
    public static final int INITIAL_BACKGROUND = 0x40000000;
    private static final int DEFAULT_LINE_WIDTH = 200;
    private static final TrackedData<Text> TEXT = DataTracker.registerData(DisplayEntity.TextDisplayEntity.class, TrackedDataHandlerRegistry.TEXT_COMPONENT);
    private static final TrackedData<Integer> LINE_WIDTH = DataTracker.registerData(DisplayEntity.TextDisplayEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> BACKGROUND = DataTracker.registerData(DisplayEntity.TextDisplayEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Byte> TEXT_OPACITY = DataTracker.registerData(DisplayEntity.TextDisplayEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Byte> TEXT_DISPLAY_FLAGS = DataTracker.registerData(DisplayEntity.TextDisplayEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final IntSet TEXT_RENDERING_DATA_IDS = IntSet.of((int[])new int[]{TEXT.id(), LINE_WIDTH.id(), BACKGROUND.id(), TEXT_OPACITY.id(), TEXT_DISPLAY_FLAGS.id()});
    private @Nullable TextLines textLines;
    private @Nullable Data data;

    public DisplayEntity.TextDisplayEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(TEXT, Text.empty());
        builder.add(LINE_WIDTH, 200);
        builder.add(BACKGROUND, 0x40000000);
        builder.add(TEXT_OPACITY, (byte)-1);
        builder.add(TEXT_DISPLAY_FLAGS, (byte)0);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (TEXT_RENDERING_DATA_IDS.contains(data.id())) {
            this.renderingDataSet = true;
        }
    }

    public final Text getText() {
        return this.dataTracker.get(TEXT);
    }

    public final void setText(Text text) {
        this.dataTracker.set(TEXT, text);
    }

    public final int getLineWidth() {
        return this.dataTracker.get(LINE_WIDTH);
    }

    public final void setLineWidth(int lineWidth) {
        this.dataTracker.set(LINE_WIDTH, lineWidth);
    }

    public final byte getTextOpacity() {
        return this.dataTracker.get(TEXT_OPACITY);
    }

    public final void setTextOpacity(byte textOpacity) {
        this.dataTracker.set(TEXT_OPACITY, textOpacity);
    }

    public final int getBackground() {
        return this.dataTracker.get(BACKGROUND);
    }

    public final void setBackground(int background) {
        this.dataTracker.set(BACKGROUND, background);
    }

    public final byte getDisplayFlags() {
        return this.dataTracker.get(TEXT_DISPLAY_FLAGS);
    }

    public final void setDisplayFlags(byte flags) {
        this.dataTracker.set(TEXT_DISPLAY_FLAGS, flags);
    }

    private static byte readFlag(byte flags, ReadView view, String nbtKey, byte flag) {
        if (view.getBoolean(nbtKey, false)) {
            return (byte)(flags | flag);
        }
        return flags;
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.setLineWidth(view.getInt(LINE_WIDTH_NBT_KEY, 200));
        this.setTextOpacity(view.getByte(TEXT_OPACITY_NBT_KEY, (byte)-1));
        this.setBackground(view.getInt(BACKGROUND_NBT_KEY, 0x40000000));
        byte b = DisplayEntity.TextDisplayEntity.readFlag((byte)0, view, SHADOW_NBT_KEY, (byte)1);
        b = DisplayEntity.TextDisplayEntity.readFlag(b, view, SEE_THROUGH_NBT_KEY, (byte)2);
        b = DisplayEntity.TextDisplayEntity.readFlag(b, view, DEFAULT_BACKGROUND_NBT_KEY, (byte)4);
        Optional<TextAlignment> optional = view.read(ALIGNMENT_NBT_KEY, TextAlignment.CODEC);
        if (optional.isPresent()) {
            b = switch (optional.get().ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> b;
                case 1 -> (byte)(b | 8);
                case 2 -> (byte)(b | 0x10);
            };
        }
        this.setDisplayFlags(b);
        Optional<Text> optional2 = view.read(TEXT_NBT_KEY, TextCodecs.CODEC);
        if (optional2.isPresent()) {
            try {
                World world = this.getEntityWorld();
                if (world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld)world;
                    ServerCommandSource serverCommandSource = this.getCommandSource(serverWorld).withPermissions(LeveledPermissionPredicate.GAMEMASTERS);
                    MutableText text = Texts.parse(serverCommandSource, optional2.get(), (Entity)this, 0);
                    this.setText(text);
                } else {
                    this.setText(Text.empty());
                }
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to parse display entity text {}", optional2, (Object)exception);
            }
        }
    }

    private static void writeFlag(byte flags, WriteView view, String nbtKey, byte flag) {
        view.putBoolean(nbtKey, (flags & flag) != 0);
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.put(TEXT_NBT_KEY, TextCodecs.CODEC, this.getText());
        view.putInt(LINE_WIDTH_NBT_KEY, this.getLineWidth());
        view.putInt(BACKGROUND_NBT_KEY, this.getBackground());
        view.putByte(TEXT_OPACITY_NBT_KEY, this.getTextOpacity());
        byte b = this.getDisplayFlags();
        DisplayEntity.TextDisplayEntity.writeFlag(b, view, SHADOW_NBT_KEY, (byte)1);
        DisplayEntity.TextDisplayEntity.writeFlag(b, view, SEE_THROUGH_NBT_KEY, (byte)2);
        DisplayEntity.TextDisplayEntity.writeFlag(b, view, DEFAULT_BACKGROUND_NBT_KEY, (byte)4);
        view.put(ALIGNMENT_NBT_KEY, TextAlignment.CODEC, DisplayEntity.TextDisplayEntity.getAlignment(b));
    }

    @Override
    protected void refreshData(boolean shouldLerp, float lerpProgress) {
        this.data = shouldLerp && this.data != null ? this.getLerpedRenderState(this.data, lerpProgress) : this.copyData();
        this.textLines = null;
    }

    public @Nullable Data getData() {
        return this.data;
    }

    private Data copyData() {
        return new Data(this.getText(), this.getLineWidth(), DisplayEntity.IntLerper.constant(this.getTextOpacity()), DisplayEntity.IntLerper.constant(this.getBackground()), this.getDisplayFlags());
    }

    private Data getLerpedRenderState(Data data, float lerpProgress) {
        int i = data.backgroundColor.lerp(lerpProgress);
        int j = data.textOpacity.lerp(lerpProgress);
        return new Data(this.getText(), this.getLineWidth(), new DisplayEntity.IntLerperImpl(j, this.getTextOpacity()), new DisplayEntity.ArgbLerper(i, this.getBackground()), this.getDisplayFlags());
    }

    public TextLines splitLines(LineSplitter splitter) {
        if (this.textLines == null) {
            this.textLines = this.data != null ? splitter.split(this.data.text(), this.data.lineWidth()) : new TextLines(List.of(), 0);
        }
        return this.textLines;
    }

    public static TextAlignment getAlignment(byte flags) {
        if ((flags & 8) != 0) {
            return TextAlignment.LEFT;
        }
        if ((flags & 0x10) != 0) {
            return TextAlignment.RIGHT;
        }
        return TextAlignment.CENTER;
    }

    public static final class TextAlignment
    extends Enum<TextAlignment>
    implements StringIdentifiable {
        public static final /* enum */ TextAlignment CENTER = new TextAlignment("center");
        public static final /* enum */ TextAlignment LEFT = new TextAlignment("left");
        public static final /* enum */ TextAlignment RIGHT = new TextAlignment("right");
        public static final Codec<TextAlignment> CODEC;
        private final String name;
        private static final /* synthetic */ TextAlignment[] field_42455;

        public static TextAlignment[] values() {
            return (TextAlignment[])field_42455.clone();
        }

        public static TextAlignment valueOf(String string) {
            return Enum.valueOf(TextAlignment.class, string);
        }

        private TextAlignment(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static /* synthetic */ TextAlignment[] method_48920() {
            return new TextAlignment[]{CENTER, LEFT, RIGHT};
        }

        static {
            field_42455 = TextAlignment.method_48920();
            CODEC = StringIdentifiable.createCodec(TextAlignment::values);
        }
    }

    public static final class Data
    extends Record {
        private final Text text;
        private final int lineWidth;
        final DisplayEntity.IntLerper textOpacity;
        final DisplayEntity.IntLerper backgroundColor;
        private final byte flags;

        public Data(Text text, int lineWidth, DisplayEntity.IntLerper textOpacity, DisplayEntity.IntLerper backgroundColor, byte flags) {
            this.text = text;
            this.lineWidth = lineWidth;
            this.textOpacity = textOpacity;
            this.backgroundColor = backgroundColor;
            this.flags = flags;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Data.class, "text;lineWidth;textOpacity;backgroundColor;flags", "text", "lineWidth", "textOpacity", "backgroundColor", "flags"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Data.class, "text;lineWidth;textOpacity;backgroundColor;flags", "text", "lineWidth", "textOpacity", "backgroundColor", "flags"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Data.class, "text;lineWidth;textOpacity;backgroundColor;flags", "text", "lineWidth", "textOpacity", "backgroundColor", "flags"}, this, object);
        }

        public Text text() {
            return this.text;
        }

        public int lineWidth() {
            return this.lineWidth;
        }

        public DisplayEntity.IntLerper textOpacity() {
            return this.textOpacity;
        }

        public DisplayEntity.IntLerper backgroundColor() {
            return this.backgroundColor;
        }

        public byte flags() {
            return this.flags;
        }
    }

    public record TextLines(List<TextLine> lines, int width) {
    }

    @FunctionalInterface
    public static interface LineSplitter {
        public TextLines split(Text var1, int var2);
    }

    public record TextLine(OrderedText contents, int width) {
    }
}
