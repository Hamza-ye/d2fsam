package org.nmcpye.am.web.rest;

import org.nmcpye.am.common.OpenApi;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@OpenApi.Ignore
@Controller
public class ClientForwardController {

    /**
     * Forwards any unmapped paths (except those containing a period) to the client {@code index.html}.
     * @return forward to client {@code index.html}.
     */
    @GetMapping(value = { "/{path:[^\\.]*}", "/{path:^(?!websocket).*}/**/{path:[^\\.]*}" })
    public String forward() {
        return "forward:/";
    }
}
