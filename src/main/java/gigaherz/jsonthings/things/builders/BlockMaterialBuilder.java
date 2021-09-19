package gigaherz.jsonthings.things.builders;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;

public class BlockMaterialBuilder
{
    private Material builtFood = null;

    private final ResourceLocation registryName;

    private final Material.Builder materialBuilder;

    private BlockMaterialBuilder(ResourceLocation registryName, MaterialColor color)
    {
        this.registryName = registryName;
        this.materialBuilder = new Material.Builder(color);
    }

    public static BlockMaterialBuilder begin(ResourceLocation registryName, MaterialColor color)
    {
        return new BlockMaterialBuilder(registryName, color);
    }

    public BlockMaterialBuilder liquid() {
        materialBuilder.liquid();
        return this;
    }

    public BlockMaterialBuilder nonSolid() {
        materialBuilder.nonSolid();
        return this;
    }

    public BlockMaterialBuilder noCollider() {
        materialBuilder.noCollider();
        return this;
    }

    public BlockMaterialBuilder notSolidBlocking() {
        materialBuilder.notSolidBlocking();
        return this;
    }

    public BlockMaterialBuilder flammable() {
        materialBuilder.flammable();
        return this;
    }

    public BlockMaterialBuilder replaceable() {
        materialBuilder.replaceable();
        return this;
    }

    public BlockMaterialBuilder destroyOnPush() {
        materialBuilder.destroyOnPush();
        return this;
    }

    public BlockMaterialBuilder notPushable() {
        materialBuilder.notPushable();
        return this;
    }

    public Material build()
    {
        return builtFood = materialBuilder.build();
    }

    public Material getBuiltMaterial()
    {
        if (builtFood == null)
            return build();
        return builtFood;
    }

    public ResourceLocation getRegistryName()
    {
        return registryName;
    }
}
