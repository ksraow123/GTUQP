package in.coempt.controller;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.*;
import in.coempt.entity.*;
import in.coempt.repository.PatternInstructionsRepository;
import in.coempt.repository.RolesRepository;
import in.coempt.repository.UserRepository;
import in.coempt.service.*;
import in.coempt.service.impl.HtmlToPdfService;
import in.coempt.util.FileUploadUtil;
import in.coempt.util.SecurityUtil;
import in.coempt.util.SendMailUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@CrossOrigin(origins = "*")
public class QPTemplateController {
    @Autowired
    private QPTemplateService qpTemplateService;
    @Autowired
    private SubjectsService subjectsService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private UserService userService;
    @Autowired
    private QpInventoryService inventoryService;
    @Autowired
    private QPUploadService qpUploadService;
    @Autowired
    private SetterModeratorService setterModeratorService;
    @Autowired
    private HtmlToPdfService htmlToPdfService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RolesRepository rolesRepository;
    @Autowired
    private SectionUserMappingService userMappingService;
    @Autowired
    private QPFilesService qpFilesService;
    @Autowired
    private SendMailUtil sendMail;
    @Autowired
    private PatternInstructionsRepository patternInstructionsRepository;
    @Value("${filePath}")
    private String filePath;

    @GetMapping("/bitwisequestionsList")
    public String getBitwiseQuestions(
            @RequestParam("id") int setNo,
            @RequestParam("subjectId") String subjectId,
            Model model) {
        UserDetails user = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User userEntity = userService.getUserByUserName(user.getUsername());
        //Appointment appointment = appointmentService.getAppointmentDetails(user.getUsername());
        Subjects subjects = subjectsService.getSubjectById(subjectId);
        model.addAttribute("subject", subjects);
        Integer filledStatus = 0;
        List<BitwiseQuestions> qPtemplate = qpTemplateService.getQuestionsList(subjectId, setNo, userEntity.getId());
        List<BitwiseQuestions> qpQuestionsFormula=qPtemplate.stream().filter(q->!q.getBit_no().equalsIgnoreCase("OR")).collect(Collectors.toList());


        model.addAttribute("qPtemplate", qPtemplate);
        model.addAttribute("setNo", setNo);
        model.addAttribute("setterId", userEntity.getId());
        List<BitwiseQuestions> filledQuestions = qPtemplate.stream().filter(p -> p.getQ_desc() != null).collect(Collectors.toList());
        if ((qpQuestionsFormula.size() == filledQuestions.size()) && filledQuestions.size() > 0) {
            filledStatus = 1;
        }
        model.addAttribute("filledStatus", filledStatus);
        UserData userData = appointmentService.getUserDataSubjectIdAndUserIdAndExamSeriesId(Math.toIntExact(subjects.getId()), Math.toIntExact(userEntity.getId()), 1);
        model.addAttribute("userData", userData);
        QPFilesEntity qpFilesEntity = qpFilesService.getQPFilesByRollIdAndSubjectIdAndSetNoAndSetterId(1, subjectId, 1, userEntity.getId());
        String sectionTeamRemarks = null;
        if (qpFilesEntity != null) {
            sectionTeamRemarks = qpFilesEntity.getRemarks();
        }
        model.addAttribute("sectionTeamRemarks", sectionTeamRemarks);
        return "QPTemplate";

    }

    @GetMapping("/reviewerQuestionsList")
    public String reviewerQuestionsList(
            @RequestParam("id") int setNo,
            @RequestParam("subjectId") String subjectId,
            @RequestParam("setterId") Long setterId,
            Model model) {
        UserDetails user = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();

        User userEntity = userService.getUserByUserName(user.getUsername());
        //  Appointment appointment = appointmentService.getAppointmentDetails(user.getUsername());
        Subjects subjects = subjectsService.getSubjectById(subjectId);
//        List<BitwiseQuestions> qPtemplate = qpTemplateService.getQuestionsList(subjectId,setNo,userEntity.getId());
        List<BitwiseQuestions> qPtemplate = qpTemplateService.getReviewerQuestionsList(subjectId, setNo, userEntity.getId(), setterId);
List<BitwiseQuestions> qpQuestionsFormula=qPtemplate.stream().filter(q->!q.getBit_no().equalsIgnoreCase("OR")).collect(Collectors.toList());
         model.addAttribute("qPtemplate", qPtemplate);
        model.addAttribute("subject", subjects);
        model.addAttribute("setNo", setNo);
        model.addAttribute("setterId", setterId);
        Boolean isApproved = qpQuestionsFormula.stream()
                .allMatch(q -> "Approved".equals(q.getQp_reviewer_status()));
        model.addAttribute("isApproved", isApproved);
        return "reviewerQPTemplate";

    }

