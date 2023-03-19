# Sound Event Definitions

EXPERIMENTAL.

Sound Event definitions let you define a new sound you can play.

Sound Event definitions go in the `sound_event` directory in the thing pack.

E.g.
```
/things/examplepack/sound_event/scream.json
```

## Basic structure of the JSON file

```json
{
  "range": 16
}
```

## "range"

Defines the maximum distance at which the sound can be heard.

Optional. Default: use legacy sound mechanics.

Must be a positive number.
