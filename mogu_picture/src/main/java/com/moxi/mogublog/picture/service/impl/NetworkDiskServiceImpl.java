package com.moxi.mogublog.picture.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moxi.mogublog.picture.entity.File;
import com.moxi.mogublog.picture.entity.FileSort;
import com.moxi.mogublog.picture.entity.NetworkDisk;
import com.moxi.mogublog.picture.global.SQLConf;
import com.moxi.mogublog.picture.global.SysConf;
import com.moxi.mogublog.picture.mapper.FileMapper;
import com.moxi.mogublog.picture.mapper.NetworkDiskMapper;
import com.moxi.mogublog.picture.service.FileService;
import com.moxi.mogublog.picture.service.FileSortService;
import com.moxi.mogublog.picture.service.NetworkDiskService;
import com.moxi.mogublog.picture.util.QiniuUtil;
import com.moxi.mogublog.utils.*;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import com.qiniu.common.QiniuException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 文件服务实现类
 * </p>
 *
 * @author xuzhixiang
 * @since 2018-09-17
 */
@Slf4j
@Service
public class NetworkDiskServiceImpl extends SuperServiceImpl<NetworkDiskMapper, NetworkDisk> implements NetworkDiskService {

    @Autowired
    NetworkDiskService networkDiskService;

    @Override
    public void insertFile(NetworkDisk networkDisk) {
        networkDisk.insert();
    }

    @Override
    public void batchInsertFile(List<NetworkDisk> fileBeanList) {

    }

    @Override
    public void updateFile(NetworkDisk fileBean) {

    }

    @Override
    public NetworkDisk selectFileById(NetworkDisk fileBean) {
        return null;
    }

    @Override
    public List<NetworkDisk> selectFilePathTreeByUserid(NetworkDisk fileBean) {
        return null;
    }

    @Override
    public List<NetworkDisk> selectFileList(NetworkDisk networkDisk) {
        QueryWrapper<NetworkDisk> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SysConf.STATUS, EStatus.ENABLE);
        if(StringUtils.isNotEmpty(networkDisk.getFilePath())) {
            queryWrapper.eq(SQLConf.FILE_PATH, networkDisk.getFilePath());
        }
        List<NetworkDisk> list = networkDiskService.list(queryWrapper);
        return list;
    }

    @Override
    public List<NetworkDisk> selectFileListByIds(List<Integer> fileidList) {
        return null;
    }

    @Override
    public List<NetworkDisk> selectFileTreeListLikeFilePath(String filePath) {
        return null;
    }

    @Override
    public void deleteFile(NetworkDisk fileBean) {

    }

    @Override
    public void deleteFileByIds(List<Integer> fileidList) {

    }

    @Override
    public void updateFilepathByFilepath(String oldfilepath, String newfilepath, String filename, String extendname) {

    }

    @Override
    public List<NetworkDisk> selectFileByExtendName(List<String> filenameList, String adminUid) {
        return null;
    }
}
