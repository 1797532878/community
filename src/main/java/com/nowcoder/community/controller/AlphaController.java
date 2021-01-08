package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import com.sun.deploy.net.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Hello";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){

        //获取请求数据
        System.out.println(request.getMethod());//请求行
        System.out.println(request.getServletPath());
        Enumeration<String> headerNames = request.getHeaderNames();//迭代器对象
        //消息头
        while (headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ":" + value);
        }
        //System.out.printf(request.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try (            PrintWriter writer = response.getWriter();
        ){
            writer.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//Get请求
    @GetMapping("/students")
    @ResponseBody
    public String getStudents(@RequestParam(name = "current",required = false,defaultValue = "1") int current,
                              @RequestParam(name = "limit",required = false,defaultValue = "10") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }


    @GetMapping("/student/{id}")
    @ResponseBody
    public String getStudent(int id){
        System.out.println(id);
        return "a student";
    }

    //POST请求
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //响应html数据
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","张三");
        mav.addObject("age",30);
        mav.setViewName("/demo/view");
        return mav;
    }
    //第二种响应
    @GetMapping("/school")
    public String getSchool(Model model){
        model.addAttribute("name","北京大学");
        model.addAttribute("age",88);
        return "/demo/view";
    }

    //响应json数据(异步请求)
    // Java对象 --》 JS对象   需要JSON字符串

    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String, Object> map = new HashMap<>();
        map.put("name","张三");
        map.put("age",23);
        map.put("salary",80000);
        return map;
    }

    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        map.put("name","张三");
        map.put("age",23);
        map.put("salary",80000);
        list.add(map);
        map.put("name","李四");
        map.put("age",23);
        map.put("salary",50000);
        list.add(map);
        return list;
    }

    //cookie示例
    @GetMapping("/cookie/set")
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        //创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        //设置生效范围
        cookie.setPath("/community/alpha");
        //设置生存时间
        cookie.setMaxAge(60*10);
        //发送cookie
        response.addCookie(cookie);
        return "set Cookie";
    }
    @GetMapping("/cookie/get")
    @ResponseBody
    public String getCookie(@CookieValue("code")String code){
        System.out.println(code);
        return "get cookie";
    }

    //session示例
    @GetMapping("/session/set")
    @ResponseBody
    public String setSession(HttpSession session){
        //自动创建session  各种类型数据
        session.setAttribute("id",1);
        session.setAttribute("name","Test");
        return "set session";
    }

    @GetMapping("/session/get")
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }
}
