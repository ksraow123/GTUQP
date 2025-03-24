package in.coempt.service;

import in.coempt.entity.QPFilesEntity;

public interface QPFilesService {
    QPFilesEntity getQPFilesByUser(Long userId, String subjectId, int setNo,Long setterId);

    QPFilesEntity saveQPs(QPFilesEntity qpFilesEntity);

    QPFilesEntity getQPFilesByRollIdAndSubjectIdAndSetNoAndSetterId(int roleId, String subjectId, int setNo,Long setterId);

}
