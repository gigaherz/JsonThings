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
  "attack_damage_bonus": false,
  "enchantment_value": false,
  "tag": false,
  "repair_ingredient": {
    
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

Must be a resource location string like `"mineable/axe"`, or `"minecraft:mineable/pickaxe"`. Like on model jsons and 
other vanilla files, if the namespace (the part before the colon) is missing "minecraft" is implied.

## "repair_ingredient"

Defines an ingredient to be used for repairing this tool tier. Due to initialization timing, the ingredient cannot be
a custom (mod-defined) ingredient. Only item and tag ingredients are supported.

Required.

Must be a json objects (`{}`) as defined below.

Item ingredients contain an `"item"` key with a resource location indicating the item registry name.

```json
{
  "item": "minecraft:clay"
}
```

Tag ingredients contain a `"tag"` key with a resource location indicating the item registry name.

```json
{
  "tag": "forge:string"
}
```
