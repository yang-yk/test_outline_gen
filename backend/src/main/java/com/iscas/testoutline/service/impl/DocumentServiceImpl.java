package com.iscas.testoutline.service.impl;

import com.iscas.testoutline.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

    @Override
    public String readWordDocument(InputStream inputStream) {
        StringBuilder content = new StringBuilder();
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            // 读取段落
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                content.append(paragraph.getText()).append("\n");
            }
            // 读取表格
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        content.append(cell.getText()).append("\t");
                    }
                    content.append("\n");
                }
            }
        } catch (IOException e) {
            log.error("读取Word文档失败", e);
            throw new RuntimeException("读取Word文档失败", e);
        }
        return content.toString();
    }

    @Override
    public void writeWordDocument(String content, OutputStream outputStream) {
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(content);
            document.write(outputStream);
        } catch (IOException e) {
            log.error("写入Word文档失败", e);
            throw new RuntimeException("写入Word文档失败", e);
        }
    }

    @Override
    public void writeTestOutline(String outline, OutputStream outputStream) {
        try (XWPFDocument document = new XWPFDocument()) {
            // 添加标题
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("测试大纲");
            titleRun.setBold(true);
            titleRun.setFontSize(16);
            titleRun.setFontFamily("宋体");
            
            // 添加内容
            XWPFParagraph content = document.createParagraph();
            content.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun contentRun = content.createRun();
            contentRun.setText(outline);
            contentRun.setFontSize(12);
            contentRun.setFontFamily("宋体");
            
            document.write(outputStream);
        } catch (IOException e) {
            log.error("生成测试大纲文档失败", e);
            throw new RuntimeException("生成测试大纲文档失败", e);
        }
    }
} 