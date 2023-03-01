package org.nmcpye.am.dxf2.events.importer.shared.validation;

import org.nmcpye.am.dxf2.events.importer.Checker;
import org.nmcpye.am.dxf2.events.importer.context.WorkContext;
import org.nmcpye.am.dxf2.events.importer.shared.ImmutableEvent;
import org.nmcpye.am.dxf2.importsummary.ImportSummary;
import org.springframework.stereotype.Component;

@Component
public class SharedActivityCheck implements Checker {
    @Override
    public ImportSummary check(ImmutableEvent event, WorkContext ctx) {
        return checkNull(ctx.getActivitiesMap().get(event.getActivity()),
            "Event.activity does not point to a valid activity: " + event.getActivity(), event);
    }
}
