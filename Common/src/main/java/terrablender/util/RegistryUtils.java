/**
 * Copyright (C) Glitchfiend
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package terrablender.util;

import com.google.common.collect.Lists;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;

import java.util.List;
import java.util.function.Consumer;

public class RegistryUtils
{
    private static RegistryAccess registryAccess = BuiltinRegistries.ACCESS;
    private static List<Consumer<RegistryAccess>> registryAccessCaptureOneShotListeners = Lists.newArrayList();

    public static void captureCurrentRegistryAccess(RegistryAccess access)
    {
        registryAccess = access;
        registryAccessCaptureOneShotListeners.forEach(listener -> listener.accept(access));
        registryAccessCaptureOneShotListeners.clear();
    }

    public static void addRegistryAccessCaptureOneShotListener(Consumer<RegistryAccess> listener)
    {
        registryAccessCaptureOneShotListeners.add(listener);
    }

    public static RegistryAccess getCurrentRegistryAccess()
    {
        return registryAccess;
    }
}
