package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.builders.CreativeModeTabBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class CreativeModeTabParser extends ThingParser<CreativeModeTabBuilder>
{
    public CreativeModeTabParser()
    {
        super(GSON, "creative_mode_tab");
    }

    @Override
    protected void finishLoadingInternal()
    {
        getBuilders().forEach(CreativeModeTabBuilder::get);
    }

    @Override
    protected CreativeModeTabBuilder processThing(ResourceLocation key, JsonObject data, Consumer<CreativeModeTabBuilder> builderModification)
    {
        final CreativeModeTabBuilder builder = CreativeModeTabBuilder.begin(key);

        JParse.begin(data)
                .key("icon", val -> val.string().map(ResourceLocation::new).handle(builder::setIcon));

        builderModification.accept(builder);

        return builder;
    }
}
