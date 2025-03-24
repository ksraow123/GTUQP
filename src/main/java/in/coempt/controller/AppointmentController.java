package in.coempt.controller;

import in.coempt.entity.*;
import in.coempt.repository.UserRepository;
import in.coempt.service.*;
import in.coempt.util.SecurityUtil;
import in.coempt.util.SendMailUtil;
import in.coempt.vo.AppointmentUpdateRequest;
import in.coempt.vo.AppointmentVo;
import in.coempt.vo.IndividualAppointmentVo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/appointments")
@CrossOrigin(origins = "*")

@RequiredArgsConstructor
public class AppointmentController {

    @Value("${filePath}")
    private String filePath;

    private final TemplateEngine templateEngine;
    private final AppointmentService appointmentService;
    private final CourseService courseService;
    private final UserService userService;
    private final SendMailUtil sendMail;
    private final SubjectsService subjectsService;
    private final RolesService rolesService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CollegeService collegeService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    private final SectionUserMappingService sectionUserMappingService;

    private  final FacultyDataService facultyDataService;


    @Value("${app.url}")
    private String appUrl;



    @GetMapping("/individualAppointments")
    public String individualAppointments(Model model) {
        UserDetails user=(UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User userEntity = userRepository.findByUserName(user.getUsername());
        SectionUserMappingEntity mappingEntity=
                sectionUserMappingService.getMappingDetailsByUserid(Math.toIntExact(userEntity.getId()));

        List<Course> courseList = courseService.getAllCourses().stream()
                .sorted(Comparator.comparing(Course::getCourse_name)) // Sort by name
                .distinct() // Remove duplicates (won't work well for objects)
                .collect(Collectors.toList());

        if(userEntity.getRoleId()==1) {
            courseList= courseList.stream().filter(a ->
                    Math.toIntExact(a.getSection_id()) == mappingEntity.getSectionId()).collect(Collectors.toList()).stream()
                    .sorted(Comparator.comparing(Course::getCourse_name)) // Sort by name
                    .distinct() // Remove duplicates (won't work well for objects)
                    .collect(Collectors.toList());
        }
        model.addAttribute("coursesList", courseList);
        model.addAttribute("collegeList", collegeService.getAllColleges());
        model.addAttribute("individualAppoint", new IndividualAppointmentVo());
        model.addAttribute("page","individualAppointment");
        return "main";
    }
    @PostMapping("/individual/save")
    @Transactional

    public String saveAppointment(Model model, RedirectAttributes redirectAttributes, IndividualAppointmentVo appointmentVo) {
        try {
            Optional<FacultyData> facultyDataOptional = facultyDataService.getFacultyByMobileNumber(appointmentVo.getMobile_number());

            FacultyData facultyData;

            if (facultyDataOptional.isPresent()) {
                facultyData = facultyDataOptional.get();
            } else {
                facultyData = new FacultyData(); // Create a new object if not found
                facultyData.setEmail(appointmentVo.getEmail());
                facultyData.setMobileNumber(appointmentVo.getMobile_number());
                facultyData.setFirstName(appointmentVo.getFname());
                facultyData.setLastName(appointmentVo.getLname());
                facultyData.setCollegeCode(appointmentVo.getCollegeCode());
                facultyDataService.saveFaculty(facultyData); // Save only if new
            }

            String customPassword = RandomUtils.nextLong(10000, 99999) + "";

            User user = userService.getUserByMobileNoAndEmailAndRoleId(
                    appointmentVo.getMobile_number(), appointmentVo.getEmail(), appointmentVo.getRole_id());

            List<User> ulist=userService.getUserByMobileNoAndEmail(appointmentVo.getMobile_number(), appointmentVo.getEmail());

if(ulist.size()>0) {
    List<UserData> userData = appointmentService.checkSubjectAvailableByUserList(ulist, appointmentVo.getSubject_id());
    if (userData.size() > 0) {
        redirectAttributes.addFlashAttribute("message", "Same user cannot be as a QP Setter and QP Moderator for same subject.");
        return "redirect:/appointments/individualAppointments";
    }
}

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

            sendAppointmentEmail(appointmentVo, user, savedUserData, isNewUser,customPassword);

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

            user.setFirstName(appointmentVo.getFname());
            user.setLastName(appointmentVo.getLname());
            user.setIsActive(1);
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
            userData.setExamSeriesId(1);

            return userData;
        }

        private void sendAppointmentEmail(IndividualAppointmentVo appointmentVo, User user, UserData userData, boolean isNewUser,String customPassword) {
            Subjects subject = subjectsService.getSubjectById(String.valueOf(appointmentVo.getSubject_id()));
            Course course = courseService.getCourseDetailsById(subject.getCourseId());
            CollegeEntity collegeEntity = collegeService.getCollegeByCode(appointmentVo.getCollegeCode());

            Context context = new Context();
            context.setVariable("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            context.setVariable("name", appointmentVo.getFname().toUpperCase() + " " + appointmentVo.getLname().toUpperCase());
            context.setVariable("collegeCode", collegeEntity.getCollegeCode()+"-"+collegeEntity.getCollege_name());
            context.setVariable("subjectCode", subject.getSubjectCode());
            context.setVariable("subjectName", subject.getSubject_name());
            context.setVariable("userName", user.getUserName());
            context.setVariable("yearCourse", course.getCourse_name());

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            // Parse and format the date
            LocalDate date = LocalDate.parse(appointmentVo.getSubmission_date(), inputFormatter);
            String formattedDate = date.format(outputFormatter);
            context.setVariable("submissionDate", formattedDate);
            context.setVariable("noOfSets", appointmentVo.getNo_of_sets());

            String emailContent = templateEngine.process("email-template", context);
            String password = isNewUser ? customPassword : "";
            String declineActionType = isNewUser ? "notaccept" : "addlSubjectDecline";

            String emailBody = emailContent + "<p>You are appointed as a <strong> QP Setter </strong> for the " +
                    subject.getSubjectCode() + "-" + subject.getSubject_name() + ".</p>" +
                    "<p><a href='" + appUrl + "'>Click here to login</a></p>" +
                    "<p><strong>Username:</strong> " + user.getUserName() + "</p>";
            if (isNewUser){
                emailBody=emailBody+ "<p><strong>Password:</strong> " + customPassword + "</p>" ;
        }
          else{
                emailBody=emailBody+  "<p>User ID and Password remain the same. In case you have forgotten the password, " +
                        "use the <strong>Forgot Password</strong> option provided on the login page.</p>";
            }

            sendMail.sendHtmlmail(user.getEmail(), "Your appointment is confirmed for the Subject "+subject.getSubjectCode()+"-"+subject.getSubject_name()+"- GTU SQQPRS", emailBody);
            appointmentService.saveUserAppointment(userData);
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
                    subjects.getSubjectCode()+"-" +subjects.getSubject_name() + ".</p>" +
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



        // ------------------ 🔹 HELPER METHODS ------------------



        private String generateUserNameByRole(int roleId) {

            return (roleId == 2 ? "S" : "M") + RandomUtils.nextLong(10000, 99999);
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

                LocalDate currentDate = LocalDate.now();
                CollegeEntity collegeEntity = collegeService.getCollegeById(ap.getCollege_id()+"");
                // Define the desired date format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                // Format the date to the desired format
                String formattedDate = currentDate.format(formatter);

                Context context = new Context();
                context.setVariable("date", formattedDate);
                context.setVariable("name", ap.getFirst_name().toUpperCase()+" "+ap.getLast_name().toUpperCase());
                context.setVariable("collegeCode", collegeEntity.getCollegeCode()+"-"+collegeEntity.getCollege_name());
                context.setVariable("subjectCode", subjects.getSubjectCode());
                context.setVariable("subjectName", subjects.getSubject_name());
                context.setVariable("yearCourse",  course.getCourse_name());
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                // Parse and format the date
                LocalDate date = LocalDate.parse(ap.getLast_date_to_submit(), inputFormatter);
                String submissionDate = date.format(outputFormatter);
                context.setVariable("submissionDate", submissionDate);
                context.setVariable("noOfSets", ap.getNo_of_sets());
                context.setVariable("userName", ap.getUser_name());
                String s1 = templateEngine.process("email-template", context);

                //  String emailBody =String.format(s1,formattedDate,appointment.getName(),appointment.getCollege_code(),
                //        appointment.getSubject_code(),subjects.getSubject_name(),   course.getCourse_name(),"02-03-2025");
                String syllabusFile=filePath+ File.separator+"1.pdf";
                String s2= s1 +  "<p><a href='" + appUrl +"'>Click here to login</a></p>" ;
//                        "<p>User ID and Password remain the same. In case you have forgotten the password, " +
//                        "use the <strong>Forgot Password</strong> option provided on the login page.</p>" +
//                        "<p>Thanks,</p>" ;
                ;

                sendMail.sendHtmlmail(ap.getEmail(), "Your appointment details of GTU", s2);

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

           String emailBody= "<html>" +
                    "<body>" +
                    "<p>Dear Sir/Madam,</p>" +
                    "<p>You have been appointed as a QP Setter for the Subject " +
                    "<strong>"+subjects.getSubjectCode()+ "-" +subjects.getSubject_name()+"</strong>.</p>" +
                    "<p>Your User ID is <strong>"+userName+"</strong>.</p>" +
                    "<p><a href='" + appUrl +"'>Click here to login</a></p>" ;

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
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<String> updateAppointment(@RequestBody AppointmentUpdateRequest request) {
        try {
            UserData appointment = appointmentService.getAppointmentDetailsById(request.getId());
            User user = userService.getUserById(appointment.getUserId()+"");
            appointment.setNo_of_sets(request.getNoOfSets());
            appointment.setLast_date_to_submit(request.getLastDateToSubmit()+"");
            appointmentService.saveUserAppointment(appointment);
            Subjects subject = subjectsService.getSubjectById(String.valueOf(appointment.getSubjectId()));
            String emailBody = "<html>"
                    + "<body>"
                    + "<p>Dear "+user.getFirstName()+""+user.getLastName()+",</p>"
                    + "<p>Admin updated your QP tasks for the subject: <strong>"+subject.getSubjectCode()+"-"+subject.getSubject_name()+"</strong>.</p>"
                    + "<p><strong>Last date of submission:</strong> "+request.getLastDateToSubmit()+"</p>"
                    + "<p>Login and check the details.</p>"
                    + "<p>Thanks,<br>Admin<br>SOQPRS - GTU</p>"
                    + "</body>"
                    + "</html>";

            sendMail.sendHtmlmail(user.getEmail(),"Updates from GTU SOQPRS Admin",emailBody);
            return ResponseEntity.ok("success");  // ✅ Return HTTP 200
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("fail");  // ✅ Return HTTP 500
        }
    }
    }
