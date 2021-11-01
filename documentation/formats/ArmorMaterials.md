# Armor Materials

Armor Materials define the tier and material properties of armor items.

Named item armor materials go in the `armor_material` directory in the thing pack.

E.g.
```
/things/examplepack/armor_material/rubber.json
```

## Basic structure of the JSON file

```json
{
  "toughness": 1.0,
  "knockback_resistance": 1.0,
  "equip_sound": "minecraft:item.armor.equip_chain",
  "repair_ingredient": {
    "item": "clay_ball"
  },
  "durability": {
    "feet": 20
  }
}
```


## "toughness"

Defines how many uses (without Unbreaking) the item has before breaking.

Required.

Must be a positive number or zero. Decimals are allowed.

## "knockback_resistance"

Defines how much knockback is reduced when wearing this armor.

Required.

Must be a positive number or zero. Decimals are allowed.

## "equip_sound"

Defines the sound the armor makes when equipped.

Required.

Must be a resource location string like `"item.armor.equip_chain"`, or `"minecraft:item.armor.equip_chain"`.
Like on model jsons and other vanilla files, if the namespace (the part before the colon) is missing "minecraft" is implied.

## "enchantment_value"

Defines how enchantable the tool is. Higher values will allow more enchantments to be placed at the same time.

Required.

Must be a positive integer or zero.

## "repair_ingredient"

Defines an ingredient to be used for repairing this tool tier.

Required.

Must be a json objects (`{}`) as defined in the [Ingredient definitions](./Ingredient.md).

## "durability"

Defines how many hits (without Unbreaking) the item can take before breaking, based on the slot the armor is equipped into.

Required.

Must be a json object (`{}`) containing positive integer values.

Values are technically optional but missing values will be 0 and the armor will break instantly on first hit.

See below for syntax.

## "armor"

Defines how many hits (without Unbreaking) the item can take before breaking, based on the slot the armor is equipped into.

Required.

Must be a json object (`{}`) containing positive integer values.

Values are optional. Missing values will be 0 and the armor will not protect when equipped in those slots.

Example:
```json
{
  "armor": {
    "feet": 30,
    "legs": 40,
    "chest": 50,
    "head": 60
  }
}
```
