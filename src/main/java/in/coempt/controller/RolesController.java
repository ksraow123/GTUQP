package in.coempt.controller;

import in.coempt.dao.MenuPageDao;
import in.coempt.entity.*;
import in.coempt.repository.RolesRepository;
import in.coempt.repository.UserRepository;
import in.coempt.service.ExamSeriesService;
import in.coempt.service.SectionUserMappingService;
import in.coempt.util.SecurityUtil;
import in.coempt.vo.MenuHeaderBean;
import in.coempt.vo.MenuPage;
import in.coempt.vo.SessionDataVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@CrossOrigin(origins = "*")
public class RolesController {
@Autowired
private MenuPageDao menuPageDao;
@Autowired
private UserRepository userRepository;
    @Autowired
    private RolesRepository rolesRepository;
@Autowired
private  ExamSeriesService examSeriesService;
    @Autowired
    private  SectionUserMappingService sectionUserMappingService;

    @GetMapping("/roles")
    public String getMenuListByRole(Model model, HttpSession session) {
      UserDetails user=(UserDetails)SecurityUtil.getLoggedUserDetails().getPrincipal();
        List<MenuPage> menuPageList=menuPageDao.getUserMenu(user.getUsername());
        SessionDataVo dataVo=new SessionDataVo();
        List<ExamSeries> examSeriesList= examSeriesService.getAllActiveExamSeries();


        List <MenuHeaderBean> menuHeaderList=menuPageDao.getMenuHeaders(user.getUsername());
        User userEntity = userRepository.findByUserName(user.getUsername());
        Roles roles= rolesRepository.findById(Long.valueOf(userEntity.getRoleId())).get();
        String redirectUrl=roles.getHome_page_url();

        // List<MenuPage> menuPageList = menuPageDao.getUserMenu(user.getUsername());
        //List<MenuHeaderBean> menuHeaderList = menuPageDao.getMenuHeaders(user.getUsername());

        // Ensure lists are not null
        if (menuHeaderList == null) {
            menuHeaderList = new ArrayList<>();
        }
        if (menuPageList == null) {
            menuPageList = new ArrayList<>();
        }
        dataVo.setRoleId(roles.getId());
        dataVo.setUserId(userEntity.getId());
        dataVo.setUserName(userEntity.getUserName());
        dataVo.setExamSeriesId(examSeriesList.get(0).getId());
        List<Integer> l1=new ArrayList<>();
        if(roles.getId()==1){
            SectionUserMappingEntity mappingEntity =
                    sectionUserMappingService.getMappingDetailsByUserid(Math.toIntExact(userEntity.getId()));
            l1.add(mappingEntity.getSectionId());
        }
        if(roles.getId()>2){
            l1.add(1);
            l1.add(2);
            l1.add(3);
            l1.add(4);
            l1.add(5);
            l1.add(6);
            l1.add(7);
            l1.add(8);

        }
        dataVo.setSectionId(l1);
        // Store in session
        session.setAttribute("menuHeaderList", menuHeaderList);
        session.setAttribute("menuPageList", menuPageList);
        session.setAttribute("homepageUrl", roles.getHome_page_url());
        session.setAttribute("sessionData", dataVo);

        return "redirect:" + roles.getHome_page_url();
    }
}
