package in.coempt.service.impl;

import in.coempt.entity.SmsEmailTemplate;
import in.coempt.repository.SmsEmailTemplateRepository;
import in.coempt.service.SmsEmailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsEmailTemplateServiceImpl implements SmsEmailTemplateService {
    @Autowired
    private SmsEmailTemplateRepository smsEmailTemplateRepository;

    @Override
    public SmsEmailTemplate getEmailTemplateDetails() {
       return smsEmailTemplateRepository.findById(1L).get();

    }
}
