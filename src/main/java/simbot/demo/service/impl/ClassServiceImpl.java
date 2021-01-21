package simbot.demo.service.impl;

import simbot.demo.entity.Class;
import simbot.demo.dao.ClassMapper;
import simbot.demo.service.ClassService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author baicx
 * @since 2021-01-19
 */
@Service
public class ClassServiceImpl extends ServiceImpl<ClassMapper, Class> implements ClassService {

}
