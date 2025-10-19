# Food Definitions

Food definitions let you define the properties of food items. They can be defined by name, or included directly in an item's `"food"` key.

Named food definitions go in the `food` directory in the thing pack.

E.g.
```
/things/examplepack/food/stick.json
```

NOTE: Food definitions **do not** automatically create an item. You must define an item that uses the food definition for it to appear ingame.

## Basic structure of the JSON file

```json
{
  "nutrition": 1,
  "saturation": 1.0,
  "meat": false,
  "fast": false,
  "always_eat": false,
  "convert_to":"minecraft:bowl",
  "effects": [
    
  ]  
}
```

## "nutrition"

Defines how much hunger this food restores.

Required.

Must be a positive integer bigger than zero.

## "saturation"

Defines how much saturation this food adds. Saturation is the delay until hunger restarts.

Required.

Must be a positive number or zero. Decimals are allowed. 

## "meat"

Defines if the food is considered meat. Meats are appealing to carnivores like wolves.

Optional. Default: false.

Must be a boolean (`false` or `true`).

## "fast"

Defines if the food can be eaten fast.

Optional. Default: false.

Must be a boolean (`false` or `true`).

## "always_eat"

Defines if the food can be eaten with a full stomach. Should only be used for snack foods and not big meals.

Optional. Default: false.

Must be a boolean (`false` or `true`).

## "convert_to"

Defines what will be left after eating it.

Optional. Default: No ("minecraft:air").

Must be a string.

## "effects"

Defines a list of effects that are applied when the food is eaten.

Optional. Default: no effects.

Must be a json array (`[]`) containing a series of json objects (`{}`), as defined below. 

The format for the effect is as follows:

```json
{
  "effects": [
    {
      "effect": "minecraft:poison",
      "duration": 5,
      "amplifier": 0,
      "visible": true,
      "show_particles": true,
      "show_icon": true      
    }
  ]
}
```

See the [Effect Instances](./EffectInstances.md) documentation page for details on the meaning of the values.
