package com.moxi.mogublog.picture.restapi;

import com.alibaba.fastjson.JSON;

import com.moxi.mogublog.picture.entity.NetworkDisk;
import com.moxi.mogublog.picture.entity.TreeNode;
import com.moxi.mogublog.picture.global.SysConf;
import com.moxi.mogublog.picture.service.NetworkDiskService;
import com.moxi.mogublog.picture.service.StorageService;
import com.moxi.mogublog.utils.RestResult;
import com.moxi.mogublog.utils.upload.FileOperation;
import com.moxi.mogublog.utils.upload.FileUtil;
import com.moxi.mogublog.utils.upload.PathUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.annotation.Resource;
import java.io.File;
import java.util.*;


@RestController
@RequestMapping("/networkDisk")
public class NetworkDiskRestApi {

    @Resource
    NetworkDiskService networkDiskService;

    @Resource
    StorageService filetransferService;

    /**
     * 是否开启共享文件模式
     */
    public static Boolean isShareFile = false;

    public static long treeid = 0;

    /**
     * @return
     */
    @RequestMapping("/fileindex")
    @ResponseBody
    public ModelAndView essayIndex() {
        ModelAndView mv = new ModelAndView("/file/fileIndex.html");
        return mv;
    }

    /**
     * 创建文件
     *
     * @return
     */
    @RequestMapping(value = "/createfile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> createFile(@RequestBody NetworkDisk networkDisk) {
        RestResult<String> restResult = new RestResult<>();
        if (!operationCheck().isSuccess()){
            return operationCheck();
        }
        networkDisk.setAdminUid(SysConf.DEFAULT_UID);
        networkDiskService.insertFile(networkDisk);
        restResult.setSuccess(true);
        return restResult;
    }

