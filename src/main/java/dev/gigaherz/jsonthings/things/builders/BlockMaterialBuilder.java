package dev.gigaherz.jsonthings.things.builders;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public class BlockMaterialBuilder implements Supplier<Material>
{
    private Material builtMaterial = null;

    private final ResourceLocation registryName;

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
        this.registryName = registryName;
    }

    public static BlockMaterialBuilder begin(ResourceLocation registryName)
    {
        return new BlockMaterialBuilder(registryName);
    }

    public void setBuiltMaterial(Material builtMaterial)
    {
        this.builtMaterial = builtMaterial;
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

    private Material build()
    {
        return builtMaterial = new Material(this.color, this.liquid, this.solid, this.blocksMotion, this.solidBlocking, this.flammable, this.replaceable, this.pushReaction);
    }

    public Material get()
    {
        if (builtMaterial == null)
            return build();
        return builtMaterial;
    }

    public ResourceLocation getRegistryName()
    {
        return registryName;
    }
}
