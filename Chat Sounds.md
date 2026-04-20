# Overview
The mod allows for specific sounds to play when users send a message in chat. 
## Configuration
Configuration for which sounds will play is stored in the `deeperdark.yaml` file. The yaml file contains entries of which players play which sounds. If a player is not mentioned in the list here, then when they send a message, a sound doesn't play. 

An example of the Chat Sounds configuration portion of the yaml file looks like this:
```yaml
chatSounds:
    FinniTheFox:
        sendMessageSound: entity.cat.ambient
        deathMessageSound: entity.cat.death
        joinMessageSound: entity.cat.stray_ambient
        pitch: 2.0
        pitchDeviance: 0.2
    snotbane:
        sendMessageSound: entity.parrot.ambient
        deathMessageSound: entity.parrot.death
        joinMessageSound: entity.parrot.ambient
        pitch: 0.5
        pitchDeviance: 0.2
    NotaNaN:
        sendMessageSound: entity.ender_dragon.ambient
        deathMessageSound: entity.ender_dragon.death
        joinMessageSound: entity.ender_dragon.hurt
        pitch: 2.0
        pitchDeviance: 0.1
    The_Throngler:
        sendMessageSound: entity.snake.ambient
        deathMessageSound: entity.snake.death
        joinMessageSound: entity.snake.tongue
        pitch: 1.0
        pitchDeviance: 0.2
```

The configuration for a single player is driven by six variables.
- The Root - The PlayerName of the Player
- `sendMessageSound` - the id of the sound to play from the server by sending packets to clients. This utilizes the vanilla sound system, but allows for custom sounds to be used through resource packs, but does not depend on the registry, which would require the client to have the mod too. This is played when the player sends a message of any kind (excluding commands) in chat. 
- `deathMessageSound` - same principle as the `sendMessageSound` term, but played when the player dies.
- `joinMessageSound` - same principle as the `sendMessageSound` term, but played when the player joins the game.
- `pitch` - the base pitch at which all chat sounds relating to that player are played.
- `pitchDeviance` - the max positive or negative variance that is added onto the pitch for variability. For example, a 1.0 pitch with a 0.2 pitch variance would have a possible range of 0.8-1.2. 

## Behavior
The sounds played by the chatSounds system are played globally for all players, including the player who send the message.

## Clientside Configuration
Players who want to disable the client sounds will run the command:

`/ddclient chat_sounds false`

Running this command will add them to an exclusion list (stored in the config) which will exclude them from hearing the chat sounds/having the packets sent to them from the server. 