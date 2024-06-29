# Attribute Modifier Definition

Attribute Modifier definitions are not named, but rather contained within other json files.

See [Items](./Items.md#attribute_modifiers) for an example of where this is used.

## Basic structure of the JSON file

```json
{
  "attribute": "minecraft:speed",
  "id": "testpack:slightly_increase_speed",
  "amount": 0.5,
  "operation": "addition"
}
```

## "attribute"

Defines the attribute will have the modifier applied.

Required.

Must be a resource location string like `"string"`, or `"minecraft:stick"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "id"

Defines the ID of the attribute modifier, used to avoid duplicates and to understand which attribute to remove when requested.

Required.

Must be a resource location string like `"somepack:my_attribute_modifier"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied, but using minecraft namespace is discouraged for custom things.

## "amount"

Defines the amount to modify the value by.

Required.

Must be a number. Decimals are allowed.

## "operation"

Defines the operation to use when applying the modifier.

Required.

Can be either a number or one of the following strings:

* `0` or `"addition"`: The amount is directly added to the accumulated value.
  * Formula: `accumulated = accumulated + amount`
* `1` or `"multiply_base"`: The amount is multiplied by the base value of the attribute, then added to the accumulated.
  * Formula: `accumulated = accumulated + amount * base_value`
* `2` or `"multiply_total"`: The amount is multiplied by the base value of the attribute, then added to the accumulated.
  * Formula: `accumulated = accumulated * (1 + amount)`

Negative amount values can be used to reduce the total by a percent, e.g.: operation "multiply_total" and amount `-0.2` will effectively result in a 20% reduction of the total, or what's the same, a factor or 80% on the total.

**Note:** The values in the modifier are not displayed raw. Multiply type modifiers are shown as percent values (multiplied by 100). Additive modifiers are displayed as-is, except Knockback Resistance, which is multiplied by 10. So a Knockback Resistance of 0.1 will display as "1".
