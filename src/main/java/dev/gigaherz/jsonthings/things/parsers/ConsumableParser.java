package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.builders.ConsumableBuilder;
import dev.gigaherz.jsonthings.things.builders.SoundTypeBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.gigaherz.jsonthings.util.parse.value.Any;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.block.SoundType;

import java.util.List;
import java.util.function.Consumer;

public class ConsumableParser extends ThingParser<Consumable, ConsumableBuilder>
{
    public ConsumableParser()
    {
        super(GSON, "consumable");
    }

    /*@Override
    protected void finishLoadingInternal()
    {
        processAndConsumeErrors(getThingType(), getBuilders(), thing -> Registry.register(ThingRegistries.SOUND_TYPE, thing.getRegistryName(), thing.get()), BaseBuilder::getRegistryName);
    }*/

    @Override
    public ConsumableBuilder processThing(ResourceLocation key, JsonObject data, Consumer<ConsumableBuilder> builderModification)
    {
        final ConsumableBuilder builder = ConsumableBuilder.begin(this, key);

        JParse.begin(data)
                .key("consume_seconds", val -> val.floatValue().range(0,1).handle(builder::setConsumeSeconds))
                .key("animation", val -> val.string().map(str -> ItemUseAnimation.valueOf(str.toUpperCase())).handle(builder::setAnimation))
                .key("sound", val -> val.string().map(ResourceLocation::parse).handle(builder::setSound))
                .key("has_consume_particles", val -> val.bool().handle(builder::setHasConsumeParticles))
                .key("on_consume_effects", val -> val.array().flatten(Any::get, JsonElement[]::new).handle(builder::setOnConsumeEffects))
        ;

        builderModification.accept(builder);

        return builder;
    }
}
