package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;

public class FoodPropertiesBuilder extends BaseBuilder<FoodProperties, FoodPropertiesBuilder>
{
    public static FoodPropertiesBuilder begin(ThingParser<FoodProperties, FoodPropertiesBuilder> ownerParser, ResourceLocation registryName)
    {
        return new FoodPropertiesBuilder(ownerParser, registryName);
    }

    private int nutrition;
    private float saturation;
    private boolean alwaysEat;

    private FoodPropertiesBuilder(ThingParser<FoodProperties, FoodPropertiesBuilder> ownerParser, ResourceLocation registryName)
    {
        super(ownerParser, registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Food Definition";
    }

    public void setNutrition(int num)
    {
        this.nutrition = num;
    }

    public void setSaturation(float num)
    {
        this.saturation = num;
    }

    public void setAlwaysEat(boolean alwaysEat)
    {
        this.alwaysEat = alwaysEat;
    }

    @Override
    protected FoodProperties buildInternal()
    {
        var foodBuilder = new FoodProperties.Builder();
        foodBuilder.nutrition(nutrition);
        foodBuilder.saturationModifier(saturation);
        if (alwaysEat) foodBuilder.alwaysEdible();
        return foodBuilder.build();
    }

    @Override
    public void validate()
    {
    }
}
