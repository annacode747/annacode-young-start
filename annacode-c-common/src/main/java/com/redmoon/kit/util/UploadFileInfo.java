package com.redmoon.kit.util;

import java.util.Vector;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class UploadFileInfo {
    public static final int state_inited = 0;
    public static final int state_started = 1;
    public static final int state_finished = 2;

    public UploadFileInfo() {
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public void setClientFilePath(String clientFilePath) {
        this.clientFilePath = clientFilePath;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileId() {
        return fileId;
    }

    public String getClientFilePath() {
        return clientFilePath;
    }

    public int getState() {
        return state;
    }

    public synchronized void addUploadThreadInfo(UploadThreadInfo uti) {
        uploadThreadInfos.addElement(uti);
    }

    public synchronized void removeUploadFileInfo(UploadThreadInfo uti) {
        uploadThreadInfos.remove(uti);
    }

    public String getBlockName(int blockId) {
        String blockName = fileId + "_" + blockId +
                "." +
                getFileExt();
        return blockName;
    }

    public Vector getUploadThreadInfos() {
        return uploadThreadInfos;
    }

    public String getFileName() {
        if (!fileName.equals(""))
            return fileName;
        int pos = clientFilePath.lastIndexOf("\\");
        if (pos != -1) {
            fileName = clientFilePath.substring(pos + 1);
        } else {
            fileName = clientFilePath;
        }
        return fileName;
    }

    public String getFileExt() {
        if (!fileExt.equals(""))
            return fileExt;
        // ??????????????????????????????????????????????????????html?????????????????????
        // extName = fileName.substring(fileName.length() - 3, fileName.length()); //?????????
        if (fileName.equals(""))
            fileName = getFileName();
        int dotindex = fileName.lastIndexOf(".");
        fileExt = fileName.substring(dotindex + 1, fileName.length());
        fileExt = fileExt.toLowerCase(); //????????????
        return fileExt.toLowerCase();
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFullSavePath(String realPath) {
        String path = realPath + filePath + "/" + fileId + "." + getFileExt();
        return path;
    }

    public String getFullSavePath(String realPath, boolean isRandName) {
        if (isRandName)
            return getFullSavePath(realPath);
        else {
            return realPath + filePath + "/" + fileName;
        }
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    private String fileId;
    private Vector uploadThreadInfos = new Vector();
    private String clientFilePath;
    private int state = state_inited;
    private String fileName = ""; // ???????????????
    private String fileExt = "";
    private String filePath; // ????????????????????????????????????
    private long receiveTime = System.currentTimeMillis();
}
