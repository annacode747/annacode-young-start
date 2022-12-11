package com.cloudweb.oa.service.impl;

import cn.js.fan.web.Global;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cloudweb.oa.entity.OaNoticeAttach;
import com.cloudweb.oa.mapper.OaNoticeAttachMapper;
import com.cloudweb.oa.service.IOaNoticeAttachService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author fgf
 * @since 2020-01-01
 */
@Service
public class OaNoticeAttachServiceImpl extends ServiceImpl<OaNoticeAttachMapper, OaNoticeAttach> implements IOaNoticeAttachService {

    @Autowired
    OaNoticeAttachMapper oaNoticeAttachMapper;

    @Override
    public boolean create(OaNoticeAttach oaNoticeAttach) {
        return oaNoticeAttachMapper.insert(oaNoticeAttach) == 1;
    }

    @Override
    public int del(long id) {
        OaNoticeAttach oaNoticeAttach = oaNoticeAttachMapper.selectById(id);
        // 删除文件
        String path = Global.getRealPath() + "/" + oaNoticeAttach.getVisualpath() + oaNoticeAttach.getDiskname();
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }

        // 删除记录
        return oaNoticeAttachMapper.deleteById(id);
    }

    @Override
    public int delOfNotice(long noticeId) {
        int n = 0;
        QueryWrapper<OaNoticeAttach> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id")
                .eq("notice_id", noticeId);
        List<OaNoticeAttach> list = oaNoticeAttachMapper.selectList(queryWrapper);
        Iterator<OaNoticeAttach> ir = list.iterator();
        while (ir.hasNext()) {
            OaNoticeAttach oaNoticeAttach = ir.next();
            n += del(oaNoticeAttach.getId());
        }
        return n;
    }

}
