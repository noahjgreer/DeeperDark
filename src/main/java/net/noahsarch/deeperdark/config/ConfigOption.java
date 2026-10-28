package net.noahsarch.deeperdark.config;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * One editable setting of the config menu. Values cross the network as strings, so {@link #parse}
 * is the single validation point for both the client's text boxes and the server's apply step.
 */
public record ConfigOption<T>(String key, Kind kind, double min, double max, String defaultValue,
                              Function<T, Object> getter, BiConsumer<T, Object> setter) {

    private static final int MAX_SOUND_LENGTH = 256;

    public enum Kind { BOOL, INT, DOUBLE, SOUND }

    /** The parsed value, or null when the text isn't valid for this option. */
    public @Nullable Object parse(String text) {
        try {
            return switch (kind) {
                case BOOL -> text.equals("true") ? Boolean.TRUE : text.equals("false") ? Boolean.FALSE : null;
                case INT -> {
                    int value = Integer.parseInt(text.trim());
                    yield value >= min && value <= max ? value : null;
                }
                case DOUBLE -> {
                    double value = Double.parseDouble(text.trim());
                    yield value >= min && value <= max ? value : null;
                }
                case SOUND -> {
                    String sound = text.trim();
                    yield sound.length() <= MAX_SOUND_LENGTH && (sound.isEmpty() || Identifier.tryParse(sound) != null) ? sound : null;
                }
            };
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String get(T target) {
        Object value = getter.apply(target);
        return value == null ? "" : String.valueOf(value); // hand-edited YAML can leave sounds null
    }

    /** Returns false (and changes nothing) when the text isn't valid. */
    public boolean set(T target, String text) {
        Object value = parse(text);
        if (value == null) return false;
        setter.accept(target, value);
        return true;
    }

    /** Builds options whose default is read from a freshly constructed target. */
    @SuppressWarnings("unchecked")
    public record Builder<T>(T defaults) {
        public ConfigOption<T> bool(String key, Function<T, Boolean> get, BiConsumer<T, Boolean> set) {
            return of(key, Kind.BOOL, 0, 0, get, set);
        }

        public ConfigOption<T> integer(String key, int min, int max, Function<T, Integer> get, BiConsumer<T, Integer> set) {
            return of(key, Kind.INT, min, max, get, set);
        }

        public ConfigOption<T> decimal(String key, double min, double max, Function<T, Double> get, BiConsumer<T, Double> set) {
            return of(key, Kind.DOUBLE, min, max, get, set);
        }

        public ConfigOption<T> sound(String key, Function<T, String> get, BiConsumer<T, String> set) {
            return of(key, Kind.SOUND, 0, 0, get, set);
        }

        private <V> ConfigOption<T> of(String key, Kind kind, double min, double max, Function<T, V> get, BiConsumer<T, V> set) {
            return new ConfigOption<>(key, kind, min, max, String.valueOf(get.apply(defaults)),
                get::apply, (target, value) -> set.accept(target, (V) value));
        }
    }
}
