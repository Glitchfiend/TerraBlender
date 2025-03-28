/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package terrablender.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public interface WeightedEntry
{
    Weight getWeight();

    static <T> Wrapper<T> wrap(T object, int i) {
        return new Wrapper(object, Weight.of(i));
    }

    record Wrapper<T>(T data, Weight weight) implements WeightedEntry
    {
        public Wrapper(T data, Weight weight) {
            this.data = data;
            this.weight = weight;
        }

        public Weight getWeight() {
            return this.weight;
        }

        public static <E> Codec<Wrapper<E>> codec(Codec<E> codec) {
            return RecordCodecBuilder.create((instance) -> {
                return instance.group(codec.fieldOf("data").forGetter(Wrapper::data), Weight.CODEC.fieldOf("weight").forGetter(Wrapper::weight)).apply(instance, Wrapper::new);
            });
        }

        public T data() {
            return this.data;
        }

        public Weight weight() {
            return this.weight;
        }
    }
}