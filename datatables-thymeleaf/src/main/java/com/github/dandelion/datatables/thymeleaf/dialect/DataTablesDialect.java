/*
 * [The "BSD licence"]
 * Copyright (c) 2013-2015 Dandelion
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of Dandelion nor the names of its contributors 
 * may be used to endorse or promote products derived from this software 
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.dandelion.datatables.thymeleaf.dialect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.AttributeNameProcessorMatcher;
import org.thymeleaf.processor.ElementNameProcessorMatcher;
import org.thymeleaf.processor.IProcessor;

import com.github.dandelion.core.util.StringUtils;
import com.github.dandelion.datatables.thymeleaf.processor.config.DivConfAttrProcessor;
import com.github.dandelion.datatables.thymeleaf.processor.config.DivConfTypeAttrProcessor;
import com.github.dandelion.datatables.thymeleaf.processor.el.ColumnFinalizerProcessor;
import com.github.dandelion.datatables.thymeleaf.processor.el.ColumnInitializerElProcessor;
import com.github.dandelion.datatables.thymeleaf.processor.el.DivExtraHtmlFinalizerElProcessor;
import com.github.dandelion.datatables.thymeleaf.processor.el.TableFinalizerElProcessor;
import com.github.dandelion.datatables.thymeleaf.processor.el.TableInitializerElProcessor;
import com.github.dandelion.datatables.thymeleaf.processor.el.TbodyElProcessor;
import com.github.dandelion.datatables.thymeleaf.processor.el.TdElProcessor;
import com.github.dandelion.datatables.thymeleaf.processor.el.TheadElProcessor;
import com.github.dandelion.datatables.thymeleaf.processor.el.TrElProcessor;

/**
 * The Dandelion-datatables dialect.
 * 
 * @author Thibault Duchateau
 */
public class DataTablesDialect extends AbstractDialect {

   private static final String DIALECT_PREFIX = "dt";
   public static final String LAYOUT_NAMESPACE = "http://www.thymeleaf.org/dandelion/datatables";
   public static final int DT_HIGHEST_PRECEDENCE = 3500;
   public static final int DT_DEFAULT_PRECEDENCE = 8000;

   public static final String INTERNAL_BEAN_TABLE = "htmlTable";
   public static final String INTERNAL_BEAN_TABLE_CONFIGURATION = "tableConfiguration";
   public static final String INTERNAL_BEAN_CONFIGS = "configs";
   public static final String INTERNAL_NODE_CONFIG = "nodeConfig";
   public static final String INTERNAL_NODE_TABLE = "tableNode";
   public static final String INTERNAL_CONF_GROUP = "confGroup";
   public static final String INTERNAL_BEAN_TABLE_STAGING_OPTIONS = "tableStagingOptions";
   public static final String INTERNAL_BEAN_COLUMN_STAGING_OPTIONS = "columnStagingOptions";
   public static final String INTERNAL_BEAN_COLUMN_STAGING_EXTENSIONS = "columnStagingExtensions";

   public String getPrefix() {
      return DIALECT_PREFIX;
   }

   public boolean isLenient() {
      return false;
   }

   /**
    * @return all processors contained inside the Dandelion-Datatables dialect.
    */
   @Override
   public Set<IProcessor> getProcessors() {
      final Set<IProcessor> processors = new HashSet<IProcessor>();

      // Element processors
      processors.add(new TableInitializerElProcessor(
            new ElementNameProcessorMatcher("table", getDatatableAtributesByNameFilter("table", "true"), false)));
      processors.add(new TableFinalizerElProcessor(
            new ElementNameProcessorMatcher("div", getXMLDatatablesAttribute("tmp"), "internalUse", false)));
      processors.add(new TheadElProcessor(
            new ElementNameProcessorMatcher("thead", getXMLDatatablesAttribute("data"), "internalUse", false)));
      processors.add(new TbodyElProcessor(
            new ElementNameProcessorMatcher("tbody", getXMLDatatablesAttribute("data"), "internalUse", false)));
      processors.add(new ColumnInitializerElProcessor(
            new ElementNameProcessorMatcher("th", getXMLDatatablesAttribute("data"), "internalUse", false)));
      processors.add(new ColumnFinalizerProcessor(
            new ElementNameProcessorMatcher("th", getXMLDatatablesAttribute("data"), "internalUse", false)));
      processors.add(
            new TrElProcessor(new ElementNameProcessorMatcher("tr", getXMLDatatablesAttribute("data"), "internalUse", false)));
      processors.add(
            new TdElProcessor(new ElementNameProcessorMatcher("td", getXMLDatatablesAttribute("data"), "internalUse", false)));

      // Config processors
      processors.add(new DivConfAttrProcessor(new AttributeNameProcessorMatcher("conf", "div")));
      processors.add(new DivConfTypeAttrProcessor(new AttributeNameProcessorMatcher("confType", "div")));
      processors.add(new DivExtraHtmlFinalizerElProcessor(
            new ElementNameProcessorMatcher("div", DIALECT_PREFIX + ":tmp", "internalUseExtraHtml", false)));

      // Table attribute processors
      for (TableAttrProcessors processor : TableAttrProcessors.values()) {
         processors.add(processor.getProcessor());
      }

      // Column attribute processors
      for (ColumnAttrProcessors processor : ColumnAttrProcessors.values()) {
         processors.add(processor.getProcessor());
      }

      return processors;
   }

	public static String getXMLDatatablesAttribute(String action) {
		return DIALECT_PREFIX + ":" + action;
	}

	public static String getHTML5DatatablesAttribute(String action) {
		return "data-" + DIALECT_PREFIX + "-" + action;
	}

	private static boolean hasAttribute(Element element, String attribute) {
		return (element.hasAttribute(attribute) && !StringUtils.isBlank(element.getAttributeValue(attribute)));
	}

	public static boolean hasDatatablesAttribute(Element element, String action) {
		return hasAttribute(element, getXMLDatatablesAttribute(action))
				|| hasAttribute(element, getHTML5DatatablesAttribute(action));
	}
	
	public static String getDatatablesAttributeValue(Element element, String action) {
		String attribute = getXMLDatatablesAttribute(action);
		String value = element.getAttributeValue(attribute);
		if (value == null) {
			attribute = getHTML5DatatablesAttribute(action);
			value = element.getAttributeValue(attribute);
		}
		return value;
	}
	
	public static Map<String, String> getDatatableAtributesByNameFilter(String action, String value) {
		Map<String, String> filter = new HashMap<String, String>(2);
		filter.put(getXMLDatatablesAttribute(action), value);
		filter.put(getHTML5DatatablesAttribute(action), value);
		return filter;
	}
	
	public static void removeDatatablesAttribute(Element element, String action) {
		element.removeAttribute(getXMLDatatablesAttribute(action));
		element.removeAttribute(getHTML5DatatablesAttribute(action));
	}

	public static void removeDatatablesAttributeIfExists(Element element, String action) {
		if (hasDatatablesAttribute(element, action)) {
			element.removeAttribute(getXMLDatatablesAttribute(action));
			element.removeAttribute(getHTML5DatatablesAttribute(action));
		}
	}

}