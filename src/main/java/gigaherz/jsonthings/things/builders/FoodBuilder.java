package gigaherz.jsonthings.things.builders;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.item.Food;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;

public class FoodBuilder
{
    private final Multimap<String, String> eventHandlers = ArrayListMultimap.create();

    private Food builtFood = null;

    private ResourceLocation registryName;

    private Food.Builder foodBuilder = new Food.Builder();

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

    public FoodBuilder effect(EffectInstance effect, float probability)
    {
        foodBuilder = foodBuilder.effect(effect, probability);
        return this;
    }

    public Food build()
    {
        return builtFood = foodBuilder.build();
    }

    public Food getBuiltFood()
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
