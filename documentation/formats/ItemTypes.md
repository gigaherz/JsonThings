# Item Types

Items come in many types. Some common items require special handling such as special superclasses to be used in code.

To support those special items, there's a number of item types that can be specified in the json.

More types will be added in the future as needed.

## "plain"

Plain is the default item type. It has no special parameters

## "block"

Block items are items that represent and place a block. They are normally used via the block json's `"item"` key, but can be defined separately.

Parameters:
* `"places"`: The block registry name for the block that will be placed by this item.
  * Optional. Default: a block with the same name as the item.

## "armor" 

Armor items are equippable in the armor slots of the player and other entities. Armor items need special textures based on the armor material.

Parameters:
* `"equipment_slot"`: A string representing an equipment slot. One of: `"head"`, `"chest"`, `"legs"`, or `"feet"`.
  * Required.
* `"material"`: The name of an armor material.

## "sword", "shovel", "axe", "pickaxe", "hoe", "digger"

Tool items are good for digging things, and some also have advantages when used to attack.

They all share a common set of parameters:
* `"tier"`: The name of an item tier.
* `"damage"`: A number added on top of the tier's base damage, used to adjust it. Can be negative.
* `"speed"`: A number added on top of the tier's base speed, used to adjust it. Can be negative.

## "tiered"

Used for items that have tiers, but are not diggers or weapons.

Parameters:
* `"tier"`: The name of an item tier.

