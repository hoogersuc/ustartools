package com.dpnet.tools.qq.domain.repository;

import com.dpnet.mybatis.plugins.DPMapper;
import com.dpnet.tools.qq.domain.model.UrlHistory;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlHistoryMapper extends DPMapper<UrlHistory> {
}