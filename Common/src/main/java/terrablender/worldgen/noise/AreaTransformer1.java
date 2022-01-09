/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.worldgen.noise;

public interface AreaTransformer1
{
    default AreaFactory run(AreaContext context, AreaFactory areaFactory)
    {
        return () -> {
            Area area = areaFactory.make();
            return context.createResult((x, y) -> {
                context.initRandom((long)x, (long)y);
                return this.apply(context, area, x, y);
            }, area);
        };
    }

    int apply(AreaContext context, Area area, int x, int y);
}
