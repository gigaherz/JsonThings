package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.builders.CreativeModeTabBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.gigaherz.jsonthings.util.parse.value.Any;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegisterEvent;
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

    public void registerTabs(RegisterEvent event)
    {
        event.register(Registries.CREATIVE_MODE_TAB, helper -> {
            LOGGER.info("Started registering Item things, errors about unexpected registry domains are harmless...");
            processAndConsumeErrors(getThingType(), getBuilders(), thing -> {
                var tab = thing.get();
                var icon = tab.icon();
                var name = tab.name();
                var builder = new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 0).icon(() -> icon.toStack(null))
                        .title(Component.translatable(name))
                        .displayItems((parameters, output) -> {
                            for (var variantProvider : thing.getVariantProviders())
                            {
                                variantProvider.provideVariants(
                                        ResourceKey.create(Registries.CREATIVE_MODE_TAB, thing.getRegistryName()),
                                        output, parameters, null, true);
                            }
                        });
                if (thing.getBefore() != null)
                    builder = builder.withTabsAfter(thing.getBefore());
                if (thing.getAfter() != null)
                    builder = builder.withTabsBefore(thing.getAfter());
                if (thing.getRightSide())
                    builder = builder.alignedRight();
                helper.register(thing.getRegistryName(), builder
                        .build());
            }, BaseBuilder::getRegistryName);
            LOGGER.info("Done processing thingpack CreativeModeTabs.");
        });
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
                .ifKey("translation_key", val -> val.string().handle(builder::setTranslationKey))
                .ifKey("right_side", val -> val.bool().handle(builder::setRightSide))
                .ifKey("items", val -> val.array().forEach((index, entry) -> entry
                        .ifString(str -> str.map(ResourceLocation::new).handle(builder::addItem))
                        .ifObj(obj -> obj.map((JsonObject name) -> parseStackContext(name, true, true)).handle(builder::addItem))
                        .typeError()
                ))
                .ifKey("before", val -> val.array().flatten(e -> e.string().map(ResourceLocation::new).value(), ResourceLocation[]::new).handle(builder::setBefore))
                .ifKey("after", val -> val.array().flatten(e -> e.string().map(ResourceLocation::new).value(), ResourceLocation[]::new).handle(builder::setAfter));

        builderModification.accept(builder);

        return builder;
    }
}
