/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
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
