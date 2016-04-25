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
package com.github.dandelion.datatables.thymeleaf.processor.el;

import static com.github.dandelion.datatables.thymeleaf.dialect.DataTablesDialect.hasDatatablesAttribute;
import static com.github.dandelion.datatables.thymeleaf.dialect.DataTablesDialect.removeDatatablesAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Text;
import org.thymeleaf.processor.IElementNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;

import com.github.dandelion.datatables.core.export.ReservedFormat;
import com.github.dandelion.datatables.core.html.HtmlColumn;
import com.github.dandelion.datatables.core.html.HtmlRow;
import com.github.dandelion.datatables.core.html.HtmlTable;
import com.github.dandelion.datatables.thymeleaf.processor.AbstractElProcessor;
import com.github.dandelion.datatables.thymeleaf.util.AttributeUtils;

/**
 * <p>
 * Element processor applied to the {@code td} tag. Whenever Thymeleaf meets a
 * {@code td} tag, a {@link HtmlColumn} is added to the last added
 * {@link HtmlRow}.
 * </p>
 * <p>
 * Important note : the unique goal of this processor is to fill the
 * {@link HtmlTable} bean in order to make it exportable.
 * </p>
 * 
 * @author Thibault Duchateau
 */
public class TdElProcessor extends AbstractElProcessor {

   private static Logger logger = LoggerFactory.getLogger(TdElProcessor.class);

   public TdElProcessor(IElementNameProcessorMatcher matcher) {
      super(matcher);
   }

   @Override
   public int getPrecedence() {
      return 4002;
   }

   @Override
   protected ProcessorResult doProcessElement(Arguments arguments, Element element, HttpServletRequest request,
         HttpServletResponse response, HtmlTable htmlTable) {

      if (htmlTable != null) {

         HtmlColumn column = null;
         String content = null;

         if (hasDatatablesAttribute(element, "csv")
               || hasDatatablesAttribute(element, "xml")
               || hasDatatablesAttribute(element, "pdf")
               || hasDatatablesAttribute(element, "xls")
               || hasDatatablesAttribute(element, "xlsx")) {

            if (hasDatatablesAttribute(element, "csv")) {
               content = AttributeUtils.parseDatatablesAttribute(arguments, element, "csv",
                     String.class);
               removeDatatablesAttribute(element, "csv");
               column = new HtmlColumn(ReservedFormat.CSV);
               column.setContent(new StringBuilder(content));
               htmlTable.getLastBodyRow().addColumn(column);
            }
            if (hasDatatablesAttribute(element, "xml")) {
               content = AttributeUtils.parseDatatablesAttribute(arguments, element, "xml",
                     String.class);
               removeDatatablesAttribute(element, "xml");
               column = new HtmlColumn(ReservedFormat.XML);
               column.setContent(new StringBuilder(content));
               htmlTable.getLastBodyRow().addColumn(column);
            }
            if (hasDatatablesAttribute(element, "pdf")) {
               content = AttributeUtils.parseDatatablesAttribute(arguments, element, "pdf",
                     String.class);
               removeDatatablesAttribute(element, "pdf");
               column = new HtmlColumn(ReservedFormat.PDF);
               column.setContent(new StringBuilder(content));
               htmlTable.getLastBodyRow().addColumn(column);
            }
            if (hasDatatablesAttribute(element, "xls")) {
               content = AttributeUtils.parseDatatablesAttribute(arguments, element, "xls",
                     String.class);
               removeDatatablesAttribute(element, "xls");
               column = new HtmlColumn(ReservedFormat.XLS);
               column.setContent(new StringBuilder(content));
               htmlTable.getLastBodyRow().addColumn(column);
            }
            if (hasDatatablesAttribute(element, "xlsx")) {
               content = AttributeUtils.parseDatatablesAttribute(arguments, element, "xlsx",
                     String.class);
               removeDatatablesAttribute(element, "xlsx");
               column = new HtmlColumn(ReservedFormat.XLSX);
               column.setContent(new StringBuilder(content));
               htmlTable.getLastBodyRow().addColumn(column);
            }
         }
         // If the element contains a Text node, the content of the text node
         // will be displayed in all formats
         else if (element.getFirstChild() instanceof Text) {
            htmlTable.getLastBodyRow().addColumn(((Text) element.getFirstChild()).getContent().trim());
         }
         // Otherwise, an empty cell will be displayed
         else {
            logger.warn("Only cells containing plain text are supported, those containing HTML code are still not!");
            htmlTable.getLastBodyRow().addColumn("");
         }
      }

      // Remove internal attribute
      if (hasDatatablesAttribute(element, "data")) {
         removeDatatablesAttribute(element, "data");
      }

      return ProcessorResult.OK;
   }
}