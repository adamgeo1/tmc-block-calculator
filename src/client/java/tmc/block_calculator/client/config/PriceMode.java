package tmc.block_calculator.client.config;

import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;

public enum PriceMode implements SelectionListEntry.Translatable {
	X1(1.0, "1x"),
	X1_15(1.15, "1.15x"),
	X1_3(1.3, "1.3x");

	public final double multiplier;
	public final String label;

	PriceMode(double multiplier, String label) {
		this.multiplier = multiplier;
		this.label = label;
	}

	public PriceMode next() {
		PriceMode[] values = values();
		return values[(ordinal() + 1) % values.length];
	}

	@Override
	public String getKey() {
		return "text.autoconfig.trappedmc-block-calculator.option.priceMode." + name();
	}
}
