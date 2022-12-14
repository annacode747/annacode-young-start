package com.cloudweb.oa.cache;

import cn.js.fan.cache.jcs.RMCache;
import cn.js.fan.web.Global;
import com.cloudweb.oa.base.ObjCache;
import com.cloudweb.oa.entity.Department;
import com.cloudweb.oa.service.IDepartmentService;
import com.cloudweb.oa.utils.ConstUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.jcs.access.exception.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class DepartmentCache extends ObjCache {

    @Autowired
    IDepartmentService departmentService;

    private final String cachePrix = "ch_";

    private static final Lock lock = new ReentrantLock();

    public DepartmentCache() {
    }

    @Override
    public Lock getLock() {
        return lock;
    }

    @Override
    public String getPrimaryKey(Object obj) {
        Department department = (Department) obj;
        return department.getCode();
    }

    @Override
    public Object getEmptyObjWithPrimaryKey(String value) {
        Department department = new Department();
        department.setCode(value);
        return department;
    }

    @Override
    public Object getObjRaw(String key) {
        return departmentService.getDepartment(key);
    }

    public void removeAllFromCache() {
        try {
            RMCache.getInstance().invalidateGroup(group);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void refreshChildren(String parentCode) {
        try {
            RMCache.getInstance().remove(cachePrix + parentCode, group);
        } catch (CacheException e) {
            e.printStackTrace();
        }
    }

    public List<Department> getChildren(String parentCode) {
        List<Department> list = null;
        boolean isNotExist = false;
        try {
            list = (List) RMCache.getInstance().getFromGroup(cachePrix + parentCode, group);
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
                        list = departmentService.getChildren(parentCode);
                        if (list.size() == 0) {
                            isNotExist = true;
                            // throw new RuntimeException("This data could not be empty. code=" + code);
                            // ?????????null??????menu?????????????????????menu.name??? &&??????????????????????????????5??????
                            Department menu = new Department();
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
                            list = departmentService.getChildren(parentCode);
                            if (list.size() == 0) {
                                isNotExist = true;
                                // throw new RuntimeException("This data could not be empty. code=" + code);
                                // ?????????null??????menu?????????????????????menu.name??? &&??????????????????????????????5??????
                                Department department = new Department();
                                department.setCode(ConstUtil.CACHE_NONE);
                                list.add(department);
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
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        if (isNotExist) {
            return new ArrayList<>();
        } else {
            return list;
        }
    }

    public Department getDepartment(String code) {
        Department department = null;
        try {
            department = (Department) RMCache.getInstance().getFromGroup(code, group);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        if (department != null) {
            return department;
        }

        department = departmentService.getDepartment(code);
        if (department != null) {
            try {
                RMCache.getInstance().putInGroup(code, group, department);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return department;
    }
}