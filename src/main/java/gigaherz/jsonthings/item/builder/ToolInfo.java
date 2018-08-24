package gigaherz.jsonthings.item.builder;

public class ToolInfo
{
    public String toolClass;
    public String material;
    public int toolDamage;
    public int toolSpeed;

    public ToolInfo(String toolType, String material)
    {
        this.toolClass = toolType;
        this.material = material;
    }
}
