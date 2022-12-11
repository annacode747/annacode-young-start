package com.cloudwebsoft.framework.security;

import org.apache.log4j.Logger;

import java.io.Serializable;

public class ProtectUnit implements Serializable {
    transient Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * 包含
     */
    public static final int TYPE_INCLUDE = 0;

    /**
     * 正则
     */
    public static final int TYPE_REGULAR = 1;

    public ProtectUnit() {
    }

    public void renew() {
        if (logger == null) {
            logger = Logger.getLogger(this.getClass().getName());
        }
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getRule() {
        return rule;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    private String rule;
    private int type;
}
