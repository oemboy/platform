
package net.platform.center.util;

import static org.springframework.expression.ParserContext.TEMPLATE_EXPRESSION;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.map.LRUMap;
import org.springframework.context.expression.BeanExpressionContextAccessor;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.EnvironmentAccessor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public abstract class SpelUtil {
    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    private static final Map<String, Expression> CACHE = new LRUMap<>(1024);

    public static Expression templateExpression(String expr) {
        Expression expression = CACHE.get(expr);
        if (Objects.isNull(expression)) {
            synchronized (SpelUtil.class) {
                CACHE.putIfAbsent(expr, expression = PARSER.parseExpression(expr, TEMPLATE_EXPRESSION));
            }
        }
        return expression;
    }

    public static EvaluationContext context(Object root) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setRootObject(root);
        context.addPropertyAccessor(new BeanExpressionContextAccessor());
        context.addPropertyAccessor(new BeanFactoryAccessor());
        context.addPropertyAccessor(new MapAccessor());
        context.addPropertyAccessor(new EnvironmentAccessor());
        return context;
    }

    public static <T> T getValue(String expr, EvaluationContext context, Class<T> tClass) {
        return templateExpression(expr).getValue(context, tClass);
    }

    public static Object getValue(String expr, EvaluationContext context) {
        return templateExpression(expr).getValue(context, Object.class);
    }

    public static String getValueString(String expr, EvaluationContext context) {
        return templateExpression(expr).getValue(context, String.class);
    }

    public static <T> T getValue(String expr, Object root, Class<T> tClass) {
        return templateExpression(expr).getValue(context(root), tClass);
    }

    public static Object getValue(String expr, Object root) {
        return templateExpression(expr).getValue(context(root), Object.class);
    }

    public static String getValueString(String expr, Object root) {
        return templateExpression(expr).getValue(context(root), String.class);
    }

    public static void setValue(String expr, EvaluationContext context, Object value) {
        templateExpression(expr).setValue(context, value);
    }
}
