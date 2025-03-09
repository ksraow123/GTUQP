package in.coempt.service;

import in.coempt.entity.QPFilesEntity;

public interface QPFilesService {
    QPFilesEntity getQPFilesByUser(Long userId, String subjectId, int setNo);

    QPFilesEntity saveQPs(QPFilesEntity qpFilesEntity);
}
