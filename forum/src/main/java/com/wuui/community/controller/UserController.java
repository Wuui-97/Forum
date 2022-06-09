package com.wuui.community.controller;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.wuui.community.annotation.LoginRequired;
import com.wuui.community.entity.User;
import com.wuui.community.service.FollowService;
import com.wuui.community.service.LikeService;
import com.wuui.community.service.UserService;
import com.wuui.community.util.CommunityConstant;
import com.wuui.community.util.CommunityUtil;
import com.wuui.community.util.HostHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Dee
 * @create 2022-05-14-14:53
 * @describe 用户的相关配置
 */
@Slf4j
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String onloadPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String bucketHeaderName;

    @Value("${qiniu.bucket.header.url}")
    private String bucketHeaderUrl;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @Autowired
    FollowService followService;

    @Autowired
    HostHandler hostHandler;

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage(Model model){
        //上传文件名称
        String fileName = CommunityUtil.generateUUID();
        //设置响应信息
        StringMap policy = new StringMap();
        policy.put("returnBody", CommunityUtil.getJSONString(200));
        //生成上传凭证
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(bucketHeaderName, fileName, 3600, policy);

        model.addAttribute("uploadToken", uploadToken);
        model.addAttribute("fileName", fileName);

        return "site/setting";
    }

    //更新头像路径
    @PostMapping("/header/url")
    @ResponseBody
    public String updateHeaderUrl(String fileName){
        if(StringUtils.isBlank(fileName)){
            return CommunityUtil.getJSONString(404, "文件名不能为空");
        }

        String headerUrl = bucketHeaderUrl + "/" + fileName;
        userService.updateHeader(hostHandler.getUser().getId(), headerUrl);

        return CommunityUtil.getJSONString(200);
    }


    /**
     * 上传头像
     */
    //使用云服务器方式，不在保存到本地
    @LoginRequired
//    @PostMapping("/upload")
    public String upload(MultipartFile headerImg, Model model){
        if(headerImg == null){
            model.addAttribute("Imgerror","上传图像不能为空");
            return "site/setting";
        }

        String fileName = headerImg.getOriginalFilename();
        if(StringUtils.isBlank(fileName)){
            model.addAttribute("Imgerror","文件名错误");
            return "site/setting";
        }
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("Imgerror","上传格式不正确");
            return "site/setting";
        }

        fileName = CommunityUtil.generateUUID() + suffix;
        File file = new File(onloadPath + "/" + fileName);
        try {
            //MultipartFile提供直接保存文件的方法
            headerImg.transferTo(file);
        } catch (IOException e) {
            log.error("保存图像失败" + e.getMessage());
        }
        //注意要更新用户的headerUrl
        //headerUrl: http://localhost:8080/forum/user/header/xxx.png
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        User user = hostHandler.getUser();
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";
    }

    /**
     * 更新头像
     */
    @GetMapping("/header/{fileName}")
    public void changeHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            throw new IllegalArgumentException("图像格式不正确");
        }
        response.setContentType("image/" + suffix);
        File file = new File(onloadPath + "/" + fileName);
        try(FileInputStream fileInputStream = new FileInputStream(file);
            ServletOutputStream outputStream = response.getOutputStream();)
        {
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = fileInputStream.read(buffer)) != -1){
                outputStream.write(buffer, 0, len);
            }
        } catch (Exception e) {
            log.info("文件解析错误" + e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @LoginRequired
    @PostMapping("/changePassword")
    public String changePassword(String oldPassword, String newPassword, String confimPassword, Model model){
        User user = hostHandler.getUser();
        if(user == null){
            model.addAttribute("oldPMsg","用户还未登录");
            return "redirect:/login";
        }

        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if(!oldPassword.equals(user.getPassword())){
            model.addAttribute("oldPMsg","密码不正确");
            return "site/setting";
        }
        if(newPassword.length() < 8){
            model.addAttribute("newPMsg","密码长度不能小于8位");
            return "site/setting";
        }
        if(!newPassword.equals(confimPassword)){
            model.addAttribute("conPMsg","两次输入密码不一致");
            return "site/setting";
        }
        userService.updatePassword(user.getId(),newPassword);
        model.addAttribute("msg","密码修改成功");
        model.addAttribute("target","/index");
        return "site/operate-result";
    }

    /**
     * 个人主页
     * @param userId 以userId来区分
     * @return
     */
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") Integer userId, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new IllegalArgumentException("用户参数不能为空");
        }

        //用户信息
        model.addAttribute("user", user);
        //点赞数量
        model.addAttribute("likeCount", likeService.findUserLikeCount(userId));
        //传递过来的userId对应的user关注了多少人
        model.addAttribute("followeeCount", followService.findFolloweeCount(userId, ENTITY_TYPE_USER));
        //传递过来的userId对应的user粉丝的数量
        model.addAttribute("followerCount", followService.findFollowerCount(ENTITY_TYPE_USER, userId));
        boolean followStatus = false;
        if(hostHandler.getUser() != null){
            //关注的状态
            model.addAttribute("followStatus", followService.findFollowStatus(hostHandler.getUser().getId(), ENTITY_TYPE_USER, userId));
        }

        return "site/profile";
    }

}
