package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class BlockSetTypeBuilder extends BaseBuilder<BlockSetType, BlockSetTypeBuilder>
{
    public static BlockSetTypeBuilder begin(ThingParser<BlockSetType, BlockSetTypeBuilder> ownerParser, Identifier registryName)
    {
        return new BlockSetTypeBuilder(ownerParser, registryName);
    }

    private Identifier soundType;
    private Identifier doorClose;
    private Identifier doorOpen;
    private Identifier trapdoorClose;
    private Identifier trapdoorOpen;
    private Identifier pressurePlateOff;
    private Identifier pressurePlateOn;
    private Identifier buttonOff;
    private Identifier buttonOn;
    private boolean isWood;
    private Identifier hangingSignSoundType;
    private Identifier fenceGateClose;
    private Identifier fenceGateOpen;
    private boolean canOpenByHand = true;
    private boolean canOpenByWindCharge = true;
    private boolean canButtonBeActivatedByArrows = true;
    private BlockSetType.PressurePlateSensitivity pressurePlateSensitivity = BlockSetType.PressurePlateSensitivity.EVERYTHING;


    private BlockSetTypeBuilder(ThingParser<BlockSetType, BlockSetTypeBuilder> ownerParser, Identifier registryName)
    {
        super(ownerParser, registryName);
    }


    public void setSoundType(Identifier resourceLocation)
    {
        soundType = resourceLocation;
    }

    public void setDoorClose(Identifier resourceLocation)
    {
        doorClose = resourceLocation;
    }

    public void setDoorOpen(Identifier resourceLocation)
    {
        doorOpen = resourceLocation;
    }

    public void setTrapdoorClose(Identifier resourceLocation)
    {
        trapdoorClose = resourceLocation;
    }

    public void setTrapdoorOpen(Identifier resourceLocation)
    {
        trapdoorOpen = resourceLocation;
    }

    public void setPressurePlateOff(Identifier resourceLocation)
    {
        pressurePlateOff = resourceLocation;
    }

    public void setPressurePlateOn(Identifier resourceLocation)
    {
        pressurePlateOn = resourceLocation;
    }

    public void setButtonOff(Identifier resourceLocation)
    {
        buttonOff = resourceLocation;
    }

    public void setButtonOn(Identifier resourceLocation)
    {
        buttonOn = resourceLocation;
    }

    public void setIsWood(boolean b)
    {
        isWood = b;
    }

    public void setHangingSignSoundType(Identifier resourceLocation)
    {
        hangingSignSoundType = resourceLocation;
    }

    public void setFenceGateClose(Identifier resourceLocation)
    {
        fenceGateClose = resourceLocation;
    }

    public void setFenceGateOpen(Identifier resourceLocation)
    {
        fenceGateOpen = resourceLocation;
    }

    public void setCanOpenByHand(boolean b)
    {
        canOpenByHand = b;
    }
    public void setCanOpenByWindCharge(boolean b)
    {
        canOpenByWindCharge = b;
    }
    public void setCanButtonBeActivatedByArrowsd(boolean b)
    {
        canButtonBeActivatedByArrows = b;
    }
    public void setPressurePlateSensitivity(BlockSetType.PressurePlateSensitivity sensitivity)
    {
        pressurePlateSensitivity = sensitivity;
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Block Set Type";
    }

    @Override
    protected BlockSetType buildInternal()
    {
        var soundTypeObj = Utils.getOrElse(ThingRegistries.SOUND_TYPE, soundType, SoundType.WOOD);
        var doorCloseEvent = Utils.getOrElse(BuiltInRegistries.SOUND_EVENT, doorClose, SoundEvents.WOODEN_DOOR_CLOSE);
        var doorOpenEvent = Utils.getOrElse(BuiltInRegistries.SOUND_EVENT, doorOpen, SoundEvents.WOODEN_DOOR_OPEN);
        var trapdoorCloseEvent = Utils.getOrElse(BuiltInRegistries.SOUND_EVENT, trapdoorClose, SoundEvents.WOODEN_TRAPDOOR_CLOSE);
        var trapdoorOpenEvent = Utils.getOrElse(BuiltInRegistries.SOUND_EVENT, trapdoorOpen, SoundEvents.WOODEN_TRAPDOOR_OPEN);
        var pressurePlateOffEvent = Utils.getOrElse(BuiltInRegistries.SOUND_EVENT, pressurePlateOff, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF);
        var pressurePlateOnEvent = Utils.getOrElse(BuiltInRegistries.SOUND_EVENT, pressurePlateOn, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON);
        var buttonOffEvent = Utils.getOrElse(BuiltInRegistries.SOUND_EVENT, buttonOff, SoundEvents.WOODEN_BUTTON_CLICK_OFF);
        var buttonOnEvent = Utils.getOrElse(BuiltInRegistries.SOUND_EVENT, buttonOn, SoundEvents.WOODEN_BUTTON_CLICK_ON);
        return new BlockSetType(getRegistryName().toString(), canOpenByHand, canOpenByWindCharge, canButtonBeActivatedByArrows, pressurePlateSensitivity, soundTypeObj,
                doorCloseEvent, doorOpenEvent, trapdoorCloseEvent, trapdoorOpenEvent,
                pressurePlateOffEvent, pressurePlateOnEvent, buttonOffEvent,buttonOnEvent);
    }

    public boolean isWood()
    {
        return isWood;
    }

    public WoodType buildWoodType(BlockSetType setType)
    {
        var hangingSignSoundType = Utils.getOrElse(ThingRegistries.SOUND_TYPE, pressurePlateOn, SoundType.HANGING_SIGN);
        var fenceGateClose = Utils.getOrElse(BuiltInRegistries.SOUND_EVENT, this.fenceGateClose, SoundEvents.FENCE_GATE_CLOSE);
        var fenceGateOpen = Utils.getOrElse(BuiltInRegistries.SOUND_EVENT, this.fenceGateOpen, SoundEvents.FENCE_GATE_OPEN);
        return new WoodType(getRegistryName().toString(), setType, setType.soundType(), hangingSignSoundType, fenceGateClose, fenceGateOpen);
    }

    @Override
    public void validate()
    {

    }
}
