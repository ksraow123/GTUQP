package in.coempt.controller;

import in.coempt.dao.QPInventoryDao;
import in.coempt.entity.*;
import in.coempt.repository.PatternInstructionsRepository;
import in.coempt.repository.UserRepository;
import in.coempt.service.*;
import in.coempt.service.impl.HtmlToPdfService;
import in.coempt.util.FileUploadUtil;
import in.coempt.vo.ProfileDetailsVo;
import in.coempt.vo.QPInventoryVo;
import in.coempt.vo.SessionDataVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class QPFilesInventoryController {

    private final QPInventoryDao qpInventoryDao;
    private final QpInventoryService inventoryService;
    private final UserRepository userRepository;
    private final ProfileDetailsService profileDetailsService;
    private final QPTemplateService qpTemplateService;
    private final TimeTableService timeTableService;
    private final SubjectsService subjectsService;
    private final HtmlToPdfService htmlToPdfService;
    private final CollegeService collegeService;
    private final PatternInstructionsRepository patternInstructionsRepository;
    private final AppointmentService appointmentService;
    private final ExamSeriesService examSeriesService;
    private final CourseService courseService;
    @Value("${filePath}")
    private String filePath;
    @GetMapping("/QPInventoryTotalSummary")
    public String QPInventoryTotalSummary(Model model, HttpSession session) {
        SessionDataVo sessionDataVo = (SessionDataVo) session.getAttribute("sessionData");
        List<Integer> sectionIds = sessionDataVo.getSectionId();
        String sessionIds = sectionIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "(", ")"));
        List<QPInventoryVo> inventoryVoList = qpInventoryDao.getInventoryTotalSummary(sessionIds);
        model.addAttribute("inventoryVoList", inventoryVoList);
        model.addAttribute("page","/inventory/totalInventorySummary");
        return "main";

    }

    @GetMapping("/QPInventorySummary")
    public String getQPInventoryFiles(Model model, HttpSession session) {
        SessionDataVo sessionDataVo=(SessionDataVo)session.getAttribute("sessionData");
        List<Integer> sectionIds = sessionDataVo.getSectionId();
        String sessionIds = sectionIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "(", ")"));
        List<QPInventoryVo> inventoryVoList=qpInventoryDao.getInventorySummary(sessionIds,sessionDataVo.getExamSeriesId());
for(QPInventoryVo qpInventoryVo:inventoryVoList){
    if(qpInventoryVo.getIs_used()==1){
        qpInventoryVo.setQpr_status("Ready");
        userRepository.findById(qpInventoryVo.getSetter_user_id())
                .ifPresent(userRecord -> {
                    ProfileDetailsVo profileDetails = profileDetailsService.getProfileDetails(userRecord.getId());
                    CollegeEntity collegeEntity = collegeService.getCollegeById(profileDetails.getCollege_id());
                    StringBuilder setterDetails = new StringBuilder();
                    setterDetails.append("<p>")
                            .append("<b>User Name:</b> ").append(userRecord.getUserName()).append("<br>\n")
                            .append("<b>Name:</b> ").append(userRecord.getFirstName()).append(" ").append(userRecord.getLastName()).append("<br>\n")
                            .append("<b>College:</b> ").append(collegeEntity.getCollegeCode()).append("-").append(collegeEntity.getCollege_name()).append("<br>\n")
                            .append("<b>Mobile:</b> ").append(userRecord.getMobileNo()).append("<br>\n")
                            .append("<b>Email:</b> ").append(userRecord.getEmail()).append("<br>\n")
                            .append("<b>Designation:</b> ").append(profileDetails.getDesignation()).append("<br>\n")
                            .append("<b>Institute Type:</b> ").append(profileDetails.getInstitute_type()).append("<br>\n")
                            .append("<b>Teaching Exp:</b> ").append(profileDetails.getTeaching_experience()).append("<br>\n")
                            .append("<b>Industry Exp:</b> ").append(profileDetails.getIndustry_experience()).append("<br>\n</p>");
                    qpInventoryVo.setSetter_details(setterDetails.toString());
                });

    }
}

       // model.addAttribute("inventoryVoList", inventoryVoList);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        List<String> distinctExamDates = inventoryVoList.stream()
                .map(QPInventoryVo::getExam_date)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.comparing(dateStr -> LocalDate.parse(dateStr, formatter)))
                .collect(Collectors.toList());
        Map<String, Integer> sessionOrder = new HashMap<>();
        sessionOrder.put("10:30 AM TO 12:00 PM", 1);
        sessionOrder.put("10:30 AM TO 12:30 PM", 2);
        sessionOrder.put("10:30 AM TO 01:00 PM", 3);
        sessionOrder.put("10:30 AM TO 01:30 PM", 4);
        sessionOrder.put("02:30 PM TO 04:00 PM", 5);
        sessionOrder.put("02:30 PM TO 04:30 PM", 6);
        sessionOrder.put("02:30 PM TO 05:00 PM", 7);
        sessionOrder.put("02:30 PM TO 05:30 PM", 8);

        List<String> distinctExamSessions = inventoryVoList.stream()
                .map(QPInventoryVo::getExam_session)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.comparingInt(esession -> sessionOrder.getOrDefault(session, Integer.MAX_VALUE)))
                .collect(Collectors.toList());
        model.addAttribute("distinctExamSessions", distinctExamSessions);
        model.addAttribute("distinctExamDates", distinctExamDates);

        model.addAttribute("page","/inventory/inventorySummary");
        return "main";

    }
    @PostMapping("/timetableQpInventoryFilters")
    public String timetableQpInventoryFilters(@RequestParam("examDate") String examDate,@RequestParam("examSession") String examSession, Model model, HttpSession session) {
        SessionDataVo sessionDataVo=(SessionDataVo)session.getAttribute("sessionData");
        List<Integer> sectionIds = sessionDataVo.getSectionId();
        String sessionIds = sectionIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "(", ")"));
        List<QPInventoryVo> inventoryVoList=qpInventoryDao.getInventorySummary(sessionIds,sessionDataVo.getExamSeriesId());
