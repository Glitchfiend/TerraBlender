/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;

public class TBCodec
{
    public static <K, V> LenientSimpleMapCodec<K, V> lenientSimpleMap(final Codec<K> keyCodec, final Codec<V> elementCodec, final Keyable keys) {
        return new LenientSimpleMapCodec<>(keyCodec, elementCodec, keys);
    }
}
