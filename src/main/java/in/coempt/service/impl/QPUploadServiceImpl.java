package in.coempt.service.impl;

import in.coempt.entity.QpUploadDetailsEntity;
import in.coempt.repository.QPUploadRepository;
import in.coempt.service.QPUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QPUploadServiceImpl implements QPUploadService {
@Autowired
private QPUploadRepository qpUploadRepository;

    @Override
    public List<QpUploadDetailsEntity> getQPsfilesBySubject(String subjectId) {

        List<QpUploadDetailsEntity> detailsEntityList =
                qpUploadRepository.findBySubjectId(Integer.parseInt(subjectId));

        return detailsEntityList;
    }

    @Override
    public void saveQPs(QpUploadDetailsEntity qpUploadDetails) {
        qpUploadRepository.save(qpUploadDetails);
    }

    @Override
    public QpUploadDetailsEntity getQPBySetNoAndSubjectId(int setNo, int subjectId) {
      return   qpUploadRepository.findBySubjectIdAndSetNo(subjectId,setNo);

    }

    @Override
    public QpUploadDetailsEntity getQPById(Long i) {
        return   qpUploadRepository.findById(i).get();
    }

    @Override
    public List<QpUploadDetailsEntity> saveBulkQps(List<QpUploadDetailsEntity> uploadDetailsEntities) {
        return   qpUploadRepository.saveAll(uploadDetailsEntities);
    }
}
