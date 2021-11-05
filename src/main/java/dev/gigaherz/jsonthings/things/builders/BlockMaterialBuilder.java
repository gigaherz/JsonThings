package dev.gigaherz.jsonthings.things.builders;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;

public class BlockMaterialBuilder extends BaseBuilder<Material>
{
    private PushReaction pushReaction = PushReaction.NORMAL;

    private MaterialColor color;
    private boolean blocksMotion = true;
    private boolean flammable;
    private boolean liquid;
    private boolean replaceable;
    private boolean solid = true;
    private boolean solidBlocking = true;

    private BlockMaterialBuilder(ResourceLocation registryName)
    {
        super(registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Block Material";
    }

    public static BlockMaterialBuilder begin(ResourceLocation registryName)
    {
        return new BlockMaterialBuilder(registryName);
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
