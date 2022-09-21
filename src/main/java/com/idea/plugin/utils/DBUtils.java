package com.idea.plugin.utils;

import com.idea.plugin.setting.support.TableConfigVO;
import com.idea.plugin.sql.support.*;
import com.idea.plugin.sql.support.enums.*;
import com.idea.plugin.translator.TranslatorFactroy;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

public class DBUtils {

    public static JDBCType convertToJdbcType(String type, String databaseType) {
        if (Objects.isNull(type) || type.isEmpty()) {
            return JDBCType.OTHER;
        }

        String fixed = type.toUpperCase();
        if (fixed.contains(JDBCType.BIGINT.name())) {
            return JDBCType.BIGINT;
        } else if (fixed.contains(JDBCType.TINYINT.name())) {
            return JDBCType.TINYINT;
        } else if (fixed.contains(JDBCType.LONGVARBINARY.name())) {
            return JDBCType.LONGVARBINARY;
        } else if (fixed.contains(JDBCType.VARBINARY.name())) {
            return JDBCType.VARBINARY;
        } else if (fixed.contains(JDBCType.LONGVARCHAR.name())) {
            return JDBCType.LONGVARCHAR;
        } else if (fixed.contains(JDBCType.SMALLINT.name())) {
            return JDBCType.SMALLINT;
        } else if (fixed.contains("DATETIME")) {
            return JDBCType.TIMESTAMP;
        } else if (fixed.equals(JDBCType.DATE.name()) && "Oracle".equals(databaseType)) {
            return JDBCType.TIMESTAMP;
        } else if (fixed.contains("NUMBER")) {
            return JDBCType.DECIMAL;
        } else if (fixed.contains(JDBCType.BOOLEAN.name())) {
            return JDBCType.BOOLEAN;
        } else if (fixed.contains(JDBCType.BINARY.name())) {
            return JDBCType.VARBINARY;
        } else if (fixed.contains(JDBCType.BIT.name())) {
            return JDBCType.BIT;
        } else if (fixed.contains("BOOL")) {
            return JDBCType.BOOLEAN;
        } else if (fixed.contains(JDBCType.DATE.name())) {
            return JDBCType.DATE;
        } else if (fixed.contains(JDBCType.TIMESTAMP.name())) {
            return JDBCType.TIMESTAMP;
        } else if (fixed.contains("TIME")) {
            return JDBCType.TIME;
        } else if (!fixed.contains(JDBCType.REAL.name()) && !fixed.contains("NUMBER")) {
            if (fixed.contains(JDBCType.FLOAT.name())) {
                return JDBCType.FLOAT;
            } else if (fixed.contains(JDBCType.DOUBLE.name())) {
                return JDBCType.DOUBLE;
            } else if (JDBCType.CHAR.name().equals(fixed)) {
                return JDBCType.CHAR;
            } else if (fixed.equals("INT")) {
                return JDBCType.INTEGER;
            } else if (fixed.contains(JDBCType.DECIMAL.name())) {
                return JDBCType.DECIMAL;
            } else if (fixed.contains(JDBCType.NUMERIC.name())) {
                return JDBCType.NUMERIC;
            } else if (!fixed.contains(JDBCType.CHAR.name()) && !fixed.contains("TEXT")) {
                if (fixed.contains(JDBCType.BLOB.name())) {
                    return JDBCType.BLOB;
                } else if (fixed.contains(JDBCType.CLOB.name())) {
                    return JDBCType.CLOB;
                } else {
                    return fixed.contains("REFERENCE") ? JDBCType.REF : JDBCType.OTHER;
                }
            } else {
                return JDBCType.VARCHAR;
            }
        } else {
            return JDBCType.REAL;
        }
    }


