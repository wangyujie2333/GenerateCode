package com.idea.plugin.apidoc;

public interface APIDocConstand {

    String REQUEST_API_DOC = "## ${name}\n" +
            "```text\n" +
            "${describe}\n" +
            "```\n" +
            "\n" +
            "#### 接口URL\n" +
            "> ${url}\n" +
            "\n" +
            "#### 请求参数\n" +
            "```json\n" +
            "${request}\n" +
            "```\n" +
            "\n" +
            "#### 响应示例\n" +
            "```json\n" +
            "${reponse}\n" +
            "```";
    String INTERFACE_API_DOC = "## ${name}\n" +
            "```text\n" +
            "${describe}\n" +
            "```\n" +
            "\n" +
            "#### 接口URL\n" +
            "> ${url}\n" +
            "\n" +
            "#### 请求参数\n" +
            "```json\n" +
            "${request}\n" +
            "```\n" +
            "\n" +
            "#### 响应示例\n" +
            "```json\n" +
            "${reponse}\n" +
            "```";
    String JSON = "application/json";
    String FORM_DATA = "multipart/form-data";
}
