package com.sp.fc.web.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.expression.AccessException;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.junit.jupiter.api.Assertions.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class Person {

    private String name;
    private int height;

    public boolean over(int pivot) {
        return height >= pivot;
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class Horse {

    private String name;
    private int height;

    public boolean over(int pivot) {
        return height >= pivot;
    }
}

public class SpELTest {

    private final ExpressionParser parser = new SpelExpressionParser();

    Person person = Person.builder()
        .name("홍길동")
        .height(189)
        .build();

    Horse nancy = Horse.builder()
        .name("nancy")
        .height(160)
        .build();

    @DisplayName("4. Context 테스트")
    @Test
    void test_4() {

        StandardEvaluationContext ctx = new StandardEvaluationContext();
        ctx.setBeanResolver(new BeanResolver() {

            @Override
            public Object resolve(EvaluationContext context, String beanName) throws AccessException {
                return beanName.equals("person") ? person : nancy;
            }
        });

        assertTrue(parser.parseExpression("@person.over(180)").getValue(ctx, Boolean.class)); // @person으로 person을 찾을 수 있다.
        assertFalse(parser.parseExpression("@nancy.over(180)").getValue(ctx, Boolean.class)); // @nancy로 nancy를 찾을 수 있다.

        ctx.setRootObject(person);
        assertTrue(parser.parseExpression("over(180)").getValue(ctx, Boolean.class)); // RootObject에 의해 person을 명시하지 않아도 빈을 찾을 수 있다.

        ctx.setVariable("horse", nancy);
        assertFalse(parser.parseExpression("#horse.over(180)").getValue(ctx, Boolean.class)); // #horse를 통해 Variable에 담긴 nancy를 가져올 수 있다.
    }

    @DisplayName("3. 메서드 호출")
    @Test
    void test_3() {
        assertTrue(parser.parseExpression("over(180)").getValue(person, Boolean.class));
        assertFalse(parser.parseExpression("over(180)").getValue(nancy, Boolean.class));
    }

    @DisplayName("2. 값 변경")
    @Test
    void test_2() {

        parser.parseExpression("name").setValue(person, "호나우드");

        assertEquals("호나우드", parser.parseExpression("name").getValue(person, String.class));
    }

    @DisplayName("1. 기본 테스트")
    @Test
    void test_1() {
        assertEquals("홍길동", parser.parseExpression("name").getValue(person, String.class));
    }
}
