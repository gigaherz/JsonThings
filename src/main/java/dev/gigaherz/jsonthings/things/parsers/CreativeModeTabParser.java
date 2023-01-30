package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.builders.CreativeModeTabBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class CreativeModeTabParser extends ThingParser<CreativeModeTabBuilder>
{
    public static final Logger LOGGER = LogManager.getLogger();

    public CreativeModeTabParser(IEventBus modBus)
    {
        super(GSON, "creative_mode_tab");

        modBus.addListener(this::registerTabs);
    }

    private void registerTabs(CreativeModeTabEvent.Register event)
    {
        LOGGER.info("Started registering Item things, errors about unexpected registry domains are harmless...");
        getBuilders().forEach(thing -> event.registerCreativeModeTab(thing.getRegistryName(), builder -> {
                var tab =  thing.get();
                var icon = tab.icon();
                var name = tab.name();
                builder.icon(() -> icon.get().getDefaultInstance()).title(Component.translatable(name)).displayItems((a, b, c) -> {});
        }));
        LOGGER.info("Done processing thingpack Blocks.");

    }

    @Override
    protected void finishLoadingInternal()
    {
        getBuilders().forEach(CreativeModeTabBuilder::get);
    }

    @Override
    protected CreativeModeTabBuilder processThing(ResourceLocation key, JsonObject data, Consumer<CreativeModeTabBuilder> builderModification)
    {
        final CreativeModeTabBuilder builder = CreativeModeTabBuilder.begin(this, key);

        JParse.begin(data)
                .key("icon", val -> val.string().map(ResourceLocation::new).handle(builder::setIcon));

        builderModification.accept(builder);

        return builder;
    }
}
