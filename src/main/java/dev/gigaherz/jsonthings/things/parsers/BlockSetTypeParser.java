package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.builders.BlockSetTypeBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.function.Consumer;

public class BlockSetTypeParser extends ThingParser<BlockSetType, BlockSetTypeBuilder>
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
                .ifKey("sound_type", val -> val.string().map(ResourceLocation::parse).handle(builder::setSoundType))
                .ifKey("door_close", val -> val.string().map(ResourceLocation::parse).handle(builder::setDoorClose))
                .ifKey("door_open", val -> val.string().map(ResourceLocation::parse).handle(builder::setDoorOpen))
                .ifKey("trapdoor_close", val -> val.string().map(ResourceLocation::parse).handle(builder::setTrapdoorClose))
                .ifKey("trapdoor_open", val -> val.string().map(ResourceLocation::parse).handle(builder::setTrapdoorOpen))
                .ifKey("pressure_plate_off", val -> val.string().map(ResourceLocation::parse).handle(builder::setPressurePlateOff))
                .ifKey("pressure_plate_on", val -> val.string().map(ResourceLocation::parse).handle(builder::setPressurePlateOn))
                .ifKey("button_off", val -> val.string().map(ResourceLocation::parse).handle(builder::setButtonOff))
                .ifKey("button_on", val -> val.string().map(ResourceLocation::parse).handle(builder::setButtonOn))
                .ifKey("is_wood", val -> val.bool().handle(builder::setIsWood))
                .ifKey("hanging_sign_sound_type", val -> val.string().map(ResourceLocation::parse).handle(builder::setHangingSignSoundType))
                .ifKey("fence_gate_close", val -> val.string().map(ResourceLocation::parse).handle(builder::setFenceGateClose))
                .ifKey("fence_gate_open", val -> val.string().map(ResourceLocation::parse).handle(builder::setFenceGateOpen))
                .ifKey("can_open_by_hand", val -> val.bool().handle(builder::setCanOpenByHand))
                .ifKey("can_open_by_wind_charge", val -> val.bool().handle(builder::setCanOpenByWindCharge))
                .ifKey("can_button_be_activated_by_arrows", val -> val.bool().handle(builder::setCanButtonBeActivatedByArrowsd))
                .ifKey("pressure_plate_sensitivity", val -> val.string().map(this::parsePressurePlateSensitivity).handle(builder::setPressurePlateSensitivity))

        ;

        builderModification.accept(builder);

        return builder;
    }

    private BlockSetType.PressurePlateSensitivity parsePressurePlateSensitivity(String str)
    {
        return "mobs".equals(str) ? BlockSetType.PressurePlateSensitivity.MOBS : BlockSetType.PressurePlateSensitivity.EVERYTHING;
    }
}
