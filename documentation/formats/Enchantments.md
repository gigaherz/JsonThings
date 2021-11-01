# Enchantments

Enchantment definitions let you define new enchantments.

Enchantment definitions go in the `enchantment` directory in the thing pack.

E.g.
```
/things/examplepack/enchantment/deadly.json
```

## Basic structure of the JSON file

```json
{
  "max_level": 3,
  "disallow_enchants": [
    "minecraft:smite", "minecraft:bane_of_arthropods"
  ]
}
```

## "rarity"

Defines the rarity of the enchantment.

Optional. Default: common.

Must be one of these strings: `"common"`, `"uncommon"`, `"rare"`, `"very_rare"`.

## "type"

Defines the type of enchantment.

Optional. Default: breakable.

Must be one of these strings: `"breakable"`, `"armor"`, `"armor_feet"`, `"armor_legs"`, `"armor_chest"`, `"armor_head"`, `"weapon"`, `"digger"`, `"fishing_rod"`, `"trident"`, `"bow"`, `"wearable"`, `"crossbow"`, `"vanishable"`.

## "min_level"

Defines the minimum enchantment level that appears in the creative menu and in the enchanting table.

Optional. Default: 1.

Must be a positive integer bigger than zero.

## "max_level"

Defines the maximum enchantment level that appears in the creative menu and in the enchanting table.

Optional. Default: 1.

Must be a positive integer bigger than zero. Must be bigger or equal to `"min_level"`.

## "base_cost"

Defines the base enchantability cost of this enchant. Bigger numbers will make it harder to obtain the enchantments along with other enchantments.

Optional. Default: 1.

Must be a positive integer or zero.

## "per_level_cost"

Defines the per-level enchantability cost increase of this enchant. Bigger numbers will make it harder to obtain the enchantments along with other enchantments.

Optional. Default: 10.

Must be a positive integer or zero.

## "random_cost"

Defines the random variance in the enchantability cost of this enchant. Bigger numbers will make it harder to obtain the enchantments along with other enchantments.

Optional. Default: 5.

Must be a positive integer or zero.

## "item_compatibility"

Defines an item predicate which matches compatible items for this enchantment.

Optional. Default: all items are allowed.

See the vanilla documentation on item predicates, which are used in both advancement criteria and loot tables.

## "disallow_enchants"

Defines a list of enchantments that are not allowed to exist at the same time as this enchantment.

Optional. Default: all enchants that don't disallow this are allowed.

Must be a json array (`[]`) containing strings of resource locations of other enchantments.

## "treasure"

Defines if the enchantment is treasure. If true, it will only be obtainable in loot, and won't appear in the enchanting table.

Optional. Default: false.

Must be a boolean (`false` or `true`).

## "curse"

Defines if the enchantment is a curse. If true, it will show red in the tooltip.

Optional. Default: false.

Must be a boolean (`false` or `true`).

## "tradeable"

Defines if the enchantment is tradeable. If true, it will appear in the villager trades.

Optional. Default: true.

Must be a boolean (`false` or `true`).

## "discoverable"

Defines if the enchantment is discoverable. If true, it will appear in loot.

Optional. Default: true.

Must be a boolean (`false` or `true`).

## "allow_on_books"

Defines if the enchantment is allowed to be on books. If true, it will appear when enchanting books.

Optional. Default: true.

Must be a boolean (`false` or `true`).