    public static void getRowValues(ResultSet resultSet, ResultSetMetaData metaData, Map<String, String> newIdCacheMap, List<String> mysqlValueList, List<String> oracleValueList, List<String> declareColumns, List<String> dbmsLobCreates, List<String> dbmsLobApends) throws Exception {
        for (int i = 2; i <= metaData.getColumnCount(); i++) {
            String columnLabel = metaData.getColumnLabel(i).toUpperCase();
            String columnClassName = metaData.getColumnClassName(i);
            Object value = resultSet.getString(i);
            String mValue = null;
            String oValue = null;
            if (value != null && value != "null") {
                if ("UUID".equalsIgnoreCase(value.toString())) {
                    value = "'" + UUID.randomUUID().toString().replace("-", "") + "'";
                    newIdCacheMap.put(columnLabel, value.toString());
                } else if ("SYSDATE".equalsIgnoreCase(value.toString())) {
                    mValue = "SYSDATE()";
                    oValue = "SYSDATE";
                } else if (newIdCacheMap.containsKey(value)) {
                    value = newIdCacheMap.get(value);
                } else if (String.class.getName().equals(columnClassName)) {
                    if (value.toString().contains("'")) {
                        value = value.toString().replaceAll("'", "\\\\'");
                    }
                    value = "'" + value + "'";
                } else if (Timestamp.class.getName().equals(columnClassName)
                        || LocalDate.class.getName().equals(columnClassName)
                        || LocalDateTime.class.getName().equals(columnClassName)
                        || Date.class.getName().equals(columnClassName)) {
                    Timestamp timestamp = resultSet.getTimestamp(i);
                    if (timestamp != null) {
                        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        mValue = "'" + dateFormat.format(timestamp.toLocalDateTime()) + "'";
                        oValue = "TIMESTAMP '" + dateFormat.format(timestamp.toLocalDateTime()) + "'";
                    }
                } else if (BigDecimal.class.getName().equals(columnClassName)) {
                    value = resultSet.getBigDecimal(i).doubleValue();
                } else if (Double.class.getName().equals(columnClassName)) {
                    value = resultSet.getDouble(i);
                } else if (Float.class.getName().equals(columnClassName)) {
                    value = resultSet.getFloat(i);
                } else if (Integer.class.getName().equals(columnClassName)) {
                    value = resultSet.getInt(i);
                } else {
                    value = "'" + value + "'";
                }
            }
            if (value != null && value.toString().contains("\\")) {
                mValue = value.toString().replaceAll("\\\\", "\\\\\\\\");
            }
            if (value != null && value.toString().length() > 3999) {
                String valueStr = value.toString();
                String declareColumnName = "V_" + columnLabel;
                int separationLength = valueStr.length() / 29999;
                for (int j = 0; j <= separationLength; j++) {
                    if (j == separationLength) {
                        dbmsLobApends.add("    DBMS_LOB.APPEND(" + declareColumnName + ", '" + valueStr.substring(j * 29999) + ");\n");
                    } else if (j == 0) {
                        dbmsLobApends.add("    DBMS_LOB.APPEND(" + declareColumnName + ", " + valueStr.substring(0, (j + 1) * 29999) + "');\n");
                    } else {
                        dbmsLobApends.add("    DBMS_LOB.APPEND(" + declareColumnName + ", '" + valueStr.substring(j * 29999, (j + 1) * 29999) + "');\n");
                    }
                }
                String declareColumn = "    " + declareColumnName + " CLOB;\n";
                String dbmsLobCreate = "    DBMS_LOB.CREATETEMPORARY(" + declareColumnName + ", TRUE);\n";
                declareColumns.add(declareColumn);
                dbmsLobCreates.add(dbmsLobCreate);
                oValue = declareColumnName;
            }
            if (mValue != null) {
                mysqlValueList.add(mValue);
            } else {
                mysqlValueList.add(value == null ? "null" : value.toString());
            }
            if (oValue != null) {
                oracleValueList.add(oValue);
            } else {
                oracleValueList.add(value == null ? "null" : value.toString());
            }
        }
    }

