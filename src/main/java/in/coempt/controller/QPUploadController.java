package in.coempt.controller;

import in.coempt.entity.Appointment;
import in.coempt.entity.QpUploadDetailsEntity;
import in.coempt.entity.Subjects;
import in.coempt.entity.User;
import in.coempt.service.AppointmentService;
import in.coempt.service.QPUploadService;
import in.coempt.service.SubjectsService;
import in.coempt.service.UserService;
import in.coempt.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class QPUploadController {
    @Value("${filePath}") // Define in application.properties
    private String uploadDir;
    @Autowired
    private SubjectsService subjectsService;
    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private QPUploadService qpUploadService;

    @Autowired
    private UserService userService;
    @GetMapping("/uploadQps")
    public String qpUploadPage(Model model) {
        UserDetails user=(UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        Appointment appointment=appointmentService.getAppointmentDetails(user.getUsername());
        Subjects subjects= subjectsService.getSubjectById(appointment.getSubject_id());
        model.addAttribute("subject", subjects);
        model.addAttribute("noOfSets",appointment.getNo_of_sets());
        List<QpUploadDetailsEntity> detailsEntityList=qpUploadService.getQPsfilesBySubject(appointment.getSubject_id());
        model.addAttribute("qpFiles",detailsEntityList);
        model.addAttribute("qpUploadStatus", detailsEntityList.stream().allMatch(q -> q.getQp_setter_status().equalsIgnoreCase("UPLOADED")));
        model.addAttribute("qpForwardStatus", detailsEntityList.stream().allMatch(q -> q.getQp_setter_status().equalsIgnoreCase("FORWARDED")));

        return "qpUploadPage";
    }

    @GetMapping("/uploadQpsReview")
    public String uploadQpsReview(Model model) {
        UserDetails user=(UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        Appointment appointment=appointmentService.getAppointmentDetails(user.getUsername());
        Subjects subjects= subjectsService.getSubjectById(appointment.getSubject_id());
        model.addAttribute("subject", subjects);
        model.addAttribute("noOfSets",appointment.getNo_of_sets());
        List<QpUploadDetailsEntity> detailsEntityList=qpUploadService.getQPsfilesBySubject(appointment.getSubject_id());
        model.addAttribute("qpFiles",detailsEntityList);
        model.addAttribute("qpUploadStatus", detailsEntityList.stream().allMatch(q -> q.getQp_setter_status().equalsIgnoreCase("UPLOADED")));
        model.addAttribute("qpForwardStatus", detailsEntityList.stream().allMatch(q -> q.getQp_setter_status().equalsIgnoreCase("FORWARDED")));

        return "qpUploadReviewPage";
    }







    @PostMapping("/uploadQPPdf")
    public String uploadFiles(@RequestParam Map<String, MultipartFile> files, Model model) {
        UserDetails userDetails=(UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();

        Appointment appointment=appointmentService.getAppointmentDetails(userDetails.getUsername());

        try {
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            // Loop through uploaded files
            int setNo=0;
            User user = userService.getUserByUserName(userDetails.getUsername());
            for (Map.Entry<String, MultipartFile> entry : files.entrySet()) {
                setNo++;
                MultipartFile file = entry.getValue();

                if (file.isEmpty()) {
                    continue;
                }

                // ✅ Generate a custom file name
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String uniqueID = UUID.randomUUID().toString().substring(0, 8); // Short UUID
                String customFileName = "QP_" + timestamp + "_" + uniqueID + extension;
                // ✅ Save the file
                File destinationFile = new File(uploadDir + File.separator + customFileName);
                Files.copy(file.getInputStream(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                QpUploadDetailsEntity qpUploadDetails = qpUploadService.getQPBySetNoAndSubjectId(setNo, Integer.parseInt(appointment.getSubject_id()));
                if (qpUploadDetails == null) {
                    qpUploadDetails = new QpUploadDetailsEntity();
                }
                qpUploadDetails.setSetNo(setNo);

                if (user.getRoleId() == 2) {
                    qpUploadDetails.setQp_setter_status("UPLOADED");
                    qpUploadDetails.setQp_setter_uploaded_date(LocalDateTime.now() + "");
                    qpUploadDetails.setQp_setter_id(Math.toIntExact(user.getId()));
                }
                if (user.getRoleId() == 3) {
                    qpUploadDetails.setQp_reviewer_status("UPLOADED");
                    qpUploadDetails.setQp_reviewed_date(LocalDateTime.now() + "");
                    qpUploadDetails.setQp_reviewer_id(Math.toIntExact(user.getId()));
                }
                qpUploadDetails.setSubjectId(Integer.parseInt(appointment.getSubject_id()));
                qpUploadDetails.setQp_file(customFileName);

                qpUploadService.saveQPs(qpUploadDetails);
                if (user.getRoleId() == 2) {
                    return "redirect:/uploadQps";
                }
                if (user.getRoleId() == 3) {
                    return "redirect:/qpReviewDocuments";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "File upload failed: " + e.getMessage());
        }

        return null;
        // Return a success page
    }
    @GetMapping("/qpPdfForward/{id}")
    public String qpPdfForward(Model model, @PathVariable("id") int id) {
        UserDetails userDetails=(UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        Appointment appointment=appointmentService.getAppointmentDetails(userDetails.getUsername());
       QpUploadDetailsEntity uploadDetailsEntities=qpUploadService.getQPBySetNoAndSubjectId(id,Integer.parseInt(appointment.getSubject_id()));

        uploadDetailsEntities.setQp_setter_status("FORWARDED");
        uploadDetailsEntities.setQp_forwarded_date(LocalDateTime.now()+"");
        qpUploadService.saveQPs(uploadDetailsEntities);
        return "redirect:/uploadQps";
    }
    @GetMapping("/qpReviewDocuments")
    public String qpReviewDocuments(Model model) {
        UserDetails user=(UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        Appointment appointment=appointmentService.getAppointmentDetails(user.getUsername());
        Subjects subjects= subjectsService.getSubjectById(appointment.getSubject_id());
        model.addAttribute("subject", subjects);
        model.addAttribute("noOfSets",appointment.getNo_of_sets());
        List<QpUploadDetailsEntity> detailsEntityList=qpUploadService.getQPsfilesBySubject(appointment.getSubject_id());
        model.addAttribute("qpFiles",detailsEntityList);
        model.addAttribute("qpForwardStatus", detailsEntityList.stream().allMatch(q -> q.getQp_reviewer_status()!=null));
        return "qpUploadReviewPage";
    }
    @PostMapping("/qpPdfApproval")
    public String qpPdfApproval(Model model,@RequestParam("qpointId") Long qpointId,
                                @RequestParam("approved") String approved,
                                @RequestParam(value = "comments", required = false) String comments) {
        UserDetails userDetails=(UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        Appointment appointment=appointmentService.getAppointmentDetails(userDetails.getUsername());
        QpUploadDetailsEntity qpUploadDetails=qpUploadService.getQPById(qpointId);

            qpUploadDetails.setQp_reviewer_status(approved);
            qpUploadDetails.setQp_reviewed_date(LocalDateTime.now()+"");
            qpUploadDetails.setReviewer_comments(comments);

        qpUploadService.saveQPs(qpUploadDetails);
        return "redirect:/qpReviewDocuments";
    }
}
