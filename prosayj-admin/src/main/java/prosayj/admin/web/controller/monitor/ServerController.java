package prosayj.admin.web.controller.monitor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prosayj.framework.common.core.domain.AjaxResult;
import prosayj.framework.monitor.MonitorServerDto;

/**
 * 服务器监控
 */
@RestController
@RequestMapping("/monitor/server")
public class ServerController {
    @PreAuthorize("@ss.hasPermi('monitor:server:list')")
    @GetMapping()
    public AjaxResult getInfo() throws Exception {
        MonitorServerDto monitorServerDto = new MonitorServerDto();
        monitorServerDto.copyTo();
        return AjaxResult.success(monitorServerDto);
    }
}
