
package net.platform.center.handle.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@MappedTypes(List.class)
public class VectorTypeHandler extends BaseTypeHandler<List<Double>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Double> parameter, JdbcType jdbcType)
        throws SQLException {
        // PGVector 接受 "[0.1, 0.2, ...]" 格式的字符串
        String vectorStr = parameter.stream().map(String::valueOf).collect(Collectors.joining(",", "[", "]"));
        // 注意：Types.OTHER 触发 PG 的类型转换
        log.debug("vectorStr: {}", vectorStr);
        ps.setObject(i, vectorStr, Types.OTHER);
    }

    @Override
    public List<Double> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseVector(rs.getString(columnName));
    }

    @Override
    public List<Double> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseVector(rs.getString(columnIndex));
    }

    @Override
    public List<Double> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseVector(cs.getString(columnIndex));
    }

    private List<Double> parseVector(String pgVectorStr) {
        // 保持底层语义：数据库没有，Java 就不该凭空变出一个空 List
        if (pgVectorStr == null || pgVectorStr.isBlank()) {
            return null;
        }

        try {
            // 兼容更复杂的格式，去掉所有括号
            String content = pgVectorStr.replace("[", "").replace("]", "");
            if (content.isBlank()) {
                return List.of();
            }
            return Arrays.stream(content.split(",")).map(Double::valueOf).toList();
        } catch (Exception e) {
            log.error("Failed to parse PGVector string: {}", pgVectorStr, e);
            return null; // 或者抛出自定义异常
        }
    }

}
