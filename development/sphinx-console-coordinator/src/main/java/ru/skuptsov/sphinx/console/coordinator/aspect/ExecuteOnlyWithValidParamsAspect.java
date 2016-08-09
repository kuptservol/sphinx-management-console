package ru.skuptsov.sphinx.console.coordinator.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.annotation.ExistsInDB;
import ru.skuptsov.sphinx.console.spring.service.api.EntityService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.List;

/**
 * Аспект для проверки существования объектов в БД
 *
 */
@Aspect
@Component
public class ExecuteOnlyWithValidParamsAspect {

    private static final Logger logger = LoggerFactory.getLogger(ExecuteOnlyWithValidParamsAspect.class);

    @Autowired
    private EntityService entityService;

    @Around("execution(public ru.skuptsov.sphinx.console.coordinator.model.Status *(..)) && @target(org.springframework.stereotype.Controller) && @annotation(org.springframework.web.bind.annotation.RequestMapping) && @annotation(ru.skuptsov.sphinx.console.coordinator.annotation.ExecuteOnlyWithValidParams)")
    public Status aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Start executing ExecuteOnlyWithValidParamsAspect");
        StringBuilder errorMessage = new StringBuilder();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        BindingResult result = getBindingResult(joinPoint.getArgs());
        if (result != null && result.hasErrors()) {
            errorMessage.append(convertSpringErrors(result.getFieldErrors()));
        } else {
            errorMessage.append(getExistsInDBErrorMessage(method, joinPoint.getArgs()));
        }

        if(errorMessage.toString().isEmpty()){
            return (Status)joinPoint.proceed();
        } else {
            logger.error("Errors found during JSON parameters validation for method " + method.getName() + ". Method invocation will not proceed: " + errorMessage);
            return Status.build(Status.SystemInterface.COORDINATOR_CONFIGURATION, Status.StatusCode.REQUEST_PARAM_VALIDATION_FAILED).addMessage(errorMessage.toString());
        }
    }

    private BindingResult getBindingResult(Object[] args) {
        for (Object currentArgument : args) {
            if (currentArgument != null && currentArgument instanceof BindingResult) {
                return (BindingResult) currentArgument;
            }
        }

        return null;
    }

    private String getExistsInDBErrorMessage(Method method, Object... params){
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < params.length; i++){
            Object param = params[i];
            Annotation[] parAnnotations = method.getParameterAnnotations()[i];
            for(int j=0; j<parAnnotations.length; j++){
                if(parAnnotations[j].annotationType().isAssignableFrom(ExistsInDB.class)){
                    ExistsInDB curAnnotation = ((ExistsInDB)parAnnotations[j]);
                    if(!entityService.existsByField(curAnnotation.entityClass(), curAnnotation.fieldName(), param)){
                        result.append(MessageFormat.format("Object {0}.{1}={2} does not exists in DB.\n", curAnnotation.entityClass(), curAnnotation.fieldName(), param));
                    }
                }
            }
        }
        return result.toString();
    }

    private String convertSpringErrors(List<FieldError> errors){
        StringBuilder validationError = new StringBuilder();
        for(FieldError error : errors){
            if(validationError.length() > 0){validationError.append(", ");}
            validationError.append(MessageFormat.format(
                    "{0}.{1} = {2} {3}",
                    error.getObjectName(), error.getField(), error.getRejectedValue(), error.getDefaultMessage()));
        }

        return validationError.toString();
    }
}
