package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.builders.FluidTypeBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.gigaherz.jsonthings.util.parse.function.ArrayValueFunction;
import dev.gigaherz.jsonthings.util.parse.function.ObjValueFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class FluidTypeParser extends ThingParser<FluidTypeBuilder>
{
    public static final Logger LOGGER = LogManager.getLogger();

    public FluidTypeParser(IEventBus bus)
    {
        super(GSON, "fluid");


        bus.addListener(this::register);
    }

    public void register(RegisterEvent event)
    {
        event.register(ForgeRegistries.Keys.FLUID_TYPES, helper -> {
            LOGGER.info("Started registering FluidType things, errors about unexpected registry domains are harmless...");
            getBuilders().forEach(thing -> helper.register(thing.getRegistryName(), thing.get()));
            LOGGER.info("Done processing thingpack Blocks.");
        });
    }

    @Override
    public FluidTypeBuilder processThing(ResourceLocation key, JsonObject data, Consumer<FluidTypeBuilder> builderModification)
    {
        final FluidTypeBuilder builder = FluidTypeBuilder.begin(key);

        JParse.begin(data)
                .ifKey("parent", val -> val.string().map(ResourceLocation::new).handle(builder::setParent))
                .ifKey("translation_key", val -> val.string().handle(builder::setTranslationKey))
                .key("still_texture", val -> val.string().map(ResourceLocation::new).handle(builder::setStillTexture))
                .key("flowing_texture", val -> val.string().map(ResourceLocation::new).handle(builder::setFlowingTexture))
                .ifKey("side_texture", val -> val.string().map(ResourceLocation::new).handle(builder::setSideTexture))
                .ifKey("rarity", val -> val.string().map(ThingParser::parseRarity).handle(builder::setRarity))
                .ifKey("color", val -> val
                        .ifObj(obj -> obj.map((ObjValueFunction<Integer>)ThingParser::parseColor).handle(builder::setColor))
                        .ifArray(arr -> arr.map((ArrayValueFunction<Integer>)ThingParser::parseColor).handle(builder::setColor))
                        .ifString(str -> str.map(ThingParser::parseColor).handle(builder::setColor))
                        .ifInteger(i -> i.handle(builder::setColor))
                        .typeError())
                .ifKey("density", val -> val.intValue().handle(builder::setDensity))
                .ifKey("luminosity", val -> val.intValue().handle(builder::setLightLevel))
                .ifKey("temperature", val -> val.intValue().handle(builder::setTemperature))
                .ifKey("viscosity", val -> val.intValue().handle(builder::setViscosity))
                .ifKey("gaseous", val -> val.bool().handle(builder::setGaseous))
                .ifKey("sounds", val -> val.obj() // TODO: make dynamic
                        .ifKey("bucket_fill", val1 -> val1.string().map(ResourceLocation::new).handle(builder::setFillSound))
                        .ifKey("bucket_empty", val1 -> val1.string().map(ResourceLocation::new).handle(builder::setEmptySound))
                        .ifKey("fluid_vaporize", val1 -> val1.string().map(ResourceLocation::new).handle(builder::setVaporizeSound))
                )
                .ifKey("events", val -> val.obj().map(this::parseEvents).handle(builder::setEventMap));

        builderModification.accept(builder);

        return builder;
    }
}