    @RequestMapping(value = "/getfilelist", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<List<NetworkDisk>> getFileList(@RequestBody NetworkDisk networkDisk){
        RestResult<List<NetworkDisk>> restResult = new RestResult<>();
        networkDisk.setFilePath(PathUtil.urlDecode(networkDisk.getFilePath()));
        List<NetworkDisk> fileList = networkDiskService.selectFileList(networkDisk);
        restResult.setData(fileList);
        restResult.setSuccess(true);
        return restResult;
    }

    /**
     * 批量删除文件
     *
     * @return
     */
    @RequestMapping(value = "/batchdeletefile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> deleteImageByIds(@RequestBody NetworkDisk networkDisk) {
        RestResult<String> result = new RestResult<>();
        if (!operationCheck().isSuccess()) {
            return operationCheck();
        }

        List<NetworkDisk> fileList = JSON.parseArray(networkDisk.getFiles(), NetworkDisk.class);
        for (NetworkDisk file : fileList) {
            networkDiskService.deleteFile(file);
        }
        result.setData("批量删除文件成功");
        result.setSuccess(true);
        return result;
    }

    /**
     * 删除文件
     *
     * @return
     */
    @RequestMapping(value = "/deletefile", method = RequestMethod.POST)
    @ResponseBody
    public String deleteFile(@RequestBody NetworkDisk networkDisk) {
        RestResult<String> result = new RestResult<>();
        if (!operationCheck().isSuccess()){
            return JSON.toJSONString(operationCheck());
        }
        networkDiskService.deleteFile(networkDisk);
        result.setSuccess(true);
        String resultJson = JSON.toJSONString(result);
        return resultJson;
    }

    /**
     * 解压文件
     *
     * @return
     */
    @RequestMapping(value = "/unzipfile", method = RequestMethod.POST)
    @ResponseBody
    public String unzipFile(@RequestBody NetworkDisk networkDisk) {
        RestResult<String> result = new RestResult<>();
        if (!operationCheck().isSuccess()){
            return JSON.toJSONString(operationCheck());
        }

        String zipFileUrl = PathUtil.getStaticPath() + networkDisk.getFileUrl();
        File file = FileOperation.newFile(zipFileUrl);
        String unzipUrl = file.getParent();

        List<String> fileEntryNameList = FileOperation.unzip(file, unzipUrl);

        List<NetworkDisk> fileBeanList = new ArrayList<>();
        for (int i = 0; i < fileEntryNameList.size(); i++){
            String entryName = fileEntryNameList.get(i);
            String totalFileUrl = unzipUrl + entryName;
            File currentFile = FileOperation.newFile(totalFileUrl);

            NetworkDisk tempFileBean = new NetworkDisk();
            tempFileBean.setCreateTime(new Date());
            tempFileBean.setAdminUid(SysConf.DEFAULT_UID);
            tempFileBean.setFilePath(FileUtil.pathSplitFormat(networkDisk.getFilePath() + entryName.replace(currentFile.getName(), "")));
            if (currentFile.isDirectory()){

                tempFileBean.setIsDir(1);

                tempFileBean.setFileName(currentFile.getName());
                tempFileBean.setTimestampName(currentFile.getName());
                //tempFileBean.setFileurl(File.separator + (file.getParent() + File.separator + currentFile.getName()).replace(PathUtil.getStaticPath(), ""));
            }else{

                tempFileBean.setIsDir(0);

                tempFileBean.setExtendName(FileUtil.getFileType(totalFileUrl));
                tempFileBean.setFileName(FileUtil.getFileNameNotExtend(currentFile.getName()));
                tempFileBean.setFileSize(currentFile.length());
                tempFileBean.setTimestampName(FileUtil.getFileNameNotExtend(currentFile.getName()));
                tempFileBean.setFileUrl(File.separator + (currentFile.getPath()).replace(PathUtil.getStaticPath(), ""));
            }
            fileBeanList.add(tempFileBean);
        }
        networkDiskService.batchInsertFile(fileBeanList);
        result.setSuccess(true);
        String resultJson = JSON.toJSONString(result);
        return resultJson;
    }

    /**
     * 文件移动
     *
     *
     * @return 返回前台移动结果
     */
    @RequestMapping(value = "/movefile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> moveFile(@RequestBody NetworkDisk networkDisk) {
        RestResult<String> result = new RestResult<String>();
        if (!operationCheck().isSuccess()){
            return operationCheck();
        }
        String oldfilepath = networkDisk.getOldFilePath();
        String newfilepath = networkDisk.getNewFilePath();
        String filename = networkDisk.getFileName();
        String extendname = networkDisk.getExtendName();

        networkDiskService.updateFilepathByFilepath(oldfilepath, newfilepath, filename, extendname);
        result.setSuccess(true);
        return result;
    }

    /**
     * 批量移动文件
     *
     *
     * @return 返回前台移动结果
     */
    @RequestMapping(value = "/batchmovefile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> batchMoveFile(@RequestBody NetworkDisk networkDisk) {

        RestResult<String> result = new RestResult<String>();
        if (!operationCheck().isSuccess()) {
            return operationCheck();
        }

        String files = networkDisk.getFiles();
        String newfilepath = networkDisk.getNewFilePath();

        List<NetworkDisk> fileList = JSON.parseArray(files, NetworkDisk.class);

        for (NetworkDisk file : fileList) {
            networkDiskService.updateFilepathByFilepath(file.getFilePath(), newfilepath, file.getFileName(), file.getExtendName());
        }

        result.setData("批量移动文件成功");
        result.setSuccess(true);
        return result;
    }

    public RestResult<String> operationCheck(){
        RestResult<String> result = new RestResult<String>();
        result.setSuccess(true);
        return result;
    }

    /**
     * 通过文件类型选择文件
     * @return
     */
    @RequestMapping(value = "/selectfilebyfiletype", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<List<NetworkDisk>> selectFileByFileType(NetworkDisk networkDisk) {
        RestResult<List<NetworkDisk>> result = new RestResult<>();
        List<NetworkDisk> file = networkDiskService.selectFileByExtendName(FileUtil.getFileExtendsByType(networkDisk.getFileType()), SysConf.DEFAULT_UID);
        result.setData(file);
        result.setSuccess(true);
        return result;
    }

    /**
     * 获取文件树
     * @return
     */
    @RequestMapping(value = "/getfiletree", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<TreeNode> getFileTree(){
        RestResult<TreeNode> result = new RestResult<TreeNode>();
        NetworkDisk networkDisk = new NetworkDisk();
        List<NetworkDisk> filePathList = networkDiskService.selectFilePathTreeByUserid(networkDisk);
        TreeNode resultTreeNode = new TreeNode();
        resultTreeNode.setNodeName("/");

        for (int i = 0; i < filePathList.size(); i++){
            String filePath = filePathList.get(i).getFilePath() + filePathList.get(i).getFileName() + "/";

            Queue<String> queue = new LinkedList<>();
            String[] strArr = filePath.split("/");
            for (int j = 0; j < strArr.length; j++){
                if (!"".equals(strArr[j]) && strArr[j] != null){
                    queue.add(strArr[j]);
                }
            }
            if (queue.size() == 0){
                continue;
            }
            resultTreeNode = insertTreeNode(resultTreeNode,"/", queue);
        }
        result.setSuccess(true);
        result.setData(resultTreeNode);
        return result;

    }

    public TreeNode insertTreeNode(TreeNode treeNode, String filepath, Queue<String> nodeNameQueue){

        List<TreeNode> childrenTreeNodes = treeNode.getChildren();
        String currentNodeName = nodeNameQueue.peek();
        if (currentNodeName == null){
            return treeNode;
        }

        Map<String, String> map = new HashMap<>();
        filepath = filepath + currentNodeName + "/";
        map.put("filepath", filepath);

        if (!isExistPath(childrenTreeNodes, currentNodeName)){  //1、判断有没有该子节点，如果没有则插入
            //插入
            TreeNode resultTreeNode = new TreeNode();
            resultTreeNode.setAttributes(map);
            resultTreeNode.setNodeName(nodeNameQueue.poll());
            resultTreeNode.setId(treeid++);

            childrenTreeNodes.add(resultTreeNode);

        }else{  //2、如果有，则跳过
            nodeNameQueue.poll();
        }

        if (nodeNameQueue.size() != 0) {
            for (int i = 0; i < childrenTreeNodes.size(); i++) {

                TreeNode childrenTreeNode = childrenTreeNodes.get(i);
                if (currentNodeName.equals(childrenTreeNode.getLabel())){
                    childrenTreeNode = insertTreeNode(childrenTreeNode, filepath, nodeNameQueue);
                    childrenTreeNodes.remove(i);
                    childrenTreeNodes.add(childrenTreeNode);
                    treeNode.setChildNode(childrenTreeNodes);
                }

            }
        }else{
            treeNode.setChildNode(childrenTreeNodes);
        }
        return treeNode;
    }

    public boolean isExistPath(List<TreeNode> childrenTreeNodes, String path){
        boolean isExistPath = false;

        try {
            for (int i = 0; i < childrenTreeNodes.size(); i++){
                if (path.equals(childrenTreeNodes.get(i).getLabel())){
                    isExistPath = true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return isExistPath;
    }


}
