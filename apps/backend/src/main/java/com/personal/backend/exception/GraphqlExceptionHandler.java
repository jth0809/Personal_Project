package com.personal.backend.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Component;

// @Component: 이 클래스를 Spring Bean으로 등록하여 자동으로 적용되도록 합니다.
@Component
@Slf4j
public class GraphqlExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof AuthenticationCredentialsNotFoundException) {
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.UNAUTHORIZED) // 명확하게 UNAUTHORIZED 타입으로 지정
                    .message("인증이 필요한 서비스입니다.")
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        }
        else if (ex instanceof AuthorizationDeniedException) {
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.FORBIDDEN) // '권한 없음'은 FORBIDDEN이 더 적절합니다.
                    .message(ex.getMessage()) // 예외에 포함된 "Access Denied" 메시지를 그대로 사용
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        }
        else if (ex instanceof EntityNotFoundException) {
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.NOT_FOUND)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        }
        // 2. 잘못된 인자가 들어온 경우 (BAD_REQUEST)
        else if (ex instanceof IllegalArgumentException) {
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        }
        // 3. 접근 권한이 없는 경우 (FORBIDDEN)
        else if (ex instanceof SecurityException) {
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.FORBIDDEN)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        }
        // 4. 그 외 예측하지 못한 모든 서버 내부 오류
        else {
            log.error("Internal Server Error in GraphQL resolver", ex);
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.INTERNAL_ERROR)
                    .message("서버 내부 오류가 발생했습니다.")
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        }
    }
}