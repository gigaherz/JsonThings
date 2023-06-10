package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
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
        processAndConsumeErrors(getThingType(), getBuilders(), thing -> event.registerCreativeModeTab(thing.getRegistryName(), builder -> {
            var tab = thing.get();
            var icon = tab.icon();
            var name = tab.name();
            builder.icon(() -> icon.toStack(null)).title(Component.translatable(name)).displayItems((parameters, output) -> {
                for(var stackContext : thing.getItems())
                {
                    output.accept(stackContext.toStack(null));
                }
            });
        }), BaseBuilder::getRegistryName);
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
                .key("icon", val -> val
                        .ifString(str -> str.map(ResourceLocation::new).map(StackContext::new).handle(builder::setIcon))
                        .ifObj(str -> str.map((JsonObject name) -> parseStackContext(name, true, true)).handle(builder::setIcon))
                        .typeError()
                )
                .ifKey("items", val -> val.array().forEach((index,entry) -> entry.obj()
                        .map((JsonObject name) -> parseStackContext(name, true, true))
                        .handle(builder::addItem)));

        builderModification.accept(builder);

        return builder;
    }
}
