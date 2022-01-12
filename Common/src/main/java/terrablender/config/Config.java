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
package terrablender.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlWriter;
import net.minecraft.util.StringRepresentable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Config
{
    private final CommentedConfig config;

    public Config(CommentedConfig config)
    {
        this.config = config;
    }

    public <T> List<T> addList(String comment, String key, List<T> defaultValue)
    {
        if (config.get(key) == null)
        {
            config.set(key, defaultValue);
        }

        if (config.getComment(key) == null)
        {
            config.setComment(key, comment);
        }
        return config.get(key);
    }

    public Config addSubConfig(String comment, String key, Config defaultValue)
    {
        if (config.get(key) == null)
        {
            config.set(key, sortConfig(defaultValue.config));
        }

        CommentedConfig subConfig = config.get(key);
        String commentValue = config.getComment(key);
        if (commentValue == null)
        {
            config.setComment(key, comment);
        }

        return new Config(subConfig);
    }

    public Map<?, ?> addMap(String comment, String key, Map<?, Number> defaultValue)
    {
        if (config.get(key) == null)
        {
            CommentedConfig subConfig = config.createSubConfig();
            defaultValue.forEach((a, b) -> {
                String subConfigKey = a.toString();
                if (subConfig.get(a.toString()) == null)
                {
                    subConfig.set(subConfigKey, b);
                }
            });
            config.set(key, subConfig);
        }

        CommentedConfig subConfig = config.get(key);
        String commentValue = config.getComment(key);
        if (commentValue == null)
        {
            config.setComment(key, comment);
        }

        return subConfig.valueMap();
    }

    public <T> T add(String comment, String key, T defaultValue)
    {
        if (config.get(key) == null)
        {
            config.set(key, defaultValue);
        }

        if (config.getComment(key) == null)
        {
            config.setComment(key, comment);
        }
        return config.get(key);
    }

    public <T extends Number & Comparable<T>> T addNumber(String comment, String key, T defaultValue, T min, T max)
    {
        if (config.get(key) == null)
        {
            config.set(key, defaultValue);
        }

        if (config.getComment(key) == null)
        {
            config.setComment(key, comment + String.format("\nRange: %s-%s", min, max));
        }
        T value = config.get(key);
        return value.compareTo(max) > 0 ? max : value.compareTo(min) < 0 ? min : value;
    }

    public <T extends Enum<T>> T addEnum(String comment, String key, T defaultValue)
    {
        if (config.get(key) == null)
        {
            config.set(key, defaultValue);
        }

        if (config.getComment(key) == null)
        {
            StringBuilder builder = new StringBuilder().append("Values: ").append(defaultValue instanceof StringRepresentable ? "\n" : "");
            for (T value : defaultValue.getDeclaringClass().getEnumConstants())
            {
                if (defaultValue instanceof StringRepresentable)
                {
                    builder.append(((StringRepresentable) value).getSerializedName()).append("\n");
                }
                else
                {
                    builder.append(value.name()).append(", ");
                }
            }

            config.setComment(key, comment + "\n" + builder.toString());
        }

        String value = config.get(key).toString();
        return T.valueOf(defaultValue.getDeclaringClass(), value);
    }

    public <T> T getValue(String key)
    {
        return this.config.get(key);
    }

    public Config getSubConfig(String key)
    {
        CommentedConfig sub = this.config.get(key);
        return new Config(sub != null ? sub : CommentedConfig.inMemory());
    }

    protected CommentedConfig getConfig()
    {
        return this.config;
    }

    protected static CommentedConfig sortConfig(CommentedConfig config)
    {
        CommentedConfig newConfig = CommentedConfig.of(com.electronwill.nightconfig.core.Config.getDefaultMapCreator(false, true), TomlFormat.instance());

        List<Map.Entry<String, Object>> organizedCollection = config.valueMap().entrySet().stream().sorted(Comparator.comparing(Objects::toString)).collect(Collectors.toList());
        organizedCollection.forEach((stringObjectEntry -> {
            newConfig.add(stringObjectEntry.getKey(), stringObjectEntry.getValue());
        }));

        newConfig.commentMap().putAll(config.commentMap());
        return newConfig;
    }
}
