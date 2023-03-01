/*
 * Copyright (c) 2004-2022, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.nmcpye.am.system.template;

import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

@Component("org.nmcpye.am.system.template.TemplateManager")
public class TemplateManager {
    public static final String CONTEXT_KEY = "object";

//    private static final String RESOURCE_LOADER_NAME = "class";
//
//    private static final String VM_SUFFIX = ".vm";

    private SpringTemplateEngine templateEngine;

//    private VelocityEngine velocityEngine;

    private SpringResourceTemplateResolver templateResolver;

    public TemplateManager() {
        // NMCP
        this.templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        // HTML is the default value, added here for the sake of clarity.
        templateResolver.setTemplateMode(TemplateMode.HTML);
        // Template cache is true by default. Set to false if you want
        // templates to be automatically updated when modified.
        templateResolver.setCacheable(true);
        // SpringTemplateEngine automatically applies SpringStandardDialect and
        // enables Spring's own MessageSource message resolution mechanisms.
        templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(this.templateResolver);
        // Enabling the SpringEL compiler with Spring 4.2.4 or newer can
        // speed up execution in most scenarios, but might be incompatible
        // with specific cases when expressions in one template are reused
        // across different data types, so this flag is "false" by default
        // for safer backwards compatibility.
        templateEngine.setEnableSpringELCompiler(true);
        /////////////////////////////////////
//        velocityEngine = new VelocityEngine();
//        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, RESOURCE_LOADER_NAME);
//        velocityEngine.setProperty(RESOURCE_LOADER_NAME + ".resource.loader.class",
//            ClasspathResourceLoader.class.getName());
//        velocityEngine.setProperty("runtime.log.logsystem.log4j.logger", "console");
//        velocityEngine.setProperty("runtime.log", "");
//
//        velocityEngine.init();
    }

//    public VelocityEngine getEngine() {
//        return velocity;
//    }

    public String render(String template) {
        return render(null, template);
    }

    public String render(Object object, String template) {
        try {
//            StringWriter writer = new StringWriter();

            Context context = new Context();

            if (object != null) {
//                context.put(CONTEXT_KEY, object);
                context.setVariable(CONTEXT_KEY, object);
            }

//            velocityEngine.getTemplate(template + VM_SUFFIX).merge(context, writer);

            return templateEngine.process(template, context);

            // TODO include encoder in context
        } catch (Exception ex) {
            throw new RuntimeException("Failed to merge velocity template", ex);
        }
    }
}
