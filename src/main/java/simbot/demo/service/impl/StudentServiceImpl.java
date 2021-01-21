package simbot.demo.service.impl;

import simbot.demo.dao.StudentMapper;
import simbot.demo.entity.Student;
import simbot.demo.service.IStudentService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author baicx
 * @since 2021-01-14
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {

}
