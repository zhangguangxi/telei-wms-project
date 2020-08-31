package com.telei.wms.commons.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * ZIP 压缩和解压缩
 */
public class ZipCompressor {

    /**
     * 待压缩条目
     */
    private Map<String, Object> compressionEntryMap = new ConcurrentHashMap<>();
    /**
     * 需解压缩文件
     */
    private Collection<File> compressedFiles = new ArrayList<>();

    /**
     * 是否处理重名的压缩文件
     */
    private boolean handleEntryDuplicateName = false;

    /**
     * 压缩条目重复名称计数器
     */
    private Map<String, AtomicInteger> entryDuplicateNameCounter = new HashMap<>();

    /**
     * 压缩条目重复名称标记
     */
    private static final String ENTRY_DUPLICATE_NAME_MARK = " ({0})";

    private ZipCompressor() {
    }

    /**
     * 创建一个压缩器
     *
     * @return
     */
    public static ZipCompressor create() {
        return new ZipCompressor();
    }

    public ZipCompressor handleEntryDuplicateName() {
        this.handleEntryDuplicateName = true;
        return this;
    }

    public ZipCompressor unHandleEntryDuplicateName() {
        this.handleEntryDuplicateName = false;
        return this;
    }

    /**
     * 添加需要压缩的条目
     *
     * @param data
     * @return
     */
    public ZipCompressor addCompressEntry(File data) {
        return putCompressEntry(data.getName(), data);
    }

    /**
     * 添加需要压缩的条目
     *
     * @param data
     * @param name
     * @return
     */
    public ZipCompressor addCompressEntry(File data, String name) {
        return putCompressEntry(name, data);
    }

    /**
     * 添加需要压缩的条目
     *
     * @param data
     * @param name
     * @return
     */
    public ZipCompressor addCompressEntry(byte[] data, String name) {
        return putCompressEntry(name, data);
    }

    /**
     * 添加压缩条目
     *
     * @param name
     * @param data
     * @return
     */
    private ZipCompressor putCompressEntry(String name, Object data) {
        if (!this.handleEntryDuplicateName || !this.compressionEntryMap.containsKey(name)) {
            this.compressionEntryMap.put(name, data);
            return this;
        }
        // 压缩条目重名处理
        if (!this.entryDuplicateNameCounter.containsKey(name)) {
            this.entryDuplicateNameCounter.put(name, new AtomicInteger(2));
        }
        // 重复数量
        int count = this.entryDuplicateNameCounter.get(name).getAndIncrement();
        // 重复标记
        String entryDuplicateNameMark = ENTRY_DUPLICATE_NAME_MARK.replace("{0}", String.valueOf(count));
        // 无后缀的情况
        if (!name.contains(".")) {
            this.compressionEntryMap.put(name + entryDuplicateNameMark, data);
            return this;
        }
        // 有后缀的情况
        int suffixIndex = name.lastIndexOf(".");
        this.putCompressEntry(name.substring(0, suffixIndex) + entryDuplicateNameMark + name.substring(suffixIndex), data);
        return this;
    }

    /**
     * 添加需要解压的文件
     *
     * @param compressedFile
     * @return
     */
    public ZipCompressor addCompressedFile(File compressedFile) {
        this.compressedFiles.add(compressedFile);
        return this;
    }

    /**
     * 添加需要解压的文件
     *
     * @param compressedFiles
     * @return
     */
    public ZipCompressor addCompressedFile(Collection<File> compressedFiles) {
        this.compressedFiles.addAll(compressedFiles);
        return this;
    }

    /**
     * 执行压缩
     *
     * @return
     */
    public byte[] compression() {
        final ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream();
        final ZipOutputStream zipOutputStream = new ZipOutputStream(bytesOutputStream);
        try {
            for (Map.Entry<String, Object> entry : compressionEntryMap.entrySet()) {
                final String key = entry.getKey();
                final Object value = entry.getValue();
                if (byte[].class == value.getClass()) {
                    compressionEntry(zipOutputStream, (byte[]) value, key);
                    continue;
                }
                if (File.class == value.getClass()) {
                    compressionEntry(zipOutputStream, (File) value, key);
                    continue;
                }
                throw new UnsupportedOperationException("不支持的压缩数据类型 : " + value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                zipOutputStream.closeEntry();
                zipOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytesOutputStream.toByteArray();
    }

    /**
     * 压缩条目
     *
     * @param outputStream
     * @param data
     * @param entryName
     * @throws IOException
     */
    private void compressionEntry(ZipOutputStream outputStream, byte[] data, String entryName) throws IOException {
        outputStream.putNextEntry(new ZipEntry(entryName));
        outputStream.write(data);
    }

    /**
     * 压缩条目
     *
     * @param outputStream
     * @param data
     * @param entryName
     * @throws IOException
     */
    private void compressionEntry(ZipOutputStream outputStream, File data, String entryName) throws IOException {
        // 目录
        if (data.isDirectory()) {
            // 目录需要 / 结尾来标识
            outputStream.putNextEntry(new ZipEntry(entryName + File.separator));
            File[] files = data.listFiles();
            if (files.length == 0) {
                return;
            }
            for (File file : files) {
                compressionEntry(outputStream, file, entryName + File.separator + file.getName());
            }
            return;
        }
        // 文件
        outputStream.putNextEntry(new ZipEntry(entryName));
        outputStream.write(Files.readAllBytes(Paths.get(data.toURI())));
    }

    /**
     * 执行解压缩
     *
     * @param outputDirectory
     * @throws IOException
     */
    public void decompression(File outputDirectory) throws IOException {
        for (File file : compressedFiles) {
            final ZipFile zipFile = new ZipFile(file);
            try {
                final Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    final ZipEntry zipEntry = entries.nextElement();
                    final String name = zipEntry.getName();

                    // 解压后的目标文件
                    final File destFile = new File(String.join(File.separator, outputDirectory.getAbsolutePath(), name));

                    // 解压目录
                    if (zipEntry.isDirectory()) {
                        if (!destFile.exists()) {
                            destFile.mkdirs();
                        }
                        continue;
                    }

                    // 解压文件
                    if (!destFile.getParentFile().exists()) {
                        destFile.getParentFile().mkdirs();
                    }
                    Files.copy(zipFile.getInputStream(zipEntry), Paths.get(destFile.toURI()));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                zipFile.close();
            }

        }
    }

}
