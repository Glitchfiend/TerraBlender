/**
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

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlWriter;

import java.nio.file.Path;

public class ConfigFile extends Config
{
    private final Path path;

    public ConfigFile(Path path)
    {
        super(CommentedFileConfig.builder(path).sync().autosave().build());
        this.path = path;
        ((CommentedFileConfig)this.getConfig()).load();
    }

    public void save()
    {
        (new TomlWriter()).write(sortConfig(this.getConfig()), this.path.toFile(), WritingMode.REPLACE);
    }
}
