package com.tuojin.bill.bean;

/**
 * Created by Administrator on 2017/1/13.
 */
public class SqlServerStructure {
    private String TABLE_CATALOG;
    private String TABLE_SCHEMA;
    private String TABLE_NAME;
    private String COLUMN_NAME;
    private int ORDINAL_POSITION;
    private String COLUMN_DEFAULT;
    private String IS_NULLABLE;
    private String DATA_TYPE;
    private String CHARACTER_MAXIMUM_LENGTH;
    private String CHARACTER_OCTET_LENGTH;
    private String NUMERIC_PRECISION;
    private String NUMERIC_SCALE;
    private String CHARACTER_SET_NAME;
    private String COLLATION_NAME;
    private String COLUMN_TYPE;
    private String COLUMN_KEY;
    private String EXTRA;
    private String PRIVILEGES;
    private String COLUMN_COMMENT;

    public SqlServerStructure() {
    }

    public SqlServerStructure(String TABLE_CATALOG,
                              String TABLE_SCHEMA,
                              String TABLE_NAME,
                              String COLUMN_NAME,
                              int ORDINAL_POSITION,
                              String COLUMN_DEFAULT,
                              String IS_NULLABLE,
                              String DATA_TYPE,
                              String CHARACTER_MAXIMUM_LENGTH,
                              String CHARACTER_OCTET_LENGTH,
                              String NUMERIC_PRECISION,
                              String NUMERIC_SCALE,
                              String CHARACTER_SET_NAME,
                              String COLLATION_NAME,
                              String COLUMN_TYPE,
                              String COLUMN_KEY,
                              String EXTRA,
                              String PRIVILEGES,
                              String COLUMN_COMMENT) {
        this.TABLE_CATALOG = TABLE_CATALOG;
        this.TABLE_SCHEMA = TABLE_SCHEMA;
        this.TABLE_NAME = TABLE_NAME;
        this.COLUMN_NAME = COLUMN_NAME;
        this.ORDINAL_POSITION = ORDINAL_POSITION;
        this.COLUMN_DEFAULT = COLUMN_DEFAULT;
        this.IS_NULLABLE = IS_NULLABLE;
        this.DATA_TYPE = DATA_TYPE;
        this.CHARACTER_MAXIMUM_LENGTH = CHARACTER_MAXIMUM_LENGTH;
        this.CHARACTER_OCTET_LENGTH = CHARACTER_OCTET_LENGTH;
        this.NUMERIC_PRECISION = NUMERIC_PRECISION;
        this.NUMERIC_SCALE = NUMERIC_SCALE;
        this.CHARACTER_SET_NAME = CHARACTER_SET_NAME;
        this.COLLATION_NAME = COLLATION_NAME;
        this.COLUMN_TYPE = COLUMN_TYPE;
        this.COLUMN_KEY = COLUMN_KEY;
        this.EXTRA = EXTRA;
        this.PRIVILEGES = PRIVILEGES;
        this.COLUMN_COMMENT = COLUMN_COMMENT;
    }

    public SqlServerStructure(String TABLE_CATALOG, String TABLE_SCHEMA, String TABLE_NAME, String COLUMN_NAME, int ORDINAL_POSITION, String IS_NULLABLE, String DATA_TYPE, String CHARACTER_MAXIMUM_LENGTH, String CHARACTER_OCTET_LENGTH, String NUMERIC_PRECISION, String CHARACTER_SET_NAME, String COLLATION_NAME, String COLUMN_TYPE, String COLUMN_KEY, String EXTRA, String PRIVILEGES, String COLUMN_COMMENT) {
        this.TABLE_CATALOG = TABLE_CATALOG;
        this.TABLE_SCHEMA = TABLE_SCHEMA;
        this.TABLE_NAME = TABLE_NAME;
        this.COLUMN_NAME = COLUMN_NAME;
        this.ORDINAL_POSITION = ORDINAL_POSITION;
        this.IS_NULLABLE = IS_NULLABLE;
        this.DATA_TYPE = DATA_TYPE;
        this.CHARACTER_MAXIMUM_LENGTH = CHARACTER_MAXIMUM_LENGTH;
        this.CHARACTER_OCTET_LENGTH = CHARACTER_OCTET_LENGTH;
        this.NUMERIC_PRECISION = NUMERIC_PRECISION;
        this.CHARACTER_SET_NAME = CHARACTER_SET_NAME;
        this.COLLATION_NAME = COLLATION_NAME;
        this.COLUMN_TYPE = COLUMN_TYPE;
        this.COLUMN_KEY = COLUMN_KEY;
        this.EXTRA = EXTRA;
        this.PRIVILEGES = PRIVILEGES;
        this.COLUMN_COMMENT = COLUMN_COMMENT;
    }

