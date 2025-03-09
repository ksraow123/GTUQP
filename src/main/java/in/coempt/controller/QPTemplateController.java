package in.coempt.controller;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.*;
import in.coempt.entity.*;
import in.coempt.repository.RolesRepository;
import in.coempt.repository.UserRepository;
import in.coempt.service.*;
import in.coempt.service.impl.HtmlToPdfService;
import in.coempt.util.FileUploadUtil;
import in.coempt.util.SecurityUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private QPFilesService qpFilesService;

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
        List<BitwiseQuestions> qPtemplate = qpTemplateService.getQuestionsList(subjectId,setNo,userEntity.getId());
        model.addAttribute("qPtemplate", qPtemplate);
        model.addAttribute("setNo", setNo);
        //model.addAttribute("page", "QPTemplate");
        return "QPTemplate";

    }

    @GetMapping("/reviewerQuestionsList")
    public String reviewerQuestionsList(
                                        @RequestParam("id") int setNo,
                                        @RequestParam("subjectId") String subjectId,
                                        Model model) {
        UserDetails user = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();

        User userEntity = userService.getUserByUserName(user.getUsername());
      //  Appointment appointment = appointmentService.getAppointmentDetails(user.getUsername());
        Subjects subjects = subjectsService.getSubjectById(subjectId);
//        List<BitwiseQuestions> qPtemplate = qpTemplateService.getQuestionsList(subjectId,setNo,userEntity.getId());
        List<BitwiseQuestions> qPtemplate = qpTemplateService.getReviewerQuestionsList(subjectId,setNo,userEntity.getId());

        model.addAttribute("qPtemplate", qPtemplate);
        model.addAttribute("subject", subjects);
        model.addAttribute("setNo", setNo);
      Boolean isApproved=  qPtemplate.stream()
                .allMatch(q -> "Approved".equals(q.getQp_reviewer_status()));
        model.addAttribute("isApproved", isApproved);
        return "reviewerQPTemplate";

    }

    @PostMapping("/saveQuestions")
    public String saveQuestion(@RequestParam("q_desc") String questionDescription,
                               @RequestParam("qid") Long qid,
                               @RequestParam(value = "instructions", required = false) String instructions,
                               @RequestParam(value = "image_file", required = false) MultipartFile imageFile) {

        try {
            UserDetails user = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
            BitwiseQuestions question = qpTemplateService.getQuestionDetailsById(qid);

            question.setQ_desc(questionDescription);
            question.setInstructions(instructions);

            // **Convert Image to Base64 String**
            if (imageFile != null && !imageFile.isEmpty()) {
                byte[] imageBytes = imageFile.getBytes();
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);  // ✅ Convert Image to Base64
                question.setImage_path(base64Image);
            }
            // Save question in DB
            qpTemplateService.saveQuestion(question);
            return "redirect:/bitwisequestionsList";
        } catch (Exception e) {

        }

        return "redirect:/bitwisequestionsList";
    }


    @GetMapping("/getQuestionDetails/{id}")
    @ResponseBody
    public BitwiseQuestions getQuestionDetails(@PathVariable("id") Long id) {
        return qpTemplateService.getQuestionById(id);

    }

    @GetMapping("/previewPdf/{id}/{subjectId}")
    public String previewPdf(HttpServletRequest request, Model model,
                             @PathVariable("id") int id,@PathVariable("subjectId") int subjectId) throws IOException, DocumentException {
        // Fetch User & Appointment Details
        UserDetails userDetails = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User userEntity = userService.getUserByUserName(userDetails.getUsername());

        //Appointment appointment = appointmentService.getAppointmentDetails(userDetails.getUsername());


        List<BitwiseQuestions> questionsList =null;
        if(userEntity.getRoleId()==2) {
            questionsList = qpTemplateService.getQuestionsList(subjectId + "", id, userEntity.getId());
        }
        if(userEntity.getRoleId()==3) {
            questionsList = qpTemplateService.getReviewerQuestionsList(subjectId + "", id, userEntity.getId());
        }

        // Process question descriptions and images
        for (BitwiseQuestions qpoint : questionsList) {
            if (qpoint.getQ_desc() != null) {
                qpoint.setQ_desc(cleanHtmlForPdf(qpoint.getQ_desc()));
            }
            if (qpoint.getImage_path() != null) {
                String imagePath = "data:image/png;base64," + qpoint.getImage_path();
                qpoint.setImage_path(imagePath);
            }
        }

        List<QPTemplate> qpTemplateList = qpTemplateService.getQuestionTemplate(subjectId);
        for (QPTemplate qpTemplate : qpTemplateList) {
            qpTemplate.setInstructions(String.format(qpTemplate.getInstructions(), qpTemplate.getNo_of_bits_answer()));
        }
        model.addAttribute("qpTemplateList", qpTemplateList);

     Subjects subjects=subjectsService.getSubjectById(subjectId+"");

        model.addAttribute("subjects", subjects);
        model.addAttribute("watermark", subjects.getSubjectCode()+","+userDetails.getUsername()+","+LocalDateTime.now());
        model.addAttribute("groupedQuestions", questionsList.stream().collect(Collectors.groupingBy(BitwiseQuestions::getQ_no)));
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


    @GetMapping("/forwardQuestions/{id}/{subjectId}")
    @Transactional
    public String forwardQuestions( @PathVariable("id") int id,@PathVariable("subjectId") String subjectId) {
        UserDetails userDetails = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User user = userService.getUserByUserName(userDetails.getUsername());
        //Appointment appointment = appointmentService.getAppointmentDetails(userDetails.getUsername());
        List<BitwiseQuestions> questionsList = qpTemplateService.getQuestionsList(subjectId,id,user.getId());
        SetterModeratorMapping setterModeratorMapping=setterModeratorService.getReviewerDetails(user.getId(), Long.valueOf(subjectId));
        questionsList.forEach(question -> {
            question.setQp_setter_status("FORWARDED");
            question.setQpSetterId(user.getId());

            question.setQpReviewerId(Integer.parseInt(setterModeratorMapping.getModeratorId()+""));

        });
// Save all updated questions in one batch
        qpTemplateService.saveAllQuestions(questionsList);
        QPFilesEntity qpFilesEntity = qpFilesService.getQPFilesByUser(user.getId(),subjectId,id);
        if(qpFilesEntity==null){
            qpFilesEntity=new QPFilesEntity();
        }
        else {
            qpFilesEntity.setId(qpFilesEntity.getId());
        }
            qpFilesEntity.setSetNo(id);
            qpFilesEntity.setQp_status("FORWARDED");
            qpFilesEntity.setUserId(user.getId());
        qpFilesEntity.setSubjectId(Long.valueOf(subjectId));
        qpFilesEntity.setRollId(user.getRoleId());
            qpFilesEntity.setQp_status_date(LocalDateTime.now() + "");
            qpFilesEntity.setRemarks("");
            qpFilesService.saveQPs(qpFilesEntity);
        QPFilesEntity rqpFilesEntity = qpFilesService.getQPFilesByUser(setterModeratorMapping.getModeratorId(),subjectId,id);
        if(rqpFilesEntity==null){
            rqpFilesEntity=new QPFilesEntity();
        }
        else {
            rqpFilesEntity.setId(rqpFilesEntity.getId());
        }
        rqpFilesEntity.setSetNo(id);
        rqpFilesEntity.setQp_status("PENDING");
        rqpFilesEntity.setUserId(setterModeratorMapping.getModeratorId());
        rqpFilesEntity.setRollId(user.getRoleId()+1);
        rqpFilesEntity.setSubjectId(Long.valueOf(subjectId));
        rqpFilesEntity.setQp_status_date(LocalDateTime.now() + "");
        rqpFilesEntity.setRemarks("");
        qpFilesService.saveQPs(rqpFilesEntity);

        return "redirect:/setterdashboard";

    }

    @GetMapping("/reviewForward/{id}/{subjectId}")
    @Transactional
    public String reviewForward( @PathVariable("id") int id,@PathVariable("subjectId") String subjectId) {
        UserDetails userDetails = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User user = userService.getUserByUserName(userDetails.getUsername());

        Subjects subjects = subjectsService.getSubjectById(subjectId);

        QPFilesEntity qpFilesEntity = qpFilesService.getQPFilesByUser(user.getId(),subjectId,id);
        if(qpFilesEntity==null){
            qpFilesEntity=new QPFilesEntity();
        }
        else {
            qpFilesEntity.setId(qpFilesEntity.getId());
        }
        qpFilesEntity.setSetNo(id);
        qpFilesEntity.setQp_status("FORWARDED");
        qpFilesEntity.setUserId(user.getId());
        qpFilesEntity.setSubjectId(Long.valueOf(subjectId));
        qpFilesEntity.setRollId(user.getRoleId());
        qpFilesEntity.setQp_status_date(LocalDateTime.now() + "");
        qpFilesEntity.setRemarks("");
        qpFilesService.saveQPs(qpFilesEntity);
        QPFilesEntity rqpFilesEntity = qpFilesService.getQPFilesByUser(subjects.getSection_user_id(),subjectId,id);
        if(rqpFilesEntity==null){
            rqpFilesEntity=new QPFilesEntity();
        }
        else {
            rqpFilesEntity.setId(rqpFilesEntity.getId());
        }
        rqpFilesEntity.setSetNo(id);
        rqpFilesEntity.setQp_status("PENDING");
        rqpFilesEntity.setUserId(subjects.getSection_user_id());
        rqpFilesEntity.setRollId(1);
        rqpFilesEntity.setSubjectId(Long.valueOf(subjectId));
        rqpFilesEntity.setQp_status_date(LocalDateTime.now() + "");
        rqpFilesEntity.setRemarks("");
        qpFilesService.saveQPs(rqpFilesEntity);

        return "redirect:/moderatordashboard";

    }






    @PostMapping("/updateQuestions")
    public String updateQuestions(
            @RequestParam("q_desc") String questionDescription,
            @RequestParam("qid") Long qid,
            @RequestParam(value = "instructions", required = false) String instructions,
            @RequestParam(value = "q_solution", required = false) String q_solution,
            @RequestParam(value = "image_file", required = false) MultipartFile imageFile) {

        try {
            UserDetails userDetails = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
            User userEntity = userRepository.findByUserName(userDetails.getUsername());
            Roles roles = rolesRepository.findById(Long.valueOf(userEntity.getRoleId())).get();


            BitwiseQuestions question = qpTemplateService.getQuestionDetailsById(qid);

            question.setQ_desc(questionDescription);
            question.setInstructions(instructions);
            question.setQ_solution(q_solution);


            question.setLast_updated_by(userDetails.getUsername());
            if (imageFile != null && !imageFile.isEmpty()) {
                byte[] imageBytes = imageFile.getBytes();
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                question.setImage_path(base64Image);
            }
            else{
                question.setImage_path(question.getImage_path());
            }
            qpTemplateService.saveQuestion(question);
            if (roles.getId() == 2) {
                return "redirect:/bitwisequestionsList?id="+question.getSetNo()+"&subjectId="+question.getSubjectId();
            }
            if (roles.getId() == 3) {
                return "redirect:/forwardQuestions/"+question.getSetNo()+"/"+question.getSubjectId();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";

}
    @GetMapping("/questionApprove/{id}")
    public String questionApprove(@PathVariable("id") Long qid) {
        try {
            BitwiseQuestions question = qpTemplateService.getQuestionDetailsById(qid);
            UserDetails userDetails = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
            User userEntity = userRepository.findByUserName(userDetails.getUsername());
            Roles roles = rolesRepository.findById(Long.valueOf(userEntity.getRoleId())).get();
            question.setQp_reviewer_status("Approved");
            qpTemplateService.saveQuestion(question);
            if (roles.getId() == 2) {
                return "redirect:/bitwisequestionsList?id="+question.getSetNo()+"&subjectId="+question.getSubjectId();
            }
            if (roles.getId() == 3) {
                return "redirect:/reviewerQuestionsList?id="+question.getSetNo()+"&subjectId="+question.getSubjectId();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    @PostMapping("/reviewerApproval")
    public String processApproval( @RequestParam("qpointId") Long qpointId,
                                   @RequestParam("approved") String approved,
                                   @RequestParam(value = "comments", required = false) String comments) {
        UserDetails userDetails=(UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User user = userService.getUserByUserName(userDetails.getUsername());
        BitwiseQuestions question = qpTemplateService.getQuestionDetailsById(qpointId);
        question.setQpReviewerId(Math.toIntExact(user.getId()));
        question.setQp_reviewer_status(approved);
        question.setReviewer_comments(comments);
        qpTemplateService.saveQuestion(question);
        return "redirect:/reviewerQuestionsList?id="+question.getSetNo()+"&subjectId="+question.getSubjectId();
    }

    @PostMapping("/setWiseReviewerApproval")
    public String setWiseReviewerApproval( @RequestParam("setId") int setId,
                                   @RequestParam("subjectId") String subjectId,
                                   @RequestParam(value = "comments") String comments,
                                           @RequestParam(value = "approved") String approved                           ) {
        UserDetails userDetails=(UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User user = userService.getUserByUserName(userDetails.getUsername());

        QPFilesEntity qpFilesEntity = qpFilesService.getQPFilesByUser(user.getId(),subjectId,setId);

        qpFilesEntity.setQp_status(approved);
        qpFilesEntity.setSubjectId(Long.valueOf(subjectId));
        qpFilesEntity.setQp_status_date(LocalDateTime.now() + "");
        qpFilesEntity.setRemarks(comments);
        qpFilesService.saveQPs(qpFilesEntity);
       Subjects subject= subjectsService.getSubjectById(subjectId);
       User userById = userService.getUserById(String.valueOf(subject.getSection_user_id()));
        QPFilesEntity rqpFilesEntity = qpFilesService.getQPFilesByUser
                (subject.getSection_user_id(),subjectId,setId);
        if(rqpFilesEntity==null){
            rqpFilesEntity=new QPFilesEntity();
        }
        else {
            rqpFilesEntity.setId(rqpFilesEntity.getId());
        }
        rqpFilesEntity.setSetNo(setId);
        rqpFilesEntity.setQp_status("PENDING");
        rqpFilesEntity.setUserId(subject.getSection_user_id());
        rqpFilesEntity.setRollId(userById.getRoleId());
        rqpFilesEntity.setSubjectId(Long.valueOf(subjectId));
        rqpFilesEntity.setQp_status_date(LocalDateTime.now() + "");
        rqpFilesEntity.setRemarks("");
        qpFilesService.saveQPs(rqpFilesEntity);
        return "redirect:/reviewerQuestionsList?id="+setId+"&subjectId="+subjectId;
    }







    private String saveBase64Image(String base64Image, String outputPath) throws IOException {
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        File outputFile = new File(outputPath);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(imageBytes);
        }
        return outputFile.getAbsolutePath();  // Return the saved file path
    }
    @GetMapping("/generatePdf/{id}")
    public String generatePdf(HttpServletRequest request,Model model, @PathVariable("id") int setNo) throws IOException, ServletException, DocumentException {
        // Fetch User & Appointment Details
        UserDetails userDetails = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User userEntity = userRepository.findByUserName(userDetails.getUsername());
        Appointment appointment = appointmentService.getAppointmentDetails(userDetails.getUsername());
        List<BitwiseQuestions> questionsList = qpTemplateService.getQuestionsList(appointment.getSubject_id(), setNo,userEntity.getId());

        // Process question descriptions and images
        for (BitwiseQuestions qpoint : questionsList) {
            if (qpoint.getQ_desc() != null) {
                qpoint.setQ_desc(cleanHtmlForPdf(qpoint.getQ_desc()));
            }
            if (qpoint.getImage_path() != null) {
                String imagePath = "data:image/png;base64," + qpoint.getImage_path();
                qpoint.setImage_path(imagePath);
            }
        }

        List<QPTemplate> qpTemplateList = qpTemplateService.getQuestionTemplate(Integer.parseInt(appointment.getSubject_id()));
        for (QPTemplate qpTemplate : qpTemplateList) {
            qpTemplate.setInstructions(String.format(qpTemplate.getInstructions(), qpTemplate.getNo_of_bits_answer()));
        }
        model.addAttribute("qpTemplateList", qpTemplateList);

        model.addAttribute("watermark", userDetails.getUsername()+","+request.getRemoteAddr()+","+LocalDateTime.now());

        model.addAttribute("subjects", subjectsService.getSubjectById(appointment.getSubject_id()));
        model.addAttribute("groupedQuestions", questionsList.stream().collect(Collectors.groupingBy(BitwiseQuestions::getQ_no)));
             // Define PDF File Paths
        String fileName = "QuestionPaperPDF_" + userDetails.getUsername() + ".pdf";
        String tempFileName = "Temp_" + fileName; // Temporary PDF before watermark

        String commonPath = filePath + File.separator;
        String ourPath = "QuestionBank" + File.separator + userDetails.getUsername() + File.separator;
        String serverPath = commonPath + ourPath;

        FileUploadUtil fileUpload = new FileUploadUtil();
        fileUpload.createRequiredDirectoryIfNotExists(commonPath, ourPath);

        File tempPdf = new File(serverPath, tempFileName);
        File finalPdf = new File(serverPath, fileName);

        // Convert HTML to PDF using wkhtmltopdf
        htmlToPdfService.generatePdfFromHtml(model, tempPdf.getAbsolutePath());

        // Apply Watermark (if required)
        htmlToPdfService.applyWatermark(tempPdf, finalPdf);

        // Delete temporary file
        tempPdf.delete();

        String  returnFilePath = ourPath + fileName;
        returnFilePath = returnFilePath.replace("\\", "/"); // ✅ Replace backslashes with forward slashes


        QpUploadDetailsEntity qpUploadDetails = qpUploadService.getQPBySetNoAndSubjectId(1, Integer.parseInt(appointment.getSubject_id()));
        if (qpUploadDetails == null) {
            qpUploadDetails = new QpUploadDetailsEntity();
        }
        qpUploadDetails.setSetNo(setNo);
        qpUploadDetails.setQp_reviewer_status("APPROVED");
        qpUploadDetails.setQp_reviewed_date(LocalDateTime.now() + "");
        qpUploadDetails.setQp_reviewer_id(Math.toIntExact(userEntity.getId()));
        qpUploadDetails.setSubjectId(Integer.parseInt(appointment.getSubject_id()));
        qpUploadDetails.setQp_file(returnFilePath);

        qpUploadService.saveQPs(qpUploadDetails);

        return "redirect:/viewPDF?fileinfo=" + returnFilePath + "&filename=" + fileName;

    }
}

