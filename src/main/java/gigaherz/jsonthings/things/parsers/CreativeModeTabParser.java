package gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import gigaherz.jsonthings.things.builders.CreativeModeTabBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class CreativeModeTabParser extends ThingParser<CreativeModeTabBuilder>
{
    public CreativeModeTabParser()
    {
        super(GSON, "creative_mode_tab");
    }

    @Override
    public void finishLoading()
    {
        getBuilders().forEach(CreativeModeTabBuilder::build);
    }

    @Override
    protected CreativeModeTabBuilder processThing(ResourceLocation key, JsonObject data)
    {
        CreativeModeTabBuilder builder = CreativeModeTabBuilder.begin(key);

        builder = builder.withIcon(new ResourceLocation(GsonHelper.getAsString(data, "icon")));

        return builder;
    }
}
