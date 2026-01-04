package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.builders.CreativeModeTabBuilder;
import dev.gigaherz.jsonthings.things.misc.FlexCreativeModeTab;
import dev.gigaherz.jsonthings.util.parse.JParse;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class CreativeModeTabParser extends ThingParser<FlexCreativeModeTab, CreativeModeTabBuilder>
{
    public static final Logger LOGGER = LogManager.getLogger();

    public CreativeModeTabParser(IEventBus modBus)
    {
        super(GSON, "creative_mode_tab");

        register(modBus, Registries.CREATIVE_MODE_TAB, CreativeModeTabBuilder::buildTab);
    }

    @Override
    protected void finishLoadingInternal()
    {
        getBuilders().forEach(CreativeModeTabBuilder::get);
    }

    @Override
    protected CreativeModeTabBuilder processThing(Identifier key, JsonObject data, Consumer<CreativeModeTabBuilder> builderModification)
    {
        final CreativeModeTabBuilder builder = CreativeModeTabBuilder.begin(this, key);

        JParse.begin(data)
                .key("icon", val -> val
                        .ifString(str -> str.map(Identifier::parse).map(StackContext::new).handle(builder::setIcon))
                        .ifObj(str -> str.map((JsonObject name) -> parseStackContext(name, true, true)).handle(builder::setIcon))
                        .typeError()
                )
                .ifKey("translation_key", val -> val.string().handle(builder::setTranslationKey))
                .ifKey("right_side", val -> val.bool().handle(builder::setRightSide))
                .ifKey("items", val -> val.array().forEach((index, entry) -> entry
                        .ifString(str -> str.map(Identifier::parse).handle(builder::addItem))
                        .ifObj(obj -> obj.map((JsonObject name) -> parseStackContext(name, true, true)).handle(builder::addItem))
                        .typeError()
                ))
                .ifKey("before", val -> val.array().flatten(e -> e.string().map(Identifier::parse).value(), Identifier[]::new).handle(builder::setBefore))
                .ifKey("after", val -> val.array().flatten(e -> e.string().map(Identifier::parse).value(), Identifier[]::new).handle(builder::setAfter));

        builderModification.accept(builder);

        return builder;
    }
}
