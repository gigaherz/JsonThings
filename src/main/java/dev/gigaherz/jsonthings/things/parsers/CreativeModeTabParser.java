package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.builders.CreativeModeTabBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import net.minecraft.util.ResourceLocation;

public class CreativeModeTabParser extends ThingParser<CreativeModeTabBuilder>
{
    public CreativeModeTabParser()
    {
        super(GSON, "creative_mode_tab");
    }

    @Override
    public void finishLoading()
    {
        getBuilders().forEach(CreativeModeTabBuilder::get);
    }

    @Override
    protected CreativeModeTabBuilder processThing(ResourceLocation key, JsonObject data)
    {
        final CreativeModeTabBuilder builder = CreativeModeTabBuilder.begin(key);

        JParse.begin(data)
                .obj()
                .key("icon", val -> val.string().map(ResourceLocation::new).handle(builder::setIcon));

        return builder;
    }
}
