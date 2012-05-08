package org.jcoderz.phoenix.sonar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;
import org.sonar.api.rules.Violation;

public class JcReportDecorator implements Decorator 
{
    public static final Logger LOGGER = LoggerFactory.getLogger(JcReportDecorator.class.getName());
    
    public boolean shouldExecuteOnProject(Project project) 
    {
      return ResourceUtils.isProject(project);
    }

    public void decorate(final Resource resource, final DecoratorContext context) 
    {
        for (Violation violation : context.getViolations())
        {
            LOGGER.warn("VIOLATION " + violation.getRule().getKey() + " at " 
                + violation.getResource().getQualifier() + violation.getLineId());
        }
    }
}