    public static void setInsertDataInfoVO(GeneralSqlInfoVO generalSqlInfoVO, TableSqlInfoVO tableSqlInfoVO) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DBUtils.getConnection(generalSqlInfoVO);
            String insertSql = tableSqlInfoVO.insertSql.replaceAll(";", " ");
            preparedStatement = connection.prepareStatement(insertSql);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> codeList = new ArrayList<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            String idCode = metaData.getColumnLabel(1);
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                codeList.add(metaData.getColumnLabel(i).toUpperCase());
            }
            int dataIdx = 0;
            List<InsertDataInfoVO> insertDataInfoVOS = new ArrayList<>();
            while (resultSet.next()) {
                InsertDataInfoVO insertDataInfoVO = new InsertDataInfoVO();
                insertDataInfoVO.setCodes(codeList);
                insertDataInfoVO.setIdCode(idCode);
                String idValue = DBUtils.getIdValue(resultSet.getString(1));
                insertDataInfoVO.setIdValue(idValue);
                insertDataInfoVO.getMvalues().add(idValue);
                insertDataInfoVO.getOvalues().add(idValue);
                List<Map<String, String>> newIdCacheList = generalSqlInfoVO.getNewIdCacheMapList();
                Map<String, String> newIdCacheMap = null;
                if (newIdCacheList.size() > dataIdx) {
                    newIdCacheMap = newIdCacheList.get(dataIdx);
                }
                if (newIdCacheMap == null) {
                    newIdCacheMap = new HashMap<>();
                    newIdCacheList.add(newIdCacheMap);
                }
                newIdCacheMap.put(idCode, idValue);
                getRowValues(resultSet, metaData, newIdCacheMap, insertDataInfoVO.getMvalues(), insertDataInfoVO.getOvalues(), insertDataInfoVO.getDeclareColumns(), insertDataInfoVO.getDbmsLobCreates(), insertDataInfoVO.getDbmsLobApends());
                insertDataInfoVOS.add(insertDataInfoVO);
                ++dataIdx;
            }
            tableSqlInfoVO.setInsertDataInfoVOS(insertDataInfoVOS);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtils.close(connection, preparedStatement);
        }
    }


    public static String getIdValue(String value) {
        if (StringUtils.isEmpty(value) || "null".equalsIgnoreCase(value) || "UUID".equalsIgnoreCase(value)) {
            value = UUID.randomUUID().toString().replace("-", "");
        }
        return "'" + value + "'";
    }

    public static Connection getConnection(String jdbcUrl, String username, String password) {
        GeneralInfoVO<?> generalInfoVO = new GeneralInfoVO<>();
        generalInfoVO.jdbcUrl = jdbcUrl;
        generalInfoVO.username = username;
        generalInfoVO.dbpasswd = password;
        return getConnection(generalInfoVO);
    }


    public static Connection getConnection(GeneralInfoVO<?> generalInfoVO) {
        try {
            AssertUtils.assertIsTrue(StringUtils.isNotEmpty(generalInfoVO.username), "username不能为空");
            AssertUtils.assertIsTrue(StringUtils.isNotEmpty(generalInfoVO.dbpasswd), "password不能为空");
            AssertUtils.assertIsTrue(StringUtils.isNotEmpty(generalInfoVO.jdbcUrl), "jdbcUrl不能为空");
            Properties properties = new Properties();
            properties.put("user", generalInfoVO.username);
            properties.put("password", generalInfoVO.dbpasswd);
            properties.setProperty("remarks", "true");
            properties.put("useInformationSchema", "true");
            if (generalInfoVO.jdbcUrl.contains(DataTypeEnum.MYSQL.getCode())) {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(generalInfoVO.jdbcUrl + "?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false", properties);
                generalInfoVO.schema = connection.getCatalog();
                generalInfoVO.dataType = DataTypeEnum.MYSQL;
                return connection;
            } else if (generalInfoVO.jdbcUrl.contains(DataTypeEnum.ORACLE.getCode())) {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                generalInfoVO.schema = generalInfoVO.username.toUpperCase();
                generalInfoVO.dataType = DataTypeEnum.ORACLE;
                return DriverManager.getConnection(generalInfoVO.jdbcUrl, properties);
            }
            throw new RuntimeException("数据库配置错误" + generalInfoVO.jdbcUrl);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static List<String> getAllTableName(Connection connection, String schema) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getTables(schema, schema, "%", new String[]{"TABLE"});
            List<String> ls = new ArrayList<>();
            while (rs.next()) {
                ls.add(rs.getString("TABLE_NAME"));
            }
            return ls;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            close(connection);
        }
    }


    public static TableInfoVO getTableInfoVOFromDB(GeneralInfoVO<?> generalInfoVO, TableInfoVO tableInfoVO, TableConfigVO config) {
        tableInfoVO = getTableInfo(generalInfoVO, tableInfoVO, config);
        getFieldInfo(generalInfoVO, tableInfoVO, config);
        return tableInfoVO;
    }

    public static TableInfoVO getTableInfo(GeneralInfoVO<?> generalInfoVO, TableInfoVO tableInfoVO, TableConfigVO config) {
        if (config.tableInfoCacheMap.containsKey(tableInfoVO.tableName)) {
            return config.tableInfoCacheMap.get(tableInfoVO.tableName);
        }
        Connection connection = null;
        try {
            connection = getConnection(generalInfoVO);
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(generalInfoVO.schema, generalInfoVO.schema, tableInfoVO.tableName, new String[]{"TABLE"});
            if (tables.next()) {
                String remarks = tables.getString("REMARKS");
                if (StringUtils.isEmpty(remarks)) {
                    remarks = TranslatorFactroy.translate(tableInfoVO.tableName);
                }
                tableInfoVO.tableComment = remarks;
            } else {
                throw new RuntimeException(String.format("数据库不存在表：%s", tableInfoVO.tableName));
            }
            ResultSet primaryKeys = metaData.getPrimaryKeys(generalInfoVO.schema, generalInfoVO.schema, tableInfoVO.tableName);
            String primaryKey = "";
            while (primaryKeys.next()) {
                primaryKey = primaryKeys.getString("COLUMN_NAME");
            }
            ResultSet idxRs = metaData.getIndexInfo(generalInfoVO.schema, generalInfoVO.schema, tableInfoVO.tableName, false, false);
            while (idxRs.next()) {
                String indexName = idxRs.getString("INDEX_NAME");
                String columnName = idxRs.getString("COLUMN_NAME");
                if (StringUtils.isEmpty(columnName) || primaryKey.equals(columnName)) {
                    continue;
                }
                tableInfoVO.addIndexInfos(IndexInfoVO.builder().indexColumnName(columnName).indexName(indexName));
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            close(connection);
        }
        config.tableInfoCacheMap.put(tableInfoVO.tableName, tableInfoVO);
        return tableInfoVO;
    }

    public static void getFieldInfo(GeneralInfoVO<?> generalInfoVO, TableInfoVO tableInfoVO, TableConfigVO config) {
        if (config.fieldInfoCacheMap.containsKey(tableInfoVO.tableName)) {
            tableInfoVO.fieldInfos = config.fieldInfoCacheMap.get(tableInfoVO.tableName);
            return;
        }
        if (tableInfoVO.getProcedureTypeList().get(0).getFileType() != null
                && !tableInfoVO.getProcedureTypeList().contains(ProcedureTypeEnum.ADD_TABLE)
                && !tableInfoVO.getProcedureTypeList().contains(ProcedureTypeEnum.ADD_COLUMN)) {
            return;
        }
        Connection connection = null;
        tableInfoVO.fieldInfos = new ArrayList<>();
        try {
            connection = getConnection(generalInfoVO);
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet primaryKeys = metaData.getPrimaryKeys(generalInfoVO.schema, generalInfoVO.schema, tableInfoVO.tableName);
            String primaryKey = "";
            while (primaryKeys.next()) {
                primaryKey = primaryKeys.getString("COLUMN_NAME");
            }
            ResultSet columns = metaData.getColumns(generalInfoVO.schema, generalInfoVO.schema, tableInfoVO.tableName, null);
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String remarks = columns.getString("REMARKS");
                String isNullable = columns.getString("IS_NULLABLE");
                String columnSize = columns.getString("COLUMN_SIZE");
                FieldTypeEnum dataType = null;
                if (generalInfoVO.jdbcUrl.contains(DataTypeEnum.MYSQL.getCode())) {
                    dataType = FieldTypeEnum.getFieldTypeBySqlType(columns.getInt("DATA_TYPE"));
                } else if (generalInfoVO.jdbcUrl.contains(DataTypeEnum.ORACLE.getCode())) {
                    String typeName = columns.getString("TYPE_NAME");
                    if (typeName.contains("(")) {
                        typeName = typeName.replaceAll("\\(.*\\)", "");
                    }
                    dataType = FieldTypeEnum.getFieldTypeByOType(typeName);
                }
                if (dataType == null) {
                    dataType = FieldTypeEnum.VARCHAR;
                }
                int digits = columns.getInt("DECIMAL_DIGITS");
                FieldInfoVO fieldInfoVO = new FieldInfoVO();
                fieldInfoVO.columnName = columnName;
                fieldInfoVO.columnType = dataType;
                if (StringUtils.isEmpty(remarks)) {
                    remarks = TranslatorFactroy.translate(columnName);
                }
                fieldInfoVO.comment = remarks;
                if (columnName.equals(primaryKey)) {
                    fieldInfoVO.primary = PrimaryTypeEnum.PRIMARY;
                }
                if (!isNullable.equals("YES")) {
                    fieldInfoVO.nullType = NullTypeEnum.NOT_NULL;
                }
                if (FieldTypeEnum.VARCHAR.equals(fieldInfoVO.columnType)) {
                    fieldInfoVO.columnTypeArgs = columnSize;
                }
                if (FieldTypeEnum.NUMBER.equals(fieldInfoVO.columnType)) {
                    fieldInfoVO.columnTypeArgs = columnSize + "," + digits;
                }
                tableInfoVO.addFieldInfos(fieldInfoVO);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            close(connection);
        }
        config.fieldInfoCacheMap.put(tableInfoVO.tableName, tableInfoVO.fieldInfos);
    }

    public static void getTableDataInfo(GeneralInfoVO<?> generalInfoVO, TableInfoVO tableInfoVO) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = getConnection(generalInfoVO);
            String sql = "SELECT * FROM " + tableInfoVO.tableName;
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> codeList = new ArrayList<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            String idCode = metaData.getColumnLabel(1);
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                codeList.add(metaData.getColumnLabel(i).toUpperCase());
            }
            String codes = String.join(", ", codeList);
            String idValue = "";
            if (resultSet.next()) {
                List<String> rowValues = new ArrayList<>();
                idValue = DBUtils.getIdValue(resultSet.getString(1));
                rowValues.add(idValue);
                getRowValues(resultSet, metaData, new HashMap<>(), rowValues, new ArrayList<>(), null, null, null);
                String values = String.join(", ", rowValues);
                tableInfoVO.addInsertData("INSERT INTO " + tableInfoVO.tableName + " (" + codes + ") VALUES (" + values + ");");
            }
            int maxCode = codeList.stream().max(Comparator.comparing(String::length)).get().trim().length();
            String codeAlias = codeList.stream().map(code -> code + StringUtil.getBlank(code, maxCode) + " AS " + code).collect(Collectors.joining(",\n                 "));
            tableInfoVO.setInsertSql("SELECT " + codeAlias + "\n          FROM " + tableInfoVO.tableName + "\n          WHERE " + idCode + " = " + idValue + ";");
            tableInfoVO.setInsertColumnName(codes);
            tableInfoVO.setInsertColumnParam(idCode);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            close(connection, preparedStatement);
        }
    }

    public static TableInfoVO initTableInfoVO(TableConfigVO tableConfigVO) {
        TableInfoVO tableInfoVO = new TableInfoVO();
        tableInfoVO.setTableName("T_TABLE_NAME");
        tableInfoVO.setTableComment("示例表");
        tableInfoVO.indexInfos.add(IndexInfoVO.builder().indexColumnName("COL_NAME, CREATE_DATE").indexName("INDEX_T_NAME"));
        tableInfoVO.fieldInfos.add(FieldInfoVO.builder().columnName("ID").columnType(FieldTypeEnum.VARCHAR).columnTypeArgs("32").comment("主键").primary(PrimaryTypeEnum.PRIMARY));
        tableInfoVO.fieldInfos.add(FieldInfoVO.builder().columnName("COL_NAME").columnType(FieldTypeEnum.VARCHAR).columnTypeArgs("32").comment("字段注释"));
        tableInfoVO.fieldInfos.add(FieldInfoVO.builder().columnName("CREATE_DATE").columnType(FieldTypeEnum.TIMESTAMP).comment("创建时间"));
        tableInfoVO.setInsertSql("SELECT * FROM " + tableInfoVO.tableName + " WHERE ID = '主键id';");
        tableInfoVO.setInsertColumnName("ID, COL_NAME, CREATE_DATE");
        tableInfoVO.setInsertColumnParam("ID");
        tableInfoVO.addInsertData("INSERT INTO " + tableInfoVO.tableName + " (ID, COL_NAME, CREATE_DATE) VALUES ('0279a59a333da6ee68dd7b3000400001', 'name', '2022-03-23 16:07:57');");
        tableInfoVO.addInsertData("INSERT INTO " + tableInfoVO.tableName + " (ID, COL_NAME, CREATE_DATE) VALUES ('0279a59a333da6ee68dd7b3000400001', 'name', '2022-03-23 16:07:57');");
        return tableInfoVO;
    }

    public static void addTableInfoAttri(TableInfoVO tableInfoVO) {

        if (CollectionUtils.isEmpty(tableInfoVO.getIndexInfos())) {
            tableInfoVO.indexInfos.add(IndexInfoVO.builder().indexColumnName("COL_NAME, CREATE_DATE").indexName("INDEX_T_NAME"));
        }
        if (CollectionUtils.isEmpty(tableInfoVO.fieldInfos)) {
            tableInfoVO.fieldInfos.add(FieldInfoVO.builder().columnName("ID").columnType(FieldTypeEnum.VARCHAR).comment("主键").primary(PrimaryTypeEnum.PRIMARY));
            tableInfoVO.fieldInfos.add(FieldInfoVO.builder().columnName("COL_NAME").columnType(FieldTypeEnum.VARCHAR).comment("字段注释"));
            tableInfoVO.fieldInfos.add(FieldInfoVO.builder().columnName("CREATE_DATE").columnType(FieldTypeEnum.TIMESTAMP).comment("创建时间"));
        }
        if (StringUtils.isEmpty(tableInfoVO.insertSql)) {
            tableInfoVO.insertSql = "SELECT * FROM " + tableInfoVO.tableName + " WHERE ID = '';";
        }
        if (StringUtils.isEmpty(tableInfoVO.insertColumnName)) {
            tableInfoVO.setInsertColumnName("ID, COL_NAME, CREATE_DATE");
            tableInfoVO.setInsertColumnParam("ID");
        }
        if (CollectionUtils.isEmpty(tableInfoVO.insertData)) {
            tableInfoVO.addInsertData("INSERT INTO " + tableInfoVO.tableName + " (ID, COL_NAME, CREATE_DATE) VALUES ('0279a59a333da6ee68dd7b3000400001', 'name', '2022-03-23 16:07:57');");
            tableInfoVO.addInsertData("INSERT INTO " + tableInfoVO.tableName + " (ID, COL_NAME, CREATE_DATE) VALUES ('0279a59a333da6ee68dd7b3000400001', 'name', '2022-03-23 16:07:57');");
        }
    }

    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(PreparedStatement preparedStatement) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(Connection connection, PreparedStatement preparedStatement) {
        close(connection);
        close(preparedStatement);
    }


}