    @GetMapping("/removeQpImage/{id}")
    public String removeQpImage(@PathVariable("id") Long id, Model model) {
        BitwiseQuestions question = qpTemplateService.getQuestionDetailsById(id);
        question.setImage_path(null);
        qpTemplateService.saveQuestion(question);
        return "redirect:/bitwisequestionsList?id=1&subjectId=" + question.getSubjectId();
    }

    @PostMapping("/saveQuestions")
    public String saveQuestion(@RequestParam("q_desc") String questionDescription,
                               @RequestParam("qid") Long qid,
                               @RequestParam(value = "instructions", required = false) String instructions,
                               @RequestParam(value = "image_file", required = false) MultipartFile imageFile,
                               RedirectAttributes redirectAttributes) {

        Logger logger = LoggerFactory.getLogger(this.getClass());

        try {
            UserDetails user = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
            BitwiseQuestions question = qpTemplateService.getQuestionDetailsById(qid);

            if (question == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Question not found!");
                return "redirect:/bitwisequestionsList";
            }

            question.setQ_desc(questionDescription);
            question.setInstructions(instructions);

            // **Convert Image to Base64 String**
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    byte[] imageBytes = imageFile.getBytes();
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                    question.setImage_path(base64Image);
                } catch (IOException e) {
                    logger.error("Error processing image file: " + e.getMessage(), e);
                    redirectAttributes.addFlashAttribute("errorMessage", "Failed to process image.");
                    return "redirect:/bitwisequestionsList";
                }
            }

            // **Save Question to DB**
            qpTemplateService.saveQuestion(question);
            redirectAttributes.addFlashAttribute("successMessage", "Question saved successfully!");
        } catch (DataIntegrityViolationException e) {
            logger.error("Unique constraint violation: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Duplicate entry! This question already exists.");
            return "redirect:/bitwisequestionsList";

        } catch (MaxUploadSizeExceededException e) {
            logger.error("File size too large: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "File size exceeds limit.");
            return "redirect:/bitwisequestionsList";
        } catch (DataAccessException e) {
            logger.error("Database error: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Database error occurred.");
            return "redirect:/bitwisequestionsList";
        } catch (Exception e) {
            logger.error("Unexpected error: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred.");
            return "redirect:/bitwisequestionsList";
        }

        return "redirect:/bitwisequestionsList";
    }


    @GetMapping("/getQuestionDetails/{id}")
    @ResponseBody
    public BitwiseQuestions getQuestionDetails(@PathVariable("id") Long id) {
        return qpTemplateService.getQuestionById(id);

    }

    @GetMapping("/previewPdf/{id}/{subjectId}/{setterId}")
    public String previewPdf(HttpServletRequest request, Model model,
                             @PathVariable("id") int id, @PathVariable("subjectId") int subjectId, @PathVariable("setterId") Long setterId) throws IOException, DocumentException {
        // Fetch User & Appointment Details
        UserDetails userDetails = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User userEntity = userService.getUserByUserName(userDetails.getUsername());
        UserData appointment = appointmentService.getUserDataSubjectIdAndUserIdAndExamSeriesId(subjectId, Math.toIntExact(setterId), 1);

        List<BitwiseQuestions> questionsList = null;
        if (userEntity.getRoleId() == 2) {
            questionsList = qpTemplateService.getQuestionsList(subjectId + "", id, userEntity.getId());

        }
        if (userEntity.getRoleId() == 1) {
            questionsList = qpTemplateService.getReviewerQuestionsList(subjectId + "", id, userEntity.getId(), setterId);
        }
        Subjects subjects = subjectsService.getSubjectById(subjectId + "");

        model.addAttribute("subjects", subjects);
        model.addAttribute("appointment", appointment);
        model.addAttribute("questionsList", questionsList);
        model.addAttribute("watermark", subjects.getSubjectCode() + "," + userDetails.getUsername() + "," + LocalDateTime.now());
        model.addAttribute("groupedQuestions", questionsList.stream().collect(Collectors.groupingBy(BitwiseQuestions::getQ_no)));
        PatternInstructionsEntity instructionsEntity = patternInstructionsRepository.findByPatternCode(subjects.getPatternCode()).get();
        model.addAttribute("patternInstructions", instructionsEntity);

        return "pdfsample";
    }
    //        model.addAttribute("subjects", subjectsService.getSubjectById(appointment.getSubject_id()));
