package gigaherz.jsonthings.things.builders;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;

public class FoodBuilder
{
    private FoodProperties builtFood = null;

    private final ResourceLocation registryName;

    private FoodProperties.Builder foodBuilder = new FoodProperties.Builder();

    private FoodBuilder(ResourceLocation registryName)
    {
        this.registryName = registryName;
    }

    public static FoodBuilder begin(ResourceLocation registryName)
    {
        return new FoodBuilder(registryName);
    }

    public FoodBuilder withHealAmount(int num)
    {
        foodBuilder = foodBuilder.nutrition(num);
        return this;
    }

    public FoodBuilder withSaturation(float num)
    {
        foodBuilder = foodBuilder.saturationMod(num);
        return this;
    }

    public FoodBuilder makeMeat()
    {
        foodBuilder = foodBuilder.meat();
        return this;
    }

    public FoodBuilder alwaysEat()
    {
        foodBuilder = foodBuilder.alwaysEat();
        return this;
    }

    public FoodBuilder fast()
    {
        foodBuilder = foodBuilder.fast();
        return this;
    }

    public FoodBuilder effect(MobEffectInstance effect, float probability)
    {
        foodBuilder = foodBuilder.effect(effect, probability);
        return this;
    }

    public FoodProperties build()
    {
        return builtFood = foodBuilder.build();
    }

    public FoodProperties getBuiltFood()
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
