/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.resource;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.resource.PackVersion;
import net.minecraft.util.dynamic.Range;
import org.jspecify.annotations.Nullable;

public record PackVersion.Format(Optional<PackVersion> min, Optional<PackVersion> max, Optional<Integer> format, Optional<Range<Integer>> supported) {
    static final MapCodec<PackVersion.Format> PACK_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)CODEC.optionalFieldOf("min_format").forGetter(PackVersion.Format::min), (App)ANY_CODEC.optionalFieldOf("max_format").forGetter(PackVersion.Format::max), (App)Codec.INT.optionalFieldOf("pack_format").forGetter(PackVersion.Format::format), (App)Range.createCodec(Codec.INT).optionalFieldOf("supported_formats").forGetter(PackVersion.Format::supported)).apply((Applicative)instance, PackVersion.Format::new));
    public static final MapCodec<PackVersion.Format> OVERLAY_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)CODEC.optionalFieldOf("min_format").forGetter(PackVersion.Format::min), (App)ANY_CODEC.optionalFieldOf("max_format").forGetter(PackVersion.Format::max), (App)Range.createCodec(Codec.INT).optionalFieldOf("formats").forGetter(PackVersion.Format::supported)).apply((Applicative)instance, (min, max, supported) -> new PackVersion.Format((Optional<PackVersion>)min, (Optional<PackVersion>)max, min.map(PackVersion::major), (Optional<Range<Integer>>)supported)));

    public static PackVersion.Format ofRange(Range<PackVersion> range, int lastOldPackVersion) {
        Range<Integer> range2 = range.map(PackVersion::major);
        return new PackVersion.Format(Optional.of(range.minInclusive()), Optional.of(range.maxInclusive()), range2.contains(lastOldPackVersion) ? Optional.of(range2.minInclusive()) : Optional.empty(), range2.contains(lastOldPackVersion) ? Optional.of(new Range<Integer>(range2.minInclusive(), range2.maxInclusive())) : Optional.empty());
    }

    public int minMajor() {
        if (this.min.isPresent()) {
            if (this.supported.isPresent()) {
                return Math.min(this.min.get().major(), this.supported.get().minInclusive());
            }
            return this.min.get().major();
        }
        if (this.supported.isPresent()) {
            return this.supported.get().minInclusive();
        }
        return Integer.MAX_VALUE;
    }

    public DataResult<Range<PackVersion>> validate(int lastOldPackVersion, boolean pack, boolean supportsOld, String packDescriptor, String supportedFormatsKey) {
        if (this.min.isPresent() != this.max.isPresent()) {
            return DataResult.error(() -> packDescriptor + " missing field, must declare both min_format and max_format");
        }
        if (supportsOld && this.supported.isEmpty()) {
            return DataResult.error(() -> packDescriptor + " missing required field " + supportedFormatsKey + ", must be present in all overlays for any overlays to work across game versions");
        }
        if (this.min.isPresent()) {
            return this.validateVersions(lastOldPackVersion, pack, supportsOld, packDescriptor, supportedFormatsKey);
        }
        if (this.supported.isPresent()) {
            return this.validateSupportedFormats(lastOldPackVersion, pack, packDescriptor, supportedFormatsKey);
        }
        if (pack && this.format.isPresent()) {
            int i = this.format.get();
            if (i > lastOldPackVersion) {
                return DataResult.error(() -> packDescriptor + " declares support for version newer than " + lastOldPackVersion + ", but is missing mandatory fields min_format and max_format");
            }
            return DataResult.success(new Range<PackVersion>(PackVersion.of(i)));
        }
        return DataResult.error(() -> packDescriptor + " could not be parsed, missing format version information");
    }

    private DataResult<Range<PackVersion>> validateVersions(int lastOldPackVersion, boolean pack, boolean supportsOld, String packDescriptor, String supportedFormatsKey) {
        int i = this.min.get().major();
        int j = this.max.get().major();
        if (this.min.get().compareTo(this.max.get()) > 0) {
            return DataResult.error(() -> packDescriptor + " min_format (" + String.valueOf(this.min.get()) + ") is greater than max_format (" + String.valueOf(this.max.get()) + ")");
        }
        if (i > lastOldPackVersion && !supportsOld) {
            String string;
            if (this.supported.isPresent()) {
                return DataResult.error(() -> packDescriptor + " key " + supportedFormatsKey + " is deprecated starting from pack format " + (lastOldPackVersion + 1) + ". Remove " + supportedFormatsKey + " from your pack.mcmeta.");
            }
            if (pack && this.format.isPresent() && (string = this.validateMainFormat(i, j)) != null) {
                return DataResult.error(() -> string);
            }
        } else {
            if (this.supported.isPresent()) {
                Range<Integer> range = this.supported.get();
                if (range.minInclusive() != i) {
                    return DataResult.error(() -> packDescriptor + " version declaration mismatch between " + supportedFormatsKey + " (from " + String.valueOf(range.minInclusive()) + ") and min_format (" + String.valueOf(this.min.get()) + ")");
                }
                if (range.maxInclusive() != j && range.maxInclusive() != lastOldPackVersion) {
                    return DataResult.error(() -> packDescriptor + " version declaration mismatch between " + supportedFormatsKey + " (up to " + String.valueOf(range.maxInclusive()) + ") and max_format (" + String.valueOf(this.max.get()) + ")");
                }
            } else {
                return DataResult.error(() -> packDescriptor + " declares support for format " + i + ", but game versions supporting formats 17 to " + lastOldPackVersion + " require a " + supportedFormatsKey + " field. Add \"" + supportedFormatsKey + "\": [" + i + ", " + lastOldPackVersion + "] or require a version greater or equal to " + (lastOldPackVersion + 1) + ".0.");
            }
            if (pack) {
                if (this.format.isPresent()) {
                    String string = this.validateMainFormat(i, j);
                    if (string != null) {
                        return DataResult.error(() -> string);
                    }
                } else {
                    return DataResult.error(() -> packDescriptor + " declares support for formats up to " + lastOldPackVersion + ", but game versions supporting formats 17 to " + lastOldPackVersion + " require a pack_format field. Add \"pack_format\": " + i + " or require a version greater or equal to " + (lastOldPackVersion + 1) + ".0.");
                }
            }
        }
        return DataResult.success(new Range<PackVersion>(this.min.get(), this.max.get()));
    }

    private DataResult<Range<PackVersion>> validateSupportedFormats(int lastOldPackVersion, boolean pack, String packDescriptor, String supportedFormatsKey) {
        Range<Integer> range = this.supported.get();
        int i = range.minInclusive();
        int j = range.maxInclusive();
        if (j > lastOldPackVersion) {
            return DataResult.error(() -> packDescriptor + " declares support for version newer than " + lastOldPackVersion + ", but is missing mandatory fields min_format and max_format");
        }
        if (pack) {
            if (this.format.isPresent()) {
                String string = this.validateMainFormat(i, j);
                if (string != null) {
                    return DataResult.error(() -> string);
                }
            } else {
                return DataResult.error(() -> packDescriptor + " declares support for formats up to " + lastOldPackVersion + ", but game versions supporting formats 17 to " + lastOldPackVersion + " require a pack_format field. Add \"pack_format\": " + i + " or require a version greater or equal to " + (lastOldPackVersion + 1) + ".0.");
            }
        }
        return DataResult.success(new Range<Integer>(i, j).map(PackVersion::of));
    }

    private @Nullable String validateMainFormat(int min, int max) {
        int i = this.format.get();
        if (i < min || i > max) {
            return "Pack declared support for versions " + min + " to " + max + " but declared main format is " + i;
        }
        if (i < 15) {
            return "Multi-version packs cannot support minimum version of less than 15, since this will leave versions in range unable to load pack.";
        }
        return null;
    }
}
