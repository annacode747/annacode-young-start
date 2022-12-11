package com.redmoon.oa.pvg;

import cn.js.fan.base.ObjectCache;

public class OnlineUserCache extends ObjectCache {
    public OnlineUserCache(OnlineUserDb ou) {
        super(ou);
    }

    public int getAllCount() {
        int count = getObjectCount(OnlineUserDb.ALLCOUNTSQL);
        return count;
    }

}
