package com.xqh.financial.utils.zkconf;

import com.github.zkclient.IZkDataListener;
import com.github.zkclient.ZkClient;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by hssh on 2017/2/15.
 */
@Component
public class ValueChangeHandler {

    @org.springframework.beans.factory.annotation.Value("${zk.host}")
    private String zkHost;

    public static Logger logger = LoggerFactory.getLogger(ValueChangeHandler.class);

    public SetMultimap<String, ObjectField> map = HashMultimap.create();

    public ZkClient zkClient;

    public IZkDataListener iZkDataListener = new IZkDataListener() {
        @Override
        public void handleDataChange(String dataPath, byte[] data) throws Exception {

            PropertiesConfiguration properties = new PropertiesConfiguration();
            properties.load(new ByteArrayInputStream(data), "utf-8");
            Iterator<String> is = properties.getKeys();
            while (is.hasNext()) {
                String key = dataPath + "/" + is.next();
                Set<ObjectField> set = map.get(key);
                for(ObjectField ob : set) {
                    ob.getField().setAccessible(true);
                    Value annotation = ob.getField().getAnnotation(Value.class);
                    ob.getField().set(ob.getObject(), properties.getProperty(annotation.key()));
                    logger.info("修改变量成功 key:{} value:{}", annotation.key(), properties.getProperty(annotation.key()));
                }

            }
        }

        @Override
        public void handleDataDeleted(String dataPath) throws Exception {

        }
    };

    public void add(String path, String key, Object object, Field field)
    {
        StringBuffer pathKey = new StringBuffer();
        pathKey.append(path);
        pathKey.append("/");
        pathKey.append(key);

        map.put(pathKey.toString(), new ObjectField(object, field));
    }

    @PostConstruct
    public void init()
    {
        if(StringUtils.isBlank(this.zkHost))
        {
            throw new IllegalArgumentException("zkHost配置无效");
        }

        this.zkClient = new ZkClient(zkHost);
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@zkHost: " + this.zkHost + "@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    }


}
