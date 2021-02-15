package prosayj.framework.monitor.domain.server;


import prosayj.framework.common.utils.BigDecimalUtil;

/**
 * CPU相关信息
 *
 * @author yangjian
 */
public class Cpu {
    /**
     * 核心数
     */
    private int cpuNum;

    /**
     * CPU总的使用率
     */
    private double total;

    /**
     * CPU系统使用率
     */
    private double sys;

    /**
     * CPU用户使用率
     */
    private double used;

    /**
     * CPU当前等待率
     */
    private double wait;

    /**
     * CPU当前空闲率
     */
    private double free;

    public int getCpuNum() {
        return cpuNum;
    }

    public void setCpuNum(int cpuNum) {
        this.cpuNum = cpuNum;
    }

    public double getTotal() {
        return BigDecimalUtil.round(BigDecimalUtil.mul(total, 100), 2);
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getSys() {
        return BigDecimalUtil.round(BigDecimalUtil.mul(sys / total, 100), 2);
    }

    public void setSys(double sys) {
        this.sys = sys;
    }

    public double getUsed() {
        return BigDecimalUtil.round(BigDecimalUtil.mul(used / total, 100), 2);
    }

    public void setUsed(double used) {
        this.used = used;
    }

    public double getWait() {
        return BigDecimalUtil.round(BigDecimalUtil.mul(wait / total, 100), 2);
    }

    public void setWait(double wait) {
        this.wait = wait;
    }

    public double getFree() {
        return BigDecimalUtil.round(BigDecimalUtil.mul(free / total, 100), 2);
    }

    public void setFree(double free) {
        this.free = free;
    }
}
