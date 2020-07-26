package com.atguigu.crud.controller;


import com.atguigu.crud.bean.Employee;
import com.atguigu.crud.bean.Msg;
import com.atguigu.crud.service.EmployeeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class EmployeeController {


   @Autowired
   private EmployeeService employeeService;


   @ResponseBody
   @RequestMapping(value = "/emp/{ids}",method = RequestMethod.DELETE)
   public Msg deleteEmpById(@PathVariable("ids")String ids){
       if(ids.contains("-")){
           List<Integer> del_ids = new ArrayList<>();
           String[] str_ids = ids.split("-");

           for (String string : str_ids) {
               del_ids.add(Integer.parseInt(string));
           }
           employeeService.deleteBatch(del_ids);
       }else {

           Integer id = Integer.parseInt(ids);
           employeeService.deleteEmp(id);
       }
       return Msg.success();
   }

   @ResponseBody
   @RequestMapping(value = "/emp/{empId}")
   public Msg saveEmp(Employee employee){
       employeeService.updateEmp(employee);
       return Msg.success();
   }

   @ResponseBody
   @RequestMapping(value = "/emp/{id}",method = RequestMethod.GET)
   public Msg getEmp(@PathVariable("id")Integer id){

      Employee employee= employeeService.getEmp(id);
       return Msg.success().add("emp",employee);
   }

   @ResponseBody
   @RequestMapping("/checkuser")
   public Msg checkUser(@RequestParam("empName")String empName){
       boolean t=employeeService.checkUser(empName);

       String regx="(^[a-zA-Z0-9_-]{6,16}$)|(^[\u2E80-\u9FFF]{2,5})";
       if(!empName.matches(regx)){
           return Msg.fail().add("va-msg","用户名必须6-16");
       }
       if(t){
           return Msg.success();
       }else {
           return Msg.fail().add("va-msg","用户名不可用");
       }
   }

   @RequestMapping(value = "/emp",method = RequestMethod.POST)
   @ResponseBody
   public Msg saveEmp(@Valid Employee employee, BindingResult result){

       if(result.hasErrors()){
           HashMap<String, Object> map = new HashMap<>();
           List<FieldError> fieldErrors = result.getFieldErrors();
           for(FieldError fieldErrors1:fieldErrors){
               System.out.println("错误字段"+fieldErrors1.getField());
               System.out.println("错误信息"+fieldErrors1.getDefaultMessage());
               map.put(fieldErrors1.getField(),fieldErrors1.getDefaultMessage());
           }
           return Msg.fail().add("errorsFields",map);
       }else {

           employeeService.saveEmp(employee);
           return Msg.success();
       }
   }


   @ResponseBody
   @RequestMapping("/emps")
   public Msg getEmpWithJson(@RequestParam(value = "pn",defaultValue = "1")Integer pn){
       PageHelper.startPage(pn,5);
       List<Employee> emps= employeeService.getAll();
       PageInfo page = new PageInfo(emps);

       return Msg.success().add("pageInfo", page);
   }


    //@RequestMapping("/emps")
    public String getEmps (@RequestParam(value = "pn",defaultValue = "1")Integer pn, Model model){

        PageHelper.startPage(pn,5);
        List<Employee> emps= employeeService.getAll();
        PageInfo page = new PageInfo(emps);
        model.addAttribute("pageInfo",page);
        return "list";
    }
}
