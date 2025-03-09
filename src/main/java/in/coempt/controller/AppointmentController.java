package in.coempt.controller;

import in.coempt.entity.*;
import in.coempt.service.*;
import in.coempt.util.SecurityUtil;
import in.coempt.util.SendMailUtil;
import in.coempt.vo.AppointmentVo;
import in.coempt.vo.IndividualAppointmentVo;
import in.coempt.vo.ProfileDetailsVo;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.MailException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Value("${filePath}")
    private String filePath;


    @Autowired
    private TemplateEngine templateEngine;
    private final AppointmentService appointmentService;
    private final CourseService courseService;
    private final UserService userService;
    private final SendMailUtil sendMail;
    private final SubjectsService subjectsService;
    private final RolesService rolesService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SmsEmailTemplateService smsEmailTemplateService;
    private final CollegeService collegeService;
    private final UserDetailsService userDetailsService;


    @Value("${app.url}")
    private String appUrl;

    public AppointmentController(AppointmentService appointmentService, CourseService courseService,
                                 UserService userService, SendMailUtil sendMail, SubjectsService subjectsService,
                                 RolesService rolesService, BCryptPasswordEncoder passwordEncoder, SmsEmailTemplateService smsEmailTemplateService, CollegeService collegeService,UserDetailsService userDetailsService ) {
        this.appointmentService = appointmentService;
        this.courseService = courseService;
        this.userService = userService;
        this.sendMail = sendMail;
        this.subjectsService = subjectsService;
        this.rolesService = rolesService;
        this.passwordEncoder = passwordEncoder;
        this.smsEmailTemplateService = smsEmailTemplateService;
        this.collegeService = collegeService;
this.userDetailsService=userDetailsService;
    }

    @GetMapping("/list")
    public String getAppointmentList(Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("appointmentList", appointmentService.getAllAppointments());
        model.addAttribute("coursesList", courseService.getAllCourses());
        model.addAttribute("collegeList", collegeService.getAllColleges());
        return "newappointment";
    }
    @GetMapping("/individualAppointments")
    public String individualAppointments(Model model) {
        model.addAttribute("coursesList", courseService.getAllCourses());
        model.addAttribute("collegeList", collegeService.getAllColleges());
        model.addAttribute("individualAppoint", new IndividualAppointmentVo());
        model.addAttribute("page","individualAppointment");
        return "main";
    }
    @PostMapping("/individual/save")
    @Transactional
