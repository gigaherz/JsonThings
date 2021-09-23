package gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import gigaherz.jsonthings.things.builders.CreativeModeTabBuilder;
import gigaherz.jsonthings.util.parse.JParse;
import net.minecraft.resources.ResourceLocation;

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
