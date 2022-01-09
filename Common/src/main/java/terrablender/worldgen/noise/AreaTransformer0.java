/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.worldgen.noise;

public interface AreaTransformer0
{
    default AreaFactory run(AreaContext context) {
        return () -> {
            return context.createResult((x, y) -> {
                context.initRandom((long)x, (long)y);
                return this.apply(context, x, y);
            });
        };
    }

    int apply(AreaContext context, int x, int y);
}
