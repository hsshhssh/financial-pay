package com.xqh.financial.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.*;
import java.util.Set;

/**
 * Created by hssh on 2017/5/7.
 */
public class ValidateUtils {

    private static Logger logger = LoggerFactory.getLogger(ValidateUtils.class);

    private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private static Validator validator = validatorFactory.getValidator();

    /**
     * 校验单个java对象
     * @param object
     */
    public static void validateEntity(Object object) {

        Set<ConstraintViolation<Object>> validateRes = validator.validate(object, new Class[]{});
        if(validateRes != null && validateRes.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Object> v : validateRes) {
                sb.append(v.getMessage()).append("\n");
            }
            logger.error("validate result invalid object:{}, msg:{}", object, sb.toString());
            throw new ValidationException(sb.toString());
        }

    }

}