//        // Define PDF File Paths
//        String fileName = "QuestionPaperPDF_" + userDetails.getUsername() + ".pdf";
//        String tempFileName = "Temp_" + fileName; // Temporary PDF before watermark
//
//        String commonPath = filePath + File.separator;
//        String ourPath = "QuestionBank" + File.separator + userDetails.getUsername() + File.separator;
//        String serverPath = commonPath + ourPath;
//
//        FileUploadUtil fileUpload = new FileUploadUtil();
//        fileUpload.createRequiredDirectoryIfNotExists(commonPath, ourPath);
//
//        File tempPdf = new File(serverPath, tempFileName);
//        File finalPdf = new File(serverPath, fileName);
//
//        // Convert HTML to PDF using wkhtmltopdf
//        htmlToPdfService.generatePdfFromHtml(model, tempPdf.getAbsolutePath());
//
//        // Apply Watermark (if required)
//        htmlToPdfService.applyWatermark(tempPdf, finalPdf);
//
//        // Delete temporary file
//        tempPdf.delete();
//
//        String returnFilePath = ourPath + fileName;
//        returnFilePath = returnFilePath.replace("\\", "/");  // Replace backslashes for URLs
//
//        return "redirect:/viewPDF?fileinfo=" + returnFilePath + "&filename=" + fileName;
//    }

    @GetMapping("/viewPDF")
    public void viewPDF(HttpServletResponse response, @RequestParam("fileinfo") String fileinfo,
                        @RequestParam("filename") String filename) throws IOException {
        File thePdfFile = new File(filePath + File.separator + fileinfo);
        if (thePdfFile.exists() && !thePdfFile.isDirectory()) {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
            response.setContentLength((int) thePdfFile.length());

            Files.copy(thePdfFile.toPath(), response.getOutputStream());
            response.getOutputStream().flush();
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "PDF Not Found");
        }
    }


    private void applyWatermark(File inputPdf, File outputPdf) throws IOException, DocumentException {
        PdfReader pdfReader = new PdfReader(new FileInputStream(inputPdf));
        PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(outputPdf));

        int totalPages = pdfReader.getNumberOfPages();
        PdfContentByte content;

        Font watermarkFont = new Font(Font.HELVETICA, 40, Font.BOLD, new GrayColor(0.75f)); // Light gray
        Phrase watermark = new Phrase("DRAFT", watermarkFont);

        for (int i = 1; i <= totalPages; i++) {
            content = pdfStamper.getUnderContent(i);
            float x = (pdfReader.getPageSize(i).getWidth()) / 2;
            float y = (pdfReader.getPageSize(i).getHeight()) / 2;

            PdfGState gState = new PdfGState();
            gState.setFillOpacity(0.3f); // Adjust opacity for a lighter watermark
            content.setGState(gState);

            ColumnText.showTextAligned(content, Element.ALIGN_CENTER, watermark, x, y, 45);
        }

        pdfStamper.close();
        pdfReader.close();
    }

    public String cleanHtmlForPdf(String html) {
        Document doc = Jsoup.parseBodyFragment(html);
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return doc.body().html();
    }

    private String renderHtmlContentWithModel(Model model, SpringTemplateEngine templateEngine) {
        Context context = new Context();
        context.setVariables(model.asMap());

        // Render HTML content using Thymeleaf template engine
        return templateEngine.process("pdfcopy", context); // "pdfsample" is the name of your Thymeleaf template
    }

    private SpringTemplateEngine getTemplateEngine(ServletContext servletContext) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }

    private String renderHtmlContent(Model model, SpringTemplateEngine templateEngine) {
        Context context = new Context();
        context.setVariables(model.asMap());

        // Render HTML content
        return templateEngine.process("pdfsample", context); // "pdfsample" is the name of your Thymeleaf template
    }


    @PostMapping("/forwardQuestions")
    @Transactional
    public String forwardQuestions(@RequestParam Integer id,
                                   @RequestParam String subjectId,
                                   @RequestParam Long setterId,
                                   RedirectAttributes redirectAttributes) {
        UserDetails userDetails = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User user = userService.getUserByUserName(userDetails.getUsername());
        //Appointment appointment = appointmentService.getAppointmentDetails(userDetails.getUsername());
        List<BitwiseQuestions> questionsList = qpTemplateService.getQuestionsList(subjectId, id, user.getId());

        Subjects subjects = subjectsService.getSubjectById(subjectId);
        SectionUserMappingEntity sectionUserMappingEntity = userMappingService.getMappingDetailsBySectionIdAndRoleId(subjects.getSectionId(), 1);

        questionsList.forEach(question -> {
            question.setQp_setter_status("SUBMITTED");
            question.setQpSetterId(user.getId());

            question.setQpReviewerId(Integer.parseInt(sectionUserMappingEntity.getUserId() + ""));

        });
// Save all updated questions in one batch
        qpTemplateService.saveAllQuestions(questionsList);
        QPFilesEntity qpFilesEntity = qpFilesService.getQPFilesByUser(user.getId(), subjectId, id, user.getId());
        if (qpFilesEntity == null) {
            qpFilesEntity = new QPFilesEntity();
        } else {
            qpFilesEntity.setId(qpFilesEntity.getId());
        }
        qpFilesEntity.setSetNo(id);
        qpFilesEntity.setQp_status("SUBMITTED");
        qpFilesEntity.setUserId(user.getId());
        qpFilesEntity.setSubjectId(Long.valueOf(subjectId));
        qpFilesEntity.setRollId(user.getRoleId());
        LocalDateTime now = LocalDateTime.now();

        // Define the formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        // Format the date-time
        String formattedDate = now.format(formatter);

        qpFilesEntity.setQp_status_date(formattedDate);
        qpFilesEntity.setRemarks("");
        qpFilesEntity.setExamSeriesId(1);
        qpFilesEntity.setQpSetterId(user.getId());
        qpFilesService.saveQPs(qpFilesEntity);
        QPFilesEntity rqpFilesEntity = qpFilesService.getQPFilesByUser(Long.valueOf(sectionUserMappingEntity.getUserId()), subjectId, id, user.getId());
        if (rqpFilesEntity == null) {
            rqpFilesEntity = new QPFilesEntity();
        } else {
            rqpFilesEntity.setId(rqpFilesEntity.getId());
        }
        rqpFilesEntity.setSetNo(id);
        rqpFilesEntity.setQp_status("PENDING");
        rqpFilesEntity.setUserId(Long.valueOf(sectionUserMappingEntity.getUserId()));
        rqpFilesEntity.setRollId(sectionUserMappingEntity.getRoleId());
        rqpFilesEntity.setSubjectId(Long.valueOf(subjectId));


        rqpFilesEntity.setQp_status_date(formattedDate);
        rqpFilesEntity.setRemarks("");
        rqpFilesEntity.setQpSetterId(user.getId());
        rqpFilesEntity.setExamSeriesId(1);
        qpFilesService.saveQPs(rqpFilesEntity);

        return "redirect:/setterdashboard";

    }

    @GetMapping("/reviewForward/{id}/{subjectId}/{setterId}")
    @Transactional
    public String reviewForward(@PathVariable("id") int id, @PathVariable("subjectId")
    String subjectId, Long setterId) {
        UserDetails userDetails = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User user = userService.getUserByUserName(userDetails.getUsername());

        Subjects subjects = subjectsService.getSubjectById(subjectId);

        QPFilesEntity qpFilesEntity = qpFilesService.getQPFilesByUser(user.getId(), subjectId, id, setterId);
        if (qpFilesEntity == null) {
            qpFilesEntity = new QPFilesEntity();
        } else {
            qpFilesEntity.setId(qpFilesEntity.getId());
        }
        qpFilesEntity.setSetNo(id);
        qpFilesEntity.setQp_status("SUBMITTED");
        qpFilesEntity.setUserId(user.getId());
        qpFilesEntity.setSubjectId(Long.valueOf(subjectId));
        qpFilesEntity.setRollId(user.getRoleId());
        LocalDateTime now = LocalDateTime.now();

        // Define the formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        // Format the date-time
        String formattedDate = now.format(formatter);
        qpFilesEntity.setQp_status_date(formattedDate);
        qpFilesEntity.setRemarks("");
        qpFilesEntity.setQpSetterId(setterId);
        qpFilesService.saveQPs(qpFilesEntity);
        QPFilesEntity rqpFilesEntity = qpFilesService.getQPFilesByUser((long) subjects.getSectionId(), subjectId, id, setterId);
        if (rqpFilesEntity == null) {
            rqpFilesEntity = new QPFilesEntity();
        } else {
            rqpFilesEntity.setId(rqpFilesEntity.getId());
        }
        rqpFilesEntity.setSetNo(id);
        rqpFilesEntity.setQp_status("PENDING");
        rqpFilesEntity.setUserId((long) subjects.getSectionId());
        rqpFilesEntity.setRollId(1);
        rqpFilesEntity.setSubjectId(Long.valueOf(subjectId));

        rqpFilesEntity.setQp_status_date(formattedDate);
        rqpFilesEntity.setRemarks("");
        rqpFilesEntity.setQpSetterId(setterId);
        qpFilesService.saveQPs(rqpFilesEntity);

        return "redirect:/moderatordashboard";

    }


    @PostMapping("/updateQuestions")
    public String updateQuestions(
            @RequestParam("q_desc") String questionDescription,
            @RequestParam("qid") Long qid,
            @RequestParam(value = "instructions", required = false) String instructions,
            @RequestParam(value = "q_solution", required = false) String q_solution,
            @RequestParam(value = "image_file", required = false) MultipartFile imageFile, RedirectAttributes redirectAttributes) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        BitwiseQuestions question = qpTemplateService.getQuestionDetailsById(qid);
        UserDetails userDetails = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User userEntity = userRepository.findByUserName(userDetails.getUsername());
        Roles roles = rolesRepository.findById(Long.valueOf(userEntity.getRoleId())).get();
        try {


            question.setQ_desc(questionDescription);
            question.setInstructions(instructions);
            question.setQ_solution(q_solution);


            question.setLast_updated_by(userDetails.getUsername());
            if (imageFile != null && !imageFile.isEmpty()) {
                byte[] imageBytes = imageFile.getBytes();
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                question.setImage_path(base64Image);
            } else {
                question.setImage_path(question.getImage_path());
            }
            qpTemplateService.saveQuestion(question);
            redirectAttributes.addFlashAttribute("successMessage", "Question updated successfully!");

            // ✅ Redirect based on Role
            if (roles.getId() == 2) {
                return "redirect:/bitwisequestionsList?id=" + question.getSetNo() + "&subjectId=" + question.getSubjectId();
            }
            if (roles.getId() == 3) {
                return "redirect:/forwardQuestions/" + question.getSetNo() + "/" + question.getSubjectId();
            }

        } catch (DataIntegrityViolationException e) {
            logger.error("Unique constraint violation: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Duplicate entry! This question already exists.");
        } catch (MaxUploadSizeExceededException e) {
            logger.error("File size too large: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "File size exceeds limit.");
        } catch (DataAccessException e) {
            logger.error("Database error: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Database error occurred.");
        } catch (Exception e) {
            logger.error("Unexpected error: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred.");
        }
        if (roles.getId() == 2) {
            return "redirect:/bitwisequestionsList?id=" + question.getSetNo() + "&subjectId=" + question.getSubjectId();
        }
        if (roles.getId() == 3) {
            return "redirect:/forwardQuestions/" + question.getSetNo() + "/" + question.getSubjectId();
        }
        return "";
    }

    @GetMapping("/questionApprove/{id}")
    public String questionApprove(@PathVariable("id") Long qid) {
        BitwiseQuestions question = qpTemplateService.getQuestionDetailsById(qid);
        question.setQp_reviewer_status("Approved");
        question.setReviewer_comments("");
        qpTemplateService.saveQuestion(question);

        return "redirect:/reviewerQuestionsList?id=" + question.getSetNo() + "&subjectId=" + question.getSubjectId() + "&setterId=" + question.getQpSetterId();
    }

    @PostMapping("/reviewerApproval")
    public String processApproval(@RequestParam("qpointId") Long qpointId,
                                  @RequestParam("approved") String approved,
                                  @RequestParam(value = "comments", required = false) String comments, RedirectAttributes redirectAttributes) {
        UserDetails userDetails = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User user = userService.getUserByUserName(userDetails.getUsername());
        BitwiseQuestions question = qpTemplateService.getQuestionDetailsById(qpointId);
        question.setQpReviewerId(Math.toIntExact(user.getId()));
        question.setQp_reviewer_status(approved);
        question.setReviewer_comments(comments);
        qpTemplateService.saveQuestion(question);
        redirectAttributes.addFlashAttribute("successMessage", approved + " successfully!");
        return "redirect:/moderatordashboard";
    }

    @PostMapping("/saveSubjectInstruction")
    public String saveSubjectInstruction(@RequestParam(value = "subjectId") Integer subjectId, @RequestParam(value = "qp_instruction") String qp_instruction, RedirectAttributes redirectAttributes) {
        UserDetails userDetails = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User user = userService.getUserByUserName(userDetails.getUsername());
        UserData userData = appointmentService.getUserDataSubjectIdAndUserIdAndExamSeriesId(subjectId, Math.toIntExact(user.getId()), 1);
        userData.setQp_instruction(qp_instruction);
        appointmentService.saveUserAppointment(userData);
        return "redirect:/bitwisequestionsList?id=1&subjectId=" + subjectId;
    }

    @PostMapping("/setWiseReviewerApproval")
    public String setWiseReviewerApproval(Model model, @RequestParam("setId") int setId,
                                          @RequestParam("setterId") Long setterId,
                                          @RequestParam("subjectId") String subjectId,
                                          @RequestParam(value = "comments") String comments,
                                          @RequestParam(value = "approved") String approved, RedirectAttributes redirectAttributes) throws IOException, InterruptedException {
        UserDetails userDetails = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User user = userService.getUserByUserName(userDetails.getUsername());

        QPFilesEntity qpFilesEntity = qpFilesService.getQPFilesByUser(user.getId(), subjectId, setId, setterId);

        qpFilesEntity.setQp_status(approved);
        qpFilesEntity.setSubjectId(Long.valueOf(subjectId));
        LocalDateTime now = LocalDateTime.now();

        // Define the formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        // Format the date-time
        String formattedDate = now.format(formatter);
        qpFilesEntity.setQp_status_date(formattedDate);
        qpFilesEntity.setRemarks(comments);
        qpFilesEntity.setQpSetterId(setterId);
        qpFilesService.saveQPs(qpFilesEntity);
        Subjects subject = subjectsService.getSubjectById(subjectId);

        if (approved.equalsIgnoreCase("APPROVED")) {

            QpInventoryEntity inventoryEntity = new QpInventoryEntity();

            inventoryEntity.setSubjectId(Integer.valueOf(subjectId));
            inventoryEntity.setSetterUserId(Math.toIntExact(setterId));
            inventoryEntity.setSectionUserId(Math.toIntExact(user.getId()));
            inventoryEntity.setQpFilesRefId(Math.toIntExact(qpFilesEntity.getId()));
            inventoryEntity.setExamSeriesId(1);
            inventoryEntity.setQp_file_path(generateQPPdf(model, inventoryEntity));
            inventoryEntity.setFile_generated_date(formattedDate);
            inventoryService.saveQPInventoryFile(inventoryEntity);


        }


        if (approved.equalsIgnoreCase("REJECTED")) {

            // User userById = userService.getUserById(String.valueOf(subject.getSection_user_id()));
            QPFilesEntity rqpFilesEntity = qpFilesService.getQPFilesByRollIdAndSubjectIdAndSetNoAndSetterId(2, subjectId, setId, setterId);
            User setterUser = userService.getUserById(rqpFilesEntity.getUserId() + "");
            if (rqpFilesEntity == null) {
                rqpFilesEntity = new QPFilesEntity();
            } else {
                rqpFilesEntity.setId(rqpFilesEntity.getId());
            }
            rqpFilesEntity.setSetNo(setId);
            rqpFilesEntity.setQp_status("Re-Submit");
            // rqpFilesEntity.setUserId(subject.getSection_user_id());
            //rqpFilesEntity.setRollId(userById.getRoleId());
            rqpFilesEntity.setSubjectId(Long.valueOf(subjectId));
            rqpFilesEntity.setQp_status_date(formattedDate);
            rqpFilesEntity.setRemarks("");
            rqpFilesEntity.setQpSetterId(setterId);
            qpFilesService.saveQPs(rqpFilesEntity);
            String emailBody = "Dear User,\n\n" +
                    "Your QP for the " + subject.getSubjectCode() + "-" + subject.getSubject_name() + " got rejected.\n\n" +
                    "Login to the QP Setter Application and check the remarks given for the Rejected QP.\n\n" +
                    "Implement the changes/suggestions provided and re-submit/forward the QP to admin for review and approval.\n\n" +
                    "Thanks\n" +
                    "Admin\n" +
                    "SOQPRS";

            //send mail if reject the file
            sendMail.sendHtmlmail(setterUser.getEmail(),
                    "GTU SOQPRS - Your QP for the " + subject.getSubjectCode() + " got rejected, check and re-submit", emailBody);


        }
        redirectAttributes.addFlashAttribute("successMessage", "QP " + approved + " successfully");

        return "redirect:/moderatordashboard";
    }

    private String saveBase64Image(String base64Image, String outputPath) throws IOException {
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        File outputFile = new File(outputPath);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(imageBytes);
        }
        return outputFile.getAbsolutePath();  // Return the saved file path
    }

    private String generateQPPdf(Model model, QpInventoryEntity inventoryEntity) throws IOException, InterruptedException {

        List<BitwiseQuestions> questionsList = qpTemplateService.getReviewerQuestionsList(inventoryEntity.getSubjectId() + "", 1, Long.valueOf(inventoryEntity.getSectionUserId()), Long.valueOf(inventoryEntity.getSetterUserId()));
        Subjects subjects = subjectsService.getSubjectById(inventoryEntity.getSubjectId() + "");
        model.addAttribute("subjects", subjects);
        model.addAttribute("questionsList", questionsList);
        model.addAttribute("groupedQuestions", questionsList.stream().collect(Collectors.groupingBy(BitwiseQuestions::getQ_no)));

        String fileName = subjects.getSubjectCode() + "_" + inventoryEntity.getQpFilesRefId() + ".pdf";
        String commonPath = filePath + File.separator;
        String ourPath = "QuestionBank" + File.separator + inventoryEntity.getSectionUserId() + File.separator;
        String serverPath = commonPath + ourPath;

        FileUploadUtil fileUpload = new FileUploadUtil();
        fileUpload.createRequiredDirectoryIfNotExists(commonPath, ourPath);

        File finalPdf = new File(serverPath, fileName);

        // Convert HTML to PDF using wkhtmltopdf
        htmlToPdfService.generatePdfFromHtml(model, finalPdf.getAbsolutePath());
        return ourPath + fileName;


    }

    @GetMapping("/solutionPreviewPdf/{subjectId}/{setterId}")
    public String solutionPreviewPdf(HttpServletRequest request, Model model,
                                     @PathVariable("subjectId") int subjectId, @PathVariable("setterId") Long setterId) throws IOException, DocumentException {
        // Fetch User & Appointment Details
        UserDetails userDetails = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User userEntity = userService.getUserByUserName(userDetails.getUsername());

        List<BitwiseQuestions> questionsList = qpTemplateService.getQuestionsList(subjectId + "", 1, setterId);

        Subjects subjects = subjectsService.getSubjectById(subjectId + "");

        model.addAttribute("subjects", subjects);
        model.addAttribute("questionsList", questionsList);
        model.addAttribute("watermark", subjects.getSubjectCode() + "," + userDetails.getUsername() + "," + LocalDateTime.now());
        model.addAttribute("groupedQuestions", questionsList.stream().collect(Collectors.groupingBy(BitwiseQuestions::getQ_no)));
        return "solutionPreview";
    }

    @PostMapping("/rejectQidQuestions")
    public String rejectQidQuestions(@RequestParam("id") Long qid, @RequestParam("rejectQidComments") String rejectQidComments) {
        BitwiseQuestions question = qpTemplateService.getQuestionDetailsById(qid);
        question.setQp_reviewer_status("Rejected");
        question.setReviewer_comments(rejectQidComments);
        qpTemplateService.saveQuestion(question);


        return "redirect:/reviewerQuestionsList?id=" + question.getSetNo() + "&subjectId=" + question.getSubjectId() + "&setterId=" + question.getQpSetterId();

    }

}