<<<<<<< HEAD
    public String saveAppointment(Model model, RedirectAttributes redirectAttributes, IndividualAppointmentVo appointmentVo) {
        try {
            String customPassword = RandomUtils.nextLong(10000, 99999) + "";

            User user = userService.getUserByMobileNoAndEmailAndRoleId(
                    appointmentVo.getMobile_number(), appointmentVo.getEmail(), appointmentVo.getRole_id());

            boolean isNewUser = (user == null);
            if (isNewUser) {
                user = createNewUser(appointmentVo, customPassword);

            }

            UserData checkUserData=appointmentService.getAppointmentDetailsByUserIdAndSubjectId(appointmentVo,user);
            if(checkUserData!=null){
                redirectAttributes.addFlashAttribute("message", "Selected Subject has already mapped");
                return "redirect:/appointments/individualAppointments";
            }
            UserData userData = createUserData(appointmentVo, user);


            UserData savedUserData = appointmentService.saveuserData(userData);

            sendAppointmentEmail(appointmentVo, user, savedUserData, isNewUser);

            redirectAttributes.addFlashAttribute("message",
                    (isNewUser ? "User" : "Additional subject") + " appointment details sent successfully to : " + appointmentVo.getEmail());

            return "redirect:/appointments/individualAppointments";
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "A data integrity error occurred: " + e.getMessage());
        } catch (MailException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send appointment email: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred: " + e.getMessage());
        }
        return "redirect:/appointments/individualAppointments";
    }

    private User createNewUser(IndividualAppointmentVo appointmentVo, String password) {
        User user = new User();
=======
    public String saveAppointment(Model model, RedirectAttributes redirectAttributes, IndividualAppointmentVo appointmentVo){
        String customPassword = RandomUtils.nextLong(10000, 99999) + "";

        User userexists = userService.getUserByMobileNo(appointmentVo.getMobile_number());
        if (userexists != null){
            redirectAttributes.addFlashAttribute("message", "User already exists with mobile number: "+appointmentVo.getMobile_number());
            return "redirect:/appointments/individualAppointments";
        }
        User user=new User();
>>>>>>> origin/master
        user.setFirstName(appointmentVo.getFname());
        user.setLastName(appointmentVo.getLname());
        user.setIsActive(0);
        user.setMobileNo(appointmentVo.getMobile_number());
        user.setEmail(appointmentVo.getEmail());
        user.setUserName(generateUserNameByRole(appointmentVo.getRole_id()));
        user.setRoleId(appointmentVo.getRole_id());
        user.setPassword(passwordEncoder.encode(password));
        return userService.saveUser(user);
    }

    private UserData createUserData(IndividualAppointmentVo appointmentVo, User user) {
        CollegeEntity collegeEntity = collegeService.getCollegeByCode(appointmentVo.getCollegeCode());

        UserData userData = new UserData();
        userData.setNo_of_sets(Integer.parseInt(appointmentVo.getNo_of_sets()));
        userData.setUserId(Math.toIntExact(user.getId()));
        userData.setCollege_id(String.valueOf(collegeEntity.getId()));
        userData.setOffice_order_date(LocalDateTime.now().toString());
        userData.setLast_date_to_submit(appointmentVo.getSubmission_date());
        userData.setSubjectId(Math.toIntExact(appointmentVo.getSubject_id()));
        userData.setRole_id(appointmentVo.getRole_id());
        userData.setCurrent_status("Appointment Sent");
        userData.setStatus_date(LocalDateTime.now().toString());
        userData.setAppointment_sent_date(LocalDateTime.now().toString());

        return userData;
    }

    private void sendAppointmentEmail(IndividualAppointmentVo appointmentVo, User user, UserData userData, boolean isNewUser) {
        Subjects subject = subjectsService.getSubjectById(String.valueOf(appointmentVo.getSubject_id()));
        Course course = courseService.getCourseDetailsById(subject.getCourseId());
        CollegeEntity collegeEntity = collegeService.getCollegeByCode(appointmentVo.getCollegeCode());

        Context context = new Context();
        context.setVariable("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        context.setVariable("name", appointmentVo.getFname() + " " + appointmentVo.getLname());
        context.setVariable("collegeCode", collegeEntity.getCollegeCode());
        context.setVariable("subjectCode", subject.getSubjectCode());
        context.setVariable("subjectName", subject.getSubject_name());
        context.setVariable("yearCourse", subject.getYear() + "/" + course.getCourse_name());
        context.setVariable("submissionDate", appointmentVo.getSubmission_date());

        String emailContent = templateEngine.process("email-template", context);
        String actionType = isNewUser ? "accept" : "addlSubjectAccept";
        String declineActionType = isNewUser ? "notaccept" : "addlSubjectDecline";

        String emailBody = emailContent + "<p>Please click on the link below to accept your appointment :</p>" +
                "<p><a href='" + appUrl + "appointments/" + userData.getId() + "/" + user.getUserName() + "/" + actionType + "'>Accept Appointment</a></p>" +
                "<p>If you are not willing to accept this appointment, please click the link below:</p>" +
                "<p><a href='" + appUrl + "appointments/" + userData.getId() + "/" + user.getUserName() + "/" + declineActionType + "'>Decline Appointment</a></p>";

        sendMail.sendHtmlMail(user.getEmail(), "Your appointment details of GTU", emailBody, filePath + File.separator + "1.pdf");

        appointmentService.saveUserAppointment(userData);
    }

    @PostMapping("/save")
    @Transactional
    public String saveAppointment(Appointment appointment) {
        String userName = generateUserName(appointment);
        appointment.setOrderNo(userName);

        User user = userService.getUserByUserName(userName);
        if (user == null) {
            user = new User();
        }
        setupUserDetails(user, appointment, userName);
        userService.saveUser(user);

        appointmentService.saveAppointment(appointment);

        String emailBody = generateEmailBody(appointment, userName);
        sendMail.sendHtmlmail(appointment.getEmail(), "Your appointment details of GTU", emailBody);

        return "redirect:/appointments/list";
    }

    @GetMapping("/edit/{id}")
    public String editAppointment(@PathVariable Long id, Model model) {
        model.addAttribute("appointment", appointmentService.getAppointmentById(id));
        model.addAttribute("coursesList", courseService.getAllCourses());
        model.addAttribute("collegeList", collegeService.getAllColleges());
        return "newappointment";
    }

    @GetMapping("/delete/{id}")
    public String deleteAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointmentById(id);
        return "redirect:/appointments/list";
    }

    @GetMapping("/{orderId}/{userName}/accept")
    public String acceptAppointment(@PathVariable Long orderId, @PathVariable String userName,Model model) {
        UserData appointment = appointmentService.getAppointmentDetailsById(orderId);
        String customPassword = RandomUtils.nextLong(10000, 99999) + "";
        updateAppointmentStatus(orderId, userName,"Accepted",customPassword);
        model.addAttribute("acceptStatus", "Accepted");
        Roles roles = rolesService.getRoleDetails(appointment.getRole_id());
        Subjects subjects = subjectsService.getSubjectById(appointment.getSubjectId()+"");

        User user = userService.getUserByUserName(userName);
        String emailBody = "<html><body>" +
                "<p>You are appointed as a <strong>" + roles.getRole() + "</strong> for the " +
                subjects.getSubject_name() + ".</p>" +
                "<p><a href='" + appUrl + "'>Click here to login</a></p>" +
                "<p><strong>Username:</strong> " + userName + "</p>" +
                "<p><strong>Password:</strong> " + customPassword + "</p>" +
                "<p>Thanks,</p>" +
                "<p><strong>GTU</strong></p>" +
                "</body></html>";
        sendMail.sendHtmlmail(user.getEmail(), "Your appointment details of GTU", emailBody);
        return "appointment-status";
    }

    @GetMapping("/{orderId}/{userName}/decline")
    public String declineAppointment(@PathVariable Long orderId,String userName, Model model) {
        updateAppointmentStatus(orderId, userName,"Declined","123");
        model.addAttribute("acceptStatus", "Declined");
        return "appointment-status";
    }

    @GetMapping("/resend/{id}")
    public String resendAppointmentMail(@PathVariable Long id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        User user = userService.getUserByUserName(appointment.getOrderNo());

        // Reset password
        String customPassword = RandomUtils.nextLong(10000, 99999) + "";
        user.setPassword(passwordEncoder.encode(customPassword));
        userService.saveUser(user);

        String emailBody = generateEmailBody(appointment, appointment.getOrderNo());
        sendMail.sendHtmlmail(appointment.getEmail(), "Your appointment details of GTU", emailBody);

        return "redirect:/appointments/list";
    }

    // ------------------ 🔹 HELPER METHODS ------------------

    private String generateUserName(Appointment appointment) {
        if (appointment.getId() != null) {
            return appointment.getOrderNo();
        }
        return (appointment.getRole_id() == 2 ? "S" : "M") + RandomUtils.nextLong(10000, 99999);
    }

    private String generateUserNameByRole(int roleId) {

        return (roleId == 2 ? "S" : "M") + RandomUtils.nextLong(10000, 99999);
    }
    private void setupUserDetails(User user, Appointment appointment, String userName) {
        user.setIsActive(0);
        user.setRoleId(appointment.getRole_id());
        user.setUserName(userName);
        user.setFirstName(appointment.getName());
        user.setEmail(appointment.getEmail());
        user.setPassword(passwordEncoder.encode(RandomUtils.nextLong(10000, 99999) + ""));
    }

    private void updateAppointmentStatus(Long orderId,String userName, String status,String password) {
        UserData appointment = appointmentService.getAppointmentDetailsById(orderId);
        appointment.setStatus_date(LocalDateTime.now().toString());
        appointment.setCurrent_status(status);


        appointmentService.saveUserAppointment(appointment);

        if ("Accepted".equals(status)) {
            User user = userService.getUserByUserName(userName);
            user.setIsActive(1);
            user.setPassword(passwordEncoder.encode(password));
            userService.saveUser(user);
        }
    }

    private String generateEmailBody(Appointment appointment, String userName) {
        Roles roles = rolesService.getRoleDetails(appointment.getRole_id());
        Subjects subjects = subjectsService.getSubjectById(appointment.getSubject_id());
        Course course = courseService.getCourseDetailsById(subjects.getCourseId());
        String customPassword = RandomUtils.nextLong(10000, 99999) + "";
        SmsEmailTemplate s11 = smsEmailTemplateService.getEmailTemplateDetails();
        LocalDate currentDate = LocalDate.now();
        CollegeEntity collegeEntity = collegeService.getCollegeById(appointment.getCollege_id());
        // Define the desired date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Format the date to the desired format
        String formattedDate = currentDate.format(formatter);

        Context context = new Context();
        context.setVariable("date", formattedDate);
        context.setVariable("name", appointment.getName());
        context.setVariable("collegeCode", collegeEntity.getCollegeCode());
        context.setVariable("subjectCode", subjects.getSubjectCode());
        context.setVariable("subjectName", subjects.getSubject_name());
        context.setVariable("yearCourse", subjects.getYear() + "/" + course.getCourse_name());
        context.setVariable("submissionDate", appointment.getSubmission_date());

        String s1 = templateEngine.process("email-template", context);

        //  String emailBody =String.format(s1,formattedDate,appointment.getName(),appointment.getCollege_code(),
        //        appointment.getSubject_code(),subjects.getSubject_name(),   course.getCourse_name(),"02-03-2025");

        return s1 + "<p>Please click on the link below to accept your appointment :</p>" +
                "<p><a href='" + appUrl + "appointments/" + userName + "/accept'>Accept Appointment</a></p>" +
                //   "<p><strong>Username:</strong> " + userName + "</p>" +
                // "<p><strong>Password:</strong> " + customPassword + "</p>" +
                "<p>If you are not willing to accept this appointment, please click the link below:</p>" +
                "<p><a href='" + appUrl + "appointments/" + userName + "/notaccept'>Decline Appointment</a></p>";
    }


    // Process Selected Records
    @PostMapping("/processSelected")
    public String processSelected(@RequestParam("selectedIds") List<Long> selectedIds, Model model) {
        if (selectedIds.isEmpty()) {
            model.addAttribute("message", "No records selected for processing.");
            return "redirect:/upload";
        }

       List<AppointmentVo> appointmentVos=appointmentService.getAppointmentDetailsList(selectedIds);
       for(AppointmentVo ap:appointmentVos){
           Roles roles = rolesService.getRoleDetails(ap.getRole_id());
           Subjects subjects = subjectsService.getSubjectById(ap.getSubject_id()+"");
           Course course = courseService.getCourseDetailsById(subjects.getCourseId());
           String customPassword = RandomUtils.nextLong(10000, 99999) + "";
           SmsEmailTemplate s11 = smsEmailTemplateService.getEmailTemplateDetails();
           LocalDate currentDate = LocalDate.now();
           CollegeEntity collegeEntity = collegeService.getCollegeById(ap.getCollege_id()+"");
           // Define the desired date format
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

           // Format the date to the desired format
           String formattedDate = currentDate.format(formatter);

           Context context = new Context();
           context.setVariable("date", formattedDate);
           context.setVariable("name", ap.getFirst_name()+" "+ap.getLast_name());
           context.setVariable("collegeCode", collegeEntity.getCollegeCode());
           context.setVariable("subjectCode", subjects.getSubjectCode());
           context.setVariable("subjectName", subjects.getSubject_name());
           context.setVariable("yearCourse", subjects.getYear() + "/" + course.getCourse_name());
           context.setVariable("submissionDate", ap.getLast_date_to_submit());

           String s1 = templateEngine.process("email-template", context);

           //  String emailBody =String.format(s1,formattedDate,appointment.getName(),appointment.getCollege_code(),
           //        appointment.getSubject_code(),subjects.getSubject_name(),   course.getCourse_name(),"02-03-2025");
String syllabusFile=filePath+ File.separator+"1.pdf";
           String s2= s1 + "<p>Please click on the link below to accept your appointment :</p>" +
                   "<p><a href='" + appUrl + "appointments/" + ap.getId() + "/"+ap.getUser_name() +"/accept'>Accept Appointment</a></p>" +
                   //   "<p><strong>Username:</strong> " + userName + "</p>" +
                   // "<p><strong>Password:</strong> " + customPassword + "</p>" +
                   "<p>If you are not willing to accept this appointment, please click the link below:</p>" +
                   "<p><a href='" + appUrl + "appointments/" + ap.getId() + "/"+ap.getUser_name() +"/notaccept'>Decline Appointment</a></p>";

           sendMail.sendHtmlMail(ap.getEmail(), "Your appointment details of GTU", s2,syllabusFile);

            UserData userData=appointmentService.getAppointmentDetailsById((long) ap.getId());
            userData.setCurrent_status("Appointment Sent");
           userData.setStatus_date(LocalDateTime.now()+"");
           userData.setAppointment_sent_date(LocalDateTime.now()+"");
           appointmentService.saveUserAppointment(userData);

        }
        return "redirect:/upload";
    }

    @GetMapping("/{orderId}/{userName}/addlSubjectAccept")
    public String acceptAdditionalSubjectAppointment(@PathVariable Long orderId, @PathVariable String userName,Model model) {
        UserData appointment = appointmentService.getAppointmentDetailsById(orderId);
        String customPassword = RandomUtils.nextLong(10000, 99999) + "";
        updateAppointmentDetails(orderId, userName,"Accepted");
        model.addAttribute("acceptStatus", "Accepted");
        Roles roles = rolesService.getRoleDetails(appointment.getRole_id());
        Subjects subjects = subjectsService.getSubjectById(appointment.getSubjectId()+"");

        User user = userService.getUserByUserName(userName);
        String emailBody = "<html><body>" +
                "<p>You are appointed as a <strong>" + roles.getRole() + "</strong> for the " +
                subjects.getSubject_name() + ".</p>" +
                "<p><a href='" + appUrl + "'>Click here to login</a></p>" +
                "<p>Thanks,</p>" +
                "<p><strong>GTU</strong></p>" +
                "</body></html>";
        sendMail.sendHtmlmail(user.getEmail(), "Your appointment details of GTU", emailBody);
        return "appointment-status";
    }

    @GetMapping("/{orderId}/{userName}/addlSubjectDecline")
    public String declineAdditionalSubjectAppointment(@PathVariable Long orderId,String userName, Model model) {
        updateAppointmentDetails(orderId, userName,"Declined");
        model.addAttribute("acceptStatus", "Declined");
        return "appointment-status";
    }

    private void updateAppointmentDetails(Long orderId,String userName, String status) {
        UserData appointment = appointmentService.getAppointmentDetailsById(orderId);
        appointment.setStatus_date(LocalDateTime.now().toString());
        appointment.setCurrent_status(status);
        appointmentService.saveUserAppointment(appointment);

    }
}