for(QPInventoryVo qpInventoryVo:inventoryVoList){
    if(qpInventoryVo.getIs_used()==1){
        qpInventoryVo.setQpr_status("Ready");
        userRepository.findById(qpInventoryVo.getSetter_user_id())
                .ifPresent(userRecord -> {
                    ProfileDetailsVo profileDetails = profileDetailsService.getProfileDetails(userRecord.getId());
                    CollegeEntity collegeEntity = collegeService.getCollegeById(profileDetails.getCollege_id());
                    StringBuilder setterDetails = new StringBuilder();
                    setterDetails.append("<p>")
                            .append("<b>User Name:</b> ").append(userRecord.getUserName()).append("<br>\n")
                            .append("<b>Name:</b> ").append(userRecord.getFirstName()).append(" ").append(userRecord.getLastName()).append("<br>\n")
                            .append("<b>College:</b> ").append(collegeEntity.getCollegeCode()).append("-").append(collegeEntity.getCollege_name()).append("<br>\n")
                            .append("<b>Mobile:</b> ").append(userRecord.getMobileNo()).append("<br>\n")
                            .append("<b>Email:</b> ").append(userRecord.getEmail()).append("<br>\n")
                            .append("<b>Designation:</b> ").append(profileDetails.getDesignation()).append("<br>\n")
                            .append("<b>Institute Type:</b> ").append(profileDetails.getInstitute_type()).append("<br>\n")
                            .append("<b>Teaching Exp:</b> ").append(profileDetails.getTeaching_experience()).append("<br>\n")
                            .append("<b>Industry Exp:</b> ").append(profileDetails.getIndustry_experience()).append("<br>\n</p>");
                    qpInventoryVo.setSetter_details(setterDetails.toString());
                });

    }
}
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        List<String> distinctExamDates = inventoryVoList.stream()
                .map(QPInventoryVo::getExam_date)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.comparing(dateStr -> LocalDate.parse(dateStr, formatter)))
                .collect(Collectors.toList());
        Map<String, Integer> sessionOrder = new HashMap<>();
        sessionOrder.put("10:30 AM TO 12:00 PM", 1);
        sessionOrder.put("10:30 AM TO 12:30 PM", 2);
        sessionOrder.put("10:30 AM TO 01:00 PM", 3);
        sessionOrder.put("10:30 AM TO 01:30 PM", 4);
        sessionOrder.put("02:30 PM TO 04:00 PM", 5);
        sessionOrder.put("02:30 PM TO 04:30 PM", 6);
        sessionOrder.put("02:30 PM TO 05:00 PM", 7);
        sessionOrder.put("02:30 PM TO 05:30 PM", 8);

        List<String> distinctExamSessions = inventoryVoList.stream()
                .map(QPInventoryVo::getExam_session)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.comparingInt(esession -> sessionOrder.getOrDefault(session, Integer.MAX_VALUE)))
                .collect(Collectors.toList());
        model.addAttribute("distinctExamSessions", distinctExamSessions);
        model.addAttribute("distinctExamDates", distinctExamDates);

