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
    public QPFilesEntity getQPFilesByUser(Long userId, String subjectId, int setNo) {
        return qpFilesRepository.findByUserIdAndSubjectIdAndSetNo(userId,Long.parseLong(subjectId),setNo);
    }

    @Override
    public QPFilesEntity saveQPs(QPFilesEntity qpFilesEntity) {
      return  qpFilesRepository.save(qpFilesEntity);
    }
}
