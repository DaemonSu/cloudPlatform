package cn.baimu.controller.reception;

import cn.baimu.mapper.OutlierMapper;
import cn.baimu.po.EdgeTerminal;
import cn.baimu.po.Outlier;
import cn.baimu.po.User;
import cn.baimu.service.EdgeTerminalService;
import cn.baimu.service.UserService;
import cn.baimu.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * 前台登录管理
 * @author wxy
 */
@Controller
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private EdgeTerminalService edgeTerminalService;

    //登录
    @RequestMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model, HttpSession httpSession, HttpServletRequest request){
        User hasUser = (User) httpSession.getAttribute("receptionUser");
        if (hasUser != null && hasUser.getUsername().equals("username")) {
            model.addAttribute("loginError","同一浏览器只能登录一个账号！");
            return "reception/login/login";
        }
        User user = userService.login(username,password);
        if (user != null) {
            httpSession.setAttribute("receptionUser",user); //保存用户信息
            List<EdgeTerminal> edgeTerminals = null;
            try {
                edgeTerminals = edgeTerminalService.getEdgeTerminals(user.getUid()); //获取用户管辖范围类已接管的边缘端信息
            } catch (Exception e) {
                e.printStackTrace();
            }
            httpSession.setAttribute("jurisdictions", edgeTerminals);

            String ua = request.getHeader("User-Agent");
            if (StringUtil.checkAgentIsMobile(ua))
                return "mobile/main";
            else
                return "reception/main";
        }else {
            model.addAttribute("loginError","用户名或密码错误！");
            return "reception/login/login";
        }
    }

    //退出
    @RequestMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.invalidate();
        return "reception/login/login";
    }

}
