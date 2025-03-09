package in.coempt.service.impl;

import in.coempt.entity.Center;
import in.coempt.repository.CenterRepository;
import in.coempt.service.CenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CenterServiceImpl implements CenterService {
    @Autowired
    private CenterRepository centerRepository;

    @Override
    public List<Center> getAllCenters() {
        return centerRepository.findAll();
    }

    @Override
    public Center getCenterById(Integer id) {
        return centerRepository.findById(id).orElse(null);
    }

    @Override
    public Center saveCenter(Center center) {
        return centerRepository.save(center);
    }

    @Override
    public void deleteCenter(Integer id) {
        centerRepository.deleteById(id);
    }
}
