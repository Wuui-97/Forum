package com.wuui.community.controller;

import com.wuui.community.service.DateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @author Dee
 * @create 2022-06-01-19:20
 * @describe
 */
@Controller
public class DataController {

    @Autowired
    DateService dateService;

    @RequestMapping(path = "/data", method = {RequestMethod.GET, RequestMethod.POST})
    public String getDataPage(){
        return "site/admin/data";
    }

    @PostMapping("/data/uv")
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                        Model model){

        long uv = dateService.calculateUV(start, end);
        model.addAttribute("uvTotalCount", uv);
        model.addAttribute("uvStart", start);
        model.addAttribute("uvEnd", end);

        return "forward:/data";

    }

    @PostMapping("/data/dau")
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                        Model model){

        long dau = dateService.calculateDAU(start, end);
        model.addAttribute("dauTotalCount", dau);
        model.addAttribute("dauStart", start);
        model.addAttribute("dauEnd", end);

        return "forward:/data";

    }

}
