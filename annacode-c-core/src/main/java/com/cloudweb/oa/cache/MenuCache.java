package com.cloudweb.oa.cache;

import cn.js.fan.cache.jcs.RMCache;
import cn.js.fan.web.Global;
import com.cloudweb.oa.base.ObjCache;
import com.cloudweb.oa.dcs.DistributedLock;
import com.cloudweb.oa.entity.Menu;
import com.cloudweb.oa.service.IMenuService;
import com.cloudweb.oa.utils.ConstUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.jcs.access.exception.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class MenuCache extends ObjCache {

    @Autowired
    IMenuService menuService;

    private final String cachePrix = "ch_";

    private static final Lock lock = new ReentrantLock();

    public MenuCache() {
    }

    @Override
    public Lock getLock() {
        return lock;
    }

    @Override
    public String getPrimaryKey(Object obj) {
        Menu menu = (Menu) obj;
        return menu.getCode();
    }

    @Override
    public Object getEmptyObjWithPrimaryKey(String value) {
        Menu menu = new Menu();
        menu.setCode(value);
        return menu;
    }

    @Override
    public Object getObjRaw(String key) {
        return menuService.getMenu(key);
    }

    public void removeAllFromCache() {
        try {
            RMCache.getInstance().invalidateGroup(group);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public List<Menu> getChildren(String parentCode) {
        List<Menu> list = null;
        boolean isNotExist = false;
        try {
            long t = System.currentTimeMillis();
            list = (List) RMCache.getInstance().getFromGroup(cachePrix + parentCode, group);
            if (Global.getInstance().isDebug()) {
                double s = (double) (System.currentTimeMillis() - t) / 1000;
                // log.info("???????????????????????????" + parentCode + " time:" + s + " s");
            }

            if (null != list) {
                // ?????????
                if (ConstUtil.CACHE_NONE.equals(list.get(0).getCode())) {
                    return new ArrayList<>();
                } else {
                    return list;
                }
            }

            boolean isLocked = false;
            String indentifier = "";
            // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            try {
                if (Global.isCluster() && Global.getInstance().isUseRedis()) {
                    indentifier = distributedLock.lock(getClass().getName(), 5000, 1000);
                    list = (List) RMCache.getInstance().getFromGroup(cachePrix + parentCode, group);
                    if (null == list) {
                        list = menuService.getChildren(parentCode);
                        if (list.size() == 0) {
                            // throw new RuntimeException("This data could not be empty. code=" + code);
                            // ?????????null??????menu?????????????????????menu.name??? &&??????????????????????????????5??????
                            isNotExist = true;
                            Menu menu = new Menu();
                            menu.setCode(ConstUtil.CACHE_NONE);
                            list.add(menu);
                            RMCache.getInstance().putInGroup(cachePrix + parentCode, group, list, ConstUtil.CACHE_NONE_EXPIRE);
                        } else {
                            RMCache.getInstance().putInGroup(cachePrix + parentCode, group, list);
                        }
                    } else {
                        if (ConstUtil.CACHE_NONE.equals(list.get(0).getCode())) {
                            isNotExist = true;
                        }
                    }
                } else {
                    if (lock.tryLock()) {
                        isLocked = true;
                        list = (List) RMCache.getInstance().getFromGroup(cachePrix + parentCode, group);
                        if (null == list) {
                            list = menuService.getChildren(parentCode);
                            if (list.size() == 0) {
                                // throw new RuntimeException("This data could not be empty. code=" + code);
                                // ?????????null??????menu?????????????????????menu.name??? &&??????????????????????????????5??????
                                isNotExist = true;
                                Menu menu = new Menu();
                                menu.setCode(ConstUtil.CACHE_NONE);
                                list.add(menu);
                                RMCache.getInstance().putInGroup(cachePrix + parentCode, group, list, ConstUtil.CACHE_NONE_EXPIRE);
                            } else {
                                RMCache.getInstance().putInGroup(cachePrix + parentCode, group, list);
                            }
                        } else {
                            if (ConstUtil.CACHE_NONE.equals(list.get(0).getCode())) {
                                isNotExist = true;
                            }
                        }
                    } else {
                        TimeUnit.MILLISECONDS.sleep(200);
                        list = getChildren(parentCode);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (CacheException e) {
                e.printStackTrace();
            } finally {
                if (Global.isCluster() && Global.getInstance().isUseRedis()) {
                    distributedLock.unlock(getClass().getName(), indentifier);
                } else {
                    if (isLocked) {
                        lock.unlock();
                    }
                }
            }

            if (Global.getInstance().isDebug()) {
                double s = (double) (System.currentTimeMillis() - t) / 1000;
                // log.info("??????????????????????????????" + parentCode + " time:" + s + " s");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        if (isNotExist) {
            return new ArrayList<>();
        } else {
            return list;
        }
    }

    public Menu getMenu(String code) {
        return (Menu) getObj(this, code);
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param code
     * @return
     */
    public Menu forExample(String code) {
        Menu menu = null;
        try {
            menu = (Menu) RMCache.getInstance().getFromGroup(code, group);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        if (null != menu) {
            // ?????????
            if (ConstUtil.CACHE_NONE.equals(menu.getCode())) {
                return null;
            } else {
                return menu;
            }
        }

        boolean isLocked = false;
        String indentifier = "";
        // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        try {
            if (Global.isCluster() && Global.getInstance().isUseRedis()) {
                indentifier = distributedLock.lock(getClass().getName(), 5000, 1000);
                menu = (Menu) RMCache.getInstance().getFromGroup(code, group);
                if (null == menu) {
                    menu = menuService.getMenu(code);
                    if (null == menu) {
                        // throw new RuntimeException("This data could not be empty. code=" + code);
                        // ?????????null??????menu?????????????????????menu.name??? &&??????????????????????????????5??????
                        menu = new Menu();
                        menu.setCode(ConstUtil.CACHE_NONE);
                        RMCache.getInstance().putInGroup(code, group, menu, ConstUtil.CACHE_NONE_EXPIRE);
                    } else {
                        RMCache.getInstance().putInGroup(code, group, menu);
                    }
                }
            } else {
                if (lock.tryLock()) {
                    isLocked = true;
                    menu = (Menu) RMCache.getInstance().getFromGroup(code, group);
                    if (null == menu) {
                        menu = menuService.getMenu(code);
                        if (null == menu) {
                            // throw new RuntimeException("This data could not be empty. code=" + code);
                            // ?????????null??????menu?????????????????????menu.name??? &&??????????????????????????????5??????
                            menu = new Menu();
                            menu.setCode(ConstUtil.CACHE_NONE);
                            RMCache.getInstance().putInGroup(code, group, menu, ConstUtil.CACHE_NONE_EXPIRE);
                        } else {
                            RMCache.getInstance().putInGroup(code, group, menu);
                        }
                    }
                } else {
                    TimeUnit.MILLISECONDS.sleep(200);
                    menu = forExample(code);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (CacheException e) {
            e.printStackTrace();
        } finally {
            if (Global.isCluster() && Global.getInstance().isUseRedis()) {
                distributedLock.unlock(getClass().getName(), indentifier);
            } else {
                if (isLocked) {
                    lock.unlock();
                }
            }
        }

        return menu;
    }
}
