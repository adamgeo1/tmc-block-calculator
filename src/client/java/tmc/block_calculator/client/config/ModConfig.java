package tmc.block_calculator.client.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.List;

@Config(name = "trappedmc-block-calculator")
public class ModConfig implements ConfigData {
	public String serverAllowlistRegex = "(?i).*trappedmc.*";

	public PriceMode priceMode = PriceMode.X1;

	public TooltipPosition tooltipPosition = TooltipPosition.LEFT;

	public boolean tooltipEnabled = true;

	@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
	public int backgroundOpacityPercent = 75;

	public List<String> sellableItems = DefaultItemsLoader.loadAsEntries();
}
