package com.example.common.util;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Excel工具类
 *
 * @author suhai
 * @since 2024-04-30
 */
public class ExcelUtils {
    public static final char DOT = '.';
    public static final Pattern EXCEL_EXT_PATTERN = Pattern.compile("\\.(xls|xlsx|csv)$", Pattern.CASE_INSENSITIVE);

    /**
     * 常量：扩展名
     */
    public static final String CSV_EXCEL_EXT = "csv";
    public static final String XLS_EXCEL_EXT = "xls";
    public static final String XLSX_EXCEL_EXT = "xlsx";

    /**
     * 常量：MIME响应类型
     */
    public static final String CSV_CONTENT_TYPE = "text/csv";
    public static final String XLS_CONTENT_TYPE = "application/vnd.ms-excel";
    public static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    /**
     * 常量：响应头
     */
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String CONTENT_DISPOSITION_TEMPLATE = "attachment;filename=%s;filename*=utf-8''%s";
    /**
     * 函数式接口：组装content_disposition响应头
     */
    public static final UnaryOperator<String> CONTENT_DISPOSITION_FUN = fileName ->
            String.format(CONTENT_DISPOSITION_TEMPLATE, fileName, fileName);

    /**
     * 配置Excel下载的响应流
     * 设置响应的内容类型、字符编码和Content-Disposition响应头，以便浏览器能正确下载和显示Excel文件
     *
     * @param response HttpServletResponse对象，用于设置响应流
     * @param fileName 要下载的Excel文件的名称
     */
    public static void configureResponseForExcelDownload(HttpServletResponse response, String fileName) {
        fileName = appendExtensionIfNeeded(fileName, XLSX_EXCEL_EXT);
        fileName = encodeFileNameForURL(fileName);
        response.setContentType(getContentType(fileName));
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader(CONTENT_DISPOSITION, CONTENT_DISPOSITION_FUN.apply(fileName));
    }

    /**
     * 如果文件名中不存在扩展名称，追加Excel扩展名，默认为.xlsx
     *
     * @param fileName       原始文件名称
     * @param excelExtension 要追加的Excel文件扩展名，如果为null或空字符串，则默认使用.xlsx
     * @return 处理后的包含Excel扩展名文件名称
     */
    public static String appendExtensionIfNeeded(String fileName, String excelExtension) {
        // 检查文件名是否存在Excel扩展名，存在则无需处理
        if (EXCEL_EXT_PATTERN.matcher(fileName).find()) {
            return fileName;
        }

        // 如果没有传入需要追加的Excel扩展名，默认使用.xlsx
        if (excelExtension == null || excelExtension.isEmpty()) {
            excelExtension = DOT + XLSX_EXCEL_EXT;
        }

        StringBuilder sb = new StringBuilder(fileName);
        // 确保文件名末尾没有点号，如果有则移除它
        if (sb.charAt(sb.length() - 1) == DOT) {
            sb.deleteCharAt(sb.length() - 1);
        }
        // 追加Excel后缀
        sb.append(excelExtension);

        return sb.toString();
    }

    /**
     * 根据文件名称的Excel扩展名，获取对应MIME的ContentType
     *
     * @param fileName 包含Excel扩展名的文件名
     * @return Excel对应的ContentType，如果未找到则默认返回XLSX对应的ContentType
     */
    public static String getContentType(String fileName) {
        String extName = getExtName(fileName);
        if (extName == null) {
            return XLSX_CONTENT_TYPE;
        }

        String contentType = null;
        if (CSV_EXCEL_EXT.equalsIgnoreCase(extName)) {
            contentType = CSV_CONTENT_TYPE;
        } else if (XLS_EXCEL_EXT.equalsIgnoreCase(extName)) {
            contentType = XLS_CONTENT_TYPE;
        } else if (XLSX_EXCEL_EXT.equalsIgnoreCase(extName)) {
            contentType = XLSX_CONTENT_TYPE;
        }

        return contentType;
    }

    /**
     * 从文件名中提取扩展名
     *
     * @param fileName 文件名
     * @return 提取到的扩展名字符串，如果文件名不包含扩展名，则返回null
     */
    private static String getExtName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        int dotIndex = fileName.lastIndexOf(DOT);
        if (dotIndex == -1) {
            return null;
        }

        return fileName.substring(dotIndex + 1);
    }

    /**
     * 对文件名进行URL编码，以符合RFC 3986标准并确保其可以安全地用作HTTP响应头的一部分
     * <p>
     * 使用UTF-8字符集进行编码，并将编码后的"+"字符替换为"%20"
     *
     * @param fileName 要进行URL编码的文件名。
     * @return 编码后的文件名字符串。
     */
    public static String encodeFileNameForURL(String fileName) {
        try {
            return URLEncoder.encode(fileName, StandardCharsets.UTF_8.name())
                    .replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 should be supported", e);
        }
    }
}