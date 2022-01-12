/*
 * Copyright (C) Glitchfiend
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package terrablender.worldgen.noise;

public enum ZoomLayer implements AreaTransformer1
{
    NORMAL,
    FUZZY
    {
        @Override
        protected int modeOrRandom(AreaContext cont, int a, int b, int c, int d)
        {
            return cont.random(a, b, c, d);
        }
    };

    private static final int ZOOM_BITS = 1;
    private static final int ZOOM_MASK = 1;

    public int getParentX(int x)
    {
        return x >> 1;
    }

    public int getParentY(int y)
    {
        return y >> 1;
    }

    @Override
    public int apply(AreaContext context, Area area, int x, int y)
    {
        int i = area.get(this.getParentX(x), this.getParentY(y));
        context.initRandom((long) (x >> 1 << 1), (long) (y >> 1 << 1));
        int j = x & 1;
        int k = y & 1;
        if (j == 0 && k == 0)
        {
            return i;
        }
        else
        {
            int l = area.get(this.getParentX(x), this.getParentY(y + 1));
            int i1 = context.random(i, l);
            if (j == 0 && k == 1)
            {
                return i1;
            }
            else
            {
                int j1 = area.get(this.getParentX(x + 1), this.getParentY(y));
                int k1 = context.random(i, j1);
                if (j == 1 && k == 0)
                {
                    return k1;
                }
                else
                {
                    int l1 = area.get(this.getParentX(x + 1), this.getParentY(y + 1));
                    return this.modeOrRandom(context, i, j1, l, l1);
                }
            }
        }
    }

    protected int modeOrRandom(AreaContext context, int a, int b, int c, int d)
    {
        if (b == c && c == d)
            return b;
        else if (a == b && a == c)
            return a;
        else if (a == b && a == d)
            return a;
        else if (a == c && a == d)
            return a;
        else if (a == b && c != d)
            return a;
        else if (a == c && b != d)
            return a;
        else if (a == d && b != c)
            return a;
        else if (b == c && a != d)
            return b;
        else if (b == d && a != c)
            return b;
        else
            return c == d && a != b ? c : context.random(a, b, c, d);
    }
}
