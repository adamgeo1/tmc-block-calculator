package tmc.block_calculator.client.config;

import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;

public enum TooltipPosition implements SelectionListEntry.Translatable {
	LEFT,
	RIGHT;

	@Override
	public String getKey() {
		return "text.autoconfig.trappedmc-block-calculator.option.tooltipPosition." + name();
	}
}
