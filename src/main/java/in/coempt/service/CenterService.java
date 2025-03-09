package in.coempt.service;

import in.coempt.entity.Center;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CenterService {
    List<Center> getAllCenters();
    Center getCenterById(Integer id);
    Center saveCenter(Center center);
    void deleteCenter(Integer id);
}
