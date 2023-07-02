package com.example.common.handle;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * mybatis-plus填充器，实现公共字段自动写入
 *
 * @author wuyiz
 * @Date 2023-07-03
 */
@Component
public class MybatisPlusMetaHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        setValue(metaObject, new Date(), "createTime", "insertUpdateTime");
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        setValue(metaObject, new Date(), "updateTime", "insertUpdateTime");
    }

    private void setValue(MetaObject metaObject, Object value, String... fieldNames) {
        for (String fieldName : fieldNames) {
            Object field = getFieldValByName(fieldName, metaObject);
            if (field == null && value != null) {
                this.setFieldValByName(fieldName, value, metaObject);
            }
        }
    }
}