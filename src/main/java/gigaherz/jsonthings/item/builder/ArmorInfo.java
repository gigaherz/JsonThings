package gigaherz.jsonthings.item.builder;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

public class ArmorInfo
{
    public EntityEquipmentSlot slot;
    public ItemArmor.ArmorMaterial material;

    public ArmorInfo(String equipmentSlot, String material)
    {
        this.slot = EntityEquipmentSlot.fromString(equipmentSlot);
        this.material = ItemArmor.ArmorMaterial.valueOf(material.toUpperCase());
    }
}
