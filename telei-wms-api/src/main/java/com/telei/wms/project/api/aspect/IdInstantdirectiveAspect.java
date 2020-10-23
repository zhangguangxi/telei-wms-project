package com.telei.wms.project.api.aspect;

import com.alibaba.fastjson.JSON;
import com.telei.wms.commons.amqp.entity.OrderContext;
import com.telei.wms.commons.amqp.entity.ParamType;
import com.telei.wms.project.api.configuration.IdInstantdirectiveConfig;
import com.telei.wms.project.api.endpoint.idInstantdirective.IdInstantdirectiveBussiness;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 数据同步埋点切面类
 * @Auther: Dean
 * @Date: 2020/8/5 10:45
 */
@Aspect
@Component
public class IdInstantdirectiveAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IdInstantdirectiveConfig idInstantdirectiveConfig;

    @Autowired
    private IdInstantdirectiveBussiness idInstantdirectiveBussiness;

    private final static String POINT_CUT = "execution(public * com.nuochen.framework.autocoding.domain.mybatis.BaseService.insert*(..))"
            + " || execution(public * com.nuochen.framework.autocoding.domain.mybatis.BaseService.update*(..))"
            + " || execution(public * com.nuochen.framework.autocoding.domain.mybatis.BaseService.delete*(..))";

//    @AfterReturning("execution(public * com.telei.oms.datasource.oms.service.*.*(..))")
    @AfterReturning(POINT_CUT)
    public void afterReturning(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String idTable = idInstantdirectiveConfig.getDataDependencies().get(className);
        if (! Objects.isNull(idTable)) {
            logger.debug("进入数据同步埋点。idTable：" + idTable + " methodName：" + methodName);
            String operationCode = null;
            if (methodName.startsWith("insert")) {
                operationCode = "ADD";
            } else if (methodName.startsWith("delete")) {
                operationCode = "DEL";
            } else if (methodName.startsWith("update")) {
                operationCode = "MOD";
            }
            boolean flag = true;
            if (! Objects.isNull(operationCode)) {
                //获取参数类型
                List<ParamType> paramTypes = new ArrayList<>();
                for (Object object : joinPoint.getArgs()) {
                    ParamType paramType = new ParamType();
                    paramType.setType(object.getClass().getSimpleName());
                    if (object instanceof Collection) {
                        List list = (List) object;
                        if (list.isEmpty()) {
                            flag = false;
                            logger.warn("数据同步出现参数类型为空。" + JSON.toJSONString(joinPoint.getArgs()));
                            break;
                        }
                        paramType.setGenericType(list.get(0).getClass().getSimpleName());
                    }
                    paramTypes.add(paramType);
                }
                if (flag) {
                    OrderContext orderContext = new OrderContext();
                    orderContext.setClassName(className);
                    orderContext.setMethodName(methodName);
                    orderContext.setParamTypes(paramTypes);
                    orderContext.setBody(joinPoint.getArgs());
                    logger.debug("orderContext：" + JSON.toJSONString(orderContext));
                    //添加数据同步指令
                    idInstantdirectiveBussiness.add(idTable, operationCode, orderContext);
                }
            }
        }
    }
}
