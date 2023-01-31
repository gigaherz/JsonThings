package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;

public class BlockMaterialBuilder extends BaseBuilder<Material, BlockMaterialBuilder>
{
    public static BlockMaterialBuilder begin(ThingParser<BlockMaterialBuilder> ownerParser, ResourceLocation registryName)
    {
        return new BlockMaterialBuilder(ownerParser, registryName);
    }

    private PushReaction pushReaction = PushReaction.NORMAL;
    private MaterialColor color;
    private boolean blocksMotion = true;
    private boolean flammable;
    private boolean liquid;
    private boolean replaceable;
    private boolean solid = true;
    private boolean solidBlocking = true;

    private BlockMaterialBuilder(ThingParser<BlockMaterialBuilder> ownerParser, ResourceLocation registryName)
    {
        super(ownerParser, registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Block Material";
    }

    public void setPushReaction(PushReaction pushReaction)
    {
        this.pushReaction = pushReaction;
    }

    public void setBlocksMotion(boolean blocksMotion)
    {
        this.blocksMotion = blocksMotion;
    }

    public void setFlammable(boolean flammable)
    {
        this.flammable = flammable;
    }

    public void setLiquid(boolean liquid)
    {
        this.liquid = liquid;
    }

    public void setReplaceable(boolean replaceable)
    {
        this.replaceable = replaceable;
    }

    public void setSolid(boolean solid)
    {
        this.solid = solid;
    }

    public void setColor(MaterialColor color)
    {
        this.color = color;
    }

    public void setSolidBlocking(boolean solidBlocking)
    {
        this.solidBlocking = solidBlocking;
    }

    @Override
    protected Material buildInternal()
    {
        return new Material(this.color, this.liquid, this.solid, this.blocksMotion, this.solidBlocking, this.flammable, this.replaceable, this.pushReaction);
    }
}
