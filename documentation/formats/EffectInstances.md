# Effect Instance Definitions

This format applies to effect instances as defined in other json files. There is no named definitions for effect instances
at this point.

See [Food](./Food.md) for an example of where this is used.

## Basic structure of the JSON file

```json
{
  "effect": "minecraft:poison",
  "duration": 5,
  "amplifier": 0,
  "ambient": false,
  "visible": true,
  "show_particles": true,
  "show_icon": true
}
```

## "effect"

Defines which potion effect to apply.

Required.

Must be a resource location string like `"poison"`, or `"minecraft:blindness"`. Like on model jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "duration"

Defines how long the effect lasts, in ingame ticks (20ths of a second).

Optional. Default: 0. 

The default will disappear immediately so a non-zero value is advised.

Must be a positive integer.

## "amplifier"

Defines how strong the effect is. Higher numbers increase the potency of the effect further.

Optional. Default: 0. 

The default means no amplification, and will do the standard effect.

Must be a positive integer or zero.

## "ambient"

Defines if the effect should be considered an ambient effect such as coming from a beacon. Ambient effects appear
in a different color and without a countdown. It is advised not to set the "ambient" property to true for foods and other
contextual effects.

Optional. Default: false.

Must be a boolean (`false` or `true`).

## "visible"

Defines if the effect is visible. Invisible effects by default have no icon or particles, and don't appear in the list.

Optional. Default: true.

Must be a boolean (`false` or `true`).

## "show_particles"

Defines if the effect produces particles around the player.

Optional. Default: same as "visible".

Must be a boolean (`false` or `true`).

## "show_icon"

Defines if the effect has an icon in the top right of the HUD.

Optional. Default: same as "visible".

Must be a boolean (`false` or `true`).
