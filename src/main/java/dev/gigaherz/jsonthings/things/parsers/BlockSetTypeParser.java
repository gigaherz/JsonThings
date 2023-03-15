package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.builders.BlockSetTypeBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.function.Consumer;

public class BlockSetTypeParser extends ThingParser<BlockSetTypeBuilder>
{
    public BlockSetTypeParser()
    {
        super(GSON, "block_set_type");
    }

    @Override
    protected void finishLoadingInternal()
    {
        processAndConsumeErrors(getThingType(), getBuilders(), thing -> {
            var type = BlockSetType.register(thing.get());
            if (thing.isWood())
                WoodType.register(thing.buildWoodType(type));
        }, BaseBuilder::getRegistryName);
    }

    @Override
    public BlockSetTypeBuilder processThing(ResourceLocation key, JsonObject data, Consumer<BlockSetTypeBuilder> builderModification)
    {
        final BlockSetTypeBuilder builder = BlockSetTypeBuilder.begin(this, key);

        JParse.begin(data)
                .ifKey("sound_type", val -> val.string().map(ResourceLocation::new).handle(builder::setSoundType))
                .ifKey("door_close", val -> val.string().map(ResourceLocation::new).handle(builder::setDoorClose))
                .ifKey("door_open", val -> val.string().map(ResourceLocation::new).handle(builder::setDoorOpen))
                .ifKey("trapdoor_close", val -> val.string().map(ResourceLocation::new).handle(builder::setTrapdoorClose))
                .ifKey("trapdoor_open", val -> val.string().map(ResourceLocation::new).handle(builder::setTrapdoorOpen))
                .ifKey("pressure_plate_off", val -> val.string().map(ResourceLocation::new).handle(builder::setPressurePlateOff))
                .ifKey("pressure_plate_on", val -> val.string().map(ResourceLocation::new).handle(builder::setPressurePlateOn))
                .ifKey("button_off", val -> val.string().map(ResourceLocation::new).handle(builder::setButtonOff))
                .ifKey("button_on", val -> val.string().map(ResourceLocation::new).handle(builder::setButtonOn))
                .ifKey("is_wood", val -> val.bool().handle(builder::setIsWood))
                .ifKey("hanging_sign_sound_type", val -> val.string().map(ResourceLocation::new).handle(builder::setHangingSignSoundType))
                .ifKey("fence_gate_close", val -> val.string().map(ResourceLocation::new).handle(builder::setFenceGateClose))
                .ifKey("fence_gate_open", val -> val.string().map(ResourceLocation::new).handle(builder::setFenceGateOpen))
        ;

        builderModification.accept(builder);

        return builder;
    }
}
