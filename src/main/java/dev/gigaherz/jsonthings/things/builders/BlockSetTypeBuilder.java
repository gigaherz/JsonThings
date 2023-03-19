package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockSetTypeBuilder extends BaseBuilder<BlockSetType, BlockSetTypeBuilder>
{
    public static BlockSetTypeBuilder begin(ThingParser<BlockSetTypeBuilder> ownerParser, ResourceLocation registryName)
    {
        return new BlockSetTypeBuilder(ownerParser, registryName);
    }

    private ResourceLocation soundType;
    private ResourceLocation doorClose;
    private ResourceLocation doorOpen;
    private ResourceLocation trapdoorClose;
    private ResourceLocation trapdoorOpen;
    private ResourceLocation pressurePlateOff;
    private ResourceLocation pressurePlateOn;
    private ResourceLocation buttonOff;
    private ResourceLocation buttonOn;
    private boolean isWood;
    private ResourceLocation hangingSignSoundType;
    private ResourceLocation fenceGateClose;
    private ResourceLocation fenceGateOpen;

    private BlockSetTypeBuilder(ThingParser<BlockSetTypeBuilder> ownerParser, ResourceLocation registryName)
    {
        super(ownerParser, registryName);
    }


    public void setSoundType(ResourceLocation resourceLocation)
    {
        soundType = resourceLocation;
    }

    public void setDoorClose(ResourceLocation resourceLocation)
    {
        doorClose = resourceLocation;
    }

    public void setDoorOpen(ResourceLocation resourceLocation)
    {
        doorOpen = resourceLocation;
    }

    public void setTrapdoorClose(ResourceLocation resourceLocation)
    {
        trapdoorClose = resourceLocation;
    }

    public void setTrapdoorOpen(ResourceLocation resourceLocation)
    {
        trapdoorOpen = resourceLocation;
    }

    public void setPressurePlateOff(ResourceLocation resourceLocation)
    {
        pressurePlateOff = resourceLocation;
    }

    public void setPressurePlateOn(ResourceLocation resourceLocation)
    {
        pressurePlateOn = resourceLocation;
    }

    public void setButtonOff(ResourceLocation resourceLocation)
    {
        buttonOff = resourceLocation;
    }

    public void setButtonOn(ResourceLocation resourceLocation)
    {
        buttonOn = resourceLocation;
    }

    public void setIsWood(boolean b)
    {
        isWood = b;
    }

    public void setHangingSignSoundType(ResourceLocation resourceLocation)
    {
        hangingSignSoundType = resourceLocation;
    }

    public void setFenceGateClose(ResourceLocation resourceLocation)
    {
        fenceGateClose = resourceLocation;
    }

    public void setFenceGateOpen(ResourceLocation resourceLocation)
    {
        fenceGateOpen = resourceLocation;
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Block Set Type";
    }

    @Override
    protected BlockSetType buildInternal()
    {
        var soundTypeObj = Utils.getOrElse(ThingRegistries.SOUND_TYPES, soundType, SoundType.WOOD);
        var doorCloseEvent = Utils.getOrElse(ForgeRegistries.SOUND_EVENTS, doorClose, SoundEvents.WOODEN_DOOR_CLOSE);
        var doorOpenEvent = Utils.getOrElse(ForgeRegistries.SOUND_EVENTS, doorOpen, SoundEvents.WOODEN_DOOR_OPEN);
        var trapdoorCloseEvent = Utils.getOrElse(ForgeRegistries.SOUND_EVENTS, trapdoorClose, SoundEvents.WOODEN_TRAPDOOR_CLOSE);
        var trapdoorOpenEvent = Utils.getOrElse(ForgeRegistries.SOUND_EVENTS, trapdoorOpen, SoundEvents.WOODEN_TRAPDOOR_OPEN);
        var pressurePlateOffEvent = Utils.getOrElse(ForgeRegistries.SOUND_EVENTS, pressurePlateOff, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF);
        var pressurePlateOnEvent = Utils.getOrElse(ForgeRegistries.SOUND_EVENTS, pressurePlateOn, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON);
        var buttonOffEvent = Utils.getOrElse(ForgeRegistries.SOUND_EVENTS, buttonOff, SoundEvents.WOODEN_BUTTON_CLICK_OFF);
        var buttonOnEvent = Utils.getOrElse(ForgeRegistries.SOUND_EVENTS, buttonOn, SoundEvents.WOODEN_BUTTON_CLICK_ON);
        return new BlockSetType(getRegistryName().toString(), soundTypeObj,
                doorCloseEvent, doorOpenEvent, trapdoorCloseEvent, trapdoorOpenEvent,
                pressurePlateOffEvent, pressurePlateOnEvent, buttonOffEvent,buttonOnEvent);
    }

    public boolean isWood()
    {
        return isWood;
    }

    public WoodType buildWoodType(BlockSetType setType)
    {
        var hangingSignSoundType = Utils.getOrElse(ThingRegistries.SOUND_TYPES, pressurePlateOn, SoundType.HANGING_SIGN);
        var fenceGateClose = Utils.getOrElse(ForgeRegistries.SOUND_EVENTS, this.fenceGateClose, SoundEvents.FENCE_GATE_CLOSE);
        var fenceGateOpen = Utils.getOrElse(ForgeRegistries.SOUND_EVENTS, this.fenceGateOpen, SoundEvents.FENCE_GATE_OPEN);
        return new WoodType(getRegistryName().toString(), setType, setType.soundType(), hangingSignSoundType, fenceGateClose, fenceGateOpen);
    }
}
