package simbot.demo.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import simbot.demo.entity.Student;
import simbot.demo.service.IStudentService;

import javax.xml.ws.soap.Addressing;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author baicx
 * @since 2021-01-14
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private IStudentService studentService;

    @GetMapping("/list")
    public Object list(){
        List<Student> students = studentService.selectList(new EntityWrapper<>());
        return students;
    }
}