//        List<QPInventoryVo> filteredList = inventoryVoList.stream()
//                .filter(vo -> examDate.equals(vo.getExam_date()) && examSession.equals(vo.getExam_session()))
//                .collect(Collectors.toList());


        inventoryVoList.removeIf(a ->
                (!"All".equalsIgnoreCase(examDate) && !examDate.equalsIgnoreCase(a.getExam_date())) ||
                        (!"All".equalsIgnoreCase(examSession) && !examSession.equalsIgnoreCase(a.getExam_session()))
        );

        model.addAttribute("inventoryVoList", inventoryVoList);

       // model.addAttribute("inventoryVoList", filteredList);
        model.addAttribute("examDate", examDate);
        model.addAttribute("examSession", examSession);

        model.addAttribute("page","/inventory/inventorySummary");
        return "main";

    }
    @GetMapping("/getAvailableQpFiles/{subjectId}")

    public String getAvailableQpFiles(@PathVariable("subjectId") Long subjectId, Model model) {
        List<QPInventoryVo> viewSubjectQPFiles=qpInventoryDao.getDraftQpFiles(subjectId);
     //   List<QPInventoryVo> viewAvailableQps= viewSubjectQPFiles.stream().filter(p->p.getIs_used()==0).collect(Collectors.toList());
        for (QPInventoryVo qpInventoryVo : viewSubjectQPFiles) {
            userRepository.findById(qpInventoryVo.getSetter_user_id())
                    .ifPresent(userRecord -> {
                        ProfileDetailsVo profileDetails = profileDetailsService.getProfileDetails(userRecord.getId());
                        CollegeEntity collegeEntity = collegeService.getCollegeById(profileDetails.getCollege_id());
                        StringBuilder setterDetails = new StringBuilder();
                        setterDetails.append("<p>")
                                .append("<b>User Name:</b> ").append(userRecord.getUserName()).append("<br>\n")
                                .append("<b>Name:</b> ").append(userRecord.getFirstName()).append(" ").append(userRecord.getLastName()).append("<br>\n")
                                .append("<b>College:</b> ").append(collegeEntity.getCollegeCode()).append("-").append(collegeEntity.getCollege_name()).append("<br>\n")
                                .append("<b>Mobile:</b> ").append(userRecord.getMobileNo()).append("<br>\n")
                                .append("<b>Email:</b> ").append(userRecord.getEmail()).append("<br>\n")
                                .append("<b>Designation:</b> ").append(profileDetails.getDesignation()).append("<br>\n")
                                .append("<b>Institute Type:</b> ").append(profileDetails.getInstitute_type()).append("<br>\n")
                                .append("<b>Teaching Exp:</b> ").append(profileDetails.getTeaching_experience()).append("<br>\n")
                                .append("<b>Industry Exp:</b> ").append(profileDetails.getIndustry_experience()).append("<br>\n</p>");
                        qpInventoryVo.setSetter_details(setterDetails.toString());
                    });
        }
        model.addAttribute("viewAvailableQps", viewSubjectQPFiles);
        return "fragments/availableQps :: availableQpsTable"; // Return Thymeleaf fragment

    }
    @GetMapping("/getAvailableQps/{subjectId}")
    public String getAvailableQps(@PathVariable("subjectId") Long subjectId, Model model) {
        List<QPInventoryVo> viewSubjectQPFiles=qpInventoryDao.getAvailableQpFiles(subjectId);
        List<QPInventoryVo> viewAvailableQps= viewSubjectQPFiles.stream().filter(p->p.getIs_used()==0).collect(Collectors.toList());
        for (QPInventoryVo qpInventoryVo : viewAvailableQps) {
            userRepository.findById(qpInventoryVo.getSetter_user_id())
                    .ifPresent(userRecord -> {
                        ProfileDetailsVo profileDetails = profileDetailsService.getProfileDetails(userRecord.getId());
                        CollegeEntity collegeEntity = collegeService.getCollegeById(profileDetails.getCollege_id());
                        StringBuilder setterDetails = new StringBuilder();
                        setterDetails.append("<p>")
                                .append("<b>User Name:</b> ").append(userRecord.getUserName()).append("<br>\n")
                                .append("<b>Name:</b> ").append(userRecord.getFirstName()).append(" ").append(userRecord.getLastName()).append("<br>\n")
                                .append("<b>College:</b> ").append(collegeEntity.getCollegeCode()).append("-").append(collegeEntity.getCollege_name()).append("<br>\n")
                                .append("<b>Mobile:</b> ").append(userRecord.getMobileNo()).append("<br>\n")
                                .append("<b>Email:</b> ").append(userRecord.getEmail()).append("<br>\n")
                                .append("<b>Designation:</b> ").append(profileDetails.getDesignation()).append("<br>\n")
                                .append("<b>Institute Type:</b> ").append(profileDetails.getInstitute_type()).append("<br>\n")
                                .append("<b>Teaching Exp:</b> ").append(profileDetails.getTeaching_experience()).append("<br>\n")
                                .append("<b>Industry Exp:</b> ").append(profileDetails.getIndustry_experience()).append("<br>\n</p>");
                        qpInventoryVo.setSetter_details(setterDetails.toString());
                    });
        }
        model.addAttribute("viewAvailableQps", viewAvailableQps);
        return "fragments/getQps :: availableQpsTable"; // Return Thymeleaf fragment

    }
    @GetMapping("/getUsedQpFiles/{subjectId}")
    public String getUsedQpFiles(@PathVariable("subjectId") Long subjectId, Model model) {
        List<QPInventoryVo> viewSubjectQPFiles=qpInventoryDao.getAvailableQpFiles(subjectId);
        List<QPInventoryVo> viewAvailableQps= viewSubjectQPFiles.stream().filter(p->p.getIs_used()==1).collect(Collectors.toList());
        for (QPInventoryVo qpInventoryVo : viewAvailableQps) {
            userRepository.findById(qpInventoryVo.getSetter_user_id())
                    .ifPresent(userRecord -> {
                        ProfileDetailsVo profileDetails = profileDetailsService.getProfileDetails(userRecord.getId());
                        CollegeEntity collegeEntity = collegeService.getCollegeById(profileDetails.getCollege_id());
                        StringBuilder setterDetails = new StringBuilder();
                        setterDetails.append("<p>")
                                .append("<b>User Name:</b> ").append(userRecord.getUserName()).append("<br>\n")
                                .append("<b>Name:</b> ").append(userRecord.getFirstName()).append(" ").append(userRecord.getLastName()).append("<br>\n")
                                .append("<b>College:</b> ").append(collegeEntity.getCollegeCode()).append("-").append(collegeEntity.getCollege_name()).append("<br>\n")
                                .append("<b>Mobile:</b> ").append(userRecord.getMobileNo()).append("<br>\n")
                                .append("<b>Email:</b> ").append(userRecord.getEmail()).append("<br>\n")
                                .append("<b>Designation:</b> ").append(profileDetails.getDesignation()).append("<br>\n")
                                .append("<b>Institute Type:</b> ").append(profileDetails.getInstitute_type()).append("<br>\n")
                                .append("<b>Teaching Exp:</b> ").append(profileDetails.getTeaching_experience()).append("<br>\n")
                                .append("<b>Industry Exp:</b> ").append(profileDetails.getIndustry_experience()).append("<br>\n</p>");
                        qpInventoryVo.setSetter_details(setterDetails.toString());
                    });
        }

        model.addAttribute("usedQps", viewAvailableQps);
        return "fragments/usedQps :: usedQpsTable"; // Return the Thymeleaf fragment
    }
    @GetMapping("/getTotalQpFiles/{subjectId}")
    public String getTotalQpFiles(@PathVariable("subjectId") Long subjectId, Model model) {
        List<QPInventoryVo> viewSubjectQPFiles=qpInventoryDao.getAvailableQpFiles(subjectId);
        for (QPInventoryVo qpInventoryVo : viewSubjectQPFiles) {
            userRepository.findById(qpInventoryVo.getSetter_user_id())
                    .ifPresent(userRecord -> {
                        ProfileDetailsVo profileDetails = profileDetailsService.getProfileDetails(userRecord.getId());
                        CollegeEntity collegeEntity = collegeService.getCollegeById(profileDetails.getCollege_id());
                        StringBuilder setterDetails = new StringBuilder();
                        setterDetails.append("<p>")
                                .append("<b>User Name:</b> ").append(userRecord.getUserName()).append("<br>\n")
                                .append("<b>Name:</b> ").append(userRecord.getFirstName()).append(" ").append(userRecord.getLastName()).append("<br>\n")
                                .append("<b>College:</b> ").append(collegeEntity.getCollegeCode()).append("-").append(collegeEntity.getCollege_name()).append("<br>\n")
                                .append("<b>Mobile:</b> ").append(userRecord.getMobileNo()).append("<br>\n")
                                .append("<b>Email:</b> ").append(userRecord.getEmail()).append("<br>\n")
                                .append("<b>Designation:</b> ").append(profileDetails.getDesignation()).append("<br>\n")
                                .append("<b>Institute Type:</b> ").append(profileDetails.getInstitute_type()).append("<br>\n")
                                .append("<b>Teaching Exp:</b> ").append(profileDetails.getTeaching_experience()).append("<br>\n")
                                .append("<b>Industry Exp:</b> ").append(profileDetails.getIndustry_experience()).append("<br>\n</p>");
                        qpInventoryVo.setSetter_details(setterDetails.toString());
                    });
        }

       // model.addAttribute("totalQps", viewSubjectQPFiles);
        model.addAttribute("totalQps", viewSubjectQPFiles);
        return "fragments/totalQps :: totalQpsTable";
    }
    @GetMapping("/getDraftQpFiles/{subjectId}")
    public String getDraftQpFiles(@PathVariable("subjectId") Long subjectId, Model model) {
        List<QPInventoryVo> viewSubjectQPFiles=qpInventoryDao.getDraftQpFiles(subjectId);
        for (QPInventoryVo qpInventoryVo : viewSubjectQPFiles) {
            userRepository.findById(qpInventoryVo.getSetter_user_id())
                    .ifPresent(userRecord -> {
                        ProfileDetailsVo profileDetails = profileDetailsService.getProfileDetails(userRecord.getId());
                        CollegeEntity collegeEntity = collegeService.getCollegeById(profileDetails.getCollege_id());
                        StringBuilder setterDetails = new StringBuilder();
                        setterDetails.append("<p>")
                                .append("<b>User Name:</b> ").append(userRecord.getUserName()).append("<br>\n")
                                .append("<b>Name:</b> ").append(userRecord.getFirstName()).append(" ").append(userRecord.getLastName()).append("<br>\n")
                                .append("<b>College:</b> ").append(collegeEntity.getCollegeCode()).append("-").append(collegeEntity.getCollege_name()).append("<br>\n")
                                .append("<b>Mobile:</b> ").append(userRecord.getMobileNo()).append("<br>\n")
                                .append("<b>Email:</b> ").append(userRecord.getEmail()).append("<br>\n")
                                .append("<b>Designation:</b> ").append(profileDetails.getDesignation()).append("<br>\n")
                                .append("<b>Institute Type:</b> ").append(profileDetails.getInstitute_type()).append("<br>\n")
                                .append("<b>Teaching Exp:</b> ").append(profileDetails.getTeaching_experience()).append("<br>\n")
                                .append("<b>Industry Exp:</b> ").append(profileDetails.getIndustry_experience()).append("<br>\n</p>");
                        qpInventoryVo.setSetter_details(setterDetails.toString());
                    });
        }

        model.addAttribute("viewAvailableQps", viewSubjectQPFiles);
        return "fragments/availableQps :: availableQpsTable";
    }
    @PostMapping("/submitSelectedQp")
    public String prepareFinalQp(@RequestParam("selectedQp") Long selectedQpId, RedirectAttributes redirectAttributes, HttpSession session,Model model) throws IOException, InterruptedException {
        SessionDataVo sessionDataVo=(SessionDataVo)session.getAttribute("sessionData");
        QpInventoryEntity inventoryEntity=inventoryService.getInventorySummaryById(selectedQpId);
        inventoryEntity.setQp_file_path(generateQPPdf(model, inventoryEntity));
        inventoryEntity.setIsUsed(1);
        LocalDateTime now = LocalDateTime.now();

        // Define the formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        // Format the date-time
        String formattedDate = now.format(formatter);
        inventoryEntity.setUsed_date(formattedDate);
        inventoryEntity.setUsedSeriesId(sessionDataVo.getExamSeriesId());
        inventoryService.saveQPInventoryFile(inventoryEntity);

        redirectAttributes.addFlashAttribute("successMessage", "QP successfully submitted!");

        return "redirect:/QPInventorySummary";
    }
