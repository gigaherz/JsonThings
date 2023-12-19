package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.builders.FluidTypeBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.gigaherz.jsonthings.util.parse.function.ObjValueFunction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class FluidTypeParser extends ThingParser<FluidTypeBuilder>
{
    public static final Logger LOGGER = LogManager.getLogger();

    public FluidTypeParser(IEventBus bus)
    {
        super(GSON, "fluid_type");


        bus.addListener(this::register);
    }

    public void register(RegisterEvent event)
    {
        event.register(NeoForgeRegistries.Keys.FLUID_TYPES, helper -> {
            LOGGER.info("Started registering FluidType things, errors about unexpected registry domains are harmless...");
            processAndConsumeErrors(getThingType(), getBuilders(), thing -> helper.register(thing.getRegistryName(), thing.get()), BaseBuilder::getRegistryName);
            LOGGER.info("Done processing thingpack FluidTypes.");
        });
    }

    @Override
    public FluidTypeBuilder processThing(ResourceLocation key, JsonObject data, Consumer<FluidTypeBuilder> builderModification)
    {
        final FluidTypeBuilder builder = FluidTypeBuilder.begin(this, key);

        JParse.begin(data)
                .ifKey("parent", val -> val.string().map(ResourceLocation::new).handle(builder::setParent))
                .ifKey("translation_key", val -> val.string().handle(builder::setTranslationKey))
                .ifKey("still_texture", val -> val.string().map(ResourceLocation::new).handle(builder::setStillTexture))
                .ifKey("flowing_texture", val -> val.string().map(ResourceLocation::new).handle(builder::setFlowingTexture))
                .ifKey("side_texture", val -> val.string().map(ResourceLocation::new).handle(builder::setSideTexture))
                .ifKey("rarity", val -> val.string().map(ThingParser::parseRarity).handle(builder::setRarity))
                .ifKey("color", val -> val
                        .ifObj(obj -> obj.map((ObjValueFunction<Integer>) ThingParser::parseColor).handle(builder::setColor))
                        .ifArray(arr -> arr.mapWhole(ThingParser::parseColor).handle(builder::setColor))
                        .ifString(str -> str.map(ThingParser::parseColor).handle(builder::setColor))
                        .ifInteger(i -> i.handle(builder::setColor))
                        .typeError())
                .ifKey("density", val -> val.intValue().handle(builder::setDensity))
                .ifKey("luminosity", val -> val.intValue().handle(builder::setLightLevel))
                .ifKey("temperature", val -> val.intValue().handle(builder::setTemperature))
                .ifKey("viscosity", val -> val.intValue().handle(builder::setViscosity))
                .ifKey("gaseous", val -> val.bool().handle(builder::setGaseous))

                .ifKey("motion_scale", val -> val.doubleValue().handle(builder::setMotionScale))
                .ifKey("fall_distance_modifier", val -> val.floatValue().handle(builder::setFallDistanceModifier))
                .ifKey("can_push_entity", val -> val.bool().handle(builder::setCanPushEntity))
                .ifKey("can_swim", val -> val.bool().handle(builder::setCanSwim))
                .ifKey("can_drown", val -> val.bool().handle(builder::setCanDrown))
                .ifKey("can_extinguish", val -> val.bool().handle(builder::setCanExtinguish))
                .ifKey("can_hydrate", val -> val.bool().handle(builder::setCanHydrate))
                .ifKey("can_convert_to_source", val -> val.bool().handle(builder::setCanConvertToSource))
                .ifKey("supports_boating", val -> val.bool().handle(builder::setSupportsBoating))
                //.ifKey pathType(@org.jetbrains.annotations.Nullable BlockPathTypes pathType)
                //.ifKey adjacentPathType(@org.jetbrains.annotations.Nullable BlockPathTypes adjacentPathType)
                //.ifKey canHydrate(boolean canHydrate)

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
