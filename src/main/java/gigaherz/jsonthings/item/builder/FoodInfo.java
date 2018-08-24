package gigaherz.jsonthings.item.builder;

public class FoodInfo
{
    public int healAmount;
    public float saturation;
    public boolean isWolfFood;

    public FoodInfo(int healAmount, float saturation, boolean isWolfFood)
    {
        this.healAmount = healAmount;
        this.saturation = saturation;
        this.isWolfFood = isWolfFood;
    }
}
