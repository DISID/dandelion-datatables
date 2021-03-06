package com.github.dandelion.datatables.core.extension.plugin;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import com.github.dandelion.core.web.AssetRequestContext;
import com.github.dandelion.datatables.core.extension.AbstractExtensionTest;
import com.github.dandelion.datatables.core.extension.Extension;
import com.github.dandelion.datatables.core.generator.DTConstants;
import com.github.dandelion.datatables.core.option.DatatableOptions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class ScrollerPluginTest extends AbstractExtensionTest {

	@Test
	public void shoud_load_the_extension() {
		extensionProcessor.process(new HashSet<Extension>(Arrays.asList(new ScrollerPlugin())));

		assertThat(AssetRequestContext.get(table.getTableConfiguration().getRequest()).getBundles(true)).hasSize(1);
		assertThat(mainConfig).hasSize(1);
		assertThat(mainConfig).contains(entry(DTConstants.DT_DOM, "frtiS"));
	}

	@Test
	public void shoud_load_the_extension_with_already_configured_dom() {
		DatatableOptions.FEATURE_DOM.setIn(table.getTableConfiguration().getOptions(), "lfr");
		mainConfig.put(DTConstants.DT_DOM, "lfr");
		extensionProcessor.process(new HashSet<Extension>(Arrays.asList(new ScrollerPlugin())));

		assertThat(AssetRequestContext.get(table.getTableConfiguration().getRequest()).getBundles(true)).hasSize(1);
		assertThat(mainConfig).hasSize(1);
		assertThat(mainConfig).contains(entry(DTConstants.DT_DOM, "lfrS"));
	}
	
	@Test
	public void shoud_load_the_extension_with_jqueryui_enabled() {
		DatatableOptions.FEATURE_JQUERYUI.setIn(table.getTableConfiguration().getOptions(), true);
		mainConfig.put(DTConstants.DT_DOM, "lfr");
		extensionProcessor.process(new HashSet<Extension>(Arrays.asList(new ScrollerPlugin())));

		assertThat(AssetRequestContext.get(table.getTableConfiguration().getRequest()).getBundles(true)).hasSize(1);
		assertThat(mainConfig).hasSize(1);
		assertThat(mainConfig).contains(entry(DTConstants.DT_DOM, "<\"H\"lfr>t<\"F\"ip>S"));
	}
}