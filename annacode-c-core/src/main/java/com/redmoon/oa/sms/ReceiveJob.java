package com.redmoon.oa.sms;

import cn.js.fan.util.StrUtil;
import com.cloudwebsoft.framework.util.LogUtil;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ReceiveJob implements Job {

    public ReceiveJob() {
    }

    public void receiveSms() {
        try {
            IMsgUtil imu = SMSFactory.getMsgUtil();
            imu.receive();
        } catch (Exception e) {
            LogUtil.getLog(getClass()).error("receiveSms:" + e.getMessage());
        }
    }

    /**
     * execute
     *
     * @param jobExecutionContext JobExecutionContext
     * @throws JobExecutionException
     * @todo Implement this org.quartz.Job method
     */
    public void execute(JobExecutionContext jobExecutionContext) throws
            JobExecutionException {
        JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
        try {
            receiveSms();

            // System.out.println(getClass() + " sql=" + System.currentTimeMillis());

        } catch (Exception e) {
            LogUtil.getLog(getClass()).error("execute:" + e.getMessage());
            LogUtil.getLog(getClass()).error(StrUtil.trace(e));
        }
    }
}
