# Block Set Type Definitions

EXPERIMENTAL.

Sound Type definitions let you specify a new set of sounds for a new block material.

Sound Type definitions go in the `sound_type` directory in the thing pack.

E.g.
```
/things/examplepack/block_set_type/scream.json
```

## Basic structure of the JSON file

```json
{
  "sound_type": "some:sound_type",
  "door_close": "some:sound_event",
  "door_open": "some:sound_event",
  "trapdoor_close": "some:sound_event",
  "trapdoor_open": "some:sound_event",
  "pressure_plate_off": "some:sound_event",
  "pressure_plate_on": "some:sound_event",
  "button_off": "some:sound_event",
  "button_on": "some:sound_event",
  "is_wood": true,
  "hanging_sign_sound_type": "some:sound_type",
  "fence_gate_close": "some:sound_event",
  "fence_gate_open": "some:sound_event",
  "can_open_by_hand": true,
  "can_open_by_wind_charge": true,
  "can_botton_be_activated_by_arrows": true,
  "pressure_plate_sensitivity":"mobs"
}
```

## "sound_type"

Defines which [Sound Type](./SoundTypes.md) the block set will use.

Optional. Default: wood.

Must be a resource location string like `"wood"`, or `"minecraft:wood"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "door_close"

Defines which [Sound Event](./SoundEvents.md) to use when a door is closed.

Optional. Default: wooden_door_close.

Must be a resource location string like `"block.anvil.break"`, or `"minecraft:block.amethyst_cluster.break"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "door_open"

Defines which [Sound Event](./SoundEvents.md) to use when a door is opened.

Optional. Default: wooden_door_open.

Must be a resource location string like `"block.anvil.break"`, or `"minecraft:block.amethyst_cluster.break"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "trapdoor_close"

Defines which [Sound Event](./SoundEvents.md) to use when a trapdoor is closed.

Optional. Default: wooden_trapdoor_close.

Must be a resource location string like `"block.anvil.break"`, or `"minecraft:block.amethyst_cluster.break"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "trapdoor_open"

Defines which [Sound Event](./SoundEvents.md) to use when a trapdoor is opened.

Optional. Default: wooden_trapdoor_open.

Must be a resource location string like `"block.anvil.break"`, or `"minecraft:block.amethyst_cluster.break"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "pressure_plate_off"

Defines which [Sound Event](./SoundEvents.md) to use when a pressure plate is released.

Optional. Default: wooden_pressure_plate_click_off.

Must be a resource location string like `"block.anvil.break"`, or `"minecraft:block.amethyst_cluster.break"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "pressure_plate_on"

Defines which [Sound Event](./SoundEvents.md) to use when something steps on a pressure plate.

Optional. Default: wooden_pressure_plate_click_on.

Must be a resource location string like `"block.anvil.break"`, or `"minecraft:block.amethyst_cluster.break"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "button_off"

Defines which [Sound Event](./SoundEvents.md) to use when a button clicks off.

Optional. Default: wooden_button_click_off.

Must be a resource location string like `"block.anvil.break"`, or `"minecraft:block.amethyst_cluster.break"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "button_on"

Defines which [Sound Event](./SoundEvents.md) to use when a button is pressed.

Optional. Default: wooden_button_click_on.

Must be a resource location string like `"block.anvil.break"`, or `"minecraft:block.amethyst_cluster.break"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "is_wood"

Defines if the block set type represents a wood type.

Optional. Default: false.

Must be a boolean `true` or `false`.

## "hanging_sign_sound_type"

Defines which [Sound Type](./SoundTypes.md) the hanging sign will use.

Optional. Default: hanging_sign.

Must be a resource location string like `"wood"`, or `"minecraft:wood"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "fence_gate_close"

Defines which [Sound Event](./SoundEvents.md) to use when a fence gate closes.

Optional. Default: fence_gate_close.

Must be a resource location string like `"block.anvil.break"`, or `"minecraft:block.amethyst_cluster.break"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "fence_gate_open"

Defines which [Sound Event](./SoundEvents.md) to use when a fence gate opens.

Optional. Default: fence_gate_open.

Must be a resource location string like `"block.anvil.break"`, or `"minecraft:block.amethyst_cluster.break"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.
