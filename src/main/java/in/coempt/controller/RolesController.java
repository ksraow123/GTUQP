package in.coempt.controller;

import in.coempt.dao.MenuPageDao;
import in.coempt.entity.Roles;
import in.coempt.entity.User;
import in.coempt.repository.RolesRepository;
import in.coempt.repository.UserRepository;
import in.coempt.util.SecurityUtil;
import in.coempt.vo.MenuHeaderBean;
import in.coempt.vo.MenuPage;
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


    @GetMapping("/roles")
    public String getMenuListByRole(Model model, HttpSession session) {
      UserDetails user=(UserDetails)SecurityUtil.getLoggedUserDetails().getPrincipal();
        List<MenuPage> menuPageList=menuPageDao.getUserMenu(user.getUsername());

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

        // Store in session
        session.setAttribute("menuHeaderList", menuHeaderList);
        session.setAttribute("menuPageList", menuPageList);
        session.setAttribute("homepageUrl", roles.getHome_page_url());

        return "redirect:" + roles.getHome_page_url();
    }
}
