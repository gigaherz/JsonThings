# Fluid Factories

Fluids come in many... well 2 types, by default. Basic, and in-world (flowing).

To support flowing fluids and other custom fluid implementations, the fluid types provide extra values that can be specified,
and trigger other behaviours.

More types can be added in the future as needed.

## "plain"

Default fluid type. Can only exist in containers. Cannot be placed in the world.

No default fluidstate properties.

## "flowing"

A block that can be placed in multiple cardinal directions, including up and down.

Can be placed in the world.

Default fluidstate properties: level (required, cannot be removed)

Flowing fluids have some extra parameters that don't exist for plain fluids.

```json
{
  ...
  "block": true,
  "can_convert_to_source": false,
  "slope_distance": 4,
  "dropoff": 1,
  "tick_delay": 5,
  "explosion_resistance": 100
}
```

### "block"

Defines the fluids's placeable block.

Optional. Default: no block. If not used, the fluid will not be able to exist in the world.

Can be one of 2 types:
* A Boolean: If `true`, the fluid block will have all the default properties.
* A json object (`{}`) containing the definition of a block, as seen in the [Blocks](./Blocks.md) page.

### "can_convert_to_source"

Defines if the block can form new source blocks when 2 sources are spaced 1 block apart.

Optional. Default: false (no source creation).

Must be a boolean (`false` or `true`).

### "slope_distance"

Defines how far the game will scan when looking for holes the fluid can flow toward.

Optional. Default: 4.

Must be an integer number bigger than zero.

### "dropoff"

Defines how much the level drops off by for each blow the fluid spreads out from its source.

Optional. Default: 1.

Must be an integer number between 1 and 8.

### "tick_delay"

Defines how much time the game waits between fluid updates.

Optional. Default: 5.

Must be an integer bigger than or equal to zero.

### "explosion_resistance"

Defines how much explosions weaken when passing through the fluid blocks.

Optional. Default: 100.

Must be a number.
