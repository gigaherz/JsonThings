# Item Tiers

Item tiers define the material tiers for tools. 

Named item tiers go in the `item_tier` directory in the thing pack.

Eg.
```
/things/examplepack/item_tiers/clay.json
```

## Basic structure of the JSON file

```json
{
  "uses": 10,
  "speed": 1.0,
  "attack_damage_bonus": 1,
  "enchantment_value": 1,
  "tag": "forge:requires_gold_tool",
  "repair_ingredient": {
    "item": "string"
  },
  "sort_after": [],
  "sort_before": []
}
```


## "uses"

Defines how many uses (without Unbreaking) the item has before breaking.

Required.

Must be a positive integer bigger than zero.

## "speed"

Defines how much cooldown the tool has after attacking.

Required.

Must be a positive number bigger than zero. Decimals are allowed.

## "attack_damage_bonus"

Defines how much attack bonus this tool has when used to attack enemies.

Required.

Must be a positive number or zero. Decimals are allowed.

## "enchantment_value"

Defines how enchantable the tool is. Higher values will allow more enchantments to be placed at the same time.

Required.

Must be a positive integer or zero.

## "tag"

Defines the set of blocks this tool can break faster than an empty hand.

Required.

Must be a resource location string like `"requires_stone_tool"`, or `"minecraft:requires_gold_tool"`. Like on model jsons and 
other vanilla files, if the namespace (the part before the colon) is missing "minecraft" is implied.

## "repair_ingredient"

Defines an ingredient to be used for repairing this tool tier. 

Required.

Must be a json objects (`{}`) as defined in the [Ingredient definitions](./Ingredient.md).

## "sort_before" and "sort_after"

Define the ordering of this tier in relation to others.

Sort After: Defines the tiers that are considered lower than this tier and must appear first on the list. This tier goes after them.

Sort Before: Defines the tiers that are considered higher than this tier and must appear later on the list. This tier goes before them.

Optional. Default: no dependencies.

Must be a json array (`[]`) containing strings.