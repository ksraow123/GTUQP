package in.coempt.service;

import in.coempt.entity.QpUploadDetailsEntity;

import java.util.List;

public interface QPUploadService {

    List<QpUploadDetailsEntity> getQPsfilesBySubject(String subjectId);

    void saveQPs(QpUploadDetailsEntity qpUploadDetails);

    QpUploadDetailsEntity getQPBySetNoAndSubjectId(int setNo, int i);

    QpUploadDetailsEntity getQPById(Long i);

    List<QpUploadDetailsEntity>  saveBulkQps(List<QpUploadDetailsEntity> uploadDetailsEntities);

}
