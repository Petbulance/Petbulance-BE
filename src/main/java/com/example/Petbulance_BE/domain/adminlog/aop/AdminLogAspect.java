package com.example.Petbulance_BE.domain.adminlog.aop;

import com.example.Petbulance_BE.domain.adminlog.entity.AdminActionLog;
import com.example.Petbulance_BE.domain.adminlog.service.AdminActionLogService;
import com.example.Petbulance_BE.domain.adminlog.type.AdminActionResult;
import com.example.Petbulance_BE.domain.adminlog.type.AdminActorType;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminLogAspect {

    private final AdminActionLogService adminActionLogService;
    private final UserUtil userUtil;
    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(adminLoggable)")
    public Object around(ProceedingJoinPoint joinPoint, AdminLoggable adminLoggable) throws Throwable {
        Object result;
        AdminActionResult resultType = AdminActionResult.SUCCESS;

        try{
            result = joinPoint.proceed();
            return result;
        } catch (Exception e){
            resultType = AdminActionResult.FAIL;
            throw e;
        } finally {
            try{

                String rawTargetId = adminLoggable.targetId();
                String extractedTargetId = (rawTargetId != null && !rawTargetId.isBlank())
                        ? parseTargetId(joinPoint, rawTargetId)
                        : null;

                AdminActionLog adminActionLog = AdminActionLog.builder()
                        .actorType(AdminActorType.ADMIN)
                        .admin(userUtil.getCurrentUser())
                        .pageType(adminLoggable.pageType())
                        .actionType(adminLoggable.actionType())
                        .targetType(adminLoggable.targetType())
                        .targetId(extractedTargetId)
                        .resultType(resultType)
                        .description(adminLoggable.description())
                        .build();

                adminActionLogService.save(adminActionLog);


            } catch (Exception ex) {
                log.error("관리자 로그 저장 중 오류 발생: {}", ex.getMessage());
            }
        }
    }

    private String parseTargetId(ProceedingJoinPoint jp, String expression) {
        if (expression == null || expression.isEmpty()) return null;

        try {
            // 1. SpEL 분석을 위한 컨텍스트(바구니) 생성
            StandardEvaluationContext context = new StandardEvaluationContext();

            // 2. 현재 실행 중인 메서드의 파라미터 이름과 실제 인자값을 가져옴
            String[] parameterNames = ((MethodSignature) jp.getSignature()).getParameterNames();
            Object[] args = jp.getArgs();

            // 3. 파라미터 이름과 값을 1:1로 매칭시켜 컨텍스트에 담음
            // 예: "id" -> 5L
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }

            // 4. 파서가 "#id"라는 문구를 분석해서 컨텍스트에서 값을 찾아옴
            Object value = parser.parseExpression(expression).getValue(context);

            return value != null ? value.toString() : null;
        } catch (Exception e) {
            log.warn("SpEL 파싱 실패: {}", expression);
            return expression; // 파싱 실패 시 원문("#id")이라도 리턴
        }
    }


}
