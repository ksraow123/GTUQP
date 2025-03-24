package in.coempt.service.impl;

import in.coempt.entity.QPFilesEntity;
import in.coempt.repository.QPFilesRepository;
import in.coempt.service.QPFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QPFilesServiceImpl implements QPFilesService {
    @Autowired
    QPFilesRepository qpFilesRepository;
    @Override
    public QPFilesEntity getQPFilesByUser(Long userId, String subjectId, int setNo,Long setterId) {
        return qpFilesRepository.findByUserIdAndSubjectIdAndSetNoAndQpSetterId(userId,Long.parseLong(subjectId),setNo,setterId);
    }

    @Override
    public QPFilesEntity saveQPs(QPFilesEntity qpFilesEntity) {
      return  qpFilesRepository.save(qpFilesEntity);
    }

    @Override
    public QPFilesEntity getQPFilesByRollIdAndSubjectIdAndSetNoAndSetterId(int roleId, String subjectId, int setNo,Long setterId) {
        return qpFilesRepository.findByRollIdAndSubjectIdAndSetNoAndQpSetterId(roleId,Long.parseLong(subjectId),setNo,setterId);
    }
}
