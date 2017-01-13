package com.tuojin.bill.bean;

/**
 * Created by Administrator on 2017/1/13.
 */
public class MySqlStructure {
    private String Field;
    private String Type;
    private String Null;
    private String Key;
    private String Default;
    private String Extro;

    public MySqlStructure(String field, String type, String aNull, String key, String aDefault, String extro) {
        Field = field;
        Type = type;
        Null = aNull;
        Key = key;
        Default = aDefault;
        Extro = extro;
    }

    public String getField() {
        return Field;
    }

    public void setField(String field) {
        Field = field;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getNull() {
        return Null;
    }

    public void setNull(String aNull) {
        Null = aNull;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getDefault() {
        return Default;
    }

    public void setDefault(String aDefault) {
        Default = aDefault;
    }

    public String getExtro() {
        return Extro;
    }

    public void setExtro(String extro) {
        Extro = extro;
    }
}