@PostMapping("/prepareSelectedQp")
    public String prepareDraftQp(@RequestParam("selectedQp") Long selectedQpId, RedirectAttributes redirectAttributes, HttpSession session,Model model) throws IOException, InterruptedException {
        SessionDataVo sessionDataVo=(SessionDataVo)session.getAttribute("sessionData");
        QpInventoryEntity inventoryEntity=inventoryService.getInventorySummaryById(selectedQpId);
        List<QpInventoryEntity> inventoryList=inventoryService.getDraftCopyDetails(inventoryEntity.getSubjectId(),2);
        if(inventoryList.size()>0) {
            inventoryList.forEach(p -> p.setIsUsed(null));
        }
    inventoryEntity.setQp_file_path(generateQPPdf(model, inventoryEntity));
    inventoryEntity.setIsUsed(2);
    LocalDateTime now = LocalDateTime.now();

    // Define the formatter
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Format the date-time
    String formattedDate = now.format(formatter);
    inventoryEntity.setUsed_date(formattedDate);
    inventoryEntity.setUsedSeriesId(sessionDataVo.getExamSeriesId());
    inventoryService.saveQPInventoryFile(inventoryEntity);

    redirectAttributes.addFlashAttribute("successMessage", "QP successfully submitted!");

        return "redirect:/QPInventoryTotalSummary";
    }

    private String generateQPPdf(Model model, QpInventoryEntity inventoryEntity) throws IOException, InterruptedException {

        List<BitwiseQuestions> questionsList = qpTemplateService.getReviewerQuestionsList(inventoryEntity.getSubjectId() + "", 1, Long.valueOf(inventoryEntity.getSectionUserId()), Long.valueOf(inventoryEntity.getSetterUserId()));
        Subjects subjects = subjectsService.getSubjectById(inventoryEntity.getSubjectId() + "");
        model.addAttribute("subjects", subjects);
        model.addAttribute("examSeries", examSeriesService.getExamSeriesById(inventoryEntity.getExamSeriesId()));
        model.addAttribute("course", courseService.getCourseDetailsById(subjects.getCourseId()));
        model.addAttribute("questionsList", questionsList);

        String courseNames=appointmentService.getCourseNamesBySubjectCode(subjects.getSubjectCode());
        String semesterNames=appointmentService.getSemestersBySubjectCode(subjects.getSubjectCode());

        UserData appointment = appointmentService.getUserDataSubjectIdAndUserIdAndExamSeriesId(Math.toIntExact(subjects.getId()), inventoryEntity.getSetterUserId(), 1);
        model.addAttribute("courseNames", courseNames);
        model.addAttribute("semesterNames", semesterNames);
        model.addAttribute("appointment", appointment);
        PatternInstructionsEntity instructionsEntity = patternInstructionsRepository.findByPatternCode(subjects.getPatternCode()).get();
        model.addAttribute("patternInstructions", instructionsEntity);
        model.addAttribute("groupedQuestions", questionsList.stream().collect(Collectors.groupingBy(BitwiseQuestions::getQ_no, LinkedHashMap::new, // specify the map type to preserve order
                Collectors.toList()
        )));
model.addAttribute("timetableData",timeTableService.getTimeTableBySubjectIdAndExamSeriesId(inventoryEntity.getSubjectId(),inventoryEntity.getExamSeriesId()));
        String fileName = subjects.getSubjectCode() + "_" + inventoryEntity.getQpFilesRefId() + ".pdf";
        String commonPath = filePath + File.separator;
        String ourPath = "QuestionBank" + File.separator + inventoryEntity.getSectionUserId() + File.separator;
        String serverPath = commonPath + ourPath;

        FileUploadUtil fileUpload = new FileUploadUtil();
        fileUpload.createRequiredDirectoryIfNotExists(commonPath, ourPath);

        File finalPdf = new File(serverPath, fileName);

        // Convert HTML to PDF using wkhtmltopdf
        htmlToPdfService.generateFinalPdfFromHtml(model, finalPdf.getAbsolutePath());
        return ourPath + fileName;


    }
    @DeleteMapping("/deleteFinalQp/{qpId}")
    @ResponseBody
    @Transactional
    public ResponseEntity<String> deleteAppointment(@PathVariable("qpId") Long id, RedirectAttributes redirectAttributes) {

        QpInventoryEntity inventoryEntity=inventoryService.getInventorySummaryById(id);
        inventoryEntity.setIsUsed(null);
        inventoryService.saveQPInventoryFile(inventoryEntity);
        return ResponseEntity.ok("Deleted");

    }


}