    public String getTABLE_CATALOG() {
        return TABLE_CATALOG;
    }

    public String getNUMERIC_SCALE() {
        return NUMERIC_SCALE;
    }

    public void setNUMERIC_SCALE(String NUMERIC_SCALE) {
        this.NUMERIC_SCALE = NUMERIC_SCALE;
    }

    public void setTABLE_CATALOG(String TABLE_CATALOG) {
        this.TABLE_CATALOG = TABLE_CATALOG;
    }

    public String getTABLE_SCHEMA() {
        return TABLE_SCHEMA;
    }

    public void setTABLE_SCHEMA(String TABLE_SCHEMA) {
        this.TABLE_SCHEMA = TABLE_SCHEMA;
    }

    public String getTABLE_NAME() {
        return TABLE_NAME;
    }

    public String getCOLUMN_DEFAULT() {
        return COLUMN_DEFAULT;
    }

    public void setCOLUMN_DEFAULT(String COLUMN_DEFAULT) {
        this.COLUMN_DEFAULT = COLUMN_DEFAULT;
    }

    public void setTABLE_NAME(String TABLE_NAME) {
        this.TABLE_NAME = TABLE_NAME;
    }

    public String getCOLUMN_NAME() {
        return COLUMN_NAME;
    }

    public void setCOLUMN_NAME(String COLUMN_NAME) {
        this.COLUMN_NAME = COLUMN_NAME;
    }

    public int getORDINAL_POSITION() {
        return ORDINAL_POSITION;
    }

    public void setORDINAL_POSITION(int ORDINAL_POSITION) {
        this.ORDINAL_POSITION = ORDINAL_POSITION;
    }

    public String getIS_NULLABLE() {
        return IS_NULLABLE;
    }

    public void setIS_NULLABLE(String IS_NULLABLE) {
        this.IS_NULLABLE = IS_NULLABLE;
    }

    public String getDATA_TYPE() {
        return DATA_TYPE;
    }

    public void setDATA_TYPE(String DATA_TYPE) {
        this.DATA_TYPE = DATA_TYPE;
    }

    public String getCHARACTER_MAXIMUM_LENGTH() {
        return CHARACTER_MAXIMUM_LENGTH;
    }

    public void setCHARACTER_MAXIMUM_LENGTH(String CHARACTER_MAXIMUM_LENGTH) {
        this.CHARACTER_MAXIMUM_LENGTH = CHARACTER_MAXIMUM_LENGTH;
    }

    public String getCHARACTER_OCTET_LENGTH() {
        return CHARACTER_OCTET_LENGTH;
    }

    public void setCHARACTER_OCTET_LENGTH(String CHARACTER_OCTET_LENGTH) {
        this.CHARACTER_OCTET_LENGTH = CHARACTER_OCTET_LENGTH;
    }

    public String getNUMERIC_PRECISION() {
        return NUMERIC_PRECISION;
    }

    public void setNUMERIC_PRECISION(String NUMERIC_PRECISION) {
        this.NUMERIC_PRECISION = NUMERIC_PRECISION;
    }

    public String getCHARACTER_SET_NAME() {
        return CHARACTER_SET_NAME;
    }

    public void setCHARACTER_SET_NAME(String CHARACTER_SET_NAME) {
        this.CHARACTER_SET_NAME = CHARACTER_SET_NAME;
    }

    public String getCOLLATION_NAME() {
        return COLLATION_NAME;
    }

    public void setCOLLATION_NAME(String COLLATION_NAME) {
        this.COLLATION_NAME = COLLATION_NAME;
    }

    public String getCOLUMN_TYPE() {
        return COLUMN_TYPE;
    }

    public void setCOLUMN_TYPE(String COLUMN_TYPE) {
        this.COLUMN_TYPE = COLUMN_TYPE;
    }

    public String getCOLUMN_KEY() {
        return COLUMN_KEY;
    }

    public void setCOLUMN_KEY(String COLUMN_KEY) {
        this.COLUMN_KEY = COLUMN_KEY;
    }

    public String getEXTRA() {
        return EXTRA;
    }

    public void setEXTRA(String EXTRA) {
        this.EXTRA = EXTRA;
    }

    public String getPRIVILEGES() {
        return PRIVILEGES;
    }

    public void setPRIVILEGES(String PRIVILEGES) {
        this.PRIVILEGES = PRIVILEGES;
    }

    public String getCOLUMN_COMMENT() {
        return COLUMN_COMMENT;
    }

    public void setCOLUMN_COMMENT(String COLUMN_COMMENT) {
        this.COLUMN_COMMENT = COLUMN_COMMENT;
    }
}
