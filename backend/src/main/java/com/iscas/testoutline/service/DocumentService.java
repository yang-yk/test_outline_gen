package com.iscas.testoutline.service;

import java.io.InputStream;
import java.io.OutputStream;

public interface DocumentService {
    /**
     * 从Word文档中读取文本内容
     *
     * @param inputStream Word文档输入流
     * @return 文档中的文本内容
     */
    String readWordDocument(InputStream inputStream);

    /**
     * 将文本内容写入Word文档
     *
     * @param content 要写入的内容
     * @param outputStream Word文档输出流
     */
    void writeWordDocument(String content, OutputStream outputStream);

    /**
     * 将测试大纲写入Word文档
     *
     * @param outline 测试大纲内容
     * @param outputStream Word文档输出流
     */
    void writeTestOutline(String outline, OutputStream outputStream);
} 