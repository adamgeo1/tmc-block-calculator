package tmc.block_calculator.client.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import java.util.List;

@Config(name = "trappedmc-block-calculator")
public class ModConfig implements ConfigData {
	public String serverAllowlistRegex = "(?i).*trappedmc.*";

	public PriceMode priceMode = PriceMode.X1;

	public TooltipPosition tooltipPosition = TooltipPosition.RIGHT;

	public List<String> sellableItems = DefaultItemsLoader.loadAsEntries();
}
