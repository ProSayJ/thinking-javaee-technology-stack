package prosayj.framework.monitor.domain.server;


import prosayj.framework.common.utils.BigDecimalUtil;
import prosayj.framework.common.utils.DateUtil;

import java.lang.management.ManagementFactory;

/**
 * JVM相关信息
 *
 * @author ProSayJ
 */
public class Jvm {
    /**
     * 当前JVM占用的内存总数(M)
     */
    private double total;

    /**
     * JVM最大可用内存总数(M)
     */
    private double max;

    /**
     * JVM空闲内存(M)
     */
    private double free;

    /**
     * JDK版本
     */
    private String version;

    /**
     * JDK路径
     */
    private String home;

    public double getTotal() {
        return BigDecimalUtil.div(total, (1024 * 1024), 2);
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getMax() {
        return BigDecimalUtil.div(max, (1024 * 1024), 2);
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getFree() {
        return BigDecimalUtil.div(free, (1024 * 1024), 2);
    }

    public void setFree(double free) {
        this.free = free;
    }

    public double getUsed() {
        return BigDecimalUtil.div(total - free, (1024 * 1024), 2);
    }

    public double getUsage() {
        return BigDecimalUtil.mul(BigDecimalUtil.div(total - free, total, 4), 100);
    }

    /**
     * 获取JDK名称
     */
    public String getName() {
        return ManagementFactory.getRuntimeMXBean().getVmName();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    /**
     * JDK启动时间
     */
    public String getStartTime() {
        return DateUtil.parseDateToStr(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.getServerStartDate());
    }

    /**
     * JDK运行时间
     */
    public String getRunTime() {
        return DateUtil.getDatePoor(DateUtil.getNowDate(), DateUtil.getServerStartDate());
    }
}
