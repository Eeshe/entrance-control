# EntranceControl

EntranceControl is a plugin that allows players to create a selection of entrances (doors, trapdoors and fence gates) to
lock, sync and protect them.

## Features

- Easy selection of entrances.
- Modular settings for lock, sync and protect each selection.
- Highly configurable.
- Folia support.
- WorldGuard integration.

## Commands and Permissions

| **Command**                                      | **Description**                                                                  | **Permission**                      |
|--------------------------------------------------|----------------------------------------------------------------------------------|-------------------------------------|
| `/entrancecontrol`                               | Base command of the plugin.                                                      | `entrancecontrol.base`              |
| `/entrancecontrol lock [SelectionID]`            | Starts an entrance selection.                                                    | `entrancecontrol.lock`              |
| `/entrancecontrol manager`                       | Opens the entrance selection manager menu.                                       | `entrancecontrol.manager`           |
| `/entrancecontrol open <SelectionID>`            | Opens all the entrances of the specified entrance selection.                     | `entrancecontrol.open`              |
| `/entrancecontrol close <SelectionID>`           | Closes all the entrances of the specified entrance selection.                    | `entrancecontrol.close`             |
| `/entrancecontrol identify`                      | Starts an entrance selection identification session.                             | `entrancecontrol.identify`          |
| `/entrancecontrol delete <Player> <SelectionID>` | Deletes the specified entrance selection from the indicated player.              | `entrancecontrol.delete`            |
| `/entrancecontrol reload`                        | Reloads the configurations of the plugin.                                        | `entrancecontrol.reload`            |
| `/entrancecontrol help`                          | Displays a list of the commands of the plugin.                                   | `entrancecontrol.help`              |
|                                                  | Allows players to open locked entrance selections without being members of them. | `entrancecontrol.bypass.lock`       |
|                                                  | Allows players to break entrance selections without being members of them.       | `entrancecontrol.bypass.protection` |

## Configurations

| **Configuration**                 | **Description**                                                                                                  | **Example**                                                                                                     |
|-----------------------------------|------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| world-blacklist                   | List of worlds EntranceControl will handle actions in.                                                           | world-blacklist: <br/>- world_the_end                                                                           |
| region-blacklist                  | List of WorldGuard regions EntranceControl will handle actions in.                                               | region-blacklist: <br/>- region1 <br/>- region2                                                                 |
| maximum-entrance-selection-amount | Permission and default settings for the amount of entrance selections players can have.                          | maximum-entrance-selection-amount:<br/>permission: entrancecontrol.maximum_entrance_selections.</br>default: 10 |
| maximum-selected-entrances        | Permission and default settings for the amount of entrances players can select in an entrance selection.         | maximum-selected-entrances:<br/>permission: entrancecontrol.maximum_selected_entrances.</br>default: 4          |
| maximum-selection-distance        | Permission and default settings for the maximum allowed distance between all entrances in an entrance selection. | maximum-selection-distance:<br/>permission: entrancecontrol.maximum_selection_distance.</br>default: 20         |
| break-protection                  | Whether the plugin should protect entrance selections from griefing.                                             | break-protection: true                                                                                          |

Messages, sounds and particles from the plugin are also configurable. For instructions for how to configure this,
check the [PenPenLib](https://github.com/Eeshe/pen-pen-lib) guide.

## Dependencies

- [PenPenLib](https://github.com/Eeshe/pen-pen-lib).

## Soft Dependencies

- [WorldGuard](https://dev.bukkit.org/projects/worldguard): Used to check for blacklisted search regions.

## Installation

1. Download the latest version of [PenPenLib](https://github.com/Eeshe/pen-pen-lib) and EntranceControl JAR files.
2. Drop the JAR files into the `plugins` folder of your Spigot/Paper server.
3. Restart the server.

## Compatibility

EntranceControl is compatible with Minecraft versions 1.19.4 and later. Compatibility with older versions is not
currently
planned, as Folia's oldest supported version is 1.19.4.

## Contributing

Contributions to EntranceControl are welcome! If you encounter any issues or have suggestions for improvements, please
open
an issue on the GitHub repository. To contribute code:

1. Fork the repository.
2. Create a new branch for your feature/fix.
3. Commit your changes.
4. Open a pull request and assign me as the reviewer.

## License

EntranceControl is licensed under the [MIT License](LICENSE).
