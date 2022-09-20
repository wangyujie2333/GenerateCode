package com.idea.plugin.text.json.json5;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ApiDocObjectSerialPo {
    /**
     * 类型全名
     */
    String type;
    /**
     * 别名
     */
    String alias;
    /**
     * 默认值
     */
    String value;

    public ApiDocObjectSerialPo deepCopy() {
        return new ApiDocObjectSerialPo(this.type, this.alias, this.value);
    }

    public ApiDocObjectSerialPo(String type, String alias, String value) {
        this.type = type;
        this.alias = alias;
        this.value = value;
    }

    public ApiDocObjectSerialPo() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 默认值设置
     *
     * @return {@link List}
     */
    static List<ApiDocObjectSerialPo> defaultObjects() {
        List<ApiDocObjectSerialPo> objects = new ArrayList<>();
//        WebCopyConstants.PRIMITIVE_SERIALS.stream().map(ApiDocObjectSerialPo::deepCopy).forEach(objects::add);
//        WebCopyConstants.WRAPPED_SERIALS.stream().map(ApiDocObjectSerialPo::deepCopy).forEach(objects::add);

        return objects;
    }

    public boolean isOk() {
        return
                Objects.nonNull(this.type) && !this.type.isEmpty()
                        && Objects.nonNull(this.alias) && !this.alias.isEmpty()
                        && Objects.nonNull(this.value) && !this.value.isEmpty();
    }
}
