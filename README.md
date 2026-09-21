# TrappedMC Block Calculator

>[!NOTE] Intended for use on [TrappedMC](https://trappedmc.com/)

This mod adds a toggleable overlay to inventory screens to calculate the `/sellall` values for all the sellable items in the inventory. Values are gotten from the `/sellallfilter` menu in the server.

## Controls

Both controls need to be given keybinds in the controls menu

- Toggle Tooltip - Enables/Disables the Overlay
- Cycle Sell Price Mode - Cycles through the different sell booster modes: `1x` (normal mode), `1.15x`, and `1.3x`

## Config

Using Mod Menu, you can customize the overlay and what items appear in it

- Server Allowlist Regex - Default: `(?!).*trappedmc.*`
 - Mod is only enabled when on a server that has the IP matching the Regex. No reason to change unless you want to use the mod in singleplayer for whatever reason
- Price Mode - Default: `1x`
 - The last used sell multiplier used
- Tooltip Position - Default: Left
 - The position relative to the inventory GUI that the tooltip appears. Options are Left or Right
- Tooltip Enabled - Default: Yes
- Background Opacity - Default: 75
 - The opacity percentage of the tooltip background
- Sellable Items - Default: All items from `/sellallfilter`
 - Modifiable list of items that will be detected and calculated in the tooltip. Can adjust both items and values

## AI Disclaimer

This mod was developed with assistance of generative AI.

## License

This project is licensed under the [MIT License](LICENSE).
