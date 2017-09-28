package com.bupt.service.impl;

import com.bupt.dao.ResOsnrAmplifierDao;
import com.bupt.entity.ResOsnrAmplifier;
import com.bupt.pojo.AmplifierCreateInfo;
import com.bupt.pojo.AmplifierDTO;
import com.bupt.service.AmplifierService;
import com.bupt.util.exception.controller.result.NoneGetException;
import com.bupt.util.exception.controller.result.NoneRemoveException;
import com.bupt.util.exception.controller.result.NoneSaveException;
import com.bupt.util.exception.controller.result.NoneUpdateException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by caoxiaohong on 17/9/13.
 */
@Service(value = "amplifierService")
public class AmplifierServiceImpl implements AmplifierService {
    @Resource
    private ResOsnrAmplifierDao resOsnrAmplifierDao;

    @Override
    public AmplifierDTO updateAmplifiers(Long versionId, Long amplifierID, AmplifierCreateInfo amplifierCreateInfo) {
        if (resOsnrAmplifierDao.selectByExample(getExample(versionId,amplifierID)).size() > 0) {
            ResOsnrAmplifier roa = amplifierDtoToDao(amplifierCreateInfo);
            if (resOsnrAmplifierDao.updateByExampleSelective(roa,getExample(versionId,amplifierID)) > 0) {
                roa = resOsnrAmplifierDao.selectByExample(getExample(versionId,amplifierID)).get(0);
                return amplifierDaoToDto(roa);
            } else {
                throw new NoneUpdateException();
            }
        } else {
            throw new NoneGetException();
        }
    }

    private Example getExample(Long amplifierID) {
        Example condition = new Example(ResOsnrAmplifier.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("versionId", amplifierID);
        return condition;
    }


    private Example getExample(Long amplifierID,Long versionId) {
        Example condition = new Example(ResOsnrAmplifier.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("amplifierId", amplifierID);
        criteria.andEqualTo("versionId", versionId);
        return condition;
    }

    @Override
    @Transactional
    public boolean deleteByAmpid(Long versionId, List<Long> listAmpid) {
        //只能按照迭代器方式删除,不能for删除
        for (Long aListAmpid : listAmpid) {
            if (resOsnrAmplifierDao.deleteByPrimaryKey(aListAmpid) == 0) {
                throw new NoneRemoveException();
            }
        }
        return true;
    }


    @Override
    @Transactional
    public AmplifierDTO insertAmplifier(Long versionId, AmplifierCreateInfo amplifierCreateInfo) {
        ResOsnrAmplifier roa = amplifierDtoToDao(amplifierCreateInfo);
        roa.setVersionId(versionId);
        if (resOsnrAmplifierDao.insertSelective(roa) < 0) {
            throw new NoneSaveException();
        } else {
            ResOsnrAmplifier result = resOsnrAmplifierDao.selectOne(roa);
            if (null== result){
                throw new NoneSaveException();
            }
            return amplifierDaoToDto(result);
        }
    }

    @Override
    public List<AmplifierDTO> selectAmplifiers(Long versionID) {
        List<ResOsnrAmplifier> list = resOsnrAmplifierDao.selectByExample(getExample(versionID));
        if (list.size() <= 0) {
            throw new NoneGetException();
        }
        List<AmplifierDTO> result = new ArrayList<>();
        for (ResOsnrAmplifier i : list) {
            AmplifierDTO amplifierDTO = amplifierDaoToDto(i);
            result.add(amplifierDTO);
        }
        return result;
    }

    @Override
    public void batchRemove(Long versionId) {
        resOsnrAmplifierDao.deleteByExample(getExample(versionId));
    }

    @Override
    public void batchCreate(Long baseVersionId, Long newVersionId) {
        List<ResOsnrAmplifier> oldInfo = resOsnrAmplifierDao.selectByExample(getExample(baseVersionId));
        for (ResOsnrAmplifier amplifier : oldInfo) {
            AmplifierCreateInfo newAmp = new AmplifierCreateInfo();
            BeanUtils.copyProperties(amplifier, newAmp);
            insertAmplifier(newVersionId, newAmp);
        }
    }

    /**
     * dto转为dao
     *
     * @param amplifierCreateInfo
     * @return
     */
    private ResOsnrAmplifier amplifierDtoToDao(AmplifierCreateInfo amplifierCreateInfo) {
        ResOsnrAmplifier result = new ResOsnrAmplifier();
        if (amplifierCreateInfo == null)
            return result;
        result.setAmplifierName(amplifierCreateInfo.getAmplifierName());
        result.setGain(amplifierCreateInfo.getGain());
        result.setMinimumInputPower(amplifierCreateInfo.getMinimumInputPower());
        result.setMaximumOutputPower(amplifierCreateInfo.getMaximumOutputPower());
        result.setMaximumInputPower(amplifierCreateInfo.getMaximumInputPower());
        return result;
    }

    /**
     * dao转为dto
     *
     * @param resOsnrAmplifier
     * @return
     */
    private AmplifierDTO amplifierDaoToDto(ResOsnrAmplifier resOsnrAmplifier) {
        AmplifierDTO result = new AmplifierDTO();
        if (resOsnrAmplifier == null)
            return result;
        result.setAmplifierID(resOsnrAmplifier.getAmplifierId());
        result.setAmplifierName(resOsnrAmplifier.getAmplifierName());
        result.setGain(resOsnrAmplifier.getGain());
        result.setMaximumInputPower(resOsnrAmplifier.getMaximumInputPower());
        result.setMaximumOutputPower(resOsnrAmplifier.getMaximumOutputPower());
        result.setMinimumInputPower(resOsnrAmplifier.getMinimumInputPower());
        return result;
    }
}