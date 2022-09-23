package com.sp.fc.web.config;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.expression.method.ExpressionBasedPreInvocationAdvice;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdviceVoter;
import org.springframework.security.access.vote.*;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import java.util.ArrayList;
import java.util.List;

@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

    @Override
    protected AccessDecisionManager accessDecisionManager() {

        List<AccessDecisionVoter<?>> decisionVoters = new ArrayList<>();

        ExpressionBasedPreInvocationAdvice expressionAdvice = new ExpressionBasedPreInvocationAdvice();
        expressionAdvice.setExpressionHandler(getExpressionHandler());

        // 여러 Voter를 넣는 AccessDecisionManager
        decisionVoters.add(new PreInvocationAuthorizationAdviceVoter(expressionAdvice));
        decisionVoters.add(new RoleVoter());
        decisionVoters.add(new AuthenticatedVoter());
        decisionVoters.add(new CustomVoter()); // 커스텀 voter를 추가

        return new AffirmativeBased(decisionVoters); // AffirmativeBased: 긍정 위원회
//        return new UnanimousBased(decisionVoters); // UnanimousBased: 만장일치 위원회
//        ConsensusBased committee = new ConsensusBased(decisionVoters); // ConsensusBased: 다수결 위원회
//        committee.setAllowIfEqualGrantedDeniedDecisions(false); // 기본은 true이며, 투표가 동점일 경우에 대한 결정을 설정할 수 있다. (true: 승인, false: 거부)
//        return committee;
    }
}
