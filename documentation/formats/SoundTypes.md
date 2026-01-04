# Sound Type Definitions

EXPERIMENTAL.

Sound Type definitions let you specify a new set of sounds for a new block material.

Sound Type definitions go in the `sound_type` directory in the thing pack.

E.g.
```
/things/examplepack/sound_type/scream.json
```

## Basic structure of the JSON file

```json
{
  "volume": 1.0,
  "pitch": 1.0,
  "break_sound": "sound:location",
  "step_sound": "sound:location",
  "hit_sound": "sound:location",
  "fall_sound": "sound:location"
}
```

## "volume"

Defines how loud the sound is.

Optional. Default: 1.0.

Must be a number between 0 and 1.

## "pitch"

Defines the pitch of the sound.

Optional. Default: 1.0.

Must be a number bigger than zero. 1.0 uses the original pitch in the sound file. 

## "break_sound"

Defines which [Sound Event](./SoundEvents.md) to use for when the block breaks.

Required.

Must be a resource location string like `"block.anvil.break"`, or `"minecraft:block.amethyst_cluster.break"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "step_sound"

Defines which [Sound Event](./SoundEvents.md) to use for when something steps on the block.

Required.

Must be a resource location string like `"block.anvil.break"`, or `"minecraft:block.amethyst_cluster.break"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "hit_sound"

Defines which [Sound Event](./SoundEvents.md) to use for when a player or entity hits the block with their hands or a tool.

Required.

Must be a resource location string like `"block.anvil.break"`, or `"minecraft:block.amethyst_cluster.break"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "fall_sound"

Defines which [Sound Event](./SoundEvents.md) to use for when something falls on the block.

Required.

Must be a resource location string like `"block.anvil.break"`, or `"minecraft:block.amethyst_cluster.break"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.
