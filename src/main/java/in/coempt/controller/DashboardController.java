package in.coempt.controller;

import in.coempt.entity.BitwiseQuestions;
import in.coempt.entity.QpTemplateMaster;
import in.coempt.entity.User;
import in.coempt.repository.QpTemplateMasterRepository;
import in.coempt.repository.UserRepository;
import in.coempt.service.BitwiseQuestionsService;
import in.coempt.service.DashBoardService;
import in.coempt.util.SecurityUtil;
import in.coempt.vo.QPSetterDashBoardVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {
    @Autowired
    private DashBoardService dashBoardService;
@Autowired
private BitwiseQuestionsService bitwiseQuestionsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QpTemplateMasterRepository qpTemplateMasterRepository;

    @GetMapping("/setterdashboard")
   // @Transactional
    public String userDashBoard(Model model) {
        try {
            UserDetails user = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
            User userEntity = userRepository.findByUserName(user.getUsername());

            List<QPSetterDashBoardVo> qpSetterDashBoardList = dashBoardService.getQPSetterDashBord(userEntity.getUserName(), userEntity.getId());

            for (QPSetterDashBoardVo qpSetterDashBoardVo : qpSetterDashBoardList) {
                try {
                    int noOfSets = Integer.parseInt(qpSetterDashBoardVo.getNo_of_sets());
                    int subjectId = Integer.parseInt(qpSetterDashBoardVo.getSubject_id());

                    for (int setNo = 1; setNo <= noOfSets; setNo++) {
                        List<BitwiseQuestions> existingQuestions = bitwiseQuestionsService.getQpTemplateQuestions(userEntity.getId(), String.valueOf(subjectId), setNo);
                        List<BitwiseQuestions> questionsList = new ArrayList<>();

                        if (existingQuestions.isEmpty()) {
                            List<QpTemplateMaster> qpTemplateMasterList = qpTemplateMasterRepository.findBySubjectId(subjectId);

                            for (QpTemplateMaster template : qpTemplateMasterList) {
                                BitwiseQuestions bitwiseQuestions = new BitwiseQuestions();
                                bitwiseQuestions.setQpSetterId(userEntity.getId());
                                bitwiseQuestions.setSubjectId(subjectId);
                                bitwiseQuestions.setQ_no(template.getQNo());
                                bitwiseQuestions.setLevel(template.getLevel());
                                bitwiseQuestions.setTopic(template.getTopic());
                                bitwiseQuestions.setBit_no(template.getBitNo());
                                bitwiseQuestions.setMarks(template.getMarks());
                                bitwiseQuestions.setSetNo(setNo);

                                questionsList.add(bitwiseQuestions);
                            }
                            if (!questionsList.isEmpty()) {
                                bitwiseQuestionsService.saveBitWiseQuestions(questionsList);
                            }
                        }
                    }
                } catch (NumberFormatException e) {

                } catch (Exception e) {

                }
            }

            List<QPSetterDashBoardVo> setwiseDashBoard = dashBoardService.getSetWiseQPDashBoard(userEntity.getId());
            model.addAttribute("qpSetterDashBoardList", qpSetterDashBoardList);
            model.addAttribute("setwiseDashBoard", setwiseDashBoard);
            model.addAttribute("page", "setterDashBoard");

            return "main";
        }  catch (Exception e) {

            model.addAttribute("error", "An unexpected error occurred. Please try again later.");
        }
        return "errorPage";
    }


    @GetMapping("/moderatordashboard")
    public String moderatorDashBoard(Model model) {
        UserDetails user=(UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User userEntity = userRepository.findByUserName(user.getUsername());
        List<QPSetterDashBoardVo>  qpModeratorDashBoardList= dashBoardService.getQPModeratorDashBord(userEntity.getUserName(),userEntity.getId());
        List<QPSetterDashBoardVo>  setWiseReviewerQPDashBoard= dashBoardService.getSetWiseReviewerQPDashBoard(userEntity.getUserName(),userEntity.getId());

        model.addAttribute("qpModeratorDashBoardList",qpModeratorDashBoardList);
        model.addAttribute("setWiseReviewerQPDashBoard",setWiseReviewerQPDashBoard);
        model.addAttribute("page","moderatorDashBoard");
        return "main";

    }

}
